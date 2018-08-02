package sub.ent.backend;

/**
 * Access to environment variables.
 *
 */
public class Environment {

	/**
	 * Gets an environment variable by name.
	 */
	private String getVariable(String name) {
		String variable = System.getenv(name);
		if (variable == null) {
			throw new RuntimeException("Missing environment variable: " + name);
		}
		return variable;
	}

	public String stagingUrl() {
		return getVariable("SOLR_STAGING_URL");
	}
	public String liveUrl() {
		return getVariable("SOLR_LIVE_URL");
	}
	public String importCore() {
		return getVariable("SOLR_IMPORT_CORE");
	}
	public String onlineCore() {
		return getVariable("SOLR_ONLINE_CORE");
	}
	public String gitUrl() {
		return getVariable("GIT_URL");
	}
	public String gitUser() {
		return getVariable("GIT_USER");
	}
	public String gitPassword() {
		return getVariable("GIT_PASSWORD");
	}
	public String inputDir() {
		return getVariable("GIT_INPUT_DIR");
	}
	public String outputDir() {
		return getVariable("OUTPUT_DIR");
	}
	public String mailHost() {
		return getVariable("MAIL_HOST");
	}
	public String mailUser() {
		return getVariable("MAIL_USER");
	}
	public String mailPassword() {
		return getVariable("MAIL_PASSWORD");
	}

}
