#!/bin/sh

echo "THE SCRIPT VALIDATES LIST OF INPUT GZ FILES."
echo "FOLLOWING ARGUMENT IS EXPECTED:"
echo " FILE WITH LIST OF INPUT GZ FILES."


#verify arguments:
if [ -z "$1" ]
  then
    echo "FIRST ARGUMENT NEEDED: FILE WITH LIST OF INPUT GZ FILES "
    exit
fi
LISTFILE=$1
echo "[ARGUMENT] FILE WITH LIST OF INPUT GZ FILES: $LISTFILE"


#simple validation
echo "VALIDATING LIST OF FILES"
sh validate_sizes.sh $LISTFILE
python validate_timeline2.py < $LISTFILE
