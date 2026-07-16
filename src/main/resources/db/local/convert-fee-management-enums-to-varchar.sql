-- Purpose: align local fee-management tables with the current R2DBC entities.
-- The backend stores term/status/class/type values as Java Strings. If these
-- columns are PostgreSQL enum types, R2DBC inserts/updates can fail with:
-- executeMany; bad SQL grammar [INSERT INTO ...]

BEGIN;

-- Drop indexes whose predicates still compare columns to enum literals.
-- Example: status = 'PUBLISHED'::school.fee_structure_status_enum
DO $$
DECLARE
    idx RECORD;
BEGIN
    FOR idx IN
        SELECT schemaname, indexname
        FROM pg_indexes
        WHERE schemaname = 'school'
          AND tablename IN (
              'academic_terms',
              'fee_structures',
              'fee_structure_items',
              'student_charges',
              'fee_payments'
          )
          AND indexdef LIKE '%::school.%_enum%'
    LOOP
        EXECUTE FORMAT('DROP INDEX IF EXISTS %I.%I', idx.schemaname, idx.indexname);
    END LOOP;
END $$;

ALTER TABLE school.academic_terms
    ALTER COLUMN term_name DROP DEFAULT,
    ALTER COLUMN status DROP DEFAULT;

ALTER TABLE school.fee_structures
    ALTER COLUMN class_group DROP DEFAULT,
    ALTER COLUMN status DROP DEFAULT;

ALTER TABLE school.fee_structure_items
    ALTER COLUMN item_type DROP DEFAULT,
    ALTER COLUMN applies_to_class_group DROP DEFAULT;

ALTER TABLE school.student_charges
    ALTER COLUMN charge_type DROP DEFAULT,
    ALTER COLUMN status DROP DEFAULT;

ALTER TABLE school.fee_payments
    ALTER COLUMN payment_method DROP DEFAULT;

ALTER TABLE school.academic_terms
    ALTER COLUMN term_name TYPE VARCHAR(20) USING term_name::TEXT,
    ALTER COLUMN status TYPE VARCHAR(20) USING status::TEXT;

ALTER TABLE school.fee_structures
    ALTER COLUMN class_group TYPE VARCHAR(40) USING class_group::TEXT,
    ALTER COLUMN status TYPE VARCHAR(20) USING status::TEXT;

ALTER TABLE school.fee_structure_items
    ALTER COLUMN item_type TYPE VARCHAR(20) USING item_type::TEXT,
    ALTER COLUMN applies_to_class_group TYPE VARCHAR(40) USING applies_to_class_group::TEXT;

ALTER TABLE school.student_charges
    ALTER COLUMN charge_type TYPE VARCHAR(30) USING charge_type::TEXT,
    ALTER COLUMN status TYPE VARCHAR(20) USING status::TEXT;

ALTER TABLE school.fee_payments
    ALTER COLUMN payment_method TYPE VARCHAR(30) USING payment_method::TEXT;

ALTER TABLE school.academic_terms
    ALTER COLUMN status SET DEFAULT 'UPCOMING';

ALTER TABLE school.fee_structures
    ALTER COLUMN status SET DEFAULT 'DRAFT';

ALTER TABLE school.fee_structure_items
    ALTER COLUMN item_type SET DEFAULT 'COMPULSORY';

ALTER TABLE school.student_charges
    ALTER COLUMN charge_type SET DEFAULT 'TERM_FEE',
    ALTER COLUMN status SET DEFAULT 'ACTIVE';

ALTER TABLE school.fee_payments
    ALTER COLUMN payment_method SET DEFAULT 'MANUAL';

CREATE UNIQUE INDEX IF NOT EXISTS unique_published_fee_structure
    ON school.fee_structures (academic_year_id, term_id, class_group)
    WHERE status = 'PUBLISHED';

COMMIT;
