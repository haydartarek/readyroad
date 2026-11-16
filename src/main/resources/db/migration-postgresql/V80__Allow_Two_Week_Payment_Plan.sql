-- Preserve historical plans and allow the new 14-day plan.
ALTER TABLE purchases DROP CONSTRAINT purchases_plan_check;
ALTER TABLE purchases ADD CONSTRAINT purchases_plan_check CHECK (plan IN ('RIJVIA_3_DAYS', 'RIJVIA_1_WEEK', 'RIJVIA_2_WEEKS', 'RIJVIA_4_WEEKS'));
