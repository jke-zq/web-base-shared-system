#!/bin/bash

arg="$1"

cd ..
rootPath=$(pwd)
tempFile="$rootPath/tempFiles/$arg"

if [ $# -ne 1 ]
then
        echo "Usage: $0 {file-name}"
	exit 1
fi

if [ -f "$tempFile" ]
then
	/bin/rm -f "$tempFile"
else    
	echo "$tempFile  file does not  exit!"
	exit 0
fi

