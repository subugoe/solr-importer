package sub.ent.backend;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import sub.ent.config.ConfigStrings;

/**
 * Wrapper for Dependency Injection functionality.
 *
 */
public class BeanRetriever {

	public final static String DI_FILE_IN_PLUGIN = "context.xml";
	public final static String DI_FILE_DEFAULT = "context-default.xml";

	private ConfigStrings getConfig() {
		return (ConfigStrings) getBean("config");
	}

	/**
	 * Looks up the description of the current project (generic or specific).
	 */
	public String getProjectDescription() {
		return getConfig().getDescription();
	}

	/**
	 * Loads the importer from the DI context.
	 */
	public Importer getImporter() {
		return (Importer) getBean("importer");
	}

	private Object getBean(String beanId) {
		String contextFile = null;
		boolean loadUserDefinedContext = getClass().getResource("/" + DI_FILE_IN_PLUGIN) != null;
		if (loadUserDefinedContext) {
			contextFile = DI_FILE_IN_PLUGIN;
		} else {
			contextFile = DI_FILE_DEFAULT;
		}
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(contextFile);
		Object bean = ctx.getBean(beanId);
		ctx.close();
		return bean;
	}

	/**
	 * Looks up the name of the current project (generic or specific).
	 */
	public String getProjectName() {
		return getConfig().getProjectName();
	}
}
