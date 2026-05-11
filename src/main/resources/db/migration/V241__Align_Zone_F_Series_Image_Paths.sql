-- Align existing databases with the final official Zone-F image filenames.

UPDATE road_signs
SET image_path = CASE sign_code
    WHEN 'F103' THEN '/images/signs/zone_signs/Zone-F103 Begin van een voetgangerszone.png'
    WHEN 'F105' THEN '/images/signs/zone_signs/Zone-F105-Einde zone van een voetgangerszone.png'
    WHEN 'F117' THEN '/images/signs/zone_signs/Zone-F117 Begin van lage emissiezone.png'
    WHEN 'F118' THEN '/images/signs/zone_signs/Zone-F118-Einde van lage emissiezone.png'
    ELSE image_path
END
WHERE sign_code IN ('F103', 'F105', 'F117', 'F118');

UPDATE quiz_questions
SET content_image_url = CASE content_image_url
    WHEN '/images/signs/zone_signs/F103 Begin van een voetgangerszone.png' THEN '/images/signs/zone_signs/Zone-F103 Begin van een voetgangerszone.png'
    WHEN '/images/signs/zone_signs/F105-Einde zone van een voetgangerszone.png' THEN '/images/signs/zone_signs/Zone-F105-Einde zone van een voetgangerszone.png'
    WHEN '/images/signs/zone_signs/F117 Begin van lage emissiezone.png' THEN '/images/signs/zone_signs/Zone-F117 Begin van lage emissiezone.png'
    WHEN '/images/signs/zone_signs/F118-Einde van lage emissiezone.png' THEN '/images/signs/zone_signs/Zone-F118-Einde van lage emissiezone.png'
    ELSE content_image_url
END
WHERE content_image_url IN (
    '/images/signs/zone_signs/F103 Begin van een voetgangerszone.png',
    '/images/signs/zone_signs/F105-Einde zone van een voetgangerszone.png',
    '/images/signs/zone_signs/F117 Begin van lage emissiezone.png',
    '/images/signs/zone_signs/F118-Einde van lage emissiezone.png'
);

UPDATE exam_questions
SET image_url = CASE image_url
    WHEN '/images/signs/zone_signs/F103 Begin van een voetgangerszone.png' THEN '/images/signs/zone_signs/Zone-F103 Begin van een voetgangerszone.png'
    WHEN '/images/signs/zone_signs/F105-Einde zone van een voetgangerszone.png' THEN '/images/signs/zone_signs/Zone-F105-Einde zone van een voetgangerszone.png'
    WHEN '/images/signs/zone_signs/F117 Begin van lage emissiezone.png' THEN '/images/signs/zone_signs/Zone-F117 Begin van lage emissiezone.png'
    WHEN '/images/signs/zone_signs/F118-Einde van lage emissiezone.png' THEN '/images/signs/zone_signs/Zone-F118-Einde van lage emissiezone.png'
    ELSE image_url
END
WHERE image_url IN (
    '/images/signs/zone_signs/F103 Begin van een voetgangerszone.png',
    '/images/signs/zone_signs/F105-Einde zone van een voetgangerszone.png',
    '/images/signs/zone_signs/F117 Begin van lage emissiezone.png',
    '/images/signs/zone_signs/F118-Einde van lage emissiezone.png'
);
