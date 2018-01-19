package sub.ent.web;

/**
 * Holds a reference to the currently running import process.
 * This way, the import can be stopped by a web request.
 */
public class RunningThread {

	public static Thread instance;

}
