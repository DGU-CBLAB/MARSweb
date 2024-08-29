package Main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Servlet implementation class GAMMAServlet
 */
@WebServlet("/GAMMAServlet")
@MultipartConfig(maxFileSize = -1, maxRequestSize = -1,location =Setup.FileSaveDirectory)
public class GAMMAServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GAMMAServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		RequestDispatcher view = request.getRequestDispatcher("GAMMA.html");
		view.forward(request, response);
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String emailAddress = request.getParameter("emailAddress");
		String NumSNPs = request.getParameter("NumSNPs");
		System.out.println(emailAddress);
		System.out.println(NumSNPs);
		
		// Get Current Time
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");//dd/MM/yyyy
		Date now = new Date();
		String strDate = sdfDate.format(now);
		
		// Make Directory
		String userDirString = Setup.FileSaveDirectory+emailAddress+"\\";
		
		File userDir=new File(userDirString);    
		System.out.println(userDirString);
		if(userDir.mkdir()) {
			userDirString += strDate+"\\";
			userDir = new File(userDirString);
			if(userDir.mkdir())
				System.out.println("Directory Made");
		}else{
			userDirString += strDate+"\\";
			userDir = new File(userDirString);
			if(userDir.mkdir())
				System.out.println("Directory Made");
		}
		
		// Upload SNP file
		Part part = request.getPart("SNPfile");
		File file = new File(userDirString+"\\X.txt");
		try (InputStream inputStream= part.getInputStream()) { // save uploaded file
			Files.copy(inputStream, file.toPath());
		}
		
		// Upload Phenotype file
		Part part1 = request.getPart("Phenotypefile");
		File file1 = new File(userDirString+"\\Y.txt");
		try (InputStream inputStream= part1.getInputStream()) { // save uploaded file
			Files.copy(inputStream, file1.toPath());
		}
		
		/** 여기에 GAMMA Instruction을 실행하는 서블릿으로 연결하면 됩니다. **/
		RequestDispatcher view = request.getRequestDispatcher("index.html");
		view.forward(request, response);
		//doGet(request, response);
	}
}
