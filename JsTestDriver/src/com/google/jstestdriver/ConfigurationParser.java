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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.jstestdriver.directoryscanner.DirectoryScanner;

import org.jvyaml.YAML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO: needs to give more feedback when something goes wrong...
 * 
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class ConfigurationParser {

  private static final String LOAD = "load";
  private static final String EXCLUDE = "exclude";
  private static final String SERVER = "server";
  private static final String PLUGIN = "plugin";
  private static final String SERVE = "serve";

  private static final String PATCH = "patch";

  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationParser.class);

  private final Set<FileInfo> filesList = new LinkedHashSet<FileInfo>();
  private final File basePath;
  private final Reader configReader;

  private String server = "";
  private List<Plugin> plugins = new LinkedList<Plugin>();

  public ConfigurationParser(File basePath, Reader configReader) {
    this.basePath = basePath;
    this.configReader = configReader;
  }

  @SuppressWarnings("unchecked")
  public void parse() {
    Map<Object, Object> data = (Map<Object, Object>) YAML.load(configReader);
    Set<FileInfo> resolvedFilesLoad = new LinkedHashSet<FileInfo>();
    Set<FileInfo> resolvedFilesExclude = new LinkedHashSet<FileInfo>();

    if (data.containsKey(LOAD)) {
      resolvedFilesLoad.addAll(resolveFiles((List<String>) data.get(LOAD), false));
    }
    if (data.containsKey(EXCLUDE)) {
      resolvedFilesExclude.addAll(resolveFiles((List<String>) data.get(EXCLUDE), false));
    }
    if (data.containsKey(SERVER)) {
      this.server = (String) data.get(SERVER);
    }
    if (data.containsKey(PLUGIN)) {
      for (Map<String, String> value: (List<Map<String, String>>) data.get(PLUGIN)) {
        plugins.add(new Plugin(value.get("name"), value.get("jar"), value.get("module")));
      }
    }
    if (data.containsKey(SERVE)) {
      Set<FileInfo> resolvedServeFiles = resolveFiles((List<String>) data.get(SERVE), true);
      resolvedFilesLoad.addAll(resolvedServeFiles);
    }
    filesList.addAll(consolidatePatches(resolvedFilesLoad));
    filesList.removeAll(resolvedFilesExclude);
  }

  private Set<FileInfo> consolidatePatches(Set<FileInfo> resolvedFilesLoad) {
    Set<FileInfo> consolidated = new LinkedHashSet<FileInfo>(resolvedFilesLoad.size());
    FileInfo currentNonPatch = null;
    for (FileInfo fileInfo : resolvedFilesLoad) {
      if (fileInfo.isPatch()) {
        if (currentNonPatch == null) {
          throw new IllegalStateException("Patch " + fileInfo + " without a core file to patch");
        }
        currentNonPatch.addPatch(fileInfo);
      } else {
        consolidated.add(fileInfo);
        currentNonPatch = fileInfo;
      }
    }
    return consolidated;
  }

  private List<FileInfo> getAbsoluteFileInfos(List<String> files) {
    List<FileInfo> absolutePaths = Lists.newLinkedList();
    PathResolver pathResolver = new PathResolver();

    for (String f : files) {
      boolean isPatch = f.startsWith(PATCH);

      if (isPatch) {
        String[] tokens = f.split(" ", 2);

        f = tokens[1].trim();
      }
      if (f.startsWith("http://") || f.startsWith("https://")) {
        absolutePaths.add(new FileInfo(f, -1, false, false, null));
      } else {
        File file = basePath != null ? new File(basePath, f) : new File(f);
        String finalPath = getFinalPath(file);

        absolutePaths.add(new FileInfo(finalPath, -1, isPatch, false, null));
      }
    }
    return absolutePaths;
  }

  private Set<FileInfo> resolveFiles(List<String> files, boolean serveOnly) {
    if (files == null) {
      return Collections.emptySet();
    }
    List<FileInfo> absoluteFiles = getAbsoluteFileInfos(files);
    CommonPathResolver commonPathResolver = new CommonPathResolver(absoluteFiles);

    String baseDir = commonPathResolver.resolve();
    RelativePathConverter relativePathConverter = new RelativePathConverter(baseDir, absoluteFiles);

    List<FileInfo> relativeFiles = relativePathConverter.convert();
    DirectoryScanner directoryScanner = new DirectoryScanner();

    directoryScanner.setBasedir(new File(baseDir));
    Set<FileInfo> resolvedFileInfos = Sets.newLinkedHashSet();

    for (FileInfo relativeFile : relativeFiles) {
      if (relativeFile.canLoad()) {

        /**
         * We have to use the directory scanner this way because we want to preserve the order of the
         * files as they appear in the configuration file.
         */
        System.out.println("FILENAME: " + relativeFile.getFileName());
        directoryScanner.setIncludes(new String[] { relativeFile.getFileName() });
        directoryScanner.scan();
        String[] resolvedFiles = directoryScanner.getIncludedFiles();

        for (String resolvedFile : resolvedFiles) {
          System.out.println("RESOLVED FILE: " + resolvedFile);
          File finalFile = new File(baseDir, resolvedFile);
          String finalPath = getFinalPath(finalFile);

          resolvedFileInfos.add(new FileInfo(finalPath.replaceAll("\\\\", "/"), finalFile
              .lastModified(), relativeFile.isPatch(), serveOnly, null));
        }
      } else {
        resolvedFileInfos.add(relativeFile);
      }
    }
    return resolvedFileInfos;
  }

  private String getFinalPath(File file) {
    PathResolver pathResolver = new PathResolver();
    String finalPath = "";

    try {
      finalPath = file.getCanonicalPath();
    } catch (IOException e) {
      LOGGER.info("Could not get canonical path, trying with absolute path and PathResolver", e);
      finalPath = pathResolver.resolvePath(file.getAbsolutePath());
    }
    System.out.println("FILE: " + file.getName() + "FINALPATH: " + finalPath);
    return finalPath;
  }

  public Set<FileInfo> getFilesList() {
    return filesList;
  }

  public String getServer() {
    return server;
  }

  public List<Plugin> getPlugins() {
    return plugins;
  }
}
