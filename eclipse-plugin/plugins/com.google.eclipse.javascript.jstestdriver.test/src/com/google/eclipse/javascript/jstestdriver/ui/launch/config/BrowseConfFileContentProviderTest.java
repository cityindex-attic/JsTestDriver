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
package com.google.eclipse.javascript.jstestdriver.ui.launch.config;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.jmock.Expectations;
import org.jmock.Mockery;

import com.google.eclipse.javascript.jstestdriver.ui.launch.config.BrowseConfFileContentProvider;

/**
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class BrowseConfFileContentProviderTest extends TestCase {
  
  BrowseConfFileContentProvider provider = new BrowseConfFileContentProvider();
  
  public void testGetChildrenReturnsEmptyArrayIfParentElementIsNotProject() throws Exception {
    assertEquals(0, provider.getChildren(new Object()).length);
    assertEquals(0, provider.getChildren("ASASA").length);
  }
  
  public void testGetChildrenOnlyReturnsConfFiles() throws Exception {
    Mockery mockery = new Mockery();
    final IProject mockProject = mockery.mock(IProject.class);
    final IResource nonConfResource1 = mockery.mock(IResource.class, "nonConfResource1");
    final IResource nonConfResource2 = mockery.mock(IResource.class, "nonConfResource2");
    final IResource confResource = mockery.mock(IResource.class, "confResource");
    mockery.checking(new Expectations() {{
      oneOf(mockProject).members();
      will(returnValue(new IResource[] {nonConfResource1, confResource, nonConfResource2}));
      oneOf(nonConfResource1).getName();
      will(returnValue("NotAConfFile"));
      oneOf(confResource).getName();
      will(returnValue("jsTestDriver.conf"));
      oneOf(nonConfResource2).getName();
      will(returnValue("NotAConfFile2"));
    }});
    Object[] children = provider.getChildren(mockProject);
    assertEquals(1, children.length);
    assertEquals(confResource, children[0]);
    mockery.assertIsSatisfied();
  }
  
  public void testHasChildren() throws Exception {
    assertFalse(provider.hasChildren("ASBASA"));
    Mockery mockery = new Mockery();
    final IProject mockProject = mockery.mock(IProject.class);
    final IResource nonConfResource1 = mockery.mock(IResource.class, "nonConfResource1");
    final IResource confResource = mockery.mock(IResource.class, "confResource");
    mockery.checking(new Expectations() {{
      oneOf(mockProject).members();
      will(returnValue(new IResource[] {nonConfResource1, confResource}));
      oneOf(nonConfResource1).getName();
      will(returnValue("NotAConfFile"));
      oneOf(confResource).getName();
      will(returnValue("jsTestDriver.conf"));
    }});
    assertTrue(provider.hasChildren(mockProject));
    mockery.assertIsSatisfied();
  }
}
