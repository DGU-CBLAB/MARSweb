#include "Mars_cpp.h"

Mars_cpp::Mars_cpp(std::string Geno, std::string Stat) {
	subsize = 50; //default = 50;
	maxCausalSNP = 2;
	simNum = 1000;
	NCP = 5.7;
	gamma = 0.01;

	read_input_file(Geno, Stat); // Geno and Stat into GS var 

	snpCount = 50;

	stat = new double[snpCount];
	postValues = new double[snpCount];
	histValues = new double[maxCausalSNP + 1];

	for (int i = 0; i < snpCount; i++) {
		stat[i] = GS[i].second;
		postValues[i] = 0.0;
	}
	for (int i = 0; i < maxCausalSNP + 1; i++) {
		histValues[i] = 0.0;
	}

	Geno_part = read_mat(GS, 50, 1);
	Geno_all = read_mat(GS, snpCount, 1);
	sigmaMatrix = cal_cor(Geno_part);

	std::cout << "Data preprocessing complete\n";
	Geno_LD = generateLD(Geno_part);
	std::cout << "LD calculation complete\n";

	BS = basic_sampling(1000, 50, Geno_all);
	WBS = importance_sampling(1000, 50, Geno_all);

	//alt
	std::cout << "alteration pvalue is " << computePvalue(GS[0].second) << std::endl;
	computeLRT(stat);
	std::cout << "alteration LRTscore is " << LRTscore << std::endl;

	makeSigmaPositiveSemiDefinite(sigmaMatrix);
	for (int i = 0; i < 1000; i++) { //simNum
		for (int j = 0; j < 50; j++) {
			stat[j] = BS[i][j].second;
		}

		std::cout << "Sampling pvalue is " << computePvalue(stat[0]) << " " << stat[0] << std::endl;
		computeLRT(stat);
		std::cout << "Sampling LRTscore is " << LRTscore << std::endl;
	}
	for (int i = 0; i < 1000; i++) { //simNum
		for (int j = 0; j < 50; j++) {
			stat[j] = WBS[i].second[j].second;
		}

		std::cout << "Importance Sampling pvalue is " << computePvalue(stat[0]) << std::endl;
		computeLRT(stat);
		std::cout << "Importance Sampling LRTscore is " << LRTscore << std::endl;
	}
};

int Mars_cpp::nextBinary(int* data, int size) { //이거 실제로 출력해보기
	int i = 0;
	int total_one = 0;
	int index = size - 1;
	int one_countinus_in_end = 0;

	while (index >= 0 && data[index] == 1) {
		index = index - 1;
		one_countinus_in_end = one_countinus_in_end + 1; //1개수 확인.
	}
	if (index >= 0) {
		while (index >= 0 && data[index] == 0) {
			index = index - 1;
		}
	}
	if (index == -1) {
		while (i < one_countinus_in_end + 1 && i < size) {
			data[i] = 1;
			i = i + 1;
		}
		i = 0;
		while (i < size - one_countinus_in_end - 1) {
			data[i + one_countinus_in_end + 1] = 0;
			i = i + 1;
		}
	}
	else if (one_countinus_in_end == 0) {
		data[index] = 0;
		data[index + 1] = 1;
	}
	else {
		data[index] = 0;
		while (i < one_countinus_in_end + 1) {
			data[i + index + 1] = 1;
			if (i + index + 1 >= size)
				std::cout << "ERROR3 " << i + index + 1  << "\n";
			i = i + 1;
		}
		i = 0;
		while (i < size - index - one_countinus_in_end - 2) {
			data[i + index + one_countinus_in_end + 2] = 0;
			if (i + index + one_countinus_in_end + 2 >= size) {
				std::cout << "ERROR4 " << i + index + one_countinus_in_end + 2 << "\n";
			}
			i = i + 1;
		}
	}
	i = 0;
	total_one = 0;
	for (i = 0; i < size; i++)
		if (data[i] == 1)
			total_one = total_one + 1;

	return(total_one);
}

int Mars_cpp::count_string_col(std::string str) {
	std::string token;
	std::stringstream stream; int count = 0;

	stream.str(str);
	while (stream >> token) {
		count++;
	}
	return count;
}

double Mars_cpp::computePvalue(double max_stat) {
	return 2 * normaICDF(-fabs(max_stat));
}

