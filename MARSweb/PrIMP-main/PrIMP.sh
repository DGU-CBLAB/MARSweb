#!/bin/sh

if [ $# -ne 2 ]; then
	echo 'usage: PrIMP.sh [target_fastq.fasta] [output.csv] '
	exit 0
fi

path="/DATA/taegun/NICER-master/PrIMP-main"


source ~/.bash_profile
source $path/PrIMP/bin/activate

python3 $path/PrIMP.py --fasta $1 --output output.csv

deactivate
