-- Step 1: Remove NOT NULL constraint from dosage_id column
ALTER TABLE prescription_medicines
    ALTER COLUMN dosage_id DROP NOT NULL;

-- Step 2: Add unique constraint on prescription_id and medicine_id (guarded)
DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_prescription_medicine') THEN
            ALTER TABLE prescription_medicines
                ADD CONSTRAINT uk_prescription_medicine UNIQUE (prescription_id, medicine_id);
        END IF;
    END
$$;

-- Step 3: Add new payments columns as NULLABLE first (cannot add NOT NULL to a populated table without a default)
ALTER TABLE payments
    ADD COLUMN IF NOT EXISTS treatment_details TEXT;

ALTER TABLE payments
    ADD COLUMN IF NOT EXISTS received_date DATE;

-- Step 4: Backfill from the related treatments table via join
UPDATE payments p
SET received_date     = t.date,
    treatment_details = t.details
FROM treatments t
WHERE p.treatment_id = t.id
  AND (p.received_date IS NULL OR p.treatment_details IS NULL);


-- Step 5: Now that every row is populated, enforce NOT NULL constaints
DO
$$
    BEGIN
        IF EXISTS (SELECT 1
                   FROM information_schema.columns
                   WHERE table_name = 'payments'
                     AND column_name = 'received_date'
                     AND is_nullable = 'YES') THEN
            ALTER TABLE payments
                ALTER COLUMN received_date SET NOT NULL;
        END IF;
    END
$$;

DO
$$
    BEGIN
        IF EXISTS (SELECT 1
                   FROM information_schema.columns
                   WHERE table_name = 'payments'
                     AND column_name = 'treatment_details'
                     AND is_nullable = 'YES') THEN
            ALTER TABLE payments
                ALTER COLUMN treatment_details SET NOT NULL;
        END IF;
    END
$$;
