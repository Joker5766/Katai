package com.jokerdev.katai.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object MarkdownParser {

    fun parse(text: String): AnnotatedString {
        return buildAnnotatedString {
            val lines = text.split("\n")
            lines.forEachIndexed { lineIdx, line ->
                var currentLine = line
                
                // 1. Header Detection
                var isHeader = false
                var headerLevel = 0
                if (currentLine.trimStart().startsWith("#")) {
                    val trimmed = currentLine.trimStart()
                    var hashes = 0
                    while (hashes < trimmed.length && trimmed[hashes] == '#') {
                        hashes++
                    }
                    if (hashes < trimmed.length && trimmed[hashes] == ' ') {
                        isHeader = true
                        headerLevel = hashes
                        currentLine = trimmed.substring(hashes + 1)
                    }
                }

                // 2. Bullet Point Detection
                val isBullet = currentLine.trimStart().startsWith("- ") || currentLine.trimStart().startsWith("* ")
                if (isBullet) {
                    append(" • ")
                    currentLine = currentLine.trimStart().substring(2)
                }

                // Push Header SpanStyle
                if (isHeader) {
                    val fontSize = when (headerLevel) {
                        1 -> 20.sp
                        2 -> 18.sp
                        else -> 16.sp
                    }
                    pushStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = fontSize
                        )
                    )
                }

                // 3. Inline Style Scanner (Bold, Italic, Code Spans)
                var scanIdx = 0
                while (scanIdx < currentLine.length) {
                    if (currentLine.startsWith("**", scanIdx)) {
                        val endIdx = currentLine.indexOf("**", scanIdx + 2)
                        if (endIdx != -1) {
                            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                            append(currentLine.substring(scanIdx + 2, endIdx))
                            pop()
                            scanIdx = endIdx + 2
                            continue
                        }
                    }
                    if (currentLine.startsWith("*", scanIdx)) {
                        val endIdx = currentLine.indexOf("*", scanIdx + 1)
                        if (endIdx != -1) {
                            pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                            append(currentLine.substring(scanIdx + 1, endIdx))
                            pop()
                            scanIdx = endIdx + 1
                            continue
                        }
                    }
                    if (currentLine.startsWith("`", scanIdx)) {
                        val endIdx = currentLine.indexOf("`", scanIdx + 1)
                        if (endIdx != -1) {
                            pushStyle(
                                SpanStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            append(currentLine.substring(scanIdx + 1, endIdx))
                            pop()
                            scanIdx = endIdx + 1
                            continue
                        }
                    }
                    
                    append(currentLine[scanIdx])
                    scanIdx++
                }

                if (isHeader) {
                    pop() // Pop Header Style
                }

                if (lineIdx < lines.size - 1) {
                    append("\n")
                }
            }
        }
    }
}
