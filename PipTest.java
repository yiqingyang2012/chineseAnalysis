
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

/** This class demonstrates building and using a Stanford CoreNLP pipeline. */
public class PipTest {

  private static final String basedir = System.getProperty("SegDemo", "data");
  public CRFClassifier<CoreLabel> segmenter = null;
  public MaxentTagger posTager;

  public PipTest(Properties props)
  {
    initSegment();
    initPostag(props);

  }
  /** Usage: java -cp "*" StanfordCoreNlpDemo [inputFile [outputTextFile [outputXmlFile]]] */
  public static void main(String[] args) throws IOException {
    PrintWriter out;
    Properties props;
    byte[] byteTypeFile;
    String passage;

    props = loadProperty(args[0]);
    PipTest instance = new PipTest(props);
    String segResult = instance.segmentTest(props.getProperty("srcFile"));
    String posResult = instance.posTagTest(segResult);



/*
    props = loadProperty(args[0]);
    out = new PrintWriter(props.getProperty("outFile"));
    // Initialize an Annotation with some text to be annotated. The text is the argument to the constructor.
    Annotation annotation;
    if (args.length > 0) {
      //read file
      annotation = new Annotation(IOUtils.slurpFileNoExceptions(props.getProperty("srcFile")));
    } else {
      annotation = new Annotation("Kosgi Santosh sent an email to Stanford University. He didn't get a reply.");
    }

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    pipeline.annotate(annotation);
    pipeline.prettyPrint(annotation, out);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    if (sentences != null && ! sentences.isEmpty()) {
      for(CoreMap sentence : sentences)
      {
        System.out.println(sentence.keySet());

      }
    }else
      System.out.println("sentences is empty");
*/
  }

  public static Properties loadProperty(String propFile)
  {
        Properties properties = new Properties();
        try {
            FileInputStream in = new FileInputStream(propFile);
            properties.load(in);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return properties;
  }

  public void initSegment()
  {
    Properties props = new Properties();
    String segBaseDir = "data/segment";
    props.setProperty("sighanCorporaDict", segBaseDir);
    // props.setProperty("NormalizationTable", "data/norm.simp.utf8");
    // props.setProperty("normTableEncoding", "UTF-8");
    // below is needed because CTBSegDocumentIteratorFactory accesses it
    props.setProperty("serDictionary", segBaseDir + "/dict-chris6.ser.gz");
    props.setProperty("inputEncoding", "UTF-8");
    props.setProperty("sighanPostProcessing", "true");

    segmenter = new CRFClassifier<>(props);
    segmenter.loadClassifierNoExceptions(segBaseDir + "/ctb.gz", props);
  }

  public void initPostag(Properties props)
  {
    posTager = new MaxentTagger("./data/pos/chinese-nodistsim.tagger");
  }

  public String segmentTest(String filename)
  {
    String input = IOUtils.slurpFileNoExceptions(filename);
    String segmentedSentences;

    List<String> segmented = this.segmenter.segmentString(input);
    segmentedSentences = String.join(" ", segmented);
    System.out.println("out;");
    System.out.println(segmentedSentences);
    return segmentedSentences;

  }

  public String posTagTest(String input)
  {
    String posTagResult = posTager.tagTokenizedString(input);
    System.out.println(posTagResult);
    return posTagResult;
  }
}