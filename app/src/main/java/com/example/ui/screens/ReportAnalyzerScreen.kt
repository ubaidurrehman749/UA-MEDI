package com.example.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.ai.ReportAnalysisResult
import com.example.data.local.AppLanguage
import com.example.data.local.SavedReportSummaryEntity
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.components.SectionHeader
import com.example.ui.state.AiUiState
import com.example.ui.theme.EmergencyOnRedContainer
import com.example.ui.theme.EmergencyRedContainer
import com.example.ui.theme.SafeGreenContainer
import com.example.ui.theme.SafeOnGreenContainer
import com.example.ui.theme.WarningAmberContainer
import com.example.ui.theme.WarningOnAmberContainer

@Composable
fun ReportAnalyzerScreen(
    language: AppLanguage,
    bitmaps: List<Bitmap>,
    fileName: String?,
    loadError: String?,
    uiState: AiUiState<ReportAnalysisResult>,
    savedReports: List<SavedReportSummaryEntity>,
    onSelectFile: (android.net.Uri, String) -> Unit,
    onAnalyze: (String) -> Unit,
    onSaveSummary: () -> Unit,
    onDeleteSavedReport: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isUrdu = language == AppLanguage.URDU
    var notes by remember { mutableStateOf("") }

    // Zero-permission Photo Picker for JPG/PNG medical reports
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onSelectFile(uri, "Selected Image Report")
        }
    }

    // Zero-permission SAF OpenDocument for PDF (or images)
    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val label = uri.lastPathSegment?.substringAfterLast('/') ?: "Selected Document"
            onSelectFile(uri, label)
        }
    }

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
                    title = if (isUrdu) "میڈیکل رپورٹ تجزیہ کار" else "Medical Report Analyzer",
                    subtitle = if (isUrdu) {
                        "PDF یا تصویر اپ لوڈ کریں — سادہ الفاظ میں وضاحت حاصل کریں"
                    } else {
                        "Upload PDF or lab report images for plain-language explanations"
                    }
                )
                Spacer(Modifier.height(8.dp))
                MedicalDisclaimerBanner(language = language)
            }
        }

        item {
            Card(
                modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Upload Laboratory or Medical Report",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Supports PDF documents (renders up to 3 pages for analysis) as well as JPG/PNG photos. Reference ranges vary between laboratories; a qualified clinician must interpret results in clinical context.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FilledTonalButton(
                            onClick = {
                                photoPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.weight(1f).testTag("upload_image_report_btn")
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Select Image")
                        }
                        FilledTonalButton(
                            onClick = {
                                pdfPicker.launch(arrayOf("application/pdf", "image/*"))
                            },
                            modifier = Modifier.weight(1f).testTag("upload_pdf_report_btn")
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Select PDF")
                        }
                    }

                    if (!loadError.isNullOrBlank()) {
                        Text(loadError, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }

                    if (bitmaps.isNotEmpty()) {
                        Text(
                            text = "Loaded: ${fileName ?: "Report"} (${bitmaps.size} page${if (bitmaps.size > 1) "s" else ""})",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(bitmaps) { bmp ->
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = "Uploaded report preview",
                                    modifier = Modifier
                                        .size(width = 140.dp, height = 180.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Optional context (e.g., fasting test, routine checkup)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { onAnalyze(notes) },
                        enabled = bitmaps.isNotEmpty() && uiState !is AiUiState.Loading,
                        modifier = Modifier.fillMaxWidth().testTag("analyze_report_btn")
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(if (uiState is AiUiState.Loading) "Analyzing Report..." else "Analyze Report with AI")
                    }
                }
            }
        }

        when (uiState) {
            AiUiState.Idle -> Unit
            AiUiState.Loading -> {
                item {
                    Card(modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth()) {
                        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Reading report values & preparing plain-language summary...")
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
                    ReportAnalysisResultCard(
                        result = uiState.data,
                        onSaveSummary = onSaveSummary,
                        modifier = Modifier.widthIn(max = 720.dp)
                    )
                }
            }
        }

        item {
            Column(modifier = Modifier.widthIn(max = 720.dp)) {
                SectionHeader(
                    title = "Saved Report Summaries",
                    subtitle = "Stored privately on this device"
                )
                Spacer(Modifier.height(8.dp))
                if (savedReports.isEmpty()) {
                    EmptyStateCard(
                        icon = Icons.Default.Description,
                        title = "No Saved Report Summaries",
                        description = "Analyze a PDF or lab image above and save its plain-language summary locally."
                    )
                } else {
                    savedReports.forEach { saved ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(saved.title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                                    IconButton(onClick = { onDeleteSavedReport(saved.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete saved report")
                                    }
                                }
                                Text(saved.summary, style = MaterialTheme.typography.bodySmall)
                                if (saved.flaggedHighlights.isNotBlank()) {
                                    Spacer(Modifier.height(6.dp))
                                    Text("Highlights:\n${saved.flaggedHighlights}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportAnalysisResultCard(
    result: ReportAnalysisResult,
    onSaveSummary: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(result.reportTitle, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                OutlinedButton(onClick = onSaveSummary, modifier = Modifier.testTag("save_report_summary_btn")) {
                    Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Save")
                }
            }

            Text("Simple Summary", style = MaterialTheme.typography.titleSmall)
            Text(result.simpleSummary, style = MaterialTheme.typography.bodyMedium)

            if (result.extractedTests.isNotEmpty()) {
                HorizontalDivider()
                Text("Identified Test Values", style = MaterialTheme.typography.titleSmall)
                result.extractedTests.forEach { test ->
                    val (bg, fg) = when (test.flag) {
                        "HIGH" -> EmergencyRedContainer to EmergencyOnRedContainer
                        "LOW" -> WarningAmberContainer to WarningOnAmberContainer
                        "NORMAL" -> SafeGreenContainer to SafeOnGreenContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(test.testName, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                                Surface(color = bg, shape = RoundedCornerShape(6.dp)) {
                                    Text(
                                        text = test.flag,
                                        color = fg,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Reported: ${test.reportedValue}  |  Ref: ${test.referenceRange}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(test.plainExplanation, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            if (result.termsExplained.isNotEmpty()) {
                HorizontalDivider()
                Text("Medical Terms Explained", style = MaterialTheme.typography.titleSmall)
                result.termsExplained.forEach { term ->
                    Text("• $term", style = MaterialTheme.typography.bodySmall)
                }
            }

            if (result.questionsForDoctor.isNotEmpty()) {
                HorizontalDivider()
                Text("Questions to Ask Your Doctor", style = MaterialTheme.typography.titleSmall)
                result.questionsForDoctor.forEach { q ->
                    Text("• $q", style = MaterialTheme.typography.bodySmall)
                }
            }

            Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.small) {
                Text(
                    text = result.disclaimer,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}
