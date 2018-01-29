package docs;

import sub.ent.api.ImporterStepCoreSwap;
import sub.ent.api.ImporterStepSleep;
import sub.ent.api.ImporterStepUpload;
import sub.ent.backend.BeanRetriever;
import sub.ent.backend.CoreSwapper;
import sub.ent.backend.FileAccess;
import sub.ent.backend.Importer;
import sub.ent.backend.Uploader;

import static docs.ConvenienceMethods.*;

class CoreModuleDocumentation {

	void bla() {
		/*
		 
		 The essential part of this module is */ the(Importer.class); /* with its 'importer steps'.
		 Its task is to pass some parameters to the steps and to execute them one by one.
		 Although */ the(Importer.class); /* does not know it, it is initialized by a dependency injection (DI) container.
		 Also, all the importer steps are injected into it at runtime.
		 The class that wraps the DI container functionality is */ the(BeanRetriever.class); /*.
		 If there is no project-specific plug-in available, then it loads the steps using 
		 the default file defined by the */ constant(BeanRetriever.DI_CONTEXT_DEFAULT); /*.
		 However, if it finds the file which is defined in the */ constant(BeanRetriever.DI_CONTEXT_IN_PLUGIN); /*,
		 then the steps are loaded from that file.
		 That non-default file must be provided by the external project (the plug-in).
		 
		 When the default file is used, then some predefined importer steps are executed.
		 An */ objectOf(ImporterStepSleep.class); /* creates an artificial pause before any other steps are run.
		 This is useful for users who click too fast or by mistake and want to cancel the import process.
		 Next, an */ objectOf(ImporterStepUpload.class); /* is started.
		 It uses an */ objectOf(FileAccess.class); /* to read XML files from a local directory.
		 An */ objectOf(Uploader.class); /* then takes over.
		 It converts the XML files to Java objects and sends them to a core in a Solr server.
		 It also manages all connections to Solr and some manipulations of the core, like cleaning and reloading it.
		 In general, it is a good practice to import all the data into a core that is offline.
		 The last importer step is an */ objectOf(ImporterStepCoreSwap.class); /*.
		 It uses an */ objectOf(CoreSwapper.class); /* to switch two cores, usually the offline core 
		 with the freshly imported data and the online core.
		 
		 
		 */
	}
}
