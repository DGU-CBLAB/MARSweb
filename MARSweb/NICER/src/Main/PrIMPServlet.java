package Main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
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

import mail.GoogleAuthentication;

/**
 * Servlet implementation class PrIMPServlet
 */
@WebServlet("/PrIMPServlet")
@MultipartConfig(maxFileSize = -1, maxRequestSize = -1,location =Setup.FileSaveDirectory3)
public class PrIMPServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private long end_t = 0;
    private long start_t = 0;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PrIMPServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		RequestDispatcher view = request.getRequestDispatcher("PrIMP.html");
		view.forward(request, response);
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//String input_type_str = request.getParameter("tabss"); 1 type data
		String emailAddress = request.getParameter("emailAddress");
		
		start_t = System.currentTimeMillis();	// for calculate working time

		PrIMP pr = new PrIMP(emailAddress,request);
		
		if(pr.donwloadFasta(request)) {
			pr.run();	
			
			end_t = System.currentTimeMillis();		
			sendResultMail(pr.email_dir,"output.txt", emailAddress, end_t-start_t);

			RequestDispatcher view = request.getRequestDispatcher("PrIMP.jsp");
			view.forward(request, response);
		}
		else {
			response.setContentType("text/html; charset=UTF-8"); 
			PrintWriter writer = response.getWriter(); 
			writer.println("<script>alert('Incorrect File Type'); location.href='"+"PrIMP.html"+"';</script>"); 
			writer.close();
		}
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
				//String _tmp = file_dir.replace("/", Setup.urlencode);
				String _tmp = file_dir;
				
			    MimeBodyPart mbp = new MimeBodyPart();
			    Multipart mp = new MimeMultipart();
				
				if(res.exists()) {
					msg.setSubject(file_name+" complete");
					msg.setContent("<br>Working time is " +(end_start)/1000.0 + "Second.</br>", "text/html; charset=euc-kr");
					
					FileDataSource fds = new FileDataSource(res.getPath()); //파일 읽어오기
					mbp.setDataHandler(new DataHandler(fds));  	                    
					mbp.setFileName(fds.getName());
				    mp.addBodyPart(mbp);
				    
				    msg.setContent(mp);
				}
				else {
					msg.setSubject("PrIMP fail");
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
