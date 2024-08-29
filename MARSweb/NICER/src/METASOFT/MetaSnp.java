////////////////////////////////////////////////////////////////////// 
// MetaSnp.java
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
import java.util.concurrent.*;
import cern.jet.stat.Probability;
import org.apache.commons.math.special.Gamma;
import org.apache.commons.math.special.Beta;

public class MetaSnp 
{
   private String  rsid_;
   private int     nStudy_            = 0;
   private int     nStudyIncludingNa_ = 0;
   private double      statisticFixedEffects_;
   private double         pvalueFixedEffects_;
   private double           betaFixedEffects_;
   private double  standardErrorFixedEffects_;
   private double      statisticRandomEffects_;
   private double         pvalueRandomEffects_;
   private double           betaRandomEffects_;
   private double  standardErrorRandomEffects_;
   private double      statisticHanEskin_;
   private double      statisticHanEskinMeanEffectPart_;
   private double      statisticHanEskinHeterogeneityPart_;
   private double         pvalueHanEskinAsymptotic_;
   private double         pvalueHanEskinTabulated_;
   private double      statisticBinaryEffects_;
   private double         pvalueBinaryEffects_;
   private double      statisticQ_;
   private double         pvalueQ_;
   private double      statisticISquare_;
   private double      statisticTauSquare_;
   private boolean isFixedEffectsComputed_           = false;
   private boolean isRandomEffectsComputed_          = false;
   private boolean isHeterogeneityComputed_          = false;
   private boolean isHvaluesComputed_                = false;
   private boolean isMvaluesComputed_                = false;
   private boolean isHanEskinComputed_               = false;
   private boolean isBinaryEffectsStatisticComputed_ = false;
   private boolean isBinaryEffectsPvalueComputed_    = false;
   private ArrayList<Double>  betas_;
   private ArrayList<Double>  standardErrors_;
   private ArrayList<Boolean> isNa_;
   private ArrayList<Double>  hvalues_;
   private ArrayList<Double>  mvalues_;
   private static final double ML_ESTIMATE_CHANGE_RATIO_THRESHOLD = 0.00001;
   private static final double LOG_SQRT2PI = 0.5*Math.log(2*Math.PI);

   public MetaSnp (String rsid) {
      rsid_                    = rsid;
      betas_                   = new ArrayList<Double>();
      standardErrors_          = new ArrayList<Double>();
      isNa_                    = new ArrayList<Boolean>();
      hvalues_                 = new ArrayList<Double>();
      mvalues_                 = new ArrayList<Double>();
   }

   public void addStudy(double beta, double standardError) {
      nStudy_++;
      betas_.add(beta);
      standardErrors_.add(standardError);
      nStudyIncludingNa_++;
      isNa_.add(false);
   }

   public void addNaStudy() {
      nStudyIncludingNa_++;
      isNa_.add(true);
   }

   public void computeFixedEffects(double lambdaMeanEffect) {
      double [] betas     = new double [nStudy_];
      double [] variances = new double [nStudy_];
      double [] weights   = new double [nStudy_];
      for (int i = 0; i < nStudy_; i++) {
	 betas[i]         = betas_.get(i);
	 variances[i]     = Math.pow(standardErrors_.get(i), 2.0);
	 weights[i]       = 1.0 / variances[i];
      }
      double sumBetaProdWeight = 0.0;
      double sumWeight         = 0.0;
      for (int i = 0; i < nStudy_; i++) {
	 sumBetaProdWeight    += betas[i]   * weights[i];
	 sumWeight            += weights[i];
      }
      betaFixedEffects_          = sumBetaProdWeight / sumWeight;
      standardErrorFixedEffects_ = 1.0               / Math.sqrt(sumWeight);
      statisticFixedEffects_     = sumBetaProdWeight / Math.sqrt(sumWeight);
      statisticFixedEffects_     /= Math.sqrt(lambdaMeanEffect);
      pvalueFixedEffects_        = Probability.chiSquareComplemented(1.0, Math.pow(statisticFixedEffects_, 2.0));
      isFixedEffectsComputed_    = true;
   }

   public void computeFixedEffects() {
      computeFixedEffects( 1.0 );
   }

   public void computeHeterogeneity() {
      double [] betas     = new double [nStudy_];
      double [] variances = new double [nStudy_];
      double [] weights   = new double [nStudy_];
      for (int i = 0; i < nStudy_; i++) {
	 betas[i]         = betas_.get(i);
	 variances[i]     = Math.pow(standardErrors_.get(i), 2.0);
	 weights[i]       = 1.0 / variances[i];
      }
      double sumBetaProdWeight = 0.0;
      double sumWeight         = 0.0;
      double sumWeightSquare   = 0.0;
      for (int i = 0; i < nStudy_; i++) {
	 sumBetaProdWeight    += betas[i]   * weights[i];
	 sumWeight            += weights[i];
	 sumWeightSquare      += weights[i] * weights[i];
      }
      // Compute Q and ISquare
      double meanBeta = sumBetaProdWeight / sumWeight;
      double Q = 0.;
      for (int i = 0; i < nStudy_; i++) {
	 Q    += weights[i] * (betas[i] - meanBeta) * (betas[i] - meanBeta);
      }
      statisticQ_         = Q;
      pvalueQ_            = Probability.chiSquareComplemented(nStudy_ - 1, Q);
      statisticISquare_   = Math.max( (Q - (double)(nStudy_ - 1)) / Q * 100.0, 0.0);
      // Compute tauSquare
      double meanWeight   = sumWeight / nStudy_;
      double Sw2          = (1.0 / (double)(nStudy_ - 1)) * 
                      	    (sumWeightSquare - nStudy_ * meanWeight * meanWeight);
      double U            = (double)(nStudy_ - 1) * 
	                    (meanWeight - Sw2 / (nStudy_ * meanWeight));
      statisticTauSquare_ = Math.max( (Q - (double)(nStudy_ - 1)) / U, 0.0 );
      isHeterogeneityComputed_ = true;
   }

