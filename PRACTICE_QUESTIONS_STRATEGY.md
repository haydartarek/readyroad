# Strategy for Completing Practice Questions

## Overview
We need to add 145 practice questions for lessons 3-31. Each question must have:
- Question text in 4 languages
- 4 answer options in 4 languages  
- Correct answer indicator
- Explanation in 4 languages

## Time Estimation
- **Per Question**: ~5-7 minutes
- **Total Time**: 12-17 hours
- **Recommended Approach**: Work in batches of 10-15 questions

## Batch Strategy

### Batch 1: Infrastructure & Roads (Lessons 3-6) - 20 questions
- Lesson 3: Cyclists (5 questions)
- Lesson 4: Highway (5 questions)
- Lesson 5: Express Road (5 questions)
- Lesson 6: Special Areas (5 questions)

### Batch 2: Road Users (Lessons 7-8) - 10 questions
- Lesson 7: Pedestrians (5 questions)
- Lesson 8: Drivers (5 questions)

### Batch 3: Technical & Load (Lessons 9-10, 31) - 15 questions
- Lesson 9: Maximum Weight (5 questions)
- Lesson 10: Vehicle Load (5 questions)
- Lesson 31: Car Technology (5 questions)

### Batch 4: Safety & Speed Part 1 (Lessons 11-15) - 25 questions
- Lesson 11: Lights & Horn (5 questions)
- Lesson 12: Maximum Speed (5 questions)
- Lesson 13: Braking Distance (5 questions)
- Lesson 14: Crossing Vehicles (5 questions)
- Lesson 15: Overtaking (5 questions)

### Batch 5: Priority (Lessons 16-20) - 25 questions
- Lesson 16: Priority at Intersections (5 questions)
- Lesson 17: Roundabouts (5 questions)
- Lesson 18: Public Transport Priority (5 questions)
- Lesson 19: Emergency Vehicles (5 questions)
- Lesson 20: Railroad Crossings (5 questions)

### Batch 6: Traffic Signs (Lesson 21) - 5 questions
- Lesson 21: Understanding Traffic Signs (5 questions)

### Batch 7: Public Transport (Lesson 22) - 5 questions
- Lesson 22: Tram & Bus (5 questions)

### Batch 8: Prohibition Signs (Lessons 23-24) - 10 questions
- Lesson 23: Entry Restrictions (5 questions)
- Lesson 24: Speed & Action Restrictions (5 questions)

### Batch 9: Mandatory Signs (Lessons 25-26) - 10 questions
- Lesson 25: Direction Obligations (5 questions)
- Lesson 26: Lane & Path Obligations (5 questions)

### Batch 10: Parking & Safety (Lessons 27-30) - 20 questions
- Lesson 27: Parking & Stopping Rules (5 questions)
- Lesson 28: Alcohol & Drugs (5 questions)
- Lesson 29: Accidents (5 questions)
- Lesson 30: Eco Driving (5 questions)

## Question Types to Include

### Type 1: Definition Questions (30%)
- "What is...?"
- "What does... mean?"
- "Define..."

**Example**:
```sql
'ما هو تعريف الطريق السريع؟'
'What is the definition of a highway?'
```

### Type 2: Rule Questions (40%)
- "When should you...?"
- "What must you do when...?"
- "What is the rule for...?"

**Example**:
```sql
'متى يجب استخدام الحارة اليسرى؟'
'When should the left lane be used?'
```

### Type 3: Scenario Questions (20%)
- "What should you do if...?"
- "How should you react when...?"
- "In situation X, what is correct?"

**Example**:
```sql
'ماذا يجب أن تفعل عند رؤية إشارة توقف؟'
'What should you do when seeing a stop sign?'
```

### Type 4: Priority Questions (10%)
- "Who has priority when...?"
- "Which vehicle should go first?"
- "Who must give way?"

**Example**:
```sql
'من له الأولوية في دوار؟'
'Who has priority at a roundabout?'
```

## Quality Checklist

