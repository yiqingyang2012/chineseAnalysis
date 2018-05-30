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

public class HanlpDemo
{
    static int i = 0;
    public final static int THRESHOLD = 20;
    static int tmp = 0;

    public static void main(String[] args) throws IOException
    {
        String inputString = readFile(null);
        Path sourceFile = Paths.get("data/raw");
        Path desFile = Paths.get("data/seged");

        //parseDependencyWithChTag(inputString);
        //parseDependencyWithEnTag(inputString);
        System.exit(0);
        segDir(sourceFile, desFile);
        ngramStatistic(desFile);

        Iterator titer=vocabulary.entrySet().iterator();  
        while(titer.hasNext()){  
            Map.Entry ent=(Map.Entry )titer.next();  
            String keyt=ent.getKey().toString();
            int valuet=(int)ent.getValue();
            if(valuet > HanlpDemo.THRESHOLD)
                System.out.println(keyt+"=="+valuet);  
        }  
    }

    public static void nerTest()
    {
        String[] testCase = new String[]{
                "签约仪式前，秦光荣、李纪恒、仇和等一同会见了参加签约的企业家。",
                "王国强、高峰、汪洋、张朝阳光着头、韩寒、小四",
                "张浩和胡健康复员回家了",
                "王总和小丽结婚了",
                "编剧邵钧林和稽道青说",
                "这里有关天培的有关事迹",
                "龚学平等领导,邓颖超生前",
                };
        Segment segment = HanLP.newSegment().enableNameRecognize(true);
        for (String sentence : testCase)
        {
            List<Term> termList = segment.seg(sentence);
            System.out.println(termList);
        }
    }

    public static String segText(String input)
    {
        List<Term> termList = StandardTokenizer.segment(input);
        StringBuilder sb = new StringBuilder(termList.size()+2);
        for(Term term : termList){
            sb.append(term.word+' ');
        }

        return sb.toString();
    }

    public static void parseDependencyWithChTag(String input)
    {
        CoNLLSentence sentence = HanLP.parseDependency(input);
        System.out.println(sentence);
        /*
        // 可以方便地遍历它
        for (CoNLLWord word : sentence)
        {
            System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
        }
        */
        // 也可以直接拿到数组，任意顺序或逆序遍历
        CoNLLWord[] wordArray = sentence.getWordArray();
        for (int i = wordArray.length - 1; i >= 0; i--)
        {
            CoNLLWord word = wordArray[i];
            System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
        }
        // 还可以直接遍历子树，从某棵子树的某个节点一路遍历到虚根
        CoNLLWord head = wordArray[12];
        while ((head = head.HEAD) != null)
        {
            if (head == CoNLLWord.ROOT) System.out.println(head.LEMMA);
            else System.out.printf("%s --(%s)--> ", head.LEMMA, head.DEPREL);
        }
    }

    public static void parseDependencyWithEnTag(String input)
    {
        IDependencyParser parser = new NeuralNetworkDependencyParser().enableDeprelTranslator(false);
        System.out.println("parseDependencyWithEnTag");
        System.out.println(parser.parse(input));
    }

