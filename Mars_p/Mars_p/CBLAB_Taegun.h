#pragma once


#define log_sqrt2pi 0.5 * std::log(2 * std::atan(1) * 4)
#define _USE_MATH_DEFINES

#include "Eigen/Eigen/QR"
#include "Eigen/Eigen/Core"
#include "Eigen/Eigen/Dense"
#include "Eigen/Eigen/LU"
#include "Eigen/Eigen/Eigenvalues"
#include "Eigen/unsupported/Eigen/MatrixFunctions"

#include <ctime>
#include <iostream>
#include <fstream>
#include <future>
#include <string>
#include <vector>
#include <sstream>
#include <map> 
#include <tuple> // 3pair data


#define _FILE_OFFSET_BITS  64 // large file support

#include <cmath>
#include <ctime>
#include <cstdio>
#include <cstdlib>
#include <cstring>

#include <numeric>
#include <algorithm>
#include <random>
#include <thread>
#include <boost/math/distributions/students_t.hpp>
#include <boost/math/special_functions/erf.hpp>
#include <boost/math/distributions/inverse_gaussian.hpp>
#include <boost/math/special_functions/beta.hpp>
#include <boost/random/mersenne_twister.hpp>
#include <boost/random/normal_distribution.hpp>    


#define MAXNSEC 1000 // maximum number of sections for scaling factor
#define EIGEN_EPS 1E-10 // epsilon to add to the matrix diagonal
#define DBL_EPS_HARD 1E-20 // hard epsilon
#define DBL_EPS_SOFT 1E-10 // soft epsilon
#define P_THRES .05 // starting point of scaling factor computation
#define BONFFACTOR 10 // conservative estimate of bonferroni factor
#define DECREASE_RATIO .1 // control between-sections of scaling factor part
#define DOSE_EFF .5 // Armitage test
#define ONEWRITE 1000
#define DBL_CHUNK 1000
#define EPS 1E-100
#define min(a,b) (((a)<(b)) ? (a) : (b))
#define max(a,b) (((a)>(b)) ? (a) : (b))

static std::mt19937 ran;
using namespace Eigen;


struct eigenrot {
    Eigen::VectorXd Kva;
    Eigen::MatrixXd Kve;
    Eigen::MatrixXd y;
    Eigen::MatrixXd X;
};

struct lmm_fit {
    double hsq;
    Eigen::VectorXd beta;
    double sigmasq;
    double loglik;
    double rss;
    double logdetXSX;
};

struct calcLL_args {
    Eigen::VectorXd Kva;
    Eigen::VectorXd y;
    Eigen::MatrixXd X;
    bool reml;
    double logdetXpX;
};

// calc X'X
MatrixXd calc_xpx(const MatrixXd& X);

// eigen decomposition
//    returns eigenvalues and transposed eigenvectors
std::pair<VectorXd, MatrixXd> eigen_decomp(const MatrixXd& A);

// eigen + rotation
// perform eigen decomposition of kinship matrix
// and rotate phenotype and covariate matrices by transpose of eigenvectors
struct eigenrot eigen_rotation(const MatrixXd& K, const MatrixXd& y, const MatrixXd& X, const std::pair<VectorXd, MatrixXd>& e);
// getMLsoln
// for fixed value of hsq, calculate MLEs of beta and sigmasq
// sigmasq = total variance = sig^2_g + sig^2_e
//
// hsq   = heritability
// Kva   = eigenvalues of kinship matrix
// y     = rotated vector of phenotypes
// X     = rotated matrix of covariates
// reml  = whether you'll be using REML (so need to calculate log det XSX)
struct lmm_fit getMLsoln(const double hsq, const VectorXd& Kva, const VectorXd& y,
    const MatrixXd& X, const bool reml);

// calcLL
// calculate log likelihood for fixed value of hsq
// sigmasq = total variance = sig^2_g + sig^2_e
//
// hsq   = heritability
// Kva   = eigenvalues of kinship matrix
// y     = rotated vector of phenotypes
// X     = rotated matrix of covariates
// reml  = boolean indicating whether to use REML (vs ML)
// logdetXpX = log det X'X; if NA, it's calculated
struct lmm_fit calcLL(const double hsq, const VectorXd& Kva, const VectorXd& y,
    const MatrixXd& X, const bool reml, const double logdetXpX);

// just the negative log likelihood, for the optimization
double negLL(const double x, struct calcLL_args* args);

// fitLMM
// Optimize log liklihood over hsq
//
// Kva   = eigenvalues of kinship matrix
// y     = rotated vector of phenotypes
// X     = rotated matrix of covariates
// reml  = boolean indicating whether to use REML (vs ML)
// check_boundary = if true, explicity check 0.0 and 1.0 boundaries
// logdetXpX = log det X'X; if NA, it's calculated
// tol   = tolerance for convergence
struct lmm_fit fitLMM(const VectorXd& Kva, const VectorXd& y, const MatrixXd& X,
    const bool reml, const bool check_boundary,
    const double tol);

// 1-d optimization by Brent's method
double qtl2_Brent_fmin(double ax, double bx, double (*f)(double, void*),
    void* info, double tol);


class REMLE {
public:
    double REML; double delta; double ve; double vg;
    REMLE() {
        REML = 0.0; delta = 0.0; ve = 0.0; vg = 0.0;
    }
};

class input_MS {
public:
    double cal_estimate(input_MS* pheno);
    double cal_RSE(input_MS* pheno);

    std::vector<double> element_vec;
    std::vector<double> residual_vec;

