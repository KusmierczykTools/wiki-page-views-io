#!/bin/sh

grep "^en " | python column_fgrep.py $1 2

