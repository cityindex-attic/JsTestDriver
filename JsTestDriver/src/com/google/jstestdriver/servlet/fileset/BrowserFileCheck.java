package com.google.jstestdriver.servlet.fileset;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.FileSetCacheStrategy;
import com.google.jstestdriver.SlaveBrowser;
import com.google.jstestdriver.browser.BrowserFileSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author corbinrsmith@gmail.com (Cory Smith)
 */
public class BrowserFileCheck implements FileSetRequestHandler<BrowserFileSet> {

  private static final Logger logger = LoggerFactory.getLogger(BrowserFileCheck.class);

  public static final String ACTION = "browserFileCheck";
  private final FileSetCacheStrategy strategy;

  @Inject
  public BrowserFileCheck(FileSetCacheStrategy strategy) {
    this.strategy = strategy;
  }

  public BrowserFileSet handle(SlaveBrowser browser, Collection<FileInfo> clientFiles) {
    if (browser == null) {
      logger.debug("no browser, returning empty set.");
      return new BrowserFileSet(Collections.<FileInfo>emptyList(),
          Collections.<FileInfo>emptyList());
    }
    final List<FileInfo> filesToUpdate = Lists.newLinkedList();
    final List<FileInfo> extraFiles = Lists.newLinkedList();
    // reload all files if Safari, Opera, or Konqueror, because they don't overwrite properly.
    // TODO(corysmith): Replace this with polymorphic browser classes.
    if (browser.getBrowserInfo().getName().contains("Safari")
        || browser.getBrowserInfo().getName().contains("Opera")
        || browser.getBrowserInfo().getName().contains("Konqueror")) {
      logger.debug("Resetting browser fileset to ensure proper overwriting.");
      filesToUpdate.addAll(clientFiles);
      browser.resetFileSet();
    } else {
      logger.debug("Determing files to update {}, {}", clientFiles, browser.getFileSet());
      filesToUpdate.addAll(strategy.createExpiredFileSet(clientFiles, browser.getFileSet()));
    }
    extraFiles.addAll(browser.getFileSet());
    extraFiles.removeAll(clientFiles);
    return new BrowserFileSet(filesToUpdate, extraFiles);
  }
  

  public boolean canHandle(String action) {
    return ACTION.equalsIgnoreCase(action);
  }
}