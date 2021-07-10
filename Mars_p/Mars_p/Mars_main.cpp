#include <iostream>
#include "CBLAB_Taegun.h"

bool cmp(std::pair<std::string, double> left, std::pair<std::string, double> right);
int main() {

	int select_num = 50;

	std::ifstream geno("ENSG00000173862.3_GENO");
	std::ifstream stat("ENSG00000173862.3_STAT");
	std::ofstream ldout("cppld50.txt");
	std::ofstream read_matout("read_matGS.txt");

	std::vector<std::pair<std::string, double>> GS;
	std::map<int, double>::iterator iter;

	double stemp = 0.0;
	std::string gtemp = "";
	
	while (stat >> stemp) {
		getline(geno, gtemp);
		GS.push_back(std::pair<std::string, double>(gtemp, stemp));
	}

	std::sort(GS.begin(), GS.end(), cmp);

	geno.close();
	stat.close();

	Eigen::MatrixXd read_GS = read_mat(GS, 50, 1);
	Eigen::MatrixXd cal_LD = cal_cor(read_GS);
	
	ldout << cal_LD << std::endl;

	//SelfAdjointEigenSolver<MatrixXd> solver(0.5 * (cal_LD + cal_LD.transpose()));
	//std::cout << "??" << std::endl;
	//MatrixXd Z = solver.eigenvectors() * solver.eigenvalues().cwiseMax(0).asDiagonal() * solver.eigenvectors().transpose();

	//std::cout << Z << std::endl;
	ldout.close();
	read_matout.close();
	return 0;

}


bool cmp(std::pair<std::string, double> left, std::pair<std::string, double> right) {
	return std::abs(left.second) > std::abs(right.second);
}