#!/usr/bin/env python3
"""
Generate Quiz Questions from Traffic Signs
توليد أسئلة الاختبار من العلامات المرورية
"""

import json
import random
from pathlib import Path
from datetime import datetime

# Configuration
SIGNS_FILE = Path(__file__).parent / "traffic_signs_complete.json"
OUTPUT_FILE = Path(__file__).parent / "quiz_questions_generated.json"

# Question templates
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
    'sign_action': {
        'ar': 'ماذا يجب عليك فعله عند رؤية العلامة {sign_code}؟',
        'en': 'What should you do when you see sign {sign_code}?',
        'nl': 'Wat moet je doen als je bord {sign_code} ziet?',
        'fr': 'Que devez-vous faire lorsque vous voyez le panneau {sign_code}?'
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

def load_signs():
    """Load traffic signs from JSON file."""
    if not SIGNS_FILE.exists():
        print(f"❌ Signs file not found: {SIGNS_FILE}")
        return []
    
    with open(SIGNS_FILE, 'r', encoding='utf-8') as f:
        data = json.load(f)
        # Convert dict to list if needed
        if isinstance(data, dict):
            return list(data.values())
        return data if isinstance(data, list) else []

def generate_identify_question(sign, all_signs):
    """Generate a 'What does this sign mean?' question."""
    # Get 3 wrong answers from same or different categories
    wrong_signs = [s for s in all_signs if s['sign_code'] != sign['sign_code']]
    wrong_answers = random.sample(wrong_signs, min(3, len(wrong_signs)))
    
    # Create options
    options = []
    
    # Correct answer
    options.append({
        'option_text_ar': sign.get('name_ar', ''),
        'option_text_en': sign.get('name_en', ''),
        'option_text_nl': sign.get('name_nl', ''),
        'option_text_fr': sign.get('name_fr', ''),
        'is_correct': True,
        'display_order': 0
    })
    
    # Wrong answers
    for i, wrong in enumerate(wrong_answers):
        options.append({
            'option_text_ar': wrong.get('name_ar', ''),
            'option_text_en': wrong.get('name_en', ''),
            'option_text_nl': wrong.get('name_nl', ''),
            'option_text_fr': wrong.get('name_fr', ''),
            'is_correct': False,
            'display_order': i + 1
        })
    
    # Randomize display order
    random.shuffle(options)
    for i, opt in enumerate(options):
        opt['display_order'] = i
    
    question = {
        'question_ar': QUESTION_TEMPLATES['identify_sign']['ar'].format(sign_code=sign['sign_code']),
        'question_en': QUESTION_TEMPLATES['identify_sign']['en'].format(sign_code=sign['sign_code']),
        'question_nl': QUESTION_TEMPLATES['identify_sign']['nl'].format(sign_code=sign['sign_code']),
        'question_fr': QUESTION_TEMPLATES['identify_sign']['fr'].format(sign_code=sign['sign_code']),
        'question_type': 'MULTIPLE_CHOICE',
        'difficulty_level': 'EASY',
        'traffic_sign_code': sign['sign_code'],
        'category': sign.get('category', ''),
        'explanation_ar': f"العلامة {sign['sign_code']} تعني: {sign.get('name_ar', '')}",
        'explanation_en': f"Sign {sign['sign_code']} means: {sign.get('name_en', '')}",
        'explanation_nl': f"Bord {sign['sign_code']} betekent: {sign.get('name_nl', '')}",
        'explanation_fr': f"Le panneau {sign['sign_code']} signifie: {sign.get('name_fr', '')}",
        'options': options[:4]  # Limit to 4 options
    }
    
    return question

def generate_category_question(sign, all_signs):
    """Generate a category identification question."""
    category_code = sign.get('category', '')[0] if sign.get('category') else 'A'
    
    # Get all unique categories
    all_categories = list(set([s.get('category', '')[0] for s in all_signs if s.get('category')]))
    wrong_categories = [c for c in all_categories if c != category_code]
    wrong_categories = random.sample(wrong_categories, min(3, len(wrong_categories)))
    
    options = [
        {
            'option_text_ar': CATEGORY_NAMES.get(category_code, {}).get('ar', ''),
            'option_text_en': CATEGORY_NAMES.get(category_code, {}).get('en', ''),
            'option_text_nl': CATEGORY_NAMES.get(category_code, {}).get('nl', ''),
            'option_text_fr': CATEGORY_NAMES.get(category_code, {}).get('fr', ''),
            'is_correct': True,
            'display_order': 0
        }
    ]
    
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
    
    question = {
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
    
    return question

def generate_true_false_question(sign):
    """Generate a True/False question."""
    # 50% chance correct statement, 50% wrong
    is_correct_statement = random.choice([True, False])
    
    if is_correct_statement:
        statement_ar = sign.get('description_ar', sign.get('name_ar', ''))
        statement_en = sign.get('description_en', sign.get('name_en', ''))
        statement_nl = sign.get('description_nl', sign.get('name_nl', ''))
        statement_fr = sign.get('description_fr', sign.get('name_fr', ''))
    else:
        # Create wrong statement (simplified - in real app would need better logic)
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
    
    question = {
        'question_ar': QUESTION_TEMPLATES['true_false']['ar'].format(
            sign_code=sign['sign_code'], 
            statement=statement_ar
        ),
        'question_en': QUESTION_TEMPLATES['true_false']['en'].format(
            sign_code=sign['sign_code'], 
            statement=statement_en
        ),
        'question_nl': QUESTION_TEMPLATES['true_false']['nl'].format(
            sign_code=sign['sign_code'], 
            statement=statement_nl
        ),
        'question_fr': QUESTION_TEMPLATES['true_false']['fr'].format(
            sign_code=sign['sign_code'], 
            statement=statement_fr
        ),
        'question_type': 'TRUE_FALSE',
        'difficulty_level': 'EASY',
        'traffic_sign_code': sign['sign_code'],
        'category': sign.get('category', ''),
        'explanation_ar': sign.get('description_ar', ''),
        'explanation_en': sign.get('description_en', ''),
        'explanation_nl': sign.get('description_nl', ''),
        'explanation_fr': sign.get('description_fr', ''),
        'options': options
    }
    
    return question

def main():
    """Main execution function."""
    print("🎓 Starting Quiz Question Generation...")
    
    signs = load_signs()
    if not signs:
        print("❌ No signs loaded")
        return
    
    print(f"📊 Loaded {len(signs)} traffic signs")
    
    questions = []
    
    # Generate questions for each sign
    for sign in signs:
        # Generate 2-3 questions per sign
        question_types = random.sample([
            'identify',
            'category',
            'true_false'
        ], k=random.randint(2, 3))
        
        for q_type in question_types:
            if q_type == 'identify':
                q = generate_identify_question(sign, signs)
            elif q_type == 'category':
                q = generate_category_question(sign, signs)
            else:  # true_false
                q = generate_true_false_question(sign)
            
            questions.append(q)
    
    # Save to JSON
    output_data = {
        'generated_at': datetime.now().isoformat(),
        'total_questions': len(questions),
        'questions': questions
    }
    
    OUTPUT_FILE.parent.mkdir(parents=True, exist_ok=True)
    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        json.dump(output_data, f, ensure_ascii=False, indent=2)
    
    print(f"\n✅ Generation Complete!")
    print(f"📊 Total questions generated: {len(questions)}")
    print(f"💾 Saved to: {OUTPUT_FILE}")
    
    # Statistics
    types = {}
    difficulties = {}
    for q in questions:
        q_type = q['question_type']
        types[q_type] = types.get(q_type, 0) + 1
        
        diff = q['difficulty_level']
        difficulties[diff] = difficulties.get(diff, 0) + 1
    
    print("\n📊 Questions by Type:")
    for t, count in types.items():
        print(f"  {t}: {count}")
    
    print("\n📊 Questions by Difficulty:")
    for d, count in difficulties.items():
        print(f"  {d}: {count}")

if __name__ == "__main__":
    main()
