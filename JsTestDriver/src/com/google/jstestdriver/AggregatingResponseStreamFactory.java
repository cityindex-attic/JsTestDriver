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

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * Aggregates a set of {@link ResponseStreamFactory}s by providing an AggregatingResponseStream.
 * @author corysmith@google.com (Cory Smith)
 */
public class AggregatingResponseStreamFactory implements ResponseStreamFactory {

  private final Set<ResponseStreamFactory> factories;

  @Inject
  public AggregatingResponseStreamFactory(
      Set<ResponseStreamFactory> factories) {
    this.factories = factories;
  }

  public ResponseStream getDryRunActionResponseStream() {
    List<ResponseStream> streams = Lists.newLinkedList();
    for (ResponseStreamFactory factory : factories) {
      streams.add(factory.getDryRunActionResponseStream());
    }
    return new AggregatingResponseStream(streams);
  }

  public ResponseStream getEvalActionResponseStream() {
    List<ResponseStream> streams = Lists.newLinkedList();
    for (ResponseStreamFactory factory : factories) {
      streams.add(factory.getEvalActionResponseStream());
    }
    return new AggregatingResponseStream(streams);
  }

  public ResponseStream getResetActionResponseStream() {
    List<ResponseStream> streams = Lists.newLinkedList();
    for (ResponseStreamFactory factory : factories) {
      streams.add(factory.getResetActionResponseStream());
    }
    return new AggregatingResponseStream(streams);
  }

  public ResponseStream getRunTestsActionResponseStream(String browserId) {
    List<ResponseStream> streams = Lists.newLinkedList();
    for (ResponseStreamFactory factory : factories) {
      streams.add(factory.getRunTestsActionResponseStream(browserId));
    }
    return new AggregatingResponseStream(streams);
  }

  static class AggregatingResponseStream implements ResponseStream {

    private final List<ResponseStream> streams;

    public AggregatingResponseStream(List<ResponseStream> streams) {
      this.streams = streams;
    }

    public void finish() {
      for (ResponseStream stream : streams) {
        stream.finish();
      }
    }

    public void stream(Response response) {
      for (ResponseStream stream : streams) {
        stream.stream(response);
      }
    }
  }
}
