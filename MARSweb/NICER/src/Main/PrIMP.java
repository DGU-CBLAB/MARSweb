package Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

public class PrIMP {
	private File fasta_file;
	public String email_dir;
	
	PrIMP(String emailaddr,HttpServletRequest request){
		email_dir = Setup.FileSaveDirectory3 + emailaddr +"/";
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

	public boolean donwloadFasta(HttpServletRequest request) {
		try {
			Part part = request.getPart("Fasta_file");
			fasta_file = new File(email_dir+"/target_file.fasta");
			try (InputStream inputStream = part.getInputStream()) { // save uploaded file
				
				BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
				String line; String temp_sequence = "";
				boolean sequence_check = false; boolean sp_check = false;
				while((line = rd.readLine()) != null) {
					if(Character.compare(line.charAt(0), '>') == 0) { // in case sp name line >sp|??|???
						if(!sp_check) { // start line
							sp_check = true;
						}
						else if(sp_check && !sequence_check) {
							System.out.println(line + "'s sequence is empty");
							return false;							
						}
						else if(sp_check && sequence_check) {
							if(temp_sequence.length() > 300) {
								System.out.println("line length over 300");
								return false;
							}
							else if(temp_sequence.length() < 8) {
								System.out.println(temp_sequence);
								System.out.println("line length under 8");
								return false;							
							}
							if(!check_sequence(temp_sequence)) {
								return false;
							}
							temp_sequence = ""; // temp_sequence reset
							sequence_check = false;
						}
					}
					else if(sp_check) {
						temp_sequence += line.trim();
						sequence_check = true;
					}
					else { // in case sequence 1. length() <= 300
						System.out.println("sp name must be first line");
						return false;
					}
				}
				Files.copy(part.getInputStream(), fasta_file.toPath());
				rd.close();	

			}		
		}catch(Exception e) {
			printERROR("unknown error");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public void run() {
		System.out.println(
				Setup.PrIMPdir+"PrIMP/bin/python3 "
				+ Setup.PrIMPdir + "PrIMP.py "
				+ "--fasta " + fasta_file.toPath() + " --output "
				+ email_dir + "output.txt");
		try {
			Process proc = Runtime.getRuntime().exec(
					Setup.PrIMPdir+"PrIMP/bin/python3 "
					+ Setup.PrIMPdir + "PrIMP.py "
					+ "--fasta " + fasta_file.toPath() + " --output "
					+ email_dir + "output.txt");
			proc.waitFor();
			System.out.println("PrIMP finish");
		}catch(Exception e) {
			System.err.println("Error occured");
			e.printStackTrace();
	}		
}
	
	
	private static void printERROR(String err_msg) {
		System.err.print(err_msg+"\n");
	}
	private String getCurrentStrDate() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");//dd/MM/yyyy
		Date now = new Date();
		return sdfDate.format(now);
	}
	private boolean check_sequence(String line) {
		char check_char[] = {'g','k','l','a','i','v','s','c','f','t','n','p','r','e','d','h','q','m','y','w','x','u'};
		String temp = line.toLowerCase().trim();
		boolean s_checker = false;
		for(int i = 0; i < temp.length(); i++) {
			for(int j = 0; j < check_char.length; j++) {
				if(Character.compare(temp.charAt(i), check_char[j]) == 0) {
					s_checker = true;
					break;
				}
			}
			if(!s_checker) {
				System.out.println("unexpected character in squence");
				return false;
			}
			s_checker = false;
		}
		return true;
	}
}
