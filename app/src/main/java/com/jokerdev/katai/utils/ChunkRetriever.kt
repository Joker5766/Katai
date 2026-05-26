package com.jokerdev.katai.utils

object ChunkRetriever {
    // Standard set of English stopwords to ignore when calculating term weights
    private val stopWords = setOf(
        "the", "a", "an", "and", "or", "but", "if", "then", "else", "when", 
        "at", "by", "for", "with", "about", "against", "between", "into", 
        "through", "during", "before", "after", "above", "below", "to", 
        "from", "up", "down", "in", "out", "on", "off", "over", "under", 
        "again", "further", "once", "here", "there", "all", "any", 
        "both", "each", "few", "more", "most", "other", "some", "such", 
        "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very",
        "s", "t", "can", "will", "just", "don", "should", "now", "i", "me", 
        "my", "myself", "we", "our", "ours", "ourselves", "you", "your", 
        "yours", "yourself", "yourselves", "he", "him", "his", "himself", 
        "she", "her", "hers", "herself", "it", "its", "itself", "they", 
        "them", "their", "theirs", "themselves", "what", "which", "who", 
        "whom", "this", "that", "these", "those", "am", "is", "are", "was", 
        "were", "be", "been", "being", "have", "has", "had", "having", 
        "do", "does", "did", "doing", "would", "should", "could", "ought", 
        "isn't", "aren't", "wasn't", "weren't", "hasn't", "haven't", "hadn't", 
        "doesn't", "don't", "didn't", "won't", "wouldn't", "shan't", "shouldn't", 
        "can't", "cannot", "couldn't", "mustn't"
    )

    /**
     * Scores the text chunks against the query using a TF-IDF weight matching.
     * Selects and returns the topK most relevant chunks.
     */
    fun getRelevantChunks(query: String, chunks: List<String>, topK: Int = 3): List<String> {
        if (chunks.isEmpty()) return emptyList()
        if (query.isBlank()) return chunks.take(topK)

        val queryTokens = tokenize(query).filter { !stopWords.contains(it) }

        val activeTokens = if (queryTokens.isEmpty()) tokenize(query) else queryTokens
        if (activeTokens.isEmpty()) return chunks.take(topK)

        return rankChunks(activeTokens, chunks, topK)
    }

    private fun tokenize(text: String): List<String> {
        return text.lowercase()
            .split(Regex("[^a-zA-Z0-9]+"))
            .filter { it.isNotBlank() }
    }

    private fun rankChunks(queryTokens: List<String>, chunks: List<String>, topK: Int): List<String> {
        val totalDocs = chunks.size.toDouble()

        val idf = queryTokens.associateWith { token ->
            val docsWithToken = chunks.count { it.lowercase().contains(token) }
            Math.log(1.0 + (totalDocs - docsWithToken + 0.5) / (docsWithToken + 0.5))
        }

        val scoredChunks = chunks.map { chunk ->
            val chunkLower = chunk.lowercase()
            val wordsInChunk = chunkLower.split(Regex("[^a-zA-Z0-9]+")).filter { it.isNotBlank() }
            val wordCounts = wordsInChunk.groupingBy { it }.eachCount()
            val docLen = wordsInChunk.size.toDouble().coerceAtLeast(1.0)

            var score = 0.0
            for (token in queryTokens) {
                val tf = wordCounts[token]?.toDouble() ?: 0.0
                if (tf > 0) {
                    val tokenIdf = idf[token] ?: 0.0
                    score += (tf / docLen) * tokenIdf
                }
            }
            chunk to score
        }

        val matchedChunks = scoredChunks
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .map { it.first }

        return if (matchedChunks.isEmpty()) {
            chunks.take(topK)
        } else {
            matchedChunks.take(topK)
        }
    }
}
