/*
 * Copyright 2011 Google Inc.
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * The user defined configuration source.
 * 
 * @author Cory Smith (corbinrsmith@gmail.com)
 */
public class UserConfigurationSource implements ConfigurationSource {

  private final File configurationFile;

  /**
   * @param configuration The absolute file containing the configuration.
   */
  public UserConfigurationSource(File configuration) {
    this.configurationFile = configuration;
  }

  /** {@inheritDoc} */
  public File getParentFile() {
    return configurationFile.getParentFile();
  }

  /** {@inheritDoc} */
  public Configuration parse(File basePath, YamlParser configParser) throws FileNotFoundException {
    return configParser.parse(
        new InputStreamReader(new FileInputStream(configurationFile), Charset.defaultCharset()),
        basePath);
  }

  /** {@inheritDoc} */
  public String getName() {
    return configurationFile.getName();
  }
}
