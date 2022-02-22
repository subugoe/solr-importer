package sub.ent.backend;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

/**
 * Sender of the text from the log file.
 *
 */
public class Mailer {

	private final LogAccess logAccess = new LogAccess();
	private final Email email = new SimpleEmail();
	private final Environment env = new Environment();

	/**
	 * Sends one or several e-mails containing the text of the log file.
	 * @param mailAddress Can contain several comma-separated addresses.
	 */
	public void sendLog(String mailAddress, String mailSubject) {
		try {
			String user = env.mailUser();
			String password = env.mailPassword();
			String str_port = env.mailPort();
			if (notEmpty(user) && notEmpty(password)) {
				email.setAuthentication(user, password);
			}
			email.setHostName(env.mailHost());
			int port = 587;
			if(notEmpty(str_port)) {
				try {
					port = Integer.parseInt(str_port);
				} catch (NumberFormatException ignored) {
				}
			}
			email.setSmtpPort(port);
			email.setFrom(env.mailFrom());
			email.setDebug(false);
			email.setSubject(mailSubject);
			email.setMsg(logAccess.getLogContents());
			String[] addresses = mailAddress.split("[,;]");
			for (String address : addresses) {
				email.addTo(address);
			}
			email.send();
		} catch (EmailException e) {
			System.err.println("WARNING Could not send mail to '" + mailAddress + "' (" + e.getMessage() + ")");
		}
	}

	private boolean notEmpty(String s) {
		return s != null && !s.isEmpty();
	}

}
