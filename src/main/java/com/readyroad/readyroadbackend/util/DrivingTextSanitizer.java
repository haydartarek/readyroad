package com.readyroad.readyroadbackend.util;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Centralized terminology and phrasing normalizer for driving-learning copy.
 *
 * <p>
 * This keeps repeated prompts and key Arabic terminology consistent across
 * imported sign-question content, quiz data, lessons, and API responses.
 * </p>
 */
public final class DrivingTextSanitizer {

        private static final String LEGACY_SERIES_WORD = "الس" + "لسلة";
        private static final String LATIN_A = "A";
        private static final String LATIN_B = "B";
        private static final String LATIN_D = "D";

        private static final List<Replacement> ARABIC_REPLACEMENTS = List.of(
                        new Replacement("الالعلامة المروريةية", "العلامة المرورية"),
                        new Replacement("العلامة المروريةية", "العلامة المرورية"),
                        new Replacement("ما معنى علامة المرور هذه؟", "ما معنى هذه العلامة المرورية؟"),
                        new Replacement("ماذا تعني علامة المرور هذه؟", "ما معنى هذه العلامة المرورية؟"),
                        new Replacement("ما الخطر الذي تعلنه هذه العلامة المرورية؟",
                                        "ما هو الخطر الذي تشير إليه هذه العلامة المرورية؟"),
                        new Replacement("ما الخطر الذي تشير إليه هذه العلامة؟",
                                        "ما هو الخطر الذي تشير إليه هذه العلامة المرورية؟"),
                        new Replacement("إلى أي فئة من العلامات المرورية تنتمي هذه العلامة المرورية؟",
                                        "إلى أي فئة تُصنَّف هذه العلامة المرورية؟"),
                        new Replacement("إلى أي فئة من العلامات المرورية تنتمي هذه العلامة؟",
                                        "إلى أي فئة تُصنَّف هذه العلامة المرورية؟"),
                        new Replacement("نظام " + LEGACY_SERIES_WORD, "نظام التعاقب"),
                        new Replacement("منعطفات مزدوجة خطيرة أو أكثر", "منعطف مزدوج خطير أو أكثر"),
                        new Replacement("تعرجات خطيرة متعددة، الأول إلى اليسار", "منعطف مزدوج خطير، الأول إلى اليسار"),
                        new Replacement("تحذر هذه العلامة المرورية من " + LEGACY_SERIES_WORD
                                        + " منعطفات خطيرة متتالية يكون أولها إلى اليسار. اضبط سرعتك لكامل "
                                        + LEGACY_SERIES_WORD + ".",
                                        "تحذر هذه العلامة المرورية من منعطفات خطيرة متتالية يكون أولها إلى اليسار. اضبط سرعتك على امتداد جميع المنعطفات."),
                        new Replacement("تحذر هذه العلامة المرورية من " + LEGACY_SERIES_WORD
                                        + " منعطفات خطيرة متتالية يكون أولها إلى اليمين. اضبط سرعتك لكامل "
                                        + LEGACY_SERIES_WORD + ".",
                                        "تحذر هذه العلامة المرورية من منعطفات خطيرة متتالية يكون أولها إلى اليمين. اضبط سرعتك على امتداد جميع المنعطفات."),
                        new Replacement(LEGACY_SERIES_WORD + " منعطفات خطيرة متتالية يكون أولها إلى اليسار",
                                        "منعطفات خطيرة متتالية يكون أولها إلى اليسار"),
                        new Replacement(LEGACY_SERIES_WORD + " منعطفات خطيرة متتالية يكون أولها إلى اليمين",
                                        "منعطفات خطيرة متتالية يكون أولها إلى اليمين"),
                        new Replacement("تقليل السرعة والحفاظ على القيادة المناسبة طوال سلسلة المنعطفات بأكملها",
                                        "تقليل السرعة والحفاظ على القيادة المناسبة على امتداد جميع المنعطفات"),
                        new Replacement("التباطؤ بشكل ملحوظ قبل المنعطف الأول والحفاظ على السرعة المنخفضة طوال "
                                        + LEGACY_SERIES_WORD,
                                        "التباطؤ بشكل ملحوظ قبل المنعطف الأول والحفاظ على السرعة المنخفضة على امتداد جميع المنعطفات"),
                        new Replacement("أنت تسير بسرعة 90 كم/ساعة ليلاً وترى هذه العلامة المرورية التي تعلن عن تعاقب منعطفات، الأول إلى اليمين. ماذا تفعل أولاً؟",
                                        "أنت تقود ليلًا بسرعة 90 كم/ساعة، وترى هذه العلامة المرورية التي تشير إلى تعاقب منعطفات أولها إلى اليمين. ماذا تفعل أولاً؟"),
                        new Replacement("أنت تسير بسرعة 90 كم/ساعة ليلاً وترى هذه العلامة المرورية التي تعلن عن تعاقب منعطفات، الأول إلى اليسار. ماذا تفعل أولاً؟",
                                        "أنت تقود ليلًا بسرعة 90 كم/ساعة، وترى هذه العلامة المرورية التي تشير إلى تعاقب منعطفات أولها إلى اليسار. ماذا تفعل أولاً؟"),
                        new Replacement("سلسلة منعطفات", "تعاقب منعطفات"),
                        new Replacement("على الطريق الرطب أمام " + LEGACY_SERIES_WORD
                                        + " منعطفات، تباطأ بشكل ملحوظ قبل المنعطف الأول. لا تزد السرعة بين المنعطفات وابق دائماً في مسارك.",
                                        "على الطريق المبلل أمام منعطفات متتالية، تباطأ بشكل ملحوظ قبل المنعطف الأول. لا تزد السرعة بين المنعطفات وابق دائماً في مسارك."),
                        new Replacement("هل يجب الحفاظ على السرعة المنخفضة طوال سلسلة المنعطفات المعلنة، وليس فقط الأول؟",
                                        "هل يجب الحفاظ على السرعة المنخفضة على امتداد جميع المنعطفات المعلنة، وليس فقط الأول؟"),
                        new Replacement("هل يجب الحفاظ على السرعة المنخفضة على امتداد جميع المنعطفات المعلنة، وليس فقط الأول؟",
                                        "هل يجب الحفاظ على السرعة المنخفضة على امتداد جميع المنعطفات المعلنة، وليس فقط المنعطف الأول؟"),
                        new Replacement("على أي مسافة توضع هذه العلامة المرورية التحذيرية خارج المنطقة السكنية؟",
                                        "ما هي المسافة التي توضع عندها هذه العلامة التحذيرية خارج المنطقة السكنية؟"),
                        new Replacement("تعلن هذه العلامة المرورية عن " + LEGACY_SERIES_WORD
                                        + " من المنعطفات الخطيرة. يجب ضبط سرعتك لكامل " + LEGACY_SERIES_WORD
                                        + "، وليس فقط المنعطف الأول.",
                                        "تعلن هذه العلامة المرورية عن منعطفات خطيرة متتالية. يجب ضبط سرعتك على امتداد جميع المنعطفات، وليس فقط المنعطف الأول."),
                        new Replacement("تعلن هذه العلامة المرورية عن منعطفات خطيرة متعددة.",
                                        "تشير هذه العلامة المرورية إلى تعاقب منعطفات خطيرة."),
                        new Replacement("تعلن هذه العلامة المرورية عن منعطفات متعددة.",
                                        "تشير هذه العلامة المرورية إلى تعاقب منعطفات."),
                        new Replacement("تقليل السرعة والبقاء في مسارك طوال سلسلة المنعطفات بأكملها",
                                        "تقليل السرعة والبقاء في مسارك على امتداد جميع المنعطفات"),
                        new Replacement("أنت تسير بسرعة 90 كم/ساعة ليلاً وترى هذه العلامة المرورية التي تعلن عن "
                                        + LEGACY_SERIES_WORD + " منعطفات، الأول إلى اليمين. ماذا تفعل أولاً؟",
                                        "أنت تسير بسرعة 90 كم/ساعة ليلاً وترى هذه العلامة المرورية التي تعلن عن منعطفات متتالية، أولها إلى اليمين. ماذا تفعل أولاً؟"),
                        new Replacement("أنت تسير بسرعة 90 كم/ساعة ليلاً وترى هذه العلامة المرورية التي تعلن عن منعطفات متتالية، أولها إلى اليمين. ماذا تفعل أولاً؟",
                                        "أنت تقود ليلًا بسرعة 90 كم/ساعة، وترى هذه العلامة المرورية التي تشير إلى تعاقب منعطفات أولها إلى اليمين. ماذا تفعل أولاً؟"),
                        new Replacement("التباطؤ قبل المنعطف الأول والحفاظ على سرعة منخفضة طوال "
                                        + LEGACY_SERIES_WORD,
                                        "التباطؤ قبل المنعطف الأول والحفاظ على سرعة منخفضة على امتداد جميع المنعطفات"),
                        new Replacement("في الليل، يجب التباطؤ قبل المنعطف الأول والحفاظ على سرعة منخفضة طوال "
                                        + LEGACY_SERIES_WORD
                                        + ". التحرك نحو المنتصف يزيد من خطر التصادم مع المركبات القادمة.",
                                        "في الليل، يجب التباطؤ قبل المنعطف الأول والحفاظ على سرعة منخفضة على امتداد جميع المنعطفات. التحرك نحو المنتصف يزيد من خطر التصادم مع المركبات القادمة."),
                        new Replacement("هل أنت ملزم قانونياً بتقليل سرعتك قبل دخول المنعطف الأول من "
                                        + LEGACY_SERIES_WORD + "؟",
                                        "هل أنت ملزم قانونياً بتقليل سرعتك قبل دخول المنعطف الأول من المنعطفات المتتالية؟"),
                        new Replacement("تلزمك علامات الخطر قانونياً بتعديل سلوك قيادتك. يجب تقليل السرعة قبل دخول المنعطف الأول من "
                                        + LEGACY_SERIES_WORD + ".",
                                        "تلزمك علامات الخطر قانونياً بتعديل سلوك قيادتك. يجب تقليل السرعة قبل دخول المنعطف الأول من المنعطفات المتتالية."),
                        new Replacement("هذه علامة خطر (مثلثة، حدود حمراء). تنتمي إلى " + LEGACY_SERIES_WORD
                                        + " أ وتحذر من المواقف الخطرة.",
                                        "هذه علامة خطر (مثلثة، حدود حمراء). تنتمي إلى علامات الخطر وتحذر من المواقف الخطرة."),
                        new Replacement("علامات الخطر (" + LATIN_A + ") مثلثة؛ علامات الإلزام (" + LATIN_D
                                        + ") زرقاء.",
                                        "علامات الخطر مثلثة؛ وعلامات الإلزام زرقاء."),
                        new Replacement("فئة " + LATIN_A + " و" + LATIN_B, "الفئتين أ و ب"),
                        new Replacement("الفئتان " + LATIN_A + " و" + LATIN_B, "الفئتين أ و ب"),
                        new Replacement("فئة " + LATIN_A + ":", "الفئة أ:"),
                        new Replacement("فئة " + LATIN_B + ":", "الفئة ب:"),
                        new Replacement("فئة " + LATIN_B + " من", "الفئة ب من"),
                        new Replacement("هل يُسمح باستخدام ناقل الحركة على الوضع المحايد أثناء النزول في منحدر خطير؟",
                                        "هل يُسمح باستخدام ناقل الحركة على الوضع المحايد أثناء النزول في منحدر خطير؟"),
                        new Replacement("هل يُسمح بالنزول في منحدر خطير باستخدام ناقل الحركة على الوضع المحايد؟",
                                        "هل يُسمح باستخدام ناقل الحركة على الوضع المحايد أثناء النزول في منحدر خطير؟"),
                        new Replacement("تشغيل غيار منخفضة واستخدام الكبح بالمحرك للتحكم في السرعة",
                                        "استخدام الغيار المنخفض والاعتماد على فرملة المحرك للتحكم في السرعة"),
                        new Replacement("تشغيل غيار أدنى قبل الانحدار والنزول بشكل منضبط مع الكبح بالمحرك",
                                        "اختيار غيار منخفض قبل النزول والتحكم في السرعة بالمحرك"),
                        new Replacement("هل يُسمح بالتجاوز قبل تضيق الطريق مباشرة؟",
                                        "هل يمكن التجاوز قبل الوصول إلى هذه العلامة المرورية؟"),
                        new Replacement("أنت تسير بسرعة 90 كم/ساعة وترى هذه العلامة المرورية قبل تضيق الطريق مباشرة، مع اقتراب شاحنة من الأمام. ماذا تفعل أولاً؟",
                                        "أنت تقود بسرعة 90 كم/ساعة، وترى تضيقًا في الطريق أمامك مع اقتراب شاحنة. ماذا تفعل أولاً؟"),
                        new Replacement("ماذا يجب أن تفعل عند رؤية علامة تضيق الطريق هذه؟",
                                        "ماذا يجب عليك فعله عند رؤية هذه العلامة المرورية التي تشير إلى تضيق الطريق؟"),
                        new Replacement("أنت تسير بسرعة 90 كم/ساعة على طريق مبلل وترى هذه العلامة المرورية للتضيق من اليسار. المرور القادم يسير قريباً من مسارك. ماذا تفعل أولاً؟",
                                        "أنت تقود بسرعة 90 كم/ساعة على طريق مبلل، والطريق يضيق من اليسار مع اقتراب سيارات من الاتجاه المقابل. ماذا تفعل أولاً؟"),
                        new Replacement("ماذا يجب أن تفعل عندما ترى هذه العلامة المرورية؟",
                                        "ماذا يجب عليك فعله عند رؤية هذه العلامة المرورية؟"),
                        new Replacement("ماذا يجب أن تفعل عند رؤية هذه العلامة المرورية؟",
                                        "ماذا يجب عليك فعله عند رؤية هذه العلامة المرورية؟"),
                        new Replacement("أنت تقود على طريق مبلل بسرعة 90 كم/ساعة وترى هذه العلامة المرورية بالقرب من رصيف. ماذا تفعل؟",
                                        "أنت تقود بسرعة 90 كم/ساعة على طريق مبلل، وترى هذه العلامة بالقرب من الرصيف. ماذا يجب عليك فعله؟"),
                        new Replacement("أنت تقود على طريق مبلل بسرعة 70 كم/ساعة وترى هذه العلامة المرورية قبيل مطب. ما الإجراء الصحيح؟",
                                        "أنت تقود بسرعة 70 كم/ساعة على طريق مبلل، وترى علامة قبل مطب. ماذا تفعل؟"),
                        new Replacement("تقليل السرعة إلى سرعة منخفضة (10-20 كم/ساعة) والمرور فوق المطب مباشرة",
                                        "تقليل السرعة إلى نحو 30 كم/ساعة والمرور فوق المطب بشكل آمن"),
                        new Replacement("هل يُسمح بالكبح فجأة على الطريق الزلق الذي تعلنه هذه العلامة المرورية؟",
                                        "هل يمكن الفرملة فجأة على طريق زلق؟"),
                        new Replacement("هل أنت ملزم قانونياً بتكييف سرعتك مع أحوال الطريق الزلق التي تعلنها هذه العلامة المرورية؟",
                                        "هل يجب عليك تخفيف سرعتك بسبب طريق زلق؟"),
                        new Replacement("هل يُسمح بالسير خلف مركبة أخرى عن قرب في منطقة تطاير الحصى؟",
                                        "هل يجوز السير على مسافة قريبة خلف مركبة أخرى في منطقة تتناثر فيها الحصى؟"),
                        new Replacement("على أي مسافة توضع هذه العلامة المرورية التحذيرية خارج المنطقة السكنية؟",
                                        "ما هي المسافة التي توضع عندها هذه العلامة التحذيرية خارج المناطق السكنية؟"),
                        new Replacement("على أي مسافة توضع هذه العلامة المرورية التحذيرية خارج المنطقة السكنية؟",
                                        "ما هي المسافة التي توضع عندها هذه العلامة التحذيرية خارج المناطق السكنية؟"),
                        new Replacement("هل يُسمح بالتوقف أو الانتظار في منطقة تساقط الأحجار التي تعلنها هذه العلامة المرورية؟",
                                        "هل يمكن التوقف أو الانتظار في منطقة تساقط الأحجار؟"),
                        new Replacement("أنت تقود بسرعة 70 كم/ساعة بعد هطول أمطار غزيرة في منطقة جبلية وترى هذه العلامة المرورية. ماذا تفعل؟",
                                        "أنت تقود بسرعة 70 كم/ساعة بعد أمطار غزيرة في منطقة جبلية، وترى هذه العلامة. ماذا يجب عليك فعله؟"),
                        new Replacement("هل أنت ملزم بتقليل السرعة والمرور بسرعة عبر منطقة الخطر دون التوقف بشكل غير ضروري؟",
                                        "هل أنت ملزم بتقليل السرعة والمرور عبر منطقة الخطر دون توقف غير ضروري؟"),
                        new Replacement("أنت تقود ليلاً بسرعة 50 كم/ساعة وتقترب من هذه العلامة المرورية. أحد المشاة يقف عند حافة ممر المشاة. ماذا تفعل؟",
                                        "أنت تقود ليلاً بسرعة 50 كم/ساعة، وهناك مشاة عند ممر المشاة. ماذا تفعل؟"),
                        new Replacement("هل يُسمح بالتجاوز قبيل ممر عبور المشاة مباشرة؟",
                                        "هل يُسمح بالتجاوز مباشرة قبل ممر عبور المشاة؟"),
                        new Replacement("تقترب من العلامة المرورية: تحذير: أطفال. الساعة 15:30 يوم عمل وانتهى الدوام المدرسي للتو. كيف تقود؟",
                                        "أمامك علامة تحذير: أطفال. الساعة 15:30 بعد انتهاء المدرسة. كيف تتصرف أثناء القيادة؟"),
                        new Replacement("الكبح الخفيف والمراقبة مع الاستمرار بسرعة 30 كم/ساعة وهي السرعة الاعتيادية",
                                        "أخفف السرعة، أراقب الطريق جيدًا، وأستعد للتوقف عند الحاجة."),
                        new Replacement("الضغط على البوق لتحذير الأطفال ثم الاستمرار بالسرعة الاعتيادية",
                                        "أستخدم المنبّه لتنبيه الأطفال ثم أتابع السير بالسرعة العادية"),
                        new Replacement("ترى العلامة المرورية: تحذير: أطفال لكن لا أطفال مرئيون. هل أنت ملزم بعد ذلك بتقليل سرعتك والقيادة بيقظة إضافية؟",
                                        "ترى علامة تحذير بوجود أطفال، لكن لا يوجد أطفال ظاهرون. هل يجب عليك مع ذلك تخفيف السرعة وزيادة الانتباه؟"),
                        new Replacement("هل يُسمح بالتجاوز في منطقة مميّزة بـ العلامة المرورية: تحذير: أطفال؟",
                                        "هل يُسمح بالتجاوز في منطقة تحمل علامة تحذير: أطفال؟"),
                        new Replacement("ما الفرق بين العلامة المرورية: تحذير: أطفال و العلامة المرورية: منطقة عبور المشاة (ممر عبور المشاة)؟",
                                        "ما الفرق بين علامة تحذير: أطفال وعلامة ممر عبور المشاة؟"),
                        new Replacement("تعلن العلامة المرورية: تحذير: أطفال أن الأطفال قد يدخلون الطريق فجأة. إدراك الأطفال لحركة المرور محدود وتصرفاتهم غير متوقعة، مما يستوجب الحذر الخاص.",
                                        "تشير هذه العلامة المرورية إلى احتمال ظهور الأطفال بشكل مفاجئ بالقرب من الطريق. لذلك يجب القيادة بحذر شديد والانتباه المستمر."),
                        new Replacement("تستوجب العلامة المرورية: تحذير: أطفال تكيفات فعلية: تقليل السرعة بشكل ملحوظ والحفاظ على يقظة مرتفعة. غياب الأطفال المرئيين لا يعفيك من الالتزام بالبقاء متيقظاً ومستعداً للفرملة.",
                                        "عند رؤية هذه العلامة المرورية يجب تخفيف السرعة وزيادة الانتباه، حتى لو لم يظهر أطفال في تلك اللحظة."),
                        new Replacement("انتهاء الدوام المدرسي هو اللحظة الأعلى خطورة عند منطقة العلامة المرورية: تحذير: أطفال: مجموعات من الأطفال تتنقل بشكل متفرق وغير منظم. يجب أن تكون سرعتك منخفضة بما يكفي للتوقف فوراً لأي شخص يدخل الطريق.",
                                        "بعد انتهاء المدرسة يزداد احتمال ظهور الأطفال بشكل مفاجئ. لذلك يجب أن تكون سرعتك منخفضة جدًا وأن تبقى مستعدًا للتوقف الفوري."),
                        new Replacement("تحدد العلامة المرورية: تحذير: أطفال منطقة غير منظمة قد يظهر فيها الأطفال فجأة — لا يوجد ممر عبور ثابت. تحدد العلامة المرورية: منطقة عبور المشاة ممر عبور مشاة مخصصاً يسري فيه التزام قانوني بالأولوية للمشاة العابرين أو المزمعين العبور.",
                                        "علامة تحذير: أطفال تنبّه إلى احتمال وجود أطفال بالقرب من الطريق أو اندفاعهم إليه فجأة، أما علامة ممر عبور المشاة فتشير إلى مكان عبور مخصص يجب فيه احترام أولوية المشاة."),
                        new Replacement("تفرض العلامة المرورية: تحذير: أطفال التزاماً سلوكياً دائماً في جميع أنحاء المنطقة المشار إليها بصرف النظر عن رؤية الأطفال. قد يكون الأطفال خلف المركبات أو العوائق ويمكنهم دخول الطريق في أي لحظة.",
                                        "وجود هذه العلامة يعني أن احتمال ظهور الأطفال قائم دائمًا، لذلك يجب تخفيف السرعة والانتباه حتى إن لم ترَ أحدًا في تلك اللحظة."),
                        new Replacement("التجاوز محظور في منطقة العلامة المرورية: تحذير: أطفال. المركبة المتجاوِزة تزيد من عرض الطريق الذي قد يعبره الأطفال فجأة وتحدّ أيضاً من مجال رؤية سائقين آخرين والأطفال أنفسهم.",
                                        "لا يُسمح بالتجاوز في منطقة قد يظهر فيها الأطفال بشكل مفاجئ، لأن ذلك يزيد الخطر ويقلل مجال الرؤية."),
                        new Replacement("الضغط على البوق حتى يعرف المشاة بأنك قادم",
                                        "أستخدم المنبّه حتى يعرف المشاة أنني قادم"),
                        new Replacement("الضغط على البوق لتحذير الدراجين ثم الاستمرار بالسرعة الاعتيادية",
                                        "أستخدم المنبّه لتنبيه الدراجين ثم أتابع السير بالسرعة العادية"),
                        new Replacement("ما الفرق بين العلامة المرورية: منطقة عبور المشاة (ممر عبور المشاة) و العلامة المرورية: منطقة عبور الدراجات والدراجات البخارية (ممر عبور الدراجات)؟",
                                        "ما الفرق بين ممر عبور المشاة وممر عبور الدراجات؟"),
                        new Replacement("تتعلق العلامة المرورية: منطقة عبور المشاة بممرات عبور المشاة؛ والعلامة المرورية: منطقة عبور الدراجات والدراجات البخارية بالدراجين وراكبي الدراجات البخارية. كلاهما يستوجب إعطاء الأولوية لمستخدمي الطريق المعنيين العابرين.",
                                        "ممر عبور المشاة يخص المشاة، أما ممر عبور الدراجات فيخص الدراجين وراكبي الدراجات البخارية. في الحالتين يجب إعطاء الأولوية للعابرين المعنيين."),
                        new Replacement("دراج يقترب من ممر العبور المشار إليه بـ A25 من جهة اليمين. ماذا تفعل؟",
                                        "دراج يقترب من ممر عبور الدراجات من جهة اليمين. ماذا تفعل؟"),
                        new Replacement("عند ممر عبور الدراجات (A25) يجب إعطاء الأولوية للدراجين العابرين أو المزمعين العبور. التوقف إلزامي إذا كانوا سيتعرضون للخطر.",
                                        "عند ممر عبور الدراجات، يجب إعطاء الأولوية للدراجين الذين يعبرون أو يستعدون للعبور. يجب التوقف إذا كان مرورك قد يعرّضهم للخطر."),
                        new Replacement("دراج مستعد للعبور عند العلامة المرورية: منطقة عبور الدراجات والدراجات البخارية. هل أنت ملزم بالتوقف؟",
                                        "دراج مستعد لعبور الطريق عند ممر عبور الدراجات. هل يجب عليك التوقف؟"),
                        new Replacement("عند ممر عبور الدراجات (العلامة المرورية: منطقة عبور الدراجات والدراجات البخارية) يجب التوقف أو التباطؤ لإعطاء الأولوية للدراجين العابرين أو المزمعين العبور بوضوح. نفس القاعدة المطبقة على العلامة المرورية: منطقة عبور المشاة للمشاة.",
                                        "عند ممر عبور الدراجات، يجب عليك التوقف أو التباطؤ لإعطاء الأولوية للدراجين الذين يعبرون أو يستعدون للعبور بوضوح. تنطبق القاعدة نفسها على ممرات عبور المشاة."),
                        new Replacement("هل يُسمح بالتجاوز قبيل ممر عبور الدراجات المشار إليه بـ A25؟",
                                        "هل يُسمح بالتجاوز مباشرة قبل ممر عبور الدراجات؟"),
                        new Replacement("على أي مسافة توضع هذه العلامة المرورية التحذيرية خارج المدينة؟",
                                        "ما هي المسافة التي تُوضع عندها هذه العلامة التحذيرية خارج المنطقة السكنية؟"),
                        new Replacement("ما معنى علامة المرور هذه", "ما معنى هذه العلامة المرورية"),
                        new Replacement("الأولوية على الطريق الجانبي اليسار",
                                        "أولوية على طريق جانبي متقاطع"),
                        new Replacement("الأولوية على الطريق الجانبي اليمين",
                                        "أولوية على طريق جانبي متقاطع"),
                        new Replacement("B1 – إعطاء الأولوية", "إعطاء الأولوية"),
                        new Replacement("تستخدم هذه الفئة15 رموزاً لإظهار تكوين الطريق والإشارة إلى حق أولويتك على طرق جانبية محددة.",
                                        "تستخدم هذه العلامة رمزًا يوضح شكل الطريق لتبيّن حق الأولوية على الطريق الجانبي المحدد."),
                        new Replacement("تستخدم هذه الفئة15 رموزاً لإظهار تكوين الطريق. B9 علامة مرورية طريق أولوية عامة؛ B1 تُلزمك بإعطاء الأولوية.",
                                        "تستخدم هذه العلامة رمزًا يوضح شكل الطريق لتبيّن حق الأولوية على الطريق الجانبي المحدد. أما علامة طريق الأولوية فتعني أن الطريق الذي تسلكه هو طريق أولوية بشكل عام، وعلامة إعطاء الأولوية تلزمك بالتخلي عن الأولوية."),
                        new Replacement("في جميع علامات هذه الفئة15 يُمثّل الخط السميك دائماً طريقك (طريق الأولوية). الخط الرفيع يُمثّل الطريق الجانبي الذي يجب أن يعطي الأولوية.",
                                        "في جميع هذه العلامات يُمثّل الخط السميك دائمًا طريقك، أي طريق الأولوية. أما الخط الرفيع فيمثّل الطريق الجانبي الذي يجب على سائقيه إعطاؤك الأولوية."),
                        new Replacement("لا، علامة مرورية تالفة لا تُلغي الالتزام القانوني؛ عند الشك يجب على السائق التوقف وإعطاء الأولوية",
                                        "لا، العلامة المرورية التالفة لا تُلغي الالتزام القانوني؛ عند الشك يجب على السائق التوقف وإعطاء الأولوية"),
                        new Replacement("تعرج", "منعطف"),
                        new Replacement("خارج المنطقة السكنية", "خارج المنطقة السكنية"),
                        new Replacement("خارج المناطق السكنية", "خارج المنطقة السكنية"),
                        new Replacement("داخل المنطقة السكنية", "داخل المنطقة السكنية"));

