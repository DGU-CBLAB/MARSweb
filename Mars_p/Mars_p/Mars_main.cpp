#include "Mars_cpp.h"

//std::vector<std::vector<std::pair<int, double>>> BS;
//std::vector<std::pair<double, std::vector<std::pair<int, double>>>> WBS;

bool isNumber(const std::string& str);

int main() { // interface ¡¶¿€ int argc, char* argv[]

	std::string geno_path = "ENSG00000173862.3_GENO";
	std::string stat_path = "ENSG00000173862.3_STAT";
	std::string ld_path = "";

	
	// default value
	int simNum = 10000;
	int sub_size = 50;
	int maxCausal_SNP = 2;
	int mode = 0; //0 is normal MARS, 1 is basic sampling, 2 is importance sampling

	double NCP = 5.7;
	double gamma = 0.01;
	double UNI_threshold = 5e-06;

	//for (int i = 1; i < argc; i++)
	//{
	//	if (!strcmp(argv[i],"-geno")) {
	//		try {
	//			geno_path = argv[++i];
	//		}
	//		catch (...) {
	//			std::cout << "check your genotype data file path\n";
	//			return 0;
	//		}
	//	}
	//	else if (!strcmp(argv[i], "-stat")) {
	//		try {
	//			stat_path = argv[++i];
	//		}
	//		catch (...) {
	//			std::cout << "check your statistic data file path\n";
	//			return 0;
	//		}
	//	}
	//	else if (!strcmp(argv[i], "-sim")) {
	//		try {
	//			std::string temp  = argv[++i];
	//			if (!isNumber(temp)) { throw temp;}
	//			simNum = std::stoi(temp);
	//		}catch (...) {
	//			std::cout << "check your parameter simulation count\n";
	//			return 0;
	//		}
	//	}
	//	else if (!strcmp(argv[i], "-size")) {
	//		try {
	//			std::string temp = argv[++i];
	//			if (!isNumber(temp)) { throw temp; }
	//			sub_size = std::stoi(temp);
	//		}catch (...) {
	//			std::cout << "check your parameter sub size\n";
	//			return 0;
	//		}
	//	}
	//	else if (!strcmp(argv[i], "-mode")) {
	//		try {
	//			mode = std::stoi(argv[++i]);
	//			if (!(mode == 0 || mode == 1 || mode == 2)) { throw mode; }
	//		}
	//		catch (...) {
	//			std::cout << "select sampling mode 1 or 2";
	//			return 0;
	//		}
	//	}
	//}

	std::cout << "parameters check\n";
	std::cout << "geno path:" << geno_path << "\n";
	std::cout << "stat path:" << stat_path << "\n";
	std::cout << "mode number:" << mode << "\n";
	std::cout << "simulation count:" << simNum << "\n";
	std::cout << "max Casual snp count:" << maxCausal_SNP << "\n";

	Mars_cpp M(geno_path, stat_path, ld_path, simNum, NCP, gamma, sub_size, maxCausal_SNP, mode, UNI_threshold);

	M.run();

	std::cout << "Analysis done" << std::endl;

	return 0;
}


bool isNumber(const std::string& str)
{
	for (char const& c : str) {
		if (std::isdigit(c) == 0) return false;
	}
	return true;
}
