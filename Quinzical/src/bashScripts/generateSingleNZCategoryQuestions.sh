#!/bin/bash

categoryFound=0
categoryStart=0
categoryEnd=0
currentLine=1

inputFile="gameData/questions/allNewZealandQuestions"
outputFile="gameData/questions/currentNewZealandQuestions"

touch $outputFile

#Locates which lines correspond to the category
while read line; do
	#stores the first line of the category when found
	if [[ $line == $CATEGORYNAME ]]; then
		categoryFound=1;
		categoryStart=$(($currentLine + 1))
	fi
	
	#Stores the last line of the category when found
	if [ -z  "$line" ] && [[ $categoryFound -eq 1 ]]; then
		categoryEnd=$((currentLine - 1))
		break
	fi

	currentLine=$((currentLine + 1))
done < $inputFile

#Picks 5 random questions from the category
questionLineNumbers=$(shuf -i $categoryStart-$categoryEnd -n 5)

#Picks out line from alQuestions and writes in to category file
echo $CATEGORYNAME >> $outputFile
for i in $questionLineNumbers; do
	question=$(sed "${i}q;d" $inputFile) #Collects string on line
	echo $question >> $outputFile #Writes to file
done

#Empty line spacer between categories
echo "" >> $outputFile

