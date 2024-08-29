#i/bin/sh

path=$1

for file1 in $path/*_result.txt
do
	file2=${file1/_result.txt/}
	file3=${file2##/*/}
	value=`sed -n '2p' $file1`
	echo '|' $file3 $value '|'
done

