package com.example.data.education

data class EducationFaq(
    val questionEn: String,
    val answerEn: String,
    val questionUr: String,
    val answerUr: String
)

data class EducationArticle(
    val id: String,
    val category: String,
    val titleEn: String,
    val titleUr: String,
    val summaryEn: String,
    val summaryUr: String,
    val bodyEn: String,
    val bodyUr: String,
    val faqs: List<EducationFaq>,
    val sourceName: String,
    val sourceUrl: String,
    val dateChecked: String = "Nov 2026"
)

object HealthEducationCatalog {

    val categories: List<String> = listOf(
        "All",
        "General Health",
        "Nutrition",
        "Sleep",
        "Mental Wellness",
        "Physical Fitness",
        "Heart Health",
        "Diabetes Awareness",
        "Kidney Health",
        "Preventive Healthcare"
    )

    val dailyTipsEn: List<String> = listOf(
        "Drink water steadily across the day—especially in warm climates—rather than waiting until you feel very thirsty.",
        "Aim for 7–9 hours of consistent sleep; keeping a fixed wake-up time helps regulate your body clock.",
        "Bring a written list of current medicines and questions to every doctor visit so nothing gets forgotten.",
        "Even a brisk 20–30 minute walk most days supports heart health, blood sugar balance, and mood.",
        "Wash hands with soap and water for at least 20 seconds before meals to lower everyday infection risk."
    )

    val dailyTipsUr: List<String> = listOf(
        "Din bhar thora thora pani peete rahein—khaas taur par garm mausam mein—piyas lagne ka intezar na karein.",
        "Rozana 7 se 9 ghante ki neend aur muqarrar waqt par jaagne ki aadat sehat ke liye mufeed hai.",
        "Doctor ke paas jaate waqt apni dawaon aur sawalat ki list saath le kar jayein.",
        "Rozana 20 se 30 minute tez chalna dil ki sehat, blood sugar aur mood ke liye behtareen hai.",
        "Khane se pehle kam az kam 20 second sabun se haath dhona aam bimariyon se bachata hai."
    )

