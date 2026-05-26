package com.jokerdev.katai.utils

object PdfChunker {
    /**
     * Splits a large block of text extracted from a PDF into overlapping chunks of words.
     * On-demand chunking is highly fast, low-overhead, and keeps our state architecture fully backward-compatible.
     */
    fun chunkText(text: String, chunkSizeWords: Int = 150, overlapWords: Int = 30): List<String> {
        if (text.isBlank()) return emptyList()

        val words = text.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        if (words.size <= chunkSizeWords) return listOf(text)

        val chunks = mutableListOf<String>()
        var start = 0
        val effectiveOverlap = overlapWords.coerceAtMost(chunkSizeWords - 1)
        val step = chunkSizeWords - effectiveOverlap

        while (start < words.size) {
            val end = (start + chunkSizeWords).coerceAtMost(words.size)
            val chunkWords = words.subList(start, end)
            chunks.add(chunkWords.joinToString(" "))
            
            if (end >= words.size) break
            start += step
        }
        return chunks
    }
}
