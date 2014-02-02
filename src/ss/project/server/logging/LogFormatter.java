package ss.project.server.logging;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
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
public class LogFormatter extends Formatter {
	private static final String LINE_SEPARATOR = System
			.getProperty("line.separator");
	public final boolean newLine;
	public LogFormatter() {
		super();
		newLine = true;
	}
	public LogFormatter(final boolean line) {
		newLine = line;
	}
	public String format(final LogRecord record) {
		final StringBuilder result = new StringBuilder().append("[").append(
				record.getLevel().getName()).append("] ").append(
				new Date(record.getMillis())).append(": ").append(
				record.getLoggerName()).append(": ")
				.append(record.getMessage()).append(
						ThrowableUtils.throwableToString(record.getThrown()));
		if (newLine) {
			result.append(LogFormatter.LINE_SEPARATOR);
		}
		return result.toString();
	}
}
