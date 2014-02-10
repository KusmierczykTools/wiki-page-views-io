#!/bin/sh

java -jar -Xms512M -Xmx5120M bin/WikiViewsFlushCache.jar $1 $2 $3