        private static final List<Replacement> ENGLISH_REPLACEMENTS = List.of(
                        new Replacement("What does this sign mean?", "What does this traffic sign mean?"),
                        new Replacement("What does this road sign mean?", "What does this traffic sign mean?"),
                        new Replacement("What hazard does this sign announce?",
                                        "What danger does this traffic sign indicate?"),
                        new Replacement("What danger does this sign indicate?",
                                        "What danger does this traffic sign indicate?"),
                        new Replacement("What should you do when you see this sign?",
                                        "What should you do when you see this traffic sign?"),
                        new Replacement("B9 – Priority road", "Priority road sign"),
                        new Replacement("B1 – Give way", "Give way sign"),
                        new Replacement("This type of sign uses symbols to show road configuration. B9 is a general priority road sign; B1 requires you to give way.",
                                        "This type of sign uses symbols to show the road configuration. A priority road sign marks the main priority road, while a give way sign requires you to yield."),
                        new Replacement("Which series does sign the traffic sign",
                                        "To which category does the traffic sign"),
                        new Replacement("A category of consecutive dangerous bends",
                                        "A succession of dangerous bends"),
                        new Replacement(
                                        "Which sign forms the starting point for the traffic sign \"End of path reserved for pedestrians, cyclists, horse riders and agricultural vehicles\" reserved zone?",
                                        "Which sign marks the beginning of the reserved zone that ends with the traffic sign \"End of path reserved for pedestrians, cyclists, horse riders and agricultural vehicles\"?"),
                        new Replacement("To which category of traffic signs does this sign belong?",
                                        "To which category does this traffic sign belong?"),
                        new Replacement("Dangerous double bend, first to the left",
                                        "Dangerous double bend or succession of bends, first to the left"),
                        new Replacement("Dangerous double bend, first to the right",
                                        "Dangerous double bend or succession of bends, first to the right"),
                        new Replacement("A consecutive dangerous bends where the first is to the left",
                                        "A succession of dangerous bends where the first is to the left"),
                        new Replacement("A consecutive dangerous bends where the first is to the right",
                                        "A succession of dangerous bends where the first is to the right"),
                        new Replacement("You approach the traffic sign \"Warning: children\". It is 15:30 on a weekday and school has just ended. How do you drive?",
                                        "You are approaching a children warning sign. It is 15:30, just after school has ended. How should you drive?"),
                        new Replacement("The end of the school day is the highest-risk moment near an the traffic sign \"Warning: children\" zone: groups of children walk around in a scattered and disorganised way. Your speed must be low enough to stop immediately for anyone entering the road.",
                                        "Just after school ends, children may appear suddenly and move unpredictably. Your speed must be very low and you must be ready to stop immediately."),
                        new Replacement("What is the difference between the traffic sign \"Warning: children\" (warning: children) and the traffic sign \"Pedestrian crossing\" (pedestrian crossing)?",
                                        "What is the difference between a children warning sign and a pedestrian crossing sign?"),
                        new Replacement("the traffic sign \"Warning: children\" marks an unstructured zone where children may appear unexpectedly â€\" there is no fixed crossing. the traffic sign \"Pedestrian crossing\" marks a designated pedestrian crossing with a legal priority obligation for pedestrians who are crossing or about to cross.",
                                        "A children warning sign warns of children near the road or of children who may suddenly enter it. A pedestrian crossing sign indicates a designated crossing where you must respect pedestrians' priority."),
                        new Replacement("You see the traffic sign \"Warning: children\" but no children are visible. Are you still required to reduce your speed and drive with extra alertness?",
                                        "You see a children warning sign, but no children are visible. Do you still have to reduce your speed and be extra alert?"),
                        new Replacement("the traffic sign \"Warning: children\" imposes a permanent behavioural obligation throughout the indicated zone regardless of children being visible at the time. Children may be behind vehicles or obstacles and can enter the road at any moment.",
                                        "The presence of this sign means that children may appear at any time. You must therefore reduce speed and remain attentive even if you do not currently see any children."),
                        new Replacement("Is it allowed to overtake in a zone marked by the traffic sign \"Warning: children\"?",
                                        "Is overtaking allowed in an area marked by a children warning sign?"),
                        new Replacement("Overtaking is prohibited in an the traffic sign \"Warning: children\" zone. An overtaking vehicle increases the road width that children may unexpectedly cross and also limits the field of view of other drivers and the children themselves.",
                                        "Overtaking is not allowed in an area where children may suddenly appear, because it increases the danger and reduces visibility."),
                        new Replacement("What is the difference between the traffic sign \"Pedestrian crossing\" (pedestrian crossing) and the traffic sign \"Bicycle and moped crossing\" (cyclist crossing)?",
                                        "What is the difference between a pedestrian crossing and a cyclist crossing?"),
                        new Replacement("the traffic sign \"Pedestrian crossing\" refers to pedestrian crossings; the traffic sign \"Bicycle and moped crossing\" to cyclists and moped riders. Both require giving way to the respective road users who are crossing.",
                                        "A pedestrian crossing is for pedestrians, while a cyclist crossing is for cyclists and moped riders. In both cases, you must give way to the relevant road users who are crossing."),
                        new Replacement("the traffic sign \"Bicycle and moped crossing\" belongs to the danger signs. Danger signs are triangular with a red border on a white background.",
                                        "This traffic sign belongs to the danger signs. Danger signs are triangular, with a red border and a white background."),
                        new Replacement("Near the traffic sign \"Bicycle and moped crossing\", cyclists and moped riders may suddenly cross the road. They can be difficult to see, travel fast and have no extra protection â€\" extra caution is required.",
                                        "This traffic sign warns that cyclists and moped riders may suddenly cross the road. They may be hard to see and can approach quickly, so extra caution is required."),
                        new Replacement("A cyclist is approaching the crossing from the right, indicated by the traffic sign \"Bicycle and moped crossing\". What do you do?",
                                        "A cyclist is approaching the cyclist crossing from the right. What do you do?"),
                        new Replacement("At a cyclist crossing (the traffic sign \"Bicycle and moped crossing\") you must give way to cyclists who are crossing or about to cross. Stopping is mandatory if they would be endangered.",
                                        "At a cyclist crossing, you must give way to cyclists who are crossing or about to cross. You must stop if passing would put them in danger."),
                        new Replacement("A cyclist is ready to cross the road at the traffic sign \"Bicycle and moped crossing\". Are you required to stop?",
                                        "A cyclist is ready to cross the road at a cyclist crossing. Are you required to stop?"),
                        new Replacement("At a cyclist crossing (the traffic sign \"Bicycle and moped crossing\"), you must stop or slow down to give way to cyclists who are crossing or clearly about to cross. The same rule applies as for the traffic sign \"Pedestrian crossing\" for pedestrians.",
                                        "At a cyclist crossing, you must stop or slow down to give way to cyclists who are crossing or clearly about to cross. The same rule applies at pedestrian crossings."),
                        new Replacement("Is it allowed to overtake just before a cyclist crossing indicated by the traffic sign \"Bicycle and moped crossing\"?",
                                        "Is it allowed to overtake just before a cyclist crossing?"));

