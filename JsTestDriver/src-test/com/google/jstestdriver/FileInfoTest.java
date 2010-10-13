/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.jstestdriver;

import junit.framework.TestCase;

import java.io.File;

/**
 * @author andrewtrenk
 */
public class FileInfoTest extends TestCase {

	public void testFormatFileSeparator() {
		assertEquals("a/b/c", FileInfo.formatFileSeparator("a/b/c"));
		assertEquals("a/b/c", FileInfo.formatFileSeparator("a\\b\\c"));
	}

	public void testGetPath() {
		assertEquals("a/b/c/file.js", FileInfo.getPath(new File("a/b/c"), "file.js"));

		// Make sure path doesn't have extra slashes if directory ends in a slash
		assertEquals("a/b/c/file.js", FileInfo.getPath(new File("a/b/c/"), "file.js"));
	}

	public void testIsWebAddress() {
		FileInfo httpFile = new FileInfo("http://www.google.com", 0, -1, false, false, null);
		FileInfo httpsFile = new FileInfo("https://www.google.com", 0, -1, false, false, null);
		FileInfo nonWebFile = new FileInfo("a/b/c/file.js", 0, -1, false, false, null);

		assertTrue(httpFile.isWebAddress());
		assertTrue(httpsFile.isWebAddress());
		assertFalse(nonWebFile.isWebAddress());
	}
}