    public static String readFile(Path inputFile) throws IOException
    {
        List<String> lines;
        if(inputFile != null)
            lines = Files.readAllLines(inputFile);
        else
            lines = Files.readAllLines(Paths.get("./input.txt"));

        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(line);
        }
        String contents = sb.toString();
        return contents;
    }

    public static void segDir(Path sourceDir,Path destDir) throws IOException,NotDirectoryException
    {
        // Get all file under sourceDir
        File dirObj = sourceDir.toFile();
        
        File file;
        Path sourceFilePath;
        Path desFilePath;
        
        
        // If desDir does not exist, Create it!
        file = destDir.toFile();
        if(! file.exists())
        {
            file.mkdirs();
        }
        if(dirObj.isDirectory())
        {
            String[] sourceFileNames = dirObj.list();
            for(String fileName : sourceFileNames)
            {   
                sourceFilePath = Paths.get(sourceDir.toString(),fileName);
                file = sourceFilePath.toFile();
                if(file.isFile())
                {                   
                    desFilePath = Paths.get(destDir.toString(),fileName+".seg");
                    segFile(sourceFilePath, desFilePath);
                    continue;
                }
                else
                {
                    desFilePath = Paths.get(destDir.toString(),fileName);
                    segDir(sourceFilePath, desFilePath);
                }
            }
        }
        else
        {
            throw new NotDirectoryException(sourceDir.toRealPath(null).toString());
        }
    }

    public static void segFile(Path sourceFilePath,Path destFilePath) throws IOException
    {
        FileOutputStream outputFileStream;
        byte[] byteTypeFile;
        String passage;
        String segmentedPassage;
        
        // Create destFile if it not exist
        File outputFile = destFilePath.toFile();
        if(!outputFile.exists())
        {
            outputFile.createNewFile();
        }
        // Create output stream     
        try {
            outputFileStream = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e){          
            throw new FileNotFoundException();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputFileStream));
        // Read source file
        byteTypeFile = Files.readAllBytes(sourceFilePath);
        passage = new String(byteTypeFile, "GB2312");
        String[] sentences = passage.split("\n");
        for(String sentence : sentences)
        {
            sentence = CharMatcher.WHITESPACE.trimAndCollapseFrom(sentence, ' ');
            sentence = sentence.replaceAll(" ", "");
            segmentedPassage = segText(sentence);
            bufferedWriter.write(segmentedPassage + "\n");
        }
        bufferedWriter.close();
        outputFileStream.close();
    }

    public static TreeMap<String,Integer> vocabulary = new TreeMap<String,Integer>();

    public static void ngramStatistic(Path sourceDir) throws IOException,NotDirectoryException
    {
        // Get all file under sourceDir
        File dirObj = sourceDir.toFile();
        File file;
        Path sourceFilePath;
        

        if(dirObj.isDirectory())
        {
            String[] sourceFileNames = dirObj.list();
            for(String fileName : sourceFileNames)
            {   
                sourceFilePath = Paths.get(sourceDir.toString(),fileName);
                file = sourceFilePath.toFile();
                if(file.isFile())
                {                   
                    readAndstatistic(sourceFilePath);
                    continue;
                }
                else
                {
                    ngramStatistic(sourceFilePath);
                }
            }
        }
        else
        {
            throw new NotDirectoryException(sourceDir.toRealPath(null).toString());
        }
    }

    
    public static void readAndstatistic(Path sourceFilePath)
    throws IOException,NotDirectoryException
    {
        byte[] byteTypeFile;
        String passage;
        int NGRAM = 5;

        // Read source file
        byteTypeFile = Files.readAllBytes(sourceFilePath);
        passage = new String(byteTypeFile, "UTF-8");

        String sentences[] = passage.split("\n");

        for (String sentence : sentences)
        {
            sentence = sentence.trim();
            if(sentence.indexOf("null") != -1)
                continue;

            String[] words = sentence.replaceAll("\\pP" , "<PUK>").split(" ");;
            if(words.length < NGRAM)
                continue;

            for(int i=0; i+NGRAM< words.length; i++)
            {
                Boolean drop = false;
                String tmpNgram = "";
                for(int j = 0; j<NGRAM; j++)
                {
                    if(words[i+j] == "null")
                    {
                        System.out.println(passage);
                        System.exit(0);
                    }

                    if(words[i+j].indexOf("<PUK>") != -1)
                    {
                        drop = true;
                        break;
                    }
                    tmpNgram += words[i+j];
                }


                if(drop)
                    continue;

                if(vocabulary.containsKey(tmpNgram))
                {
                    vocabulary.put(tmpNgram, vocabulary.get(tmpNgram)+1);
                }
                else
                {
                    if(tmpNgram.indexOf("<PUK>") != -1)
                    {
                        System.out.println("error happend");
                        System.out.println(sourceFilePath.toString());
                        System.out.println(sentence);
                        System.out.println(tmpNgram);
                        System.exit(0);
                    }
                    vocabulary.put(tmpNgram, 1);
                }
            }
        }
    }
}
