-- DoctorEntity no longer persists total_patients; patient counts come from the patients relation.
ALTER TABLE doctors
    DROP COLUMN total_patients;
