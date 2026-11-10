package com.example

import com.example.model.DailyMissions
import com.example.model.MilestoneData
import com.example.model.RankSystem
import com.example.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testDateUtilsOperations() {
        val today = DateUtils.getTodayString()
        assertNotNull(today)
        assertTrue(DateUtils.isToday(today))
        assertFalse(DateUtils.isFuture(today))
        assertFalse(DateUtils.isPast(today))

        val yesterday = DateUtils.getPreviousDay(today)
        assertTrue(DateUtils.isPast(yesterday))
        assertFalse(DateUtils.isFuture(yesterday))

        val tomorrow = DateUtils.getNextDay(today)
        assertTrue(DateUtils.isFuture(tomorrow))
        assertFalse(DateUtils.isPast(tomorrow))

        val diff = DateUtils.getDaysDifference(yesterday, tomorrow)
        assertEquals(2, diff)
    }

    @Test
    fun testMilestonesSequence() {
        val milestones = MilestoneData.MILESTONES
        assertTrue(milestones.isNotEmpty())
        assertEquals(1, milestones.first().days)
        assertEquals(365, milestones.last().days)

        for (i in 0 until milestones.size - 1) {
            assertTrue(milestones[i].days < milestones[i + 1].days)
        }
    }

    @Test
    fun testRankSystemCalculations() {
        val rank0 = RankSystem.getRankForXp(0)
        assertEquals(1, rank0.level)
        assertEquals("Recruta da Vontade", rank0.title)

        val rankMid = RankSystem.getRankForXp(850)
        assertEquals(3, rankMid.level)
        assertEquals("Sentinela da Mente", rankMid.title)

        val rankMax = RankSystem.getRankForXp(5000)
        assertEquals(6, rankMax.level)
        assertEquals("Titã Inabalável", rankMax.title)
    }

    @Test
    fun testDailyMissionsCompletion() {
        val missions = DailyMissions("2026-11-10", workoutDone = true, coldShowerDone = true, screenDetoxDone = true)
        assertEquals(3, missions.completedCount)
        assertTrue(missions.isAllCompleted)

        val partial = DailyMissions("2026-11-10", workoutDone = true, coldShowerDone = false, screenDetoxDone = false)
        assertEquals(1, partial.completedCount)
        assertFalse(partial.isAllCompleted)
    }
}
