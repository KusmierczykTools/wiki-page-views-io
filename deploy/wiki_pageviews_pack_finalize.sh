#!/bin/sh

LISTOFSHARDS="list_of_shards.txt"

echo "THE SCRIPTS FINALIZES pack_wiki_pageviews.sh."
echo "FOLLOWING ARGUMENTS ARE EXPECTED: STORAGE DIRECTORY."
echo "ADDITIONAL LIST OF SHARDS TO BE PROCESSED IS READ FROM THE FILE: $LISTOFSHARDS"

if [ -z "$1" ]
  then
    echo "ARGUMENT NEEDED: STORAGE DIRECTORY"
    exit
fi
STORAGE=$1
echo "STORAGE DIRECTORY: $STORAGE"

#process each shard
for SHARD in `cat $LISTOFSHARDS`; do
    echo "RUNNING FOR SHARD=$SHARD"
    sh files_processing_finalization.sh $STORAGE/$SHARD $2
done






