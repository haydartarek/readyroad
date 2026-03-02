-- V96: Set typical_error_type on quiz questions based on category code
-- Previously all questions had typical_error_type = NULL, making error pattern analytics meaningless.
-- Belgian driving categories: A=Warning, B=Priority, C=Prohibition, D=Obligation,
--   E=Parking/Stopping, F=Info/Direction, G=Supplementary panels, Z=Zone signs,
--   M=Mandatory, H=Indication signs

UPDATE quiz_questions qq
JOIN categories c ON c.id = qq.category_id
SET qq.typical_error_type = CASE c.code
    WHEN 'A' THEN 'SIGN_CONFUSION'            -- Warning signs: easily confused shapes
    WHEN 'B' THEN 'PRIORITY_MISUNDERSTANDING' -- Priority / right-of-way signs
    WHEN 'C' THEN 'RULE_OVERGENERALIZATION'   -- Prohibition signs: rules misapplied
    WHEN 'D' THEN 'RULE_OVERGENERALIZATION'   -- Obligation/Mandatory: rules misapplied
    WHEN 'E' THEN 'ZONE_CONFUSION'            -- Parking/Stopping restrictions: zone confusion
    WHEN 'F' THEN 'SIGN_CONFUSION'            -- Information/Direction signs: confusion
    WHEN 'G' THEN 'SUPPLEMENTARY_IGNORED'     -- Supplementary panels: often overlooked
    WHEN 'Z' THEN 'ZONE_CONFUSION'            -- Zone signs: zone boundary confusion
    WHEN 'M' THEN 'SIGN_CONFUSION'            -- Mandatory direction signs: shape confusion
    WHEN 'H' THEN 'SIGN_CONFUSION'            -- Indication/blue signs: confusion
    ELSE 'RULE_OVERGENERALIZATION'
END
WHERE qq.typical_error_type IS NULL OR qq.typical_error_type = 'OTHER';
