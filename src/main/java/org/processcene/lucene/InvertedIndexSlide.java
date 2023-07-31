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
import org.processcene.DocumentAvatar;
import org.processcene.core.BaseSlide;
import org.processcene.core.ProcessCene;

import java.io.IOException;
import java.util.List;

public class InvertedIndexSlide extends BaseSlide {

  final LuceneAnalyzer text_analyzer = new LuceneAnalyzer();
  private List<DocumentAvatar> documents;

  public InvertedIndexSlide(String title) {
    super(title);
  }

  @Override
  public void init(ProcessCene p) {
    super.init(p);

    documents = p.documents;
  }

  @Override
  public void draw(ProcessCene p, int step) {
    if (step == 0) {
      p.text("0", p.width/2, p.height/2);
    }

    ByteBuffersDirectory index = new ByteBuffersDirectory();

    // TODO: Make analyzer name cycle
    Analyzer analyzer = text_analyzer.getAnalyzer("Standard");
    try {
      IndexWriter writer = new IndexWriter(index, new IndexWriterConfig(analyzer));

      for (int i = 0; i < step; i++) {
        DocumentAvatar d = documents.get(i);

        Document doc = new Document();
        doc.add(new StringField("id", ""+d.id, Field.Store.YES));
        doc.add(new StringField("type", d.type, Field.Store.YES));
        doc.add(new TextField("title", d.title, Field.Store.YES));
        // TODO: Add all other fields
        writer.addDocument(doc);
      }
      writer.close();
    } catch (Exception e) {
      System.err.println(e.getLocalizedMessage());
    }

    float x = 20;
    float y = p.textAscent() + p.textDescent() + 10;

    for (int i = 0; i < step; i++) {
      DocumentAvatar d = documents.get(i);
      d.draw(p, x,y);
      p.text(d.title, x, y);
      y += 40;
    }

    x = 200;
    y = 180;
    float top_of_postings_y = y - p.textAscent();
    /*
          Other index stats: avg field length?

          term | df |      postings list        |
          foo  |  3 |  "

     */
    try {
      IndexReader reader = DirectoryReader.open(index);
      Terms terms = MultiTerms.getTerms(reader, "title");

      float df_x = x + p.textWidth("XXXXXXXXXXXXX");
      float posting_list_x = df_x + p.textWidth("XXX");

      if (terms != null) {
        p.text("Term", x, y);
        p.text("df", df_x, y);
        String postings_list_header = "Postings List";
        p.text(postings_list_header, posting_list_x, y);

        // draw line under posting list header row
        p.line(x, y + p.textDescent(), posting_list_x + p.textWidth(postings_list_header), y + p.textDescent());

        y += p.textAscent() + p.textDescent() + 15;

        TermsEnum terms_enum = terms.iterator();
        while (terms_enum.next() != null) {
          int df = terms_enum.docFreq();
          String term_value = terms_enum.term().utf8ToString();

          p.text(term_value, x, y);
          p.text(df, df_x, y);

          PostingsEnum postings = terms_enum.postings(null);
          while (postings.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
            int id = Integer.parseInt(reader.storedFields().document(postings.docID()).get("id"));

            documents.get(id-1).draw(p, posting_list_x + 30 * (id - 1), y - p.textAscent() - p.textDescent());
          }
          // horizontal line under posting
          p.line(x, y + p.textDescent(), posting_list_x + p.textWidth(postings_list_header), y + p.textDescent());

          y += p.textAscent() + p.textDescent() + 15;
        }

        // draw lines for the columns
        p.line(df_x - 5, top_of_postings_y, df_x - 5, y - p.textAscent() - p.textDescent());
        p.line(posting_list_x - 5, top_of_postings_y, posting_list_x - 5, y - p.textAscent() - p.textDescent());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    super.draw(p, step);
  }

  @Override
  public int getNumberOfSteps() {
    return documents.size();
  }
}
