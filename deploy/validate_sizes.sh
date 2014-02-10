#!/bin/bash

echo "Validates sizes of gz files (wikipedia pageviews)"
echo "Single argument expected: path of a file with list of files as lines."



for FILE in `cat $1`
do
	SIZE=`du $FILE | cut -f 1`
	if [ "$SIZE" -lt "1000" ]
	then
		echo "$FILE has size $SIZE!"
    else
        echo "$FILE ok."
	fi
done

echo "Done."
