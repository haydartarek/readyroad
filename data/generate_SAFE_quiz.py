#!/usr/bin/env python3
"""
Generate Quiz Questions ONLY from signs with complete translations
"""

import json
import random
from pathlib import Path
from datetime import datetime

# Configuration
SIGNS_FILE = Path(__file__).parent / "traffic_signs_complete.json"
OUTPUT_FILE = Path(__file__).parent / "quiz_questions_SAFE.json"

# Question templates (same as before)
QUESTION_TEMPLATES = {
    'identify_sign': {
        'ar': 'ما هي العلامة المرورية {sign_code}؟',
        'en': 'What does the traffic sign {sign_code} mean?',
        'nl': 'Wat betekent verkeersbord {sign_code}?',
        'fr': 'Que signifie le panneau de signalisation {sign_code}?'
    },
    'sign_category': {
        'ar': 'إلى أي فئة تنتمي العلامة {sign_code}؟',
        'en': 'Which category does sign {sign_code} belong to?',
        'nl': 'Tot welke categorie behoort bord {sign_code}?',
        'fr': 'À quelle catégorie appartient le panneau {sign_code}?'
    },
    'true_false': {
        'ar': 'العلامة {sign_code} تعني: {statement}. صحيح أم خطأ؟',
        'en': 'Sign {sign_code} means: {statement}. True or False?',
        'nl': 'Bord {sign_code} betekent: {statement}. Waar of Onwaar?',
        'fr': 'Le panneau {sign_code} signifie: {statement}. Vrai ou Faux?'
    }
}

CATEGORY_NAMES = {
    'A': {'ar': 'علامات الخطر', 'en': 'Danger Signs', 'nl': 'Gevaarborden', 'fr': 'Panneaux de danger'},
    'B': {'ar': 'علامات الأولوية', 'en': 'Priority Signs', 'nl': 'Voorrangsborden', 'fr': 'Panneaux de priorité'},
    'C': {'ar': 'علامات المنع', 'en': 'Prohibition Signs', 'nl': 'Verbodsborden', 'fr': 'Panneaux d\'interdiction'},
    'D': {'ar': 'علامات الإلزام', 'en': 'Mandatory Signs', 'nl': 'Gebodsborden', 'fr': 'Panneaux d\'obligation'},
    'E': {'ar': 'علامات الوقوف', 'en': 'Parking Signs', 'nl': 'Parkeerverbod', 'fr': 'Panneaux de stationnement'},
    'F': {'ar': 'علامات إرشادية', 'en': 'Information Signs', 'nl': 'Informatieborden', 'fr': 'Panneaux d\'indication'},
    'M': {'ar': 'لوحات الدراجات', 'en': 'Bicycle Signs', 'nl': 'Fietsborden', 'fr': 'Panneaux vélos'}
}

def is_translation_valid(sign):
    """Check if sign has valid translations (not Dutch text in all fields)."""
    nl_text = sign.get('name_nl', '').strip()
    ar_text = sign.get('name_ar', '').strip()
    en_text = sign.get('name_en', '').strip()
    fr_text = sign.get('name_fr', '').strip()
    
    # Must have all fields
    if not all([nl_text, ar_text, en_text, fr_text]):
        return False
    
    # Check if EN/FR/AR are same as NL (indicatesבר translation)
    if en_text == nl_text or fr_text == nl_text:
        return False
        
    # Check if AR contains Dutch words
    dutch_indicators = ['Einde', 'Verplicht', 'Voor', 'Uitgezonderd', 'Begin']
    if any(word in ar_text for word in dutch_indicators):
        return False
    
    return True

def load_valid_signs():
    """Load only signs with valid translations."""
    with open(SIGNS_FILE, 'r', encoding='utf-8') as f:
        data = json.load(f)
        if isinstance(data, dict):
            signs = list(data.values())
        else:
            signs = data
    
    valid_signs = [s for s in signs if is_translation_valid(s)]
    print(f"✅ Loaded {len(valid_signs)} signs with valid translations (out of {len(signs)} total)")
    return valid_signs

