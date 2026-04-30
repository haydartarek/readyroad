UPDATE sign_questions
SET question_nl = 'U bent een leerling-bestuurder met een persoonlijke maximumsnelheid van 90 km/u. Geldt ook voor u de snelheidslimiet van een ZC43-zone?'
WHERE question_ref = 'ZC43_Q08'
  AND question_nl LIKE '%ZC43-28 zone%';
