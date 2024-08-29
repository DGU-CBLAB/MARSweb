package Main;

import java.io.File;
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
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mail.GoogleAuthentication;
/**
 * Servlet implementation class eQTL
 */
@WebServlet("/eQTL")
@MultipartConfig(maxFileSize = -1, maxRequestSize = -1,location =Setup.FileSaveDirectory)
public class eQTLServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public eQTLServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		RequestDispatcher view = request.getRequestDispatcher("eQTL.html");
		view.forward(request, response);
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String input_type_str = request.getParameter("tabss");
		String emailAddress = request.getParameter("emailAddress");
		System.out.println(emailAddress);
		
		eQTL eq = new eQTL(emailAddress,request);
		eq.downloadInputData(request, input_type_str);

		eq.run(input_type_str);
		sendResultMail(emailAddress, eq);
		
		//Feed Client the main page 
		RequestDispatcher view = request.getRequestDispatcher("eQTL.jsp");
		view.forward(request, response);
//		response.sendRedirect("eQTL.jsp");
	}
	public static void sendResultMail(String emailaddr, eQTL eq) {
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
				if(eq.eqtlplot.exists()) {
					msg.setSubject(eq.eqtlplot.getName()+" complete");

					MimeMultipart mimeMultipart = new MimeMultipart();
		            MimeBodyPart contentBodyPart = new MimeBodyPart();
		            contentBodyPart.setContent("Thanks you for using our Service.", "text/html;charset=UTF-8");
		            mimeMultipart.addBodyPart(contentBodyPart);

		            // MimeBodyPart 객체 생성 후 MimeMultipart에 담기 (첨부파일을 담기 위함)
	                MimeBodyPart fileBodyPart1 = new MimeBodyPart();
	                fileBodyPart1.attachFile(eq.eqtlplot.getAbsoluteFile());
	                MimeBodyPart fileBodyPart2 = new MimeBodyPart();
	                fileBodyPart2.attachFile(eq.M_genelist.getAbsoluteFile());

	                mimeMultipart.addBodyPart(fileBodyPart1);
	                mimeMultipart.addBodyPart(fileBodyPart2);
	                
	                msg.setContent(mimeMultipart);
	                msg.setSentDate(new java.util.Date());
//					msg.setContent("<a href = \"http://"+Setup.ipAddr+"/NICER/Download?file="+_tmp+file_name+"\">download_link</a>" 
//							+"<br>Working time is " +(end_start)/1000.0 + "Second.</br>", "text/html; charset=euc-kr");
		            
				}
				else {
					msg.setSubject("Draw eQTL plot fail");
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
