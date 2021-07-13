#include <iostream>
#include "CBLAB_Taegun.h"

bool cmp(std::pair<std::string, double> left, std::pair<std::string, double> right);

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

	//int select_num = 50;

	//std::ifstream geno("ENSG00000173862.3_GENO");
	//std::ifstream stat("ENSG00000173862.3_STAT");
	//std::ofstream ldout("cppld50.txt");
	//std::ofstream read_matout("read_matGS.txt");

	//std::vector<std::pair<std::string, double>> GS;
	//std::map<int, double>::iterator iter;

	//double stemp = 0.0;
	//std::string gtemp = "";
	//
	//while (stat >> stemp) {
	//	getline(geno, gtemp);
	//	GS.push_back(std::pair<std::string, double>(gtemp, stemp));
	//}

	//std::sort(GS.begin(), GS.end(), cmp);

	//geno.close();
	//stat.close();

	//Eigen::MatrixXd read_GS = read_mat(GS, 50, 1);
	//Eigen::MatrixXd cal_LD = cal_cor(read_GS);
	//
	//ldout << cal_LD << std::endl;

	//SelfAdjointEigenSolver<MatrixXd> solver(0.5 * (cal_LD + cal_LD.transpose()));
	//std::cout << "??" << std::endl;
	//MatrixXd Z = solver.eigenvectors() * solver.eigenvalues().cwiseMax(0).asDiagonal() * solver.eigenvectors().transpose();

	//std::cout << Z << std::endl;
	
	//ldout.close();
	//read_matout.close();


	int size = 2; // Dimensionality (rows)
	int nn = 5;     // How many samples (columns) to draw
	Eigen::internal::scalar_normal_dist_op<double> randN; // Gaussian functor
	Eigen::internal::scalar_normal_dist_op<double>::rng.seed(1); // Seed the rng

	// Define mean and covariance of the distribution
	Eigen::VectorXd mean(size);
	Eigen::MatrixXd covar(size, size);

	mean << 0, 0;
	covar << 1, 0,
		0, 1;

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

	std::cout << "Mean\n" << mean << std::endl;
	std::cout << "Covar\n" << covar << std::endl;
	std::cout << "Samples\n" << samples << std::endl;

	return 0;

}


bool cmp(std::pair<std::string, double> left, std::pair<std::string, double> right) {
	return std::abs(left.second) > std::abs(right.second);
}

