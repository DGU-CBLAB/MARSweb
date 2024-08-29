package Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import mail.GoogleAuthentication;

/**
 * Servlet implementation class MultiTransServlet
 */



@WebServlet("/MultiTransServlet")
@MultipartConfig(maxFileSize = -1, maxRequestSize = -1,location =Setup.FileSaveDirectory2)
public class MultiTransServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private long end_t = 0;
    private long start_t = 0;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MultiTransServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		RequestDispatcher view = request.getRequestDispatcher("MultiTrans.html");
		view.forward(request, response);
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		String emailAddress = request.getParameter("emailAddress");
//		String windowsize = request.getParameter("windowsize");
//		String sampling = request.getParameter("sampling");
		
//		// Get Current Time
//		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");//dd/MM/yyyy
//		Date now = new Date();
//		String strDate = sdfDate.format(now);
//		
//		// Make Directory
//		String userDirString = Setup.FileSaveDirectory2+emailAddress+"/";
//		
//		File userDir=new File(userDirString);    
//		System.out.println(userDirString);
//			
//		if(!userDir.exists() && userDir.mkdir()) {			//this part need Optimize
//			Process f_chm = null;
//			try {
//				if(f_chm == null) {
//					f_chm = Runtime.getRuntime().exec("chmod -R 777 "+ userDirString);
//					
//					f_chm.waitFor();
//				}
//			}
//			catch(Exception e) {
//				e.printStackTrace();
//			} 
//		}
//		userDirString += strDate;
//		userDir = new File(userDirString);
//		if(!userDir.exists() && userDir.mkdir()) {			//this part need Optimize
//			Process f_chm = null;
//			try {
//				if(f_chm == null) {
//					f_chm = Runtime.getRuntime().exec("chmod -R 777 "+ userDirString);
//					
//					f_chm.waitFor();
//				}
//			}
//			catch(Exception e) {
//				e.printStackTrace();
//			} 
//		}		
//		// Upload SNP file
//		Part part = request.getPart("SNPfile");
//		File file = new File(userDirString+"/X.txt");
//		try (InputStream inputStream= part.getInputStream()) { // save uploaded file
//			Files.copy(inputStream, file.toPath());
//		}
//		
//		// Upload Phenotype file
//		Part part1 = request.getPart("Phenotypefile");
//		File file1 = new File(userDirString+"/Y.txt");
//		try (InputStream inputStream= part1.getInputStream()) { // save uploaded file
//			Files.copy(inputStream, file1.toPath());
//		}
//		
//		// Upload Phenotype file
//		Part part2 = request.getPart("Thresholdfile");
//		File file2 = new File(userDirString+"/threshold.txt");
//		try (InputStream inputStream= part2.getInputStream()) { // save uploaded file
//				Files.copy(inputStream, file2.toPath());
//		}
//		
//		//doGet(request, response);

	
//		try {
//			Process proc = Runtime.getRuntime().exec(
//						Setup.MultiTransdir+"mtcvpp.sh "
//						+userDirString + "/X.txt "
//						+userDirString + "/Y.txt "
//						+windowsize + " " + sampling + " "
//						+userDirString + "/threshold.txt "
//						+userDirString + "/result "
//						+userDirString + "/c.txt "
//						+userDirString + "/r.txt");
//				proc.waitFor();
//				System.out.println(	Setup.MultiTransdir+"mtcvpp.sh "
//						+userDirString + "/X.txt "
//						+userDirString + "/Y.txt "
//						+windowsize + " " + sampling + " "
//						+userDirString + "/threshold.txt "
//						+userDirString + "/result "
//						+userDirString + "/c.txt "
//						+userDirString + "/r.txt");				
//				System.out.println("Multitrans finish");
//			}catch(Exception e) {
//				System.err.println("Error occured");
//				e.printStackTrace();
//		}		
		String input_type_str = request.getParameter("tabss");
		String emailAddress = request.getParameter("emailAddress");
		//String thr_num = request.getParameter("set_num");
		start_t = System.currentTimeMillis();	// for calculate working time

		MultiTrans Mul = new MultiTrans(emailAddress,request);
		Mul.downloadInputData(request, input_type_str);
		
		/*String someMessage = "Upload Complete!! Please Wait for Email Notification\n";
		PrintWriter out = response.getWriter();
		out.print("<html><head>");
		out.print("<script type=\"text/javascript\">alert(" + someMessage + ");</script>");
		out.print("</head><body></body></html>");

		
		/** Running NICE **/
		Mul.run();
		end_t = System.currentTimeMillis();
		sendResultMail(Mul.email_dir + "/result/","MultiTrans.txt", emailAddress, end_t-start_t);
//		sendResultMail(nice.email_dir,"NICE.txt", emailAddress, end_t-start_t);

		RequestDispatcher view = request.getRequestDispatcher("index.html");
		view.forward(request, response);
	}
	public static void sendResultMail(String file_dir,String file_name, String emailaddr, long end_start) {
		//send e_mail
			try {
				java.util.Properties prop = System.getProperties();
				prop.put("mail.smtp.starttls.enable", "true");
				prop.put("mail.smtp.host", "smtp.gmail.com");
				prop.put("mail.smtp.auth", "true");
				prop.put("mail.smtp.port", "587");
				
				Authenticator auth = new GoogleAuthentication();
				Session s;
				try {
					s = Session.getDefaultInstance(prop, auth);
				}catch(Exception e) {
					s = Session.getInstance(prop,auth);
				}
				
				Message msg = new MimeMessage(s);
				Address sender_address = new InternetAddress("taegun89@gmail.com");
				Address receiver_address = new InternetAddress(emailaddr);
		
				msg.setHeader("content-type", "text/html;charset=euc-kr");
				msg.setFrom(sender_address);
				msg.addRecipient(Message.RecipientType.TO, receiver_address);
				File res = new File(file_dir+file_name);
				String _tmp = file_dir.replace("/", Setup.urlencode);
				if(res.exists()) {
					msg.setSubject(file_name+" complete");
					msg.setContent("<a href = \"http://"+Setup.ipAddr+"/NICER/Download?file="+_tmp+file_name+"\">download_link</a>" 
							+"<br>Working time is " +(end_start)/1000.0 + "Second.</br>", "text/html; charset=euc-kr");
				
				}
				else {
					msg.setSubject("MultiTrans fail");
					msg.setContent("Please send us an e-mail for further explanations.", "text/html; charset=euc-kr");
							
				}		  
				
				Transport.send(msg);
				prop.clear();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
	}
}
