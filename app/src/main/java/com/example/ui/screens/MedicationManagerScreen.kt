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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.NotificationsActive
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.AppLanguage
import com.example.data.local.MedicationEntity
import com.example.data.local.MedicationLogEntity
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.SectionHeader

@Composable
fun MedicationManagerScreen(
    language: AppLanguage,
    medications: List<MedicationEntity>,
    logs: List<MedicationLogEntity>,
    onSaveMedication: (MedicationEntity) -> Unit,
    onDeleteMedication: (Long) -> Unit,
    onLogStatus: (MedicationEntity, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var editingMed by remember { mutableStateOf<MedicationEntity?>(null) }
    var showFormDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingMed = null
                    showFormDialog = true
                },
                modifier = Modifier.testTag("add_medication_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add medication")
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
                    SectionHeader(
                        title = if (language == AppLanguage.URDU) "دواؤں کا یاد دہانی منیجر" else "Medication Reminder Manager",
                        subtitle = "Organize your own prescribed doses and track daily adherence"
                    )
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "In-app reminders only: Notifications may not trigger when the app is closed. HealthMate AI does not recommend medicines or adjust prescribed dosages.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }

            if (medications.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon = Icons.Default.Medication,
                        title = "No Medications Added",
                        description = "Add medicines prescribed by your doctor to keep a clear schedule and history.",
                        actionLabel = "Add Medication",
                        onAction = {
                            editingMed = null
                            showFormDialog = true
                        },
                        modifier = Modifier.widthIn(max = 720.dp)
                    )
                }
            } else {
                items(medications, key = { it.id }) { med ->
                    Card(
                        modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(med.name, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        "Dosage (user-entered): ${med.prescribedDosage}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        "${med.reminderTime} • ${med.frequency} • Start: ${med.startDate}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (med.endDate.isNotBlank()) {
                                        Text(
                                            "End date: ${med.endDate}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (med.notes.isNotBlank()) {
                                        Text(
                                            med.notes,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                Row {
                                    IconButton(
                                        onClick = {
                                            editingMed = med
                                            showFormDialog = true
                                        }
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit medication")
                                    }
                                    IconButton(onClick = { onDeleteMedication(med.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete medication")
                                    }
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = { onLogStatus(med, "TAKEN") },
                                    modifier = Modifier.weight(1f).testTag("med_taken_btn_${med.id}")
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Mark Taken")
                                }
                                OutlinedButton(
                                    onClick = { onLogStatus(med, "MISSED") },
                                    modifier = Modifier.weight(1f).testTag("med_missed_btn_${med.id}")
                                ) {
                                    Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Mark Missed")
                                }
                            }
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.widthIn(max = 720.dp)) {
                    SectionHeader(
                        title = "Medication History Log",
                        subtitle = "Recent taken and missed records"
                    )
                    Spacer(Modifier.height(8.dp))
                    if (logs.isEmpty()) {
                        Text(
                            text = "No doses logged yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        logs.take(15).forEach { log ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(log.medicationName, style = MaterialTheme.typography.titleSmall)
                                        Text("${log.dosage} • ${log.dateLabel}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Text(
                                        text = log.status,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (log.status == "TAKEN") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFormDialog) {
        MedicationFormDialog(
            initial = editingMed,
            onDismiss = { showFormDialog = false },
            onSave = { med ->
                onSaveMedication(med)
                showFormDialog = false
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MedicationFormDialog(
    initial: MedicationEntity?,
    onDismiss: () -> Unit,
    onSave: (MedicationEntity) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var dosage by remember { mutableStateOf(initial?.prescribedDosage ?: "") }
    var reminderTime by remember { mutableStateOf(initial?.reminderTime ?: "09:00 AM") }
    var frequency by remember { mutableStateOf(initial?.frequency ?: "Once daily") }
    var startDate by remember { mutableStateOf(initial?.startDate ?: "Today") }
    var endDate by remember { mutableStateOf(initial?.endDate ?: "") }
    var notes by remember { mutableStateOf(initial?.notes ?: "") }
    var errorText by remember { mutableStateOf<String?>(null) }

    val frequencies = listOf("Once daily", "Twice daily", "Three times daily", "As prescribed")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Add Medication Reminder" else "Edit Medication") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorText = null },
                    label = { Text("Medication Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("med_name_input")
                )
                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it; errorText = null },
                    label = { Text("Prescribed Dosage (as on your prescription) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("med_dosage_input")
                )
                OutlinedTextField(
                    value = reminderTime,
                    onValueChange = { reminderTime = it },
                    label = { Text("Reminder Time (e.g., 09:00 AM)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Frequency", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    frequencies.forEach { freq ->
                        FilterChip(
                            selected = frequency == freq,
                            onClick = { frequency = freq },
                            label = { Text(freq) }
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text("Start Date") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text("End Date") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorText != null) {
                    Text(errorText!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || dosage.isBlank()) {
                        errorText = "Please enter both medication name and your prescribed dosage."
                    } else {
                        onSave(
                            MedicationEntity(
                                id = initial?.id ?: 0L,
                                name = name.trim(),
                                prescribedDosage = dosage.trim(),
                                reminderTime = reminderTime.trim().ifBlank { "09:00 AM" },
                                frequency = frequency,
                                startDate = startDate.trim().ifBlank { "Today" },
                                endDate = endDate.trim(),
                                notes = notes.trim()
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("med_save_dialog_btn")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
