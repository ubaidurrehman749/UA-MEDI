package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.AppLanguage
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.components.SectionHeader

@Composable
fun AboutScreen(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(modifier = Modifier.widthIn(max = 720.dp)) {
                SectionHeader(
                    title = "About HealthMate AI",
                    subtitle = "Your Intelligent Health Companion • Version 1.0"
                )
            }
        }

        item {
            Card(
                modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.img_onboarding_welcome),
                        contentDescription = "HealthMate AI Companion",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Text("Mission", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "HealthMate AI helps patients, caregivers, and families in Pakistan and worldwide understand general health topics, read medical terminology in plain English or Urdu, organize medications and wellness logs, and prepare confidently for doctor consultations.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("Mandatory Medical Safety Charter", style = MaterialTheme.typography.titleMedium)
                    Text("• Educational and organizational support only — never a substitute for licensed physicians.", style = MaterialTheme.typography.bodySmall)
                    Text("• Never claims to diagnose, treat, cure, or rule out diseases.", style = MaterialTheme.typography.bodySmall)
                    Text("• Never prescribes medications or adjusts prescribed dosages.", style = MaterialTheme.typography.bodySmall)
                    Text("• Highlights emergency warning signs and directs users to immediate care (1122 / 112 / 911).", style = MaterialTheme.typography.bodySmall)
                    Text("• Built with local Room SQLite privacy and Firebase AI Logic App Check protection.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        item {
            MedicalDisclaimerBanner(
                language = language,
                modifier = Modifier.widthIn(max = 720.dp)
            )
        }
    }
}
