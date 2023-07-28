# org.processcene.ProcessCene

Inspired by [Story of Search](https://storyofsearch.com/) and impressive Processing work that my colleague John Page does at MongoDB.

## Launching org.processcene.ProcessCene

A recent Java version is required.  Tested successfully on `openjdk 17.0.7 2023-04-18`.

    git clone https://github.com/erikhatcher/org.processcene.ProcessCene
    cd org.processcene.ProcessCene
    ./gradlew run

### External Dependencies

Some slides depend on external systems, such as Solr or Atlas Search, being
accessible.  If they aren't available or errors happen in communication,
the slides will continue to run with error messages shown instead of actual content.

If you have an Atlas Search collection set up and accessible to the sample movies
data, setting `ATLAS_URI` to your Atlas cluster will allow the slides that depend 
on that endpoint to work, rather than showing a connection error.

Set this before `run`ing:

    export ATLAS_URI="mongodb+srv://<username>:<password>@<cluster>.mongodb.net/?retryWrites=true&w=majority"

## Happy org.processcene.ProcessCene!

Keyboard navigation is key to stepping through the slides. Each slide contains one
or more *variations*. Within each variation could be zero or more *steps*.

The simplest way to navigate through all materials is to use the `<spacebar>` to step through
each slide, each variation within each slide, and each step within each variation.  For example, the
`Analysis` slide has a variation for each analyzer, and within each analyzer are steps for each term emitted
from that analyzer.

Here are the useful key-bindings to navigate the dynamic org.processcene.ProcessCene presentation:

| Key        | Action                                                       |
|------------|--------------------------------------------------------------|
| \          | Next slide                                                   |
| `]`        | Previous slide                                               |
| `[`        | Next step (if any)                                           |
| `'`        | Previous step (if any)                                       |
| `=`        | Next variation (if any)                                      |
| `-`        | Previous variation (if any)                                  |
| `1` to `9` | Jump to that main slide number from Table of Contents        |
| `f`        | Toggle footer                                                |



