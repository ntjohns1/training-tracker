-- Repair enum CHECK constraints that drifted under the old `ddl-auto: update` schema.
--
-- Hibernate's `validate` inspects tables/columns/types but NOT check constraints, so this drift
-- survived the V1 baseline adoption and only surfaced on INSERT. Two problems:
--
--   1. exercise_sets.unit still allowed the pre-rename Unit values ('KGS','LBS') instead of the
--      current ('KG','LB') -- any set carrying a unit failed to insert.
--   2. The four *_status_check constraints predate the PENDING_CONFIRMATION Status constant, which
--      finish-day transitions use.
--
-- Written drop-then-add with IF EXISTS so it is also a safe no-op re-assert on fresh databases,
-- where V1 already created these constraints correctly.

ALTER TABLE exercise_sets DROP CONSTRAINT IF EXISTS exercise_sets_unit_check;
ALTER TABLE exercise_sets ADD CONSTRAINT exercise_sets_unit_check
    CHECK (unit IN ('KG', 'LB'));

ALTER TABLE day_exercises DROP CONSTRAINT IF EXISTS day_exercises_status_check;
ALTER TABLE day_exercises ADD CONSTRAINT day_exercises_status_check
    CHECK (status IN ('COMPLETE','EMPTY','PARTIAL','PENDING','PENDING_CONFIRMATION',
                      'PENDING_FEEDBACK','PENDING_WEIGHT','PROGRAMMED','READY','SKIPPED',
                      'UNPROGRAMMED'));

ALTER TABLE day_muscle_groups DROP CONSTRAINT IF EXISTS day_muscle_groups_status_check;
ALTER TABLE day_muscle_groups ADD CONSTRAINT day_muscle_groups_status_check
    CHECK (status IN ('COMPLETE','EMPTY','PARTIAL','PENDING','PENDING_CONFIRMATION',
                      'PENDING_FEEDBACK','PENDING_WEIGHT','PROGRAMMED','READY','SKIPPED',
                      'UNPROGRAMMED'));

ALTER TABLE exercise_sets DROP CONSTRAINT IF EXISTS exercise_sets_status_check;
ALTER TABLE exercise_sets ADD CONSTRAINT exercise_sets_status_check
    CHECK (status IN ('COMPLETE','EMPTY','PARTIAL','PENDING','PENDING_CONFIRMATION',
                      'PENDING_FEEDBACK','PENDING_WEIGHT','PROGRAMMED','READY','SKIPPED',
                      'UNPROGRAMMED'));

ALTER TABLE mesocycles DROP CONSTRAINT IF EXISTS mesocycles_status_check;
ALTER TABLE mesocycles ADD CONSTRAINT mesocycles_status_check
    CHECK (status IN ('COMPLETE','EMPTY','PARTIAL','PENDING','PENDING_CONFIRMATION',
                      'PENDING_FEEDBACK','PENDING_WEIGHT','PROGRAMMED','READY','SKIPPED',
                      'UNPROGRAMMED'));
