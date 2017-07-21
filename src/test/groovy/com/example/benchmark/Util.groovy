package com.example.benchmark

import groovy.transform.CompileStatic

import java.util.concurrent.Callable

/**
 *
 */
@CompileStatic
class Util {

    static executeBenchmarks(String testName, Callable task) {
        execute(testName, task, [1, 2, 4, 8, 10, 16])
    }

    static executeBenchmarks(String testName, Callable task, List<Integer> multiThreadCase) {
        execute(testName, task, multiThreadCase)
    }

    private static List<BenchmarkReport> execute(String testName, Callable task, List<Integer> multiThreadCase) {
        multiThreadCase.collect { threadCount ->
            new BenchmarkReport(
                    duration: 30_000,
                    threadCount: threadCount,
                    checkSpeedInterval: 10_000,
                    logInIntervalsEnabled: false)
        }.each { report ->
            BenchmarkSuite.executeBenchmark(report, [("$testName" as String): task])
        }
    }
}
