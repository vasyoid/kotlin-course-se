package ru.hse.spb

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class TestSource {

    private val graphSimple = arrayListOf(
            arrayListOf(1, 2),
            arrayListOf(0, 3),
            arrayListOf(0, 3),
            arrayListOf(1, 2)
    )

    private val graphNormal = arrayListOf(
            arrayListOf(1, 2),
            arrayListOf(0, 2),
            arrayListOf(0, 1, 3, 4),
            arrayListOf(2, 5),
            arrayListOf(2),
            arrayListOf(3)
    )

    private val cycleSimple = arrayListOf(0, 2, 3, 1, 0)

    private val cycleNormal = arrayListOf(0, 2, 1, 0)

    @Test
    fun findCycleSimpleInput() {
        assertEquals(
            "Cycles differ!",
            cycleSimple,
            findCycle(graphSimple)
        )
    }

    @Test
    fun findCycleNormalInput() {
        assertEquals(
            "Cycles differ!",
            cycleNormal,
            findCycle(graphNormal)
        )
    }

    @Test
    fun countDistsSimpleInput() {
        assertArrayEquals("Results differ!", intArrayOf(0, 0, 0, 0), countDists(graphSimple, cycleSimple))
    }

    @Test
    fun countDistsNormalInput() {
        assertArrayEquals("Results differ!", intArrayOf(0, 0, 0, 1, 1, 2), countDists(graphNormal, cycleNormal))
    }
}