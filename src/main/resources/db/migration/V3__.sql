-- Step 1: Remove NOT NULL constraint from dosage_id column
ALTER TABLE prescription_medicines
    ALTER COLUMN dosage_id DROP NOT NULL;

-- Step 2: Add unique constraint on prescription_id and medicine_id
ALTER TABLE prescription_medicines
    ADD CONSTRAINT uk_prescription_medicine UNIQUE (prescription_id, medicine_id);

-- Step 3: Add new payments columns as NULLABLE first (cannot add NOT NULL to a populated table without a default)
ALTER TABLE payments
    ADD COLUMN treatment_details TEXT;

ALTER TABLE payments
    ADD COLUMN received_date DATE;

-- Step 4: Backfill from the related treatments table via join
UPDATE payments p
SET received_date     = t.date,
    treatment_details = t.details
FROM treatments t
WHERE p.treatment_id = t.id;

-- Step 5: Now that every row is populated, enforce NOT NULL constaints
ALTER TABLE payments
    ALTER COLUMN received_date SET NOT NULL;

ALTER TABLE payments
    ALTER COLUMN treatment_details SET NOT NULL;
