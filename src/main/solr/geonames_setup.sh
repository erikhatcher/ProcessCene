$SOLR_HOME/bin/solr delete -c geonames
$SOLR_HOME/bin/solr create -c geonames

curl -X POST -H 'Content-type:application/json'  http://localhost:8983/solr/geonames/schema -d '{
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

  "add-field":{"name":"name_tag", "type":"tag", "stored":false },

  "add-copy-field":{"source":"name", "dest":["name_tag"]}
}'

curl -X POST -H 'Content-type:application/json' http://localhost:8983/solr/geonames/config -d '{
  "add-requesthandler" : {
    "name": "/tag",
    "class":"solr.TaggerRequestHandler",
    "defaults":{"field":"name_tag"}
  }
}'

$SOLR_HOME/bin/post -c geonames -type text/csv \
  -params 'optimize=true&maxSegments=1&separator=%09&encapsulator=%00&fieldnames=id,name,,alternative_names,latitude,longitude,,,countrycode,,,,,,population,elevation,,timezone,lastupdate' \
  ~/Downloads/cities1000.txt

curl -X POST \
  'http://localhost:8983/solr/geonames/tag?overlaps=NO_SUB&tagsLimit=5000&fl=id,name,countrycode&wt=json&indent=on' \
  -H 'Content-Type:text/plain' -d 'Good Morning San Francisco!!!'