   public void computeRandomEffects() {
      if (!isHeterogeneityComputed_) 
	 computeHeterogeneity();
      double [] betas     = new double [nStudy_];
      double [] variances = new double [nStudy_];
      double [] weights   = new double [nStudy_];
      for (int i = 0; i < nStudy_; i++) {
	 betas[i]         = betas_.get(i);
	 variances[i]     = Math.pow(standardErrors_.get(i), 2.0);
	 weights[i]       = 1.0 / variances[i];
      }
      double sumBetaProdWeightWithTau = 0.0;
      double sumWeightWithTau         = 0.0;
      for (int i = 0; i < nStudy_; i++) {
	 sumBetaProdWeightWithTau    += betas[i] / (variances[i] + statisticTauSquare_);
	 sumWeightWithTau            +=      1.0 / (variances[i] + statisticTauSquare_);
      }
      betaRandomEffects_          = sumBetaProdWeightWithTau / sumWeightWithTau;
      standardErrorRandomEffects_ = 1.0                      / Math.sqrt(sumWeightWithTau);
      statisticRandomEffects_     = sumBetaProdWeightWithTau / Math.sqrt(sumWeightWithTau);
      pvalueRandomEffects_        = Probability.chiSquareComplemented(1.0, Math.pow(statisticRandomEffects_,2.0));
      isRandomEffectsComputed_    = true;
   }

   public void computeMvalues(double priorAlpha, double priorBeta, double priorSigma) { 
      mvalues_.clear();
      double priorVar = priorSigma * priorSigma;
      double [] betas = new double [nStudy_];
      double [] ts    = new double [nStudy_]; // Precision
      for (int i = 0; i < nStudy_; i++) {
	 betas[i] = betas_.get(i);
	 ts[i]    = 1.0 / Math.pow(standardErrors_.get(i), 2.0);
      }
      boolean [] H1 = new boolean [nStudy_]; // Array defining configuration
      double  [] priorConfig = new double [nStudy_+1]; // Prob of each configuration with respect to # of studies with effect
      for (int i = 0; i <= nStudy_; i++) { 
	 priorConfig[i] = 
	    Math.exp(Beta.logBeta(i + priorAlpha, nStudy_ - i + priorBeta))
	    / Math.exp(Beta.logBeta(priorAlpha, priorBeta));
      }
      double [] accumProbH0 = new double [nStudy_]; // Accumulate probability for each study
      double [] accumProbH1 = new double [nStudy_];
      for (int i = 0; i < nStudy_; i++) {
	 accumProbH0[i] = 0.0;
	 accumProbH1[i] = 0.0;
      }
      for (int T = 0; T < Math.pow(2, nStudy_); T++) { // For all configurations.
	 int t = T;
	 int numH1 = 0;
	 for (int i = 0; i < nStudy_; i++) {
	    if (t % 2 == 1) {
	       H1[i] = true;
	       numH1++;
	    } else {
	       H1[i] = false;
	    }
	    t = (int)(Math.floor(t / 2.0));
	 }
	 // First for null points
	 double probNullPoints = 1.0;
	 for (int i = 0; i < nStudy_; i++) {
	    if (!H1[i]) {
	       probNullPoints *= 
		  Math.sqrt(ts[i]/(2*Math.PI)) * Math.exp(-ts[i] * betas[i] * betas[i] / 2);
	    }
	 }
	 // Second for alternative points
	 double probAltPoints = 1.0;
	 if (numH1 > 0) {
	    double sum_t = 0.0;
	    double sum_tm = 0.0;
	    double sum_tmm = 0.0;
	    double prod_t = 1.0;
	    for (int i = 0; i < nStudy_; i++) {
	       if (H1[i]) {
		  sum_t += ts[i];
		  sum_tm += ts[i] * betas[i];
		  sum_tmm += ts[i] * betas[i] * betas[i];
		  prod_t *= ts[i];
	       }
	    }
	    double betaJoint = sum_tm / sum_t;
	    double tJoint = sum_t;
	    double tFinal = 1 / ((1/tJoint) + priorVar);
	    double scaleFactor = Math.sqrt(prod_t/sum_t) * Math.pow(2*Math.PI,-0.5*(numH1-1)) *
	       Math.exp(-(sum_tmm - sum_tm * sum_tm / sum_t)/2);
	    double jointPDF = 
	       Math.sqrt(tFinal/(2*Math.PI)) * Math.exp(-tFinal * betaJoint * betaJoint / 2);
	    probAltPoints = jointPDF * scaleFactor;
	 }
	 for (int i = 0; i < nStudy_; i++) { 
	    if (H1[i]) {
	       accumProbH1[i] += probNullPoints * probAltPoints * priorConfig[numH1];
	    } else {
	       accumProbH0[i] += probNullPoints * probAltPoints * priorConfig[numH1];
	    }
	 }
      }
      for (int i = 0; i < nStudy_; i++) { 
	 double mvalue = accumProbH1[i] / (accumProbH0[i] + accumProbH1[i]);
	 mvalues_.add( mvalue );
      }
      isMvaluesComputed_ = true;
   }


