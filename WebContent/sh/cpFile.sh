#!/bin/bash

arg="$1"
userName=`echo "$arg" | cut -d '#' -f1`
fileName=`echo "$arg" | cut -d '#' -f2 | cut -d '*' -f1`
#to assemble the dir and the file path

cd ..
rootPath=$(pwd)
tempFile="$rootPath/tempFiles/$arg"
userDir="$rootPath/uploadFiles/$userName"

if [ $# -ne 1 ]
then
        echo "Usage: $0 {file-name}"
	exit 1
fi

if [ -f "$userDir/$fileName" ]
then
	echo "$userDir/$fileName file  exits!"
	exit 0
else
	if [ -d "$userDir" ]
		then 
		echo "$userDir exists!"
	else
		echo "$userDir diretory not found!"
		mkdir $userDir
	fi
fi

cp  "$tempFile"  "$userDir/$fileName"
