/u/local/apps/R/R-2.15.1-gnu/bin/R CMD BATCH --args -snp=./test/X_rightdim.txt -pheno=./test/Y_rightdim.txt -out=./test/ -- .//inputMS.R

/usr/bin/java -Xmx2048m -jar .//Metasoft.jar -input ./test//inputMS.txt -mvalue -mvalue_method mcmc -mcmc_sample 1000000 -seed 0 -mvalue_p_thres 1.0 -mvalue_prior_sigma 0.05 -mvalue_prior_beta 1 5 -pvalue_table .//HanEskinPvalueTable.txt -output ./test//posterior.txt

/u/local/apps/R/R-2.15.1-gnu/bin/R CMD BATCH --args -snp=./test/X_rightdim.txt -pheno=./test/Y_rightdim.txt -MvalueThreshold=0.5 -Mvalue=./test//posterior.txt -minGeneNumber=10 -Pdefault=./test/p_ttest.txt -out=./test/ -NICE=./ -- .//NICE.R

