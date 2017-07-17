package com.example.benchmark

import groovy.transform.PackageScope

import java.util.concurrent.Callable

/**
 *
 */
@PackageScope
class Util {

    static executeBenchmarks(String testName, Callable task) {
        BenchmarkSuite.executeBenchmarks(
                [
                        new BenchmarkReport(
                                duration: 30_000,
                                threadCount: 1,
                                checkSpeedInterval: 10_000),
                        new BenchmarkReport(
                                duration: 30_000,
                                threadCount: 2,
                                checkSpeedInterval: 10_000),
                        new BenchmarkReport(
                                duration: 30_000,
                                threadCount: 4,
                                checkSpeedInterval: 10_000),
                        new BenchmarkReport(
                                duration: 30_000,
                                threadCount: 8,
                                checkSpeedInterval: 10_000),
                        new BenchmarkReport(
                                duration: 30_000,
                                threadCount: 10,
                                checkSpeedInterval: 10_000),
                        new BenchmarkReport(
                                duration: 30_000,
                                threadCount: 16,
                                checkSpeedInterval: 10_000)
                ],
                [("$testName" as String): task])
    }
}
