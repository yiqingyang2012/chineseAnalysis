import java.io.*;
import java.util.*;

import edu.stanford.nlp.coref.CorefCoreAnnotations;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.io.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;
import java.nio.charset.StandardCharsets;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import java.util.ArrayList;
import java.util.Iterator;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import edu.stanford.nlp.ling.Label;

public class StanfordParser {

  public StanfordCoreNLP pipeline = null;
  PrintWriter out = null;
  LexicalizedParser lp = null;
  public final String PARSER_MODEL_PATH = "edu/stanford/nlp/models/lexparser/chineseFactored.ser.gz";

  public StanfordParser() throws FileNotFoundException
  {
    String props="StanfordCoreNLP-chinese.properties";//prop file name
    pipeline = new StanfordCoreNLP(props);
    String[] options = { "-maxLength", "140", "-MAX_ITEMS","500000"};
    lp = LexicalizedParser.loadModel(PARSER_MODEL_PATH, options);
    out = new PrintWriter("log.txt");
  }


  public List<String> getSentence(String fileName)
  {
    Annotation annotation;
    String fileContent = IOUtils.slurpFileNoExceptions(fileName);
    String[] paragraphs = fileContent.split("\n");
    List<String> result = new ArrayList<String>();


    for (String paragraph : paragraphs)
    {
      if(paragraph.length() == 0)
        continue;

      annotation = new Annotation(paragraph);
      pipeline.annotate(annotation);
      List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

      if (sentences != null && ! sentences.isEmpty()) 
      {
        for (CoreMap sentence : sentences)
        {
          String text = sentence.get(CoreAnnotations.TextAnnotation.class);
          result.add(text);
        }
      }
    }

    return result;
  }

  public List<String> getSentence(Path sourceFilePath) throws IOException
  {
    byte[] byteTypeFile;
    byteTypeFile = Files.readAllBytes(sourceFilePath);
    String fileContent = new String(byteTypeFile, "GB2312");
    String[] paragraphs = fileContent.split("\n");
    List<String> result = new ArrayList<String>();

    for (String paragraph : paragraphs)
    {
      if(paragraph.length() == 0)
        continue;

      Annotation annotation = new Annotation(paragraph);
      pipeline.annotate(annotation);
      List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

      if (sentences != null && ! sentences.isEmpty()) 
      {
        for (CoreMap sentence : sentences)
        {
          String text = sentence.get(CoreAnnotations.TextAnnotation.class);
          result.add(text);
        }
      }
    }

    return result;
  }

  public void contextParser(List<CoreLabel> rawWords)
  {
    Tree parse = lp.apply(rawWords);
    parse.indexSpans(0);
    NpChunker(parse);
  }

  public void NpChunker(Tree parse)
  {
    List<Tree> result = new ArrayList<Tree>();
    HashSet<Tree> subNodes = new HashSet<Tree>();

    Iterator<Tree> it = parse.iterator();
    //System.out.println("NpChunker begin");
    while(it.hasNext()){
      Tree subTree = it.next();

      if( subTree.depth() == 2 &&
          subTree.label().value().equals("NP")
        )
      {
        Tree[] childs = subTree.children();
        if(childs.length < 2)
          continue;
      }
      
      if((subTree.depth() == 2 || subTree.depth() ==3 ) &&
          subTree.label().value().equals("NP")
        )
      {
        if(subNodes.contains(subTree))
        {
          System.out.println("find sub");
          continue;
        }

        result.add(subTree);

        Label label = subTree.label();
        if (label instanceof CoreMap) {
          CoreMap afl = (CoreMap) label;

          int begin = afl.get(CoreAnnotations.BeginIndexAnnotation.class);
          int end = afl.get(CoreAnnotations.EndIndexAnnotation.class);
          System.out.println(begin);
          System.out.println(end);
        }

        for (Tree sub : subTree.children())
        {
          subNodes.add(sub);
        }
        //out.println(subTree.label().value());
        printTree(subTree);
      }
    
    }
    
    //System.out.println("NpChunker end");
  }

  public void printTree(Tree subTree)
  {
    System.out.println("printTree");
    pipeline.getConstituentTreePrinter().printTree(subTree, out);
  }

  public static void main(String[] args) throws IOException,FileNotFoundException
  {
    String props="StanfordCoreNLP-chinese.properties";//prop file name
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    Annotation annotation;
    PrintWriter out = null;
    PrintWriter xmlOut = null;
    if(args.length >2)
    {
      out = new PrintWriter(args[2]);
    }
    
    if(args.length >3){
      xmlOut = new PrintWriter(args[3]);
    }


    annotation = new Annotation(IOUtils.slurpFileNoExceptions(args[0]));
    //annotation = new Annotation("能带来什么好处或者造成什么后果");
    pipeline.annotate(annotation);

    List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

    System.out.println(annotation.keySet());
    System.out.println();
    System.out.println("The top level annotation");
    System.out.println(annotation.toShorterString());
    System.out.println();

    String[] tokenAnnotations = {
            "Text",
            //"PartOfSpeech"
          };

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    StringBuilder sb = new StringBuilder(100);
    if (sentences != null && ! sentences.isEmpty()) {
      for(CoreMap sentence : sentences)
      {
        List<CoreLabel> tokens1 = sentence.get(CoreAnnotations.TokensAnnotation.class);
        for (CoreLabel token : tokens1) {
          sb.append((token.toShortString(tokenAnnotations)) + " ");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("\n");

        /*
        //only print context parser result
        Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
        if (tree != null) {
          out.println();
          out.println("Constituency parse: ");
          pipeline.getConstituentTreePrinter().printTree(tree, out);
        }
        */
        //will print all the information
        if(out != null)
        {
          pipeline.prettyPrint(annotation, out);
        }
        
        if (xmlOut != null) {
          pipeline.xmlPrint(annotation, xmlOut);//print in a xml
        }
      }
    }else{
      System.out.println("sentences is empty");
    }


  }

}
