package ss.project.server.logging;
import java.io.*;
import java.util.logging.*;
public class LoggingBootstrap {
	private static final Logger EXCEPTION_LOG = Logger
			.getLogger("EXCEPTION");
	private static final Logger ERROR_LOG = Logger.getLogger("STDERR");
	public static void bootstrap() {
		Logger.getLogger("").addHandler(new ConsoleLogger());
		Thread
				.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
					public void uncaughtException(final Thread t,
							final Throwable e) {
						LoggingBootstrap.EXCEPTION_LOG.logp(
								Level.SEVERE, "EXCEPTION", "",
								"Unhandled exception in thread " + t.getName()
										+ ": ", e);
					}
				});
		System.setErr(new PrintStream(new LoggingOutputStream(
				LoggingBootstrap.ERROR_LOG, Level.SEVERE), true));
	}
}
