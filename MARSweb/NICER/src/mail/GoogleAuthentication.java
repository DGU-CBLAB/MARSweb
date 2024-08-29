package mail;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.util.Map;

public class GoogleAuthentication extends Authenticator{
	PasswordAuthentication passAuth;
	final String username = System.getenv("MARSweb_username");
    final String password = System.getenv("MARSweb_passwd");
    Map<String, String> env = System.getenv();
	public GoogleAuthentication() {
		System.out.println(username);
		System.out.println(password);
        		
		passAuth = new PasswordAuthentication(username, password); // input gmail id and passwd
	}
	
	public PasswordAuthentication getPasswordAuthentication() {
		return passAuth;
	}
}