   public void computeMvaluesMCMC(double priorAlpha, double priorBeta, double priorSigma, long sample, long burnin,
				  double probRandom, double maxNumFlipArg,
				  int seed) {
      int maxNumFlip = 1;
      if (maxNumFlipArg < 1.0) {
	 maxNumFlip = Math.max((int)Math.floor(maxNumFlipArg * nStudy_), 1); 
      } else {
	 maxNumFlip = (int)Math.floor(maxNumFlipArg);
      }
      mvalues_.clear();
      double priorVar = priorSigma * priorSigma;
      Random random   = new Random(seed);
      double [] betas = new double [nStudy_];
      double [] ts    = new double [nStudy_]; // Precision
      for (int i = 0; i < nStudy_; i++) {
	 betas[i] = betas_.get(i);
	 ts[i]    = 1.0 / Math.pow(standardErrors_.get(i), 2.0);
      }
      boolean [] H1 = new boolean [nStudy_]; // Array defining configuration
      double [] logPriorConfig = new double [nStudy_+1]; // Prob of each configuration with respect to # of studies with effect
      for (int i = 0; i <= nStudy_; i++) { 
	 logPriorConfig[i] = 
	    Beta.logBeta(i + priorAlpha, nStudy_ - i + priorBeta)
	    - Beta.logBeta(priorAlpha, priorBeta);
      }
      int [] accumCntH0 = new int [nStudy_]; // Accumulate count for each study
      int [] accumCntH1 = new int [nStudy_];
      for (int i = 0; i < nStudy_; i++) {
	 accumCntH0[i] = 0;
	 accumCntH1[i] = 0;
      }
      // Start from a random configuration
      int numH1 = 0;
      for (int i = 0; i < nStudy_; i++) {
	 H1[i] = random.nextBoolean();
	 if (H1[i]) numH1++;
      }
      long burninCount = burnin;
      long chainCount = 0;
      boolean [] tmp = new boolean [nStudy_];
      int[] shuffleBuffer = new int [nStudy_];
      for (int i = 0; i < nStudy_; i++) {
	 shuffleBuffer[i] = i;
      }
      // Chain
      while (chainCount < sample) {
	 double currentLogProb = observationLogLikelihood(betas, ts, H1, numH1, priorVar) + logPriorConfig[numH1];
	 if (random.nextDouble() > probRandom) {
	    // Usual jump
	    int numFlip = Math.min(random.nextInt(maxNumFlip) + 1, nStudy_);
	    for (int i = 0; i < numFlip; i++) {
	       int pick = random.nextInt(nStudy_-i);
	       int t = shuffleBuffer[i];
	       shuffleBuffer[i] = shuffleBuffer[i+pick];
	       shuffleBuffer[i+pick] = t;
	    }
	    for (int i = 0; i < numFlip; i++) {
	       int j = shuffleBuffer[i];
	       H1[j] = !H1[j];
	       numH1 += H1[j]? 1: -1;
	    }
	    double nextLogProb = observationLogLikelihood(betas, ts, H1, numH1, priorVar) + logPriorConfig[numH1];
	    if (nextLogProb > currentLogProb || random.nextDouble() < Math.exp(nextLogProb - currentLogProb)) {
	       // Move
	       currentLogProb = nextLogProb;
	    } else {
	       // Stay ... revert back
	       for (int i = 0; i < numFlip; i++) {
		  int j = shuffleBuffer[i];
		  H1[j] = !H1[j];
		  numH1 += H1[j]? 1: -1;
	       }
	    }
	 } else {
	    // Randomization move
	    int tmpNumH1 = 0;
	    for (int i = 0; i < nStudy_; i++) { 
	       tmp[i] = random.nextBoolean();
	       if (tmp[i]) tmpNumH1++;
	    }
	    double nextLogProb = observationLogLikelihood(betas, ts, tmp, tmpNumH1, priorVar) + logPriorConfig[tmpNumH1];
	    if (nextLogProb > currentLogProb || random.nextDouble() < Math.exp(nextLogProb - currentLogProb)) {
	       // Move 
	       System.arraycopy(tmp, 0, H1, 0, nStudy_);
	       numH1 = tmpNumH1;
	       currentLogProb = nextLogProb;
	    } else {
	       // Stay ...
	    }
	 }
	 // Are we still in Burn-in?
	 if (burninCount > 0) {
	    burninCount--;
	 } else {
	    for (int i = 0; i < nStudy_; i++) { 
	       if (H1[i]) {
		  accumCntH1[i]++;
	       } else {
		  accumCntH0[i]++;
	       }
	    }
	    chainCount++;
	 }
      }
      for (int i = 0; i < nStudy_; i++) { 
	 double mvalue = (double)accumCntH1[i] / (accumCntH0[i] + accumCntH1[i]);
	 mvalues_.add( mvalue );
      }
      isMvaluesComputed_ = true;
   }

