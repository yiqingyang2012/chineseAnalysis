import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.dependency.nnparser.NeuralNetworkDependencyParser;
import com.hankcs.hanlp.dependency.IDependencyParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.io.*;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Map;
import com.google.common.base.CharMatcher;

public class StringFunction
{
    public void stringTest() throws UnsupportedEncodingException
    {
        System.out.println("stringTest");
        String input = "（贝宝）";
        input = AsciiUtil.sbc2dbcCase(input);
        //input = input.replaceAll("[\\pZ‘'“”]", "");
        System.out.println(input); 

        byte[] bytes = input.getBytes();
        printHexString(bytes);
        String input2 = new String(input.getBytes() , "utf-8");
        System.out.println("after:"); 
        bytes = input2.getBytes();
        printHexString(bytes);
        input2 = input2.trim();
        System.out.println(input2); 

    }

    public void testGuava()
    {
        String s = "（贝宝）";
        System.out.println("testGuava");
        String s1 = CharMatcher.WHITESPACE.trimAndCollapseFrom(s, ' ');
        s1 = s1.replaceAll(" ", "");
        System.out.println(s1);

        String noDigits = CharMatcher.javaDigit().replaceFrom(s, "");
        System.out.println(noDigits);
    }

    public void printHexString( byte[] b) { 
        for (int i = 0; i < b.length; i++) 
        { 
            String hex = Integer.toHexString(b[i] & 0xFF); 
            if (hex.length() == 1) { 
                hex = '0' + hex; 
        } 
            System.out.println(hex.toUpperCase() ); 
        } 

    } 

    public static boolean isChinesePunctuation(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
                || ub == Character.UnicodeBlock.VERTICAL_FORMS) {
            return true;
        } else {
            return false;
        }
    }
    //str = str.replaceAll("[\\pP‘’“”]", "");
    public static void main(String[] args) throws IOException
    {
        StringFunction sf = new StringFunction();
        sf.testGuava();
        sf.stringTest();
        String s = "；";
        System.out.println(sf.isChinesePunctuation(s.charAt(0)));
    }
}
