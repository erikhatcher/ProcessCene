$SOLR_HOME/bin/solr delete -c tagger
$SOLR_HOME/bin/solr create -c tagger

# TODO: Add location field with lat/long.... and eventually shape?
curl -X POST -H 'Content-type:application/json'  http://localhost:8983/solr/tagger/schema -d '{
  "add-field-type":{
    "name":"tag",
    "class":"solr.TextField",
    "postingsFormat":"FST50",
    "omitNorms":true,
    "omitTermFreqAndPositions":true,
    "indexAnalyzer":{
      "tokenizer":{
         "class":"solr.StandardTokenizerFactory" },
      "filters":[
        {"class":"solr.EnglishPossessiveFilterFactory"},
        {"class":"solr.ASCIIFoldingFilterFactory"},
        {"class":"solr.LowerCaseFilterFactory"},
        {"class":"solr.ConcatenateGraphFilterFactory", "preservePositionIncrements":false }
      ]},
    "queryAnalyzer":{
      "tokenizer":{
         "class":"solr.StandardTokenizerFactory" },
      "filters":[
        {"class":"solr.EnglishPossessiveFilterFactory"},
        {"class":"solr.ASCIIFoldingFilterFactory"},
        {"class":"solr.LowerCaseFilterFactory"}
      ]}
    },

  "add-field":{"name":"name", "type":"text_general"},
  "add-field":{"name":"type", "type":"string"},
  "add-field":{"name":"name_tag", "type":"tag", "stored":false, "multiValued": true },
  "add-copy-field":{"source":"name", "dest":["name_tag"]}
}'

curl -X POST -H 'Content-type:application/json' http://localhost:8983/solr/tagger/config -d '{
  "add-requesthandler" : {
    "name": "/tag",
    "class":"solr.TaggerRequestHandler",
    "defaults":{"field":"name_tag"}
  }
}'

$SOLR_HOME/bin/post -c tagger cast-tagger.json
$SOLR_HOME/bin/post -c tagger genres-tagger.json
$SOLR_HOME/bin/post -c tagger titles-tagger.json

#id,type,name,metadata_whatever_s
#special-star,special,Star,tagged ya!