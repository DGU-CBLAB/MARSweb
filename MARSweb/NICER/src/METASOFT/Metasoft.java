////////////////////////////////////////////////////////////////////// 
// Metasoft.java
// (c) 2011-2020 Buhm Han
// 
// This file may be used for your personal use.
// This file may be modified but the modified version must retain this copyright notice.
// This file or modified version must not be redistributed
// without prior permission from the author.
// This software is provided �쏛S IS��, without warranty of any kind.
// In no event shall the author be liable for any claim, damages or other liability
// in connection with the software or the use or other dealings in the software.
package METASOFT;
import java.io.*;
import java.util.*;
import cern.jet.stat.Probability;

public class Metasoft
{
   // Arguments and default values
   private  String  inputFile_  = "";
   private  String  outputFile_ = "out";
   private  String  logFile_    = "log";
   private  String  pvalueTableFile_ = "HanEskinPvalueTable.txt";
   private  double  inputLambdaMeanEffect_    = 1.0;
   private  double  inputLambdaHeterogeneity_ = 1.0;
   private  boolean willComputeMvalue_ = false;
   private  double  priorSigma_ = 0.2;
   private  double  priorAlpha_ = 1.0;
   private  double  priorBeta_  = 1.0;
   private  double  mvaluePvalueThreshold_ = 1E-7;
   private  String  mvalueMethod_ = "exact";
   private  long    mcmcSample_ = 10000;
   private  long    mcmcBurnin_ = 1000;
   private  double  mcmcProbRandom_ = 0.01;
   private  double  mcmcMaxNumFlip_ = 0.1;
   private  boolean willComputeBinaryEffects_ = false;
   private  long    binaryEffectsSample_      = 1000;
   private  long    binaryEffectsLargeSample_ = 100000;
   private  double  binaryEffectsPvalueThreshold_ = 1E-4;
   private  int     seed_ = 0;
   private  boolean isVerbose_ = false;
   // Internally used variables
   private  int    numSnps_;
   private  int    maxNumStudy_;
   private  double outputLambdaMeanEffect_;
   private  double outputLambdaHeterogeneity_;
   private  ArrayList<Double> meanEffectParts_;
   private  ArrayList<Double> heterogeneityParts_;
   private  String argsSummary_;
   public Metasoft(String inputFile, String pvalueTable, String output_file, String log_file) {
	   inputFile_ = inputFile;
	   pvalueTableFile_ = pvalueTable;
	   outputFile_ = output_file;
	   logFile_ = log_file;
	   defaultSetup();
	   double startTime = System.currentTimeMillis();
       System.err.printf("Arguments: "+argsSummary_);
       MetaSnp.readPvalueTableFile(pvalueTableFile_);
       System.err.println("----- Performing meta-analysis");
       doMetaAnalysis();
       computeLambda();
       printLog();
       System.err.println("----- Finished");
       double endTime   = System.currentTimeMillis();
       System.err.printf("----- Elapsed time: %.2f minutes\n", 
		 	(endTime - startTime)/(60*1000F));
   }
   public void defaultSetup() {
	   mvalueMethod_ = "mcmc";
	   willComputeMvalue_ = true;
	   priorSigma_ = 0.05;
	   priorAlpha_ = 1;
	   priorBeta_ = 5;
	   mvaluePvalueThreshold_ = 1.0;
	   mcmcSample_ = 1000000;
	   seed_ = 0;
	      // Manual argument validity checkup
	      System.err.println("-------- Processing arguments ---------");
	      if (inputFile_.equals("")) {
		 printErrorAndQuit("A valid input file must be specified using option -input");
	      }
	      if (inputLambdaMeanEffect_ <= 0.0) {
		 printErrorAndQuit("lambda_mean option takes a float value > 0");
	      }
	      if (inputLambdaHeterogeneity_ <= 0.0) {
		 printErrorAndQuit("lambda_hetero option takes a float value > 0");
	      }
	      if (priorSigma_ <= 0.0) {
		 printErrorAndQuit("mvalue_prior_sigma option takes a float value > 0");
	      }
	      if (priorAlpha_ <= 0.0 || priorBeta_ <= 0.0) {
		 printErrorAndQuit("mvalue_prior_beta option takes two float values > 0");
	      }
	      if (mvaluePvalueThreshold_ < 0.0 || mvaluePvalueThreshold_ > 1.0) {
		 printErrorAndQuit("mvalue_p_thres takes a float value between 0 and 1");
	      }
	      if (!mvalueMethod_.equals("exact") &&
		  !mvalueMethod_.equals("mcmc")  &&
		  !mvalueMethod_.equals("variational")) {
		 printErrorAndQuit("mvalue_method option only takes a value 'exact' or 'mcmc'");
	      }
	      if (mcmcSample_ < 1) {
		 printErrorAndQuit("mcmc_sample option takes an integer value > 0");
	      }
	      if (mcmcBurnin_ < 1) {
		 printErrorAndQuit("mcmc_burnin option takes an integer value > 0");
	      }
	      if (mcmcSample_ < mcmcBurnin_) {
		 printErrorAndQuit("mcmc_sample must be larger than mcmc_burnin");
	      }
	      if (mcmcProbRandom_ < 0.0 || mcmcProbRandom_ > 1.0) {
		 printErrorAndQuit("mcmc_prob_random takes a float value between 0 and 1");
	      }
	      if (mcmcMaxNumFlip_ <= 0.0) {
		 printErrorAndQuit("mcmc_max_num_flip takes a value > 0");
	      }
	      if (binaryEffectsSample_ < 1) {
		 printErrorAndQuit("binary_effects_sample option takes an integer value > 0");
	      }
	      if (binaryEffectsLargeSample_ < 1) {
		 printErrorAndQuit("binary_effects_large option takes an integer value > 0");
	      }
	      if (binaryEffectsPvalueThreshold_ < 0.0 || binaryEffectsPvalueThreshold_ > 1.0) {
		 printErrorAndQuit("binary_effects_p_thres takes a float value between 0 and 1");
	      }
   }
   