        private static final List<Replacement> DUTCH_REPLACEMENTS = List.of(
                        new Replacement("Wat betekent dit bord?", "Wat betekent dit verkeersbord?"),
                        new Replacement("Welk gevaar kondigt dit bord aan?", "Welk gevaar duidt dit verkeersbord aan?"),
                        new Replacement("Een reeks opeenvolgende gevaarlijke bochten waarbij de eerste naar links gaat",
                                        "Opeenvolgende gevaarlijke bochten waarbij de eerste naar links gaat"),
                        new Replacement("Een reeks opeenvolgende gevaarlijke bochten waarbij de eerste naar rechts gaat",
                                        "Opeenvolgende gevaarlijke bochten waarbij de eerste naar rechts gaat"),
                        new Replacement("Een opeenvolgende gevaarlijke bochten waarbij de eerste naar links gaat",
                                        "Opeenvolgende gevaarlijke bochten waarvan de eerste naar links gaat"),
                        new Replacement("Een opeenvolgende gevaarlijke bochten waarbij de eerste naar rechts gaat",
                                        "Opeenvolgende gevaarlijke bochten waarvan de eerste naar rechts gaat"),
                        new Replacement("Gevaarlijke dubbele of meer bochten, de eerste naar links",
                                        "Gevaarlijke dubbele bocht of opeenvolgende bochten, de eerste naar links"),
                        new Replacement("Gevaarlijke dubbele of meer bochten, de eerste naar rechts",
                                        "Gevaarlijke dubbele bocht of opeenvolgende bochten, de eerste naar rechts"),
                        new Replacement("Wat moet u doen wanneer u dit bord ziet?",
                                        "Wat moet u doen wanneer u dit verkeersbord ziet?"));

