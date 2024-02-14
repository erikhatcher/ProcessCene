# TODO: add script to export movies to Solr JSON

# $SOLR_HOME/bin/solr start

$SOLR_HOME/bin/solr delete -c movies
$SOLR_HOME/bin/solr create -c movies

$SOLR_HOME/bin/post -c movies ~/Desktop/sample_mflix.movies.json

# First try: multiple values encountered for non multiValued field cast_s: [Charles Kayser, John Ott]",
# ==> renamed *_s fields to *_ss

# next error: multiple values encountered for non multiValued field cast_t: [Charles Kayser, John Ott]"
# ==> renamed *t fields to *_txt

# next error: Error adding field 'year_i'='1981è' msg=For input string: \"1981è\"
# ==>  changed projection to:  year_i: { $substr: [ "$year", 0, 4 ] }
# Success!


#[
#  {
#    $project: {
#      _id: 0,
#      id: {
#        $toString: "$_id",
#      },
#      title_txt: "$title",
#      cast_ss: "$cast",
#      cast_txt: "$cast",
#      directors_ss: "directors",
#      directors_txt: "directors",
#      fullplot_txt: "$fullplot",
#      plot_txt: "$plot",
#      genres_ss: "$genres",
#      genres_txt: "$genres",
#      year_i: {
#        $substr: ["$year", 0, 4],
#      },
#      countries_ss: "$countries",
#      type_s: "$type",
#      rated_s: "$rated",
#      imdb_rating_f: "$imdb.rating",
#    },
#  },
#]