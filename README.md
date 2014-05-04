wiki-page-views-io
==================

The project is devoted to support Wikipedia page view statistics dumps IO 
(http://dumps.wikimedia.org/other/pagecounts-raw/). 

-----------------------------------------------------------------------------------------------------------------

Source data:

The GZ files can be downloaded from:
    http://dumps.wikimedia.org/other/pagecounts-raw/    
You can use: `sh deploy/wiki_pageviews_download.sh YEAR MONTH` for that.
WARNING: These files need to be manualy fixed that there is exactly one file per hour.

List of allowed wiki pages can be downloaded from:
    http://dumps.wikimedia.org/enwiki/20140102/enwiki-20140102-pages-articles-multistream-index.txt.bz2

-----------------------------------------------------------------------------------------------------------------

To prepare list of allowed wikipedia pages (should be done in deploy directory):

     wget http://dumps.wikimedia.org/enwiki/20140102/enwiki-20140102-pages-articles-multistream-index.txt.bz2
     bzip2 -d enwiki-20140102-pages-articles-multistream-index.txt.bz2
     cut -d ":" enwiki-20140102-pages-articles-multistream-index.txt -f 3 | python create_list_of_variants.py > /tmp/wikipedia_variants.txt

The script above unpacks and expands list of page names in a way that each possible variant is included.

-----------------------------------------------------------------------------------------------------------------

To transform WikiPageViews gz archives to more readable form we first need a list of files:

    cd deploy
    ls DIRECTORY_WITH_GZ_FILES/*.gz > /tmp/list

To validate (size and dates) list of files:

    sh wiki_pageviews_validation.sh /tmp/list

To preprocess files:

    sh wiki_pageviews_preprocess.sh /tmp/list /tmp/temporal_storage /tmp/wikipedia_variants.txt

To transform and pack files into storage:

    ls /tmp/temporal_storage/*out > /tmp/list
    sh wiki_pageviews_pack.sh  /tmp/list OUTPUT-STORAGE-PATH

All above steps can be run on as many SUBSEQUENT list as you wish e.g. you can create separate list for each year
and run one after the other.
At the end you should run:

    sh wiki_pageviews_pack_finalize.sh OUTPUT-STORAGE-PATH
    #OR: sh wiki_pageviews_pack_finalize.sh OUTPUT-STORAGE-PATH full 
    #    (if you don't want anything in meta.txt file)

-----------------------------------------------------------------------------------------------------------------

If you want to process only some part of entries (e.g. only entries with names starting with 'a')
you can remove some shards from list_of_shards.txt file. 

WARNING: there is one process created per shard so be careful with things that are in 
file_sharding.sh (number of shards) and list_of_shards.txt (list of shards that should be processed on the node). 

To edit cache length: files_processing.sh
To edit number of shards: file_sharding.sh and list_of_shards.txt

-----------------------------------------------------------------------------------------------------------------

Data storage description:

Single storage contains file meta.txt and set of directories. 
Directories names are formatted according to project_prefix2 
where prefix2 means first first two letters of page name 
e.g. en_Tr (for project=en name=Trondheim).

There is zero or one files per page. If zero than all the data about the page
is in the meta.txt. If there is one file than part (begining) 
of the timeline is in the file named
according to the format project_name e.g. en_Tr/en_Trondheim.
Rest of the timeline (ending) is stored always in meta.txt

File (meta.txt) format:
Single line describes single page. Every line contain list of fields separated with single space:
1) project
2) page name
3) timeline start date (string as in source data)
4) timeline start time (string as in source data)
5) some number (internal counter)
6) timeline (list of consequent hour entries: every entry can be either
 single hour represented as a single number (=number of views in that hour) or
 two numbers separated with "x" where first number means number of views and 
 second means number of hours that the page was viewed this number of times e.g.
 3x6 is equivalent of 3 3 3 3 3 3.
 
-----------------------------------------------------------------------------------------------------------------

To validate consistency of sharded storage run (Warning: Huge memory is needed!):

    java -jar bin/WikiViewsShardedStorageVerify.jar SHARDED-STORAGE-DIR-PATH


To pack whole storage into single text file (the same format as in meta.txt files) run:

    java -jar bin/WikiViewsStorageToSingleFile.jar SHARDED-STORAGE-DIR-PATH OUTPUT-TEXT-FILE-PATH


To pack single (e.g. single shard) storage into single text file (the same format as in meta.txt files) run:

    java -jar bin/WikiViewsStorageToSingleFile.jar STORAGE-DIR-PATH OUTPUT-TEXT-FILE-PATH



