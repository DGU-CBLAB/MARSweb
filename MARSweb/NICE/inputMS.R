############### define functions ###############
getZscore = function(snp_one,pheno_one) {                               #for (one snp* every indi) and (one pheno * every indi)
        coeff = summary(lm(pheno_one~snp_one))$coeff
        if (dim(coeff)[1] == 1) return(NA)
        else if(is.na(coeff[2,4])) return(NA)   ### this has been changed
        else {
                zscore = abs(qnorm(coeff[2,4]/2))
                if (coeff[2,3] >= 0) return(zscore)
                else return(-1*zscore)
        }
}
getBeta = function(snp_one,pheno_one) {                                  #for (one snp* every indi) and (one pheno * every indi)
        coeff = summary(lm(pheno_one~snp_one))$coeff
        if (dim(coeff)[1] == 1) return(NA)
        else return(coeff[2,1])
}
############### get input ###############
args=(commandArgs(TRUE))
print(length(args))
if(length(args)!=3){
        print("Usage: R CMD BATCH --args -snp=X.txt -pheno=Y.txt -out=./ -- generateInputMS.R\n\t
			snp is sample by snp matrix/vector\n\t
			pheno is sample by phenotypes matrix\n")
	stop ()
}
for( i in c(1:3)){
	str = unlist(strsplit(args[i],"="))[1]
	if(str=="-snp") 	snp = unlist(strsplit(args[i],"="))[2]
	else if(str=="-pheno") 	pheno = unlist(strsplit(args[i],"="))[2]
	else if(str=="-out")    out = unlist(strsplit(args[i],"="))[2]
	else{
		print(str)
	        print("Usage: R CMD BATCH --args -snp=X.txt -pheno=Y.txt -out=./ -- generateInputMS.R\n\t
                        snp is sample by snp matrix/vector\n\t
                        pheno is sample by phenotypes matrix\n")
        	stop ()
	}
}
print(snp)
print(pheno)
print(out)
################# generate input for metasoft for snp i ################
snp = as.matrix(read.table(snp))
pheno = as.matrix(read.table(pheno))
snpNum= dim(snp)[2]
phenoNum= dim(pheno)[2]
print(snpNum)
print(phenoNum)
Beta = mat.or.vec(snpNum,phenoNum)                                       # snp * pheno matrix
Zscore = mat.or.vec(snpNum,phenoNum)
for(i in c(1:phenoNum)){
        print(i)
        Beta[,i] = apply(snp, 2, getBeta, pheno[,i])                            # fill ith column (pheno i and all the snps)
	Zscore[,i] = apply(snp, 2, getZscore, pheno[,i])
}
inputBEM = mat.or.vec(snpNum, phenoNum*2)                                # snp*(pheno*2) matrix
inputBEM = matrix(rbind(Beta,Beta/Zscore),nrow=snpNum)                  # (beta, stderr=beta/Zscore)
RSID = matrix(c(1:snpNum), nrow=snpNum, ncol=1)
inputMS =cbind(RSID, inputBEM)
write.table(inputMS, paste(out,"/inputMS.txt",sep=""), row.names=F, col.names=F)
