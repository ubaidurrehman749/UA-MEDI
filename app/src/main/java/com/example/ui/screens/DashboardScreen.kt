package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.education.HealthEducationCatalog
import com.example.data.local.AppLanguage
import com.example.data.local.AppointmentEntity
import com.example.data.local.JournalEntryEntity
import com.example.data.local.MedicationEntity
import com.example.data.local.MedicationLogEntity
import com.example.data.local.UserPreferences
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.components.SectionHeader
import com.example.ui.components.WellnessTrendChart
import com.example.ui.theme.EmergencyOnRedContainer
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedContainer
import java.util.Calendar

data class QuickToolItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String,
    val accentColor: Color
)

@Composable
fun DashboardScreen(
    preferences: UserPreferences,
    medications: List<MedicationEntity>,
    medicationLogs: List<MedicationLogEntity>,
    journalEntries: List<JournalEntryEntity>,
    appointments: List<AppointmentEntity>,
    onNavigate: (String) -> Unit,
    onMarkMedTaken: (MedicationEntity) -> Unit,
    onLoadDemoData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUrdu = preferences.language == AppLanguage.URDU
    val greetingName = preferences.displayName.ifBlank { if (isUrdu) "دوست" else "Friend" }
    val tipIndex = remember { Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % HealthEducationCatalog.dailyTipsEn.size }
    val dailyTip = if (isUrdu) HealthEducationCatalog.dailyTipsUr[tipIndex] else HealthEducationCatalog.dailyTipsEn[tipIndex]

    val tools = listOf(
        QuickToolItem(
            title = if (isUrdu) "AI ہیلتھ اسسٹنٹ" else "AI Assistant",
            subtitle = if (isUrdu) "انگریزی اور اردو" else "EN / Urdu Q&A",
            icon = Icons.AutoMirrored.Filled.Chat,
            route = Routes.ASSISTANT,
            accentColor = MaterialTheme.colorScheme.primary
        ),
        QuickToolItem(
            title = if (isUrdu) "علامات چیکر" else "Symptom Check",
            subtitle = if (isUrdu) "رہنمائی اور احتیاط" else "Structured Guide",
            icon = Icons.Default.MonitorHeart,
            route = Routes.SYMPTOM_CHECKER,
            accentColor = MaterialTheme.colorScheme.secondary
        ),
        QuickToolItem(
            title = if (isUrdu) "رپورٹ تجزیہ" else "Report Analyzer",
            subtitle = if (isUrdu) "PDF اور تصویر" else "PDF & Lab Images",
            icon = Icons.Default.Description,
            route = Routes.REPORT_ANALYZER,
            accentColor = MaterialTheme.colorScheme.tertiary
        ),
        QuickToolItem(
            title = if (isUrdu) "دواؤں کا ریکارڈ" else "Medications",
            subtitle = if (isUrdu) "یاد دہانی اور لاگ" else "${medications.size} active",
            icon = Icons.Default.Medication,
            route = Routes.MEDICATIONS,
            accentColor = MaterialTheme.colorScheme.primary
        ),
        QuickToolItem(
            title = if (isUrdu) "ہیلتھ جرنل" else "Health Journal",
            subtitle = if (isUrdu) "روزمرہ ٹریکنگ" else "${journalEntries.size} logs",
            icon = Icons.AutoMirrored.Filled.Assignment,
            route = Routes.JOURNAL,
            accentColor = MaterialTheme.colorScheme.secondary
        ),
        QuickToolItem(
            title = if (isUrdu) "ڈاکٹر وزٹ تیاری" else "Doctor Prep",
            subtitle = if (isUrdu) "سوالات و چیک لسٹ" else "${appointments.size} upcoming",
            icon = Icons.Default.CalendarMonth,
            route = Routes.APPOINTMENTS,
            accentColor = MaterialTheme.colorScheme.tertiary
        ),
        QuickToolItem(
            title = if (isUrdu) "صحت لائبریری" else "Health Library",
            subtitle = if (isUrdu) "مستند مضامین" else "9 Topics (EN/UR)",
            icon = Icons.Default.MenuBook,
            route = Routes.EDUCATION,
            accentColor = MaterialTheme.colorScheme.primary
        ),
        QuickToolItem(
            title = if (isUrdu) "ایمرجنسی گائیڈ" else "Emergency",
            subtitle = if (isUrdu) "فوری علامات و مدد" else "Red Flags & 1122",
            icon = Icons.Default.Emergency,
            route = Routes.EMERGENCY,
            accentColor = EmergencyRed
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Box(modifier = Modifier.widthIn(max = 720.dp)) {
                HeroWelcomeCard(
                    greetingName = greetingName,
                    isUrdu = isUrdu,
                    medCount = medications.size,
                    journalCount = journalEntries.size,
                    appointmentCount = appointments.size,
                    onAskAssistant = { onNavigate(Routes.ASSISTANT) }
                )
            }
        }

        item {
            Box(modifier = Modifier.widthIn(max = 720.dp)) {
                MedicalDisclaimerBanner(language = preferences.language, compact = true)
            }
        }

        // Sample Data Prompt if completely empty
        if (medications.isEmpty() && journalEntries.isEmpty() && appointments.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    shape = MaterialTheme.shapes.large
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Explore with Sample Health Data",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                text = "Populate realistic sample logs, medications, and a visit prep sheet locally.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        FilledTonalButton(
                            onClick = onLoadDemoData,
                            modifier = Modifier.testTag("dashboard_load_demo_btn")
                        ) {
                            Text("Load Demo")
                        }
                    }
                }
            }
        }

        // Quick Tools Grid (2 columns)
        item {
            Column(modifier = Modifier.widthIn(max = 720.dp)) {
                SectionHeader(
                    title = if (isUrdu) "صحت کے اہم ٹولز" else "Care & Organization Tools",
                    subtitle = if (isUrdu) "اپنی ضرورت کے مطابق ٹول منتخب کریں" else "Tap any companion module to begin"
                )
                Spacer(Modifier.height(10.dp))
                tools.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { item ->
                            QuickToolCard(
                                item = item,
                                onClick = { onNavigate(item.route) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
        }

        // Medication Overview Card
        item {
            Card(
                modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Medication, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (isUrdu) "آج کی ادویات" else "Medication Overview",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        OutlinedButton(onClick = { onNavigate(Routes.MEDICATIONS) }) {
                            Text(if (isUrdu) "سب دیکھیں" else "Manage")
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    if (medications.isEmpty()) {
                        Text(
                            text = if (isUrdu) "ابھی کوئی دوا شامل نہیں ہے۔" else "No active medication reminders yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        medications.take(3).forEach { med ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(med.name, style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        "${med.prescribedDosage} • ${med.reminderTime} (${med.frequency})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                FilledTonalButton(
                                    onClick = { onMarkMedTaken(med) },
                                    modifier = Modifier.testTag("dash_take_med_${med.id}")
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Taken")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Upcoming Appointment Card
        item {
            Card(
                modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (isUrdu) "آنے والی ڈاکٹر ملاقات" else "Upcoming Doctor Visit",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        OutlinedButton(onClick = { onNavigate(Routes.APPOINTMENTS) }) {
                            Text(if (isUrdu) "تیاری کریں" else "Prepare")
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    val nextApt = appointments.firstOrNull()
                    if (nextApt == null) {
                        Text(
                            text = if (isUrdu) "کوئی ملاقات درج نہیں۔ وزٹ سے پہلے سوالات تیار کرنے کے لیے شامل کریں۔" else "No appointments added yet. Prepare questions and checklists before your next consultation.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "${nextApt.doctorName} • ${nextApt.specialty}",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "${nextApt.dateTimeLabel} — ${nextApt.reason}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Journal Summary & Trend Chart
        item {
            Box(modifier = Modifier.widthIn(max = 720.dp)) {
                WellnessTrendChart(entries = journalEntries)
            }
        }

        // Daily Wellness Tip + Recent Activity
        item {
            Card(
                modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)),
                shape = MaterialTheme.shapes.large
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = "Daily tip", tint = Color.White)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isUrdu) "آج کا صحت مشورہ" else "Daily Wellness Tip",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = dailyTip,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        // Recent Health Activities
        item {
            Card(
                modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (isUrdu) "حالیہ سرگرمیاں" else "Recent Health Activity",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    if (medicationLogs.isEmpty() && journalEntries.isEmpty()) {
                        Text(
                            text = "Your medication logs and wellness entries will appear here.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        medicationLogs.take(2).forEach { log ->
                            Text(
                                text = "• ${log.medicationName} (${log.status}) — ${log.dateLabel}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                        journalEntries.take(2).forEach { entry ->
                            Text(
                                text = "• Journal (${entry.dateLabel}): Sleep ${entry.sleepHours}h, Water ${entry.waterGlasses} glasses, Mood: ${entry.mood}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // High-visibility Emergency Strip
        item {
            Card(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .clickable { onNavigate(Routes.EMERGENCY) },
                colors = CardDefaults.cardColors(containerColor = EmergencyRedContainer),
                shape = MaterialTheme.shapes.large
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Emergency, contentDescription = "Emergency", tint = EmergencyRed)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isUrdu) "ہنگامی طبی رہنمائی" else "Emergency Warning Signs",
                                style = MaterialTheme.typography.titleSmall,
                                color = EmergencyOnRedContainer
                            )
                            Text(
                                text = if (isUrdu) "فوری مدد کے لیے علامات اور ہیلپ لائن نمبر دیکھیں" else "Know when to call 1122 / 112 / 911 immediately",
                                style = MaterialTheme.typography.bodySmall,
                                color = EmergencyOnRedContainer
                            )
                        }
                    }
                    Button(
                        onClick = { onNavigate(Routes.EMERGENCY) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                    ) {
                        Text("View")
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroWelcomeCard(
    greetingName: String,
    isUrdu: Boolean,
    medCount: Int,
    journalCount: Int,
    appointmentCount: Int,
    onAskAssistant: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.img_dashboard_hero),
                contentDescription = "HealthMate AI Banner",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xCC064642),
                                Color(0xEE083B3E)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.18f)
                ) {
                    Text(
                        text = if (isUrdu) "آپ کا ذہین ہیلتھ ساتھی" else "Your Intelligent Health Companion",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = if (isUrdu) "خوش آمدید، $greetingName" else "Welcome back, $greetingName",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (isUrdu) {
                        "صحت کی معلومات سمجھیں، رپورٹس منظم کریں اور ڈاکٹر وزٹ کی تیاری کریں۔"
                    } else {
                        "Understand health topics, organize records, and prepare confidently for doctor visits."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        MiniStatBadge("$medCount", "Meds")
                        MiniStatBadge("$journalCount", "Logs")
                        MiniStatBadge("$appointmentCount", "Visits")
                    }
                    Button(
                        onClick = onAskAssistant,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF0A6E6B)
                        ),
                        modifier = Modifier.testTag("hero_ask_ai_button")
                    ) {
                        Text("Ask AI", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniStatBadge(value: String, label: String) {
    Column {
        Text(value, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
private fun QuickToolCard(
    item: QuickToolItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .testTag("quick_tool_${item.route}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(item.accentColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = item.accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
