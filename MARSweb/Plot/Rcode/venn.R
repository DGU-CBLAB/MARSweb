rm(list=ls())
#### get input ####
args=(commandArgs(TRUE))
print(length(args))
if(length(args)!=3){
  print("Usage: R CMD BATCH --args -result=result_file -threshold=0.01 -out=file.tiff -- venn.R\n
                        result is output file by MarsNet\n")
  stop ()
}
for( i in c(1:3)){
  str = unlist(strsplit(args[i],"="))[1]
  if(str=="-result")                  result_path = unlist(strsplit(args[i],"="))[2]
  else if(str=="-out")             outputpath = unlist(strsplit(args[i],"="))[2]
  else if(str=="-threshold")   threshold = as.double(unlist(strsplit(args[i],"="))[2])
  else{
    print(str);
    print("Usage: R CMD BATCH --args -result=result_file -threshold=0.01 -out=file.tiff -- venn.R\n
                        result is output file by MarsNet\n")
    stop ()
  }
}
# venndiagram
library(VennDiagram)

#result_path, threshold, outputpath
result_data <- read.table(result_path, header = T, stringsAsFactors=F)

venn.plot <- venn.diagram(
  x = list(
    "LRT" = which(result_data$LRT_pvalue < threshold),   
    "UNI" = which(result_data$UNI_pvalue < threshold)    
  ),
  filename = outputpath,
  col = "black",   
  fill = c("blue", "red"),   
  alpha = 0.50,   
  cat.col = c("blue", "red"),   
  cat.cex = 1.5,   
  cat.fontface = "bold",   
  margin = 0.05,
  scaled = FALSE
)
