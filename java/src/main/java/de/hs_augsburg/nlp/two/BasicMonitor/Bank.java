package de.hs_augsburg.nlp.two.BasicMonitor;



import java.util.List;
import java.util.Map;

public class Bank {
    private Map<Long,Account> accounts;
    private NumberGenerator accNoGenerator;
    public void deposit(long accNo, int amount){

    }
    public void withdraw(long accNo, int amount){

    }
    public void transfer(long from, long to, int amount){

    }
    public List<Entry> getAccountEntries(long accNo){
        return null;
    }

    private Account getAcc(long accNo){
        return null;
    }
    public long createAccount(){
        return 0;
    }
}
