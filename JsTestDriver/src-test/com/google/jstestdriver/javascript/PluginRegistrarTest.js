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
var PluginRegistrarTest = jstestdriver.testCaseManager.TestCase('PluginRegistrarTest');


PluginRegistrarTest.prototype.testRegisterPlugin = function() {
  var pluginRegistrar = new jstestdriver.PluginRegistrar();
  pluginRegistrar.register({ name: 'default' });
  var myPlugin = new TestPlugin('myPlugin', true);

  pluginRegistrar.register(myPlugin);
  var plugin = pluginRegistrar.getPlugin('myPlugin');

  assertSame(myPlugin, plugin);
  assertEquals(2, pluginRegistrar.getNumberOfRegisteredPlugins());
};


PluginRegistrarTest.prototype.testUnregisterPlugin = function() {
  var pluginRegistrar = new jstestdriver.PluginRegistrar();
  pluginRegistrar.register({ name: 'default' });
  var myPlugin = new TestPlugin('myPlugin', true);

  pluginRegistrar.register(myPlugin);
  assertEquals(2, pluginRegistrar.getNumberOfRegisteredPlugins());
  pluginRegistrar.unregister(myPlugin);

  var plugin = pluginRegistrar.getPlugin('myPlugin');

  assertNotSame(myPlugin, plugin);
  assertSame(null, plugin);
  assertEquals(1, pluginRegistrar.getNumberOfRegisteredPlugins());
};


PluginRegistrarTest.prototype.testUnregisterPluginUnknowPlugin = function() {
  var pluginRegistrar = new jstestdriver.PluginRegistrar();
  pluginRegistrar.register({ name: 'default' });
  var myPlugin = new TestPlugin('myPlugin', true);

  pluginRegistrar.unregister(myPlugin);

  var plugin = pluginRegistrar.getPlugin('myPlugin');

  assertNotSame(myPlugin, plugin);
  assertSame(null, plugin);
  assertEquals(1, pluginRegistrar.getNumberOfRegisteredPlugins());
};


PluginRegistrarTest.prototype.testHandleAction = function() {
  var pluginRegistrar = new jstestdriver.PluginRegistrar();
  pluginRegistrar.register({ name: 'default' });
  var myFirstPlugin = new TestPlugin('myFirstPlugin', false);
  var mySecondPlugin = new TestPlugin('mySecondPlugin', true);
  var myThirdPlugin = new TestPlugin('myThirdPlugin', false)

  pluginRegistrar.register(myFirstPlugin);
  pluginRegistrar.register(mySecondPlugin);
  pluginRegistrar.register(myThirdPlugin);

  var file = 'someFile.js';
  var callback = function() {};

  pluginRegistrar.loadSource(file, callback);
  assertTrue(myFirstPlugin.called);
  assertNotNull(myFirstPlugin.file);
  assertEquals(file, myFirstPlugin.file);
  assertNotNull(myFirstPlugin.callback);
  assertEquals(callback, myFirstPlugin.callback);
  assertTrue(mySecondPlugin.called);
  assertNotNull(mySecondPlugin.file);
  assertEquals(file, mySecondPlugin.file);
  assertNotNull(mySecondPlugin.callback);
  assertEquals(callback, mySecondPlugin.callback);
  assertFalse(myThirdPlugin.called);
  assertNull(myThirdPlugin.file);
  assertNull(myThirdPlugin.callback);
};


PluginRegistrarTest.prototype.testPluginIsReplaced = function() {
  var pluginRegistrar = new jstestdriver.PluginRegistrar();
pluginRegistrar.register({ name: 'default' });
  var myPlugin = new TestPlugin('myPlugin', false);
  var myOtherPlugin = new TestPlugin('myPlugin', false);

  pluginRegistrar.register(myPlugin);
  pluginRegistrar.register(myOtherPlugin);
  assertEquals(2, pluginRegistrar.getNumberOfRegisteredPlugins());
  assertEquals(myOtherPlugin, pluginRegistrar.getPlugin(myOtherPlugin.name));
};
