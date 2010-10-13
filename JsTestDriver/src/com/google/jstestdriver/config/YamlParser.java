/*
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
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.Plugin;

import org.jvyaml.YAML;

import java.io.Reader;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Parses Yaml files.
 * 
 * @author corysmith@google.com (Cory Smith)
 */
public class YamlParser {
  @SuppressWarnings("unchecked")
  public Configuration parse(Reader configReader) {
    Map<Object, Object> data = (Map<Object, Object>) YAML.load(configReader);
    Set<FileInfo> resolvedFilesLoad = new LinkedHashSet<FileInfo>();
    Set<FileInfo> testFiles = new LinkedHashSet<FileInfo>();
    Set<FileInfo> resolvedFilesExclude = new LinkedHashSet<FileInfo>();

    String server = "";
    long timeOut = 0;
    List<Plugin> plugins = Lists.newLinkedList();

    if (data.containsKey("load")) {
      resolvedFilesLoad.addAll(createFileInfos((List<String>) data
        .get("load"), false));
    }
    if (data.containsKey("test")) {
      testFiles.addAll(createFileInfos((List<String>) data
          .get("test"), false));
    }
    if (data.containsKey("exclude")) {
      resolvedFilesExclude.addAll(createFileInfos((List<String>) data
        .get("exclude"), false));
    }
    if (data.containsKey("server")) {
      server = (String) data.get("server");
    }
    if (data.containsKey("plugin")) {
      for (Map<String, String> value :
          (List<Map<String, String>>) data.get("plugin")) {
        plugins.add(new Plugin(value.get("name"), value.get("jar"),
            value.get("module"), createArgsList(value.get("args"))));
      }
    }
    if (data.containsKey("serve")) {
      Set<FileInfo> resolvedServeFiles = createFileInfos((List<String>) data.get("serve"),
        true);
      resolvedFilesLoad.addAll(resolvedServeFiles);
    }
    
    if (data.containsKey("timeout")) {
      timeOut = (Long)data.get("timeout");
    }

    return new ParsedConfiguration(resolvedFilesLoad,
                                   resolvedFilesExclude,
                                   plugins,
                                   server,
                                   timeOut,
                                   Lists.newArrayList(testFiles));
  }

  private List<String> createArgsList(String args) {
    if (args == null) {
      return Collections.<String> emptyList();
    }
    List<String> argsList = Lists.newLinkedList();
    String[] splittedArgs = args.split(",");

    for (String arg : splittedArgs) {
      argsList.add(arg.trim());
    }
    return argsList;
  }

  private Set<FileInfo> createFileInfos(List<String> files, boolean serveOnly) {
    if (files != null) {
      Set<FileInfo> fileInfos = new LinkedHashSet<FileInfo>();

      for (String f : files) {
        boolean isPatch = f.startsWith("patch");

        if (isPatch) {
          String[] tokens = f.split(" ", 2);
          f = tokens[1].trim();
        }
        fileInfos.add(new FileInfo(f, -1, -1, isPatch, serveOnly, null));
      }
      return fileInfos;
    }
    return Collections.emptySet();
  }
}
