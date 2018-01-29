package sub.ent.backend;

/**
 * Access to environment variables.
 *
 */
public class Environment {

	/**
	 * Gets an environment variable by name.
	 */
	public String getVariable(String name) {
		String variable = System.getenv(name);
		if (variable == null) {
			throw new RuntimeException("Missing environment variable: " + name);
		}
		return variable;
	}

}