   private double observationLogLikelihood(double[] betas, double[] ts, boolean[] H1, int numH1, double priorVar) {
      int n = betas.length;
      // First for null points
      double logProbNullPoints = 0;
      for (int i = 0; i < n; i++) {
	 if (!H1[i]) {
	    logProbNullPoints += 
	       0.5 * Math.log(ts[i]) - LOG_SQRT2PI - ts[i] * betas[i] * betas[i] / 2;
	 }
      }
      // Second for alternative points
      double logProbAltPoints = 0;
      if (numH1 > 0) {
	 double sum_t = 0.0;
	 double sum_tm = 0.0;
	 double sum_tmm = 0.0;
	 double sum_logt = 0.0;
	 for (int i = 0; i < n; i++) {
	    if (H1[i]) {
	       sum_t += ts[i];
	       sum_tm += ts[i] * betas[i];
	       sum_tmm += ts[i] * betas[i] * betas[i];
	       sum_logt += Math.log(ts[i]);
	    }
	 }
	 double betaJoint = sum_tm / sum_t;
	 double tJoint = sum_t;
	 double tFinal = 1 / ((1/tJoint) + priorVar);
	 double logScaleFactor = 
	    -(numH1-1)*LOG_SQRT2PI + 0.5*sum_logt - 0.5*Math.log(sum_t)
	    -(sum_tmm - sum_tm * sum_tm / sum_t)/2;
	 double logJointPDF = 
	    0.5 * Math.log(tFinal) - LOG_SQRT2PI -tFinal * betaJoint * betaJoint / 2;
	 logProbAltPoints = logJointPDF + logScaleFactor;
      }
      return logProbNullPoints + logProbAltPoints;
   }

   public void computeHvalues() { // Hvalue is approximated Mvalue.
      if (!isFixedEffectsComputed_) {
	 computeFixedEffects();
      }
      if (!isHvaluesComputed_) {
	 for (int i = 0; i < nStudy_; i++) {
	    MetaSnp metaSnp = new MetaSnp("dummy_rsid");
	    for (int j = 0; j < nStudy_; j++) {
	       if (i != j) {
		  metaSnp.addStudy(betas_.get(j),
				   standardErrors_.get(j));
	       }
	    }
	    double var1 = 
	       Math.pow(standardErrors_.get(i), 2.0) + 
	       Math.pow(metaSnp.getStandardErrorFixedEffects(), 2.0); //
	    double var0 = 
	       Math.pow(standardErrors_.get(i), 2.0);
	    double f1 = 
	       Math.exp(-Math.pow(betas_.get(i) - 
				  metaSnp.getBetaFixedEffects(), 2.0) / //
			(2.0 * var1 ) );
	    double f0 = Math.exp( -Math.pow(betas_.get(i), 2.0) / 
				  (2.0 * var0 ) );
	    double hvalue = f1 / (f0 + f1);
	    hvalues_.add(hvalue);
	 }
	 isHvaluesComputed_ = true;
      }
   }
   
   public void printPvaluesAndHvalues() {
      computeFixedEffects(1.0);
      computeRandomEffects();
      computeHanEskin(1.0, 1.0);
      if (pvalueHanEskinTabulated_ < pvalueFixedEffects_ &&
	  pvalueHanEskinTabulated_ < pvalueRandomEffects_) {

	 System.out.printf("%s ", rsid_);
	 for (int i = 0; i < nStudy_; i++) {
	    System.out.printf("%.5E ", getPvalue(i));
	 }
	 for (int i = 0; i < nStudy_; i++) {
	    System.out.printf("%.5f ", getHvalue(i));
	 }
	 System.out.println();
      }
   }

   public void computeBinaryEffectsStatistic() {
      if (!isHvaluesComputed_) {
	 computeHvalues();
      }
      double [] zs       = new double [nStudy_];
      double [] zWeights = new double [nStudy_];
      for (int i = 0; i < nStudy_; i++) {
	 zs[i]       = betas_.get(i) / standardErrors_.get(i);
	 zWeights[i] =           1.0 / standardErrors_.get(i);
      }
      double sumHvalueZweightZ = 0F;
      double sumHvalueSquareZweightSquare = 0F;
      for (int i = 0; i < nStudy_; i++) {
	 sumHvalueZweightZ += hvalues_.get(i) * zWeights[i] * zs[i];
	 sumHvalueSquareZweightSquare += Math.pow(
    			  hvalues_.get(i) * zWeights[i], 2F);
      }
      statisticBinaryEffects_ = sumHvalueZweightZ / 
	 Math.sqrt(sumHvalueSquareZweightSquare);
      isBinaryEffectsStatisticComputed_ = true;
   }