### For Each Question:
- [ ] Question is clear and unambiguous
- [ ] All 4 options are plausible
- [ ] Only one option is clearly correct
- [ ] Options are similar in length
- [ ] Explanation provides learning value
- [ ] All translations are accurate
- [ ] Matches lesson content
- [ ] Appropriate difficulty level

### Common Pitfalls to Avoid:
1. ❌ Options that are too obviously wrong
2. ❌ Questions with multiple correct answers
3. ❌ Translations that don't match exactly
4. ❌ Questions that are too complex
5. ❌ Missing explanations

### Good Practices:
1. ✅ Use realistic scenarios
2. ✅ Include specific numbers (speeds, distances)
3. ✅ Reference specific signs or rules
4. ✅ Make distractors believable
5. ✅ Provide educational explanations

## SQL Template

```sql
INSERT INTO practice_questions (lesson_id, question_ar, question_en, question_nl, question_fr, 
                                option_a_ar, option_a_en, option_a_nl, option_a_fr,
                                option_b_ar, option_b_en, option_b_nl, option_b_fr,
                                option_c_ar, option_c_en, option_c_nl, option_c_fr,
                                option_d_ar, option_d_en, option_d_nl, option_d_fr,
                                correct_answer, explanation_ar, explanation_en, explanation_nl, explanation_fr,
                                display_order, is_active, created_at, updated_at) 
VALUES
    (LESSON_ID, 
     'ARABIC_QUESTION', 'ENGLISH_QUESTION', 'DUTCH_QUESTION', 'FRENCH_QUESTION',
     'ARABIC_A', 'ENGLISH_A', 'DUTCH_A', 'FRENCH_A',
     'ARABIC_B', 'ENGLISH_B', 'DUTCH_B', 'FRENCH_B',
     'ARABIC_C', 'ENGLISH_C', 'DUTCH_C', 'FRENCH_C',
     'ARABIC_D', 'ENGLISH_D', 'DUTCH_D', 'FRENCH_D',
     'A/B/C/D',
     'ARABIC_EXPLANATION', 'ENGLISH_EXPLANATION', 'DUTCH_EXPLANATION', 'FRENCH_EXPLANATION',
     ORDER, TRUE, NOW(), NOW());
```

## Workflow Recommendation

### Session 1 (2 hours): Batches 1-2
- Complete Infrastructure & Roads
- Complete Road Users
- Total: 30 questions

### Session 2 (2 hours): Batches 3-4
- Complete Technical & Load
- Start Safety & Speed Part 1
- Total: 40 questions

### Session 3 (2 hours): Batches 5-6
- Complete Priority
- Complete Traffic Signs
- Total: 30 questions

### Session 4 (2 hours): Batches 7-10
- Complete remaining lessons
- Total: 45 questions

### Session 5 (1 hour): Review & Test
- Review all questions
- Test migration
- Fix any issues

## Tracking Progress

### Completed:
- ✅ Lesson 1: 5/5
- ✅ Lesson 2: 5/5

### In Progress:
- ⏳ Total: 10/155 (6.5%)

### Remaining:
- 🎯 145 questions to go

## Quick Reference: Lesson IDs

| Lesson | ID | Category | Questions |
|--------|----|---------| ----------|
| Lesson 1 | 1 | F | 5/5 ✅ |
| Lesson 2 | 2 | F | 5/5 ✅ |
| Lesson 3 | 3 | G | 0/5 ⏳ |
| Lesson 4 | 4 | F | 0/5 ⏳ |
| Lesson 5 | 5 | F | 0/5 ⏳ |
| Lesson 6 | 6 | F | 0/5 ⏳ |
| Lesson 7 | 7 | G | 0/5 ⏳ |
| Lesson 8 | 8 | G | 0/5 ⏳ |
| Lesson 9 | 9 | M | 0/5 ⏳ |
| Lesson 10 | 10 | M | 0/5 ⏳ |
| ... | ... | ... | ... |

---

**Note**: Focus on quality over speed. Each question is an educational opportunity!
