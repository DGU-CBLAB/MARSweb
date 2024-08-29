package Main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
/**
 * Servlet implementation class PRSServlet
 */
@WebServlet("/PRSServlet")
@MultipartConfig(maxFileSize = -1, maxRequestSize = -1,location =Setup.FileSaveDirectory5)
public class PRSServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PRSServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

		String input_type_str = request.getParameter("tabss");
		String emailAddress = "testemail";
		String age = request.getParameter("age"); //set the number of thread
		String imagepath = "";

		List<String> imageUrls = new ArrayList<>();
		
		PRS prs = new PRS(emailAddress,request);
		prs.downloadInputData(request, input_type_str);

		prs.run(imageUrls);
		
        Gson gson = new Gson();
        JsonObject jsonResponse = new JsonObject();
        JsonArray jsonImageUrls = gson.toJsonTree(imageUrls).getAsJsonArray();
        jsonResponse.add("imageUrls", jsonImageUrls);

        // JSON 응답 전송
        System.out.println(jsonImageUrls);
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(jsonResponse));        
        out.flush();
//        // 응답 생성
//        PrintWriter out = response.getWriter();
//        out.write(currentTime);
//        out.flush();

	}
	public static void printERROR(String err_msg) {
		System.err.print(err_msg+"\n");
	}
	

}