   public void computeBinaryEffectsPvalue(long numSampling, int seed) {
      if (!isBinaryEffectsStatisticComputed_) {
	 computeBinaryEffectsStatistic();
      }
      Random random = new Random(seed);
      double [] zs       = new double [nStudy_];
      double [] ws       = new double [nStudy_]; // zWeight
      for (int i = 0; i < nStudy_; i++) {
	 zs[i]       = betas_.get(i) / standardErrors_.get(i);
	 ws[i]       =           1.0 / standardErrors_.get(i);
      }
      double z = Math.abs(statisticBinaryEffects_);
      // Code in this method currently follows C convention
      //            (i.e. lots of omissions in variable names! sorry.)
      int i, j, k, m, cnt, tmp;
      double pv, rnd, ratio, tau, lambda, ll, finalz, ci95;
      double p_sample, p_situation, p_original, p_left, p_right;
      double mean, tmpsum, tmpsumsq, sumwssq;
      int n = nStudy_;
      double[] xs          = new double[n+1];
      double[] stds        = new double[n+1];
      double[] pdf_num_alt = new double[n+1];
      double[] cdf_num_alt = new double[n+1];
      double[] samplezs    = new double[n+1];
      double[] samplebeta  = new double[n+1];
      // Uniform probability of each scenario
      for (m=1; m <= n; m++) pdf_num_alt[m] = 1./n;
      cdf_num_alt[1] = pdf_num_alt[1];
      for (m=2; m <= n; m++) cdf_num_alt[m] = cdf_num_alt[m-1] + pdf_num_alt[m];
      // Get the factors of each scenario.
      int[] a = new int[n];
      int x, y, t, done, rep;
      for (i=0;i < n;i++) { a[i] = i; }
      for (m=1; m <= n; m++) {
	 tmpsum = 0.;
	 tmpsumsq = 0.;
	 cnt = 0;
	 for (rep = 0;rep < 1000;rep++) {
	    // permute "a" list
	    for (i = 0;i < n; i++) {
	       j = i + random.nextInt(n - i);
	       tmp = a[i];
	       a[i] = a[j];
	       a[j] = tmp;
	    }
	    sumwssq = 0.;
	    for (i = 0;i < m;i++) {
	       sumwssq += ws[a[i]]*ws[a[i]];
	    }
	    for (i = 0;i < m;i++) {
	       mean = z*ws[a[i]]/Math.sqrt(sumwssq);
	       tmpsum += mean;
	       tmpsumsq += mean*mean;
	       cnt++;
	    }
	 }
	 xs[m] = tmpsum/cnt;
	 stds[m] = Math.sqrt(tmpsumsq/cnt - xs[m]*xs[m] + 1);
      }
      // sample.
      pv = 0.;
      cnt = 0;
      for(long sam = 0;sam < numSampling;sam++) {
	 // First, randomly choose situation (# of alt)
	 rnd = random.nextDouble();
	 for(i=1; i <= n;i++) {
	    if (rnd <= cdf_num_alt[i]) {
	       m = i; // "m" is the situation number.
	       break;
	    }
	 }
	 // Given situation (m), sample n points.
	 for(i=0;i < n;i++) {
	    samplezs[i] = random.nextGaussian();
	 }
	 for(i=0;i < n;i++) {
	    if (random.nextDouble() <= (double)m/n) {
	       samplezs[i] = samplezs[i]*stds[m] + xs[m];
	    }
	    samplebeta[i] = samplezs[i] / ws[i];
	 }
	 // run 
	 MetaSnp metaSnp = new MetaSnp("dummy_rsid");
	 for(i=0;i < n;i++) {
	    metaSnp.addStudy( samplebeta[i], 1.0/ws[i] );
	 }
	 double sampleStatistic = metaSnp.getStatisticBinaryEffects();
	 
	 if (sampleStatistic >= z) { // significant.
	    p_sample = 0.; // pdf of sample distribution
	    for (m=1;m<=n;m++) {
	       p_situation = pdf_num_alt[m];
	       for (k=0;k<n;k++) {
		  p_left = ((double)(n-m)/n)*(1./Math.sqrt(2*Math.PI))*Math.exp(-0.5*samplezs[k]*samplezs[k]);
		  p_right = ((double)m/n)*(1./Math.sqrt(2*Math.PI*stds[m]*stds[m]))*Math.exp(-0.5*(samplezs[k]-xs[m])*(samplezs[k]-xs[m])/(stds[m]*stds[m]));
		  p_situation *= p_left+p_right;
	       }
	       p_sample += p_situation;
	    }
	    p_original = 1.; // pdf of original distribution
	    for (k=0;k < n;k++) p_original *= (1./Math.sqrt(2*Math.PI))*Math.exp(-0.5*samplezs[k]*samplezs[k]);
	    ratio = p_original/p_sample;
	    pv += ratio;
	    cnt++;
	 }
      }
      pv /= (double)numSampling;
      pv *= 2.;
      pvalueBinaryEffects_ = Math.min(pv, 1.0); 
      isBinaryEffectsPvalueComputed_ = true;
   }
   
   protected static String configToString(boolean[] H1) {
      char[] str = new char[H1.length];
      for (int i = 0; i < H1.length; i++) {
	 str[i] = H1[i]?'1':'0';
      }
      return new String(str);
   }

