#!/bin/sh


#params
TMPDIR="tmpshards"
LISTOFSHARDS="list_of_shards.txt"

echo "THE SCRIPT PACKS (UNPACKED) PAGEVIEWS OF WIKIDUMPS INTO STORAGE-FILES."
echo "FOLLOWING ARGUMENTS ARE EXPECTED:"
echo " FILE WITH LIST OF INPUT FILES, STORAGE DIRECTORY."
echo "ADDITIONAL LIST OF SHARDS TO BE PROCESSED IS READ FROM THE FILE: $LISTOFSHARDS"
echo "WARNING: THERE IS ONE PROCESS PER SHARD CREATED SO BE CAREFUL WITH IT!"


#verify arguments:
if [ -z "$1" ]
  then
    echo "FIRST ARGUMENT NEEDED: FILE WITH LIST OF INPUT FILES "
    exit
fi
LISTFILE=$1
LISTFILENAME=`basename $LISTFILE`
echo "[ARGUMENT] FILE WITH LIST OF INPUT FILES: $LISTFILE"

if [ -z "$2" ]
  then
    echo "SECOND ARGUMENT NEEDED: STORAGE DIRECTORY"
    exit
fi
STORAGE=$2
echo "[ARGUMENT] STORAGE DIRECTORY: $STORAGE"


#create temporary directory for each of shards
echo "CREATING TMP DIRECTORY FOR DATA SHARDS"
rm -rf $TMPDIR
mkdir $TMPDIR 2> /dev/null
for SHARD in `cat $LISTOFSHARDS`; do
    mkdir "$TMPDIR/$SHARD" 2> /dev/null
done


#create separated shards of input data
echo "DISTRIBUTING FILES AMONG SHARDS:"
for FILE in `cat $LISTFILE | sort`; do
    NAME=`basename $FILE .gz`
    TIME=`date`
    echo "[$TIME]> SHARDING FILE=$FILE (NAME=$NAME)"

    #filter & split data among shard files
    cat $FILE | sh file_sharding.sh

    #move each part of the file to proper shard directory 
    for SHARD in `cat $LISTOFSHARDS`; do
        mv $SHARD "$TMPDIR/$SHARD/$NAME.txt"
    done

    TIME=`date`
    echo "[$TIME]< SHARDING DONE. CLEANING UP LEFT SHARD FILES"
    rm -rf shard_*
done


#process each shard in separate process
echo "RUNNING PROCESSOR FOR EACH OF SHARDS:"
mkdir $STORAGE 2> /dev/null
for SHARD in `cat $LISTOFSHARDS`; do
    LOG="$STORAGE/$SHARD/load-$LISTFILENAME-$SHARD.log"
    TMPLIST="$TMPDIR/$SHARD.list"
    TIME=`date`

    mkdir $STORAGE/$SHARD 2> /dev/null
    ls $TMPDIR/$SHARD/*.txt | sort > $TMPLIST 
    echo "[$TIME]> PROCESSING SHARD = $SHARD  LIST = $TMPLIST  STORAGE = $STORAGE/$SHARD  LOG = $LOG"
    sh files_processing.sh $TMPLIST $STORAGE/$SHARD $4 2> $LOG &
done


#wait till done
wait
echo "CLEANING UP TEMPORARY DIR ($TMPDIR)"
rm -rf $TMPDIR
echo "DONE. Now you can run wiki_pageviews_pack_finalize.sh"


