package de.hs_augsburg.nlp.one.account;

import de.hs_augsburg.meixner.account.*;
import de.hs_augsburg.meixner.utils.profiling.Clock;
import de.hs_augsburg.meixner.utils.profiling.ProfiledThread;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class AccountAccessVanilla {
    private static final int NO_RUNS = 1;
    private static final int NO_CUSTOMERS = 20;
    private static final int NO_VISITS = 100000000;
    private static final boolean PAYIN_ONLY = true;

    private static AccountImpl[] accountImpls = {
//                                                 AccountImpl.UNSAFE, 
            AccountImpl.MONITOR,
            AccountImpl.LESS_MONITOR,
//                                                 AccountImpl.WAITNOTIFY,
            AccountImpl.LOCKFACADE,
//                                                 AccountImpl.SPINLOCKFACADE,
            AccountImpl.ATOMIC,
//                                                 AccountImpl.TASLOCKFACADE,
//                                                 AccountImpl.TTASLOCKFACADE
            AccountImpl.ADDER,
            AccountImpl.ATOMIC_ADDER,
    };

    private static class StatisticElement {
        public final AccountImpl impl;
        public final long elapsedTime;
        public final long cpuTime;

        public StatisticElement(AccountImpl impl, long elapsedTime, long cpuTime) {
            this.impl = impl;
            this.elapsedTime = elapsedTime;
            this.cpuTime =cpuTime;
        }
    }

    private static List<StatisticElement> statistics = new Vector<StatisticElement> ();

    private static class Customer implements Runnable {
        private Account account;
        private boolean payin; // Einzahler oder Auszahler

        public Customer(Account a, boolean pin) {
            account = a;
            payin = pin;
        }

        public void run() {
            for (int i = 1; i <= NO_VISITS/NO_CUSTOMERS; i++) { // try also more contended case
                if (payin)
                    account.deposit(1);
                else
                    account.withdraw(1);
            }
        }
    }


    private static void runOn(Account account) {
        System.out.println("KontoStand am Anfang: " + account.getBalance());

        Thread[] customerThreads = new Thread[NO_CUSTOMERS];

        for (int i = 0; i < NO_CUSTOMERS; i++) {
            boolean payIn = true;
            if (!PAYIN_ONLY)
                payIn = (i % 2) == 0;
            customerThreads[i] = new ProfiledThread(new Customer(account, payIn ));
//            System.out.println(customerThreads[i]);
        }

        Clock.reset();
        for (int i = 0; i < NO_CUSTOMERS; i++) {
            customerThreads[i].start();
        }

        for (int i = 0; i < NO_CUSTOMERS; i++) {
            try {
                customerThreads[i].join();
            } catch (InterruptedException e) {
                System.out.println(e);
            }

        }
        System.out.println("KontoStand am Ende: " + account.getBalance());
    }

    private static void printStatistics() {
        System.out.println();
        System.out.println("--------------------------------------");
        for ( StatisticElement element : statistics) {
            System.out.println("time," + element.elapsedTime + ",cputime," + element.cpuTime + ",impl," + element.impl);
        }
        System.out.println("--------------------------------------");
    }

    public static void main(String[] args) throws IOException {
//        System.out.println("hit enter to continue");
//        System.in.read();
        for (int i=0; i < NO_RUNS; i++) {
            for (AccountImpl impl: accountImpls) {
                System.out.println();
                System.out.println("Account Type:" + impl);
                runOn(impl.account);
//                Clock.fs("Account Type:" + impl);
                System.out.println("Vergangene Zeit: " + Clock.elapsed());
                statistics.add(new StatisticElement(impl,Clock.elapsed(), Clock.elapsedCpu()));
            }
        }
        printStatistics();
    }
}