   public void computeHanEskin
      (double lambdaMeanEffect, double lambdaHeterogeneity) {
      if (!isHeterogeneityComputed_) {
	 computeHeterogeneity();
      }
      if (!isPvalueTableRead_) {
	 System.err.println("ERROR: Cannot compute HanEskin method without initializing p-value Table");
	 System.exit(-1);
      }
      double [] betas     = new double [nStudy_];
      double [] variances = new double [nStudy_];
      double [] weights   = new double [nStudy_];
      for (int i = 0; i < nStudy_; i++) {
	 betas[i]         = betas_.get(i);
	 variances[i]     = Math.pow(standardErrors_.get(i), 2.0);
	 weights[i]       = 1.0 / variances[i];
      }
      double sumBetaProdWeight = 0.0;
      double sumWeight         = 0.0;
      double sumWeightSquare   = 0.0;
      for (int i = 0; i < nStudy_; i++) {
	 sumBetaProdWeight    += betas[i]   * weights[i];
	 sumWeight            += weights[i];
	 sumWeightSquare      += weights[i] * weights[i];
      }
      // Iteratively find maximum likelihood parameters
      double previousMLBeta;
      double previousMLTauSquare;
      double previousLogLikelihood;
      double fixedEffectsMLBeta     = sumBetaProdWeight / sumWeight;
      double nextMLBeta             = fixedEffectsMLBeta;  // starting point
      double nextMLTauSquare        = statisticTauSquare_; // starting point
      double nextLogLikelihood      = Double.NEGATIVE_INFINITY;
      double changeRatioMLBeta      = Double.POSITIVE_INFINITY;
      double changeRatioMLTauSquare = Double.POSITIVE_INFINITY;
      double changeLogLikelihood    = Double.POSITIVE_INFINITY;
      double sumNumerator;
      double sumDenominator;
      while (changeRatioMLBeta      > ML_ESTIMATE_CHANGE_RATIO_THRESHOLD ||
	     changeRatioMLTauSquare > ML_ESTIMATE_CHANGE_RATIO_THRESHOLD) {
	 previousMLBeta      = nextMLBeta;
	 previousMLTauSquare = nextMLTauSquare;
	 previousLogLikelihood = nextLogLikelihood;
	 sumNumerator        = 0.0;
	 sumDenominator      = 0.0;
	 for (int i = 0; i < nStudy_; i++) {
	    sumNumerator    += betas[i] / (variances[i] + previousMLTauSquare);
	    sumDenominator  +=      1.0 / (variances[i] + previousMLTauSquare);
	 }
	 nextMLBeta          = sumNumerator / sumDenominator;
	 sumNumerator        = 0.0;
	 sumDenominator      = 0.0;
	 for (int i = 0; i < nStudy_; i++) {
	    sumNumerator    += (Math.pow(betas[i] - nextMLBeta, 2.0) - 
			     variances[i]) /
	                     Math.pow(variances[i] + previousMLTauSquare, 2.0);
	    sumDenominator  += 1.0 / 
	                     Math.pow(variances[i] + previousMLTauSquare, 2.0);
	 }
	 nextMLTauSquare     = Math.max(sumNumerator / sumDenominator, 0.0);
	 double ll = 0.0;
	 for (int i = 0; i < nStudy_; i++) {
	    ll += 0.5 * Math.log(2*Math.PI*(variances[i]+nextMLTauSquare)) -
	       Math.pow(betas[i] - nextMLBeta, 2)/(2*(variances[i]+nextMLTauSquare));
	 }
	 nextLogLikelihood = ll;
	 changeLogLikelihood = nextLogLikelihood - previousLogLikelihood;
	 changeRatioMLBeta   = Math.abs((nextMLBeta - previousMLBeta) / 
					 previousMLBeta);
	 changeRatioMLTauSquare = Math.abs((nextMLTauSquare - 
					     previousMLTauSquare) / 
					    previousMLTauSquare);
	 if (changeLogLikelihood < 0.0) { // If somehow likelihood decreases,
	    nextMLBeta = previousMLBeta;  // step back and finish.
	    nextMLTauSquare = previousMLTauSquare;
	    break;
	 }
      }
      double MLBeta          = nextMLBeta;
      double MLTauSquare     = nextMLTauSquare;
      // Compute statistics based on ML parameters
      double sumFormula1 = 0.0;
      double sumFormula2 = 0.0;
      double sumFormula3 = 0.0;
      double sumFormula4 = 0.0;
      for (int i = 0; i < nStudy_; i++) {
	 sumFormula1 += Math.log(variances[i] / (variances[i] + MLTauSquare));
	 sumFormula2 += betas[i] * betas[i] / variances[i];
	 sumFormula3 += Math.pow(betas[i] - fixedEffectsMLBeta, 2.0) / variances[i];
	 sumFormula4 += Math.pow(betas[i] - MLBeta, 2.0) /
	                (variances[i] + MLTauSquare);

      }
      statisticHanEskinMeanEffectPart_    = sumFormula2 - sumFormula3;
      statisticHanEskinHeterogeneityPart_ = Math.max( sumFormula1 + 
						      sumFormula3 - sumFormula4,
						      0.0 );
      // Genomic-control
      statisticHanEskinMeanEffectPart_    /= lambdaMeanEffect;
      statisticHanEskinHeterogeneityPart_ /= lambdaHeterogeneity;
      statisticHanEskin_ = statisticHanEskinMeanEffectPart_ +
	                   statisticHanEskinHeterogeneityPart_;
      // Compute asymptotic p-value
      pvalueHanEskinAsymptotic_ = 
	 0.5 * Probability.chiSquareComplemented(1.0, statisticHanEskin_) + 
	 0.5 * Probability.chiSquareComplemented(2.0, statisticHanEskin_);
      // Use table to calculate accurate p-value
      if (nStudy_ <= TABLE_MAX_NSTUDY) {
	 int nearestIndexBottom = Math.max(0, 
					   (int)Math.floor(statisticHanEskin_ * 10.0));
	 int nearestIndexTop    = Math.max(0,
					   (int)Math.ceil (statisticHanEskin_ * 10.0));
	 if (nearestIndexTop < TABLE_NCOLUMN) {
	    int    rowNumber                     = nStudy_ - 2;
	    double tablePvalueAtIndexBottom = 0.0;;
	    try {
	       tablePvalueAtIndexBottom      = pvalueTable_[rowNumber][nearestIndexBottom];
	    } 
	    catch (Exception e) {
	       System.err.printf("%f %f %f %f\n",
				 statisticHanEskinMeanEffectPart_,
				 statisticHanEskinHeterogeneityPart_,
				 nearestIndexBottom,
				 nearestIndexTop);
	       System.exit(-1);
	    }
	    double asymptoticPvalueAtIndexBottom = 
	       0.5 * Probability.chiSquareComplemented(1.0, nearestIndexBottom/10.0) +
	       0.5 * Probability.chiSquareComplemented(2.0, nearestIndexBottom/10.0);
	    double ratioAtIndexBottom            = tablePvalueAtIndexBottom / 
	                                           asymptoticPvalueAtIndexBottom;
	    double tablePvalueAtIndexTop         = pvalueTable_[rowNumber][nearestIndexTop];
	    double asymptoticPvalueAtIndexTop    = 
	       0.5 * Probability.chiSquareComplemented(1.0, nearestIndexTop/10.0) +
	       0.5 * Probability.chiSquareComplemented(2.0, nearestIndexTop/10.0);
	    double ratioAtIndexTop               = tablePvalueAtIndexTop / 
	                                           asymptoticPvalueAtIndexTop;
	    double ratioInterpolated             = 
	               ratioAtIndexBottom + (ratioAtIndexTop - ratioAtIndexBottom) *
	               (statisticHanEskin_ - nearestIndexBottom / 10.0) / 0.1;
	    pvalueHanEskinTabulated_             = ratioInterpolated * pvalueHanEskinAsymptotic_;
	 } else {
	    int    rowNumber                = nStudy_ - 2;
	    double tablePvalueAtTheEnd      = pvalueTable_[rowNumber][TABLE_NCOLUMN-1];
	    double asymptoticPvalueAtTheEnd = 
	       0.5 * Probability.chiSquareComplemented(1.0, TABLE_MAX_THRESHOLD) +
	       0.5 * Probability.chiSquareComplemented(2.0, TABLE_MAX_THRESHOLD);
	    double ratioAtTheEnd            = tablePvalueAtTheEnd / 
	                                      asymptoticPvalueAtTheEnd;
	    pvalueHanEskinTabulated_        = ratioAtTheEnd * pvalueHanEskinAsymptotic_;
	 }
      } else {
	 pvalueHanEskinTabulated_           = pvalueHanEskinAsymptotic_;
      }
      isHanEskinComputed_ = true;
   }
   
