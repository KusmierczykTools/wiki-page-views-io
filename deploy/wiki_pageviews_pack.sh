#!/bin/sh


#params
TMPDIR="tmpshards"
LISTOFSHARDS="list_of_shards.txt"

echo "THE SCRIPTS UNPACKS GZ-FILES AND PACKS INTO STORAGE PAGEVIEWS OF WIKIDUMPS."
echo "FOLLOWING ARGUMENTS ARE EXPECTED:"
echo " FILE WITH LIST OF INPUT GZ FILES, STORAGE DIRECTORY, FILE WITH ALLOWED NAMES OF PAGES."
echo "ADDITIONAL LIST OF SHARDS TO BE PROCESSED IS READ FROM THE FILE: $LISTOFSHARDS"

#verify arguments:
if [ -z "$1" ]
  then
    echo "FIRST ARGUMENT NEEDED: FILE WITH LIST OF INPUT GZ FILES "
    exit
fi
LISTFILE=$1
LISTFILENAME=`basename $LISTFILE`
echo "FILE WITH LIST OF INPUT GZ FILES: $LISTFILE"

if [ -z "$2" ]
  then
    echo "SECOND ARGUMENT NEEDED: STORAGE DIRECTORY"
    exit
fi
STORAGE=$2
echo "STORAGE DIRECTORY: $STORAGE"

if [ -z "$3" ]
  then
    echo "THIRD ARGUMENT NEEDED: FILE WITH ALLOWED NAMES OF PAGES"
    exit
fi
LEGALVALUES=$3
echo "FILE WITH ALLOWED NAMES OF PAGES: $LEGALVALUES"


#simple validation
echo "VALIDATING LIST OF FILES"
sh validate_sizes.sh $LISTFILE
python validate_timeline2.py < $LISTFILE


#create temporary directory for each of shards
echo "CREATING TMP DIRECTORY FOR DATA SHARDS"
mkdir $TMPDIR
for SHARD in `cat $LISTOFSHARDS`; do
    mkdir "$TMPDIR/$SHARD"
done


#create separated shards of input data
echo "DISTRIBUTING FILES AMONG SHARDS:"
for FILE in `cat $LISTFILE | sort`; do
    NAME=`basename $FILE .gz`
    TIME=`date`
    echo "[$TIME]> SHARDING FILE=$FILE (NAME=$NAME)"

    #filter & split data among shard files
    gunzip -c $FILE | sh file_filtering.sh $LEGALVALUES | sh file_sharding.sh

    #move each part of the file to proper shard directory 
    for SHARD in `cat $LISTOFSHARDS`; do
        mv $SHARD "$TMPDIR/$SHARD/$NAME.txt"
    done

    TIME=`date`
    echo "[$TIME]< SHARDING DONE. CLEANING UP LEFT SHARD FILES"
    rm -rf shard_*
done


#process each shard in separate thread
echo "RUNNING PROCESSOR FOR EACH OF SHARDS:"
mkdir $STORAGE
for SHARD in `cat $LISTOFSHARDS`; do
    LOG="$STORAGE/$SHARD/load-$LISTFILENAME-$SHARD.log"
    TMPLIST="$TMPDIR/$SHARD.list"
    TIME=`date`

    mkdir $STORAGE/$SHARD
    ls $TMPDIR/$SHARD/*.txt | sort > $TMPLIST 
    echo "[$TIME]> PROCESSING SHARD=$SHARD LIST=$TMPLIST STORAGE=$STORAGE/$SHARD LOG=$LOG"
    sh files_processing.sh $TMPLIST $STORAGE/$SHARD $4 2> $LOG &
done


#wait till done
wait
echo "CLEANING UP TEMPORARY DIR ($TMPDIR)"
rm -rf $TMPDIR
echo "DONE. Now you can run wiki_pageviews_pack_finalize.sh"


