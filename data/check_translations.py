#!/usr/bin/env python3
"""Find signs with incorrect translations (Dutch text in EN/FR/AR fields)"""

import json
from pathlib import Path

with open(Path(__file__).parent / 'traffic_signs_complete.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

print("Checking for incorrect translations...")
print("=" * 60)

problems = []

for sign_code, sign in data.items():
    nl_text = sign.get('name_nl', '')
    
    # Check if EN, FR, or AR contains the Dutch text (likely wrong)
    if nl_text and len(nl_text) > 20:  # Only check substantial Dutch text
        if sign.get('name_en') == nl_text:
            problems.append((sign_code, 'EN', 'Same as NL'))
        if sign.get('name_fr') == nl_text:
            problems.append((sign_code, 'FR', 'Same as NL'))
        if sign.get('name_ar') and 'Einde' in sign.get('name_ar', ''):
            problems.append((sign_code, 'AR', 'Contains Dutch words'))

if problems:
    print(f"\n❌ Found {len(problems)} translation problems:\n")
    for sign_code, lang, issue in problems:
        sign = data[sign_code]
        print(f"Sign {sign_code} - {lang}: {issue}")
        print(f"  NL: {sign.get('name_nl', '')[:70]}")
        print(f"  AR: {sign.get('name_ar', '')[:70]}")
        print(f"  EN: {sign.get('name_en', '')[:70]}")
        print(f"  FR: {sign.get('name_fr', '')[:70]}")
        print()
else:
    print("\n✅ All translations look good!")
