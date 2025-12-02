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


