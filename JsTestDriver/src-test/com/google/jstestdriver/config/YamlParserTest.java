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

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import com.google.common.collect.Lists;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.Plugin;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class YamlParserTest extends TestCase {


  public void testParseConfigFileAndHaveListOfFiles() throws Exception {
    String configFile = "load:\n - code/*.js\n - test/*.js\nexclude:\n"
      + " - code/code2.js\n - test/test2.js";
    ByteArrayInputStream bais = new ByteArrayInputStream(configFile.getBytes());
    YamlParser parser = new YamlParser();

    Configuration config = parser.parse(new InputStreamReader(bais));
    Set<FileInfo> files = config.getFilesList();
    List<FileInfo> listFiles = new ArrayList<FileInfo>(files);

    assertEquals(2, files.size());
    assertTrue(listFiles.get(0).getFilePath().endsWith("code/*.js"));
    assertTrue(listFiles.get(1).getFilePath().endsWith("test/*.js"));
  }
 
  public void testParseConfigFileAndHaveListOfFilesWithPatches()
      throws Exception {

    String configFile = "load:\n" + "- code/code.js\n"
      + "- patch code/patch.js\n" + "- code/code2.js\n" + "- test/*.js\n"
      + "exclude:\n" + "- code/code2.js\n" + "- test/test2.js";
    ByteArrayInputStream bais = new ByteArrayInputStream(configFile.getBytes());
    YamlParser parser = new YamlParser();

    Configuration config = parser.parse( new InputStreamReader(bais));
    Set<FileInfo> files = config.getFilesList();
    List<FileInfo> listFiles = new ArrayList<FileInfo>(files);

    assertEquals(3, files.size());
    assertTrue(listFiles.get(1).toString(),
        listFiles.get(0).getFilePath().endsWith("code/code.js"));
    assertTrue(listFiles.get(1).toString(),
        listFiles.get(1).getFilePath().endsWith("code/patch.js"));
  }

  public void testParsePlugin() {
    Plugin expected = new Plugin("test", "pathtojar", "com.test.PluginModule",
      Lists.<String> newArrayList());
    String configFile = "plugin:\n" + "  - name: test\n"
      + "    jar: \"pathtojar\"\n" + "    module: \"com.test.PluginModule\"\n";
    ByteArrayInputStream bais = new ByteArrayInputStream(configFile.getBytes());
    YamlParser parser = new YamlParser();

    Configuration config = parser.parse(new InputStreamReader(bais));
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
    YamlParser parser = new YamlParser();

    Configuration config = parser.parse(new InputStreamReader(bais));
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
    YamlParser parser = new YamlParser();

    Configuration config = parser.parse(new InputStreamReader(bais));
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
    YamlParser parser = new YamlParser();

    Configuration config = parser.parse(new InputStreamReader(bais));
    List<Plugin> plugins = config.getPlugins();
    Plugin plugin = plugins.get(0);
    List<String> args = plugin.getArgs();

    assertEquals(0, args.size());
  }

  public void testServeFile() throws Exception {
    String configFile = "load:\n" + " - code/*.js\n" + " - test/*.js\n"
      + "serve:\n" + " - serve/serve1.js\n" + "exclude:\n"
      + " - code/code2.js\n" + " - test/test2.js";
    ByteArrayInputStream bais = new ByteArrayInputStream(configFile.getBytes());
    YamlParser parser = new YamlParser();

    Configuration config = parser.parse(new InputStreamReader(bais));
    Set<FileInfo> serveFilesSet = config.getFilesList();
    List<FileInfo> serveFiles = new ArrayList<FileInfo>(serveFilesSet);

    assertEquals(3, serveFilesSet.size());
    assertTrue(serveFiles.get(0).getFilePath().endsWith("code/*.js"));
    assertTrue(serveFiles.get(1).getFilePath().endsWith("test/*.js"));
    assertTrue(serveFiles.get(2).getFilePath().endsWith("serve/serve1.js"));
    assertTrue(serveFiles.get(2).isServeOnly());
  }
  
  public void testParseTests() throws Exception {
    String configFile =
          "load:\n"
        + " - code/*.js\n"
        + "test:\n"
        + " - test/*.js\n"
        + "serve:\n"
        + " - serve/serve1.js\n"
        + "exclude:\n"
        + " - code/code2.js\n"
        + " - test/test2.js";
    ByteArrayInputStream bais = new ByteArrayInputStream(configFile.getBytes());
    YamlParser parser = new YamlParser();
    
    Configuration config = parser.parse(new InputStreamReader(bais));
    Set<FileInfo> serveFilesSet = config.getFilesList();
    List<FileInfo> serveFiles = Lists.newArrayList(serveFilesSet);
    
    assertEquals(2, serveFilesSet.size());
    assertTrue(serveFiles.get(0).getFilePath().endsWith("code/*.js"));
    assertTrue(serveFiles.get(1).getFilePath().endsWith("serve/serve1.js"));
    assertTrue(serveFiles.get(1).isServeOnly());
    
    List<FileInfo> tests = config.getTests();
    assertEquals("test/*.js", tests.get(0).getFilePath());
  }
}
