package com.jokerdev.katai.data.repository

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PdfRepository {

    suspend fun extractTextFromPdf(
        context: Context,
        pdfUri: Uri
    ): String {
        return withContext(Dispatchers.IO){
            val inputStream = context.contentResolver.openInputStream(pdfUri)
            val document = PDDocument.load(inputStream)
            val extractedText = PDFTextStripper().getText(document)
            document.close()
            extractedText
        }

    }

}