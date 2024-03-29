package sub.ent.web;

import java.io.File;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import sub.ent.backend.FileAccess;
import sub.ent.backend.Importer;
import sub.ent.backend.LockFile;
import sub.ent.backend.LogAccess;
import sub.ent.backend.Mailer;
import sub.ent.backend.BeanRetriever;
import sub.ent.backend.Environment;
import sub.ent.backend.Timer;

/**
 * Runnable that starts the import process, normally in a thread.
 */
public class ImporterRunner implements Runnable {

	private Importer importer;
	private LogAccess logAccess = new LogAccess();
	private Environment env = new Environment();
	private LockFile lock = new LockFile();
	private FileAccess fileAccess = new FileAccess();
	private Timer timer = new Timer();
	private Mailer mailer = new Mailer();
	private String solrUrl;
	private String gitMessage;
	private String mailAddress;
	private static final String STOP_MESSAGE = "Import process was stopped manually.";

	/**
	 * Sets the URL of the running Solr server into which the data will be imported.
	 */
	public void setSolrUrl(String newUrl) {
		solrUrl = newUrl;
	}

	/**
	 * The contents of the current log file will be sent to this address (or addresses).
	 */
	public void setMailAddressToSendLog(String newAddress) {
		mailAddress = newAddress;
	}

	/**
	 * This is only used for logging.
	 */
	public void setGitMessage(String newMessage) {
		gitMessage = newMessage;
	}

	/**
	 * Starts the import.
	 */
	@Override
	public void run() {
		timer.setStart(new Date().getTime());
		logAccess.clear();
		PrintStream log = logAccess.getOutput();

		log.println("    Starting import (" + currentDate() + ")");
		log.println();
		log.println("    Git commit message: " + gitMessage);
		log.println("    Solr URL: " + solrUrl);
		log.println("    Import core: " + env.importCore());
		log.println("    Online core: " + env.onlineCore());
		log.println();
		BeanRetriever retriever = new BeanRetriever();
		try {
			importer = retriever.getImporter();
			importer.setLogOutput(log);
			Map<String, String> parametersForAllSteps = constructParams();
			for (int i = 0; i < importer.getNumberOfSteps(); i++) {
				importer.executeStep(i, parametersForAllSteps);
				checkIfContinue();
			}

		} catch (Exception e) {
			log.println();
			if ("sleep interrupted".equals(e.getMessage())) {
				log.println("ERROR: " + STOP_MESSAGE);
			} else {
				log.println("ERROR: " + e.getMessage());
			}
			e.printStackTrace();
		} finally {
			lock.delete();
			timer.setStop(new Date().getTime());
			log.println();
			log.println("    " + timer.getDurationMessage());
			log.close();
			String projectName = retriever.getProjectName();
			String mailSubject = projectName + ": Import endete mit dem Status " + logAccess.getStatusOfLastLog() + " (" + serverName() + ")";
			mailer = new Mailer();
			mailer.sendLog(mailAddress, mailSubject);
		}
	}

	private String currentDate() {
		DateFormat form = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.GERMANY);
		TimeZone timezone = TimeZone.getTimeZone("Europe/Berlin");
		form.setTimeZone(timezone);
		return form.format(new Date());
	}

	private void checkIfContinue() throws InterruptedException {
		if (Thread.currentThread().isInterrupted()) {
			throw new InterruptedException(STOP_MESSAGE);
		}
	}

	private Map<String, String> constructParams() {
		Map<String, String> params = new HashMap<>();
		params.put("gitDir", gitDir());
		params.put("solrXmlDir", solrXmlDir());
		params.put("solrUrl", solrUrl);
		params.put("solrImportCore", env.importCore());
		params.put("solrOnlineCore", env.onlineCore());
		params.put("solrUser", env.solrUser());
		params.put("solrPassword", env.solrPassword());
		return params;
	}

	private String gitDir() {
		File gitDir = new File(env.inputDir());
		return gitDir.getAbsolutePath();
	}

	private String solrXmlDir() {
		File outputDir = new File(env.outputDir());
		String solrXmlDir = new File(outputDir, "solrxml").getAbsolutePath();
		fileAccess.makeSureThatExists(new File(solrXmlDir));
		return solrXmlDir;
	}

	private String serverName() {
		String liveUrl = env.liveUrl();
		String stagingUrl = env.stagingUrl();
		if (solrUrl.equals(liveUrl)) {
			return "Live-Server";
		} else if (solrUrl.equals(stagingUrl)) {
			return "Staging-Server";
		} else {
			return "Unknown Server";
		}
	}

	/**
	 * Asks for descriptions of importer steps.
	 * Can be used in the frontend to list all the steps.
	 *
	 * @return List of all descriptions.
	 */
	public List<String> getAllStepDescriptions() {
		List<String> stepDescriptions = new ArrayList<>();
		if (importer != null) {
			stepDescriptions = importer.getAllStepDescriptions();
		}
		return stepDescriptions;
	}

	// for unit tests
	void setImporter(Importer newImporter) {
		importer = newImporter;
	}

	void setLogAccess(LogAccess newLog) {
		logAccess = newLog;
	}

	void setEnv(Environment newEnv) {
		env = newEnv;
	}

	void setLockFile(LockFile newLock) {
		lock = newLock;
	}

	void setFileAccess(FileAccess newAccess) {
		fileAccess = newAccess;
	}

	void setTimer(Timer newTimer) {
		timer = newTimer;
	}

	void setMailer(Mailer newMailer) {
		mailer = newMailer;
	}

}