        private static final List<Replacement> FRENCH_REPLACEMENTS = List.of(
                        new Replacement("Que signifie ce panneau ?", "Que signifie ce panneau de signalisation ?"),
                        new Replacement("Quel danger ce panneau annonce-t-il ?",
                                        "Quel danger indique ce panneau de signalisation ?"),
                        new Replacement("Que devez-vous faire lorsque vous voyez ce panneau ?",
                                        "Que devez-vous faire lorsque vous voyez ce panneau de signalisation ?"),
                        new Replacement("À quelle catégorie de panneaux de signalisation ce panneau appartient-il ?",
                                        "À quelle catégorie appartient ce panneau de signalisation ?"),
                        new Replacement("A quelle categorie de panneaux de signalisation ce panneau appartient-il ?",
                                        "A quelle categorie appartient ce panneau de signalisation ?"),
                        new Replacement("Une virages dangereux consécutifs dont le premier est à gauche",
                                        "Une succession de virages dangereux dont le premier est à gauche"),
                        new Replacement("Une virages dangereux consécutifs dont le premier est à droite",
                                        "Une succession de virages dangereux dont le premier est à droite"),
                        new Replacement("Double virage, le premier à gauche",
                                        "Double virage dangereux ou succession de virages, le premier à gauche"),
                        new Replacement("Double virage, le premier à droite",
                                        "Double virage dangereux ou succession de virages, le premier à droite"),
                        new Replacement("sur l'ensemble des virages de virages annoncés",
                                        "sur l'ensemble des virages annoncés"));