   public void computeHanEskin() {
      computeHanEskin( 1.0, 1.0 );
   }

   //***** get methods ********//
   public int getNStudy() {
      return nStudy_;
   }
   
   public double getHvalue(int i) {
      if (!isHvaluesComputed_) {
	 computeHvalues();
      }
      return hvalues_.get(i);
   }

   public double getMvalue(int i) {
      if (!isMvaluesComputed_) {
// 	 computeMvalues();
	 System.err.println("First compute m-value before calling");
	 System.exit(-1);
      }
      return mvalues_.get(i);
   }

   public double getPvalue(int i) {
      return Probability.chiSquareComplemented(1.0, Math.pow(
		 betas_.get(i)/standardErrors_.get(i),2.0));
   }
   
   public double getPvalueFixedEffects() {
      if (!isFixedEffectsComputed_) {
	 computeFixedEffects();
      }
      return pvalueFixedEffects_;
   }
   
   public double getBetaFixedEffects() {
      if (!isFixedEffectsComputed_) {
	 computeFixedEffects();
      }
      return betaFixedEffects_;
   }
   
   public double getStandardErrorFixedEffects() {
      if (!isFixedEffectsComputed_) {
	 computeFixedEffects();
      }
      return standardErrorFixedEffects_; 
   }
   
   public double getPvalueRandomEffects() {
      if (!isRandomEffectsComputed_) {
	 computeRandomEffects();
      }
      return pvalueRandomEffects_;
   }
   
   public double getBetaRandomEffects() {
      if (!isRandomEffectsComputed_) {
	 computeRandomEffects();
      }
      return betaRandomEffects_;
   }
   
   public double getStandardErrorRandomEffects() {
      if (!isRandomEffectsComputed_) {
	 computeRandomEffects();
      }
      return standardErrorRandomEffects_; 
   }
   
   public double getPvalueHanEskin() {
      if (!isHanEskinComputed_) {
	 computeHanEskin();
      }
      return pvalueHanEskinTabulated_;
   }

   public double getStatisticHanEskinMeanEffectPart() {
      if (!isHanEskinComputed_) {
	 computeHanEskin();
      }
      return statisticHanEskinMeanEffectPart_;
   }

   public double getStatisticHanEskinHeterogeneityPart() {
      if (!isHanEskinComputed_) {
	 computeHanEskin();
      }
      return statisticHanEskinHeterogeneityPart_;
   }

   public double getStatisticBinaryEffects() {
      if (!isBinaryEffectsStatisticComputed_) {
	 computeBinaryEffectsStatistic();
      }
      return statisticBinaryEffects_;
   }
   
   public double getPvalueBinaryEffects() {
      if (!isBinaryEffectsPvalueComputed_) {
	 System.err.println("First compute binary effects p-value before calling");
	 System.exit(-1);
      }
      return pvalueBinaryEffects_;
   }
   
