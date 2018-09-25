package ru.hse.spb

import java.util.*
import kotlin.collections.ArrayList

private fun findCycle(graph: List<List<Int>>, prevVertex: Int, curVertex: Int, used: BooleanArray): MutableList<Int> {
    if (used[curVertex]) {
        return arrayListOf(curVertex)
    }
    used[curVertex] = true
    for (nextVertex in graph[curVertex]) {
        if (nextVertex == prevVertex) {
            continue
        }
        val result = findCycle(graph, curVertex, nextVertex, used)
        if (result.isEmpty()) {
            continue
        }
        if (result.size > 1 && result.first() == result.last()) {
            return result
        }
        result.add(curVertex)
        return result
    }
    return arrayListOf()
}

fun findCycle(graph: List<List<Int>>): List<Int> {
    return findCycle(graph, 0, 0, BooleanArray(graph.size) { false })
}

fun countDists(graph: List<List<Int>>, cycle: List<Int>): IntArray {
    val queue = ArrayDeque<Int>()
    val dists = IntArray(graph.size) { -1 }
    for (vertex in cycle.dropLast(1)) {
        queue.addLast(vertex)
        dists[vertex] = 0
    }
    while (queue.isNotEmpty()) {
        val curVertex = queue.pollFirst()
        for (nextVertex in graph[curVertex]) {
            if (dists[nextVertex] < 0) {
                dists[nextVertex] = dists[curVertex] + 1
                queue.addLast(nextVertex)
            }
        }
    }
    return dists
}

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    val n = scanner.nextInt()
    val graph = List<MutableList<Int>>(n) { ArrayList() }
    for (i in 0 until n) {
        val x = scanner.nextInt() - 1
        val y = scanner.nextInt() - 1
        graph[x].add(y)
        graph[y].add(x)
    }
    for (dist in countDists(graph, findCycle(graph))) {
        print("$dist ")
    }
}

