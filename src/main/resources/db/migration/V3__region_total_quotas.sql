-- V4__region_total_quotas.sql
-- Общая квота на регион + проверки при INSERT/UPDATE allocation_quotas и region_total_quotas

-- 1) Таблица общей квоты на регион
CREATE TABLE IF NOT EXISTS region_total_quotas (
                                                   id           SERIAL PRIMARY KEY,
                                                   region_id    INTEGER NOT NULL REFERENCES fishing_regions(id) ON DELETE RESTRICT,
    period_start DATE    NOT NULL,
    period_end   DATE    NOT NULL CHECK (period_end >= period_start),
    limit_kg     NUMERIC(12,3) NOT NULL CHECK (limit_kg > 0),
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE (region_id, period_start, period_end)
    );

COMMENT ON TABLE region_total_quotas IS 'Общая квота на регион за период (без привязки к виду)';
CREATE INDEX IF NOT EXISTS idx_region_total_quotas_lookup
    ON region_total_quotas (region_id, period_start, period_end);

-- 2) Проверка: при добавлении/обновлении allocation_quotas не превышаем общую региональную квоту
CREATE OR REPLACE FUNCTION check_allocation_against_region_total()
RETURNS TRIGGER AS $$
DECLARE
rt RECORD;
    sum_alloc NUMERIC(18,3);
    exclude_id INTEGER := NULL;
BEGIN
    IF TG_OP = 'UPDATE' THEN
        exclude_id := NEW.id;
END IF;

FOR rt IN
SELECT r.* FROM region_total_quotas r
WHERE r.region_id = NEW.region_id
  AND NOT (r.period_end < NEW.period_start OR r.period_start > NEW.period_end)
    LOOP
SELECT COALESCE(SUM(a.limit_kg), 0)
INTO sum_alloc
FROM allocation_quotas a
WHERE a.region_id = NEW.region_id
  AND NOT (a.period_end < rt.period_start OR a.period_start > rt.period_end)
  AND (exclude_id IS NULL OR a.id <> exclude_id);

sum_alloc := sum_alloc + NEW.limit_kg;

        IF sum_alloc > rt.limit_kg THEN
            RAISE EXCEPTION
                'Сумма мини-квот по региону % за период %..% превысит общий лимит: % кг < % кг (после добавления).',
                NEW.region_id, rt.period_start, rt.period_end, rt.limit_kg, sum_alloc;
END IF;
END LOOP;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_allocation_vs_region_total ON allocation_quotas;
CREATE TRIGGER trg_allocation_vs_region_total
    BEFORE INSERT OR UPDATE ON allocation_quotas
                         FOR EACH ROW EXECUTE FUNCTION check_allocation_against_region_total();

-- 3) Проверка: при создании/изменении общей региональной квоты — уже существующие мини-квоты не должны её превышать
CREATE OR REPLACE FUNCTION check_region_total_vs_allocations()
RETURNS TRIGGER AS $$
DECLARE
sum_alloc NUMERIC(18,3);
BEGIN
SELECT COALESCE(SUM(a.limit_kg), 0)
INTO sum_alloc
FROM allocation_quotas a
WHERE a.region_id = NEW.region_id
  AND NOT (a.period_end < NEW.period_start OR a.period_start > NEW.period_end);

IF sum_alloc > NEW.limit_kg THEN
        RAISE EXCEPTION
            'Текущая сумма мини-квот по региону % за период %..% = % кг превышает устанавливаемый общий лимит % кг.',
            NEW.region_id, NEW.period_start, NEW.period_end, sum_alloc, NEW.limit_kg;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_region_total_vs_allocations ON region_total_quotas;
CREATE TRIGGER trg_region_total_vs_allocations
    BEFORE INSERT OR UPDATE ON region_total_quotas
                         FOR EACH ROW EXECUTE FUNCTION check_region_total_vs_allocations();
