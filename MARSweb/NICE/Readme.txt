###########################################################
Download
###########################################################
NICE.zip contains the followings,

NICE software
	NICE.R : NICE main software
	NICE_script.jar : Generates a script for running NICE
	Readme
	License
METASOFT
	Metasoft.jar : METASOFT main software
	HanEskinPvalueTable.txt : Tabulated p-value file required for running METASOFT
	inputMS.R : Generates input file for running METASORT
Else
	emma : Linear mixed model based program to correct for confounding/population structure. For details please visit emma.
	Test data: test/

###########################################################
Steps for Running NICE
###########################################################
1. Generate an option file NICE_option.txt where you set all the options that you want to use for running NICE. For details about the option file, please read Options below.
2. Run NICE_script.jar which will create a script file for running NICE. Command: java -jar NICE_script.jar NICE_option.txt NICE_script.sh
3. Run the script file. Command: ./NICE_script.sh There are three command lines in the script file. The first line is for generating an input for the METASOFT and the second line is for running the METASOFT and the last line is for running the NICE. We put the first two lines for your convenience for whom are not used to run the METASOFT. However, if you already have mvalues estimated from METASOFT or posterior probabilities estimated from any other program, just run the last line with the values

###########################################################
Options: Please provide each option in a line

	java: path of java program on your system
	R: path of R program on your system
	snp: path of SNP file. It should be a sample by snp matrix
	pheno: path pf phenotype file. It should be a sample by phenotype matrix
	min_gene: Minimum number of genes that have only confounding effects but not genetic effects. Default is 10 and minimum is 10.
	pvalue: Default pvalue to be used when NICE is not applicable. For example, if the number of genes that assumes to have only confounding effects but not genetic effects is less than 10, NICE uses the default p-value for the snp. You can use p-value from the standard ttest using the t_test_static in the NICE package.
	out: Output path
	mvalue_threshold: Threshold for selecting genes that have only confounding effects but not genetic effects. Default is 0.5

Below options are for running the MetaSoft. For the details please visit METASOFT
	-mvalue_method: Default mcmc
	-mcmc_sample: Default 1000000
	-seed: Default 0
	-mvalue_p_thres: Default 1.0
	-mvalue_prior_sigma: Default 0.05
	-mvalue_prior_beta: Default 1 5

For more sophisticated options with different context of eQTL mapping, please contact Jong Wha J Joo (jwjoo1204@gmail.com)