double Mars_cpp::normaICDF(double value) {
	return 0.5 * erfc(-value * M_SQRT1_2);
}

long int Mars_cpp::fact(int n) {
	if (n == 0)
		return 1;
	return n * fact(n - 1);
}

long int Mars_cpp::nCr(int n, int r) {
	long int result = 1;
	for (int i = n; i > n - r; i--)
		result *= i;
	return result / fact(r);
}

void Mars_cpp::computeLRT(double* stat) {
	int num = 0; //int total = 0;
	double sumLikelihood = 0;
	double allZero_likelihood = 0;//define allZero_likelihood
	double tmp_likelihood = 0;
	long int total_iteration = 0;
//	int* configure = (int*)malloc(snpCount * sizeof(int*)); // original data
	int* configure = new int[snpCount];
	for (long int i = 0; i <= maxCausalSNP; i++)
		total_iteration = total_iteration + nCr(snpCount, i);
	for (long int i = 0; i < snpCount; i++)
		configure[i] = 0;
	for (long int i = 0; i < total_iteration; i++) {
		tmp_likelihood = fastLikelihood(configure, stat) * (pow(gamma, num)) * (pow(1 - gamma, snpCount - num));
		if (i == 0) { allZero_likelihood = tmp_likelihood; }//save allzero likelihood
		sumLikelihood += tmp_likelihood;
		for (int j = 0; j < snpCount; j++) {
			postValues[j] = postValues[j] + tmp_likelihood * configure[j];
		}
		histValues[num] = histValues[num] + tmp_likelihood;
		num = nextBinary(configure, snpCount);
	}//cout<<"here="<<sigmaMatrix(1,0)<<endl;
	for (int i = 0; i <= maxCausalSNP; i++)
		histValues[i] = histValues[i] / sumLikelihood;
	LRTscore = (sumLikelihood - allZero_likelihood) / allZero_likelihood;//compute LRTscore
	delete[] configure;
	//free(configure);
	//for(int i=0; i<snpCount; i++) total = total+stat[i];
	//if(total==0) LRTscore= 10000000; 
}

double Mars_cpp::fastLikelihood(int* configure, double* stat) {
	int snpCount = 50; // input Par or estimate
	int causalCount = 0;
	std::vector<int> causalIndex;

	for (int i = 0; i < snpCount; i++) {
		causalCount += configure[i];
		if (configure[i] == 1)
			causalIndex.push_back(i);
	}
	if (causalCount == 0) {
		int maxVal = 0;
		for (int i = 0; i < snpCount; i++) {
			if (maxVal < abs(stat[i]))
				maxVal = stat[i];
		}
	//	baseValue = maxVal * maxVal;
	}
	Eigen::MatrixXd Rcc(causalCount, causalCount); Rcc.setZero();
	Eigen::VectorXd Zcc(causalCount); Zcc.setZero();
	Eigen::VectorXd mean(causalCount); mean.setZero();
	Eigen::MatrixXd diagC(causalCount, causalCount); diagC.setZero();
	for (int i = 0; i < causalCount; i++) {
		for (int j = 0; j < causalCount; j++) {
			Rcc(i, j) = sigmaMatrix(causalIndex[i], causalIndex[j]);
		}
		Zcc(i, 0) = stat[causalIndex[i]];
		diagC(i, i) = NCP;
	}

	while (Rcc.determinant() <= 0.01) {
		Eigen::MatrixXd toAdd(causalCount, causalCount);
		toAdd.setIdentity(); // Identity
		Rcc = Rcc + 0.1 * toAdd;
	}
	return fracdmvnorm(Zcc, mean, Rcc, diagC);
}

double Mars_cpp::fracdmvnorm(Eigen::MatrixXd Z, Eigen::MatrixXd mean, Eigen::MatrixXd R, Eigen::MatrixXd diagC) {
	Eigen::MatrixXd newR = R + R * diagC * R;
	Eigen::MatrixXd ZcenterMean = Z - mean;
	Eigen::MatrixXd res1 = ZcenterMean.transpose() * R.inverse() * (ZcenterMean);
	Eigen::MatrixXd res2 = ZcenterMean.transpose() * newR.inverse() * (ZcenterMean);

	double baseValue = 0.0; //maxVal * maxVal;

	double v1 = res1(0, 0) / 2 - res2(0, 0) / 2 - baseValue / 2;
	return(exp(v1) / sqrt(newR.determinant()) * sqrt(R.determinant()));
}

