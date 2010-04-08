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

import com.google.inject.ImplementedBy;

/**
 * Manages the starting and canceling of a {@link HeartBeat}.
 * @author corysmith
 *
 */
@ImplementedBy(SimpleHeartBeatManager.class)
public interface HeartBeatManager {

  // TODO(corysmith): Clean this up. The timer should be started without needing to 
  // explicitly start it.
  /**
   * Necessary pre-call to ensure the timer is started for the HeartBeat to be scheduled.
   */
  public void startTimer();

  /**
   * Cancels the {@link HeartBeat}.
   */
  public void cancelTimer();

  /**
   * Starts the {@link HeartBeat} for a given server, browser and session.
   */
  public void startHeartBeat(String baseUrl, String browserId, String sessionId);

}