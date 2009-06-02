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
package com.google.jstestdriver;

import org.apache.oro.io.GlobFilenameFilter;
import org.apache.oro.text.GlobCompiler;
import org.jvyaml.YAML;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO: needs to give more feedback when something goes wrong...
 * 
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class ConfigurationParser { 

  private static final Set<String> EMPTY_SET = new LinkedHashSet<String>();
  private final Set<String> filesList = new LinkedHashSet<String>();
  private final File basePath;

  private String server = "";

  public ConfigurationParser(File basePath) {
    this.basePath = basePath;
  }

  @SuppressWarnings("unchecked")
  public void parse(InputStream inputStream) {
    Map<Object, Object> data =
        (Map<Object, Object>) YAML.load(new BufferedReader(new InputStreamReader(inputStream)));
    Set<String> resolvedFilesLoad = new LinkedHashSet<String>();
    Set<String> resolvedFilesExclude = new LinkedHashSet<String>();

    if (data.containsKey("load")) {
      resolvedFilesLoad.addAll(resolveFiles((List<String>) data.get("load")));
    }
    if (data.containsKey("exclude")) {
      resolvedFilesExclude.addAll(resolveFiles((List<String>) data.get("exclude")));
    }
    if (data.containsKey("server")) {
      this.server = (String) data.get("server");
    }

    filesList.addAll(resolvedFilesLoad);
    filesList.removeAll(resolvedFilesExclude);
  }

  private Set<String> resolveFiles(List<String> files) {
    if (files != null) {
      Set<String> resolvedFiles = new LinkedHashSet<String>();

      for (String f : files) {
        boolean isPatch = f.startsWith("patch");

        if (isPatch) {
          String[] tokens = f.split(" ");

          f = tokens[1].trim();
        }
        if (f.startsWith("http://") || f.startsWith("https://")) {
          resolvedFiles.add(f);
        } else {
          File testFile = new File(f);
          String relativeDir = testFile.getParent() == null ? "" : testFile.getParent();
          File file = basePath != null ? new File(basePath, f) : new File(f);
          File dir = file.getParentFile() == null ? new File(".") : file.getParentFile();
          final String pattern = file.getName();
          String[] filteredFiles = dir.list(new GlobFilenameFilter(pattern,
              GlobCompiler.DEFAULT_MASK | GlobCompiler.CASE_INSENSITIVE_MASK));

          if (filteredFiles == null) {
            System.err.println("No files to load. The patterns/paths used in the configuration" +
            		" file didn't match any file, the files patterns/paths need to be relative to" +
            		" the configuration file.");
            System.exit(1);
          }
          String normalizedBasePath =
              basePath != null ? basePath.getPath().replaceAll("\\\\", "/") + "/" : "";
          for (String filteredFile : filteredFiles) {
            String normalizedRelativeDir = relativeDir.replaceAll("\\\\", "/");

            if (normalizedRelativeDir.length() > 0) {
              normalizedRelativeDir += "/";
            }
            String resolvedFile = String.format("%s%s%s", normalizedBasePath, normalizedRelativeDir,
                filteredFile.replaceAll("\\\\", "/"));

            if (isPatch) {
              resolvedFile = "patch:" + resolvedFile;
            }
            resolvedFiles.add(resolvedFile);
          }
        }
      }
      return resolvedFiles;
    }
    return EMPTY_SET;
  }

  public Set<String> getFilesList() {
    return filesList;
  }

  public String getServer() {
    return server;
  }
}
