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
package com.google.jstestdriver.eclipse.ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Adaptation of the JUnitProgressBar. Paints rectangles on a canvas to represent the ProgressBar.
 * Allows moving the bar by a few ticks at a time, as compared to one step at a time by the 
 * original progress bar.
 *
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class TestResultProgressBar extends Canvas {
  
  private final Color passColor;
  private final Color failColor;
  private int maxCount;
  private int currentCount;
  private int barWidth;
  private Color currentColor;

  public TestResultProgressBar(Composite parent, int style) {
    super(parent, style | SWT.BORDER);
    passColor = new Color(parent.getDisplay(), 95, 191, 95);
    failColor = new Color(parent.getDisplay(), 159, 63, 63);
    currentColor = passColor;
    maxCount = 0;
    currentCount = 0;
    
    addControlListener(new ControlAdapter() {
      @Override
      public void controlResized(ControlEvent e) {
        barWidth = scale(currentCount);
        redraw();
      }
    });
    
    addPaintListener(new PaintListener() {
      public void paintControl(PaintEvent e) {
        paint(e);
      }
    });
    
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        passColor.dispose();
        failColor.dispose();
        currentColor.dispose();
      }
    });
  }
  
  protected void paint(PaintEvent e) {
    GC gc = e.gc;
    Display display = getDisplay();
    
    Rectangle rectangle = getClientArea();
    gc.fillRectangle(rectangle);
    drawBorder(gc, rectangle.x, rectangle.y, rectangle.width - 1, rectangle.height - 1,
        display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW),
        display.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
    
    gc.setBackground(currentColor);
    barWidth = Math.min(rectangle.width - 2, barWidth);
    gc.fillRectangle(1, 1, barWidth, rectangle.height - 2);
  }
  
  private void paintStep(int startX, int endX) {
    GC gc = new GC(this);
    gc.setBackground(currentColor);
    Rectangle rectangle = getClientArea();
    startX = Math.max(1, startX);
    gc.fillRectangle(startX, 1, endX - startX, rectangle.height - 2);
    gc.dispose();
  }

  private void drawBorder(GC gc, int x, int y, int width, int height, Color topLeft,
      Color bottomRight) {
    gc.setForeground(topLeft);
    gc.drawLine(x, y, x + width - 1, y);
    gc.drawLine(x, y, x, y + height - 1);
    
    gc.setForeground(bottomRight);
    gc.drawLine(x + width, y, x + width, y + height);
    gc.drawLine(x, y + height, x + width, y + height);
  }

  private int scale(int currentCount) {
    if (maxCount > 0) {
      Rectangle rectangle = getClientArea();
      if (rectangle.width > 0) {
        return currentCount * (rectangle.width - 2) / maxCount;
      }
    }
    return currentCount;
  }
  
  public void reset() {
    currentColor = passColor;
    maxCount = 0;
    barWidth = 0;
    currentCount = 0;
  }
  
  public void setMax(int max) {
    maxCount = max;
  }
  
  public void step(int ticks, boolean passed) {
    int x = barWidth;
    if (!passed) {
      currentColor = failColor;
      x = 1;
    }
    if (currentCount < maxCount) {
      currentCount += ticks;
    }
    barWidth = scale(currentCount);
    if (currentCount >= maxCount) {
      currentCount = maxCount;
      barWidth = getClientArea().width - 1;
    }
    paintStep(x, barWidth);
  }
  
}
