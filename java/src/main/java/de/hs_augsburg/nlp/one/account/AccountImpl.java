package de.hs_augsburg.nlp.one.account;

import de.hs_augsburg.meixner.account.*;

import java.util.concurrent.locks.ReentrantLock;


enum AccountImpl {
    UNSAFE(new UnsafeAccount()),
    MONITOR(new MonitorAccount()),
    LESS_MONITOR(new LessMonitorAccount()),
    WAITNOTIFY(new WaitNotifyAccount()),
    LOCKFACADE(new LockFacadeAccount(new ReentrantLock())),
    SPINLOCKFACADE(new SpinLockFacadeAccount()),
    ATOMIC(new AtomicAccount()),
    TASLOCKFACADE(new LockFacadeAccount(new TASLock())),
    TTASLOCKFACADE(new LockFacadeAccount(new TTASLock())),
    ATOMIC_ADDER_ACCOUNT(new AtomicAdderAccount()),
    ADDER_ACCOUNT(new AdderAccount());

    final Account account;

    private AccountImpl(Account account) {
        this.account = account;
    }
}