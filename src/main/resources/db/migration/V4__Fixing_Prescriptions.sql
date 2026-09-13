-- Step 1: Add serial_no column (guarded)
ALTER TABLE prescription_medicines
    ADD COLUMN IF NOT EXISTS serial_no BIGINT;

-- Step 2: Backfill serial_no per prescription_id, ordered by created_at (guarded)
UPDATE prescription_medicines pm
SET serial_no = ranked.rn
FROM (SELECT id,
             ROW_NUMBER() OVER (
                 PARTITION BY prescription_id
                 ORDER BY created_at
                 ) AS rn
      FROM prescription_medicines) ranked
WHERE pm.id = ranked.id
  AND pm.serial_no IS NULL;

-- Step 3: Enforce NOT NULL (guarded)
DO
$$
    BEGIN
        IF EXISTS (SELECT 1
                   FROM information_schema.columns
                   WHERE table_name = 'prescription_medicines'
                     AND column_name = 'serial_no'
                     AND is_nullable = 'YES') THEN
            ALTER TABLE prescription_medicines
                ALTER COLUMN serial_no SET NOT NULL;
        END IF;
    END
$$;
