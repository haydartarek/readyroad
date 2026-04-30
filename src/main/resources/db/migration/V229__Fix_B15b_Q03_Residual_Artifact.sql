-- Fix a residual learner-facing artifact left by earlier B15b cleanup passes.
-- The source JSON is already clean; this migration brings the persisted row back
-- into line without touching unrelated content.

UPDATE sign_questions
SET question_ar = 'ما هو الخطر الذي تشير إليه هذه العلامة المرورية؟'
WHERE question_ref = 'B15b_Q03'
  AND question_ar = 'ما الخطر الذي تنبّهك إليه العلامة المرورية: إعطاء الأولوية5b؟';
