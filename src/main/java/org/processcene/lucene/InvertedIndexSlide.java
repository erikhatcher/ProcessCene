package org.processcene.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.processcene.BaseSlide;
import org.processcene.ProcessCene;
import processing.core.PImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InvertedIndexSlide extends BaseSlide {
  final List<String> docs = new ArrayList<>();

  final LuceneAnalyzer text_analyzer = new LuceneAnalyzer();
  private final PImage[] doc_images = new PImage[10];
  private final PImage step_0_image;

  public InvertedIndexSlide(String title, ProcessCene presentation) {
    super(title, presentation);

    docs.add("What is Lucene?");
    docs.add("'Love of Lucene' @ Uberconf");
    docs.add("We *love* Lucene!");

    // TODO: centralize this - reused on org.processcene.lucene.VectorSearchSlide
    for (int i = 0; i < 10; i++) {
      doc_images[i] = presentation.loadImage(presentation.getFilePathFromResources("Assets/normal/" + i + "_Inverted10x.png"));
      doc_images[i].resize(30, 30);
    }

    step_0_image = presentation.loadImage(presentation.getFilePathFromResources("mongodb-assets/SearchTerminal/Technical_SOFTWARE_SearchTerminal_Spot_BS_ForestGreen.png"));
    step_0_image.resize(0, 700);
  }

  @Override
  public void draw(int step) {
    if (step == 0) {
      presentation.image(step_0_image, (presentation.width - step_0_image.width) / 2, (presentation.height - step_0_image.height) / 2);
    }

    //presentation.textFont(presentation.loadFont(presentation.getFilePathFromResources("SourceCodeProRoman-Medium-24.vlw")));

    ByteBuffersDirectory index = new ByteBuffersDirectory();
    // TODO: Make analyzer name cycle
    Analyzer analyzer = text_analyzer.getAnalyzer("Standard");
    try {
      IndexWriter writer = new IndexWriter(index, new IndexWriterConfig(analyzer));

      for (int i = 0; i < step; i++) {
        Document doc = new Document();
        doc.add(new StringField("id", "" + (i + 1), Field.Store.YES));
        doc.add(new TextField("title", docs.get(i), Field.Store.YES));
        writer.addDocument(doc);
      }
      writer.close();
    } catch (Exception e) {
      System.err.println(e.getLocalizedMessage());
    }

    float x = 20;
    float y = presentation.textAscent() + presentation.textDescent() + 10;

    for (int i = 0; i < step; i++) {
      PImage doc_image = doc_images[i + 1];
      presentation.image(doc_image, x, y - presentation.textAscent() - presentation.textDescent());
      presentation.text(docs.get(i), x + doc_image.width, y);
      y += 40;
    }

    x = 200;
    y = 180;
    float top_of_postings_y = y - presentation.textAscent();
    /*
          Other index stats: avg field length?

          term | df |      postings list        |
          foo  |  3 |  "

     */
    try {
      IndexReader reader = DirectoryReader.open(index);
      Terms terms = MultiTerms.getTerms(reader, "title");

      float df_x = x + presentation.textWidth("XXXXXXXXXXXXX");
      float posting_list_x = df_x + presentation.textWidth("XXX");

      if (terms != null) {
        presentation.text("Term", x, y);
        presentation.text("df", df_x, y);
        String postings_list_header = "Postings List";
        presentation.text(postings_list_header, posting_list_x, y);

        // draw line under posting list header row
        presentation.line(x, y + presentation.textDescent(), posting_list_x + presentation.textWidth(postings_list_header), y + presentation.textDescent());

        y += presentation.textAscent() + presentation.textDescent() + 15;

        TermsEnum terms_enum = terms.iterator();
        while (terms_enum.next() != null) {
          int df = terms_enum.docFreq();
          String term_value = terms_enum.term().utf8ToString();

          presentation.text(term_value, x, y);
          presentation.text(df, df_x, y);

          PostingsEnum postings = terms_enum.postings(null);
          while (postings.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
            int id = Integer.parseInt(reader.storedFields().document(postings.docID()).get("id"));
            PImage doc_image = doc_images[id];
            presentation.image(doc_image, posting_list_x + doc_image.width * (id - 1), y - presentation.textAscent() - presentation.textDescent());
          }
          // horizontal line under posting
          presentation.line(x, y + presentation.textDescent(), posting_list_x + presentation.textWidth(postings_list_header), y + presentation.textDescent());

          y += presentation.textAscent() + presentation.textDescent() + 15;
        }

        // draw lines for the columns
        presentation.line(df_x - 5, top_of_postings_y, df_x - 5, y - presentation.textAscent() - presentation.textDescent());
        presentation.line(posting_list_x - 5, top_of_postings_y, posting_list_x - 5, y - presentation.textAscent() - presentation.textDescent());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    super.draw(step);
  }

  @Override
  public int getNumberOfSteps() {
    return docs.size();
  }
}
