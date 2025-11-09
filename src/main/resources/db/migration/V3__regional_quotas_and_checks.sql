-- V3__regional_quotas_and_checks.sql
-- Общие (региональные) квоты + проверки при вставке/обновлении allocation_quotas

CREATE TABLE IF NOT EXISTS regional_quotas (
    id          SERIAL PRIMARY KEY,
    species_id  INTEGER NOT NULL REFERENCES fish_species(id) ON DELETE RESTRICT,
    region_id   INTEGER NOT NULL REFERENCES fishing_regions(id) ON DELETE RESTRICT,
    period_start DATE NOT NULL,
    period_end   DATE NOT NULL CHECK (period_end >= period_start),
    limit_kg    NUMERIC(12,3) NOT NULL CHECK (limit_kg > 0),
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

COMMENT ON TABLE regional_quotas IS 'Общие (региональные) квоты: лимит по виду в районе на период';
CREATE UNIQUE INDEX IF NOT EXISTS idx_regional_quotas_unique ON regional_quotas (species_id, region_id, period_start, period_end);
CREATE INDEX IF NOT EXISTS idx_regional_quotas_lookup ON regional_quotas (species_id, region_id, period_start, period_end);

CREATE OR REPLACE FUNCTION check_allocation_against_regional_quota()
RETURNS TRIGGER AS $$
DECLARE
    regional_row RECORD;
    sum_alloc NUMERIC(18,3);
    exclude_id INTEGER := NULL;
BEGIN
    IF (TG_OP = 'UPDATE') THEN
        exclude_id := NEW.id;
    END IF;

    FOR regional_row IN
        SELECT r.*
        FROM regional_quotas r
        WHERE r.species_id = NEW.species_id
          AND r.region_id  = NEW.region_id
          AND NOT (r.period_end < NEW.period_start OR r.period_start > NEW.period_end)
    LOOP
        SELECT COALESCE(SUM(a.limit_kg), 0)
        INTO sum_alloc
        FROM allocation_quotas a
        WHERE a.species_id = NEW.species_id
          AND a.region_id = NEW.region_id
          AND NOT (a.period_end < regional_row.period_start OR a.period_start > regional_row.period_end)
          AND (exclude_id IS NULL OR a.id <> exclude_id);

        sum_alloc := sum_alloc + NEW.limit_kg;

        IF sum_alloc > regional_row.limit_kg THEN
            RAISE EXCEPTION
                'Сумма мини-квот по виду % в районе % за период % — % превышает общую региональную квоту % кг (текущее суммарное значение: % кг).',
                NEW.species_id, NEW.region_id, regional_row.period_start, regional_row.period_end, regional_row.limit_kg, sum_alloc;
        END IF;
    END LOOP;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_allocation_vs_regional ON allocation_quotas;
CREATE TRIGGER trg_allocation_vs_regional
    BEFORE INSERT OR UPDATE ON allocation_quotas
    FOR EACH ROW EXECUTE FUNCTION check_allocation_against_regional_quota();


CREATE OR REPLACE FUNCTION check_regional_quota_vs_allocations()
RETURNS TRIGGER AS $$
DECLARE
    sum_alloc NUMERIC(18,3);
    exclude_regional_id INTEGER := NULL;
BEGIN
    IF (TG_OP = 'UPDATE') THEN
        exclude_regional_id := NEW.id;
    END IF;

    SELECT COALESCE(SUM(a.limit_kg), 0)
    INTO sum_alloc
    FROM allocation_quotas a
    WHERE a.species_id = NEW.species_id
      AND a.region_id = NEW.region_id
      AND NOT (a.period_end < NEW.period_start OR a.period_start > NEW.period_end);

    IF sum_alloc > NEW.limit_kg THEN
        RAISE EXCEPTION
            'Существующие мини-квоты (сумма = % кг) по виду % в районе % за период %..% превышают новый региональный лимит (% кг).',
            sum_alloc, NEW.species_id, NEW.region_id, NEW.period_start, NEW.period_end, NEW.limit_kg;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_regional_vs_allocations ON regional_quotas;
CREATE TRIGGER trg_regional_vs_allocations
    BEFORE INSERT OR UPDATE ON regional_quotas
    FOR EACH ROW EXECUTE FUNCTION check_regional_quota_vs_allocations();
