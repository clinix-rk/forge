DO
$$
BEGIN
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'suggestions') THEN
        ALTER TABLE suggestions DROP CONSTRAINT IF EXISTS suggestions_status_check;

        UPDATE suggestions
        SET status = 'SUGGESTED'
        WHERE status = 'PENDING';

        ALTER TABLE suggestions
            ADD CONSTRAINT suggestions_status_check
            CHECK (status IN ('SUGGESTED', 'ACCEPTED', 'DECLINED'));
    END IF;
END
$$;
