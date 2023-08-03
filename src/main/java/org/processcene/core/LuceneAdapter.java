package org.processcene.core;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LuceneAdapter implements SearchEngineAdapter {
  private final ByteBuffersDirectory index;
  private final List<DocumentAvatar> documents;

  public LuceneAdapter(List<DocumentAvatar> docs) {
    index = new ByteBuffersDirectory();
    Analyzer analyzer = new StandardAnalyzer();

    documents = docs;

    try {
      IndexWriter writer = new IndexWriter(index, new IndexWriterConfig(analyzer));

      for (int i = 0; i < documents.size(); i++) {
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
  }

  @Override
  public SearchResponse search(SearchRequest request) {
    SearchResponse response = new SearchResponse();
    LuceneSearchRequest lsr = (LuceneSearchRequest) request;
    Query q = lsr.query;

    try {
      IndexReader reader = DirectoryReader.open(index);
      IndexSearcher searcher = new IndexSearcher(reader);
      TopDocs top_docs = searcher.search(q, ProcessCene.MAX_DOCS);

      for (ScoreDoc hit : top_docs.scoreDocs) {
        ResponseDocument rd = new ResponseDocument();
        rd.score = hit.score;
        rd.explain = searcher.explain(q, hit.doc).toString();
        if (hit.score > response.max_score) response.max_score = hit.score;

        Document d = reader.document(hit.doc);
        rd.id = Integer.parseInt(d.get("id"));

        response.documents.add(rd);
      }

    } catch (IOException e) {
      response.error = e.getMessage();
    }


    return response;
  }

  @Override
  public List<SearchRequest> getQueries() {
    List<SearchRequest> queries = new ArrayList<>();
    queries.add(new LuceneSearchRequest(new TermQuery(new Term("title", "foo"))));
    return queries;
  }

  @Override
  public List<DocumentAvatar> getDocuments() {
    return documents;
  }
}
