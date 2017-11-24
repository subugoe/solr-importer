package sub.ent.backend;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import sub.ent.config.ConfigStrings;

public class BeanRetriever {

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
		boolean loadUserDefinedContext = getClass().getResource("/context.xml") != null;
		if (loadUserDefinedContext) {
			contextFile = "context.xml";
		} else {
			contextFile = "context-default.xml";
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
