-- V2__add_updated_at_columns.sql
-- Добавляет updated_at для таблиц, но сначала проверяет наличие таблицы.
-- Сохранять в UTF-8 без BOM.

-- helper: для каждой таблицы выполняем блок IF EXISTS ... THEN (ALTER + UPDATE) END IF

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'fishing_regions' AND table_schema = 'public') THEN
ALTER TABLE IF EXISTS fishing_regions
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW();
UPDATE fishing_regions SET updated_at = COALESCE(updated_at, created_at) WHERE (updated_at IS NULL OR created_at IS NOT NULL);
END IF;
END;
$$;

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'fish_species' AND table_schema = 'public') THEN
ALTER TABLE IF EXISTS fish_species
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW();
UPDATE fish_species SET updated_at = COALESCE(updated_at, created_at) WHERE (updated_at IS NULL OR created_at IS NOT NULL);
END IF;
END;
$$;

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'organizations' AND table_schema = 'public') THEN
ALTER TABLE IF EXISTS organizations
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW();
UPDATE organizations SET updated_at = COALESCE(updated_at, created_at) WHERE (updated_at IS NULL OR created_at IS NOT NULL);
END IF;
END;
$$;

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'users' AND table_schema = 'public') THEN
ALTER TABLE IF EXISTS users
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW();
UPDATE users SET updated_at = COALESCE(updated_at, created_at) WHERE (updated_at IS NULL OR created_at IS NOT NULL);
END IF;
END;
$$;

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'allocation_quotas' AND table_schema = 'public') THEN
ALTER TABLE IF EXISTS allocation_quotas
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW();
UPDATE allocation_quotas SET updated_at = COALESCE(updated_at, created_at) WHERE (updated_at IS NULL OR created_at IS NOT NULL);
END IF;
END;
$$;

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'catch_reports' AND table_schema = 'public') THEN
ALTER TABLE IF EXISTS catch_reports
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW();
UPDATE catch_reports SET updated_at = COALESCE(updated_at, created_at) WHERE (updated_at IS NULL OR created_at IS NOT NULL);
END IF;
END;
$$;

-- Если у вас осталась старая таблица annual_quotas — обработаем её безопасно:
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'annual_quotas' AND table_schema = 'public') THEN
ALTER TABLE IF EXISTS annual_quotas
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW();
UPDATE annual_quotas SET updated_at = COALESCE(updated_at, created_at) WHERE (updated_at IS NULL OR created_at IS NOT NULL);
END IF;
END;
$$;
