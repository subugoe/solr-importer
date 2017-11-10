package sub.ent.backend;

import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ImporterFactory {

	public Importer getImporter() {
		Importer importer = null;
		ClassPathXmlApplicationContext ctx = null;
		try {
			ctx = new ClassPathXmlApplicationContext("context.xml");
		} catch (BeansException e) {
			System.out.println("Beanzzzzzzzzzzz");
		}
		if (ctx == null) {
			ctx = new ClassPathXmlApplicationContext("context-default.xml");
		}
		importer = ctx.getBean("importer", Importer.class);
		ctx.close();
		return importer;
	}
}
