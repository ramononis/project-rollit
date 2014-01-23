package ss.project.server.logging;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
public class LoggingOutputStream extends OutputStream {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	protected boolean hasBeenClosed = false;
	protected byte[] buf;
	protected int count;
	private int bufLength;
	public static final int DEFAULT_BUFFER_LENGTH = 2048;
	protected Logger category;
	protected Level priority;
	public LoggingOutputStream(final Logger cat, final Level prio) {
		if (cat == null) {
			throw new IllegalArgumentException("cat == null");
		}
		if (prio == null) {
			throw new IllegalArgumentException("priority == null");
		}
		priority = prio;
		category = cat;
		bufLength = LoggingOutputStream.DEFAULT_BUFFER_LENGTH;
		buf = new byte[LoggingOutputStream.DEFAULT_BUFFER_LENGTH];
		count = 0;
	}
	public void close() {
		flush();
		hasBeenClosed = true;
	}
	public void flush() {
		if (count == 0) {
			return;
		}
		if (count == LoggingOutputStream.LINE_SEPARATOR.length()) {
			if (((char) buf[0] == LoggingOutputStream.LINE_SEPARATOR.charAt(0)) && ((count == 1) ||
					((count == 2) && 
							((char) buf[1] == LoggingOutputStream.LINE_SEPARATOR.charAt(1))))) {
				reset();
				return;
			}
		}
		final byte[] theBytes = new byte[count];
		System.arraycopy(buf, 0, theBytes, 0, count);
		category.log(priority, new String(theBytes));
		reset();
	}
	private void reset() {
		count = 0;
	}
	public void write(final int b) throws IOException {
		if (hasBeenClosed) {
			throw new IOException("The stream has been closed.");
		}
		if (b == 0) {
			return;
		}
		if (count == bufLength) {
			final int newBufLength = bufLength + LoggingOutputStream.DEFAULT_BUFFER_LENGTH;
			final byte[] newBuf = new byte[newBufLength];
			System.arraycopy(buf, 0, newBuf, 0, bufLength);
			buf = newBuf;
			bufLength = newBufLength;
		}
		buf[count] = (byte) b;
		count++;
	}
}
