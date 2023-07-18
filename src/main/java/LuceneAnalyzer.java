import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.email.UAX29URLEmailAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.TermFrequencyAttribute;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LuceneAnalyzer extends TextAnalyzer {

  // lol, this calls for a NamedList instead :)
  private List<String> analyzer_names = new ArrayList<String>();
  private List<Analyzer> analyzers = new ArrayList<Analyzer>();

  public LuceneAnalyzer() {
    analyzer_names.add("Standard");
    analyzers.add(new StandardAnalyzer());

    analyzer_names.add("Whitespace");
    analyzers.add(new WhitespaceAnalyzer());

    analyzer_names.add("Simple");
    analyzers.add(new SimpleAnalyzer());

    analyzer_names.add("Keyword");
    analyzers.add(new KeywordAnalyzer());

    analyzer_names.add("English");
    analyzers.add(new EnglishAnalyzer());

    analyzer_names.add("CJK");
    analyzers.add(new CJKAnalyzer());

    analyzer_names.add("UAX29URLEmail");
    analyzers.add(new UAX29URLEmailAnalyzer());

    analyzer_names.add("StandardEdgeNGram");
    analyzers.add(new Analyzer() {
      @Override
      protected TokenStreamComponents createComponents(String s) {
        Tokenizer tokenizer = new StandardTokenizer();
        return new Analyzer.TokenStreamComponents(tokenizer, new EdgeNGramTokenFilter(tokenizer, 2, 15, true));
      }
    });

    analyzer_names.add("StandardNGram");
    analyzers.add(new Analyzer() {
      @Override
      protected TokenStreamComponents createComponents(String s) {
        Tokenizer tokenizer = new StandardTokenizer();
        return new Analyzer.TokenStreamComponents(tokenizer, new NGramTokenFilter(tokenizer, 2, 15, true));
      }
    });
  }

  @Override
  List<Map<String, Object>> analyzeString(String analyzer_name, String text) {
    if (!analyzer_names.contains(analyzer_name)) {
      throw new RuntimeException("Unknown analyzer: " + analyzer_name);
    }
    Analyzer analyzer = analyzers.get(analyzer_names.indexOf(analyzer_name));

    List<Map<String, Object>> token_list = new ArrayList<>();

    //System.out.println("Analyzing: " + text);
    try (TokenStream ts = analyzer.tokenStream("myfield", text)) {
      OffsetAttribute offsets = ts.addAttribute(OffsetAttribute.class);
      CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
      TermToBytesRefAttribute bytes = ts.addAttribute(TermToBytesRefAttribute.class);
      PositionIncrementAttribute position_increment = ts.addAttribute(PositionIncrementAttribute.class);
      PositionLengthAttribute position_length = ts.addAttribute(PositionLengthAttribute.class);
      TypeAttribute type = ts.addAttribute(TypeAttribute.class);
      TermFrequencyAttribute term_frequency = ts.addAttribute(TermFrequencyAttribute.class);

      ts.reset(); // Resets this stream to the beginning. (Required)
      while (ts.incrementToken()) {
        // Use AttributeSource.reflectAsString(boolean)
        // for token stream debugging.
        // println("token: " + ts.reflectAsString(true));
        // token: org.apache.lucene.analysis.tokenattributes.CharTermAttribute#term=goes
        //        org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute#bytes=[67 6f 65 73]
        //        org.apache.lucene.analysis.tokenattributes.OffsetAttribute#startOffset=10
        //        org.apache.lucene.analysis.tokenattributes.OffsetAttribute#endOffset=14
        //        org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute#positionIncrement=1
        //        org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute#positionLength=1
        //        org.apache.lucene.analysis.tokenattributes.TypeAttribute#type=<ALPHANUM>
        //        org.apache.lucene.analysis.tokenattributes.TermFrequencyAttribute#termFrequency=1

        // TODO: Consider formalizing this into a class so all implementations can have the same `Token` class
        HashMap<String, Object> token = new HashMap<String, Object>();
        token.put("term", new String(term.buffer(), 0, term.length()));
        token.put("bytes", "[xxx]");
        token.put("start_offset", offsets.startOffset());
        token.put("end_offset", offsets.endOffset());
        token.put("position_increment", position_increment.getPositionIncrement());
        token.put("position_length", position_length.getPositionLength());
        token.put("type", type.type());
        token.put("term_frequency", term_frequency.getTermFrequency());
        // println(token);
        token_list.add(token);
      }
      ts.end();   // Perform end-of-stream operations, e.g. set the final offset.
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }

    return token_list;
  }

  public Analyzer getAnalyzer(String name) {
    // TODO: make analyzers a HashMap<name,Analyzer> instead of parallel Lists
    return analyzers.get(analyzer_names.indexOf(name));
  }
  @Override
  List<String> getAnalyzerNames() {
    return analyzer_names;
  }


}