   //***** print methods ********//
   public static void printHeadings(PrintWriter printWriter) {
      printWriter.printf("RSID\t"+
			 "#STUDY\t"+
			 "PVALUE_FE\t"+
			 "BETA_FE\t"+
			 "STD_FE\t"+
			 "PVALUE_RE\t"+
			 "BETA_RE\t"+
			 "STD_RE\t"+
			 "PVALUE_RE2\t"+
			 "STAT1_RE2\t"+
			 "STAT2_RE2\t"+
			 "PVALUE_BE\t"+
			 "I_SQUARE\t"+
			 "Q\t"+
			 "PVALUE_Q\t"+
			 "TAU_SQUARE\t"+
			 "PVALUES_OF_STUDIES(Tab_delimitered)\t"+
			 "MVALUES_OF_STUDIES(Tab_delimitered)\n"
			 );
   }

   public void printResults(PrintWriter printWriter) {
      printWriter.printf("%s\t", rsid_);
      printWriter.printf("%d\t", nStudy_);
      if (isFixedEffectsComputed_) {
	 printWriter.printf("%G\t", pvalueFixedEffects_);
	 printWriter.printf("%G\t", betaFixedEffects_);
	 printWriter.printf("%G\t", standardErrorFixedEffects_);
	 printWriter.printf("%G\t", pvalueRandomEffects_);
	 printWriter.printf("%G\t", betaRandomEffects_);
	 printWriter.printf("%G\t", standardErrorRandomEffects_);
	 printWriter.printf("%G\t", pvalueHanEskinTabulated_);
	 printWriter.printf("%G\t", statisticHanEskinMeanEffectPart_);
	 printWriter.printf("%G\t", statisticHanEskinHeterogeneityPart_);
	 if (isBinaryEffectsPvalueComputed_) {
	    printWriter.printf("%G\t", pvalueBinaryEffects_);
	 } else {
	    printWriter.printf("NA\t");
	 }	 
	 printWriter.printf("%G\t", statisticISquare_);
	 printWriter.printf("%G\t", statisticQ_);
	 printWriter.printf("%G\t", pvalueQ_);
	 printWriter.printf("%G\t", statisticTauSquare_);
      } else { // if not, it must be a problematic SNPs with nStudy < 2; just print NA
	 for (int i = 0; i < 14; i++) printWriter.printf("NA\t");
      }
      int j;
      j = 0;
      for (int i = 0;i < nStudyIncludingNa_; i++) {
	 if (isNa_.get(i)) {
	    printWriter.printf("NA\t");
	 } else {
	    printWriter.printf("%G\t", getPvalue(j));
	    ++j;
	 }
      }
      j = 0;
      for (int i = 0;i < nStudyIncludingNa_; i++) {
	 if (isNa_.get(i)) {
	    printWriter.printf("NA\t");
	 } else {
		 
	    if (isMvaluesComputed_) {
	       printWriter.printf("%.3f\t", getMvalue(j));
	    } else {
	       printWriter.printf("NA\t");
	    }
	    ++j;
	 }
      }
      printWriter.printf("\n");
   }

   // Class-variable part for
   // P-value Table of Han Eskin Statistic.
   // Currently, table dimension is fixed as follows.
   // nStudy (rows) from 2 to 50 (toal 49 rows)
   // statistic thresholds (columns) from 0.0 to 33.0 (total 331 columns)
   private static final int TABLE_NROW = 49;
   private static final int TABLE_MAX_NSTUDY = 50;
   private static final int TABLE_NCOLUMN = 331;
   private static final double TABLE_MAX_THRESHOLD = 33.0;
   private static final double[][] pvalueTable_ = new double [TABLE_NROW][TABLE_NCOLUMN];
   private static boolean isPvalueTableRead_;

   public static void readPvalueTableFile(String pvalueTableFile) {
      BufferedReader bufferedReader = null;
      try {
	 bufferedReader = new BufferedReader(new FileReader(pvalueTableFile));
      }
      catch (FileNotFoundException exception) {
	 System.err.println("ERROR: P-value Table file not found");
	 System.exit(-1);
      }
      catch (Exception exception) {
	 System.err.println("ERROR: P-value Table cannot be opened");
	 System.exit(-1);
      }
      try {
	 String readLine;
	 readLine = bufferedReader.readLine(); //ignore top row.
	 for (int i = 0;i < TABLE_NROW; i++) {
	    readLine = bufferedReader.readLine();
	    if (readLine == null) {
	       System.err.println("ERROR: Reading error from P-value Table file");
	       System.exit(-1);
	    }
	    String[] tokens = readLine.split("\\s+");
	    if (tokens.length < TABLE_NCOLUMN + 1) { // +1 considering leftmost column
	       System.err.println("ERROR: P-value Table File has too few columns");
	       System.exit(-1);
	    }
	    for (int j = 0;j < TABLE_NCOLUMN;j++) {
	       try {
		  pvalueTable_[i][j] = new Double(tokens[j + 1]); // +1 for ignoring leftmost column
	       }
	       catch (Exception exception) {
		  System.err.println("Incorrect float value in Pvalue Table file.");
		  System.exit(-1);
	       }
	    }
	 }
      }
      catch (IOException exception) {
	 System.err.println("ERROR: error encountered while reading Pvalue Table file");
	 System.exit(-1);
      }
      isPvalueTableRead_ = true;
   }

} // end Class MetaSnp.