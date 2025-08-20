package dev.logickoder.keyguarde.app.ai

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.logickoder.keyguarde.app.data.model.Keyword
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SemanticMatchingServiceTest {

    private lateinit var semanticMatchingService: SemanticMatchingService

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        semanticMatchingService = SemanticMatchingService.getInstance(context)
    }

    @Test
    fun testExactMatch() {
        val keywords = listOf(
            Keyword(word = "job", useSemanticMatching = true)
        )
        val text = "Looking for a job opportunity"
        
        val matches = semanticMatchingService.findSemanticMatches(text, keywords)
        
        assertTrue("Should find exact match", matches.contains("job"))
    }

    @Test
    fun testContextBasedMatch() {
        val keywords = listOf(
            Keyword(
                word = "opportunity", 
                context = "job work career employment",
                useSemanticMatching = true
            )
        )
        val text = "We are hiring for a software engineer position"
        
        val matches = semanticMatchingService.findSemanticMatches(text, keywords)
        
        assertTrue("Should find context-based match", matches.contains("opportunity"))
    }

    @Test
    fun testRelatedTermsMatch() {
        val keywords = listOf(
            Keyword(
                word = "job", 
                context = "career opportunities",
                useSemanticMatching = true
            )
        )
        val text = "Great opportunity for software developer role"
        
        val matches = semanticMatchingService.findSemanticMatches(text, keywords)
        
        assertTrue("Should find related terms match", matches.contains("job"))
    }

    @Test
    fun testFuzzyMatch() {
        val keywords = listOf(
            Keyword(word = "urgent", useSemanticMatching = true)
        )
        val text = "This is urgnt please respond asap"
        
        val matches = semanticMatchingService.findSemanticMatches(text, keywords)
        
        assertTrue("Should find fuzzy match", matches.contains("urgent"))
    }

    @Test
    fun testNoMatchWhenSemanticDisabled() {
        val keywords = listOf(
            Keyword(
                word = "opportunity", 
                context = "job work career",
                useSemanticMatching = false
            )
        )
        val text = "We are hiring for a position"
        
        val matches = semanticMatchingService.findSemanticMatches(text, keywords)
        
        assertTrue("Should not match when semantic matching is disabled", matches.isEmpty())
    }

    @Test
    fun testMultipleMatches() {
        val keywords = listOf(
            Keyword(word = "job", useSemanticMatching = true),
            Keyword(
                word = "trade", 
                context = "crypto bitcoin forex stock market",
                useSemanticMatching = true
            )
        )
        val text = "Looking for job and also interested in crypto trading"
        
        val matches = semanticMatchingService.findSemanticMatches(text, keywords)
        
        assertEquals("Should find multiple matches", 2, matches.size)
        assertTrue("Should contain job", matches.contains("job"))
        assertTrue("Should contain trade", matches.contains("trade"))
    }

    @Test
    fun testWordAssociations() {
        val keywords = listOf(
            Keyword(word = "sale", useSemanticMatching = true)
        )
        val text = "Great discount available, 50% off all items"
        
        val matches = semanticMatchingService.findSemanticMatches(text, keywords)
        
        assertTrue("Should find word association match", matches.contains("sale"))
    }
}