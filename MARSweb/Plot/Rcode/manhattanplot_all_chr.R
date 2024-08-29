rm(list=ls())
library(ggplot2)
library(ggrepel)
library(dplyr)

#### get input ####
args=(commandArgs(TRUE))
print(length(args))
if(length(args)!=3){
  print("Usage: R CMD BATCH --args -result=result_file -threshold=0.01 -out=file.tiff -- eqtlPlot.R\n
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

data <-  read.table(result_path, header = T, stringsAsFactors=F)

## Manhattan plot
data <- data %>%
  mutate(LRT_pvalue = ifelse(LRT_pvalue == 0, min(data$LRT_pvalue[data$LRT_pvalue > 0]) * 0.1, LRT_pvalue))

interest_gene1 = data[(data$LRT_pvalue < threshold & data$UNI_pvalue > threshold),]$GENE #MARSweb
interest_gene2 = data[(data$LRT_pvalue > threshold & data$UNI_pvalue < threshold),]$GENE #Set-based
interest_gene3 = data[(data$LRT_pvalue < threshold & data$UNI_pvalue < threshold),]$GENE #Common

# Prepare the dataset
{
  don <- data %>% 
    
    # Compute chromosome size
    group_by(CHR) %>% 
    summarise(chr_len=max(BP)) %>% 
    
    # Calculate cumulative position of each chromosome
    mutate(tot=cumsum(chr_len)-chr_len) %>%
    select(-chr_len) %>%
    
    # Add this info to the initial dataset
    left_join(data, ., by=c("CHR"="CHR")) %>%
    
    # Add a cumulative position of each SNP
    arrange(CHR, BP) %>%
    mutate( BPcum=BP+tot) %>%
    
    # Add highlight and annotation information
    mutate( is_highlight=ifelse(GENE %in% interest_gene1, "yes", "no")) %>% mutate( is_annotate=ifelse(GENE %in% interest_gene1, "yes", "no")) %>%  
    mutate( is_highlight2=ifelse(GENE %in% interest_gene2, "yes", "no")) %>% mutate( is_annotate2=ifelse(GENE %in% interest_gene2, "yes", "no")) 
  
  # Prepare X axis
  axisdf <- don %>% group_by(CHR) %>% summarize(center=( max(BPcum) + min(BPcum) ) / 2 )
  
  # Make the plot
  man_plot <- ggplot(don, aes(x=BPcum, y=-log10(LRT_pvalue))) +
    
    # Show all points
    geom_point(data=subset(don, CHR%%2==1),color="gray40", size=1) +
    geom_point(data=subset(don, CHR%%2==0),aes(color="Common"), size=1) +
    #geom_point(aes(color=as.factor(CHR)), size=1) +
    scale_color_manual(values = c("MARSweb"="red", "Set-based"="blue", "Common"="gray")) +
    # custom X axis:
    scale_x_continuous( label = axisdf$CHR, breaks= axisdf$center ) +
    scale_y_continuous(expand = c(0, 0) ) +     # remove space between plot area and x axis
    ylim(0,7) +
    # Add highlighted points
    geom_point(data=subset(don, is_highlight=="yes"), aes(color="MARSweb"), size=1.5) +
    geom_point(data=subset(don, is_highlight2=="yes"), aes(color="Set-based"), size=1.5) +
    geom_hline(yintercept = -log10(threshold), linetype = "solid", color = "purple") + 
    
    # Add label using ggrepel to avoid overlapping
    #  geom_label_repel( data=subset(don, is_annotate=="yes"), aes(label=GENE), size=2, color="red") +
    #  geom_label_repel( data=subset(don, is_annotate2=="yes"), aes(label=GENE), size=2, color="blue") +
    
    # Custom the theme:
    theme_bw() +
    theme( 
      panel.border = element_blank(),
      panel.grid.major.x = element_blank(),
      panel.grid.minor.x = element_blank(),
      legend.title = element_blank(),
      legend.key = element_rect(fill = "white"),  # Optional: white background for legend key
      legend.text = element_text(face = "bold"),
      legend.position= c(0.9,0.9),
      legend.margin = margin(0,0,0,0)
    ) +   labs(x = "Chromosome")
  
  pdf(paste(outputpath,".pdf", sep=""))
  print(man_plot)
  dev.off()
  # write gene list
  writeLines(interest_gene1,(paste(outputpath,"_only_MARSweb_gene_list.txt", sep="")))
  writeLines(interest_gene2,(paste(outputpath,"_only_Set-based_gene_list.txt", sep="")))
  writeLines(interest_gene3,(paste(outputpath,"_Common_gene_list.txt", sep="")))
}
#dev.off()
