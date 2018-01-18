package docs;

import sub.ent.web.MainController;
import sub.ent.web.WebApplication;
import static docs.ConvenienceMethods.*;

import org.springframework.ui.Model;

public class MainDocumentation {

	protected void OVERVIEW() throws Exception {
		
		/* The entry point of the application is the */ class_(WebApplication.class); /*
		 * It has a main method that starts a Spring Boot application.
		 * 
		 * This Spring application searches for all Spring controllers on the classpath.
		 * The one that is found is the */ objectOf(MainController.class); /*
		 * It is responsible to process all web requests which are directed to the application. 
		 * 
		 * Methods:
		 * 
		 * */ objectOf(MainController.class).index(arg(Model.class)); /*
		 * 
		 * This method is called when the user navigates to the root path.
		 * 
		 * 
		 */
	}
}
