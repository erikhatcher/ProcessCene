package org.processcene.core;

import org.json.simple.JSONObject;

public interface Tagger {
  JSONObject tag(String input);
}
