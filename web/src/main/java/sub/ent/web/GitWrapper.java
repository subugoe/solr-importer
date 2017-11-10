package sub.ent.web;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.DetachedHeadException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefNotAdvertisedException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class GitWrapper {

	private Git git;
	private String gitUser;
	private String gitPassword;
	private Environment env = new Environment();

	public void init() {
		String gitDir = env.getVariable("GIT_DIR");
		String path = gitDir + "/.git";
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository db;
		try {
			db = builder.setGitDir(new File(path)).readEnvironment().findGitDir().build();
			git = Git.wrap(db);
			gitUser = env.getVariable("GIT_USER");
			gitPassword = env.getVariable("GIT_PASSWORD");
			if (gitUser == null || gitPassword == null) {
				throw new RuntimeException("Missing login data for git.");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void pull() throws WrongRepositoryStateException, InvalidConfigurationException, DetachedHeadException,
			InvalidRemoteException, CanceledException, RefNotFoundException, RefNotAdvertisedException, NoHeadException,
			TransportException, GitAPIException {
		PullCommand pc = git.pull();
		CredentialsProvider creds = new UsernamePasswordCredentialsProvider(gitUser, gitPassword);
		pc.setCredentialsProvider(creds);
		pc.call();
	}
	
	public String getLastCommitMessage() throws NoHeadException, GitAPIException {
		Iterable<RevCommit> revs = git.log().call();
		String lastMessage = revs.iterator().next().getShortMessage();
		return lastMessage;
	}

}
