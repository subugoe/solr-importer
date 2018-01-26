package sub.ent.backend;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import sub.ent.config.ConfigStrings;

public class BeanRetriever {

	public final static String DI_CONTEXT_IN_PLUGIN = "context.xml";
	public final static String DI_CONTEXT_DEFAULT = "context-default.xml";

	public ConfigStrings getConfig() {
		return (ConfigStrings) getBean("config");
	}

	public String getProjectDescription() {
		return getConfig().getDescription();
	}

	public Importer getImporter() {
		return (Importer) getBean("importer");
	}

	private Object getBean(String beanId) {
		String contextFile = null;
		boolean loadUserDefinedContext = getClass().getResource("/" + DI_CONTEXT_IN_PLUGIN) != null;
		if (loadUserDefinedContext) {
			contextFile = DI_CONTEXT_IN_PLUGIN;
		} else {
			contextFile = DI_CONTEXT_DEFAULT;
		}
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(contextFile);
		Object bean = ctx.getBean(beanId);
		ctx.close();
		return bean;
	}

	public String getProjectName() {
		return getConfig().getProjectName();
	}
}