def generate_identify_question(sign, all_signs):
    """Generate identification question."""
    wrong_signs = [s for s in all_signs if s['sign_code'] != sign['sign_code']]
    wrong_answers = random.sample(wrong_signs, min(3, len(wrong_signs)))
    
    options = [{
        'option_text_ar': sign['name_ar'],
        'option_text_en': sign['name_en'],
        'option_text_nl': sign['name_nl'],
        'option_text_fr': sign['name_fr'],
        'is_correct': True,
        'display_order': 0
    }]
    
    for i, wrong in enumerate(wrong_answers):
        options.append({
            'option_text_ar': wrong['name_ar'],
            'option_text_en': wrong['name_en'],
            'option_text_nl': wrong['name_nl'],
            'option_text_fr': wrong['name_fr'],
            'is_correct': False,
            'display_order': i + 1
        })
    
    random.shuffle(options)
    for i, opt in enumerate(options):
        opt['display_order'] = i
    
    return {
        'question_ar': QUESTION_TEMPLATES['identify_sign']['ar'].format(sign_code=sign['sign_code']),
        'question_en': QUESTION_TEMPLATES['identify_sign']['en'].format(sign_code=sign['sign_code']),
        'question_nl': QUESTION_TEMPLATES['identify_sign']['nl'].format(sign_code=sign['sign_code']),
        'question_fr': QUESTION_TEMPLATES['identify_sign']['fr'].format(sign_code=sign['sign_code']),
        'question_type': 'MULTIPLE_CHOICE',
        'difficulty_level': 'EASY',
        'traffic_sign_code': sign['sign_code'],
        'category': sign.get('category', ''),
        'explanation_ar': f"العلامة {sign['sign_code']} تعني: {sign['name_ar']}",
        'explanation_en': f"Sign {sign['sign_code']} means: {sign['name_en']}",
        'explanation_nl': f"Bord {sign['sign_code']} betekent: {sign['name_nl']}",
        'explanation_fr': f"Le panneau {sign['sign_code']} signifie: {sign['name_fr']}",
        'options': options[:4]
    }

def generate_category_question(sign, all_signs):
    """Generate category question."""
    category_code = sign.get('category', '')[0] if sign.get('category') else 'A'
    all_categories = list(set([s.get('category', '')[0] for s in all_signs if s.get('category')]))
    wrong_categories = [c for c in all_categories if c != category_code]
    wrong_categories = random.sample(wrong_categories, min(3, len(wrong_categories)))
    
    options = [{
        'option_text_ar': CATEGORY_NAMES.get(category_code, {}).get('ar', ''),
        'option_text_en': CATEGORY_NAMES.get(category_code, {}).get('en', ''),
        'option_text_nl': CATEGORY_NAMES.get(category_code, {}).get('nl', ''),
        'option_text_fr': CATEGORY_NAMES.get(category_code, {}).get('fr', ''),
        'is_correct': True,
        'display_order': 0
    }]
    
    for i, wrong_cat in enumerate(wrong_categories):
        options.append({
            'option_text_ar': CATEGORY_NAMES.get(wrong_cat, {}).get('ar', ''),
            'option_text_en': CATEGORY_NAMES.get(wrong_cat, {}).get('en', ''),
            'option_text_nl': CATEGORY_NAMES.get(wrong_cat, {}).get('nl', ''),
            'option_text_fr': CATEGORY_NAMES.get(wrong_cat, {}).get('fr', ''),
            'is_correct': False,
            'display_order': i + 1
        })
    
    random.shuffle(options)
    for i, opt in enumerate(options):
        opt['display_order'] = i
    
    return {
        'question_ar': QUESTION_TEMPLATES['sign_category']['ar'].format(sign_code=sign['sign_code']),
        'question_en': QUESTION_TEMPLATES['sign_category']['en'].format(sign_code=sign['sign_code']),
        'question_nl': QUESTION_TEMPLATES['sign_category']['nl'].format(sign_code=sign['sign_code']),
        'question_fr': QUESTION_TEMPLATES['sign_category']['fr'].format(sign_code=sign['sign_code']),
        'question_type': 'MULTIPLE_CHOICE',
        'difficulty_level': 'MEDIUM',
        'traffic_sign_code': sign['sign_code'],
        'category': sign.get('category', ''),
        'explanation_ar': f"العلامة {sign['sign_code']} تنتمي إلى فئة {CATEGORY_NAMES.get(category_code, {}).get('ar', '')}",
        'explanation_en': f"Sign {sign['sign_code']} belongs to {CATEGORY_NAMES.get(category_code, {}).get('en', '')}",
        'explanation_nl': f"Bord {sign['sign_code']} behoort tot {CATEGORY_NAMES.get(category_code, {}).get('nl', '')}",
        'explanation_fr': f"Le panneau {sign['sign_code']} appartient à {CATEGORY_NAMES.get(category_code, {}).get('fr', '')}",
        'options': options[:4]
    }

