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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.AppLanguage
import com.example.data.local.JournalEntryEntity
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.WellnessTrendChart
import com.example.util.PdfReportHelper
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HealthJournalScreen(
    language: AppLanguage,
    entries: List<JournalEntryEntity>,
    onSaveEntry: (JournalEntryEntity) -> Unit,
    onDeleteEntry: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }
    var exportMessage by remember { mutableStateOf<String?>(null) }

    val weeklyEntries = entries.take(7)
    val monthlyEntries = entries.take(30)
    val avgSleep = if (weeklyEntries.isNotEmpty()) weeklyEntries.map { it.sleepHours }.average() else 0.0
    val avgWater = if (weeklyEntries.isNotEmpty()) weeklyEntries.map { it.waterGlasses }.average() else 0.0
    val totalActivityMonth = monthlyEntries.sumOf { it.activityMinutes }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.testTag("add_journal_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add journal entry")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(modifier = Modifier.widthIn(max = 720.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SectionHeader(
                            title = if (language == AppLanguage.URDU) "ذاتی ہیلتھ جرنل" else "Personal Health Journal",
                            subtitle = "User-entered wellness measurements (not verified clinical records)",
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    val res = PdfReportHelper.exportJournalPdfAndShare(context, entries)
                                    res.fold(
                                        onSuccess = { intent -> context.startActivity(intent) },
                                        onFailure = { err -> exportMessage = err.message }
                                    )
                                }
                            },
                            enabled = entries.isNotEmpty(),
                            modifier = Modifier.testTag("export_journal_pdf_btn")
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("PDF")
                        }
                    }
                    if (!exportMessage.isNullOrBlank()) {
                        Text(exportMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.large
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        JournalStatItem("Avg Sleep (7d)", String.format(Locale.US, "%.1f h", avgSleep))
                        JournalStatItem("Avg Water (7d)", String.format(Locale.US, "%.1f gl", avgWater))
                        JournalStatItem("Activity (30d)", "$totalActivityMonth m")
                    }
                }
            }

            item {
                WellnessTrendChart(
                    entries = entries,
                    modifier = Modifier.widthIn(max = 720.dp)
                )
            }

            if (entries.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon = Icons.AutoMirrored.Filled.Assignment,
                        title = "No Health Journal Entries Yet",
                        description = "Record sleep, water, activity, mood, symptoms, blood pressure, or blood glucose locally.",
                        actionLabel = "Log Today's Wellness",
                        onAction = { showAddDialog = true },
                        modifier = Modifier.widthIn(max = 720.dp)
                    )
                }
            } else {
                items(entries, key = { it.id }) { entry ->
                    Card(
                        modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(entry.dateLabel, style = MaterialTheme.typography.titleSmall)
                                    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.small) {
                                        Text(
                                            text = entry.mood,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                IconButton(onClick = { onDeleteEntry(entry.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete journal entry")
                                }
                            }
                            Text(
                                text = "Sleep: ${entry.sleepHours}h  •  Water: ${entry.waterGlasses} glasses  •  Activity: ${entry.activityMinutes} min",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                            if (entry.bloodPressure.isNotBlank() || entry.bloodGlucoseMgDl != null) {
                                Text(
                                    text = "User-entered vitals — BP: ${entry.bloodPressure.ifBlank { "-" }}  |  Glucose: ${entry.bloodGlucoseMgDl?.let { "$it mg/dL" } ?: "-"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            if (entry.symptoms.isNotBlank()) {
                                Text("Symptoms: ${entry.symptoms}", style = MaterialTheme.typography.bodySmall)
                            }
                            if (entry.observations.isNotBlank()) {
                                Text(entry.observations, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        JournalEntryDialog(
            onDismiss = { showAddDialog = false },
            onSave = { entry ->
                onSaveEntry(entry)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun JournalStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun JournalEntryDialog(
    onDismiss: () -> Unit,
    onSave: (JournalEntryEntity) -> Unit
) {
    val todayLabel = remember { SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date()) }
    var sleepText by remember { mutableStateOf("7.5") }
    var waterText by remember { mutableStateOf("8") }
    var activityText by remember { mutableStateOf("30") }
    var mood by remember { mutableStateOf("Good") }
    var symptoms by remember { mutableStateOf("") }
    var bp by remember { mutableStateOf("") }
    var glucoseText by remember { mutableStateOf("") }
    var observations by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val moods = listOf("Energized", "Good", "Calm", "Okay", "Tired", "Stressed")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Health Journal Log ($todayLabel)") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sleepText,
                        onValueChange = { sleepText = it },
                        label = { Text("Sleep (hrs)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("journal_sleep_input")
                    )
                    OutlinedTextField(
                        value = waterText,
                        onValueChange = { waterText = it.filter { c -> c.isDigit() } },
                        label = { Text("Water (glasses)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = activityText,
                        onValueChange = { activityText = it.filter { c -> c.isDigit() } },
                        label = { Text("Activity (min)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = bp,
                        onValueChange = { bp = it },
                        label = { Text("BP (e.g. 120/80)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = glucoseText,
                    onValueChange = { glucoseText = it.filter { c -> c.isDigit() } },
                    label = { Text("Blood Glucose mg/dL (optional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Mood", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    moods.forEach { m ->
                        FilterChip(selected = mood == m, onClick = { mood = m }, label = { Text(m) })
                    }
                }
                OutlinedTextField(
                    value = symptoms,
                    onValueChange = { symptoms = it },
                    label = { Text("Symptoms (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = observations,
                    onValueChange = { observations = it },
                    label = { Text("Daily observations") },
                    modifier = Modifier.fillMaxWidth().testTag("journal_notes_input")
                )
                if (errorMsg != null) {
                    Text(errorMsg!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val sleep = sleepText.toFloatOrNull()
                    if (sleep == null || sleep < 0f || sleep > 24f) {
                        errorMsg = "Enter valid sleep hours (0–24)."
                    } else {
                        onSave(
                            JournalEntryEntity(
                                dateLabel = todayLabel,
                                observations = observations.trim(),
                                sleepHours = sleep,
                                waterGlasses = waterText.toIntOrNull() ?: 0,
                                activityMinutes = activityText.toIntOrNull() ?: 0,
                                mood = mood,
                                symptoms = symptoms.trim(),
                                bloodPressure = bp.trim(),
                                bloodGlucoseMgDl = glucoseText.toIntOrNull()
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("journal_save_dialog_btn")
            ) {
                Text("Save Log")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
