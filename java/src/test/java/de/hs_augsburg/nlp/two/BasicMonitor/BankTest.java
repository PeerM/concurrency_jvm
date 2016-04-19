package de.hs_augsburg.nlp.two.BasicMonitor;

import de.hs_augsburg.nlp.two.IBank;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import static org.junit.Assert.*;

class Action {
    final String name;

    final Consumer<IBank> test;

    public Action(String name, Consumer<IBank> test) {
        this.name = name;
        this.test = test;
    }
}


public class BankTest {
    private List<Long> accNos;

    private List<Action> getActions() {
        return Arrays.asList(
                new Action("deposit", iBank -> {
                    long accNo = accNos.get(ThreadLocalRandom.current().nextInt(accNos.size()));
                    iBank.deposit(accNo, 2);
                    iBank.getAccountEntries(accNo);
                }),
                new Action("create", iBank -> {
                    long accNo = iBank.createAccount();
                    iBank.deposit(accNo, 2);
                    List<Entry> entries = iBank.getAccountEntries(accNo);
                    assertEquals(1,entries.size());

                }));
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void createAccount() throws Exception {

    }


}