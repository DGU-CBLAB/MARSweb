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
public class Venn {
	private File result_file; // SNP file
	public File vennDiagram;

	public String email_dir;
	private double threshold;
	
	Venn(String emailaddr, HttpServletRequest request){
		// create directory
		email_dir = Setup.vennDiagram_result + emailaddr+"/";
		System.out.println(email_dir);		
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
			System.out.println(input_type_str);
				if(input_type_str.equals("type1")){
				String cmd = "R CMD BATCH --args "
						+"-result="+ result_file.getPath()
						+" -threshold="+ threshold
						+" -out="+ email_dir+"venn.tiff "
						+Setup.Rplotdir+"/venn.R" + " " + email_dir + "/venn.Rout";
				Process proc = Runtime.getRuntime().exec(cmd);
				System.out.println(cmd);
				proc.waitFor();	
				vennDiagram = new File(email_dir+"venn.tiff");
				System.out.println("venndiagram end");
				}
		}catch(Exception e) {
			printERROR("Error Occured while draw vennDiagram!!");
			e.printStackTrace();
		}
		return; // end of running NICE
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
			 System.out.println("????");
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
