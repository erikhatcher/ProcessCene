package org.processcene;

import org.processcene.core.AboutProcessCene;
import org.processcene.core.LoveLuceneSlide;
import org.processcene.core.ProcessCene;
import org.processcene.core.Theme;

public class Demo extends ProcessCene {

  public Demo() {
    super();

    add(new LoveLuceneSlide());
    add(new AboutProcessCene("About"));
    //add(new BulletPointsSlide("ProcessCene", new String[] {"one", "two", "three"}, null, null));

    setTheme(new Theme());
  }

  public static void main(String[] args) {
    ProcessCene.run(Demo.class.getName());
  }
}
