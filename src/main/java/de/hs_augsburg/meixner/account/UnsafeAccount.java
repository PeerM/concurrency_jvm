package de.hs_augsburg.meixner.account;

public class UnsafeAccount implements Account {

    private int balance;

    public  void  deposit(int i) {
        balance += i;
    }

    public boolean withdraw(int i) {
        if (balance >= i) { // don't allow overdrawing
            balance -= i;
            return true;
        }
        return false;
        
//      while (balance < i) {
//      System.out.println("wait by sleeping:" + Thread.currentThread());
//      try {
//          Thread.sleep(1);
//      } catch (InterruptedException e) {
//          System.out.println(e);
//      }
//      ;
//  }
//  balance -= i;
//  return true;
    }

    public int getBalance() {
        return balance;
    }
    
    public String toString() {
        return "Konto mit Stand: " + balance;
    }
}
