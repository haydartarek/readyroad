#!/usr/bin/env python3
"""
Generate SQL Migration for Quiz Questions
توليد SQL migration لأسئلة الاختبار
"""

import json
from pathlib import Path
from datetime import datetime

# Configuration
INPUT_FILE = Path(__file__).parent / "quiz_questions_SAFE.json"
OUTPUT_FILE = Path(__file__).parent.parent / "src" / "main" / "resources" / "db" / "migration" / "V8__Add_Quiz_Questions_Safe.sql"

def escape_sql(text):
    """Escape text for SQL insertion."""
    if text is None:
        return "NULL"
    return "'" + str(text).replace("'", "''").replace("\\", "\\\\") + "'"

def main():
    """Main execution function."""
    print("🔄 Generating SQL Migration for Quiz Questions...")
    
    if not INPUT_FILE.exists():
        print(f"❌ Input file not found: {INPUT_FILE}")
        return
    
    with open(INPUT_FILE, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    questions = data.get('questions', [])
    print(f"📊 Loaded {len(questions)} questions")
    
    # Start building SQL
    sql_lines = [
        "-- V8__Add_Quiz_Questions.sql",
        "-- إضافة أسئلة الاختبار",
        "-- Quiz Questions Data",
        f"-- Generated: {datetime.now().isoformat()}",
        f"-- Total Questions: {len(questions)}",
        "",
        "-- ========================================",
        "-- إدراج أسئلة الاختبار",
        "-- Insert Quiz Questions",
        "-- ========================================",
        ""
    ]
    
    question_id = 1
    option_id = 1
    
    for question in questions:
        # Get traffic sign ID from sign_code
        sign_code = question.get('traffic_sign_code')
        
        # Insert question
        sql = f"""INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  {question_id},
  {escape_sql(question.get('question_ar'))},
  {escape_sql(question.get('question_en'))},
  {escape_sql(question.get('question_nl'))},
  {escape_sql(question.get('question_fr'))},
  {escape_sql(question.get('question_type'))},
  {escape_sql(question.get('difficulty_level'))},
  (SELECT id FROM categories WHERE code = {escape_sql(question.get('category', '')[:1]) if question.get('category') else 'NULL'}),
  (SELECT id FROM traffic_signs WHERE sign_code = {escape_sql(sign_code)} LIMIT 1),
  {escape_sql(question.get('explanation_ar'))},
  {escape_sql(question.get('explanation_en'))},
  {escape_sql(question.get('explanation_nl'))},
  {escape_sql(question.get('explanation_fr'))},
  TRUE,
  NOW(),
  NOW()
);
"""
        sql_lines.append(sql)
        
        # Insert options
        for option in question.get('options', []):
            # Skip options with missing required fields
            if not all([
                option.get('option_text_ar'),
                option.get('option_text_en'),
                option.get('option_text_nl'),
                option.get('option_text_fr')
            ]):
                continue
            
            sql = f"""INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  {option_id},
  {question_id},
  {escape_sql(option.get('option_text_ar'))},
  {escape_sql(option.get('option_text_en'))},
  {escape_sql(option.get('option_text_nl'))},
  {escape_sql(option.get('option_text_fr'))},
  {str(option.get('is_correct', False)).upper()},
  {option.get('display_order', 0)},
  NOW()
);
"""
            sql_lines.append(sql)
            option_id += 1
        
        sql_lines.append("")  # Empty line between questions
        question_id += 1
    
    # Write to file
    OUTPUT_FILE.parent.mkdir(parents=True, exist_ok=True)
    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        f.write('\n'.join(sql_lines))
    
    print(f"\n✅ SQL Migration Generated!")
    print(f"📊 Questions: {question_id - 1}")
    print(f"📊 Options: {option_id - 1}")
    print(f"💾 Saved to: {OUTPUT_FILE}")
    print(f"📏 File size: {OUTPUT_FILE.stat().st_size / 1024:.2f} KB")

if __name__ == "__main__":
    main()
