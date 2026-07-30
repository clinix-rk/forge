-- Allow multiple payments per receipt (one receipt covers many treatment sessions)
ALTER TABLE payments
    DROP CONSTRAINT IF EXISTS payments_recipt_id_key;
