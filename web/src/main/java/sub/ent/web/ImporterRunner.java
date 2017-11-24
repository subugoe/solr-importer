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
import sub.ent.backend.BeanRetriever;
import sub.ent.backend.Timer;

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

	public void setSolrUrl(String newUrl) {
		solrUrl = newUrl;
	}

	public void setMailAddressToSendLog(String newAddress) {
		mailAddress = newAddress;
	}

	public void setGitMessage(String newMessage) {
		gitMessage = newMessage;
	}

	@Override
	public void run() {
		timer.setStart(new Date().getTime());
		logAccess.clear();
		PrintStream log = logAccess.getOutput();

		log.println("    Starting import (" + currentDate() + ")");
		log.println();
		log.println("    Git commit message: " + gitMessage);
		log.println("    Solr URL: " + solrUrl());
		log.println("    Import core: " + solrImportCore());
		log.println("    Online core: " + solrOnlineCore());
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
		params.put("solrUrl", solrUrl());
		params.put("solrImportCore", solrImportCore());
		params.put("solrOnlineCore", solrOnlineCore());
		return params;
	}

	private String gitDir() {
		File gitDir = new File(env.getVariable("GIT_DIR"));
		return gitDir.getAbsolutePath();
	}

	private String solrXmlDir() {
		File outputDir = new File(env.getVariable("OUTPUT_DIR"));
		String solrXmlDir = new File(outputDir, "solrxml").getAbsolutePath();
		fileAccess.makeSureThatExists(new File(solrXmlDir));
		return solrXmlDir;
	}

	private String solrUrl() {
		return solrUrl;
	}

	private String serverName() {
		String liveUrl = env.getVariable("SOLR_LIVE_URL");
		String stagingUrl = env.getVariable("SOLR_STAGING_URL");
		if (solrUrl.equals(liveUrl)) {
			return "Live-Server";
		} else if (solrUrl.equals(stagingUrl)) {
			return "Staging-Server";
		} else {
			return "Unknown Server";
		}
	}

	private String solrImportCore() {
		return env.getVariable("SOLR_IMPORT_CORE");
	}

	private String solrOnlineCore() {
		return env.getVariable("SOLR_ONLINE_CORE");
	}

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
