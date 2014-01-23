package ss.project.server.logging;

import java.util.logging.*;
public class TextAreaLogHandler extends Handler {
	public static final LogTextArea TEXT_AREA = new LogTextArea();
	public void close() {
	}
	public void flush() {
	}
	public void publish(final LogRecord record) {
		TextAreaLogHandler.TEXT_AREA.log(record);
	}
}