double Mars_cpp::dmvnorm_(const Eigen::VectorXd& x, const Eigen::VectorXd& meanVec, const Eigen::MatrixXd& covMat) //한개씩 계산댐.
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

Eigen::MatrixXd Mars_cpp::rmvnorm_(int simulation_Number, Eigen::VectorXd mean, Eigen::MatrixXd covar) {

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

Eigen::MatrixXd Mars_cpp::cal_cor(Eigen::MatrixXd& mat) {
	int mat_cols = mat.cols();
	int mat_rows = mat.rows();

	Eigen::MatrixXd centered = mat.colwise() - mat.rowwise().mean(); //cov랑 틀림 주의할것!. 넣는 순서가 다름.
	Eigen::MatrixXd ret(mat_rows, mat_rows);
	for (int i = 0; i < mat_rows; i++) {
		for (int j = 0; j < mat_rows; j++) {
			ret(i, j) = (centered.row(i) * centered.row(j).transpose()).sum() / (std::sqrt((centered.row(i) * centered.row(i).transpose()).sum()) * std::sqrt((centered.row(j) * centered.row(j).transpose()).sum()));
		}
	}

	return ret;
}

Eigen::MatrixXd Mars_cpp::generateLD(Eigen::MatrixXd& mat) {
	Eigen::MatrixXd cal_LD = cal_cor(mat);
	SelfAdjointEigenSolver<MatrixXd> solver(0.5 * (cal_LD + cal_LD.transpose()));
	
	return solver.eigenvectors() * solver.eigenvalues().cwiseMax(0).asDiagonal() * solver.eigenvectors().transpose(); 
}


void Mars_cpp::makeSigmaPositiveSemiDefinite(Eigen::MatrixXd& mat) {
	
	Eigen::MatrixXd iden(mat.rows(), mat.cols()); iden.setIdentity();
	while (mat.lu().determinant() < 0) {
		mat = mat + 0.1 * iden;
	}
}

void Mars_cpp::read_input_file(std::string Geno, std::string Stat) {
	std::ifstream geno(Geno);
	std::ifstream stat(Stat);

	//Error handling
	if (geno.is_open() == false) {
		std::cout << Geno << " is not correct path.\n";
		exit(1);
	}
	if (stat.is_open() == false) {
		std::cout << Stat << " is not correct path.\n";
		exit(1);
	}
	std::cout << "Read successfully\n";

	double stemp = 0.0;
	std::string gtemp = "";

	while (!stat.eof() || !geno.eof()) {
		if (stat.eof()) {
			std::cout << "More Genotype data than Statistics. Pleas check input files\n";
			exit(1);
		}
		else if (geno.eof()) {
			std::cout << "More Statistics than Genotype data. Pleas check input files\n";
			exit(1);
		}
		stat >> stemp; getline(geno, gtemp);
		GS.push_back(std::pair<std::string, double>(gtemp, stemp));
	}

	std::sort(this->GS.begin(), this->GS.end(), cmp_str_double);
	
	std::cout << "Descending sort done\n";
	geno.close(); stat.close();
}

void Mars_cpp::run() {
	




}

bool cmp_str_double(std::pair<std::string, double> left, std::pair<std::string, double> right) { //for sort vector GS
	return std::abs(left.second) > std::abs(right.second);
}

bool cmp_int_double(std::pair<int, double> left, std::pair<int, double> right) { //for sort vector GS
	return std::abs(left.second) > std::abs(right.second);
}

Eigen::MatrixXd Mars_cpp::read_mat(std::vector<std::pair<std::string, double>> X, int row, int start_number) {
	//start_number default = 0, but if using MARS, start_number is 1

	std::string read_buffer;
	std::string token;
	std::stringstream stream;

	int col = count_string_col(X[0].first) - start_number;
	std::cout << "col = " << col << std::endl;
	Eigen::MatrixXd ret_mat = Eigen::MatrixXd(row, col);
	for (int i = 0; i < row; i++) { //row
		stream.str(X[i].first);
		for (int j = 0; j < start_number; j++) {
			stream >> token;
		}
		for (int j = 0; j < col; j++) { //col
			stream >> token;
			ret_mat(i, j) = std::stold(token);
		}
		stream.clear();  //for coursor reset
	}
	return ret_mat;
}

std::vector<std::vector<std::pair<int, double>>> Mars_cpp::basic_sampling(int simNum, int topNum, Eigen::MatrixXd Geno) { // Geno row x col row is indi, col is snp count
	int Geno_cols = Geno.cols();
	int Geno_rows = Geno.rows();

	Eigen::MatrixXd xs_scale = Geno.colwise() - Geno.rowwise().mean();
	for (int i = 0; i < Geno_rows; i++) {
		xs_scale.row(i) = xs_scale.row(i) / std::sqrt((Geno.row(i).array() - Geno.row(i).mean()).square().sum() / (Geno_cols - 1));
	}

	Eigen::VectorXd null_mean(Geno_cols); null_mean.setZero();
	Eigen::MatrixXd null_covar(Geno_cols, Geno_cols); null_covar.setIdentity();

	Eigen::MatrixXd Sall = rmvnorm_(simNum, null_mean, null_covar); // Geno_cols x simNum
	std::ofstream Sall_out("basic_Sal.txt");
	Sall_out << Sall << std::endl;
	Eigen::MatrixXd Sall_new = (xs_scale / std::sqrt(Geno_cols)) * Sall;

	std::vector< std::vector<std::pair<int, double>>> BS;
	for (int i = 0; i < simNum; i++) {
		std::vector<std::pair<int, double>> BS_element;
		for (int j = 0; j < Geno_rows; j++) {
			BS_element.push_back(std::pair<int, double>(j, Sall_new(j, i)));
		}
		std::sort(BS_element.begin(), BS_element.end(), cmp_int_double);
		BS_element.resize(topNum); BS.push_back(BS_element);
	}
	Sall_out.close();
	return BS;
}

std::vector<std::pair<double, std::vector<std::pair<int, double>>>> Mars_cpp::importance_sampling(int simNum, int topNum, Eigen::MatrixXd Geno) { // Geno row x col row is indi, col is snp count
	int Geno_cols = Geno.cols();
	int Geno_rows = Geno.rows();

	Eigen::MatrixXd xs_scale = Geno.colwise() - Geno.rowwise().mean();
	for (int i = 0; i < Geno_rows; i++) {
		xs_scale.row(i) = xs_scale.row(i) / std::sqrt((Geno.row(i).array() - Geno.row(i).mean()).square().sum() / (Geno_cols - 1));
	}


	std::ofstream Xs_out("xs_cpp.txt");
	Xs_out << xs_scale << std::endl;
	Xs_out.close();

	Eigen::VectorXd null_mean(Geno_cols); null_mean.setZero();
	Eigen::MatrixXd null_covar(Geno_cols, Geno_cols); null_covar.setIdentity();
	Eigen::MatrixXd Sall = rmvnorm_(simNum, null_mean, null_covar * std::sqrt(2)); // Geno_cols x simNum, *std::sqrt(2) for importance sampling

	std::ofstream Sall_out("importance_Sal.txt");
	Sall_out << Sall << std::endl;
	Sall_out.close();

	Eigen::MatrixXd Sall_new = (xs_scale / std::sqrt(Geno_cols)) * Sall; // Geno_rows x simNum



	std::vector<std::pair<double, std::vector<std::pair<int, double>>>> WBS;
	double Weight = 0.0f;
	for (int i = 0; i < simNum; i++) { //calculate t(apply(Sall_new, 2, top50, fR=R, topNum=topNum)) in Mars
		std::vector<std::pair<int, double>> BS_element;
		for (int j = 0; j < Geno_rows; j++) {
			BS_element.push_back(std::pair<int, double>(j, Sall_new(j, i)));
		}
		std::sort(BS_element.begin(), BS_element.end(), cmp_int_double);
		BS_element.resize(topNum);
		Weight = std::log(dmvnorm_(Sall.col(i), null_mean, null_covar)) / std::log(dmvnorm_(Sall.col(i), null_mean, null_covar * std::sqrt(2)));
		WBS.push_back(std::pair<double, std::vector<std::pair<int, double>>>(Weight, BS_element));
	}
	return WBS;
}