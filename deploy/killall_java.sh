#!/bin/sh

echo "KILLS ALL JAVA PROCESSES OF SPECIFIED USER."

#verify arguments:
if [ -z "$1" ]
  then
    echo "FIRST ARGUMENT NEEDED:  SPECIFIED USER"
    exit
fi
USERNAME=$1

for PROCESS_NO in `ps U $USERNAME | grep java | cut -d " " -f 2`; do
    echo "KILLING: $PROCESS_NO"
    kill $PROCESS_NO
done

