#i/bin/sh

path="$1"
for file1 in $path/*_geno
do
	file2=${file1/_geno/}
	file3=${file2##/*/}
	num=`jobs|wc -l`
	if [[ $num -lt $2 ]]; then
		nohup /Data/ktg/cblab_webserver/NICER-master/Mars/Mars_cpp -geno "$file2"_geno -stat "$file2"_stat -sim $3 -mode $4 > "$1"/"$file3"_result.txt &
	else
		until [[ $num -lt $2 ]];
		do
			sleep 5
			num=`jobs|wc -l`
		done
		nohup /Data/ktg/cblab_webserver/NICER-master/Mars/Mars_cpp -geno "$file2"_geno -stat "$file2"_stat -sim $3 -mode $4 > "$1"/"$file3"_result.txt &
	fi
done

num=`jobs|grep "Running"|wc -l`
echo $num
while [ $num -gt 0 ]
do
	sleep 5
	num=`jobs|grep "Running"|wc -l`
	echo $num 
done
echo 'setname LRT_pvalue UNI_pvalue' >> $path/result.txt
for file1 in $path/*_result.txt
do
	file2=${file1/_result.txt/}
	file3=${file2##/*/}
	value=`sed -n '16p' $file1`
	echo $file3 $value >> $path/result.txt
done
