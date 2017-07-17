package com.example.benchmark

import groovy.transform.PackageScope

import java.util.concurrent.Callable

/**
 *
 */
@PackageScope
class Util {

    static executeBenchmarks(String testName, Callable task) {

        def duration = 30_000
        def checkSpeedInterval = 30_001 // больше блительности чтобы промеж результат не выводился

        [1, 2, 4, 8, 10, 16].collect { threadCount ->
            new BenchmarkReport(
                    duration: duration,
                    threadCount: threadCount,
                    checkSpeedInterval: checkSpeedInterval)
        }.each { report ->
            BenchmarkSuite.executeBenchmark(report, [("$testName" as String): task])
        }
    }
}