        private static final Pattern ENGLISH_TRAILING_CODE_PATTERN = Pattern.compile(
                        "(the traffic sign\\s+\"[^\"]+\")\\s*(?:\\d+/)?[A-Z]+\\d+[A-Za-z0-9/-]*");
        private static final Pattern DUTCH_TRAILING_CODE_PATTERN = Pattern.compile(
                        "(het verkeersbord\\s+\"[^\"]+\")\\s*(?:\\d+/)?[A-Z]+\\d+[A-Za-z0-9/-]*");
        private static final Pattern FRENCH_TRAILING_CODE_PATTERN = Pattern.compile(
                        "(le panneau\\s+\"[^\"]+\")\\s*(?:\\d+/)?[A-Z]+\\d+[A-Za-z0-9/-]*");
        private static final Pattern ARABIC_CATEGORY_SUFFIX_PATTERN = Pattern.compile(
                        "\\s*\\((?:السلسلة|الفئة)\\s+[أ-يA-Za-z0-9]+\\)");
        private static final Pattern ARABIC_TRAILING_CODE_AFTER_SIGN_PATTERN = Pattern.compile(
                        "(العلامة المرورية:\\s*[^؟!.،,:؛]+?)(?:[A-Z]+\\d+[A-Za-z0-9/-]*|\\d+[A-Za-z][A-Za-z0-9/-]*)");

