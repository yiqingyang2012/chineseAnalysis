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
import com.hankcs.hanlp.seg.Segment;
import java.util.ArrayList;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.SentenceUtils;
import java.util.Arrays;  
import com.google.common.base.Splitter;
import com.hankcs.hanlp.dependency.IDependencyParser;
import com.hankcs.hanlp.corpus.tag.Nature;

public class HanlpFunction
{
    public static List<Term> hannlpNpChunk(String input)
    {
        List<Term> termList = StandardTokenizer.segment(input);
        List<String> words = new ArrayList<String>(); 
        StringBuilder sb = new StringBuilder(termList.size()+2);

        List<Term> result = new ArrayList<Term>();
        for(int index = 0; index < termList.size() -1; )
        {
          if (termList.get(index).nature.startsWith('n'))
          {
            Term tmp = termList.get(index);
            for ( int j=index+1; j < termList.size(); j++)
            {
              if(termList.get(j).nature.startsWith('n'))
              {
                tmp.word += termList.get(j).word;
                tmp.nature = Nature.n;
              }
              else
              {
                result.add(tmp);
                index = j;
                break;
              }
            }
          }
          else
          {
            result.add(termList.get(index));
            index++;
          }
        }

        return result;
    }

    public static void hanlpDependencyparser(List<Term> input)
    {
        IDependencyParser parser = new NeuralNetworkDependencyParser().enableDeprelTranslator(true);
        System.out.println(parser.parse(input));
    }
}
