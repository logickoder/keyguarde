package dev.logickoder.keyguarde.app.ai

import android.content.Context
import dev.logickoder.keyguarde.app.data.model.Keyword
import io.github.aakira.napier.Napier
import kotlin.math.max
import kotlin.math.min

/**
 * Service for performing semantic keyword matching using lightweight AI techniques.
 * 
 * This service implements basic semantic similarity using string analysis and 
 * contextual matching as a lightweight alternative to heavy ML models.
 * 
 * Future enhancement: Could be replaced with actual JetBrains Koog integration
 * or TensorFlow Lite models for more sophisticated semantic matching.
 */
class SemanticMatchingService(private val context: Context) {

    /**
     * Check if the given text semantically matches any of the provided keywords.
     * 
     * @param text The notification text to analyze
     * @param keywords List of keywords with context for semantic matching
     * @return Set of matched keyword words
     */
    fun findSemanticMatches(text: String, keywords: List<Keyword>): Set<String> {
        val matchedKeywords = mutableSetOf<String>()
        
        try {
            for (keyword in keywords.filter { it.useSemanticMatching }) {
                if (isSemanticMatch(text, keyword)) {
                    matchedKeywords.add(keyword.word)
                    Napier.d { "Semantic match found: '${keyword.word}' in text: '${text.take(50)}...'" }
                }
            }
        } catch (e: Exception) {
            Napier.e(e) { "Error during semantic matching" }
        }
        
        return matchedKeywords
    }

    /**
     * Determine if the text semantically matches the keyword based on context.
     */
    private fun isSemanticMatch(text: String, keyword: Keyword): Boolean {
        val normalizedText = text.lowercase()
        val normalizedKeyword = keyword.word.lowercase()
        val normalizedContext = keyword.context.lowercase()

        // Direct keyword match (exact or partial)
        if (normalizedText.contains(normalizedKeyword)) {
            return true
        }

        // Context-based matching if context is provided
        if (normalizedContext.isNotBlank()) {
            // Check if any context words appear in the text
            val contextWords = extractMeaningfulWords(normalizedContext)
            val textWords = extractMeaningfulWords(normalizedText)
            
            // Calculate semantic similarity based on overlapping words
            val similarity = calculateWordSimilarity(contextWords, textWords)
            
            // Consider it a match if similarity is above threshold
            if (similarity > SEMANTIC_SIMILARITY_THRESHOLD) {
                return true
            }

            // Check for related terms using simple word association
            if (hasRelatedTerms(normalizedKeyword, normalizedContext, normalizedText)) {
                return true
            }
        }

        // Fuzzy matching for typos and variations
        if (hasFuzzyMatch(normalizedKeyword, normalizedText)) {
            return true
        }

        return false
    }

    /**
     * Extract meaningful words by filtering out common stop words.
     */
    private fun extractMeaningfulWords(text: String): Set<String> {
        val stopWords = setOf(
            "the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with", "by",
            "is", "are", "was", "were", "be", "been", "have", "has", "had", "do", "does", "did",
            "will", "would", "could", "should", "may", "might", "can", "must", "shall",
            "this", "that", "these", "those", "i", "you", "he", "she", "it", "we", "they",
            "me", "him", "her", "us", "them", "my", "your", "his", "her", "its", "our", "their"
        )
        
        return text.split(Regex("\\W+"))
            .filter { it.length > 2 && !stopWords.contains(it) }
            .toSet()
    }

    /**
     * Calculate similarity between two sets of words.
     */
    private fun calculateWordSimilarity(words1: Set<String>, words2: Set<String>): Double {
        if (words1.isEmpty() || words2.isEmpty()) return 0.0
        
        val intersection = words1.intersect(words2).size
        val union = words1.union(words2).size
        
        // Jaccard similarity
        return intersection.toDouble() / union.toDouble()
    }

    /**
     * Check for related terms using simple word association.
     */
    private fun hasRelatedTerms(keyword: String, context: String, text: String): Boolean {
        // Simple word association patterns
        val associations = mapOf(
            "job" to listOf("work", "employment", "career", "position", "role", "hiring", "opportunity"),
            "trade" to listOf("buy", "sell", "market", "price", "crypto", "stock", "forex", "trading"),
            "meeting" to listOf("call", "zoom", "conference", "discussion", "appointment"),
            "food" to listOf("restaurant", "menu", "order", "delivery", "meal", "lunch", "dinner"),
            "travel" to listOf("flight", "hotel", "trip", "vacation", "booking", "destination"),
            "urgent" to listOf("asap", "immediately", "quickly", "rush", "emergency", "important"),
            "sale" to listOf("discount", "offer", "deal", "promotion", "cheap", "price", "buy")
        )

        // Check if keyword has associated terms that appear in text
        associations[keyword]?.let { relatedTerms ->
            return relatedTerms.any { term -> text.contains(term) }
        }

        // Check if any context words have associations that appear in text
        val contextWords = extractMeaningfulWords(context)
        for (contextWord in contextWords) {
            associations[contextWord]?.let { relatedTerms ->
                if (relatedTerms.any { term -> text.contains(term) }) {
                    return true
                }
            }
        }

        return false
    }

    /**
     * Check for fuzzy matches to handle typos and variations.
     */
    private fun hasFuzzyMatch(keyword: String, text: String): Boolean {
        val words = text.split(Regex("\\W+"))
        
        return words.any { word ->
            calculateLevenshteinDistance(keyword, word) <= FUZZY_MATCH_THRESHOLD
        }
    }

    /**
     * Calculate Levenshtein distance between two strings.
     */
    private fun calculateLevenshteinDistance(s1: String, s2: String): Int {
        val len1 = s1.length
        val len2 = s2.length

        if (len1 == 0) return len2
        if (len2 == 0) return len1

        val matrix = Array(len1 + 1) { IntArray(len2 + 1) }

        for (i in 0..len1) matrix[i][0] = i
        for (j in 0..len2) matrix[0][j] = j

        for (i in 1..len1) {
            for (j in 1..len2) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                matrix[i][j] = min(
                    matrix[i - 1][j] + 1,      // deletion
                    min(
                        matrix[i][j - 1] + 1,  // insertion
                        matrix[i - 1][j - 1] + cost  // substitution
                    )
                )
            }
        }

        return matrix[len1][len2]
    }

    companion object {
        private const val SEMANTIC_SIMILARITY_THRESHOLD = 0.15 // 15% word overlap
        private const val FUZZY_MATCH_THRESHOLD = 2 // Allow up to 2 character differences
        
        @Volatile
        private var instance: SemanticMatchingService? = null

        fun getInstance(context: Context): SemanticMatchingService {
            return instance ?: synchronized(this) {
                instance = SemanticMatchingService(context.applicationContext)
                instance!!
            }
        }
    }
}