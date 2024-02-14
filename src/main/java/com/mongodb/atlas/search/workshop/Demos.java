package com.mongodb.atlas.search.workshop;

import com.mongodb.atlas.cene.Season1;
import org.processcene.QueryRunnerSlide;
import org.processcene.atlas.AtlasAdapter;
import org.processcene.atlas.MongoTheme;
import org.processcene.core.DocumentAvatar;
import org.processcene.core.ProcessCene;
import org.processcene.core.Theme;
import org.processcene.lucene.VectorSearchSlide;

import java.util.ArrayList;
import java.util.List;

public class Demos extends ProcessCene {
  @Override
  public void setup() {
    Theme theme = new MongoTheme();
    setTheme(theme);

//    AtlasAdapter vectors_adapter = new AtlasAdapter("workshop", "vectors", null);
//
//    List<float[]> vectors = new ArrayList<>();
//    List<DocumentAvatar> documents = vectors_adapter.getDocuments();
//    for (DocumentAvatar document : documents) {
//      int doc_num = (Integer) document.document.get("doc_num");
//      System.out.println("document = " + document.document);
//      List<Double> v_list = (List<Double>) document.document.get("v");
//      int s = v_list.size();
//      float[] v = new float[s];
//      for (int i=0;i<s;i++) {
//        v[i] = v_list.get(i).floatValue();
//      }
//      vectors.add(doc_num-1, v);
//    }
//
//    add(new VectorSearchSlide("Vectors", vectors));

    AtlasAdapter movies_adapter = new AtlasAdapter("sample_mflix", "movies", null);

    slides.add(new QueryRunnerSlide("Query Runner", movies_adapter, new String[] {
        "purple rain", "keanu reeves", "prince", "bruce lee", "Jacky Chan", "martial arts movies"
    }));

    super.setup();
  }

  public static void main(String[] args) {
    ProcessCene.run(Demos.class.getName());
  }
}
