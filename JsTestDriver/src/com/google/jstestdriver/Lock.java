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

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 *
 */
public class Lock {

  private final Object lock = new Object();
  private volatile boolean locked;
  private String sessionId = "";
  private long lastHeartBeat = 0;

  public boolean tryLock(String sessionId) {
    synchronized (lock) {
      if (locked) {
        return false;
      }
      locked = true;
      this.sessionId = sessionId;
      return true;
    }
  }

  public void unlock(String sessionId) {
    synchronized (lock) {
      if (!locked || (!this.sessionId.equals(sessionId))) {
        throw new IllegalStateException(String.format("Unlock error [%s : %s]", this.sessionId,
            sessionId));
      }
      locked = false;
      sessionId = "";
    }
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setLastHeartBeat(long lastHeartBeat) {
    this.lastHeartBeat = lastHeartBeat;
  }

  public long getLastHeartBeat() {
    return lastHeartBeat;
  }

  public void forceUnlock() {
    synchronized (lock) {
      locked = false;
      sessionId = "";
    }
  }
}
