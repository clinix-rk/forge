DO
$$
    BEGIN
        -- 1. appointments
        IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'appointments') THEN
            ALTER TABLE appointments
                ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
            ALTER TABLE appointments
                ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
        END IF;

        -- 2. complain_categories
        IF EXISTS (SELECT
                   FROM information_schema.tables
                   WHERE table_name = 'complain_categories') THEN
            ALTER TABLE complain_categories
                ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
            ALTER TABLE complain_categories
                ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
        END IF;

        -- 3. complains
        IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'complains') THEN
            ALTER TABLE complains
                ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
            ALTER TABLE complains
                ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
        END IF;

        -- 4. payments
        IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'payments') THEN
            ALTER TABLE payments
                ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
            ALTER TABLE payments
                ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
        END IF;

        -- 5. recipts
        IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'recipts') THEN
            ALTER TABLE recipts
                ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
            ALTER TABLE recipts
                ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
        END IF;

        -- 6. medical_conditions
        IF EXISTS (SELECT
                   FROM information_schema.tables
                   WHERE table_name = 'medical_conditions') THEN
            ALTER TABLE medical_conditions
                ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
            ALTER TABLE medical_conditions
                ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
        END IF;

        -- 7. patients
        IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'patients') THEN
            ALTER TABLE patients
                ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
            ALTER TABLE patients
                ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
        END IF;

        -- 8. phone_numbers
        IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'phone_numbers') THEN
            ALTER TABLE phone_numbers
                ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
            ALTER TABLE phone_numbers
                ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
        END IF;

        -- 9. drug_dosages
        IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'drug_dosages') THEN
            ALTER TABLE drug_dosages
                ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
            ALTER TABLE drug_dosages
                ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
        END IF;

        -- 10. medicines
        IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'medicines') THEN
            ALTER TABLE medicines
                ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
            ALTER TABLE medicines
                ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
        END IF;

        -- 11. prescriptions
        IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'prescriptions') THEN
            ALTER TABLE prescriptions
                ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
            ALTER TABLE prescriptions
                ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
        END IF;

        -- 12. prescription_medicines
        IF EXISTS (SELECT
                   FROM information_schema.tables
                   WHERE table_name = 'prescription_medicines') THEN
            ALTER TABLE prescription_medicines
                ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
            ALTER TABLE prescription_medicines
                ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
        END IF;

        -- 13. files
        IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'files') THEN
            ALTER TABLE files
                ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
            ALTER TABLE files
                ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
        END IF;

        -- 14. suggestions
        IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'suggestions') THEN
            ALTER TABLE suggestions
                ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
            ALTER TABLE suggestions
                ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
        END IF;

        -- 15. treatment_categories
        IF EXISTS (SELECT
                   FROM information_schema.tables
                   WHERE table_name = 'treatment_categories') THEN
            ALTER TABLE treatment_categories
                ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
            ALTER TABLE treatment_categories
                ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
        END IF;

        -- 16. treatments
        IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'treatments') THEN
            ALTER TABLE treatments
                ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
            ALTER TABLE treatments
                ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
        END IF;

        -- 17. users
        IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'users') THEN
            ALTER TABLE users
                ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
            ALTER TABLE users
                ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
        END IF;
    END
$$;
