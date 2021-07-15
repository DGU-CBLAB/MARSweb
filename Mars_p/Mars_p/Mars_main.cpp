#include <iostream>
#include "CBLAB_Taegun.h"

bool cmp_str_double(std::pair<std::string, double> left, std::pair<std::string, double> right);
bool cmp_int_double(std::pair<int, double> left, std::pair<int, double> right);
double dmvnorm_(const Eigen::VectorXd& x, const Eigen::VectorXd& meanVec, const Eigen::MatrixXd& covMat);
Eigen::MatrixXd rmvnorm_(int simulation_Number, Eigen::VectorXd mean, Eigen::MatrixXd covar);
std::vector<std::vector<std::pair<int, double>>> basic_sampling(int simNum, int topNum, Eigen::MatrixXd Geno);

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

/*
  Draw nn samples from a size-dimensional normal distribution
  with a specified mean and covariance
*/

int main() {

	int select_num = 50;

	std::ifstream geno("ENSG00000173862.3_GENO");
	std::ifstream stat("ENSG00000173862.3_STAT");
	std::ofstream ldout("cppld50.txt");
	std::ofstream read_matout("read_matGS.txt");

	std::vector<std::pair<std::string, double>> GS;

	double stemp = 0.0;
	std::string gtemp = "";
	
	while (stat >> stemp) {
		getline(geno, gtemp);
		GS.push_back(std::pair<std::string, double>(gtemp, stemp));
	}

	std::sort(GS.begin(), GS.end(), cmp_str_double);

	geno.close();
	stat.close();

	Eigen::MatrixXd read_GS = read_mat(GS, 50, 1);
	Eigen::MatrixXd read_GS_all = read_mat(GS, GS.size(), 1);

	Eigen::MatrixXd cal_LD = cal_cor(read_GS);
	
	ldout << cal_LD << std::endl;

	SelfAdjointEigenSolver<MatrixXd> solver(0.5 * (cal_LD + cal_LD.transpose()));
	MatrixXd Z = solver.eigenvectors() * solver.eigenvalues().cwiseMax(0).asDiagonal() * solver.eigenvectors().transpose();

	//std::cout << Z << std::endl;
	
	ldout.close();
	read_matout.close();

	std::vector<std::vector<std::pair<int, double>>> BS = basic_sampling(1000, 50, read_GS_all);

	for (int i = 0; i < BS[0].size(); i++) {
		std::cout << BS[0][i].first << " " << BS[0][i].second << std::endl;
	}
	return 0;

}


bool cmp_str_double(std::pair<std::string, double> left, std::pair<std::string, double> right) {
	return std::abs(left.second) > std::abs(right.second);
}
bool cmp_int_double(std::pair<int, double> left, std::pair<int, double> right) {
	return std::abs(left.second) > std::abs(right.second);
}
Eigen::MatrixXd rmvnorm_(int simulation_Number, Eigen::VectorXd mean, Eigen::MatrixXd covar) {

	int size = mean.size(); // Dimensionality (rows)
	int nn = simulation_Number;     // How many samples (columns) to draw

	std::random_device rd_rng;
	Eigen::internal::scalar_normal_dist_op<double> randN; // Gaussian functor
	Eigen::internal::scalar_normal_dist_op<double>::rng.seed(rd_rng()); // Seed the rng

	// Define mean and covariance of the distribution

	Eigen::MatrixXd normTransform(size, size);

	Eigen::LLT<Eigen::MatrixXd> cholSolver(covar);

	// We can only use the cholesky decomposition if 
	// the covariance matrix is symmetric, pos-definite.
	// But a covariance matrix might be pos-semi-definite.
	// In that case, we'll go to an EigenSolver
	if (cholSolver.info() == Eigen::Success) {
		// Use cholesky solver
		normTransform = cholSolver.matrixL();
	}
	else {
		// Use eigen solver
		Eigen::SelfAdjointEigenSolver<Eigen::MatrixXd> eigenSolver(covar);
		normTransform = eigenSolver.eigenvectors()
			* eigenSolver.eigenvalues().cwiseSqrt().asDiagonal();
	}

	Eigen::MatrixXd samples = (normTransform
		* Eigen::MatrixXd::NullaryExpr(size, nn, randN)).colwise()
		+ mean;


	return samples;
}

double dmvnorm_(const Eigen::VectorXd& x, const Eigen::VectorXd& meanVec, const Eigen::MatrixXd& covMat) //한개씩 계산댐.
{
	// avoid magic numbers in your code. Compilers will be able to compute this at compile time:
	const double logSqrt2Pi = 0.5 * std::log(2 * M_PI);
	typedef Eigen::LLT<Eigen::MatrixXd> Chol;
	Chol chol(covMat);
	// Handle non positive definite covariance somehow:
	if (chol.info() != Eigen::Success) throw "decomposition failed!";
	const Chol::Traits::MatrixL& L = chol.matrixL();
	double quadform = (L.solve(x - meanVec)).squaredNorm();
	return std::exp(-x.rows() * logSqrt2Pi - 0.5 * quadform) / L.determinant();
}

std::vector<std::vector<std::pair<int, double>>> basic_sampling(int simNum, int topNum, Eigen::MatrixXd Geno) { // Geno row x col row is indi, col is snp count
	int Geno_cols = Geno.cols();
	int Geno_rows = Geno.rows();

	std::cout << "step1" << std::endl;
	Eigen::MatrixXd xs_scale = Geno.colwise() - Geno.rowwise().mean();
	Eigen::VectorXd null_mean(Geno_cols); null_mean.setZero();
	Eigen::MatrixXd null_covar(Geno_cols, Geno_cols); null_covar.setIdentity();

	std::cout << "step2" << std::endl;
	Eigen::MatrixXd Sall = rmvnorm_(simNum, null_mean, null_covar); // Geno_cols x simNum
	Eigen::MatrixXd Sall_new = (xs_scale / std::sqrt(Geno_cols)) * Sall;

	std::cout << "step3" << std::endl;
	std::vector< std::vector<std::pair<int, double>>> BS;
	for (int i = 0; i < Geno_rows; i++) {
		std::cout << "i =" << i << std::endl;
		std::vector<std::pair<int, double>> BS_element;
		for (int j = 0; j < simNum; j++) {
			BS_element.push_back(std::pair<int, double>(j, Sall_new(i, j)));
		}
		std::sort(BS_element.begin(), BS_element.end(), cmp_int_double);
		BS_element.resize(topNum); BS.push_back(BS_element);
	}

	return BS;
}