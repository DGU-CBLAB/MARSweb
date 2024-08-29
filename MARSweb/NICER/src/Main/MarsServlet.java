package Main;

import java.io.File;
import java.io.IOException;

import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mail.GoogleAuthentication;

/**
 * Servlet implementation class MarsServlet
 */
@WebServlet("/MarsServlet")
@MultipartConfig(maxFileSize = -1, maxRequestSize = -1,location =Setup.FileSaveDirectory4)
public class MarsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private long end_t = 0;
    private long start_t = 0;
    
    /**
     * Default constructor. 
     */
    public MarsServlet() {
    	super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		RequestDispatcher view = request.getRequestDispatcher("Mars.jsp");
		view.forward(request, response);
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub

//		while (params.hasMoreElements()){
//		    String name = (String)params.nextElement();
//		    System.out.println(name + " : " +request.getParameter(name));
//		}
		String input_type_str = request.getParameter("tabss");
		String emailAddress = request.getParameter("emailAddress");
		start_t = System.currentTimeMillis();	// for calculate working time
		System.out.println(input_type_str);
		System.out.println(emailAddress);
		
		Mars mars = new Mars(emailAddress,request);
		mars.downloadInputData(request, input_type_str);
		mars.run("15", input_type_str);
		/*String someMessage = "Upload Complete!! Please Wait for Email Notification\n";
		PrintWriter out = response.getWriter();

		out.print("<html><head>");
		out.print("<script type=\"text/javascript\">alert(" + someMessage + ");</script>");
		out.print("</head><body></body></html>");
		
		/** Running NICE **/
//		mars.run(thr_num);
		end_t = System.currentTimeMillis();
		System.out.println("Working time is " +(end_t-start_t)/1000.0 + "Second");
		sendResultMail(mars.email_dir,"result.txt", emailAddress, end_t-start_t);
		
		//Feed Client the main page 
		RequestDispatcher view = request.getRequestDispatcher("index.html");
		view.forward(request, response);
		//response.sendRedirect("index.html");
	}
	public static void sendResultMail(String file_dir,String file_name, String emailaddr, long end_start) {
		//send e_mail
			try {
				java.util.Properties prop = System.getProperties();
				prop.put("mail.smtp.host", "smtp.gmail.com");
				prop.put("mail.smtp.auth", "true");
				prop.put("mail.smtp.port", "587");
				prop.put("mail.smtp.starttls.enable", "true");
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
				File res = new File(file_dir+file_name);
				String _tmp = file_dir.replace("/", Setup.urlencode);
				if(res.exists()) {
					msg.setSubject("MarsNet complete");
					msg.setContent("<a href = \"http://"+Setup.ipAddr+"Download?file="+_tmp+file_name+"\">download_link</a>" 
							+"<br>Working time is " +(end_start)/1000.0 + "Second.</br>", "text/html; charset=euc-kr");
				
				}
				else {
					msg.setSubject("Mars fail");
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
