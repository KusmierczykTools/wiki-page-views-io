#!/bin/sh

echo "EXPORTS PACKED STORAGE TO TEXT FILE."
echo "SEPARATE PROCESS IS RUN FOR EACH SHARD IN SHARDED STORAGE DIRECTORY."

#verify arguments:
if [ -z "$1" ]
  then
    echo "FIRST ARGUMENT NEEDED: SHARDED STORAGE DIRECTORY"
    exit
fi

for FILE in `ls -d $1/*/`; do
    BASENAME=`basename $FILE`
    BASEDIR=`basename $1`
    OUTPUT=$1/$BASEDIR-$BASENAME.txt
    LOG=$1/$BASEDIR-$BASENAME.log
    echo "RUNNING FOR $FILE. OUTPUT=$OUTPUT LOG=$LOG"
    java -jar bin/WikiViewsStorageToSingleFile.jar $FILE $OUTPUT 2> $LOG &
done

wait
echo "DONE."

