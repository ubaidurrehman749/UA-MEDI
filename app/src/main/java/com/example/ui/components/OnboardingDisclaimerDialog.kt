package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R

@Composable
fun OnboardingDisclaimerDialog(
    onAccept: () -> Unit,
    onAcceptWithDemoData: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* Require explicit user acknowledgment */ },
        title = {
            Text(
                text = "Welcome to HealthMate AI",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Image(
                    painter = painterResource(id = R.drawable.img_onboarding_welcome),
                    contentDescription = "HealthMate AI Welcome Illustration",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Your Intelligent Health Companion",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "• Educational Support Only: HealthMate AI helps organize health records, explain medical terms, prepare for doctor visits, and track wellness. It is NOT a doctor and never claims to diagnose, cure, or prescribe.",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "• Emergencies: Never use an AI tool during a medical emergency. Call 1122 / 112 / 911 or visit the nearest ER immediately.",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "• Privacy First: Your medications, journal logs, and appointments are stored locally on your device. Only prompts or reports you actively submit to AI tools are sent securely to Gemini via Firebase AI Logic.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onAccept,
                modifier = Modifier.testTag("onboarding_accept_button")
            ) {
                Text("I Understand & Agree")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onAcceptWithDemoData,
                modifier = Modifier.testTag("onboarding_demo_button")
            ) {
                Text("Agree + Load Demo")
            }
        }
    )
}
