package de.hs_augsburg.nlp.four.mnemonic;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.pcollections.PSet;
import org.pcollections.PStack;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class JavaCoderTest {

    private CoderAdapter scala;
    private JavaCoder java;

    @Before
    public void setUp() throws Exception {
        List<String> words = IOUtils.readLines(getClass().getClassLoader().getResourceAsStream("filtered-english.txt"), "UTF-8");
        scala = new CoderAdapter(words);
        java = new JavaCoder(words);

    }

    @Test
    @Ignore
    public void name() throws Exception {
//        Set<String> res = scala.translate("843");
//        Set<List<String>> res2 = scala.encode("843843");
//        String n = java.wordCode("The");
        Set<PStack<String>> encode = java.encode("8434");
//        Set<List<String>> encode = scala.encode("843");
    }

    @Test
    public void basic() throws Exception {
        String the = "843";
        Assert.assertEquals(scala.translate(the), java.translate(the));
    }
    // information 46367628466
    // time  8463
    // business 28746377
    // the that 843 8428

    @Test
    public void regression() throws Exception {
        String information = "46367628466";
        Assert.assertTrue(scala.translate(information).contains("information"));
        Assert.assertTrue(java.translate(information).contains("information"));
        String time = "8463";
        String business = "28746377";
        String the = "843";
        String that = "8428";
        Assert.assertTrue(scala.translate(the+that).contains("the that"));
        Assert.assertTrue(java.translate(the+that).contains("the that"));
        for (String s : Arrays.asList(information, time, business, the + that)) {
            Assert.assertEquals(scala.translate(s), java.translate(s));
        }
    }
}