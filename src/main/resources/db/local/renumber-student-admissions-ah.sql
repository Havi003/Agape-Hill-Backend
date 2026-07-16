-- LOCAL-ONLY cleanup script.
-- Purpose: renumber existing local students from long generated values like
-- ADM1782216420347 to short payment-friendly values like AH001, AH002, AH003.
--
-- Do not run this in production until you have confirmed no external records,
-- receipts, or parent instructions depend on the old admission numbers.
--
-- Ordering rule:
--   1. oldest registered_date first
--   2. full_name alphabetically
--   3. id as the final stable tie-breaker

BEGIN;

WITH numbered_students AS (
    SELECT
        id,
        'AH' || LPAD(ROW_NUMBER() OVER (
            ORDER BY registered_date NULLS LAST, full_name, id
        )::TEXT, 3, '0') AS new_admission_number
    FROM school.students
)
UPDATE school.students AS students
SET admission_number = numbered_students.new_admission_number
FROM numbered_students
WHERE students.id = numbered_students.id;

COMMIT;
