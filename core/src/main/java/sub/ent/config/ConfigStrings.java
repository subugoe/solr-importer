package sub.ent.config;

/**
 * Some configuration values that are injected at runtime by the DI container.
 *
 */
public class ConfigStrings {

	private String description;
	private String projectName;

	public void setDescription(String newDescription) {
		description = newDescription;
	}

	public String getDescription() {
		return description;
	}

	public void setProjectName(String newProjectName) {
		projectName = newProjectName;
	}

	public String getProjectName() {
		return projectName;
	}
}
