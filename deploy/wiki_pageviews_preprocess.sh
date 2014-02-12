#!/bin/sh


echo "THE SCRIPT UNPACKS GZ-FILES AND FILTERS THEM."
echo "FOLLOWING ARGUMENTS ARE EXPECTED:"
echo " FILE WITH LIST OF INPUT GZ FILES, OUTPUT DIRECTORY, FILE WITH ALLOWED NAMES OF PAGES."


#verify arguments:
if [ -z "$1" ]
  then
    echo "FIRST ARGUMENT NEEDED: FILE WITH LIST OF INPUT GZ FILES "
    exit
fi
LISTFILE=$1
echo "[ARGUMENT] FILE WITH LIST OF INPUT GZ FILES: $LISTFILE"

if [ -z "$2" ]
  then
    echo "SECOND ARGUMENT NEEDED: OUTPUT DIRECTORY"
    exit
fi
STORAGE=$2
echo "[ARGUMENT] STORAGE DIRECTORY: $STORAGE"
mkdir $STORAGE 2> /dev/null

if [ -z "$3" ]
  then
    echo "THIRD ARGUMENT NEEDED: FILE WITH ALLOWED NAMES OF PAGES"
    exit
fi
LEGALVALUES=$3
echo "[ARGUMENT] FILE WITH ALLOWED NAMES OF PAGES: $LEGALVALUES"


#simple filtering
echo "UNPACKING AND SIMPLE FILTERING OF FILES"
rm -rf /tmp/preprocess_errors.log
for FILE in `cat $LISTFILE | sort`; do
    NAME=`basename $FILE .gz`
    TIME=`date`
    echo "[$TIME]> $FILE =[SIMPLE-FILTERING]=> $STORAGE/$NAME.in"

    #filter & split data among shard files
    gunzip -c $FILE | sh file_filtering.sh > $STORAGE/$NAME.in 2>> /tmp/preprocess_errors.log

    #TIME=`date`
    #echo "[$TIME]< $FILE =[SIMPLE-FILTERING]=> $STORAGE/$NAME.in"
done


#filtering according to legal names
echo "FILTERING FILES ACCORDING TO LIST OF LEGAL VALUES"
ls $STORAGE/*.in > /tmp/temp_list_of_files
sh files_filtering.sh $LEGALVALUES /tmp/temp_list_of_files 2>> /tmp/preprocess_errors.log
rm $STORAGE/*.in

echo "ERRORS:"
cat /tmp/preprocess_errors.log
echo "DONE."

