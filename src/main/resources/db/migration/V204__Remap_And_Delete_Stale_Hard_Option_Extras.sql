-- Remap historical references from legacy third HARD options/choices
-- to the visible wrong option that remains in the current two-option model.

UPDATE exam_simulation_answers
SET selected_option_id = 565
WHERE selected_option_id = 566;

UPDATE exam_simulation_answers
SET selected_option_id = 570
WHERE selected_option_id = 571;

UPDATE exam_simulation_answers
SET selected_option_id = 588
WHERE selected_option_id = 589;

UPDATE exam_simulation_answers
SET selected_option_id = 630
WHERE selected_option_id = 631;

UPDATE exam_simulation_answers
SET selected_option_id = 633
WHERE selected_option_id = 634;

UPDATE exam_simulation_answers
SET selected_option_id = 636
WHERE selected_option_id = 637;

UPDATE exam_simulation_answers
SET selected_option_id = 639
WHERE selected_option_id = 640;

UPDATE exam_simulation_answers
SET selected_option_id = 642
WHERE selected_option_id = 643;

UPDATE exam_simulation_answers
SET selected_option_id = 645
WHERE selected_option_id = 646;

UPDATE exam_simulation_answers
SET selected_option_id = 648
WHERE selected_option_id = 649;

UPDATE sign_practice_answers
SET choice_id = 9089
WHERE choice_id = 9090;

UPDATE sign_practice_answers
SET choice_id = 9134
WHERE choice_id = 9135;

UPDATE sign_practice_answers
SET choice_id = 9157
WHERE choice_id = 9158;

UPDATE sign_practice_answers
SET choice_id = 9180
WHERE choice_id = 9181;

UPDATE sign_random_practice_questions
SET selected_choice_id = 9089
WHERE selected_choice_id = 9090;

UPDATE sign_random_practice_questions
SET selected_choice_id = 9134
WHERE selected_choice_id = 9135;

UPDATE sign_random_practice_questions
SET selected_choice_id = 9157
WHERE selected_choice_id = 9158;

UPDATE sign_random_practice_questions
SET selected_choice_id = 9180
WHERE selected_choice_id = 9181;

DELETE FROM quiz_answer_options
WHERE id IN (566, 571, 589, 631, 634, 637, 640, 643, 646, 649);

DELETE FROM sign_choices
WHERE id IN (9090, 9135, 9158, 9181);
