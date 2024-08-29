package Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.util.Vector;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

@MultipartConfig(maxFileSize = -1, maxRequestSize = -1,location =Setup.FileSaveDirectory4)
public class Mars {
	private File x_file; // SNP file
	private File y_file; // Pheno file
	private File stat;
	private File set_info;
	public String email_dir;
	private int sampling_num;
	private int sampling_mode;
	private int thr_num; // total number of threads(1,2,...)
	private Thread[] thr;
	private int tl_snp_cnt=0;
	private final int FORCE_THREAD = 0; // Force Thread Number(not to force <= 0)
	Mars(String emailaddr, HttpServletRequest request){
		// create directory
		email_dir = Setup.FileSaveDirectory4 + emailaddr+"/";
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
		System.out.println(request.getParameter("Sampling_method"));
		
		sampling_num = Integer.parseInt(request.getParameter("sampling_number"));
		if(request.getParameter("tabss").equals("type1")) {
			sampling_mode = Integer.parseInt(request.getParameter("Sampling_method"));			
		}
		else {
			sampling_mode = 0;
		}
		email_dir += getCurrentStrDate()+"/";// append date within email address 
//		email_dir += "2019-04-07_13-17-53/";
		userDir = new File(email_dir);
		userDir.mkdir();
	}
	
	private String Check_file_type(Path p) throws IOException {
		return Files.probeContentType(p);
	}
		
	public void run(String thr_num_str, String input_type_str) {
		try {
			if(input_type_str.equals("type2")) { // ld input
			System.out.println(Setup.Marsdir+"mars_ld_sc.sh" + " " + email_dir + " " + thr_num_str + " " + sampling_num +" " + sampling_mode);
			Process proc = Runtime.getRuntime().exec(Setup.Marsdir+"mars_ld_sc.sh" + " " + email_dir + " " + thr_num_str + " " + sampling_num +" " + sampling_mode);
			//proc.getErrorStream().close();
			//proc.getInputStream().close();
			//proc.getOutputStream().close();
			proc.waitFor();					
			System.out.println("run end");
			}else {
				System.out.println(Setup.Marsdir+"mars_sc.sh" + " " + email_dir + " " + thr_num_str+ " " + sampling_num +" " + sampling_mode);
				Process proc = Runtime.getRuntime().exec(Setup.Marsdir+"mars_sc.sh" + " " + email_dir + " " + thr_num_str+ " " + sampling_num +" " + sampling_mode);
				//proc.getErrorStream().close();
				//proc.getInputStream().close();
				//proc.getOutputStream().close();
				proc.waitFor();					
				System.out.println("run end");	
			}

		}catch(Exception e) {
			printERROR("Error Occured while uploading set File!!");
			e.printStackTrace();
		}
		return; // end of running NICE
	}
	private void downloadSet(HttpServletRequest request) {
		// Upload SNP file	
		try {
			Part part = request.getPart("set_file");
			File x = new File(email_dir+ getFileName(part));
			try (InputStream inputStream= part.getInputStream()) { // save uploaded file
				Files.copy(inputStream, x.toPath());
				x_file = new File(email_dir + x.getName());
				String fileType = Check_file_type(Paths.get(x_file.getPath()));
				Process proc;
				//System.out.println("File unzip2...");
				if(fileType.equals("application/zip")) {
					proc = Runtime.getRuntime().exec("unzip -qq " + x_file.getPath() + " -d " + email_dir);
					proc.getErrorStream().close();
					proc.getInputStream().close();
					proc.getOutputStream().close();
					proc.waitFor();
					x_file.delete();
				}else {
					//error download set file
				}
			}		
		}catch(Exception e) {
			printERROR("Error Occured while uploading set File!!");
			e.printStackTrace();
		}
	}
	private void downloadStat(HttpServletRequest request) {
		try {
			Part part = request.getPart("stat_file");
			stat = new File(email_dir+"stat_file.txt");
			try (InputStream inputStream= part.getInputStream()) { // save uploaded file
				Files.copy(inputStream, stat.toPath());
			}
			Part part1 = request.getPart("set_info_file");
			set_info = new File(email_dir+"/set_info.txt");
			try (InputStream inputStream= part1.getInputStream()) { // save uploaded file
				Files.copy(inputStream, set_info.toPath());
			}
			Process proc = null;
			System.out.println(Setup.Marsdir + "1000genome_ld_reference " + 
					set_info.getPath() + " " + stat.getPath()+ " " + request.getParameter("range_number") +" "+email_dir);
			proc = Runtime.getRuntime().exec(Setup.Marsdir + "1000genome_ld_reference " + 
						set_info.getPath() + " " + stat.getPath()+ " " + request.getParameter("range_number")+" "+email_dir);
			proc.getErrorStream().close();
			proc.getInputStream().close();
			proc.getOutputStream().close();
			proc.waitFor();
		}catch(Exception e) {
			printERROR("Error Occured while uploading set File!!");
			e.printStackTrace();
		}		
	}
	public void downloadInputData(HttpServletRequest request, String input_type_str) {
		 if(input_type_str.equals("type1")) {
			 downloadSet(request);
		 }else if(input_type_str.equals("type2")) {
			 downloadSet(request);
		 }else if(input_type_str.equals("type3")) {
			 downloadStat(request);
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
