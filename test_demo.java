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
public class test_demo {
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
      /*
    for (CoreLabel token : tokens) {
      String[] tokenAnnotations = {
              "Text", "PartOfSpeech", "Lemma", "Answer", "NamedEntityTag",
              "CharacterOffsetBegin", "CharacterOffsetEnd", "NormalizedNamedEntityTag",
              "Timex", "TrueCase", "TrueCaseText", "SentimentClass", "WikipediaEntity" };
  
      System.out.println(token.toShortString(tokenAnnotations));
    }
  */
    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    StringBuilder sb = new StringBuilder(100);
    if (sentences != null && ! sentences.isEmpty()) {
      for(CoreMap sentence : sentences)
      {
        System.out.println(sentence.keySet());
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

    writeToFile(sb.toString(), args[1]);
  }

  public static void writeToFile(String tagResult, String outputFile) throws IOException
  {
    FileOutputStream outputFileStream;

    try 
    {
      outputFileStream = new FileOutputStream(outputFile);
    } 
    catch (FileNotFoundException e)
    {      
      throw new FileNotFoundException();
    }


    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputFileStream));

    bufferedWriter.write(tagResult);
    bufferedWriter.close();
    outputFileStream.close();   
  }
}
