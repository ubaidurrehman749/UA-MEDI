package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.ai.SymptomAssessmentInput
import com.example.data.ai.SymptomAssessmentResult
import com.example.data.local.AppLanguage
import com.example.ui.components.EmergencyAlertBanner
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.components.SectionHeader
import com.example.ui.state.AiUiState
import com.example.ui.theme.EmergencyOnRedContainer
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedContainer
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenContainer
import com.example.ui.theme.SafeOnGreenContainer
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer
import com.example.ui.theme.WarningOnAmberContainer

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SymptomCheckerScreen(
    language: AppLanguage,
    uiState: AiUiState<SymptomAssessmentResult>,
    localEmergencyDetected: Boolean,
    onCheckRedFlags: (String) -> Unit,
    onRunAssessment: (SymptomAssessmentInput) -> Unit,
    onReset: () -> Unit,
    onOpenEmergency: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUrdu = language == AppLanguage.URDU

    var age by remember { mutableStateOf("") }
    var biologicalSex by remember { mutableStateOf("Prefer not to say") }
    var mainSymptoms by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("1–3 days") }
    var severity by remember { mutableFloatStateOf(4f) }
    var associatedSymptoms by remember { mutableStateOf("") }
    var existingConditions by remember { mutableStateOf("") }
    var currentMedications by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    val sexOptions = listOf("Female", "Male", "Prefer not to say")
    val durationOptions = listOf("Today / Hours", "1–3 days", "4–7 days", "1+ weeks")

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
                    title = if (isUrdu) "اسمارٹ علامات چیکر" else "Smart Symptom Checker",
                    subtitle = if (isUrdu) {
                        "تعلیمی علامتی جائزہ — تشخیص یا نسخے کے لیے نہیں"
                    } else {
                        "Structured educational symptom overview — never a diagnosis"
                    }
                )
                Spacer(Modifier.height(8.dp))
                MedicalDisclaimerBanner(language = language)
            }
        }

        if (localEmergencyDetected) {
            item {
                EmergencyAlertBanner(
                    language = language,
                    onOpenEmergencyGuidance = onOpenEmergency,
                    modifier = Modifier.widthIn(max = 720.dp)
                )
            }
        }

        item {
            Card(
                modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = age,
                            onValueChange = { if (it.length <= 3) age = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Age (optional)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("symptom_age_input"),
                            singleLine = true
                        )
                    }

                    Text("Biological sex (if relevant)", style = MaterialTheme.typography.labelLarge)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        sexOptions.forEach { option ->
                            FilterChip(
                                selected = biologicalSex == option,
                                onClick = { biologicalSex = option },
                                label = { Text(option) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = mainSymptoms,
                        onValueChange = {
                            mainSymptoms = it
                            validationError = null
                            onCheckRedFlags("$it $associatedSymptoms")
                        },
                        label = { Text("Main symptoms *") },
                        placeholder = { Text("e.g., mild sore throat and fatigue since yesterday") },
                        modifier = Modifier.fillMaxWidth().testTag("symptom_main_input"),
                        minLines = 2
                    )

                    Text("Duration", style = MaterialTheme.typography.labelLarge)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        durationOptions.forEach { option ->
                            FilterChip(
                                selected = duration == option,
                                onClick = { duration = option },
                                label = { Text(option) }
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Severity: ${severity.toInt()} / 10",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Slider(
                            value = severity,
                            onValueChange = { severity = it },
                            valueRange = 1f..10f,
                            steps = 8
                        )
                    }

                    OutlinedTextField(
                        value = associatedSymptoms,
                        onValueChange = {
                            associatedSymptoms = it
                            onCheckRedFlags("$mainSymptoms $it")
                        },
                        label = { Text("Associated symptoms (optional)") },
                        placeholder = { Text("e.g., low-grade fever, body aches") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = existingConditions,
                        onValueChange = { existingConditions = it },
                        label = { Text("Existing medical conditions (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = currentMedications,
                        onValueChange = { currentMedications = it },
                        label = { Text("Current medications (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (validationError != null) {
                        Text(
                            text = validationError!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                if (mainSymptoms.trim().length < 3) {
                                    validationError = "Please describe your main symptoms before running the check."
                                } else {
                                    onRunAssessment(
                                        SymptomAssessmentInput(
                                            age = age,
                                            biologicalSex = biologicalSex,
                                            mainSymptoms = mainSymptoms.trim(),
                                            duration = duration,
                                            severity = severity.toInt(),
                                            associatedSymptoms = associatedSymptoms.trim(),
                                            existingConditions = existingConditions.trim(),
                                            currentMedications = currentMedications.trim(),
                                            language = language
                                        )
                                    )
                                }
                            },
                            enabled = uiState !is AiUiState.Loading,
                            modifier = Modifier.weight(1f).testTag("symptom_submit_button")
                        ) {
                            Icon(Icons.Default.MonitorHeart, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(if (uiState is AiUiState.Loading) "Analyzing..." else "Assess Symptoms")
                        }
                        OutlinedButton(
                            onClick = {
                                age = ""
                                mainSymptoms = ""
                                associatedSymptoms = ""
                                existingConditions = ""
                                currentMedications = ""
                                severity = 4f
                                validationError = null
                                onReset()
                            }
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = "Reset form")
                        }
                    }
                }
            }
        }

        when (uiState) {
            AiUiState.Idle -> Unit
            AiUiState.Loading -> {
                item {
                    Card(modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(14.dp))
                            Text("Preparing structured educational guidance...")
                        }
                    }
                }
            }
            is AiUiState.Error -> {
                item {
                    Card(
                        modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            text = uiState.message,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
            is AiUiState.Success -> {
                item {
                    SymptomResultCard(
                        result = uiState.data,
                        language = language,
                        onOpenEmergency = onOpenEmergency,
                        modifier = Modifier.widthIn(max = 720.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SymptomResultCard(
    result: SymptomAssessmentResult,
    language: AppLanguage,
    onOpenEmergency: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (badgeBg, badgeFg) = when (result.urgencyLevel) {
        "EMERGENCY" -> EmergencyRedContainer to EmergencyOnRedContainer
        "URGENT" -> WarningAmberContainer to WarningOnAmberContainer
        "ROUTINE" -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        else -> SafeGreenContainer to SafeOnGreenContainer
    }
    val urgencyColor = when (result.urgencyLevel) {
        "EMERGENCY" -> EmergencyRed
        "URGENT" -> WarningAmber
        "ROUTINE" -> MaterialTheme.colorScheme.secondary
        else -> SafeGreen
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (result.urgencyLevel == "EMERGENCY") {
            EmergencyAlertBanner(language = language, onOpenEmergencyGuidance = onOpenEmergency)
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    color = badgeBg,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "${result.urgencyLabel} (${result.urgencyLevel})",
                        color = badgeFg,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                Text(
                    text = result.urgencyExplanation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = urgencyColor
                )

                Text("Reported Summary", style = MaterialTheme.typography.titleSmall)
                Text(result.symptomSummary, style = MaterialTheme.typography.bodyMedium)

                Text("Possible Explanations (Educational Possibilities Only)", style = MaterialTheme.typography.titleSmall)
                result.possibleExplanations.forEach { exp ->
                    Text("• $exp", style = MaterialTheme.typography.bodyMedium)
                }

                Text("Helpful Follow-Up Questions", style = MaterialTheme.typography.titleSmall)
                result.followUpQuestions.forEach { q ->
                    Text("• $q", style = MaterialTheme.typography.bodyMedium)
                }

                Text("Warning Signs to Watch For", style = MaterialTheme.typography.titleSmall, color = EmergencyRed)
                result.warningSigns.forEach { w ->
                    Text("• $w", style = MaterialTheme.typography.bodyMedium)
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = result.disclaimer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}
