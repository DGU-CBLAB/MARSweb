package Main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@MultipartConfig(maxFileSize = -1, maxRequestSize = -1,location =Setup.FileSaveDirectory)
public class eQTL {
	
	private File result_file; // SNP file
	private File snp_pos_file; // SNP file
	private File phenotype_pos_file; // SNP file
	private File p_value_file; // SNP file
	public File eqtlplot;
	public File M_genelist;
	
	public String email_dir;
	private double threshold;
	
	eQTL(String emailaddr, HttpServletRequest request){
		// create directory
		email_dir = Setup.eQTLPlot_result + emailaddr+"/";
		System.out.println(Setup.eQTLPlot_result + "????");
		System.out.println(email_dir + "????");
		File userDir = new File(email_dir);
		if(!userDir.exists() && userDir.mkdir()) {			//this part need Optimize
			Process f_chm = null;
			try {
				if(f_chm == null) {
					f_chm = Runtime.getRuntime().exec("chmod 777 "+ email_dir);
					
					f_chm.waitFor();
				}						
			}
			catch(Exception e) {
				e.printStackTrace();
			} 
		}		
		email_dir += getCurrentStrDate()+"/";// append date within email address 
//		email_dir += "2019-04-07_13-17-53/";
		userDir = new File(email_dir);
		userDir.mkdir();
	}
	public void run(String input_type_str) {
		try {
			if(input_type_str.equals("type1")) { // ld input
			Process a_0 = Runtime.getRuntime().exec(Setup.NICEdir+"/eQTLmap4 -g "
						+ email_dir + "/y_pos.txt -s " + email_dir + "/x_pos.txt -p " 
					     + email_dir + "/p_value.txt -m 2.5 -M 5 -o " + email_dir + "/eQTL.png -z 2");
			a_0.waitFor();
			
			eqtlplot = new File(email_dir + "/eQTL.png");
			System.out.println("eQTLplot end");
			
			}else if(input_type_str.equals("type2")){
				String cmd = "R CMD BATCH --args "
						+"-result="+ result_file.getPath()
						+" -threshold="+ threshold
						+" -out="+ email_dir+"eQTLplot "
						+Setup.Rplotdir+"/eQTLmapping_all_chr.R" + " " + email_dir + "/eQTLmapping_all_chr.Rout";
				Process proc = Runtime.getRuntime().exec(cmd);
				System.out.println(cmd);
//				File n_check = new File(f_path + "/NICE.txt");

				proc.waitFor();	
				eqtlplot = new File(email_dir+"eQTLplot.pdf");
				M_genelist = new File(email_dir+"eQTLplot_only_MARSweb_gene_list.txt");
				System.out.println("eQTLplot end");
			}

		}catch(Exception e) {
			printERROR("Error Occured while draw eQTL plot!!");
			e.printStackTrace();
		}
		return; // end of running NICE
	}
	
	private void download_NICER_Result(HttpServletRequest request) {
		try {			
			Part part = request.getPart("SNP_pos");
			snp_pos_file = new File(email_dir+"x_pos.txt");
			try (InputStream inputStream= part.getInputStream()) { // save uploaded file
				Files.copy(inputStream, snp_pos_file.toPath());
			}
			Part part1 = request.getPart("Phenotype_pos");
			phenotype_pos_file = new File(email_dir+"/set_info.txt");
			try (InputStream inputStream= part1.getInputStream()) { // save uploaded file
				Files.copy(inputStream, phenotype_pos_file.toPath());
			}

			Part part2 = request.getPart("set_info_file");
			p_value_file = new File(email_dir+"/p_value.txt");
			try (InputStream inputStream = part2.getInputStream()) { // save uploaded file
				Files.copy(inputStream, p_value_file.toPath());
			}
		}catch(Exception e) {
			printERROR("Error Occured while uploading set File!!");
			e.printStackTrace();
		}		
	}
	
	private void download_MARSweb_Result(HttpServletRequest request) {
		try {			
			Part part = request.getPart("SNP_pos");
			result_file = new File(email_dir+"result.txt");
			threshold = Double.parseDouble(request.getParameter("threshold"));

			try (InputStream inputStream= part.getInputStream()) { // save uploaded file
				Files.copy(inputStream, result_file.toPath());
			}
		}catch(Exception e) {
			printERROR("Error Occured while uploading set File!!");
			e.printStackTrace();
		}		
	}
	public void downloadInputData(HttpServletRequest request, String input_type_str) {
		 if(input_type_str.equals("type1")) {
			 System.out.println("1111");
			 download_NICER_Result(request);
		 }else if(input_type_str.equals("type2")) {
			 System.out.println("2222");
			 download_MARSweb_Result(request);
		 }
	}	
	/**
	 * Error Printing Screen
	 * @param err_str error message
	 */
	public static void printERROR(String err_msg) {
		System.err.print(err_msg+"\n");
	}
	/**
	 * Get String formatted Current Date
	 * @return
	 */
	public String getCurrentStrDate() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");//dd/MM/yyyy
		Date now = new Date();
		return sdfDate.format(now);
	}
	public String getFileName(Part part) {
		String contentDisp = part.getHeader("content-disposition");
		String[] tokens = contentDisp.split(";");
		for (String token : tokens) {
			if (token.trim().startsWith("filename")) {
				return token.substring(token.indexOf("=") +2, token.length()-1);
			}
		}
		return "";
	}

}