        private DrivingTextSanitizer() {
        }

        public static String sanitize(String languageCode, String value) {
                return sanitize(Language.fromCode(languageCode), value);
        }

        public static String sanitize(Language language, String value) {
                String normalized = ImportedTextSanitizer.sanitize(value);
                if (normalized == null || normalized.isBlank()) {
                        return normalized;
                }

                String sanitized = switch (language) {
                        case AR -> applyReplacements(normalized, ARABIC_REPLACEMENTS);
                        case EN -> applyReplacements(normalized, ENGLISH_REPLACEMENTS);
                        case NL -> applyReplacements(normalized, DUTCH_REPLACEMENTS);
                        case FR -> applyReplacements(normalized, FRENCH_REPLACEMENTS);
                        case UNKNOWN -> normalized;
                };

                sanitized = switch (language) {
                        case AR -> normalizeArabicPatterns(sanitized);
                        case EN -> normalizeEnglishPatterns(sanitized);
                        case NL -> normalizeDutchPatterns(sanitized);
                        case FR -> normalizeFrenchPatterns(sanitized);
                        case UNKNOWN -> sanitized;
                };

                return sanitized
                                .replaceAll("\\s{2,}", " ")
                                .replaceAll("\\s+([.,;:!?؟])", "$1")
                                .trim();
        }

        private static String applyReplacements(String value, List<Replacement> replacements) {
                String result = value;
                for (Replacement replacement : replacements) {
                        result = result.replace(replacement.from(), replacement.to());
                }
                return result;
        }

        private static String normalizeArabicPatterns(String value) {
                String normalized = value
                                .replace("لافتات الخطر", "علامات الخطر")
                                .replace("لافتات الحظر", "علامات الحظر")
                                .replace("لافتات الأولوية", "علامات الأولوية")
                                .replace("لافتات الإلزام", "علامات الإلزام")
                                .replace("لافتات المعلومات", "علامات المعلومات")
                                .replace("لافتات الوقوف والانتظار", "علامات الوقوف والانتظار")
                                .replace("لافتات التحذير والخطر", "علامات التحذير والخطر")
                                .replace("من لافتات المرور", "من العلامات المرورية")
                                .replace("اللافتة العلامة المرورية:", "العلامة المرورية:")
                                .replace("لافتة العلامة المرورية:", "العلامة المرورية:")
                                .replace("هذا اللافتة التحذيرية", "هذه العلامة التحذيرية")
                                .replace("هذا اللافتة", "هذه العلامة")
                                .replace("خارج التجمعات السكانية", "خارج المنطقة السكنية")
                                .replace("داخل التجمعات السكانية", "داخل المنطقة السكنية")
                                .replace("هل أنت ملزم بتعديل سلوك قيادتك عندما ترى لافتة الخطر هذه؟",
                                                "هل يجب عليك تعديل سلوك قيادتك عند رؤية هذه العلامة المرورية التحذيرية؟")
                                .replace("على أي مسافة من الخطر توضع لافتة الخطر خارج المنطقة السكنية؟",
                                                "ما هي المسافة التي توضع عندها هذه العلامة التحذيرية خارج المنطقة السكنية؟")
                                .replace("على أي مسافة يُوضع هذا اللافتة التحذيرية خارج المنطقة السكنية؟",
                                                "ما هي المسافة التي توضع عندها هذه العلامة التحذيرية خارج المنطقة السكنية؟")
                                .replace("على أي مسافة قبل إشارات المرور توضع لافتة العلامة المرورية: وجود إشارة ضوئية عادةً خارج المنطقة السكنية؟",
                                                "ما هي المسافة التي توضع عندها هذه العلامة التحذيرية عادةً خارج المنطقة السكنية؟")
                                .replace("على أي مسافة قبل إشارات المرور توضع العلامة المرورية: وجود إشارة ضوئية عادةً خارج المنطقة السكنية؟",
                                                "ما هي المسافة التي توضع عندها هذه العلامة التحذيرية عادةً خارج المنطقة السكنية؟")
                                .replace("ترى اللافتة العلامة المرورية:", "ترى العلامة المرورية:")
                                .replace("فاللافتة", "فالعلامة المرورية")
                                .replace("شكل اللافتة", "شكل العلامة المرورية")
                                .replace("لكن لافتة", "لكن علامة")
                                .replace("بدون لافتات", "بدون علامات مرورية")
                                .replace("أي لافتة", "أي علامة مرورية")
                                .replace("اي لافتة", "أي علامة مرورية")
                                .replace("لافتة حظر", "علامة حظر")
                                .replace("لافتة الممر الضيق", "علامة الممر الضيق")
                                .replace("ما معنى الرمز الموجود على اللافتة؟",
                                                "ما معنى الرمز الموجود على هذه العلامة المرورية؟")
                                .replace("من السلسلة", "من المنعطفات المتتالية")
                                .replace("طوال السلسلة", "على امتداد جميع المنعطفات")
                                .replace("لكامل السلسلة", "على امتداد جميع المنعطفات")
                                .replace("نظام السلسلة", "نظام التعاقب")
                                .replace("سلسلة المنطقة", "علامات المنطقة")
                                .replace("خالم", "خالٍ")
                                .replace("استثناءًا", "استثناءً")
                                .replace("ماذا يجب أن تفعل عند رؤية لافتة تضيق الطريق هذه؟",
                                                "ماذا يجب عليك فعله عند رؤية هذه العلامة المرورية التي تشير إلى تضيق الطريق؟")
                                .replace("هل أنت ملزم قانونياً بتقليل سرعتك عند رؤية لافتة تضيق الطريق هذه؟",
                                                "هل يجب عليك قانونياً تخفيف سرعتك عند رؤية هذه العلامة المرورية التي تشير إلى تضيق الطريق؟")
                                .replace("ماذا يجب أن تفعل عند رؤية لافتة التضيق من اليسار هذه؟",
                                                "ماذا يجب عليك فعله عند رؤية هذه العلامة المرورية التي تشير إلى تضيق الطريق من اليسار؟")
                                .replace("هل أنت ملزم قانونياً بتعديل سلوك قيادتك عند رؤية لافتة تضيق الطريق من اليمين هذه؟",
                                                "هل يجب عليك قانونياً تعديل سلوك قيادتك عند رؤية هذه العلامة المرورية التي تشير إلى تضيق الطريق من اليمين؟")
                                .replace("ترى العلامة المرورية: وجود إشارة ضوئية. كيف تعدّل سلوكك القيادي؟",
                                                "ترى العلامة المرورية التي تشير إلى وجود إشارة ضوئية. كيف تعدّل سلوكك أثناء القيادة؟")
                                .replace("منعطف خطر.", "منعطف خطير.")
                                .replace("الانحناء المزدوج", "المنعطف المزدوج")
                                .replace("تتابع أكثر من انحناءتين", "تتابع أكثر من منعطفين")
                                .replace("تتابع أكثر من ثنيتين", "تتابع أكثر من منعطفين")
                                .replace("أنت تسير بسرعة 90 كم/ساعة ليلاً وترى هذه العلامة المرورية التي تعلن عن تعاقب منعطفات، الأول إلى اليمين. ماذا تفعل أولاً؟",
                                                "أنت تقود ليلًا بسرعة 90 كم/ساعة، وترى هذه العلامة المرورية التي تشير إلى تعاقب منعطفات أولها إلى اليمين. ماذا تفعل أولاً؟")
                                .replace("أنت تسير بسرعة 90 كم/ساعة ليلاً وترى هذه العلامة المرورية التي تعلن عن تعاقب منعطفات، الأول إلى اليسار. ماذا تفعل أولاً؟",
                                                "أنت تقود ليلًا بسرعة 90 كم/ساعة، وترى هذه العلامة المرورية التي تشير إلى تعاقب منعطفات أولها إلى اليسار. ماذا تفعل أولاً؟")
                                .replace("اللافتة", "العلامة المرورية")
                                .replace("لافتات", "علامات")
                                .replace("لافتة", "علامة");
                normalized = ARABIC_CATEGORY_SUFFIX_PATTERN.matcher(normalized).replaceAll("");
                return ARABIC_TRAILING_CODE_AFTER_SIGN_PATTERN.matcher(normalized).replaceAll("$1");
        }

