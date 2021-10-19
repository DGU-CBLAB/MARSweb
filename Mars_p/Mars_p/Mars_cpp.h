#pragma once

#define _USE_MATH_DEFINES

#include <iostream>
#include <fstream>
#include <cmath>
#include <vector>
#include <map>
#include <random>

#include "Eigen/Eigen/QR"
#include "Eigen/Eigen/Core"
#include "Eigen/Eigen/Dense"
#include "Eigen/Eigen/LU"
#include "Eigen/Eigen/Eigenvalues"
#include "Eigen/unsupported/Eigen/MatrixFunctions"

#include <boost/math/distributions/students_t.hpp>
#include <boost/math/special_functions/erf.hpp>
#include <boost/math/distributions/inverse_gaussian.hpp>
#include <boost/math/special_functions/beta.hpp>
#include <boost/random/mersenne_twister.hpp>
#include <boost/random/normal_distribution.hpp>    

static std::mt19937 ran;
using namespace Eigen;

namespace Eigen {
	namespace internal {
		template<typename Scalar>
		struct scalar_normal_dist_op
		{
			static boost::mt19937 rng;    // The uniform pseudo-random algorithm
			mutable boost::normal_distribution<Scalar> norm;  // The gaussian combinator

			EIGEN_EMPTY_STRUCT_CTOR(scalar_normal_dist_op)

				template<typename Index>
			inline const Scalar operator() (Index, Index = 0) const { return norm(rng); }
		};

		template<typename Scalar> boost::mt19937 scalar_normal_dist_op<Scalar>::rng;

		template<typename Scalar>
		struct functor_traits<scalar_normal_dist_op<Scalar> >
		{
			enum { Cost = 50 * NumTraits<Scalar>::MulCost, PacketAccess = false, IsRepeatable = false };
		};
	} // end namespace internal
} // end namespace Eigen

class Mars_cpp {
private:
	int mode;
	int subsize;
	int maxCausalSNP;
	int snpCount;
	int simNum;
	int LRT_count;
	int pvalue_count;

	double* postValues;
	double* histValues;
	double* stat;

	double LRTscore;
	double pvalue;
	double NCP;
	double gamma;
	double alt_LRTscore;
	double alt_pvalue;

	std::vector<std::pair<std::string, double>> GS;

	Eigen::MatrixXd sigmaMatrix;
	Eigen::MatrixXd Geno_LD;
	Eigen::MatrixXd Geno_part;
	Eigen::MatrixXd Geno_all;

public:

	std::vector<std::vector<std::pair<int, double>>> BS;
	std::vector<std::pair<double, std::vector<std::pair<int, double>>>> WBS;


	Mars_cpp(std::string Geno, std::string Stat, int simNum_, double NCP_, double gamma_, int subsize,int maxCausal_SNP,int mode, double UNI_threshold);

	int nextBinary(int* data, int size);
	int count_string_col(std::string str);

	double computePvalue(double max_stat);
	double normaICDF(double value);
	double fastLikelihood(int* configure, double* stat);
	double fracdmvnorm(Eigen::MatrixXd Z, Eigen::MatrixXd mean, Eigen::MatrixXd R, Eigen::MatrixXd diagC);
	double dmvnorm_(const Eigen::VectorXd& x, const Eigen::VectorXd& meanVec, const Eigen::MatrixXd& covMat);

	long int fact(int n);
	long int nCr(int n, int r);

	Eigen::MatrixXd cal_cor(Eigen::MatrixXd& mat); //makesubSigma
	Eigen::MatrixXd generateLD(Eigen::MatrixXd& mat);
	Eigen::MatrixXd read_mat(std::vector<std::pair<std::string, double>> X, int row, int start_number);
	Eigen::MatrixXd rmvnorm_(int simulation_Number, Eigen::VectorXd mean, Eigen::MatrixXd covar);

	std::vector<std::vector<std::pair<int, double>>> basic_sampling(int simNum, int topNum, Eigen::MatrixXd Geno);
	std::vector<std::pair<double, std::vector<std::pair<int, double>>>> importance_sampling(int simNum, int topNum, Eigen::MatrixXd Geno);

	//void makeSubSigma(double* snp, double* sigma, int subSize, int sampleSize);
	void makeSigmaPositiveSemiDefinite(Eigen::MatrixXd& mat);
	void read_input_file(std::string Geno, std::string Stat);
	void computeLRT(double* stat);
	void run();

	~Mars_cpp() {
		delete[] postValues;
		delete[] histValues;
		delete[] stat;
	}
};

bool cmp_str_double(std::pair<std::string, double> left, std::pair<std::string, double> right);
bool cmp_int_double(std::pair<int, double> left, std::pair<int, double> right);

