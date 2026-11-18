package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import com.example.data.local.AppLanguage
import com.example.data.local.AppointmentEntity
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.SectionHeader
import com.example.util.PdfReportHelper
import kotlinx.coroutines.launch

@Composable
fun AppointmentPrepScreen(
    language: AppLanguage,
    appointments: List<AppointmentEntity>,
    aiLoadingId: Long?,
    aiError: String?,
    onSaveAppointment: (AppointmentEntity) -> Unit,
    onDeleteAppointment: (Long) -> Unit,
    onToggleDocument: (AppointmentEntity, String) -> Unit,
    onGenerateAiQuestions: (AppointmentEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var exportError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier.testTag("add_appointment_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add appointment")
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
                        title = if (language == AppLanguage.URDU) "ڈاکٹر ملاقات کی تیاری" else "Doctor Appointment Preparation",
                        subtitle = "Prepare questions, symptom notes, and a document checklist. (Personal planner only — does not book external clinics.)"
                    )
                    if (!aiError.isNullOrBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text(aiError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                    if (!exportError.isNullOrBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text(exportError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            if (appointments.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon = Icons.Default.CalendarMonth,
                        title = "No Upcoming Consultations Logged",
                        description = "Add your next doctor visit to auto-generate smart questions, document checklists, and exportable visit summaries.",
                        actionLabel = "Add Consultation",
                        onAction = { showDialog = true },
                        modifier = Modifier.widthIn(max = 720.dp)
                    )
                }
            } else {
                items(appointments, key = { it.id }) { apt ->
                    val docs = apt.documentChecklist.split("|").filter { it.isNotBlank() }
                    val checkedSet = apt.checkedDocuments.split("|").filter { it.isNotBlank() }.toSet()
                    val isAiLoading = aiLoadingId == apt.id

                    Card(
                        modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${apt.doctorName} (${apt.specialty})", style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        "${apt.dateTimeLabel}${if (apt.clinicOrHospital.isNotBlank()) " • ${apt.clinicOrHospital}" else ""}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { onDeleteAppointment(apt.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete appointment")
                                }
                            }

                            Text("Reason: ${apt.reason}", style = MaterialTheme.typography.bodyMedium)
                            if (apt.symptomHistoryNotes.isNotBlank()) {
                                Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small) {
                                    Text(
                                        text = "Symptom Notes: ${apt.symptomHistoryNotes}",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                FilledTonalButton(
                                    onClick = { onGenerateAiQuestions(apt) },
                                    enabled = !isAiLoading,
                                    modifier = Modifier.weight(1f).testTag("apt_ai_questions_btn_${apt.id}")
                                ) {
                                    if (isAiLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                        Spacer(Modifier.width(6.dp))
                                        Text("Generating...")
                                    } else {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("AI Doctor Questions")
                                    }
                                }
                                OutlinedButton(
                                    onClick = {
                                        scope.launch {
                                            val res = PdfReportHelper.exportAppointmentPdfAndShare(context, apt)
                                            res.fold(
                                                onSuccess = { intent -> context.startActivity(intent) },
                                                onFailure = { err -> exportError = err.message }
                                            )
                                        }
                                    },
                                    modifier = Modifier.testTag("apt_export_pdf_btn_${apt.id}")
                                ) {
                                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Export PDF")
                                }
                            }

                            if (apt.generatedQuestions.isNotBlank()) {
                                HorizontalDivider()
                                Text("Questions to Ask the Doctor", style = MaterialTheme.typography.titleSmall)
                                Text(apt.generatedQuestions, style = MaterialTheme.typography.bodySmall)
                            }

                            HorizontalDivider()
                            Text("Documents & Items Checklist", style = MaterialTheme.typography.titleSmall)
                            docs.forEach { docItem ->
                                val isChecked = checkedSet.contains(docItem)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onToggleDocument(apt, docItem) },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { onToggleDocument(apt, docItem) }
                                    )
                                    Text(docItem, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AppointmentDialog(
            onDismiss = { showDialog = false },
            onSave = { apt ->
                onSaveAppointment(apt)
                showDialog = false
            }
        )
    }
}

@Composable
private fun AppointmentDialog(
    onDismiss: () -> Unit,
    onSave: (AppointmentEntity) -> Unit
) {
    var doctorName by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("General Physician") }
    var clinic by remember { mutableStateOf("") }
    var dateTime by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var symptomHistory by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Prepare Doctor Consultation") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = doctorName,
                    onValueChange = { doctorName = it; errorMsg = null },
                    label = { Text("Doctor Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("apt_doctor_input")
                )
                OutlinedTextField(
                    value = specialty,
                    onValueChange = { specialty = it },
                    label = { Text("Specialty") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = clinic,
                    onValueChange = { clinic = it },
                    label = { Text("Clinic / Hospital (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dateTime,
                    onValueChange = { dateTime = it; errorMsg = null },
                    label = { Text("Date & Time * (e.g. 28 Nov, 4:00 PM)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("apt_datetime_input")
                )
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it; errorMsg = null },
                    label = { Text("Reason for Consultation *") },
                    modifier = Modifier.fillMaxWidth().testTag("apt_reason_input")
                )
                OutlinedTextField(
                    value = symptomHistory,
                    onValueChange = { symptomHistory = it },
                    label = { Text("Symptom History Summary (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorMsg != null) {
                    Text(errorMsg!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (doctorName.isBlank() || dateTime.isBlank() || reason.isBlank()) {
                        errorMsg = "Please enter doctor name, date/time, and reason."
                    } else {
                        onSave(
                            AppointmentEntity(
                                doctorName = doctorName.trim(),
                                specialty = specialty.trim().ifBlank { "General Physician" },
                                clinicOrHospital = clinic.trim(),
                                dateTimeLabel = dateTime.trim(),
                                reason = reason.trim(),
                                symptomHistoryNotes = symptomHistory.trim()
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("apt_save_dialog_btn")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
