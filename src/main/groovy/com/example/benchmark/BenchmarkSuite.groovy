package com.example.benchmark

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.mutable.MutableBoolean

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@CompileStatic
@Slf4j
class BenchmarkSuite implements Callable {

    MutableBoolean stop = new MutableBoolean(false)
    BenchmarkReport report
    List<Callable> tasks

    @Override
    def call() {
        tasks.each {
            it.call()
        }
    }

    void start() {
        try {
            for (; ;) {
                call()
                report.increment()
                if (stop.isTrue()) {
                    break
                }
                Thread.yield()
            }
        } catch (Exception e) {
            log.error("Exception in task", e)
            stop.setTrue()
        }
    }

    ExecutorService startTasks(Map tasks) {

        report.taskNames = tasks.keySet().toString()
        def tp = Executors.newFixedThreadPool(report.threadCount)

        log.info "Starting the tasks: ${tasks.keySet()} in ${report.threadCount} thread(s), test duration: ${report.duration}ms"
        report.start()
        report.threadCount.times {
            tp.submit({
                new BenchmarkSuite(
                        tasks: tasks.values() as List<Callable>,
                        stop: stop,
                        report: report
                ).start()
            })
        }
        tp
    }

    void startMonitoringThread() {

        def stp = Executors.newSingleThreadExecutor()

        log.info "Starting monitoring thread for ${report.threadCount} concurrent thread(s), cheking interval: ${report.checkSpeedInterval}ms"
        stp.submit({
            while (true) {
                if (report.finished() || this.stop.isTrue()) {
                    break
                }
                Thread.sleep(report.checkSpeedInterval)
                report.logSpeed()
            }
            this.stop.setTrue()
        }).get()
        stp.shutdown()
    }


    static void executeBenchmark(BenchmarkReport report, Map<String, Callable> tasks) {
        def suite = new BenchmarkSuite(report: report)
        ExecutorService es = suite.startTasks(tasks)
        suite.startMonitoringThread()
        es.shutdown()
    }
}
