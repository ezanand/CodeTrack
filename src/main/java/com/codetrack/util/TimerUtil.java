package com.codetrack.util;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class TimerUtil {

    public static long measureExecutionTime(Runnable task) {
        long start = System.nanoTime();
        task.run();
        long end = System.nanoTime();
        return TimeUnit.NANOSECONDS.toMillis(end - start);
    }

    public static <T> TimedResult<T> measureExecutionTime(Callable<T> task) throws Exception {
        long start = System.nanoTime();
        T result = task.call();
        long end = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(end - start);
        return new TimedResult<>(result, duration);
    }

    public static class TimedResult<T> {
        private final T result;
        private final long executionTimeMs;

        public TimedResult(T result, long executionTimeMs) {
            this.result = result;
            this.executionTimeMs = executionTimeMs;
        }

        public T getResult() {
            return result;
        }

        public long getExecutionTimeMs() {
            return executionTimeMs;
        }

        @Override
        public String toString() {
            return "TimedResult{result=" + result + ", executionTimeMs=" + executionTimeMs + "ms}";
        }
    }
}
