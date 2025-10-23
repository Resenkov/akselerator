BEGIN;

-- Удаляем всё
DROP TABLE IF EXISTS catch_reports, annual_quotas, user_roles, users, roles, fish_species, fishing_regions;

-- =============================================
-- helper: функция, триггер для updated_at
-- =============================================
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- =============================================
-- 1. Справочник: Рыбопромысловые регионы
-- =============================================
CREATE TABLE fishing_regions (
                                 id          SERIAL PRIMARY KEY,
                                 code        VARCHAR(20) NOT NULL UNIQUE, -- AZOV, BLACK
                                 name        VARCHAR(100) NOT NULL,
                                 created_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                                 updated_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
CREATE UNIQUE INDEX idx_fishing_regions_code ON fishing_regions (code);

CREATE TRIGGER trg_fishing_regions_updated_at
    BEFORE UPDATE ON fishing_regions
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- =============================================
-- 2. Справочник: Виды рыбы
-- =============================================
CREATE TABLE fish_species (
                              id               SERIAL PRIMARY KEY,
                              scientific_name  VARCHAR(100) NOT NULL UNIQUE,
                              common_name      VARCHAR(100) NOT NULL,
                              is_endangered    BOOLEAN NOT NULL DEFAULT FALSE,
                              created_at       TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                              updated_at       TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
CREATE UNIQUE INDEX idx_fish_species_scientific ON fish_species (scientific_name);

CREATE TRIGGER trg_fish_species_updated_at
    BEFORE UPDATE ON fish_species
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- =============================================
-- 3. Роли
-- =============================================
CREATE TABLE roles (
                       id          SERIAL PRIMARY KEY,
                       name        VARCHAR(50) NOT NULL UNIQUE,
                       description TEXT
);

-- =============================================
-- 4. Пользователи
-- =============================================
CREATE TABLE users (
                       id            SERIAL PRIMARY KEY,
                       username      VARCHAR(50) NOT NULL UNIQUE,
                       email         VARCHAR(255) UNIQUE,
                       password_hash TEXT NOT NULL,
                       is_active     BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at    TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                       updated_at    TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_users_username ON users(username);

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- =============================================
-- 5. Связь пользователей и ролей
-- =============================================
CREATE TABLE user_roles (
                            user_id     INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                            role_id     INTEGER NOT NULL REFERENCES roles(id) ON DELETE RESTRICT,
                            granted_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                            granted_by  INTEGER REFERENCES users(id) ON DELETE SET NULL,
                            PRIMARY KEY (user_id, role_id)
);

CREATE INDEX idx_user_roles_granted_by ON user_roles(granted_by);

-- =============================================
-- 6. Годовые квоты
-- =============================================
CREATE TABLE annual_quotas (
                               id          SERIAL PRIMARY KEY,
                               species_id  INTEGER NOT NULL REFERENCES fish_species(id) ON DELETE RESTRICT,
                               region_id   INTEGER NOT NULL REFERENCES fishing_regions(id) ON DELETE RESTRICT,
                               year        INTEGER NOT NULL CHECK (year > 2000),
    limit_kg    NUMERIC(12, 3) NOT NULL CHECK (limit_kg > 0),
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE (species_id, region_id, year)
);

CREATE INDEX idx_annual_quotas_year ON annual_quotas(year);
CREATE INDEX idx_annual_quotas_species_region ON annual_quotas(species_id, region_id);

CREATE TRIGGER trg_annual_quotas_updated_at
    BEFORE UPDATE ON annual_quotas
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- =============================================
-- 7. Отчёты об улове
-- =============================================
CREATE TABLE catch_reports (
                               id             SERIAL PRIMARY KEY,
                               reported_by    INTEGER NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
                               species_id     INTEGER NOT NULL REFERENCES fish_species(id) ON DELETE RESTRICT,
                               region_id      INTEGER NOT NULL REFERENCES fishing_regions(id) ON DELETE RESTRICT,
                               fishing_date   DATE NOT NULL CHECK (fishing_date <= CURRENT_DATE),
                               reporting_date DATE NOT NULL DEFAULT CURRENT_DATE,
                               weight_kg      NUMERIC(12, 3) NOT NULL CHECK (weight_kg > 0),
                               notes          TEXT,
                               is_verified    BOOLEAN NOT NULL DEFAULT FALSE,
                               created_at     TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                               updated_at     TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- индексы для фильтров (админские просмотры)
CREATE INDEX idx_catch_reports_reported_by ON catch_reports (reported_by);
CREATE INDEX idx_catch_reports_species ON catch_reports (species_id);
CREATE INDEX idx_catch_reports_region ON catch_reports (region_id);
CREATE INDEX idx_catch_reports_fishing_date ON catch_reports (fishing_date);
-- composite индекс для быстрых агрегатов по квотам (species+region+date)
CREATE INDEX idx_catch_reports_species_region_date ON catch_reports (species_id, region_id, fishing_date);

CREATE TRIGGER trg_catch_reports_updated_at
    BEFORE UPDATE ON catch_reports
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

COMMIT;

-- =============================================
-- Утилиты / представления (опционально, удобно для UI)
-- =============================================
/*
  Представление, показывающее использование квоты за год:
  fields: quota row + used_kg, remaining_kg, used_percent
*/
CREATE OR REPLACE VIEW quota_usage AS
SELECT
    q.id AS quota_id,
    q.species_id,
    q.region_id,
    q.year,
    q.limit_kg,
    COALESCE(SUM(cr.weight_kg), 0)::NUMERIC(12,3) AS used_kg,
    (q.limit_kg - COALESCE(SUM(cr.weight_kg),0))::NUMERIC(12,3) AS remaining_kg,
    CASE WHEN q.limit_kg > 0
             THEN ROUND((COALESCE(SUM(cr.weight_kg),0) / q.limit_kg) * 100, 2)
         ELSE 0
        END AS used_percent
FROM annual_quotas q
         LEFT JOIN catch_reports cr
                   ON cr.species_id = q.species_id
                       AND cr.region_id = q.region_id
                       AND EXTRACT(YEAR FROM cr.fishing_date)::INT = q.year
GROUP BY q.id, q.species_id, q.region_id, q.year, q.limit_kg;

-- =============================================
-- Seed: примерные роли и справочники (опционально)
-- =============================================
INSERT INTO roles (name, description) VALUES
                                          ('FISHERMAN', 'Рыбак, может вводить уловы и смотреть свои записи'),
                                          ('ADMIN', 'Администратор, может задавать квоты и просматривать отчёты');

INSERT INTO fishing_regions (code, name) VALUES
                                             ('AZOV', 'Азовское море'),
                                             ('BARENTS', 'Баренцево море'),
                                             ('BLACK', 'Чёрное море');

INSERT INTO fish_species (scientific_name, common_name) VALUES
                                                            ('Gadus morhua', 'Треска'),
                                                            ('Clupea harengus', 'Сельдь'),
                                                            ('Pleuronectes platessa', 'Камбала');

-- Примечание: создание пользователя/пароля рекомендуется делать через отдельный seed,
-- где password_hash заполняется реальным bcrypt-хэшем (не plain text).

