package Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import mail.GoogleAuthentication;


/**
 * Servlet implementation class VennServlet
 */
@WebServlet("/VennServlet")
@MultipartConfig(maxFileSize = -1, maxRequestSize = -1,location =Setup.FileSaveDirectory)
public class VennServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VennServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		RequestDispatcher view = request.getRequestDispatcher("Venn.jsp");
		view.forward(request, response);
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String input_type_str = request.getParameter("tabss");
		String emailAddress = request.getParameter("emailAddress");
		System.out.println(emailAddress);
		
		// Get session info
		Venn vn = new Venn(emailAddress,request);
		vn.downloadInputData(request, input_type_str);
		
		vn.run(input_type_str);
		sendResultMail(emailAddress, vn);

		RequestDispatcher view = request.getRequestDispatcher("Venn.jsp");
		view.forward(request, response);
	}
	public static void sendResultMail(String emailaddr, Venn vn) {
		//send e_mail
			try {
				java.util.Properties prop = System.getProperties();
				prop.put("mail.smtp.starttls.enable", "true");
				prop.put("mail.smtp.host", "smtp.gmail.com");
				prop.put("mail.smtp.auth", "true");
				prop.put("mail.smtp.port", "587");
				prop.put("mail.smtp.ssl.protocols", "TLSv1.2");
				
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
				if(vn.vennDiagram.exists()) {
					msg.setSubject(vn.vennDiagram.getName()+" complete");

					MimeMultipart mimeMultipart = new MimeMultipart();					
		            MimeBodyPart contentBodyPart = new MimeBodyPart();
		            contentBodyPart.setContent("Thanks you for using our Service.", "text/html;charset=UTF-8");
		            mimeMultipart.addBodyPart(contentBodyPart);
	
	                // MimeBodyPart 객체 생성 후 MimeMultipart에 담기 (첨부파일을 담기 위함)
	                MimeBodyPart fileBodyPart1 = new MimeBodyPart(); // plot
	                fileBodyPart1.attachFile(vn.vennDiagram.getAbsoluteFile()); //gene list
	                	                
	                mimeMultipart.addBodyPart(fileBodyPart1);
	                
	                msg.setContent(mimeMultipart);
	                msg.setSentDate(new java.util.Date());
//					msg.setContent("<a href = \"http://"+Setup.ipAddr+"/NICER/Download?file="+_tmp+file_name+"\">download_link</a>" 
//							+"<br>Working time is " +(end_start)/1000.0 + "Second.</br>", "text/html; charset=euc-kr");           
				}
				else {
					msg.setSubject("Draw Manhattan plot fail");
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