    double mean;
    double sum;
    double squared_residual_sum;
    // y = ax + b
    double slope; // a
    double intercept; // b
    int count;

    input_MS();
    input_MS(std::string arr, int check);
    input_MS(Eigen::MatrixXd& Y, int row_idx, int col);
    input_MS(double** Y, int row_idx, int col);

};
class Metasoft {

};
class MetaSnp {
public:
    std::vector<double>* betas_; //later delete plz
    std::vector<double>* standardErrors_; //later delete plz

    MetaSnp();
    MetaSnp(int snp_count);
};


void emma_REMLE(Eigen::ArrayXd& y, Eigen::MatrixXd& x, Eigen::MatrixXd& K, Eigen::SelfAdjointEigenSolver<Eigen::MatrixXd>& eig_R, REMLE& REM, int indi);

int count_matrix_col(std::ifstream& matrix);
int count_matrix_row(std::ifstream& matrix);
int count_string_col(std::string str);

void emma_eigen_L_wo_Z(Eigen::MatrixXd& Kinship, Eigen::SelfAdjointEigenSolver<Eigen::MatrixXd>& eig_L);
void emma_eigen_R_wo_Z(Eigen::MatrixXd& Kinship, Eigen::MatrixXd& X, Eigen::SelfAdjointEigenSolver<Eigen::MatrixXd>& eig_R, int indi);
double emma_delta_REML_dLL_wo_Z(double logdelta, Eigen::Map<Eigen::ArrayXd>& eig_value, Eigen::Map<Eigen::ArrayXd>& etas, Eigen::ArrayXd& etasq);
double emma_delta_REML_LL_wo_Z(double logdelta, Eigen::Map<Eigen::ArrayXd>& eig_value, Eigen::Map<Eigen::ArrayXd>& etas);
double uniroot_emma_delta_REML_dLL_wo_Z(double a, double b, Eigen::Map<Eigen::ArrayXd>& eig_value, Eigen::Map<Eigen::ArrayXd>& etas, Eigen::ArrayXd& etasq);
long double p_value(boost::math::students_t_distribution<double> t_dist, long double x);
void emma(Eigen::MatrixXd& X, Eigen::MatrixXd& Y, Eigen::MatrixXd& K, std::ofstream& out);
Eigen::ArrayXd emma2(Eigen::MatrixXd& X, Eigen::MatrixXd& Y, Eigen::MatrixXd& K, Eigen::ArrayXd& P_val);

double zscore(double x, double t_value);
long double p_value(boost::math::students_t_distribution<double> t_dist, long double x);
long double t_value(boost::math::students_t_distribution<double> t_dist, long double x);

void NICE_CPP(std::ifstream& snp, Eigen::MatrixXd& Y, std::string output, double prior_val, double priorAlpha, double priorBeta);
void NICE_CPP2(std::ifstream& snp, Eigen::MatrixXd& Y, std::string output, double prior_val, double priorAlpha, double priorBeta, int thread_num);
std::vector<std::string> Gamma_cpp(Eigen::MatrixXd& Kx, Eigen::MatrixXd& Ky, int thread_index, int thread_num);

void computeMvaluesMCMC(double* betas, double* std_, double* std_tm, double* std_tmm, double* std_logt, double* logProbNullPoints_, int sample, int pheno_num, std::string X, Eigen::MatrixXd& Y, Eigen::ArrayXd& P_val, std::ofstream& NICE, double prior_val, double priorAlpha, double priorBeta, int seed = 0);
Eigen::ArrayXd computeMvaluesMCMC2(double* betas, double* std_, double* std_tm, double* std_tmm, double* std_logt, double* logProbNullPoints_, int sample, int pheno_num, std::string X, Eigen::MatrixXd& Y, Eigen::ArrayXd& P_val, double prior_val, double priorAlpha, double priorBeta, int seed = 0);

double observationLogLikelihood(double* betas, double* std_,double* std_tm, double* std_tmm, double* std_logt, double* logProbNullPoints_, int* H1, int numH1, int n_size, double prior_val);

double makeRandomDouble();
double cal_median(std::vector<double> col);


int makeRandomInteger(int number);

Eigen::MatrixXd read_mat(std::ifstream& input_file, int row, int col);
Eigen::MatrixXd read_mat(std::string X, int col);
Eigen::MatrixXd read_mat(std::vector<std::pair<std::string, double>> X, int row, int start_number = 0);
Eigen::MatrixXd cov(Eigen::MatrixXd& mat);
Eigen::MatrixXd normMe(Eigen::MatrixXd& mat);
Eigen::MatrixXd cbind(Eigen::MatrixXd& a, Eigen::MatrixXd& b);

double** read_mat_darray(std::ifstream& input_file, int row, int col);

std::vector<Eigen::ArrayXd> thread_func(std::vector<std::string> temp, int y_row,int y_col, Eigen::MatrixXd& Y,
    double prior_val, double priorAlpha, double priorBeta, int seed =0);

Eigen::MatrixXd estimateKinship(Eigen::MatrixXd snp);
void estimateVarComp(Eigen::MatrixXd& kinship, Eigen::MatrixXd& snp, Eigen::MatrixXd& phenotype, std::vector<double>& vc_1, std::vector<double>& vc_2);
void rotate_X_SigmaM(Eigen::MatrixXd& kinship, Eigen::MatrixXd& snp, double Vg, double Ve);
void cal_SigmaM(Eigen::MatrixXd& kinship, double Vg, double Ve);
Eigen::MatrixXd cal_cor(Eigen::MatrixXd& mat);