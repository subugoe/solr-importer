package sub.ent.web;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import sub.ent.backend.BeanRetriever;
import sub.ent.backend.CoreSwapper;
import sub.ent.backend.Environment;
import sub.ent.backend.GitWrapper;
import sub.ent.backend.LockFile;
import sub.ent.backend.LogAccess;

/**
 * Responder to all the user requests from the frontend.
 */
@Controller
public class MainController {

	private GitWrapper git = new GitWrapper();
	private Environment env = new Environment();
	private LogAccess logAccess = new LogAccess();
	private LockFile lock = new LockFile();
	private ImporterRunner runner = new ImporterRunner();
	private CoreSwapper swapper = new CoreSwapper();
	private BeanRetriever beanRetriever = new BeanRetriever();
	private UrlGetter urlGetter = new UrlGetter();
	private String lastMessage = "";

	@RequestMapping(method = RequestMethod.GET, value = "/test2")
	@ResponseBody
	public ResponseEntity<?> getTest() {
		return ResponseEntity.ok().body("some test");
	}

	/**
	 * Processes the user request to the root of the application.
	 * 
	 * @return The name of the template to be shown in the frontend.
	 */
	@RequestMapping(value = "/")
	public String index(Model model) throws Exception {

		model.addAttribute("SOLR_STAGING_URL", stagingUrl());
		model.addAttribute("SOLR_LIVE_URL", liveUrl());
		model.addAttribute("GIT_URL", gitUrl());
		model.addAttribute("previousCoreDate", coreInfo(liveUrl(), importCore()));
		model.addAttribute("currentCoreDate", coreInfo(liveUrl(), onlineCore()));
		model.addAttribute("currentCoreDateStaging", coreInfo(stagingUrl(), onlineCore()));
		model.addAttribute("log", logAccess.getLogContents());
		model.addAttribute("headerText", beanRetriever.getProjectDescription());
		if (lock.exists()) {
			return "started";
		}

		try {
			git.init();
			git.pull();
			lastMessage = git.getLastCommitMessage();
		} catch (Exception e) {
			e.printStackTrace();
			lastMessage = "Fehler: " + e.getMessage();
		}
		model.addAttribute("commitMessage", lastMessage);

		return "index";
	}

	private String stagingUrl() {
		return env.getVariable("SOLR_STAGING_URL");
	}

	private String liveUrl() {
		return env.getVariable("SOLR_LIVE_URL");
	}

	private String gitUrl() {
		return env.getVariable("GIT_URL");
	}

	private String importCore() {
		return env.getVariable("SOLR_IMPORT_CORE");
	}

	private String onlineCore() {
		return env.getVariable("SOLR_ONLINE_CORE");
	}

	private String coreInfo(String solrUrl, String core) {
		swapper.setSolrEndpoint(solrUrl, core);
		String coreDate = null;
		try {
			coreDate = swapper.getCoreDate();
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
			coreDate = "unbekannt";
		}
		return coreDate;
	}

	/**
	 * Processes the user request to start the import.
	 * 
	 * @return The name of the template to be shown in the frontend.
	 */
	@RequestMapping(value = "/import")
	public String importIntoSolr(Model model, @ModelAttribute("solrurl") String solrUrl,
			@ModelAttribute("mailaddress") String mailAddress) throws IOException {
		if (lock.exists()) {
			model.addAttribute("log", logAccess.getLogContents());
			return "started";
		}
		runner.setSolrUrl(solrUrl);
		runner.setMailAddressToSendLog(mailAddress);
		runner.setGitMessage(lastMessage);
		RunningThread.instance = new Thread(runner);
		RunningThread.instance.start();
		lock.create();
		model.addAttribute("headerText", beanRetriever.getProjectDescription());
		model.addAttribute("restartUrl", urlGetter.getCurrentUrl() + "/restart");
		return "started";
	}

	/**
	 * Swaps the live and the offline Solr cores.
	 * 
	 * @return Redirect to the root path.
	 */
	@RequestMapping(value = "/swapcores")
	public RedirectView swapCores(Model model) throws Exception {
		swapper.setSolrEndpoint(liveUrl(), onlineCore());
		swapper.switchTo(importCore());
		return new RedirectView("/");
	}

	/**
	 * Stops the import process.
	 * 
	 * @return The name of the template to be shown in the frontend.
	 */
	@RequestMapping(value = "/cancel")
	public String cancelImport(Model model) throws IOException {
		if (RunningThread.instance != null) {
			RunningThread.instance.interrupt();
		}
		model.addAttribute("stepDescriptions", runner.getAllStepDescriptions());
		model.addAttribute("headerText", beanRetriever.getProjectDescription());
		return "stopped";
	}

	/**
	 * Allows the import to be started again.
	 * Useful when the lock file wasn't deleted automatically because of an error.
	 * 
	 * @return Redirect to the root path.
	 */
	@RequestMapping(value = "/restart")
	public RedirectView deleteLockFile(Model model) throws Exception {
		lock.delete();
		return new RedirectView("/");
	}

	// for unit testing
	void setGit(GitWrapper newGit) {
		git = newGit;
	}

	void setLogAccess(LogAccess newLogAccess) {
		logAccess = newLogAccess;
	}

	void setLock(LockFile newLock) {
		lock = newLock;
	}

	void setImporterRunner(ImporterRunner newRunner) {
		runner = newRunner;
	}

	void setEnvironment(Environment newEnv) {
		env = newEnv;
	}

	void setCoreSwapper(CoreSwapper newSwapper) {
		swapper = newSwapper;
	}

	void setBeanRetriever(BeanRetriever newRetriever) {
		beanRetriever = newRetriever;
	}

	void setUrlGetter(UrlGetter newGetter) {
		urlGetter = newGetter;
	}
}
