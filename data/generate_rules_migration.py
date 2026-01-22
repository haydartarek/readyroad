#!/usr/bin/env python3
"""
Generate SQL migration for Traffic Rules
توليد SQL migration لقواعد المرور
"""

import json
from pathlib import Path
from datetime import datetime

INPUT_FILE = Path(__file__).parent / "traffic_rules_extracted.json"
OUTPUT_FILE = Path(__file__).parent.parent / "src" / "main" / "resources" / "db" / "migration" / "V10__Add_Traffic_Rules.sql"

def escape_sql(text):
    """Escape text for SQL insertion."""
    if text is None:
        return ""
    return str(text).replace("'", "''").replace("\\", "\\\\")

def main():
    print("🔄 Generating SQL Migration for Traffic Rules...")
    
    if not INPUT_FILE.exists():
        print(f"❌ Input file not found: {INPUT_FILE}")
        return
    
    with open(INPUT_FILE, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    rules = data.get('rules', [])
    print(f"📊 Loaded {len(rules)} rules")
    
    # Organize rules by topic
    rules_by_topic = {}
    for rule in rules:
        topic = rule.get('topic', 'General')
        if topic not in rules_by_topic:
            rules_by_topic[topic] = []
        rules_by_topic[topic].append(rule)
    
    # Start building SQL
    sql_lines = [
        "-- V10__Add_Traffic_Rules.sql",
        "-- إضافة قواعد المرور",
        "-- Add Traffic Rules from PDF files",
        f"-- Generated: {datetime.now().isoformat()}",
        f"-- Total Rules: {len(rules)}",
        "",
        "-- ========================================",
        "-- إدراج قواعد المرور",
        "-- Insert Traffic Rules",
        "-- ========================================",
        ""
    ]
    
    rule_id = 1
    
    for topic, topic_rules in sorted(rules_by_topic.items()):
        sql_lines.append(f"-- Topic: {topic} ({len(topic_rules)} rules)")
        sql_lines.append("")
        
        for rule in topic_rules:
            rule_code = f"RULE_{rule_id:04d}"
            title_nl = escape_sql(rule.get('text_nl', ''))[:200]
            title_en = 'Traffic rule'  # Placeholder for now
            title_ar = 'قاعدة مرورية'  # Placeholder
            title_fr = 'Règle de circulation'  # Placeholder
            
            content_nl = escape_sql(rule.get('text_nl', ''))
            content_en = ''  # Will need translation
            content_ar = ''  # Will need translation
            content_fr = ''  # Will need translation
            
            category = rule.get('category', 'GENERAL')
            importance = rule.get('importance', 'MEDIUM')
            source = escape_sql(rule.get('source', ''))
            
            sql_lines.append(f"INSERT INTO traffic_rules (")
            sql_lines.append(f"  id, rule_code,")
            sql_lines.append(f"  title_ar, title_en, title_nl, title_fr,")
            sql_lines.append(f"  content_ar, content_en, content_nl, content_fr,")
            sql_lines.append(f"  category, importance_level, applies_to,")
            sql_lines.append(f"  is_active, created_at, updated_at")
            sql_lines.append(f") VALUES (")
            sql_lines.append(f"  {rule_id}, '{rule_code}',")
            sql_lines.append(f"  '{title_ar}', '{title_en}', '{title_nl}', '{title_fr}',")
            sql_lines.append(f"  '{content_ar}', '{content_en}', '{content_nl}', '{content_fr}',")
            sql_lines.append(f"  '{category}', '{importance}', 'ALL',")
            sql_lines.append(f"  TRUE, NOW(), NOW()")
            sql_lines.append(f");")
            sql_lines.append("")
            
            rule_id += 1
    
    # Write SQL file
    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        f.write('\n'.join(sql_lines))
    
    file_size = OUTPUT_FILE.stat().st_size / 1024
    print(f"\n✅ SQL Migration Generated!")
    print(f"📊 Rules: {len(rules)}")
    print(f"📊 Topics: {len(rules_by_topic)}")
    print(f"💾 Saved to: {OUTPUT_FILE}")
    print(f"📏 File size: {file_size:.2f} KB")
    
    # Show topic distribution
    print("\n📊 Rules by Topic:")
    for topic, topic_rules in sorted(rules_by_topic.items(), key=lambda x: len(x[1]), reverse=True)[:10]:
        print(f"  {topic}: {len(topic_rules)}")

if __name__ == '__main__':
    main()
