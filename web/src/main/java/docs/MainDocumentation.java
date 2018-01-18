package docs;

import sub.ent.backend.LockFile;
import sub.ent.web.MainController;
import sub.ent.web.WebApplication;
import static docs.ConvenienceMethods.*;

import org.springframework.ui.Model;

class MainDocumentation {

	protected void webEntryPoint() throws Exception {
		
		/* The entry point of the application is the */ class_(WebApplication.class); /*
		 * It has a main method that starts a Spring Boot application.
		 * 
		 * This Spring application searches for all Spring controllers on the classpath.
		 * The one that is found is the */ class_(MainController.class); /*
		 * It is responsible to process all web requests which are directed to the application. 
		 * 
		 * 
		 * Methods
		 * 
		 * */ objectOf(MainController.class).index(arg(Model.class)); /*
		 * 
		 * This method is called when the user navigates to the root path.
		 * It is responsible for preparing of all the necessary web attributes,
		 * for making sure that the git repository is up to date, 
		 * and in the end for calling the 'index.html' template.
		 * It also uses an */ objectOf(LockFile.class); /* to check if a lock file is present.
		 * If it is, it means that an import process is running.
		 * In that case, there will be a 'short circuit' and the 'started' template will be called instead.
		 * 
		 * */ objectOf(MainController.class).importIntoSolr(arg(Model.class), "string", "string"); /*
		 * 
		 * 
		 */
	}
}
