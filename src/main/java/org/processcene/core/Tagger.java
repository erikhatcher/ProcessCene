package org.processcene.core;

import org.json.simple.JSONObject;

public interface Tagger {
  /**
   * For now this returns a Solr JSON response
   *
   * @param input text to be tagged
   * @return
   */
  JSONObject tag(String input);
}
