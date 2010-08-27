package com.google.jstestdriver.servlet.fileset;

import com.google.common.collect.Sets;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.FileSetCacheStrategy;
import com.google.jstestdriver.FilesCache;
import com.google.jstestdriver.SlaveBrowser;

import java.util.Collection;
import java.util.Set;

/**
 * @author corbinrsmith@gmail.com (Cory Smith)
 */
public class ServerFileCheck implements FileSetRequestHandler<Set<FileInfo>>{

  public static final String ACTION = "serverFileCheck";
  private final FilesCache filesCache;
  private final FileSetCacheStrategy strategy;

  /**
   * @param filesCache
   */
  public ServerFileCheck(FilesCache filesCache, FileSetCacheStrategy strategy) {
    this.filesCache = filesCache;
    this.strategy = strategy;
  }

  public Set<FileInfo> handle(SlaveBrowser browser, Collection<FileInfo> clientFiles) {
    return strategy.createExpiredFileSet(clientFiles, Sets.newHashSet(filesCache.getAllFileInfos()));
  }

  public boolean canHandle(String action) {
    return ACTION.equalsIgnoreCase(action);
  }
}