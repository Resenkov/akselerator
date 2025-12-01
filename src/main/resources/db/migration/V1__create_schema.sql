
-- Удаляем в порядке зависимостей
DROP TABLE IF EXISTS catch_reports, allocation_quotas, user_roles, users, roles, organizations, fish_species, fishing_regions;

-- =============================================
-- 1. Справочник: Рыбопромысловые регионы
-- =============================================
CREATE TABLE fishing_regions (
                                 id          SERIAL PRIMARY KEY,
                                 code        VARCHAR(20) NOT NULL UNIQUE,
                                 name        VARCHAR(100) NOT NULL,
                                 created_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

COMMENT ON TABLE fishing_regions IS 'Справочник рыбопромысловых районов';
COMMENT ON COLUMN fishing_regions.code IS 'Уникальный код региона (например, AZOV, BLACK)';

CREATE UNIQUE INDEX idx_fishing_regions_code ON fishing_regions (LOWER(code));

-- =============================================
-- 2. Справочник: Виды рыбы
-- =============================================
CREATE TABLE fish_species (
                              id               SERIAL PRIMARY KEY,
                              scientific_name  VARCHAR(100) NOT NULL UNIQUE,
                              common_name      VARCHAR(100) NOT NULL,
                              is_endangered    BOOLEAN NOT NULL DEFAULT FALSE,
                              created_at       TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

COMMENT ON TABLE fish_species IS 'Справочник промысловых видов рыбы';
COMMENT ON COLUMN fish_species.is_endangered IS 'Флаг для видов из Красной книги';

CREATE UNIQUE INDEX idx_fish_species_scientific ON fish_species (LOWER(scientific_name));

-- =============================================
-- 3. Организации (компании и госорганы)
-- =============================================
CREATE TABLE organizations (
                               id          SERIAL PRIMARY KEY,
                               name        VARCHAR(200) NOT NULL,
                               org_type    VARCHAR(50) NOT NULL,
                               inn         VARCHAR(12) UNIQUE,
                               region_id   INTEGER REFERENCES fishing_regions(id) ON DELETE RESTRICT,
                               created_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

COMMENT ON TABLE organizations IS 'Организации: рыбопромысловые компании и госорганы';
COMMENT ON COLUMN organizations.org_type IS 'Тип организации (например, ООО, ИП, госорган и т.п.)';
COMMENT ON COLUMN organizations.inn IS 'ИНН (заполняется только для COMPANY)';

-- =============================================
-- 4. Роли пользователей
-- =============================================
CREATE TABLE roles (
                       id          SERIAL PRIMARY KEY,
                       name        VARCHAR(50) NOT NULL UNIQUE,
                       description TEXT
);

COMMENT ON TABLE roles IS 'Роли в системе (RBAC)';

-- =============================================
-- 5. Пользователи
-- =============================================
CREATE TABLE users (
                       id              SERIAL PRIMARY KEY,
                       organization_id INTEGER REFERENCES organizations(id) ON DELETE SET NULL,
                       username        VARCHAR(50) NOT NULL UNIQUE,
                       email           VARCHAR(255) NOT NULL,
                       password_hash   TEXT NOT NULL,
                       is_active       BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

COMMENT ON TABLE users IS 'Пользователи системы';
COMMENT ON COLUMN users.organization_id IS 'Организация (может быть NULL для суперадмина)';

CREATE INDEX idx_users_org ON users (organization_id);

-- =============================================
-- 6. Связь пользователей и ролей (многие-ко-многим)
-- =============================================
CREATE TABLE user_roles (
                            user_id    INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                            role_id    INTEGER NOT NULL REFERENCES roles(id) ON DELETE RESTRICT,
                            granted_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                            granted_by INTEGER REFERENCES users(id) ON DELETE SET NULL,
                            PRIMARY KEY (user_id, role_id)
);

COMMENT ON TABLE user_roles IS 'Назначение ролей пользователям';

-- =============================================
-- 7. Квоты
-- =============================================
CREATE TABLE allocation_quotas (
                                   id            SERIAL PRIMARY KEY,
                                   organization_id INTEGER NOT NULL REFERENCES organizations(id) ON DELETE RESTRICT,
                                   species_id      INTEGER NOT NULL REFERENCES fish_species(id) ON DELETE RESTRICT,
                                   region_id       INTEGER NOT NULL REFERENCES fishing_regions(id) ON DELETE RESTRICT,
                                   period_start    DATE NOT NULL,
                                   period_end      DATE NOT NULL CHECK (period_end >= period_start),
                                   limit_kg        NUMERIC(12, 3) NOT NULL CHECK (limit_kg > 0),
                                   created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                                   UNIQUE (organization_id, species_id, region_id, period_start, period_end)
);

COMMENT ON TABLE allocation_quotas IS 'Выделенные квоты на период для конкретной компании';

CREATE INDEX idx_quotas_lookup ON allocation_quotas (organization_id, species_id, region_id, period_start, period_end);

-- =============================================
-- 8. Отчёты об улове
-- =============================================
CREATE TABLE catch_reports (
                               id             SERIAL PRIMARY KEY,
                               organization_id INTEGER NOT NULL REFERENCES organizations(id) ON DELETE RESTRICT,
                               reported_by     INTEGER NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
                               species_id      INTEGER NOT NULL REFERENCES fish_species(id) ON DELETE RESTRICT,
                               region_id       INTEGER NOT NULL REFERENCES fishing_regions(id) ON DELETE RESTRICT,
                               fishing_date    DATE NOT NULL CHECK (fishing_date <= CURRENT_DATE),
                               weight_kg       NUMERIC(12, 3) NOT NULL CHECK (weight_kg > 0),
                               notes           TEXT,
                               is_verified     BOOLEAN NOT NULL DEFAULT FALSE,
                               created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

COMMENT ON TABLE catch_reports IS 'Фактические отчёты об уловах';

CREATE INDEX idx_catch_reports_org ON catch_reports (organization_id);
CREATE INDEX idx_catch_reports_date ON catch_reports (fishing_date);
CREATE INDEX idx_catch_reports_species_region ON catch_reports (species_id, region_id);

-- Вместо CHECK с подзапросом — триггерная валидация,
-- т.к. CHECK не может надёжно ссылаться на другие таблицы.
CREATE OR REPLACE FUNCTION enforce_catch_user_org()
RETURNS TRIGGER AS $$
DECLARE
user_org_id INTEGER;
BEGIN
SELECT u.organization_id INTO user_org_id FROM users u WHERE u.id = NEW.reported_by;
-- Разрешаем, если пользователь имеет NULL organization_id (например, суперадмин).
IF user_org_id IS NULL THEN
        RETURN NEW;
END IF;
    IF user_org_id != NEW.organization_id THEN
        RAISE EXCEPTION 'Пользователь (id=%) принадлежит организации % , не совпадающей с указанной организацией отчёта %',
            NEW.reported_by, user_org_id, NEW.organization_id;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_catch_user_org
    BEFORE INSERT OR UPDATE ON catch_reports
                         FOR EACH ROW EXECUTE FUNCTION enforce_catch_user_org();

-- =============================================
-- ЗАПОЛНЕНИЕ НАЧАЛЬНЫМИ ДАННЫМИ
-- =============================================

-- Регионы
INSERT INTO fishing_regions (code, name) VALUES
                                             ('AZOV', 'Азовское море'),
                                             ('BLACK', 'Чёрное море');

-- Виды рыбы
INSERT INTO fish_species (scientific_name, common_name, is_endangered) VALUES
                                                                           ('Engraulis encrasicolus', 'Хамса', FALSE),
                                                                           ('Clupeonella cultriventris', 'Тюлька', FALSE),
                                                                           ('Mugil cephalus', 'Кефаль', FALSE),
                                                                           ('Psetta maxima', 'Камбала-калкан', FALSE);

-- Организации
INSERT INTO organizations (name, org_type, inn, region_id) VALUES
                                                               ('Рыболовецкая артель «Донская»', 'ООО', '123456789012', 1),
                                                               ('ООО «Черноморский промысел»', 'ООО', '234567890123', 2),
                                                               ('Управление Росрыболовства по ЮФО', 'Госорган', NULL, 1),
                                                               ('Федеральный оператор FishLog', 'Госорган', NULL, 1);

-- Роли
INSERT INTO roles (name, description) VALUES
                                          ('admin', 'Суперадмин системы'),
                                          ('fisherman', 'Рыбак (вносит уловы)'),
                                          ('inspector', 'Инспектор (верифицирует отчёты)');

-- Пользователи
INSERT INTO users (organization_id, username, email, password_hash) VALUES
                                                                        (1, 'user_don', 'user@don.example.com', 'hash_fisherman'),
                                                                        (2, 'user_black', 'user@black.example.com', 'hash_fisherman'),
                                                                        (3, 'inspector_yugo', 'inspector@rosryb.ru', 'hash_inspector'),
                                                                        (NULL, 'superadmin', 'admin@fishlog.ru', 'hash_admin');

-- Назначение ролей
INSERT INTO user_roles (user_id, role_id) VALUES
                                              (1, 2),
                                              (2, 2),
                                              (3, 3),
                                              (4, 1);

-- Квоты на сезон 2025 (только для компаний)
INSERT INTO allocation_quotas (organization_id, species_id, region_id, period_start, period_end, limit_kg)
SELECT
    o.id,
    fs.id,
    o.region_id,
    '2025-04-01'::DATE,
    '2025-11-30'::DATE,
    q.limit_kg
FROM (VALUES
          ('Рыболовецкая артель «Донская»', 'Engraulis encrasicolus', 15000.000),
          ('Рыболовецкая артель «Донская»', 'Clupeonella cultriventris', 10000.000),
          ('ООО «Черноморский промысел»', 'Engraulis encrasicolus', 8000.000),
          ('ООО «Черноморский промысел»', 'Psetta maxima', 1200.000)
     ) AS q (org_name, sci_name, limit_kg)
         JOIN organizations o ON o.name = q.org_name
         JOIN fish_species fs ON fs.scientific_name = q.sci_name;

-- Пример отчёта об улове
INSERT INTO catch_reports (organization_id, reported_by, species_id, region_id, fishing_date, weight_kg, notes)
SELECT
    o.id,
    u.id,
    fs.id,
    o.region_id,
    '2025-10-20'::DATE,
    1250.500,
    'Улов хамсы в Азовском море'
FROM organizations o
         JOIN users u ON u.organization_id = o.id
         JOIN fish_species fs ON fs.common_name = 'Хамса'
WHERE o.name = 'Рыболовецкая артель «Донская»';
