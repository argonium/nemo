/*
 * Written by Mike Wallace (mfwallace at gmail.com).  Available
 * on the web site http://mfwallace.googlepages.com/.
 * 
 * Copyright (c) 2006 Mike Wallace.
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package io.miti.nemo.app;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Show the search results on the first tab.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class ResultsRenderer extends DefaultTableCellRenderer
{
  /**
   * The number of directories.
   */
  private int numDirs = 0;
  
  /**
   * The default serial version number.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * Default constructor.
   */
  public ResultsRenderer()
  {
    super();
  }
  
  
  /**
   * Return the renderer.
   * 
   * @param table the table
   * @param value the value
   * @param isSelected whether this is selected
   * @param hasFocus whether this has focus
   * @param row the row number
   * @param column the column number
   * @return the renderer
   */
  public Component getTableCellRendererComponent(final JTable table,
                                                 final Object value,
                                                 final boolean isSelected,
                                                 final boolean hasFocus,
                                                 final int row,
                                                 final int column)
  {
    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    
    // Right align the size column
    if (column == 2)
    {
      setHorizontalAlignment(RIGHT);
    }
    else
    {
      setHorizontalAlignment(LEFT);
    }
    
    // Do this just for folders
    if (row < numDirs)
    {
      setForeground(Color.BLUE);
    }
    else
    {
      setForeground(Color.BLACK);
    }
    
    if (isSelected)
    {
      setBackground(Color.CYAN);
    }
    else
    {
      setBackground(Color.WHITE);
    }
    
    return this;
  }
  
  
  /**
   * Set the number of directories.
   * 
   * @param nNumDirs the number of directories
   */
  public void setNumberOfDirectories(final int nNumDirs)
  {
    numDirs = nNumDirs;
  }
}
