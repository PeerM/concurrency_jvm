package de.hs_augsburg.meixner.utils.profiling;

public class ProfiledThread extends Thread {
    public ProfiledThread(Runnable runnable) {
        super(runnable);
    }

    public ProfiledThread(String name, Runnable runnable) {
        super(runnable,name);
    }
    @Override
    public void run() {
        Clock.getDefaultInstance().startRec(this);
        try {
            super.run();
        } finally {
            Clock.getDefaultInstance().stopRec(this);
        }  
    }
}
