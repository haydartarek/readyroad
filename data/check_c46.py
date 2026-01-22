#!/usr/bin/env python3
"""Check C46 sign data"""

import json
from pathlib import Path

with open(Path(__file__).parent / 'traffic_signs_complete.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

# Check if it's dict or list
if isinstance(data, dict):
    sign = data.get('C46', {})
else:
    sign = next((s for s in data if s.get('sign_code') == 'C46'), {})

print("C46 Sign Data:")
print(f"  sign_code: {sign.get('sign_code', 'MISSING')}")
print(f"  name_ar: {sign.get('name_ar', 'MISSING')}")
print(f"  name_en: {sign.get('name_en', 'MISSING')}")
print(f"  name_nl: {sign.get('name_nl', 'MISSING')}")
print(f"  name_fr: {sign.get('name_fr', 'MISSING')}")
