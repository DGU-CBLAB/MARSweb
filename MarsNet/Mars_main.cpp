#include "Mars_cpp.h"

//std::vector<std::vector<std::pair<int, double>>> BS;
//std::vector<std::pair<double, std::vector<std::pair<int, double>>>> WBS;

bool isNumber(const std::string& str);

int main(int argc, char* argv[]) { // interface ¡¶¿€ int argc, char* argv[]

	std::string geno_path = "none";
	std::string stat_path = "none";
	std::string ld_path = "none";
	std::string set_name = "none";
	
	// default value
	int simNum = 1000;
	int sub_size = 50;
	int maxCausal_SNP = 2;
	int mode = 1; //0 is normal MARS, 1 is basic sampling, 2 is importance sampling
	int input_type = 0; // 0 is local, 1 is download from web_server

	double NCP = 5.7;
	double gamma = 0.01;
	double UNI_threshold = 5e-06;

	for (int i = 1; i < argc; i++)
	{
		if (!strcmp(argv[i],"-geno")) {
			try {
				geno_path = argv[++i];
			}
			catch (...) {
				std::cout << "check your genotype data file path\n";
				return 0;
			}
		}
		else if (!strcmp(argv[i], "-stat")) {
			try {
				stat_path = argv[++i];
			}
			catch (...) {
				std::cout << "check your statistic data file path\n";
				return 0;
			}
		}
		else if (!strcmp(argv[i], "-ld")) {
			try {
				ld_path = argv[++i];
			}
			catch (...) {
				std::cout << "check your ld data file path\n";
				return 0;
			}
		}
		else if (!strcmp(argv[i], "-sim")) {
			try {
				std::string temp  = argv[++i];
				if (!isNumber(temp)) { throw temp;}
				simNum = std::stoi(temp);
			}catch (...) {
				std::cout << "check your parameter simulation count\n";
				return 0;
			}
		}
		else if (!strcmp(argv[i], "-size")) {
			try {
				std::string temp = argv[++i];
				if (!isNumber(temp)) { throw temp; }
				sub_size = std::stoi(temp);
			}catch (...) {
				std::cout << "check your parameter sub size\n";
				return 0;
			}
		}
		else if (!strcmp(argv[i], "-mode")) {
			try {
				mode = std::stoi(argv[++i]);
				if (!(mode == 0 || mode == 1 || mode == 2)) { throw mode; }
			}
			catch (...) {
				std::cout << "select sampling mode 1 or 2";
				return 0;
			}
		}
		//else if (!strcmp(argv[i], "-threshold")) { // fix it later
		//	try {
		//		std::string temp = argv[++i];
		//	//	if (!isNumber(temp)) { throw temp; }
		//		UNI_threshold = std::stof(temp);
		//	}
		//	catch (...) {
		//		std::cout << "check your parameter threshold\n";
		//		return 0;
		//	}
		//}
		//else if (!strcmp(argv[i], "-set_name")) { // fix it later
		//	try {
		//		std::string temp = argv[++i];
		//		if (temp == "") { throw temp; }
		//		set_name = temp;
		//	}
		//	catch (...) {
		//		std::cout << "check your parameter set_name\n";
		//		return 0;
		//	}
		//}
		//else if (!strcmp(argv[i], "-web")) { // fix it later
		//	try {
		//		std::string temp = argv[++i];
		//		if (temp == "") { throw temp; }
		//		input_type = std::stoi(temp);
		//	}
		//	catch (...) {
		//		std::cout << "check your parameter set_name\n";
		//		return 0;
		//	}
		//}
	}

	//parameter check
	if (geno_path == "none" && ld_path == "none") { // no geno & ld 
		std::cout << "Need ld or genotype data to analyze" << std::endl;
		exit(1);
	}
	else if (stat_path == "none") {
		std::cout << "Need stat data to analyze" << std::endl;
	}
	else if (geno_path == "none" && ld_path != "none" && mode != 0) { // must use mode 0
		mode = 0;
		std::cout << "there is no genotype data, change mode 0" << std::endl;
	}
	else if (simNum < 1000) {
		std::cout << "The number of samples is too small. set the count to 1000" << std::endl;
		simNum = 1000;
	}
	else if (sub_size < 30) {
		std::cout << "subsize is too small. set subsize to 30" << std::endl;
		sub_size = 30;
	}
	else if (sub_size > 100) {
		std::cout << "subsize is too big. set subsize to 100" << std::endl;
	}

	std::cout << "parameters check\n";
	std::cout << "geno path:" << geno_path << "\n";
	std::cout << "stat path:" << stat_path << "\n";
	std::cout << "ld   path:" << ld_path << "\n";
	std::cout << "mode number:" << mode << "\n";
	std::cout << "simulation count:" << simNum << "\n";
	std::cout << "max Casual snp count:" << maxCausal_SNP << "\n";
	
	std::clock_t start = std::clock();

	Mars_cpp M(geno_path, stat_path, ld_path, simNum, NCP, gamma, sub_size, maxCausal_SNP, mode, UNI_threshold, set_name, input_type);
	M.run();
	double duration = (std::clock() - start) / (double)CLOCKS_PER_SEC;
	std::cout << "running time = " << duration << " secs\n";


	std::cout << "Analysis done" << std::endl;

	return 0;
}


bool isNumber(const std::string& str){
	for (char const& c : str) {
		if (std::isdigit(c) == 0) return false;
	}
	return true;
}