        private static String normalizeEnglishPatterns(String value) {
                String normalized = value
                                .replace("the the traffic sign", "the traffic sign")
                                .replace("Which series does sign the traffic sign",
                                                "To which category does the traffic sign")
                                .replace("To which series does the traffic sign",
                                                "To which category does the traffic sign")
                                .replace("Which series does the traffic sign",
                                                "To which category does the traffic sign")
                                .replace("What is the purpose of the the traffic sign",
                                                "What is the purpose of the traffic sign")
                                .replace("an the traffic sign", "the traffic sign")
                                .replace("a the traffic sign", "the traffic sign")
                                .replace("from 1 series such as", "from a matching start sign such as")
                                .replace(" is a hazard sign from the A-series", " is a danger sign")
                                .replace(" belongs to the G-series (additional and information signs)",
                                                " belongs to the information signs")
                                .replace(" belongs to the zone series", " belongs to the zone signs")
                                .replace("E-series contains all parking and stopping regulation signs.",
                                                "Parking and stopping signs regulate parking and stopping.")
                                .replace("E9-series signs regulate parking.",
                                                "These parking signs regulate parking.")
                                .replace("Z-series", "zone signs")
                                .replace(" series such as ", " sign group such as ")
                                .replace("the traffic sign \"Mandatory direction for dangerous goods\" series channels dangerous transport at junctions via safe routes determined by the authorities.",
                                                "This mandatory sign channels dangerous-goods traffic at junctions via safe routes determined by the authorities.")
                                .replace("Priority over crossing side road (the traffic sign \"Give way\"5 series)",
                                                "Priority over crossing side road (priority-configuration signs)")
                                .replace("A-series", "danger-sign category")
                                .replace("B-series", "priority-sign category")
                                .replace("C-series", "prohibition-sign category")
                                .replace("D-series", "mandatory-sign category")
                                .replace("F-series", "information-sign category")
                                .replace("G-series", "supplementary-sign category")
                                .replace("E-series signs", "parking and stopping signs")
                                .replace("C-series sign", "prohibition sign")
                                .replace("first bend of the series", "first bend in the sequence")
                                .replace("for the entire series", "through all the bends")
                                .replace("entire series", "entire sequence")
                                .replace("entire series of bends", "entire sequence of bends")
                                .replace("series of bends", "sequence of bends")
                                .replace("throughout the series", "through all the bends")
                                .replace("Dangerous bend. Double bend or succession of more than two bends, the first to the left.",
                                                "Dangerous double bend or succession of dangerous bends, the first to the left.")
                                .replace("Dangerous bend. Double bend or succession of more than two bends, the first to the right.",
                                                "Dangerous double bend or succession of dangerous bends, the first to the right.")
                                .replace("Dangerous double or more bends, first to the left",
                                                "Dangerous double bend or succession of bends, first to the left")
                                .replace("Dangerous double or more bends, first to the right",
                                                "Dangerous double bend or succession of bends, first to the right")
                                .replace("Dangerous double or multiple curves, first to the left",
                                                "Dangerous double bend or succession of bends, first to the left")
                                .replace("Dangerous double or multiple curves, first to the right",
                                                "Dangerous double bend or succession of bends, first to the right")
                                .replace("B-series (priority signs):", "Priority signs:")
                                .replace("E-series (parking and stopping signs):", "Parking and stopping signs:")
                                .replace("F-series (information signs):", "Information signs:")
                                .replace(" series that opened the reserved road", " that opened the reserved road")
                                .replace(" signs from E-series", " parking and stopping signs");
                return normalizeQuotedCodeArtifacts(normalized, ENGLISH_TRAILING_CODE_PATTERN);
        }

