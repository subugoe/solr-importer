package sub.ent.backend;

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

/**
 * Can execute git operations on a local repository.
 */
public class GitWrapper {

	private Git git;
	private String gitUser;
	private String gitPassword;
	private final Environment env = new Environment();

	/**
	 * Initializes the git repo to use.
	 */
	public void init() {
		String gitDir = env.inputDir();
		String path = gitDir + "/.git";
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository db;
		try {
			db = builder.setGitDir(new File(path)).readEnvironment().findGitDir().build();
			git = Git.wrap(db);
			gitUser = env.gitUser();
			gitPassword = env.gitPassword();
			if (gitUser == null || gitPassword == null) {
				throw new RuntimeException("Missing login data for git.");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Updates the local repository from the remote.
	 */
	public void pull() throws WrongRepositoryStateException, InvalidConfigurationException, DetachedHeadException,
			InvalidRemoteException, CanceledException, RefNotFoundException, RefNotAdvertisedException, NoHeadException,
			TransportException, GitAPIException {
		PullCommand pc = git.pull();
		CredentialsProvider creds = new UsernamePasswordCredentialsProvider(gitUser, gitPassword);
		pc.setCredentialsProvider(creds);
		pc.call();
	}

	/**
	 * Gets the last message in the local git repo.
	 */
	public String getLastCommitMessage() throws NoHeadException, GitAPIException {
		Iterable<RevCommit> revs = git.log().call();
		return revs.iterator().next().getShortMessage();
	}

}
