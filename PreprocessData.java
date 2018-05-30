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

public class PreprocessData
{
    static int i = 0;
    public final static int THRESHOLD = 20;
    static int tmp = 0;

    public static void main(String[] args) throws IOException
    {
        Path sourceFile = Paths.get("data/raw");
        Path desFile = Paths.get("data/after");

        //parseDependencyWithChTag(inputString);
        //parseDependencyWithEnTag(inputString);
        processDir(sourceFile, desFile);
 
    }

    public static void processDir(Path sourceDir,Path destDir) throws IOException,NotDirectoryException
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
                    processFile(sourceFilePath, desFilePath);
                    continue;
                }
                else
                {
                    desFilePath = Paths.get(destDir.toString(),fileName);
                    processDir(sourceFilePath, desFilePath);
                }
            }
        }
        else
        {
            throw new NotDirectoryException(sourceDir.toRealPath(null).toString());
        }
    }

    public static void processFile(Path sourceFilePath,Path destFilePath) throws IOException
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
        passage = AsciiUtil.sbc2dbcCase(passage);
        String[] sentences = passage.split("\n");
        for(String sentence : sentences)
        {
            if(sentence.indexOf("null") != -1)
                continue;
            sentence = CharMatcher.WHITESPACE.trimAndCollapseFrom(sentence, ' ');
            sentence = sentence.replaceAll(" ", "");
            bufferedWriter.write(sentence + "\n");
        }
        bufferedWriter.close();
        outputFileStream.close();
    }

}
