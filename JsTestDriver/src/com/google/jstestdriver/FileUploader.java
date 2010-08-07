// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.jstestdriver.JsonCommand.CommandType;
import com.google.jstestdriver.browser.BrowserFileSet;
import com.google.jstestdriver.util.StopWatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handles the uploading of files.
 *
 * @author corysmith@google.com (Cory Smith)
 */
public class FileUploader {
  public static final int CHUNK_SIZE = 50;

  private final StopWatch stopWatch;
  private final Gson gson = new Gson();
  private final Server server;
  private final String baseUrl;
  private final FileLoader fileLoader;
  private final JsTestDriverFileFilter filter;
  private static final Logger logger = LoggerFactory.getLogger(FileUploader.class);

  @Inject
  public FileUploader(StopWatch stopWatch, Server server, @Named("server") String baseUrl,
      FileLoader fileLoader, JsTestDriverFileFilter filter) {
    this.stopWatch = stopWatch;
    this.server = server;
    this.baseUrl = baseUrl;
    this.fileLoader = fileLoader;
    this.filter = filter;
  }

  /** Determines what files have been changed as compared to the server. */
  public List<FileInfo> determineFileSet(String browserId, Set<FileInfo> files,
      ResponseStream stream) {
    stopWatch.start("get upload set %s", browserId);
    Map<String, String> fileSetParams = new LinkedHashMap<String, String>();

    fileSetParams.put("id", browserId);
    fileSetParams.put("fileSet", gson.toJson(files));
    String postResult = server.post(baseUrl + "/fileSet", fileSetParams);
    stopWatch.stop("get upload set %s", browserId);

    if (postResult.length() > 0) {
      stopWatch.start("resolving upload %s", browserId);
      BrowserFileSet browserFileSet = gson.fromJson(postResult, BrowserFileSet.class);

      // reset if there are extra files o=in the browser
      boolean shouldReset = !browserFileSet.getExtraFiles().isEmpty();
      Set<FileInfo> finalFilesToUpload = new LinkedHashSet<FileInfo>();
      if (shouldReset) {
        reset(browserId, stream);
        finalFilesToUpload.addAll(browserFileSet.getFilesToUpload());
      } else {
        for (FileInfo file : browserFileSet.getFilesToUpload()) {
          finalFilesToUpload.addAll(findDependencies(file));
        }
      }
      stopWatch.stop("resolving upload %s", browserId);
      return fileLoader.loadFiles(finalFilesToUpload, shouldReset);
    }
    return Collections.<FileInfo>emptyList();
  }

  /** Uploads the changed files to the server and the browser. */
  public void uploadFileSet(String browserId, Set<FileInfo> files, ResponseStream stream) {

    stopWatch.start("determineFileSet(%s)", browserId);
    final List<FileInfo> loadedFiles = determineFileSet(browserId, files, stream);
    stopWatch.stop("determineFileSet(%s)", browserId);

    if (!loadedFiles.isEmpty()) {
      stopWatch.start("upload to server %s", browserId);
      uploadToServer(loadedFiles);
      stopWatch.stop("upload to server %s", browserId);

      stopWatch.start("uploadToTheBrowser %s", browserId);
      uploadToTheBrowser(browserId, stream, loadedFiles);
      stopWatch.stop("uploadToTheBrowser %s", browserId);
    }
  }

  /** Uploads files to the browser. */
  public void uploadToTheBrowser(String browserId, ResponseStream stream,
      List<FileInfo> loadedFiles) {
    List<FileSource> filesSrc = Lists.newLinkedList(filterFilesToLoad(loadedFiles));
    int numberOfFilesToLoad = filesSrc.size();
    for (int i = 0; i < numberOfFilesToLoad; i += CHUNK_SIZE) {
      int chunkEndIndex = Math.min(i + CHUNK_SIZE, numberOfFilesToLoad);
      List<String> loadParameters = new LinkedList<String>();
      List<FileSource> filesToLoad = filesSrc.subList(i, chunkEndIndex);

      loadParameters.add(gson.toJson(filesToLoad));
      loadParameters.add("false");
      JsonCommand cmd = new JsonCommand(CommandType.LOADTEST, loadParameters);
      Map<String, String> loadFileParams = new LinkedHashMap<String, String>();

      loadFileParams.put("id", browserId);
      loadFileParams.put("data", gson.toJson(cmd));
      logger.debug("Sending LOADTEST for {}", browserId);
      server.post(baseUrl + "/cmd", loadFileParams);
      String jsonResponse = server.fetch(baseUrl + "/cmd?id=" + browserId);
      StreamMessage message = gson.fromJson(jsonResponse, StreamMessage.class);
      Response response = message.getResponse();

      logger.debug("LOADTEST finished for ({}) {}", browserId, response.getBrowser());
      stream.stream(response);
    }
  }

  private void uploadToServer(final List<FileInfo> loadedFiles) {
    Map<String, String> uploadFileParams = new LinkedHashMap<String, String>();
    uploadFileParams.put("data", gson.toJson(loadedFiles));
    server.post(baseUrl + "/fileSet", uploadFileParams);
  }

  private void reset(String browserId, ResponseStream stream) {
    stopWatch.start("reset %s", browserId);
    JsonCommand cmd = new JsonCommand(CommandType.RESET, Collections.<String>emptyList());
    Map<String, String> resetParams = new LinkedHashMap<String, String>();

    resetParams.put("id", browserId);
    resetParams.put("data", gson.toJson(cmd));
    server.post(baseUrl + "/cmd", resetParams);

    logger.debug("Starting File Upload Refresh for {}", browserId);
    String jsonResponse = server.fetch(baseUrl + "/cmd?id=" + browserId);
    StreamMessage message = gson.fromJson(jsonResponse, StreamMessage.class);
    Response response = message.getResponse();
    stream.stream(response);
    logger.debug("Finished File Upload Refresh for {}", browserId);
    stopWatch.stop("reset %s", browserId);
  }

  private Collection<FileInfo> findDependencies(FileInfo file) {
    List<FileInfo> deps = new LinkedList<FileInfo>();

    // TODO(jeremiele): replace filter with a plugin
    for (String fileName : filter.resolveFilesDeps(file.getFilePath())) {
      deps.add(new FileInfo(fileName, new File(fileName).lastModified(), file.isPatch(),
          file.isServeOnly(), null));
    }
    return deps;
  }

  // TODO(corysmith): remove this function once FileInfo is used exclusively.
  // Hate static crap.
  private FileSource fileInfoToFileSource(FileInfo info) {
    if (info.getFilePath().startsWith("http://")) {
      return new FileSource(info.getFilePath(), info.getTimestamp());
    }
    return new FileSource("/test/" + info.getFilePath(), info.getTimestamp());
  }

  private List<FileSource> filterFilesToLoad(Collection<FileInfo> fileInfos) {
    List<FileSource> filteredFileSources = new LinkedList<FileSource>();

    for (FileInfo fileInfo : fileInfos) {
      if (!fileInfo.isServeOnly()) {
        filteredFileSources.add(fileInfoToFileSource(fileInfo));
      }
    }
    return filteredFileSources;
  }
}
