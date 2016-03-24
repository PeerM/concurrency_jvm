package de.hs_augsburg.meixner.utils.profiling;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

public class Clock {
    public static final boolean OFF = false;
    public static final boolean REC_PROFILING = false;
    private final Map<Thread,Long> startCpuTimes = new HashMap<Thread,Long>();
    private final Map<Thread,Long> endCpuTimes = new HashMap<Thread,Long>();
    public static final Clock defaultInstance = new Clock();
    private volatile long startTime;
    
    public Clock() {
        if (OFF)
            return;
        resetTime();
    }
    
    public static Clock getDefaultInstance()  {
        return defaultInstance;
    }
    
    public synchronized void resetTime() {
        if (OFF)
            return;
        startTime = System.currentTimeMillis();
        
        for (Thread thread : endCpuTimes.keySet()) {
            startCpuTimes.remove(thread);
        }
        
        for (Thread thread : startCpuTimes.keySet()) {
            if (thread.isAlive())
                startCpuTimes.put(thread,getCpuTime(thread));
            else
                startCpuTimes.remove(thread);
        }
        
        Thread currentThread = Thread.currentThread();
        if (!startCpuTimes.containsKey(currentThread))
             startCpuTimes.put(currentThread, getCpuTime(currentThread));
        endCpuTimes.clear();
    }
    
    public synchronized long elapsedTime() {
        if (OFF)
            return -1;
        return System.currentTimeMillis() - startTime;
    }
    
    
    public synchronized long elapsedCpuTime() {
        if (OFF)
            return -1;
        long accuCpuTime = 0;
        for (Thread thread: startCpuTimes.keySet()) {
            accuCpuTime += elapsedCpuTime(thread);
        }           
        return accuCpuTime;
    }

    public synchronized long elapsedCpuTime(Thread thread) {
        if (OFF)
            return -1;
        Long startTime = startCpuTimes.get(thread);
        if (startTime == null)
            startTime = 0l;
        Long endTime = endCpuTimes.get(thread);
        if (endTime == null) {
            if (!thread.isAlive())
                throw new RuntimeException("Not stopped recording at end of run: " + thread);
            endTime = getCpuTime(thread);
        }
        Long elapsed = endTime - startTime;
        return elapsed;
    }
    
    public synchronized void currentThreadTimeStatus(String s) {
        if (OFF)
            return;
        long currentElapsedCpu = elapsedCpuTime(Thread.currentThread());
        System.out.println(s +" TIME elapsed:" + elapsedTime() + "ms,  current:" +Thread.currentThread() + " cpu:" + currentElapsedCpu +"ms");     
    }
    
    
    public synchronized void shortTimeStatus(String s) {
        if (OFF)
            return;
        long currentElapsedCpu = elapsedCpuTime(Thread.currentThread());
        System.out.println(s +" TIME elapsed:" + elapsedTime() + "ms, Total CPU:" + elapsedCpuTime() + 
                "ms, current:" +Thread.currentThread() + ":" + currentElapsedCpu +"ms");
    }
    
    public synchronized void fullTimeStatus(String s) {
        if (OFF)
            return;
        StringBuilder sb = new StringBuilder();
        long accuCpuTime = 0;
        for (Thread thread: startCpuTimes.keySet()) {
            long cpuTime = elapsedCpuTime(thread);
            sb.append("\n ");
            sb.append(thread.toString());
            sb.append(":");
            sb.append(cpuTime);
            sb.append("ms");
            accuCpuTime += cpuTime;
        }           
        System.out.println(s +" TIME elapsed:" + elapsedTime() + "ms, Total CPU:" + accuCpuTime + "ms" + sb.toString());
    }
    
    
    public synchronized void startRec(Thread thread) {
        if (OFF)
            return;
        if (REC_PROFILING)
            System.out.println("start record " + thread);
        startCpuTimes.put(thread,getCpuTime(thread));
        endCpuTimes.remove(thread);
    }

    public synchronized void stopRec(Thread thread) {
        if (OFF)
            return;
        if (REC_PROFILING)
            System.out.println("stop record " + thread);
        endCpuTimes.put(thread,getCpuTime(thread));
    }

    private long getCpuTime(Thread thread) {
         return ManagementFactory.getThreadMXBean(). getThreadCpuTime(thread.getId())/1000000;
    }

    public static void reset() {
        defaultInstance.resetTime();
    }
    
    public static void ss(String s) {
        defaultInstance.shortTimeStatus(s);
    }
    
    public static void fs(String s) {
        defaultInstance.fullTimeStatus(s);
    }
   
    public static void cs(String s) {
        defaultInstance.currentThreadTimeStatus(s);
    }

    public static long elapsed() {
        return defaultInstance.elapsedTime();
    } 
    
    public static long elapsedCpu() {
        return defaultInstance.elapsedCpuTime();
    } 
    
    public static void startRec() {
        defaultInstance.startRec(Thread.currentThread());
    }
    
    public static void stopRec() {
        defaultInstance.stopRec(Thread.currentThread());
    }
    
    
    public static void main(String[] args) throws InterruptedException {
        Clock.reset();
        Clock.ss("At the beginning: ");
            Thread.sleep(200);
    
        Clock.ss("after sleep for 200: ");
        
        Runnable r = new Runnable() {
            public void run() {
                Clock.startRec();
                for (long n=2; n <= 10000000; n++) {
                    double d = Math.sqrt(n);
                    if ( d < 0) System.out.println("ha");
                }
                Clock.ss("in profiled thread:");
                Clock.stopRec();    
            }
        };
        (new ProfiledThread(r)).start();
        Thread.sleep(1000);
        Clock.ss("at end:");
        Clock.fs("at end:");
    }
}
