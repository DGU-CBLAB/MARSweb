#include <iostream>
#include "CBLAB_Taegun.h"

bool cmp(std::pair<std::string, double> left, std::pair<std::string, double> right);
int main() {

	//int select_num = 50;

	//std::ifstream geno("ENSG00000173862.3_GENO");
	//std::ifstream stat("ENSG00000173862.3_STAT");
	//
	//
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

	//for (int i = 0; i < select_num; i++) {
	//	std::cout << GS[i].first << "\n";
	//}


	//geno.close();
	//stat.close();

	MatrixXd m(6, 6);
	m << 1, 0.477, 0.644, 0.478, 0.651, 0.826,

		0.477, 1, 0.516, 0.233, 0.682, 0.75,

		0.644, 0.516, 1, 0.599, 0.581, 0.742,

		0.478, 0.233, 0.599, 1, 0.741, 0.8,

		0.651, 0.682, 0.581, 0.741, 1, 0.798,

		0.826, 0.75, 0.742, 0.8, 0.798, 1;

	SelfAdjointEigenSolver<MatrixXd> solver(0.5 * (m + m.transpose()));
	MatrixXd Z = solver.eigenvectors() * solver.eigenvalues().cwiseMax(0).asDiagonal() * solver.eigenvectors().transpose();

	std::cout << Z << std::endl;
	return 0;

}


bool cmp(std::pair<std::string, double> left, std::pair<std::string, double> right) {
	return std::abs(left.second) > std::abs(right.second);
}