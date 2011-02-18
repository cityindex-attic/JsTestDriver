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

import com.google.jstestdriver.Flags;

/**
 * Defines the default configuration source.
 *
 * @author Cory Smith (corbinrsmith@gmail.com) 
 */
public class DefaultConfigurationSource implements ConfigurationSource {

  /** {@inheritDoc} */
  public File getParentFile() {
    return new File(".").getAbsoluteFile();
  }

  /** {@inheritDoc} */
  public Configuration parse(File basePath, YamlParser configParser) {
    File configFile = new File(Flags.DEFAULT_CONFIG_NAME).getAbsoluteFile();
    if (!configFile.exists()) {
      return new DefaultConfiguration(basePath);
    }
    try {
      return configParser.parse(
          new InputStreamReader(new FileInputStream(configFile), Charset.defaultCharset()),
          basePath);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  /** {@inheritDoc} */
  public String getName() {
    return Flags.DEFAULT_CONFIG_NAME;
  }
}
