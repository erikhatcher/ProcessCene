package com.mongodb.atlas;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.ByteBuffersDirectory;
import processing.core.PApplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProcessCene extends PApplet {
  public static void main(String[] args) {
    ByteBuffersDirectory index;
    List<Document> docs = new ArrayList<Document>();
    Document doc1 = new Document();
    doc1.add(new StringField("id", "1", Field.Store.YES));
    doc1.add(new TextField("title", "The quick brown fox jumped over the lazy dogs.", Field.Store.YES));
    docs.add(doc1);

    Document doc2 = new Document();
    doc2.add(new StringField("id", "2", Field.Store.YES));
    doc2.add(new TextField("title", "The lazy dogs... slept.", Field.Store.YES));
    docs.add(doc2);

    index = new ByteBuffersDirectory();
    try {
      IndexWriter writer = new IndexWriter(index, new IndexWriterConfig(new StandardAnalyzer()));

      for (Document doc : docs) {
        writer.addDocument(doc);
      }
      writer.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    try {
      IndexReader reader = DirectoryReader.open(index);
      Terms terms = MultiTerms.getTerms(reader,"title");
      if (terms != null) {
        TermsEnum terms_enum = terms.iterator();
        while (terms_enum.next() != null) {
          int df = terms_enum.docFreq();
          String txt = terms_enum.term().utf8ToString();
          System.out.println("Term: " + txt + " (" + df + ")");
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    PApplet.main(new String[]{ProcessCene.class.getName()});

  }

  @Override
  public void setup() {
    frameRate(5);
    fill(0);
  }

  @Override
  public void draw() {
    text("Foo", width /2, height / 2);
  }
}
