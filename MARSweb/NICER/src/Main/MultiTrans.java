package Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

@MultipartConfig(maxFileSize = -1, maxRequestSize = -1,location =Setup.FileSaveDirectory2)
public class MultiTrans {
	private File x_file; // SNP file
	private File y_file; // Pheno file
	private File threshold_file; // threshold file
	private String Sampling_count;
	
	public String email_dir;
	public String windowsize;
	MultiTrans(String emailaddr, HttpServletRequest request){
		// create directory
		email_dir = Setup.FileSaveDirectory2 + emailaddr+"/";
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
	public void run() {
			try {
			Process proc = Runtime.getRuntime().exec(
						Setup.MultiTransdir+"mtcvpp.sh "
						+x_file.toPath() + " "
						+y_file.toPath() + " "
						+windowsize + " " + Sampling_count + " "
						+threshold_file.toPath() + " "
						+email_dir + "/result "
						+email_dir + "/c.txt "
						+email_dir + "/r.txt");
				proc.waitFor();
				System.out.println(	Setup.MultiTransdir+"mtcvpp.sh "
						+x_file.toPath() + " "
						+y_file.toPath() + " "
						+windowsize + " " + Sampling_count + " "
						+threshold_file.toPath() + " "
						+email_dir + "/result "
						+email_dir + "/c.txt "
						+email_dir + "/r.txt");				
				System.out.println("Multitrans finish");
			}catch(Exception e) {
				System.err.println("Error occured");
				e.printStackTrace();
		}		
	}
	public void downloadInputData(HttpServletRequest request, String input_type_str) {
		 Sampling_count = request.getParameter("SamplingNumber");
		 windowsize = request.getParameter("WindowSize");
		 
		 if(input_type_str.equals("type1")) {
			 downloadPlink(request);
		 }else if(input_type_str.equals("type2")) {
			 downloadXY(request);
		 }else if(input_type_str.equals("type3")) {
			 downloadTransposedXY(request);
		 }else if(input_type_str.equals("type4")){
			 downloadVCF(request);
		 }
	}
	private void downloadVCF(HttpServletRequest request) {
		try {
			Part part = request.getPart("VCFfile");
			File x = new File(email_dir+"/VCF.vcf");
			try (InputStream inputStream = part.getInputStream()){
				Files.copy(inputStream, x.toPath());
			}
			Part part1 = request.getPart("Expfile");
			File y = new File(email_dir+"/input_y.txt");
			try (InputStream inputStream= part1.getInputStream()) { // save uploaded file
				Files.copy(inputStream, y.toPath());
			}
			Part part2 = request.getPart("Threshold_file");//Threshold_file
			threshold_file = new File(email_dir+"/threshold.txt");
			try (InputStream inputStream= part2.getInputStream()) { // save uploaded file
				Files.copy(inputStream, threshold_file.toPath());
			}
			
			
			
			Process vcf_data = null;
			//--vcf test.vcf --allow-extra-chr --no-fid --no-parents --no-sex --no-pheno --out test
			vcf_data = Runtime.getRuntime().exec(Setup.PLINKdir+" --vcf " + email_dir + "/VCF.vcf --allow-extra-chr --no-fid --no-parents --no-sex --no-pheno --out "+email_dir+"/input_f");
			vcf_data.waitFor();
			
			Process plink_data = null;
			plink_data = Runtime.getRuntime().exec(Setup.PLINKdir+" --bfile " + email_dir + "/input_f --allow-extra-chr --recodeA --noweb --maf 0.3 --out "+email_dir+"/input_x");
			plink_data.waitFor();
			
			Process pl_to_input = null; //x data
			pl_to_input = Runtime.getRuntime().exec(Setup.NICEdir+"/pl_to_input " + email_dir);
			pl_to_input.waitFor();
			
			Process pl_to_input_y = null; // y data
			pl_to_input_y = Runtime.getRuntime().exec(Setup.NICEdir+"pl_to_input_y " + email_dir);
			pl_to_input_y.waitFor();
			
			// X_rightdim -> X.txt Y_rightdim -> Y.txt
			transposeFile(email_dir+"/X_rightdim.txt",email_dir+"/X.txt");
			transposeFile(email_dir+"/Y_rightdim.txt",email_dir+"/Y.txt");	
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	private void downloadTransposedXY(HttpServletRequest request) {
		// Upload SNP file	
		try {
			Part part = request.getPart("SNPfile2");
			File x = new File(email_dir+"/X_rightdim.txt");
			try (InputStream inputStream= part.getInputStream()) { // save uploaded file
				Files.copy(inputStream, x.toPath());
			}		
			// Upload Phenotype file
			Part part1 = request.getPart("Phenotypefile2");
			File y = new File(email_dir+"/Y_rightdim.txt");
			try (InputStream inputStream= part1.getInputStream()) { // save uploaded file
				Files.copy(inputStream, y.toPath());
			}
			Part part2 = request.getPart("Threshold_file");//Threshold_file
			threshold_file = new File(email_dir+"/threshold.txt");
			try (InputStream inputStream= part2.getInputStream()) { // save uploaded file
				Files.copy(inputStream, threshold_file.toPath());
			}
		}catch(Exception e) {
			printERROR("Error Occured while uploading transposed XY data!!");
			e.printStackTrace();
		}
		transposeFile(email_dir+"X_rightdim.txt",email_dir+"X.txt");
		transposeFile(email_dir+"Y_rightdim.txt",email_dir+"Y.txt");
		x_file = new File(email_dir+"/X.txt");
		y_file = new File(email_dir+"/Y.txt");
		System.out.println(email_dir);
	}
	private void downloadXY(HttpServletRequest request) {
		try {
			// Upload SNP file	
			Part part = request.getPart("SNPfile");
			x_file = new File(email_dir+"/X.txt");
			try (InputStream inputStream= part.getInputStream()) { // save uploaded file
				Files.copy(inputStream, x_file.toPath());
			}		
			// Upload Phenotype file
			Part part1 = request.getPart("Phenotypefile");
			y_file = new File(email_dir+"/Y.txt");
			try (InputStream inputStream= part1.getInputStream()) { // save uploaded file
				Files.copy(inputStream, y_file.toPath());
			}
			Part part2 = request.getPart("Threshold_file");//Threshold_file
			threshold_file = new File(email_dir+"/threshold.txt");
			try (InputStream inputStream= part2.getInputStream()) { // save uploaded file
				Files.copy(inputStream, threshold_file.toPath());
			}
			transposeFile(y_file.getPath(),email_dir+"/Y_rightdim.txt");
		}catch(Exception e) {
			printERROR("Error Occurred while uploading XY data!!");
			e.printStackTrace();
		}
	}
	private void downloadPlink(HttpServletRequest request) {
		try {
			Part part_00 = request.getPart("BIM_file");
			Part part_01 = request.getPart("BED_file");
			Part part_02 = request.getPart("FAM_file");
			Part part_03 = request.getPart("EX_file");
			
			File file_0 = new File(email_dir+"/input_f.bim");
			File file_1 = new File(email_dir+"/input_f.bed");
			File file_2 = new File(email_dir+"/input_f.fam");
			File file_3 = new File(email_dir+"/input_y.txt");
			
			try (InputStream inputStream= part_00.getInputStream()) { // save uploaded file
				Files.copy(inputStream, file_0.toPath());
			}
			try (InputStream inputStream= part_01.getInputStream()) { // save uploaded file
				Files.copy(inputStream, file_1.toPath());
			}	
			try (InputStream inputStream= part_02.getInputStream()) { // save uploaded file
				Files.copy(inputStream, file_2.toPath());
			}
			try (InputStream inputStream= part_03.getInputStream()) { // save uploaded file
				Files.copy(inputStream, file_3.toPath());
			}
			Part part2 = request.getPart("Threshold_file");//Threshold_file
			threshold_file = new File(email_dir+"/threshold.txt");
			try (InputStream inputStream= part2.getInputStream()) { // save uploaded file
				Files.copy(inputStream, threshold_file.toPath());
			}
			
			Process plink_data = null;
			plink_data = Runtime.getRuntime().exec(Setup.PLINKdir+" --bfile " + email_dir + "/input_f --recodeA --noweb --maf 0.3 --out "+email_dir+"/input_x");
			plink_data.waitFor();
			
			Process pl_to_input = null; //x data
			pl_to_input = Runtime.getRuntime().exec(Setup.NICEdir+"/pl_to_input " + email_dir);
			pl_to_input.waitFor();
			
			Process pl_to_input_y = null; // y data
			pl_to_input_y = Runtime.getRuntime().exec(Setup.NICEdir+"pl_to_input_y " + email_dir);
			pl_to_input_y.waitFor();
			
			// X_rightdim -> X.txt Y_rightdim -> Y.txt
			transposeFile(email_dir+"/X_rightdim.txt",email_dir+"/X.txt");
			transposeFile(email_dir+"/Y_rightdim.txt",email_dir+"/Y.txt");			
		}
		catch(Exception e) {
			printERROR("Error Occurred while uploading// converting PLINK data!!");
			e.printStackTrace();
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
	public static void transposeFile(String inputFile, String outputFile) {
		try {
			Vector<String[]> vec = new Vector<>();
			
			FileReader fr = new FileReader(inputFile);
			BufferedReader br = new BufferedReader(fr);
			
			String ln = br.readLine();
			while(ln!= null) {
				String[] list = ln.split(" ");
				vec.add(list);
				ln = br.readLine();
			}
			br.close(); fr.close();
			
			int row = vec.size();
			int col = vec.get(0).length;
			String mat[][] = new String[row][col];
			for(int i =0;i<row;i++) {
				String[] tmp = vec.get(i);
				for(int j=0;j<col;j++) {
					mat[i][j] = tmp[j];
				}
			}
			vec.clear();
			
			FileWriter fw = new FileWriter(outputFile);
			BufferedWriter bw = new BufferedWriter(fw);
			for(int i =0;i<col;i++) {
				for(int j =0;j<row;j++) {
					bw.write(mat[j][i]+" ");
				}
				bw.write("\n");
			}
			bw.close(); fw.close();
			
		}catch(IOException e) {
			printERROR("Error while transposing "+inputFile+"!!!");
			e.printStackTrace();
		}
	}
}