def generate_true_false_question(sign):
    """Generate True/False question."""
    is_correct_statement = random.choice([True, False])
    
    if is_correct_statement:
        statement_ar = sign.get('description_ar', sign.get('name_ar', ''))
        statement_en = sign.get('description_en', sign.get('name_en', ''))
        statement_nl = sign.get('description_nl', sign.get('name_nl', ''))
        statement_fr = sign.get('description_fr', sign.get('name_fr', ''))
    else:
        statement_ar = "هذه العلامة اختيارية"
        statement_en = "This sign is optional"
        statement_nl = "Dit bord is optioneel"
        statement_fr = "Ce panneau est optionnel"
    
    options = [
        {
            'option_text_ar': 'صحيح',
            'option_text_en': 'True',
            'option_text_nl': 'Waar',
            'option_text_fr': 'Vrai',
            'is_correct': is_correct_statement,
            'display_order': 0
        },
        {
            'option_text_ar': 'خطأ',
            'option_text_en': 'False',
            'option_text_nl': 'Onwaar',
            'option_text_fr': 'Faux',
            'is_correct': not is_correct_statement,
            'display_order': 1
        }
    ]
    
    return {
        'question_ar': QUESTION_TEMPLATES['true_false']['ar'].format(sign_code=sign['sign_code'], statement=statement_ar),
        'question_en': QUESTION_TEMPLATES['true_false']['en'].format(sign_code=sign['sign_code'], statement=statement_en),
        'question_nl': QUESTION_TEMPLATES['true_false']['nl'].format(sign_code=sign['sign_code'], statement=statement_nl),
        'question_fr': QUESTION_TEMPLATES['true_false']['fr'].format(sign_code=sign['sign_code'], statement=statement_fr),
        'question_type': 'TRUE_FALSE',
        'difficulty_level': 'EASY',
        'traffic_sign_code': sign['sign_code'],
        'category': sign.get('category', ''),
        'explanation_ar': sign.get('name_ar', ''),
        'explanation_en': sign.get('name_en', ''),
        'explanation_nl': sign.get('name_nl', ''),
        'explanation_fr': sign.get('name_fr', ''),
        'options': options
    }

def main():
    print("🚀 Starting Safe Quiz Generation...")
    print("=" * 60)
    
    valid_signs = load_valid_signs()
    
    if len(valid_signs) < 10:
        print("❌ Not enough valid signs to generate questions!")
        return
    
    questions = []
    
    # Generate questions for each sign
    for sign in valid_signs:
        # 1. Identify question (60% of signs)
        if random.random() < 0.6:
            questions.append(generate_identify_question(sign, valid_signs))
        
        # 2. Category question (30% of signs)
        if random.random() < 0.3:
            questions.append(generate_category_question(sign, valid_signs))
        
        # 3. True/False question (40% of signs)
        if random.random() < 0.4:
            questions.append(generate_true_false_question(sign))
    
    # Save to JSON
    output_data = {
        'generated_at': datetime.now().isoformat(),
        'total_questions': len(questions),
        'from_valid_signs': len(valid_signs),
        'questions': questions
    }
    
    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        json.dump(output_data, f, ensure_ascii=False, indent=2)
    
    print(f"\n✅ Generated {len(questions)} questions from {len(valid_signs)} valid signs")
    print(f"📄 Saved to: {OUTPUT_FILE}")
    
    # Statistics
    mc_count = sum(1 for q in questions if q['question_type'] == 'MULTIPLE_CHOICE')
    tf_count = sum(1 for q in questions if q['question_type'] == 'TRUE_FALSE')
    easy_count = sum(1 for q in questions if q['difficulty_level'] == 'EASY')
    medium_count = sum(1 for q in questions if q['difficulty_level'] == 'MEDIUM')
    
    print(f"\n📊 Question Types:")
    print(f"   - MULTIPLE_CHOICE: {mc_count}")
    print(f"   - TRUE_FALSE: {tf_count}")
    print(f"\n📊 Difficulty:")
    print(f"   - EASY: {easy_count}")
    print(f"   - MEDIUM: {medium_count}")

if __name__ == '__main__':
    main()
