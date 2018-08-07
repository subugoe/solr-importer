package sub.ent.web;

import static org.mockito.Mockito.*;

import sub.ent.backend.BeanRetriever;
import sub.ent.backend.Environment;
import sub.ent.backend.GitWrapper;
import sub.ent.backend.LockFile;
import sub.ent.backend.LogAccess;
import sub.ent.backend.SolrAccess;

public class MyMocks {
	public GitWrapper git = mock(GitWrapper.class);
	public LogAccess logAccess = mock(LogAccess.class);
	public LockFile lock = mock(LockFile.class);
	public ImporterRunner runner = mock(ImporterRunner.class);
	public Environment env = mock(Environment.class);
	public SolrAccess swapper = mock(SolrAccess.class);
	public BeanRetriever retriever = mock(BeanRetriever.class);

	public MyMocks(MainController mc) {
		mc.setGit(git);
		mc.setLogAccess(logAccess);
		mc.setLock(lock);
		mc.setImporterRunner(runner);
		mc.setEnvironment(env);
		mc.setCoreSwapper(swapper);
		mc.setBeanRetriever(retriever);
	}
}
