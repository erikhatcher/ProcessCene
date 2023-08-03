package org.processcene;

import org.processcene.core.AboutProcessCene;
import org.processcene.core.DocumentAvatar;
import org.processcene.core.DocumentsSlide;
import org.processcene.core.LoveLuceneSlide;
import org.processcene.core.LuceneAdapter;
import org.processcene.core.ProcessCene;
import org.processcene.core.Theme;
import org.processcene.lucene.InvertedIndexSlide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demo extends ProcessCene {

  private List<DocumentAvatar> documents = new ArrayList<>();

  private DocumentAvatar doc(int i, String title) {
    Map<String,Object> doc = new HashMap<>();
    doc.put("id", i);
    doc.put("title", title);
    doc.put("type", "doc");
    return new DocumentAvatar(doc);
  }

  public Demo() {
    super();

    documents.add(doc(1, "Foo Bar Baz"));
    documents.add(doc(2, "Everyone was kung foo fighting"));
    documents.add(doc(3, "Foo get about it"));

    setTheme(new Theme());

    add(new LoveLuceneSlide());
    add(new AboutProcessCene("About"));
    add(new InvertedIndexSlide("Inverted Index", documents));
    add(new DocumentsSlide("Documents", new LuceneAdapter(documents)));
  }

  public static void main(String[] args) {
    ProcessCene.run(Demo.class.getName());
  }
}
