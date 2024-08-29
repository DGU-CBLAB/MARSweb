#!/bin/bash
#$ -N sim
#$ -cwd
#$ -o /u/home/j/jwjjoo/project/ICEp/program/rCode/NICE/NICE_temp/NICE/test2/1_runMetaSoft.$JOB_ID.out
#$ -e /u/home/j/jwjjoo/project/ICEp/program/rCode/NICE/NICE_temp/NICE/test2/1_runMetaSoft.$JOB_ID.err
#$ -l h_data=32G,h_rt=23:59:59
#$ -l highp
/usr/bin/java -Xmx2048m -jar /u/home/j/jwjjoo/project/ICEp/program/rCode/NICE/NICE_temp/NICE//Metasoft.jar -input /u/home/j/jwjjoo/project/ICEp/program/rCode/NICE/NICE_temp/NICE/test2/inputMS.txt -mvalue -mvalue_method mcmc -mcmc_sample 1000000 -seed 0 -mvalue_p_thres 1.0 -mvalue_prior_sigma 0.05 -mvalue_prior_beta 1 5 -pvalue_table /u/home/j/jwjjoo/project/ICEp/program/rCode/NICE/NICE_temp/NICE/HanEskinPvalueTable.txt -output /u/home/j/jwjjoo/project/ICEp/program/rCode/NICE/NICE_temp/NICE/test2/posterior_.txt
