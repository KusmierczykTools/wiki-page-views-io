#!/bin/sh

echo "THE SCRIPTS DOWNLOADS PAGEVIEWS OF WIKIDUMPS."
echo "TWO ARGS ARE EXPECTED: YEAR MONTH."

#configuration:
BASEURL="http://dumps.wikimedia.org/other/pagecounts-raw"
MDFILE="md5sums.txt"

#verify arguments:
if [ -z "$1" ]
  then
    echo "FIRST ARGUMENT NEEDED: YEAR IN FORMAT YYYY"
    exit
fi
if [ -z "$2" ]
  then
    echo "SECOND ARGUMENT NEEDED: MONTH IN FORMAT MM"
    exit
fi

#retrieve list:
URL="$BASEURL/$1/$1-$2"
echo "DOWNLOADING YEAR: $1 MONTH: $2 URL: $URL"
if [ -f $MDFILE ];
then
    rm $MDFILE
fi
wget "$URL/md5sums.txt"

#parse list:
for FILE in `cat md5sums.txt | cut -d " "  -f 3`; do
    echo "DOWNLOADING FILE $URL/$FILE"
    wget -c "$URL/$FILE"
done

#clean up
if [ -f $MDFILE ];
then
    rm $MDFILE
fi

