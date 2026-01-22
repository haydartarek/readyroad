#!/usr/bin/env python3
"""Check for empty options in quiz questions"""

import json
from pathlib import Path

# Load quiz questions
with open(Path(__file__).parent / 'quiz_questions_generated.json', 'r', encoding='utf-8') as f:
    questions = json.load(f)

print(f"Total questions: {len(questions)}\n")

bad_options = []
for i, q in enumerate(questions, 1):
    for j, opt in enumerate(q.get('options', []), 1):
        missing = []
        if not opt.get('option_text_ar') or opt.get('option_text_ar').strip() == '':
            missing.append('ar')
        if not opt.get('option_text_en') or opt.get('option_text_en').strip() == '':
            missing.append('en')
        if not opt.get('option_text_nl') or opt.get('option_text_nl').strip() == '':
            missing.append('nl')
        if not opt.get('option_text_fr') or opt.get('option_text_fr').strip() == '':
            missing.append('fr')
        
        if missing:
            bad_options.append({
                'question_num': i,
                'option_num': j,
                'question': q['question_en'][:80],
                'missing_langs': missing,
                'option': opt
            })

print(f"❌ Bad options found: {len(bad_options)}\n")

if bad_options:
    print("First 5 problematic options:\n")
    for item in bad_options[:5]:
        print(f"Question #{item['question_num']}: {item['question']}")
        print(f"  Option #{item['option_num']}: Missing {item['missing_langs']}")
        print(f"  AR: '{item['option']['option_text_ar']}'")
        print(f"  EN: '{item['option']['option_text_en']}'")
        print(f"  NL: '{item['option']['option_text_nl']}'")
        print(f"  FR: '{item['option']['option_text_fr']}'")
        print()
else:
    print("✅ All options have complete translations!")