        private static String normalizeDutchPatterns(String value) {
                String normalized = value
                                .replace("de reeks het verkeersbord", "het verkeersbord")
                                .replace("Tot welke reeks hoort", "Tot welke categorie behoort")
                                .replace("Tot welke reeks behoort", "Tot welke categorie behoort")
                                .replace(" is een gevaarsbord uit de A-reeks", " is een gevaarsbord")
                                .replace("A-reeks", "categorie gevaarsborden")
                                .replace("B-reeks", "categorie voorrangsborden")
                                .replace("C-reeks", "categorie verbodsborden")
                                .replace("D-reeks", "categorie gebodsborden")
                                .replace("F-reeks", "categorie informatieborden")
                                .replace("eerste bocht van de reeks", "eerste van de opeenvolgende bochten")
                                .replace("de volledige reeks", "alle opeenvolgende bochten")
                                .replace("voor de volledige reeks", "over alle opeenvolgende bochten")
                                .replace("reeks bochten", "opeenvolgende bochten")
                                .replace("door de volledige reeks", "door alle opeenvolgende bochten")
                                .replace("Waarschuwing voor een opeenvolgende gevaarlijke bochten, eerst naar links.",
                                                "Waarschuwing voor opeenvolgende gevaarlijke bochten, eerst naar links.")
                                .replace("Waarschuwing voor een opeenvolgende gevaarlijke bochten, eerst naar rechts.",
                                                "Waarschuwing voor opeenvolgende gevaarlijke bochten, eerst naar rechts.")
                                .replace("een opeenvolgende gevaarlijke bochten", "opeenvolgende gevaarlijke bochten")
                                .replace("een opeenvolgende bochten", "opeenvolgende bochten")
                                .replace("Gevaarlijke dubbele of meer dan twee bochten, de eerste naar links",
                                                "Gevaarlijke dubbele bocht of opeenvolgende bochten, de eerste naar links")
                                .replace("Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts",
                                                "Gevaarlijke dubbele bocht of opeenvolgende bochten, de eerste naar rechts")
                                .replace("Gevaarlijke dubbele bocht (eerste links)",
                                                "Gevaarlijke dubbele bocht of opeenvolgende bochten, de eerste naar links")
                                .replace("Gevaarlijke dubbele bocht (eerste rechts)",
                                                "Gevaarlijke dubbele bocht of opeenvolgende bochten, de eerste naar rechts")
                                .replace("C-reeksbord", "verbodsbord")
                                .replace("D-reeksborden", "gebodsborden")
                                .replace("G-reeks", "aanvullende en informatieve borden")
                                .replace("B-reeks (voorrangsborden):", "Voorrangsborden:")
                                .replace("E-reeks (parkeer- en stilstandsborden):", "Parkeer- en stilstandsborden:")
                                .replace("F-reeks (informatieve borden):", "Informatieborden:")
                                .replace("E-reeksborden", "parkeer- en stilstandsborden")
                                .replace("-reeks dat de voorbehouden weg opende", " dat de voorbehouden weg opende");
                return normalizeQuotedCodeArtifacts(normalized, DUTCH_TRAILING_CODE_PATTERN);
        }

        private static String normalizeFrenchPatterns(String value) {
                String normalized = value
                                .replace("la serie le panneau", "le panneau")
                                .replace("la série le panneau", "le panneau")
                                .replace("La serie E contient tous les panneaux de reglementation du stationnement.",
                                                "Ces panneaux réglementent le stationnement.")
                                .replace("La serie E contient tous les panneaux de reglementation du stationnement et de l'arret.",
                                                "Ces panneaux réglementent le stationnement et l'arrêt.")
                                .replace("La serie E contient tous les panneaux de reglementation de stationnement.",
                                                "Ces panneaux réglementent le stationnement.")
                                .replace("Les panneaux serie E9 reglementent le stationnement.",
                                                "Ces panneaux réglementent le stationnement.")
                                .replace("série Z", "panneaux de zone")
                                .replace("serie Z", "panneaux de zone")
                                .replace("série de zone", "catégorie des panneaux de zone")
                                .replace("serie de zone", "catégorie des panneaux de zone")
                                .replace("serie G", "panneaux additionnels")
                                .replace("série G", "panneaux additionnels")
                                .replace("A quelle serie appartient", "A quelle catégorie appartient")
                                .replace("A quelle série appartient", "A quelle catégorie appartient")
                                .replace("À quelle serie appartient", "À quelle catégorie appartient")
                                .replace(" est un panneau de danger de la serie A", " est un panneau de danger")
                                .replace("série A", "catégorie des panneaux de danger")
                                .replace("serie A", "catégorie des panneaux de danger")
                                .replace("série B", "catégorie des panneaux de priorité")
                                .replace("serie B", "catégorie des panneaux de priorité")
                                .replace("série C", "catégorie des panneaux d'interdiction")
                                .replace("serie C", "catégorie des panneaux d'interdiction")
                                .replace("série D", "catégorie des panneaux d'obligation")
                                .replace("serie D", "catégorie des panneaux d'obligation")
                                .replace("série F", "catégorie des panneaux d'information")
                                .replace("serie F", "catégorie des panneaux d'information")
                                .replace("Serie B (panneaux de priorite):", "Panneaux de priorité:")
                                .replace("Série B (panneaux de priorité):", "Panneaux de priorité:")
                                .replace("Serie E (panneaux de stationnement et d arret):",
                                                "Panneaux de stationnement et d'arrêt :")
                                .replace("Série E (panneaux de stationnement et d'arrêt):",
                                                "Panneaux de stationnement et d'arrêt :")
                                .replace("Serie F (panneaux d information):", "Panneaux d'information :")
                                .replace("Série F (panneaux d'information):", "Panneaux d'information :")
                                .replace("appartient a la serie G (panneaux additionnels et informatifs)",
                                                "appartient aux panneaux additionnels et informatifs")
                                .replace("appartient à la série G (panneaux additionnels et informatifs)",
                                                "appartient aux panneaux additionnels et informatifs")
                                .replace("parce que c'est un panneau de la série C",
                                                "parce que c'est un panneau d'interdiction")
                                .replace("pour toute la succession de virages", "pour l'ensemble des virages")
                                .replace("pour toute la catégorie des panneaux d'information",
                                                "pour l'ensemble des virages")
                                .replace("premier virage de la série", "premier de la succession de virages")
                                .replace("sur toute la série", "sur l'ensemble des virages")
                                .replace("pour toute la série", "pour l'ensemble des virages")
                                .replace("Virage dangereux. Double virage ou succession de plus de deux virages, le premier à gauche.",
                                                "Double virage dangereux ou succession de virages dangereux, le premier à gauche.")
                                .replace("Virage dangereux. Double virage ou succession de plus de deux virages, le premier à droite.",
                                                "Double virage dangereux ou succession de virages dangereux, le premier à droite.")
                                .replace("Double virage dangereux ou plus, le premier à gauche",
                                                "Double virage dangereux ou succession de virages, le premier à gauche")
                                .replace("Double virage dangereux ou plus, le premier à droite",
                                                "Double virage dangereux ou succession de virages, le premier à droite")
                                .replace("Double virage dangereux ou plusieurs virages, le premier à gauche",
                                                "Double virage dangereux ou succession de virages, le premier à gauche")
                                .replace("Double virage dangereux ou plusieurs virages, le premier à droite",
                                                "Double virage dangereux ou succession de virages, le premier à droite")
                                .replace("Serie E", "Panneaux de stationnement et d'arrêt")
                                .replace("série E", "panneaux de stationnement et d'arrêt")
                                .replace("de la serie E", "des panneaux de stationnement et d'arrêt")
                                .replace("de la série E", "des panneaux de stationnement et d'arrêt")
                                .replace(" de la serie le panneau", " du panneau")
                                .replace(" de la série le panneau", " du panneau");
                return normalizeQuotedCodeArtifacts(normalized, FRENCH_TRAILING_CODE_PATTERN);
        }

        private static String normalizeQuotedCodeArtifacts(String value, Pattern pattern) {
                return pattern.matcher(value).replaceAll("$1");
        }

        public enum Language {
                NL,
                EN,
                FR,
                AR,
                UNKNOWN;

                public static Language fromCode(String code) {
                        if (code == null || code.isBlank()) {
                                return UNKNOWN;
                        }
                        return switch (code.trim().toUpperCase()) {
                                case "NL" -> NL;
                                case "EN" -> EN;
                                case "FR" -> FR;
                                case "AR" -> AR;
                                default -> UNKNOWN;
                        };
                }
        }

        private record Replacement(String from, String to) {
        }
}
