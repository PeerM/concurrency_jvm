package de.hs_augsburg.meixner.utils.monitoring;

public class ThreadMonitor extends Thread {
    public void run() {
      while (true) {
        System.out.println("new turn");
        getThreadGroup().list();
        try { Thread.sleep(2000); } 
        catch(InterruptedException e) {}
      }
    }
  }
