package Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

@MultipartConfig(maxFileSize = -1, maxRequestSize = -1,location =Setup.FileSaveDirectory5)
public class PRS {
	private File x_file; // SNP file
	public String email_dir;
	public String images_dir;
	public String current_images;
	
	PRS(String emailaddr, HttpServletRequest request){
		// create directory
		email_dir = Setup.FileSaveDirectory5 + emailaddr+"/";
		images_dir = Setup.PRSimage_save + emailaddr+"/";
		current_images = emailaddr +"/";
		File userDir = new File(email_dir);
		File imageDir = new File(images_dir);
		
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
		if(!imageDir.exists() && imageDir.mkdir()) {			//this part need Optimize
			Process f_chm = null;
			try {
				if(f_chm == null) {
					f_chm = Runtime.getRuntime().exec("chmod 777 "+ images_dir);					
					f_chm.waitFor();
				}						
			}
			catch(Exception e) {
				e.printStackTrace();
			} 
		}
		email_dir += getCurrentStrDate()+"/";// append date within email address 
		images_dir += getCurrentStrDate()+"/";// append date within email address 
		current_images += getCurrentStrDate()+"/";
//		email_dir += "2019-04-07_13-17-53/";
		userDir = new File(email_dir);
		userDir.mkdir();
		imageDir = new File(images_dir);
		imageDir.mkdir();
	}
			
	public void run(List<String> imageUrls) {
		try { // divde input type later ... 2024-08-21 taegun
			System.out.println(Setup.PRSpython+" " + Setup.PRSmodel+ "/PRS_code.py " + " --age 30"+" --input " + x_file.getPath() + " --output " + images_dir);
			Process proc = Runtime.getRuntime().exec(Setup.PRSpython+" " + Setup.PRSmodel+ "/PRS_code.py " + " --age 30"+" --input " + x_file.getPath() + " --output " + images_dir);
			//proc.getErrorStream().close();
			//proc.getInputStream().close();
			//proc.getOutputStream().close();
			proc.waitFor();					
			System.out.println("run end");
			imageUrls = getImageUrls(images_dir);
			
		}catch(Exception e) {
			printERROR("Error Occured while uploading set File!!");
			e.printStackTrace();
		}
		return; // end of running NICE
	}
	private void downloadData(HttpServletRequest request) {
		// Upload SNP file	
		try {
			Part part = request.getPart("score_file");
			File x = new File(email_dir+ getFileName(part));
			try (InputStream inputStream= part.getInputStream()) { // save uploaded file
				Files.copy(inputStream, x.toPath());
				x_file = new File(email_dir + x.getName());
			}		
		}catch(Exception e) {
			printERROR("Error Occured while uploading set File!!");
			e.printStackTrace();
		}
	}
	public void downloadInputData(HttpServletRequest request, String input_type_str) {
		 if(input_type_str.equals("type1")) {
			 downloadData(request);
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
    private List<String> getImageUrls(String directoryPath) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        Path dirPath = Paths.get(directoryPath);

        // 디렉토리 내의 파일을 순회하며 *_result.png 파일을 찾음
        DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*_result.png");
        for (Path entry : stream) {
            // 웹 애플리케이션의 컨텍스트 루트에 따라 URL 경로를 설정
            String url = "/images/" + current_images +  entry.getFileName().toString(); // 실제 URL 경로로 변경 필요
            System.out.println(url);
            imageUrls.add(url);
        }

        return imageUrls;
    }
}
