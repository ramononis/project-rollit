package ss.project.server.logging;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
* ====================================================================
*
* This software consists of voluntary contributions made by many
* individuals on behalf of the Apache Software Foundation.  For more
* information on the Apache Software Foundation, please see
* <http://www.apache.org/>.
*
*/
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
