package ss.project.logging;

import java.util.logging.ConsoleHandler;
public class ConsoleLogger extends ConsoleHandler {
	public ConsoleLogger() {
		super();
		setOutputStream(System.out);
	}
}
