-- V133: Fix F79 image_url
--
-- Bug introduced in V118: both F79 and F79-V1 were set to the "met afstandsaanduiding" image.
-- F79   = "Tijdelijke verdeling van de rijstroken"   (zonder afstand) → different image
-- F79-V1 = "Tijdelijke verdeling van de rijstroken (met afstandsaanduiding)" → already correct

UPDATE traffic_signs
SET image_url  = 'images/signs/information_signs/F79 Tijdelijke verdeling van de rijstroken.png',
    updated_at = NOW()
WHERE sign_code = 'F79';
