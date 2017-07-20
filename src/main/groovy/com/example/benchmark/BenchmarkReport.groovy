package com.example.benchmark

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.time.StopWatch

import java.util.concurrent.atomic.AtomicLong

import static org.apache.commons.lang3.time.DurationFormatUtils.formatDurationHMS

/**
 *
 */
@CompileStatic
@Slf4j
class BenchmarkReport {

    private AtomicLong counter = new AtomicLong()
    private StopWatch sw = new StopWatch()
    long duration
    Integer threadCount
    int checkSpeedInterval
    boolean logInIntervalsEnabled = true
    String taskNames

    private long lastCounterValue
    private long lastTimeValue

    void start() {
        sw.start()
    }

    void increment() {
        counter.incrementAndGet()
    }

    boolean finished() {
        def time = sw.getTime()
        def res = time >= duration
        if (res) {
            def count = counter.get()
            def perSecSpeed = (count * 1000 / time) as Long
            def perHourSpeed = perSecSpeed * 60 * 60
            log.info "Tasks: ${taskNames}, total executions ${count} for ${formatDurationHMS(time)}, " +
                    "speed: ${perSecSpeed} per/sec, ${perHourSpeed} per/h, with $threadCount thread(s)"
        }
        res
    }

    def logSpeed() {
        if (!logInIntervalsEnabled) {
            return
        }

        long count = counter.get(), countDelta = count - lastCounterValue
        long time = sw.getTime(), timeDelta = time - lastTimeValue
        long perSecSpeed = (countDelta * 1000 / timeDelta).longValue()
        long perHourSpeed = perSecSpeed * 60 * 60

        log.info "Tasks: ${taskNames}, executed ${countDelta} times for ${formatDurationHMS(timeDelta)}, " +
                "speed: ${perSecSpeed} per/sec, ${perHourSpeed} per/h, with $threadCount thread(s)"
        lastCounterValue = count
        lastTimeValue = time
    }
}
