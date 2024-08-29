package Main;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
public class Manhattan {
	private File result_file; // SNP file
	private File p_value_file; // SNP file
	public File manplot;
	public File M_genelist;

	public String email_dir;
	private double threshold;
	
	Manhattan(String emailaddr, HttpServletRequest request){
		// create directory
		email_dir = Setup.manhattanPlot_result + emailaddr+"/";
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
				Process a_0_1 = null;
				Process a_0_2 = null;
					
						if(a_0_1 == null ) { //line_number.txt make
						
						//for pipe cmd, use "/bin/sh", "-c"
							String[] command = {
									"/bin/sh",
									"-c",
									"wc -l " + email_dir + "/p_value.txt >> " + email_dir + "/line_num.txt"
							};
							a_0_1 = Runtime.getRuntime().exec(command);
									 
						    a_0_1.waitFor();
						
						}
						if(a_0_2 == null ) {
						
						int num1 = NICE.countXfile(p_value_file.getPath());
						int num2 = 0;
						BufferedReader br = new BufferedReader(new FileReader(new File(p_value_file.getPath())));
						String temp = br.readLine();
						num2 = temp.split(" ").length;
						br.close();
						File manhattan = new File(email_dir + "/man.R");
						FileWriter fw = new FileWriter(manhattan, true);
						System.out.println(num1+"\t"+num2);
						fw.write(//"install.packages(\"dplyr\")\n" + 
								"library(dplyr)\n" + 
								"\n" + 
								//"install.packages(\"data.table\")\n" + 
								"library(data.table)\n" + 
								"\n" + 
								"p_full <- fread(\""+p_value_file.getPath()+"\") %>% as.matrix\n" + 
								"sum(p_full == 0)\n" + 
								"\n" + 
								"\n" + 
								"log10p_full <- -log10(p_full)\n" + 
								"sum(log10p_full == Inf)\n" + 
								"\n" + 
								"\n" + 
								"log10p_full[log10p_full == Inf] <- max(log10p_full[log10p_full != Inf])\n" + 
								"\n" + 
								"sum(is.infinite(log10p_full))\n" + 
								"\n" + 
								"mean_p <- rowMeans(log10p_full)\n" + 
								"mean_p %>% length\n" + 
								"mean_p %>% head\n" + 
								"png(\""+email_dir +"/manhattan.png"+"\", width=2000, height=1000, pointsize=18)\n" + 
								"plot(mean_p)\n" + 
								"dev.off()\n");
						fw.close();
						
						a_0_2 = Runtime.getRuntime().exec("R CMD BATCH " + manhattan.getPath());
						
						a_0_2.waitFor();	
						
						manplot = new File(email_dir+"manhattan.png");
						System.out.println("manhattan plot end");
			
						}
				
				}
				else if(input_type_str.equals("type2")){
				String cmd = "R CMD BATCH --args "
						+"-result="+ result_file.getPath()
						+" -threshold="+ threshold
						+" -out="+ email_dir+"manhattanplot "
						+Setup.Rplotdir+"/manhattanplot_all_chr.R" + " " + email_dir + "/manhattanplot_all_chr.Rout";
				Process proc = Runtime.getRuntime().exec(cmd);
				System.out.println(cmd);

				proc.waitFor();	
				manplot = new File(email_dir+"manhattanplot.pdf");
				M_genelist = new File(email_dir+"manhattanplot_only_MARSweb_gene_list.txt");

				System.out.println("manhattan plot end");
				}

		}catch(Exception e) {
			printERROR("Error Occured while draw manhattan plot!!");
			e.printStackTrace();
		}
		return; // end of running NICE
	}
		
	
	private void download_NICER_Result(HttpServletRequest request) {
		try {			
			Part part = request.getPart("P_file");
			p_value_file = new File(email_dir+"p_value.txt");
			try (InputStream inputStream= part.getInputStream()) { // save uploaded file
				Files.copy(inputStream, p_value_file.toPath());
			}
		}catch(Exception e) {
			printERROR("Error Occured while uploading p-value File!!");
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
			 download_NICER_Result(request);
		 }else if(input_type_str.equals("type2")) {
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
