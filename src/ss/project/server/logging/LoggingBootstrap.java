package ss.project.server.logging;
import java.io.*;
import java.util.logging.*;
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
public class LoggingBootstrap {
	private static final Logger EXCEPTION_LOG = Logger
			.getLogger("EXCEPTION");
	private static final Logger ERROR_LOG = Logger.getLogger("STDERR");

	public static void bootstrap() {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
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
