package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.AppLanguage
import com.example.ui.components.SectionHeader
import com.example.ui.theme.EmergencyOnRedContainer
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedContainer

private data class EmergencySignItem(
    val titleEn: String,
    val titleUr: String,
    val detailsEn: String,
    val detailsUr: String
)

@Composable
fun EmergencyGuidanceScreen(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isUrdu = language == AppLanguage.URDU

    val itemsList = listOf(
        EmergencySignItem(
            "Chest Pain or Pressure",
            "سینے میں شدید درد یا دباؤ",
            "Squeezing, fullness, or pain in the center/left chest—especially if spreading to arm, jaw, neck, or back with sweating or nausea.",
            "سینے کے درمیان یا بائیں جانب دباؤ، جکڑن یا درد جو بازو، جبڑے یا کمر تک پھیلے، خاص طور پر پسینے یا متلی کے ساتھ۔"
        ),
        EmergencySignItem(
            "Severe Breathing Difficulty",
            "سانس لینے میں شدید دشواری",
            "Struggling to catch breath at rest, inability to speak full sentences, bluish lips, or sudden choking.",
            "آرام کی حالت میں سانس پھولنا، بات مکمل نہ کر سکنا، یا ہونٹوں کا نیلا پڑنا۔"
        ),
        EmergencySignItem(
            "Stroke Warning Signs (FAST)",
            "فالج کی ہنگامی علامات",
            "Face drooping on one side, Arm weakness/numbness, Speech slurring or confusion — Time to call emergency services immediately.",
            "چہرے کا ایک طرف جھک جانا، بازو میں کمزوری، یا بولنے میں لڑکھڑاہٹ—فوراً ایمرجنسی مدد طلب کریں۔"
        ),
        EmergencySignItem(
            "Severe Allergic Reaction (Anaphylaxis)",
            "شدید الرجی کا ردعمل",
            "Swelling of lips, tongue, or throat, widespread hives, dizziness, or breathing trouble after food, sting, or medicine.",
            "ہونٹوں، زبان یا گلے میں سوجن، سانس کی تنگی یا شدید چکر آنا۔"
        ),
        EmergencySignItem(
            "Uncontrolled Bleeding",
            "بے قابو خون بہنا",
            "Deep wounds or heavy bleeding that does not slow down after 10 minutes of firm continuous pressure.",
            "گہرا زخم یا مسلسل دباؤ کے باوجود نہ رکنے والا شدید خون بہاؤ۔"
        ),
        EmergencySignItem(
            "Loss of Consciousness or Seizures",
            "بے ہوشی یا دورہ پڑنا",
            "Fainting with slow recovery, unresponsiveness, sudden confusion, or active convulsions.",
            "اچانچ بے ہوش ہو جانا، ہوش میں نہ آنا، یا جھٹکے/دورہ پڑنا۔"
        ),
        EmergencySignItem(
            "Severe Injuries & Head Trauma",
            "شدید چوٹ یا سر پر زخم",
            "Major road accidents, suspected spinal/neck injury, severe burns, or head injury with vomiting/drowsiness.",
            "شدید حادثہ، سر کی چوٹ، ریڑھ کی ہڈی کا ممکنہ نقصان یا شدید جھلس جانا۔"
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(modifier = Modifier.widthIn(max = 720.dp)) {
                SectionHeader(
                    title = if (isUrdu) "ہنگامی طبی رہنمائی" else "Emergency Health Guidance",
                    subtitle = if (isUrdu) "AI کبھی بھی ایمرجنسی سروس کا متبادل نہیں ہے" else "Never use the AI assistant as an emergency response replacement"
                )
                Spacer(Modifier.height(10.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = EmergencyRedContainer),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Emergency, contentDescription = null, tint = EmergencyRed)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (isUrdu) "فوری ایمرجنسی ہیلپ لائنز" else "Call Local Emergency Services Immediately",
                                style = MaterialTheme.typography.titleMedium,
                                color = EmergencyOnRedContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = {
                                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:1122")))
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                                modifier = Modifier.weight(1f).testTag("dial_1122_btn")
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("1122 (PK)")
                            }
                            OutlinedButton(
                                onClick = {
                                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:112")))
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("112")
                            }
                            OutlinedButton(
                                onClick = {
                                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:911")))
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("911")
                            }
                        }
                    }
                }
            }
        }

        items(itemsList) { item ->
            Card(
                modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.WarningAmber, contentDescription = null, tint = EmergencyRed, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(if (isUrdu) item.titleUr else item.titleEn, style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(4.dp))
                        Text(if (isUrdu) item.detailsUr else item.detailsEn, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
