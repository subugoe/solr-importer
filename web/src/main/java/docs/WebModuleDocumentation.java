package docs;

import sub.ent.api.ImporterStep;
import sub.ent.api.ImporterStepCoreSwap;
import sub.ent.backend.CoreSwapper;
import sub.ent.backend.LockFile;
import sub.ent.backend.LogAccess;
import sub.ent.web.ImporterRunner;
import sub.ent.web.MainController;
import sub.ent.web.RunningThread;
import sub.ent.web.WebApplication;
import static docs.ConvenienceMethods.*;

import org.springframework.ui.Model;

class WebModuleDocumentation {

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
		 * An */ objectOf(CoreSwapper.class); /* is used to get information about the Solr cores.
		 * It also uses an */ objectOf(LockFile.class); /* to check if a lock file is present.
		 * If it is, it means that an import process is running.
		 * In that case, there will be a 'short circuit' and the 'started' template will be called instead.
		 * 
		 * */ objectOf(MainController.class).importIntoSolr(arg(Model.class), "string", "string"); /*
		 * 
		 * This one is called as soon as the user starts the import process by clicking a button.
		 * The strings are user inputs or selections from the frontend, which will be used for the import.
		 * Here again, there is a 'short circuit' if the */ objectOf(LockFile.class); /* tells that the 
		 * import process is already running.
		 * This is a precaution to prevent the import to be started more than once at the same time.
		 * In that case, nothing is done besides reading in the current logs with an */ objectOf(LogAccess.class); /*
		 * Otherwise, the actual import is started.
		 * This is done by configuring and starting an */ objectOf(ImporterRunner.class); /* inside a thread.
		 * This object is stored in the */ class_(RunningThread.class); /*, so that it can be forcefully stopped,
		 * if the user clicks the 'cancel' button.
		 * Furthermore, the lock file is created here.
		 * It will be deleted when the import either finishes regularly or is stopped by force.
		 * 
		 * */ objectOf(MainController.class).cancelImport(arg(Model.class)); /*
		 * 
		 * Here, the running import is stopped, but only after the current
		 * */ objectOf(ImporterStep.class); /* has finished.
		 * This is important, so that the application and Solr states remain consistent.
		 * See also the method */ class_(ImporterRunner.class).run(); /*
		 * 
		 * */ objectOf(MainController.class).deleteLockFile(arg(Model.class)); /*
		 * 
		 * In certain occasions, it can happen that the import does not run anymore, but the lock file is still present.
		 * An example is when the web server was shut down by the administrator while an import was running.
		 * In that situation, because of the 'short circuits' mentioned above, the user cannot start a new import.
		 * This method just deletes the lock file.
		 * It should not be executed while a process is running, because then application inconsistencies or errors
		 * are likely to happen.
		 * 
		 * */ objectOf(MainController.class).swapCores(arg(Model.class)); /*
		 * 
		 * In the frontend, the user has the possibility to swap the online and the offline core of the live server.
		 * It is a good practice to import all the data at first into the offline core and then to use an 
		 * */ objectOf(ImporterStepCoreSwap.class); /* to make it the online core.
		 * This means that the currently offline core used to be the online core before the import.
		 * This method then can be seen as the user's 'safety net' to restore the previous data state of Solr.
		 * 
		 */
	}

	protected void startingTheImport() throws Exception {
		
	}
}
