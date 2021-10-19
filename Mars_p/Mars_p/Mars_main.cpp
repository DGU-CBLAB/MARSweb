#include "Mars_cpp.h"

//std::vector<std::vector<std::pair<int, double>>> BS;
//std::vector<std::pair<double, std::vector<std::pair<int, double>>>> WBS;

int main() {

	/*
	std::string Geno, std::string Stat, int simNum_, double NCP_, double gamma_, int sub_size, int maxCausal_SNP, int mode, double UNI_threshold
	*/
	Mars_cpp M("ENSG00000173862.3_GENO", "ENSG00000173862.3_STAT",10000, 5.7, 0.01, 50, 2, 1, 5e-06);

	//for (int i = 0; i < M.BS.size(); i++) {
	//	bs << "0 ";
	//	for (int j = 0; j < M.BS[i].size(); j++) {
	//		bs << M.BS[i][j].second << " " << M.BS[i][j].first << " ";
	//	}
	//	bs << "\n";
	//}



	std::cout << "¿Ï·á" << std::endl;


	return 0;

}