    val articles: List<EducationArticle> = listOf(
        EducationArticle(
            id = "gen_hydration",
            category = "General Health",
            titleEn = "Hydration, Heat & Everyday Wellness",
            titleUr = "پانی کی کمی سے بچاؤ اور روزمرہ صحت",
            summaryEn = "How fluid balance supports energy, temperature control, and kidney function.",
            summaryUr = "جسم میں پانی کا توازن توانائی، درجہ حرارت اور گردوں کی صحت کے لیے کیوں ضروری ہے۔",
            bodyEn = "Water makes up a major part of the human body and helps carry nutrients, regulate body temperature, and cushion joints. Mild dehydration can cause fatigue, headache, dry mouth, and dark-colored urine. In hot weather or during fever and diarrhea, fluid losses rise quickly. Plain clean water is usually the best daily choice, while oral rehydration solution (ORS) is recommended during diarrhea or heavy fluid loss. Individuals with heart failure or advanced kidney disease should follow their physician's specific fluid limits.",
            bodyUr = "پانی جسم کے درجہ حرارت کو متوازن رکھنے، غذائی اجزاء پہنچانے اور تھکن سے بچانے میں اہم کردار ادا کرتا ہے۔ پانی کی ہلکی کمی بھی سر درد، سستی، منہ خشک ہونے اور گہرے رنگ کے پیشاب کا باعث بن سکتی ہے۔ گرمی، بخار یا اسہال کی صورت میں نمکیات اور پانی (ORS) کا استعمال فائدہ مند ہوتا ہے۔ دل یا گردے کے مرض میں مبتلا افراد ڈاکٹر کی ہدایت کے مطابق پانی کی مقدار مقرر کریں۔",
            faqs = listOf(
                EducationFaq(
                    questionEn = "How can I tell if I am drinking enough water?",
                    answerEn = "Pale yellow urine and rarely feeling thirsty are practical everyday signs of good hydration.",
                    questionUr = "کیسے معلوم ہو کہ میں مناسب پانی پی رہا ہوں؟",
                    answerUr = "ہلکے زرد رنگ کا پیشاب اور بار بار پیاس نہ لگنا مناسب ہائیڈریشن کی عام علامات ہیں۔"
                )
            ),
            sourceName = "World Health Organization (WHO)",
            sourceUrl = "https://www.who.int/news-room/fact-sheets/detail/drinking-water"
        ),
        EducationArticle(
            id = "nut_balanced",
            category = "Nutrition",
            titleEn = "Building a Balanced Everyday Plate",
            titleUr = "متوازن غذا اور صحت بخش کھانے کی عادات",
            summaryEn = "Practical portion guidance with vegetables, whole grains, lean protein, and healthy fats.",
            summaryUr = "سبزیوں، مکمل اناج، پروٹین اور صحت بخش چکنائی کے ساتھ روزمرہ متوازن پلیٹ کیسے بنائیں۔",
            bodyEn = "A balanced diet protects against malnutrition and lowers long-term risk of diabetes, heart disease, and hypertension. Aim to fill half your plate with colorful vegetables and whole fruits, a quarter with whole grains (such as whole-wheat roti, brown rice, or oats), and a quarter with protein sources (lentils/daal, beans, fish, eggs, or lean poultry). Limit excess added sugars, deep-fried snacks, and high-sodium packaged foods.",
            bodyUr = "متوازن غذا ذیابیطس، ہائی بلڈ پریشر اور دل کی بیماریوں کے خطرے کو کم کرتی ہے۔ اپنی پلیٹ کا آدھا حصہ سبزیوں اور پھلوں، ایک چوتھائی مکمل اناج (چکی کے آٹے کی روٹی، جو یا براؤن چاول) اور ایک چوتھائی پروٹین (دالیں، چنے، مچھلی، انڈے یا مرغی) سے بھریں۔ زیادہ چینی، تلی ہوئی اشیاء اور اضافی نمک سے پرہیز کریں۔",
            faqs = listOf(
                EducationFaq(
                    questionEn = "How much salt is generally recommended per day?",
                    answerEn = "WHO recommends less than 5 grams of salt (about one teaspoon) per day for most adults.",
                    questionUr = "روزانہ نمک کی کتنی مقدار مناسب ہے؟",
                    answerUr = "عالمی ادارہ صحت بالغ افراد کے لیے روزانہ 5 گرام (تقریباً ایک چائے کا چمچ) سے کم نمک تجویز کرتا ہے۔"
                )
            ),
            sourceName = "WHO Healthy Diet Fact Sheet",
            sourceUrl = "https://www.who.int/news-room/fact-sheets/detail/healthy-diet"
        ),
        EducationArticle(
            id = "sleep_hygiene",
            category = "Sleep",
            titleEn = "Healthy Sleep Habits for Adults",
            titleUr = "پرسکون اور صحت بخش نیند کے اصول",
            summaryEn = "Why 7+ hours of regular, restorative sleep supports memory, immunity, and metabolism.",
            summaryUr = "روزانہ 7 گھنٹے یا اس سے زیادہ باقاعدہ نیند دماغی تازگی اور مدافعتی نظام کے لیے کیوں ضروری ہے۔",
            bodyEn = "Quality sleep is essential for brain recovery, blood pressure regulation, blood sugar balance, and emotional resilience. Most adults need 7 to 9 hours per night. Helpful sleep hygiene habits include waking up at the same time daily, keeping your bedroom dark and quiet, dimming bright screens 30–60 minutes before bed, and avoiding heavy caffeine late in the afternoon.",
            bodyUr = "معیاری نیند دماغ، بلڈ پریشر، شوگر اور جذباتی توازن کے لیے بنیادی اہمیت رکھتی ہے۔ زیادہ تر بالغ افراد کو رات میں 7 سے 9 گھنٹے نیند کی ضرورت ہوتی ہے۔ سونے اور جاگنے کا وقت مقرر رکھیں، کمرہ پرسکون اور تاریک رکھیں اور سونے سے کم از کم آدھا گھنٹہ پہلے موبائل اسکرین بند کر دیں۔",
            faqs = listOf(
                EducationFaq(
                    questionEn = "When should I talk to a doctor about sleep trouble?",
                    answerEn = "Consult a doctor if poor sleep lasts several weeks, causes daytime sleepiness, or includes loud snoring with breathing pauses.",
                    questionUr = "نیند کے مسائل پر ڈاکٹر سے کب رجوع کرنا چاہیے؟",
                    answerUr = "اگر نیند کی خرابی کئی ہفتوں تک رہے، دن میں شدید غنودگی ہو یا سوتے وقت سانس رکنے کی شکایت ہو تو ڈاکٹر سے مشورہ کریں۔"
                )
            ),
            sourceName = "CDC Sleep and Sleep Disorders",
            sourceUrl = "https://www.cdc.gov/sleep/about/index.html"
        ),
        EducationArticle(
            id = "mental_stress",
            category = "Mental Wellness",
            titleEn = "Recognizing & Managing Everyday Stress",
            titleUr = "ذہنی دباؤ کی پہچان اور بہتر انتظام",
            summaryEn = "Practical breathing, social connection, and knowing when to seek professional support.",
            summaryUr = "ذہنی تناؤ کم کرنے کی عملی تدابیر اور ماہر سے مدد لینے کا صحیح وقت۔",
            bodyEn = "Ongoing stress can show up physically as muscle tension, stomach upset, poor sleep, or irritability. Evidence-based ways to cope include slow deep breathing, regular walking, staying connected with trusted family or friends, and taking breaks from distressing news feeds. If sadness, panic, or overwhelm interferes with daily life for more than two weeks, reaching out to a healthcare provider or counselor is a positive step.",
            bodyUr = "مسلسل ذہنی دباؤ جسمانی علامات جیسے پٹھوں میں کھنچاؤ، معدے کی خرابی، نیند کی کمی اور چڑچڑاپن کی صورت میں ظاہر ہو سکتا ہے۔ گہری سانس لینے کی مشق، باقاعدہ چہل قدمی اور دوستوں یا خاندان سے بات چیت مددگار ثابت ہوتی ہے۔ اگر اداسی یا گھبراہٹ دو ہفتوں سے زیادہ روزمرہ زندگی کو متاثر کرے تو معالج سے رابطہ کریں۔",
            faqs = listOf(
                EducationFaq(
                    questionEn = "Can breathing exercises really calm acute stress?",
                    answerEn = "Yes, slow paced breathing (such as inhaling 4 seconds and exhaling 6 seconds) activates the body's relaxation response.",
                    questionUr = "کیا سانس کی مشق واقعی ذہنی دباؤ کم کرتی ہے؟",
                    answerUr = "جی ہاں، آہستہ اور گہری سانس لینا (4 سیکنڈ سانس اندر اور 6 سیکنڈ باہر) اعصابی نظام کو پرسکون کرتا ہے۔"
                )
            ),
            sourceName = "WHO Mental Health Fact Sheet",
            sourceUrl = "https://www.who.int/news-room/fact-sheets/detail/mental-health-strengthening-our-response"
        ),
        EducationArticle(
            id = "fitness_activity",
            category = "Physical Fitness",
            titleEn = "Weekly Physical Activity Guidelines",
            titleUr = "ہفتہ وار جسمانی سرگرمی اور ورزش کی رہنمائی",
            summaryEn = "Aiming for 150 minutes of moderate movement per week plus muscle-strengthening days.",
            summaryUr = "ہفتے میں 150 منٹ معتدل جسمانی سرگرمی دل، پٹھوں اور ہڈیوں کو مضبوط رکھتی ہے۔",
            bodyEn = "WHO guidelines recommend adults aim for 150–300 minutes of moderate-intensity aerobic activity (like brisk walking or cycling) weekly, along with muscle-strengthening activities on 2 or more days. Every bit of movement counts—even 10-minute walks after meals help glucose control and reduce sitting time. Start gradually if you have been inactive or have chronic joint or heart conditions.",
            bodyUr = "عالمی ادارہ صحت کے مطابق بالغ افراد کو ہفتے میں 150 سے 300 منٹ معتدل ورزش (جیسے تیز چلنا یا سائیکل چلانا) اور ہفتے میں کم از کم 2 دن پٹھوں کو مضبوط کرنے والی ورزش کرنی چاہیے۔ کھانے کے بعد 10 منٹ کی چہل قدمی بھی بلڈ شوگر کو بہتر رکھنے میں مدد دیتی ہے۔",
            faqs = listOf(
                EducationFaq(
                    questionEn = "Do I need a gym membership to stay active?",
                    answerEn = "No—brisk walking, stair climbing, household movement, and bodyweight exercises are highly effective.",
                    questionUr = "کیا فٹ رہنے کے لیے جم جانا ضروری ہے؟",
                    answerUr = "نہیں، تیز چہل قدمی، سیڑھیاں چڑھنا اور گھر پر ہلکی ورزشیں بھی بہت مؤثر ہیں۔"
                )
            ),
            sourceName = "WHO Physical Activity",
            sourceUrl = "https://www.who.int/news-room/fact-sheets/detail/physical-activity"
        ),
        EducationArticle(
            id = "heart_bp",
            category = "Heart Health",
            titleEn = "Understanding Blood Pressure & Heart Health",
            titleUr = "بلڈ پریشر اور دل کی صحت کو سمجھنا",
            summaryEn = "Why blood pressure is called the silent risk factor and how routine checks protect your heart.",
            summaryUr = "بلڈ پریشر کی باقاعدہ جانچ دل اور فالج سے بچاؤ کے لیے کیوں ضروری ہے۔",
            bodyEn = "High blood pressure (hypertension) often causes no obvious symptoms, which is why regular blood pressure checks are essential. A typical resting reading near 120/80 mmHg is considered normal for many adults, while consistently elevated readings (140/90 mmHg or higher in clinic settings) require medical evaluation. Heart-healthy habits include reducing dietary salt, avoiding tobacco, staying active, managing stress, and taking prescribed medicines consistently.",
            bodyUr = "ہائی بلڈ پریشر اکثر بغیر کسی واضح علامت کے بڑھتا رہتا ہے، اس لیے باقاعدہ چیک اپ ضروری ہے۔ عام طور پر آرام کی حالت میں 120/80 mmHg نارمل شمار ہوتا ہے، جبکہ مسلسل بڑھا ہوا بلڈ پریشر ڈاکٹر کے معائنے کا متقاضی ہے۔ نمک کم کھانا، تمباکو نوشی سے پرہیز اور باقاعدہ چہل قدمی دل کی حفاظت کرتے ہیں۔",
            faqs = listOf(
                EducationFaq(
                    questionEn = "How should I measure blood pressure at home?",
                    answerEn = "Rest quietly for 5 minutes, sit with your back supported and feet flat, and keep your arm supported at heart level.",
                    questionUr = "گھر پر بلڈ پریشر ناپنے کا درست طریقہ کیا ہے؟",
                    answerUr = "5 منٹ پرسکون بیٹھنے کے بعد کمر سیدھی رکھیں، پاؤں زمین پر ہوں اور بازو دل کی سطح پر سہارا دے کر ناپیں۔"
                )
            ),
            sourceName = "WHO Hypertension Fact Sheet",
            sourceUrl = "https://www.who.int/news-room/fact-sheets/detail/hypertension"
        ),
        EducationArticle(
            id = "diabetes_aware",
            category = "Diabetes Awareness",
            titleEn = "Diabetes Basics & Blood Sugar Monitoring",
            titleUr = "ذیابیطس سے آگاہی اور بلڈ شوگر کی دیکھ بھال",
            summaryEn = "Recognizing early warning signs, HbA1c basics, and everyday lifestyle pillars.",
            summaryUr = "ذیابیطس کی ابتدائی علامات، HbA1c ٹیسٹ اور روزمرہ احتیاطی تدابیر۔",
            bodyEn = "Diabetes occurs when blood glucose remains elevated over time. Common early symptoms include increased thirst, frequent urination, unexplained fatigue, and blurred vision—though Type 2 diabetes can also develop silently. Doctors commonly use fasting blood glucose and HbA1c (which reflects average blood sugar over 2–3 months) for assessment. Balanced fiber-rich meals, portion control, regular walking, and routine eye/foot/kidney checkups are key pillars of diabetes care.",
            bodyUr = "ذیابیطس اس وقت ہوتی ہے جب خون میں شوگر کی مقدار مسلسل زیادہ رہے۔ بار بار پیشاب آنا، شدید پیاس لگنا، غیر معمولی تھکن اور نظر کا دھندلانا اس کی عام علامات ہو سکتی ہیں۔ ڈاکٹر عموماً فاسٹنگ شوگر اور HbA1c (گزشتہ 2 سے 3 ماہ کی اوسط شوگر) ٹیسٹ کرواتے ہیں۔ فائبر سے بھرپور غذا اور باقاعدہ واک بے حد مفید ہے۔",
            faqs = listOf(
                EducationFaq(
                    questionEn = "What is an HbA1c test?",
                    answerEn = "HbA1c estimates your average blood sugar level over the past 2 to 3 months without needing fasting.",
                    questionUr = "HbA1c ٹیسٹ کیا ہوتا ہے؟",
                    answerUr = "یہ ٹیسٹ گزشتہ 2 سے 3 مہینوں کی اوسط بلڈ شوگر کو ظاہر کرتا ہے اور اس کے لیے خالی پیٹ رہنا ضروری نہیں ہوتا۔"
                )
            ),
            sourceName = "WHO Diabetes Fact Sheet",
            sourceUrl = "https://www.who.int/news-room/fact-sheets/detail/diabetes"
        ),
        EducationArticle(
            id = "kidney_care",
            category = "Kidney Health",
            titleEn = "Protecting Your Kidneys Every Day",
            titleUr = "گردوں کی حفاظت کے اہم اصول",
            summaryEn = "How blood pressure, blood sugar, hydration, and cautious painkiller use affect kidney health.",
            summaryUr = "بلڈ پریشر، شوگر کا کنٹرول اور درد کی دواؤں کا غیر ضروری استعمال نہ کرنا گردوں کو کیسے بچاتا ہے۔",
            bodyEn = "Your kidneys filter waste and excess fluid from the blood every day. The two leading drivers of chronic kidney disease worldwide are uncontrolled high blood pressure and diabetes. Protecting your kidneys includes controlling blood pressure and blood sugar, staying appropriately hydrated, avoiding unnecessary over-the-counter NSAID painkillers without medical advice, and asking your doctor about routine urine albumin and blood creatinine tests if you have risk factors.",
            bodyUr = "گردے روزانہ خون سے فاسد مادے اور اضافی پانی صاف کرتے ہیں۔ گردوں کی دائمی بیماری کی دو بڑی وجوہات بے قابو ہائی بلڈ پریشر اور ذیابیطس ہیں۔ گردوں کی حفاظت کے لیے شوگر اور بلڈ پریشر کنٹرول میں رکھیں اور ڈاکٹر کے مشورے کے بغیر درد کش ادویات (painkillers) کا زیادہ استعمال نہ کریں۔",
            faqs = listOf(
                EducationFaq(
                    questionEn = "Why can frequent unmonitored painkillers affect kidneys?",
                    answerEn = "Certain NSAID pain relievers reduce blood flow to the kidneys when taken frequently, especially in dehydration or older age.",
                    questionUr = "بغیر مشورے درد کی گولیاں گردوں کو نقصان کیوں پہنچا سکتی ہیں؟",
                    answerUr = "کچھ درد کش ادویات (NSAIDs) کا بار بار استعمال گردوں میں خون کے بہاؤ کو کم کر کے نقصان پہنچا سکتا ہے۔"
                )
            ),
            sourceName = "NIH NIDDK Kidney Disease",
            sourceUrl = "https://www.niddk.nih.gov/health-information/kidney-disease/chronic-kidney-disease-ckd/prevention"
        ),
        EducationArticle(
            id = "preventive_checkups",
            category = "Preventive Healthcare",
            titleEn = "Preventive Checkups & Immunizations",
            titleUr = "حفاظتی ٹیکے اور سالانہ طبی معائنہ",
            summaryEn = "Staying ahead with routine screenings, vaccinations, and organized medical records.",
            summaryUr = "بیماری سے بچاؤ کے لیے باقاعدہ اسکریننگ، ویکسینیشن اور میڈیکل ریکارڈ محفوظ رکھنا۔",
            bodyEn = "Preventive healthcare aims to catch silent health changes early before complications develop. Depending on age, family history, and local clinical guidelines, doctors may recommend regular blood pressure checks, cholesterol and blood sugar screening, dental and vision exams, and age-appropriate vaccinations. Keeping past lab reports and medication lists organized makes every consultation faster and safer.",
            bodyUr = "حفاظتی صحت کا مقصد بیماری کو پیچیدہ ہونے سے پہلے ابتدائی مرحلے میں پکڑنا ہے۔ عمر اور خاندانی تاریخ کے مطابق بلڈ پریشر، کولیسٹرول، شوگر، آنکھوں اور دانتوں کا باقاعدہ معائنہ اور ضروری ویکسینیشن صحت کو محفوظ رکھتی ہے۔ اپنی پرانی رپورٹس اور دواؤں کا ریکارڈ منظم رکھیں۔",
            faqs = listOf(
                EducationFaq(
                    questionEn = "Should I keep copies of older lab reports even when normal?",
                    answerEn = "Yes—having past normal baselines helps your doctor compare changes accurately over time.",
                    questionUr = "کیا نارمل آنے والی پرانی لیب رپورٹس بھی سنبھالنی چاہئیں؟",
                    answerUr = "جی ہاں، پرانی رپورٹس ڈاکٹر کو وقت کے ساتھ آنے والی تبدیلیوں کا موازنہ کرنے میں مدد دیتی ہیں۔"
                )
            ),
            sourceName = "MedlinePlus Health Screening",
            sourceUrl = "https://medlineplus.gov/healthscreening.html"
        )
    )
}
