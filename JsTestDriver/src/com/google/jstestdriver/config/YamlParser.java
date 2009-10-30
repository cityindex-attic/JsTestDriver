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

import java.io.File;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.oro.io.GlobFilenameFilter;
import org.apache.oro.text.GlobCompiler;
import org.jvyaml.YAML;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.PathResolver;
import com.google.jstestdriver.PathRewriter;
import com.google.jstestdriver.Plugin;


/**
 * Parses Yaml files.
 * 
 * @author corysmith@google.com (Cory Smith)
 */
public class YamlParser {

  private final PathRewriter pathRewriter;

  private final PathResolver pathResolver = new PathResolver();

  public YamlParser(PathRewriter pathRewriter) {
    this.pathRewriter = pathRewriter;
  }

  @SuppressWarnings("unchecked")
  public Configuration parse(File basePath, Reader configReader) {
    Map<Object, Object> data = (Map<Object, Object>) YAML.load(configReader);
    Set<FileInfo> resolvedFilesLoad = new LinkedHashSet<FileInfo>();
    Set<FileInfo> resolvedFilesExclude = new LinkedHashSet<FileInfo>();

    String server = "";
    List<Plugin> plugins = Lists.newLinkedList();
    Set<FileInfo> filesList = Sets.newLinkedHashSet();

    if (data.containsKey("load")) {
      resolvedFilesLoad.addAll(resolveFiles(basePath, (List<String>) data
        .get("load"), false));
    }
    if (data.containsKey("exclude")) {
      resolvedFilesExclude.addAll(resolveFiles(basePath, (List<String>) data
        .get("exclude"), false));
    }
    if (data.containsKey("server")) {
      server = (String) data.get("server");
    }
    if (data.containsKey("plugin")) {
      for (Map<String, String> value : (List<Map<String, String>>) data
        .get("plugin")) {
        plugins.add(new Plugin(value.get("name"), value.get("jar"), value
          .get("module"), createArgsList(value.get("args"))));
      }
    }
    if (data.containsKey("serve")) {
      Set<FileInfo> resolvedServeFiles = resolveFiles(basePath,
        (List<String>) data.get("serve"), true);
      resolvedFilesLoad.addAll(resolvedServeFiles);
    }
    filesList.addAll(consolidatePatches(resolvedFilesLoad));
    filesList.removeAll(resolvedFilesExclude);
    return new ParsedConfiguration(filesList, plugins, server);
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

  private Set<FileInfo> consolidatePatches(Set<FileInfo> resolvedFilesLoad) {
    Set<FileInfo> consolidated = new LinkedHashSet<FileInfo>(resolvedFilesLoad.size());
    FileInfo currentNonPatch = null;
    for (FileInfo fileInfo : resolvedFilesLoad) {
      if (fileInfo.isPatch()) {
        if (currentNonPatch == null) {
          throw new IllegalStateException("Patch " + fileInfo
            + " without a core file to patch");
        }
        currentNonPatch.addPatch(fileInfo);
      } else {
        consolidated.add(fileInfo);
        currentNonPatch = fileInfo;
      }
    }
    return consolidated;
  }

  private Set<FileInfo> resolveFiles(File basePath, List<String> files,
      boolean serveOnly) {
    if (files != null) {
      Set<FileInfo> resolvedFiles = new LinkedHashSet<FileInfo>();

      for (String f : files) {
        f = pathRewriter.rewrite(f);
        boolean isPatch = f.startsWith("patch");

        if (isPatch) {
          String[] tokens = f.split(" ", 2);

          f = tokens[1].trim();
        }
        if (f.startsWith("http://") || f.startsWith("https://")) {
          resolvedFiles.add(new FileInfo(f, -1, false, false, null));
        } else {
          File file = basePath != null
            ? new File(basePath.getAbsoluteFile(), f) : new File(f);
          File testFile = file.getAbsoluteFile();
          File dir = testFile.getParentFile().getAbsoluteFile();
          final String pattern = file.getName();
          String[] filteredFiles = dir.list(new GlobFilenameFilter(pattern,
            GlobCompiler.DEFAULT_MASK | GlobCompiler.CASE_INSENSITIVE_MASK));

          if (filteredFiles == null || filteredFiles.length == 0) {
            String error = "The patterns/paths "
              + f
              + " used in the configuration"
              + " file didn't match any file, the files patterns/paths need to be relative to"
              + " the configuration file.";

            System.err.println(error);
            throw new RuntimeException(error);
          }
          Arrays.sort(filteredFiles, String.CASE_INSENSITIVE_ORDER);

          for (String filteredFile : filteredFiles) {
            String resolvedFilePath = pathResolver.resolvePath(dir
              .getAbsolutePath().replaceAll("\\\\", "/")
              + "/" + filteredFile.replaceAll("\\\\", "/"));
            File resolvedFile = new File(resolvedFilePath);

            resolvedFiles.add(new FileInfo(resolvedFilePath, resolvedFile
              .lastModified(), isPatch, serveOnly, null));
          }
        }
      }
      return resolvedFiles;
    }
    return Collections.emptySet();
  }
}