   private static void printErrorAndQuit(String msg) {
      System.err.println(msg);
      System.exit(-1);
   }

   private  void doMetaAnalysis() {
      Random random = new Random(seed_);
      numSnps_ = 0;
      maxNumStudy_ = 0;
      meanEffectParts_    = new ArrayList<Double>();
      heterogeneityParts_ = new ArrayList<Double>();
      MetaSnp metaSnp;    // Store only 1 Snp at a time in memory.
      BufferedReader bufferedReader = null;
      PrintWriter printWriter = null;
      try {
	 bufferedReader = new BufferedReader(new FileReader(inputFile_));
      }
      catch (Exception exception) {
	 printErrorAndQuit("ERROR: Input file cannot be opened");
      }
      try {
	 printWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFile_)));
      }
      catch (Exception exception) {
	 printErrorAndQuit("ERROR: Ouput file cannot be opened");
      }
      // Print headings
      MetaSnp.printHeadings(printWriter);
      try {
	 // Read 1 Snp information
	 String readLine;
	 while((readLine = bufferedReader.readLine()) != null) {
	    String[] tokens = readLine.split("\\s+");
	    if (tokens.length > 1) {             // only if non-empty
	       if (tokens[0].charAt(0) != '#') { // only if non-comment
		  String rsid = tokens[0];
		  metaSnp = new MetaSnp(rsid);
		  if (tokens.length % 2 == 0)
		     System.err.println("WARNING: # of Columns must be " +
					"odd including Rsid. Last column is ignored.");
		  int nStudy = (int)((tokens.length-1)/2);
		  if (nStudy > maxNumStudy_) {
		     maxNumStudy_ = nStudy;
		  }
		  for (int i = 0; i < nStudy; i++) {
		     double beta;
		     double standardError;
		     if (tokens[2*i+1].equals("NA") ||
			 tokens[2*i+1].equals("N/A") ||
			 tokens[2*i+2].equals("NA") ||
			 tokens[2*i+2].equals("N/A")) {
			metaSnp.addNaStudy();
		     } else {
			try {
			   beta = new Double(tokens[2*i+1]);
			   standardError = new Double(tokens[2*i+2]);
			   if (standardError <= 0.0) {
			      System.err.printf("Standard error cannot be <= zero "+
						"(%d th column is %f) "+
						"in the following line.\n",
						2*i+3, standardError);
			      System.err.println(readLine);
			      System.exit(-1);
			   }
			   metaSnp.addStudy(beta, standardError);
			} 
			catch (Exception exception) {
			   System.err.println("Incorrect float value in following line. "+
					      "Possibly not a double");
			   System.err.println(readLine);
			   System.exit(-1);
			}
		     }
		  }
		  if (metaSnp.getNStudy() > 1) {
		     // Analyze 1 Snp on-the-fly.
		     if (isVerbose_ && numSnps_ % 1000 == 0) {
			System.err.printf("Analyzing SNP #%d (%s)\n", numSnps_ + 1, rsid);
		     }
		     // FE, RE, and New RE
		     metaSnp.computeFixedEffects();
		     metaSnp.computeRandomEffects();
		     metaSnp.computeHanEskin(inputLambdaMeanEffect_,
					     inputLambdaHeterogeneity_);
		     meanEffectParts_.add(metaSnp.getStatisticHanEskinMeanEffectPart());
		     double h = metaSnp.getStatisticHanEskinHeterogeneityPart();
		     if (h > 0.0) {
			heterogeneityParts_.add(h);
		     }
		     // Binary effects model
		     if (willComputeBinaryEffects_) {
			metaSnp.computeBinaryEffectsPvalue(binaryEffectsSample_, random.nextInt());
			if (metaSnp.getPvalueBinaryEffects() <= binaryEffectsPvalueThreshold_) {
			   metaSnp.computeBinaryEffectsPvalue(binaryEffectsLargeSample_, random.nextInt());
			}
		     }
		     // Mvalues
		     if (willComputeMvalue_) {
			if (metaSnp.getPvalueFixedEffects() <= mvaluePvalueThreshold_ ||
			    metaSnp.getPvalueHanEskin()     <= mvaluePvalueThreshold_) {
			   if (mvalueMethod_.equals("exact")) {
			      metaSnp.computeMvalues(priorAlpha_, priorBeta_, priorSigma_);
			   } else if (mvalueMethod_.equals("mcmc")) {
			      metaSnp.computeMvaluesMCMC(priorAlpha_, priorBeta_, priorSigma_,
							 mcmcSample_, mcmcBurnin_, mcmcProbRandom_, 
							 mcmcMaxNumFlip_, 
							 random.nextInt());
			   } else {
			      assert false : mvalueMethod_;
			   }
			}
		     }
		     numSnps_++;
		  }
		  metaSnp.printResults(printWriter);

	       }
	    }
	 }
      }
      catch (IOException exception) {
	 System.err.println("ERROR: error encountered while reading input file");
	 System.exit(-1);
      }
      try {
	 bufferedReader.close();
	 printWriter.close();
      }
      catch (Exception exception) {
	 System.err.println("ERROR: file cannot be closed");
	 System.exit(-1);
      }
   }
   
   private void computeLambda() {
      double median;
      double expectedMedian;
      if (meanEffectParts_.size() > 0) {
	 Collections.sort(meanEffectParts_);
	 median = meanEffectParts_.get((int)(meanEffectParts_.size()/2.0));
	 expectedMedian = Math.pow(Probability.normalInverse(0.25),2.0);
	 outputLambdaMeanEffect_ = median / expectedMedian;
      }
      if (heterogeneityParts_.size() > 0) {
	 Collections.sort(heterogeneityParts_);
	 median = heterogeneityParts_.get((int)(heterogeneityParts_.size()/2.0));
	 if (maxNumStudy_ > 50) {
	    expectedMedian = Math.pow(Probability.normalInverse(0.25),2.0);
	 } else {
	    expectedMedian = expectedMedianHanEskinHeterogeneityPart_[maxNumStudy_ - 2];
	 }
	 outputLambdaHeterogeneity_ = median / expectedMedian;
      }
   }

   private void printLog() {
      try {
	 PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(logFile_)));
	 printWriter.printf("Arguments_this: %s ", argsSummary_);
	 printWriter.printf("Input File: %s\n", inputFile_);
	 printWriter.printf("Output File: %s\n", outputFile_);
	 printWriter.printf("Log File: %s\n", logFile_);
	 printWriter.printf("p-value Table File: %s\n", pvalueTableFile_);
	 printWriter.printf("Number of SNPs analyzed: %d\n", numSnps_);
	 printWriter.printf("Maximum number of studies: %d\n", maxNumStudy_);
	 printWriter.printf("Specified lambda for   mean effect part (default = 1.0): %f\n", inputLambdaMeanEffect_);
	 printWriter.printf("Specified lambda for heterogeneity part (default = 1.0): %f\n", inputLambdaHeterogeneity_);
	 printWriter.printf("Newly calculated inflation factor lambda for   mean effect part: %f\n", outputLambdaMeanEffect_);
	 printWriter.printf("Newly calculated inflation factor lambda for heterogeneity part: %f\n", outputLambdaHeterogeneity_);
	 printWriter.close();
      }
      catch (Exception exception) {
	 System.err.println("ERROR: error encountered while writing in log file");
	 System.exit(-1);
      }
   }

   private static double[] expectedMedianHanEskinHeterogeneityPart_ = // from nStudy 2 to 50
   {0.2195907137,0.2471516439,0.2642270318,0.2780769264,0.2886280267,0.2977812664,0.3020051148,0.3091428179,0.3158605559,0.3221788173,0.3259133140,0.3295976587,0.3335375196,0.3358395088,0.3368309971,0.3421941686,0.3448030927,0.3463590948,0.3477384754,0.3487900288,0.3494938171,0.3542087791,0.3573286353,0.3589703411,0.3586951356,0.3596101209,0.3605611682,0.3624799993,0.3648322669,0.3659817739,0.3671267389,0.3693952373,0.3693395144,0.3696863113,0.3706067524,0.3718103285,0.3749536619,0.3758886239,0.3753612342,0.3781458299,0.3798346038,0.3763434983,0.3796968747,0.3784334922,0.3794411347,0.3808582942,0.3813485882,0.3843230993,0.3824863479};
}
