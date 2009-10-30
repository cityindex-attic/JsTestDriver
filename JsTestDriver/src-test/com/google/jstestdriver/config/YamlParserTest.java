/*
 * Copyright 2009 Google Inc.
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
package com.google.jstestdriver.config;

import com.google.common.collect.Lists;
import com.google.jstestdriver.DefaultPathRewriter;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.Plugin;
import com.google.jstestdriver.config.Configuration;
import com.google.jstestdriver.config.YamlParser;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class YamlParserTest extends TestCase {

  File tmpDir;

  @Override
  protected void setUp() throws Exception {
    tmpDir = File.createTempFile("test", "JsTestDriver", new File(System
      .getProperty("java.io.tmpdir")));
    tmpDir.delete();
    tmpDir.mkdir();
    tmpDir.deleteOnExit();
  }

  private File createTmpSubDir(String dirName) {
    File codeDir = new File(tmpDir, dirName);
    codeDir.mkdir();
    codeDir.deleteOnExit();
    return codeDir;
  }

  private File createTmpFile(File codeDir, String fileName) throws IOException {
    File code = new File(codeDir, fileName);
    code.createNewFile();
    code.deleteOnExit();
    return code;
  }

  public void testParseConfigFileAndHaveListOfFiles() throws Exception {
    File codeDir = createTmpSubDir("code");
    File testDir = createTmpSubDir("test");
    createTmpFile(codeDir, "code.js");
    createTmpFile(codeDir, "code2.js");
    createTmpFile(testDir, "test.js");
    createTmpFile(testDir, "test2.js");
    createTmpFile(testDir, "test3.js");

    String configFile = "load:\n - code/*.js\n - test/*.js\nexclude:\n"
      + " - code/code2.js\n - test/test2.js";
    ByteArrayInputStream bais = new ByteArrayInputStream(configFile.getBytes());
    YamlParser parser = new YamlParser(new DefaultPathRewriter());

    Configuration config = parser.parse(tmpDir, new InputStreamReader(bais));
    Set<FileInfo> files = config.getFilesList();
    List<FileInfo> listFiles = new ArrayList<FileInfo>(files);

    assertEquals(3, files.size());
    assertTrue(listFiles.get(0).getFileName().endsWith("code/code.js"));
    assertTrue(listFiles.get(1).getFileName().endsWith("test/test.js"));
    assertTrue(listFiles.get(2).getFileName().endsWith("test/test3.js"));
  }

  public void testParseConfigFileAndHaveListOfFilesWithPatches()
      throws Exception {
    File codeDir = createTmpSubDir("code");
    File testDir = createTmpSubDir("test");
    createTmpFile(codeDir, "code.js");
    createTmpFile(codeDir, "code2.js");
    createTmpFile(codeDir, "patch.js");
    createTmpFile(testDir, "test.js");
    createTmpFile(testDir, "test2.js");
    createTmpFile(testDir, "test3.js");

    String configFile = "load:\n" + "- code/code.js\n"
      + "- patch code/patch.js\n" + "- code/code2.js\n" + "- test/*.js\n"
      + "exclude:\n" + "- code/code2.js\n" + "- test/test2.js";
    ByteArrayInputStream bais = new ByteArrayInputStream(configFile.getBytes());
    YamlParser parser = new YamlParser(new DefaultPathRewriter());

    Configuration config = parser.parse(tmpDir, new InputStreamReader(bais));
    Set<FileInfo> files = config.getFilesList();
    System.out.println("FILES: " + files);
    List<FileInfo> listFiles = new ArrayList<FileInfo>(files);

    assertEquals(3, files.size());
    assertTrue(listFiles.get(0).getFileName().endsWith("code/code.js"));
    assertTrue(listFiles.get(1).getFileName().endsWith("test/test.js"));
    assertTrue(listFiles.get(2).getFileName().endsWith("test/test3.js"));
    assertTrue(listFiles.get(0).getPatches().get(0).getFileName().endsWith(
      "code/patch.js"));
  }

  public void testParseConfigFileAndHaveListOfFilesWithUnassociatedPatch()
      throws Exception {
    File codeDir = createTmpSubDir("code");
    File testDir = createTmpSubDir("test");
    createTmpFile(codeDir, "code.js");
    createTmpFile(codeDir, "code2.js");
    createTmpFile(codeDir, "patch.js");
    createTmpFile(testDir, "test.js");
    createTmpFile(testDir, "test2.js");
    createTmpFile(testDir, "test3.js");

    String configFile = "load:\n" + "- patch code/patch.js\n"
      + "- code/code.js\n" + "- code/code2.js\n" + "- test/*.js\n"
      + "exclude:\n" + "- code/code2.js\n" + "- test/test2.js";
    ByteArrayInputStream bais = new ByteArrayInputStream(configFile.getBytes());
    YamlParser parser = new YamlParser(new DefaultPathRewriter());
    try {
      parser.parse(tmpDir, new InputStreamReader(bais));
      fail("should have thrown an exception due to patching a non-existant file");
    } catch (IllegalStateException e) {
      // pass
    }
  }

  public void testParsePlugin() {
    Plugin expected = new Plugin("test", "pathtojar", "com.test.PluginModule",
      Lists.<String> newArrayList());
    String configFile = "plugin:\n" + "  - name: test\n"
      + "    jar: \"pathtojar\"\n" + "    module: \"com.test.PluginModule\"\n";
    ByteArrayInputStream bais = new ByteArrayInputStream(configFile.getBytes());
    YamlParser parser = new YamlParser(new DefaultPathRewriter());

    Configuration config = parser.parse(tmpDir, new InputStreamReader(bais));
    List<Plugin> plugins = config.getPlugins();
    assertEquals(expected, plugins.get(0));
  }

  public void testParsePlugins() {
    List<Plugin> expected = new LinkedList<Plugin>(Arrays.asList(new Plugin(
      "test", "pathtojar", "com.test.PluginModule", Lists
        .<String> newArrayList()), new Plugin("test2", "pathtojar2",
      "com.test.PluginModule2", Lists.<String> newArrayList("hello", "world",
        "some/file.js"))));
    String configFile = "plugin:\n" + "  - name: test\n"
      + "    jar: \"pathtojar\"\n" + "    module: \"com.test.PluginModule\"\n"
      + "  - name: test2\n" + "    jar: \"pathtojar2\"\n"
      + "    module: \"com.test.PluginModule2\"\n"
      + "    args: hello, world, some/file.js\n";
    ByteArrayInputStream bais = new ByteArrayInputStream(configFile.getBytes());
    YamlParser parser = new YamlParser(new DefaultPathRewriter());

    Configuration config = parser.parse(tmpDir, new InputStreamReader(bais));
    List<Plugin> plugins = config.getPlugins();

    assertEquals(2, plugins.size());
    assertEquals(expected, plugins);
    assertEquals(0, plugins.get(0).getArgs().size());
    assertEquals(3, plugins.get(1).getArgs().size());
  }

  public void testParsePluginArgs() throws Exception {
    String configFile = "plugin:\n" + "  - name: test\n"
      + "    jar: \"pathtojar\"\n" + "    module: \"com.test.PluginModule\"\n"
      + "    args: hello, mooh, some/file.js, another/file.js";
    ByteArrayInputStream bais = new ByteArrayInputStream(configFile.getBytes());
    YamlParser parser = new YamlParser(new DefaultPathRewriter());

    Configuration config = parser.parse(tmpDir, new InputStreamReader(bais));
    List<Plugin> plugins = config.getPlugins();
    Plugin plugin = plugins.get(0);
    List<String> args = plugin.getArgs();

    assertEquals(4, args.size());
    assertEquals("hello", args.get(0));
    assertEquals("mooh", args.get(1));
    assertEquals("some/file.js", args.get(2));
    assertEquals("another/file.js", args.get(3));
  }

  public void testParsePluginNoArgs() throws Exception {
    String configFile = "plugin:\n" + "  - name: test\n"
      + "    jar: \"pathtojar\"\n" + "    module: \"com.test.PluginModule\"\n";
    ByteArrayInputStream bais = new ByteArrayInputStream(configFile.getBytes());
    YamlParser parser = new YamlParser(new DefaultPathRewriter());

    Configuration config = parser.parse(tmpDir, new InputStreamReader(bais));
    List<Plugin> plugins = config.getPlugins();
    Plugin plugin = plugins.get(0);
    List<String> args = plugin.getArgs();

    assertEquals(0, args.size());
  }

  public void testServeFile() throws Exception {
    File codeDir = createTmpSubDir("code");
    File testDir = createTmpSubDir("test");
    File serveDir = createTmpSubDir("serve");
    createTmpFile(codeDir, "code.js");
    createTmpFile(codeDir, "code2.js");
    createTmpFile(testDir, "test.js");
    createTmpFile(testDir, "test2.js");
    createTmpFile(testDir, "test3.js");
    createTmpFile(serveDir, "serve1.js");

    String configFile = "load:\n" + " - code/*.js\n" + " - test/*.js\n"
      + "serve:\n" + " - serve/serve1.js\n" + "exclude:\n"
      + " - code/code2.js\n" + " - test/test2.js";
    ByteArrayInputStream bais = new ByteArrayInputStream(configFile.getBytes());
    YamlParser parser = new YamlParser(new DefaultPathRewriter());

    Configuration config = parser.parse(tmpDir, new InputStreamReader(bais));
    Set<FileInfo> serveFilesSet = config.getFilesList();
    List<FileInfo> serveFiles = new ArrayList<FileInfo>(serveFilesSet);

    assertEquals(4, serveFilesSet.size());
    assertTrue(serveFiles.get(0).getFileName().endsWith("code/code.js"));
    assertTrue(serveFiles.get(1).getFileName().endsWith("test/test.js"));
    assertTrue(serveFiles.get(2).getFileName().endsWith("test/test3.js"));
    assertTrue(serveFiles.get(3).getFileName().endsWith("serve/serve1.js"));
    assertTrue(serveFiles.get(3).isServeOnly());
  }

  public void testCheckValidTimeStamp() throws Exception {
    File codeDir = createTmpSubDir("code");
    File testDir = createTmpSubDir("test");
    createTmpFile(codeDir, "code.js");
    createTmpFile(codeDir, "code2.js");
    createTmpFile(testDir, "test.js");
    createTmpFile(testDir, "test2.js");
    createTmpFile(testDir, "test3.js");

    String configFile = "load:\n - code/*.js\n - test/*.js\nexclude:\n"
      + " - code/code2.js\n - test/test2.js";
    ByteArrayInputStream bais = new ByteArrayInputStream(configFile.getBytes());
    YamlParser parser = new YamlParser(new DefaultPathRewriter());

    Configuration config = parser.parse(tmpDir,new InputStreamReader(bais));
    Set<FileInfo> files = config.getFilesList();
    List<FileInfo> listFiles = new ArrayList<FileInfo>(files);

    assertEquals(3, files.size());
    assertTrue(listFiles.get(0).getTimestamp() > 0);
    assertTrue(listFiles.get(1).getTimestamp() > 0);
    assertTrue(listFiles.get(2).getTimestamp() > 0);
  }
}
