package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import com.example.data.local.AppointmentEntity
import com.example.data.local.JournalEntryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object PdfReportHelper {

    suspend fun loadBitmapsFromUri(
        context: Context,
        uri: Uri,
        maxPagesForPdf: Int = 3
    ): Result<List<Bitmap>> = withContext(Dispatchers.IO) {
        try {
            val mimeType = context.contentResolver.getType(uri).orEmpty()
            if (mimeType == "application/pdf" || uri.toString().lowercase().endsWith(".pdf")) {
                val pfd = context.contentResolver.openFileDescriptor(uri, "r")
                    ?: return@withContext Result.failure(Exception("Could not open PDF file."))
                pfd.use { descriptor ->
                    val renderer = PdfRenderer(descriptor)
                    renderer.use { pdf ->
                        val pageCount = pdf.pageCount
                        if (pageCount == 0) {
                            return@withContext Result.failure(Exception("The selected PDF has 0 pages."))
                        }
                        val bitmaps = mutableListOf<Bitmap>()
                        val limit = minOf(pageCount, maxPagesForPdf)
                        for (i in 0 until limit) {
                            pdf.openPage(i).use { page ->
                                val scale = minOf(1240f / page.width.coerceAtLeast(1), 2.0f)
                                val width = (page.width * scale).toInt().coerceAtLeast(1)
                                val height = (page.height * scale).toInt().coerceAtLeast(1)
                                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                                val canvas = Canvas(bitmap)
                                canvas.drawColor(Color.WHITE)
                                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                                bitmaps.add(bitmap)
                            }
                        }
                        Result.success(bitmaps)
                    }
                }
            } else {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: return@withContext Result.failure(Exception("Could not read selected image."))
                inputStream.use { stream ->
                    val decoded = BitmapFactory.decodeStream(stream)
                        ?: return@withContext Result.failure(Exception("Unsupported or corrupted image file."))
                    Result.success(listOf(scaleBitmapIfNeeded(decoded, maxDimension = 1400)))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Failed to process selected report file."))
        }
    }

    private fun scaleBitmapIfNeeded(source: Bitmap, maxDimension: Int): Bitmap {
        val maxSide = maxOf(source.width, source.height)
        if (maxSide <= maxDimension) return source
        val ratio = maxDimension.toFloat() / maxSide.toFloat()
        val targetW = (source.width * ratio).toInt().coerceAtLeast(1)
        val targetH = (source.height * ratio).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(source, targetW, targetH, true)
    }

    suspend fun exportJournalPdfAndShare(
        context: Context,
        entries: List<JournalEntryEntity>
    ): Result<Intent> = withContext(Dispatchers.IO) {
        try {
            val lines = buildString {
                appendLine("HealthMate AI — Personal Health Journal Summary")
                appendLine("Notice: User-entered wellness logs. Not verified clinical records.")
                appendLine("Generated entries: ${entries.size}")
                appendLine()
                entries.take(25).forEach { e ->
                    appendLine("Date: ${e.dateLabel} | Mood: ${e.mood}")
                    appendLine("  Sleep: ${e.sleepHours} hrs | Water: ${e.waterGlasses} glasses | Activity: ${e.activityMinutes} min")
                    if (e.bloodPressure.isNotBlank() || e.bloodGlucoseMgDl != null) {
                        appendLine("  BP: ${e.bloodPressure.ifBlank { "-" }} | Glucose: ${e.bloodGlucoseMgDl?.let { "$it mg/dL" } ?: "-"}")
                    }
                    if (e.symptoms.isNotBlank()) appendLine("  Symptoms: ${e.symptoms}")
                    if (e.observations.isNotBlank()) appendLine("  Notes: ${e.observations}")
                    appendLine("------------------------------------------------------------")
                }
            }
            val file = writeTextToPdf(context, "healthmate_journal_summary.pdf", lines)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "HealthMate AI - Health Journal Summary")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            Result.success(Intent.createChooser(shareIntent, "Export Health Journal PDF"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun exportAppointmentPdfAndShare(
        context: Context,
        appointment: AppointmentEntity
    ): Result<Intent> = withContext(Dispatchers.IO) {
        try {
            val checklistItems = appointment.documentChecklist.split("|").filter { it.isNotBlank() }
            val checked = appointment.checkedDocuments.split("|").toSet()
            val text = buildString {
                appendLine("HealthMate AI — Doctor Appointment Preparation Summary")
                appendLine("Educational preparation sheet (not an official clinic booking).")
                appendLine()
                appendLine("Doctor: ${appointment.doctorName} (${appointment.specialty})")
                if (appointment.clinicOrHospital.isNotBlank()) {
                    appendLine("Clinic / Hospital: ${appointment.clinicOrHospital}")
                }
                appendLine("Date & Time: ${appointment.dateTimeLabel}")
                appendLine("Reason for Consultation: ${appointment.reason}")
                appendLine()
                if (appointment.symptomHistoryNotes.isNotBlank()) {
                    appendLine("Symptom History Notes:")
                    appendLine(appointment.symptomHistoryNotes)
                    appendLine()
                }
                if (appointment.generatedQuestions.isNotBlank()) {
                    appendLine("Questions to Ask the Doctor:")
                    appendLine(appointment.generatedQuestions)
                    appendLine()
                }
                appendLine("Documents & Items Checklist:")
                checklistItems.forEach { item ->
                    val mark = if (checked.contains(item)) "[x]" else "[ ]"
                    appendLine("  $mark $item")
                }
            }
            val file = writeTextToPdf(context, "healthmate_appointment_prep.pdf", text)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Appointment Prep - ${appointment.doctorName}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            Result.success(Intent.createChooser(shareIntent, "Export Appointment Summary PDF"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun writeTextToPdf(context: Context, filename: String, bodyText: String): File {
        val pageWidth = 595
        val pageHeight = 842
        val margin = 40
        val usableWidth = pageWidth - margin * 2

        val document = PdfDocument()
        val textPaint = TextPaint().apply {
            color = Color.BLACK
            textSize = 11f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }
        val headerPaint = Paint().apply {
            color = Color.rgb(13, 124, 117)
            style = Paint.Style.FILL
        }

        val layout = StaticLayout.Builder.obtain(
            bodyText,
            0,
            bodyText.length,
            textPaint,
            usableWidth
        ).setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(4f, 1.15f)
            .build()

        val maxContentHeight = pageHeight - margin * 2 - 20
        var lineIndex = 0
        var pageNumber = 1

        while (lineIndex < layout.lineCount) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            canvas.drawColor(Color.WHITE)
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), 14f, headerPaint)

            var yOffset = margin.toFloat() + 10f
            val startTop = layout.getLineTop(lineIndex)
            while (lineIndex < layout.lineCount) {
                val lineBottom = layout.getLineBottom(lineIndex) - startTop
                if (lineBottom > maxContentHeight && lineIndex > 0) {
                    break
                }
                val start = layout.getLineStart(lineIndex)
                val end = layout.getLineEnd(lineIndex)
                val lineText = bodyText.substring(start, end).trimEnd('\n')
                canvas.drawText(lineText, margin.toFloat(), yOffset + (layout.getLineBaseline(lineIndex) - startTop), textPaint)
                lineIndex++
            }
            document.finishPage(page)
            pageNumber++
        }

        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val outputFile = File(exportDir, filename)
        FileOutputStream(outputFile).use { out ->
            document.writeTo(out)
        }
        document.close()
        return outputFile
    }
}
