#!/bin/sh


if [ $# -ne 8 ]; then
	echo 'usage: mtvcpp.sh [gene_file] [pheno_file] [windowsize] [sampling_count] [threshold_file] [out_dir] [c_matrix_file] [r_matrix_file]'
	exit 0
fi

path="/DATA/taegun/NICER-master/mtvcpp"

mkdir $6
chmod 777 $6 


$path/multitrans $1 $2 $7 $8 $3
$path/slide_1prep -C $7 $3 $6/prep
$path/slide_2run $6/prep $6/maxstat $4 123
$path/slide_3sort $6/sorted $6/maxstat
$path/slide_4correct -p $6/sorted $5 $6/MultiTrans.txt
