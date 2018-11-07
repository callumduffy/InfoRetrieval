# InfoRetrieval
Repositiory for a group assignment to build an application to index and query a data set of news articles from the following sources.

1. Financial Times Limited.
2. Federal Register.
3. Foreign Broadcast Information Service.
4. LA Times.  

## Initial Group name ideas  
1. Rage Against the Lucene
2. Now Lucene me, Now you don't

## Initial thoughts for assignment  
- Jsoup is a really useful library for parsing documents. I think we should definitely use this, saves a lot of ugly code.  
- Think we should split the assignment into the parsers and then one person doing initial work on managing queries. We can then aggregrate after this point.
- Can work on master branch for this stage, but will need to implement branching and PR's once a base model has been developed.
- Only want one index, so we will want to create a list of all documents seperately based off source, then conjoin and index them.
- Please manage your .gitignore's. Maven projects can get messy if the metadata files are not managed do please try not to push any .settings, or.classpath files from eclipse etc. Just to help us, as they can cause merge conflicts.

## Documentation  
Think it's a good idea just to have a doc's folder where we keep track of what we're doing, at each meeting etc. and any design decisions that we make throughout the process, just to make the report easier.  


