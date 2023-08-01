package org.processcene;

import org.processcene.core.AboutProcessCene;
import org.processcene.core.BulletPointsSlide;
import org.processcene.core.LoveLuceneSlide;
import org.processcene.core.ProcessCene;
import org.processcene.core.Theme;

import java.util.HashMap;
import java.util.Map;

public class Demo extends ProcessCene {

  @Override
  public void setup() {
    documents.clear();
    documents.add(doc(1, "Document One"));
    documents.add(doc(2, "Document Two"));

    super.setup();
  }

  private DocumentAvatar doc(int i, String title) {
    Map<String,Object> doc = new HashMap<>();
    doc.put("id", i);
    doc.put("title", title);
    return new DocumentAvatar(doc);
  }

  public Demo() {
    super();

    setTheme(new Theme());

    add(new LoveLuceneSlide());
    add(new AboutProcessCene("About"));
    add(new DocumentsSlide("Documents"));
//    add(new BulletPointsSlide("ProcessCene", new String[] {"one", "two", "three"}));

  }

  public static void main(String[] args) {
    ProcessCene.run(Demo.class.getName());
  }
}
