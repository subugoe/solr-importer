package sub.ent.backend;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ImporterFactory {

	public Importer getImporter() {
		String contextFile = null;
		boolean loadUserDefinedContext = getClass().getResource("/context.xml") != null;
		if (loadUserDefinedContext) {
			contextFile = "context.xml";
		} else {
			contextFile = "context-default.xml";
		}
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(contextFile);
		Importer importer = ctx.getBean("importer", Importer.class);
		ctx.close();
		return importer;
	}
}
