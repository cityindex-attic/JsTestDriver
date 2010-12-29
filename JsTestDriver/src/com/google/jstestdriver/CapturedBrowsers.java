/*
 * Copyright 2008 Google Inc.
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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.jstestdriver.browser.BrowserCaptureEvent;
import com.google.jstestdriver.browser.BrowserCaptureEvent.Event;
import com.google.jstestdriver.browser.BrowserIdStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
@Singleton
public class CapturedBrowsers extends Observable {

  private final Map<String, SlaveBrowser> slaves = new ConcurrentHashMap<String, SlaveBrowser>();
  private final BrowserIdStrategy idStrategy;
  
  @Inject
  public CapturedBrowsers(BrowserIdStrategy idStrategy) {
    this.idStrategy = idStrategy;
  }

  public SlaveBrowser getBrowser(String id) {
    return slaves.get(id);
  }

  public String getUniqueId() {
    return Long.toString(idStrategy.nextId());
  }

  public void addSlave(SlaveBrowser slave) {
    slaves.put(slave.getId(), slave);
    setChanged();
    notifyObservers(new BrowserCaptureEvent(Event.CONNECTED, slave));
  }

  public Collection<SlaveBrowser> getSlaveBrowsers() {
    return slaves.values();
  }

  public List<BrowserInfo> getBrowsers() {
    List<BrowserInfo> browsersList = new ArrayList<BrowserInfo>();

    for (SlaveBrowser slave : getSlaveBrowsers()) {
      browsersList.add(slave.getBrowserInfo());
    }
    return browsersList;
  }

  public void removeSlave(String id) {
    SlaveBrowser slave = slaves.remove(id);
    setChanged();
    notifyObservers(new BrowserCaptureEvent(Event.DISCONNECTED, slave));
  }
  
  @Override
  public String toString() {
    return String.format("CapturedBrowsers(%s)", slaves);
  }
}
