package sub.ent.web;

public class Environment {

	public String getVariable(String name) {
		String variable = System.getenv(name);
		if (variable == null) {
			throw new RuntimeException("Missing environment variable: " + name);
		}
		return variable;
	}

}
