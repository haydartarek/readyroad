#!/usr/bin/env python3
"""
Extract Traffic Rules from PDF Files
استخراج القواعد المرورية من ملفات PDF
"""

import os
import re
import json
from pathlib import Path
import PyPDF2
from datetime import datetime

# Configuration
PDF_FOLDER = Path("data/theory_source")
OUTPUT_FILE = Path("data/traffic_rules_extracted.json")

def extract_text_from_pdf(pdf_path):
    """Extract all text from a PDF file."""
    text = ""
    try:
        with open(pdf_path, 'rb') as file:
            pdf_reader = PyPDF2.PdfReader(file)
            for page in pdf_reader.pages:
                text += page.extract_text() + "\n"
    except Exception as e:
        print(f"Error reading {pdf_path.name}: {e}")
    return text

def identify_sign_code(text):
    """Identify traffic sign codes in text (A1, B2, C3, etc.)."""
    # Match patterns like A1, B2, C3a, D10, etc.
    pattern = r'\b([A-Z]\d{1,2}[a-z]?)\b'
    matches = re.findall(pattern, text)
    return list(set(matches))  # Remove duplicates

def extract_rules_from_text(text, filename):
    """Extract traffic rules from PDF text."""
    rules = []
    
    # Split by common section headers
    sections = re.split(r'\n\s*\n+', text)
    
    for i, section in enumerate(sections):
        section = section.strip()
        if len(section) < 50:  # Skip very short sections
            continue
        
        # Look for rule patterns
        lines = section.split('\n')
        if len(lines) < 2:
            continue
        
        # First line often contains the title
        title = lines[0].strip()
        content = '\n'.join(lines[1:]).strip()
        
        # Identify related signs
        related_signs = identify_sign_code(section)
        
        # Determine category from filename or content
        category = None
        if 'voorrang' in filename.lower() or 'priority' in section.lower():
            category = 'PRIORITY'
        elif 'snelheid' in filename.lower() or 'speed' in section.lower():
            category = 'SPEED_LIMITS'
        elif 'parkeren' in filename.lower() or 'parking' in section.lower():
            category = 'PARKING'
        elif 'gevaar' in filename.lower() or 'danger' in section.lower():
            category = 'DANGER'
        elif 'verbod' in filename.lower() or 'prohibition' in section.lower():
            category = 'PROHIBITION'
        
        # Determine importance
        importance = 'MEDIUM'
        if any(word in section.lower() for word in ['verplicht', 'moet', 'required', 'mandatory']):
            importance = 'HIGH'
        elif any(word in section.lower() for word in ['tip', 'advies', 'recommendation']):
            importance = 'LOW'
        
        rule = {
            'rule_code': f"RULE_{i+1:03d}_{filename[:10]}",
            'title_nl': title[:200] if len(title) <= 200 else title[:197] + '...',
            'content_nl': content[:1000] if len(content) <= 1000 else content[:997] + '...',
            'category': category,
            'importance_level': importance,
            'related_signs': related_signs,
            'source_file': filename
        }
        
        rules.append(rule)
    
    return rules

def main():
    """Main execution function."""
    print("🚦 Starting Traffic Rules Extraction from PDFs...")
    print(f"📁 Reading from: {PDF_FOLDER}")
    
    if not PDF_FOLDER.exists():
        print(f"❌ Error: PDF folder not found: {PDF_FOLDER}")
        return
    
    all_rules = []
    pdf_files = list(PDF_FOLDER.glob("*.pdf"))
    
    print(f"📄 Found {len(pdf_files)} PDF files")
    
    for pdf_file in pdf_files:
        print(f"\n📖 Processing: {pdf_file.name}")
        
        # Extract text from PDF
        text = extract_text_from_pdf(pdf_file)
        
        if not text.strip():
            print(f"  ⚠️  No text extracted from {pdf_file.name}")
            continue
        
        # Extract rules
        rules = extract_rules_from_text(text, pdf_file.name)
        print(f"  ✅ Extracted {len(rules)} rules")
        
        all_rules.extend(rules)
    
    # Save to JSON
    output_data = {
        'extracted_at': datetime.now().isoformat(),
        'total_pdfs': len(pdf_files),
        'total_rules': len(all_rules),
        'rules': all_rules
    }
    
    OUTPUT_FILE.parent.mkdir(parents=True, exist_ok=True)
    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        json.dump(output_data, f, ensure_ascii=False, indent=2)
    
    print(f"\n✅ Extraction Complete!")
    print(f"📊 Total PDFs processed: {len(pdf_files)}")
    print(f"📊 Total rules extracted: {len(all_rules)}")
    print(f"💾 Saved to: {OUTPUT_FILE}")
    
    # Statistics by category
    categories = {}
    for rule in all_rules:
        cat = rule.get('category', 'UNCATEGORIZED')
        categories[cat] = categories.get(cat, 0) + 1
    
    print("\n📊 Rules by Category:")
    for cat, count in sorted(categories.items()):
        print(f"  {cat}: {count}")

if __name__ == "__main__":
    main()
