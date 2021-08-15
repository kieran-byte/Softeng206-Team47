#!/bin/bash

#iterates through all lines of a file
lineNumber=1

inputFile="gameData/questions/allInternationalQuestions"
outputFile="gameData/questions/internationalSave"

declare -a endPositions
declare -a startPositions
declare -a categoryNames
nextAddPoint=1

# the first start position will always be 1
startPositions+=("1")

while read line; do
      	
      	#Adds category names to array 
      	if [[ $lineNumber -eq $nextAddPoint ]]; then
      		categoryNames+=("$line")
      	fi
      	
      	
      	#Checks if a line is empty. If it is, then the next line must be the start of the next category
      	#so we set the point(line number) where we'll add next category	
      	if [ -z "$line" ]; then
    		lineNumberEmptyLine=$((lineNumber+1))
      		startPositions+=("$lineNumberEmptyLine")
      		nextAddPoint=$lineNumber
      		nextAddPoint=$((nextAddPoint + 1))
      	fi		
      
      lineNumber=$((lineNumber + 1 ))
      
       #ends the loop when its done
done < $inputFile

# latest categories end point will be lineNumber-1
#now need to calcualte end posiitons given start positions
startCount=0
posNum=0
for z in ${startPositions[@]}; do

	#must minus two to find the end position of the last category
	if [[  posNum -ne startCount ]]; then
		endPositionOfCategory=${startPositions[$posNum]}
		#echo "$temp"
		endPositionOfCategory=$((endPositionOfCategory - 2))
		endPositions[$posNum]=$endPositionOfCategory
	fi
	
	posNum=$((posNum + 1))
done

lineNumber=$((lineNumber - 1))
endPositions+=("$lineNumber") #Sets end position for last category to be the last line of file
maxNum=${#categoryNames[@]} #Maximum number of categories
maxNum=$((maxNum - 1))

#now need to generate 5 unqiue numbers and store them in a variable
unqiueNumArray=$(shuf -i 0-$maxNum -n 5)

echo "" >> $outputFile
#Loop goes through each selected category and writes 5 random questions to current questions filee
for i in $unqiueNumArray; do
	#Writes the categoryName to file
	currentCategoryName=${categoryNames[i]}
	echo $currentCategoryName >> $outputFile
	
	#Generates 5 random line numbers within bounds for current category's questions
	uniqueRandomNumbers=$(shuf -i $(( ${startPositions[i]} + 1 ))-${endPositions[i+1]} -n 5)
	echo $uniqueRandomNumbers
	
	#Writes question and answer to current questions files
	for j in $uniqueRandomNumbers; do
		question=$(sed "${j}q;d" $inputFile) #Collects string on line
		echo $question >> $outputFile #Writes to file
		
	done
	
	#Writes an empty line to separate the categories
	echo "" >> $outputFile
done

#Trims last empty line from file
sed -i '$ d' $outputFile



