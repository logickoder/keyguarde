package dev.logickoder.keyguarde.app.ai

import dev.logickoder.keyguarde.app.data.model.Keyword
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SemanticMatchingServiceUnitTest {

    private lateinit var semanticMatchingService: SemanticMatchingService

    @Before
    fun setUp() {
        semanticMatchingService = SemanticMatchingService(mockk())
    }

    @Test
    fun testEmptyKeywordsList() {
        val keywords = emptyList<Keyword>()
        val text = "Some notification text"
        
        val matches = semanticMatchingService.findSemanticMatches(text, keywords)
        
        assertTrue("Should return empty set for empty keywords", matches.isEmpty())
    }

    @Test
    fun testEmptyText() {
        val keywords = listOf(
            Keyword(word = "job", useSemanticMatching = true)
        )
        val text = ""
        
        val matches = semanticMatchingService.findSemanticMatches(text, keywords)
        
        assertTrue("Should return empty set for empty text", matches.isEmpty())
    }

    @Test
    fun testOnlyExactMatchingKeywords() {
        val keywords = listOf(
            Keyword(word = "job", useSemanticMatching = false),
            Keyword(word = "trade", useSemanticMatching = false)
        )
        val text = "Looking for work opportunity"
        
        val matches = semanticMatchingService.findSemanticMatches(text, keywords)
        
        assertTrue("Should not match when all keywords have semantic matching disabled", matches.isEmpty())
    }

    @Test
    fun testCaseSensitivity() {
        val keywords = listOf(
            Keyword(word = "JOB", useSemanticMatching = true)
        )
        val text = "looking for a job opportunity"
        
        val matches = semanticMatchingService.findSemanticMatches(text, keywords)
        
        assertTrue("Should be case insensitive", matches.contains("JOB"))
    }
}