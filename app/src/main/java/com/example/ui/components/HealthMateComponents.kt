package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.AppLanguage
import com.example.data.local.JournalEntryEntity
import com.example.ui.theme.EmergencyOnRedContainer
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedContainer

@Composable
fun MedicalDisclaimerBanner(
    language: AppLanguage,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val text = if (language == AppLanguage.URDU) {
        "طبی انتباہ: HealthMate AI صرف تعلیمی اور صحت کی تنظیم میں مدد کے لیے ہے—یہ طبی تشخیص، علاج یا نسخہ فراہم نہیں کرتا۔"
    } else {
        "Educational Support Only: HealthMate AI does not diagnose diseases or prescribe treatments. Always consult a qualified doctor for medical advice."
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = if (compact) 8.dp else 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Medical disclaimer",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun EmergencyAlertBanner(
    language: AppLanguage,
    onOpenEmergencyGuidance: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, EmergencyRed, MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(containerColor = EmergencyRedContainer),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Emergency,
                    contentDescription = "Emergency Warning",
                    tint = EmergencyRed,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (language == AppLanguage.URDU) {
                        "فوری ایمرجنسی انتباہ"
                    } else {
                        "Urgent Emergency Warning"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = EmergencyOnRedContainer,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (language == AppLanguage.URDU) {
                    "اگر سینے میں درد، سانس لینے میں شدید دشواری، فالج کی علامات یا شدید خون بہنے جیسی علامات ہوں تو فوراً قریبی ایمرجنسی یا 1122 / 112 / 911 پر رابطہ کریں۔"
                } else {
                    "If you or someone nearby has chest pain, severe breathing trouble, stroke signs, or uncontrolled bleeding, call local emergency services (1122 / 112 / 911) immediately."
                },
                style = MaterialTheme.typography.bodySmall,
                color = EmergencyOnRedContainer
            )
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onOpenEmergencyGuidance,
                colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                modifier = Modifier.testTag("emergency_banner_button")
            ) {
                Icon(Icons.Default.WarningAmber, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (language == AppLanguage.URDU) "ایمرجنسی گائیڈ کھولیں" else "Open Emergency Guide"
                )
            }
        }
    }
}

@Composable
fun LanguageTogglePill(
    language: AppLanguage,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = language == AppLanguage.URDU,
        onClick = onToggle,
        label = {
            Text(
                text = if (language == AppLanguage.ENGLISH) "EN / اردو" else "اردو / EN",
                style = MaterialTheme.typography.labelMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = "Toggle language",
                modifier = Modifier.size(16.dp)
            )
        },
        modifier = modifier.testTag("language_toggle_chip")
    )
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (!subtitle.isNullOrBlank()) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmptyStateCard(
    icon: ImageVector,
    title: String,
    description: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (actionLabel != null && onAction != null) {
                Spacer(Modifier.height(14.dp))
                Button(onClick = onAction) {
                    Text(actionLabel)
                }
            }
        }
    }
}

@Composable
fun WellnessTrendChart(
    entries: List<JournalEntryEntity>,
    modifier: Modifier = Modifier
) {
    val recent = entries.take(7).reversed()
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = modifier.fillMaxWidth(),
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
                Text(
                    text = "Sleep & Hydration Trend (Recent Logs)",
                    style = MaterialTheme.typography.titleSmall
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LegendDot(color = primaryColor, label = "Sleep (h)")
                    Spacer(Modifier.width(10.dp))
                    LegendDot(color = secondaryColor, label = "Water")
                }
            }
            Spacer(Modifier.height(12.dp))

            if (recent.isEmpty()) {
                Text(
                    text = "Add journal entries or load demo data to view sleep and water trends.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    val count = recent.size
                    val slotWidth = size.width / count.coerceAtLeast(1)
                    val barWidth = (slotWidth * 0.34f).coerceAtLeast(10f)
                    val maxSleep = 10f
                    val maxWater = 12f
                    val chartBottom = size.height - 8f

                    // Baseline
                    drawLine(
                        color = trackColor,
                        start = Offset(0f, chartBottom),
                        end = Offset(size.width, chartBottom),
                        strokeWidth = 2f
                    )

                    val waterLinePath = Path()
                    recent.forEachIndexed { idx, entry ->
                        val centerX = slotWidth * idx + slotWidth / 2f
                        val sleepRatio = (entry.sleepHours / maxSleep).coerceIn(0.05f, 1f)
                        val barHeight = sleepRatio * (chartBottom - 14f)
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                listOf(primaryColor, primaryColor.copy(alpha = 0.65f))
                            ),
                            topLeft = Offset(centerX - barWidth / 2f, chartBottom - barHeight),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(8f, 8f)
                        )

                        val waterRatio = (entry.waterGlasses / maxWater).coerceIn(0.05f, 1f)
                        val waterY = chartBottom - waterRatio * (chartBottom - 14f)
                        if (idx == 0) waterLinePath.moveTo(centerX, waterY)
                        else waterLinePath.lineTo(centerX, waterY)
                    }

                    if (recent.size > 1) {
                        drawPath(
                            path = waterLinePath,
                            color = secondaryColor,
                            style = Stroke(width = 4f, cap = StrokeCap.Round)
                        )
                    }
                    recent.forEachIndexed { idx, entry ->
                        val centerX = slotWidth * idx + slotWidth / 2f
                        val waterRatio = (entry.waterGlasses / maxWater).coerceIn(0.05f, 1f)
                        val waterY = chartBottom - waterRatio * (chartBottom - 14f)
                        drawCircle(color = Color.White, radius = 7f, center = Offset(centerX, waterY))
                        drawCircle(color = secondaryColor, radius = 4.5f, center = Offset(centerX, waterY))
                    }
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    recent.forEach { entry ->
                        Text(
                            text = entry.dateLabel.take(6),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}
