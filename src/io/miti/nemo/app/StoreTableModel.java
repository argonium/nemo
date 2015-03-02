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

import io.miti.nemo.common.StoreInfo;
import io.miti.nemo.common.Utility;

import javax.swing.table.AbstractTableModel;

import java.util.List;

/**
 * The model for drawing the results table.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class StoreTableModel extends AbstractTableModel
{
  /**
   * Set up the version number.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * The names of the columns.
   */
  private static final String[] columnNames = {"Folder", "Date", "Label"};
  
  /**
   * The data stored in each row.
   */
  private List<StoreInfo> rowData = null;
  
  /**
   * The current row count.
   */
  private int nRowCount = 0;
  
  
  /**
   * Returns the number of rows.
   * 
   * @return the number of rows
   */
  public int getRowCount()
  {
    return nRowCount;
  }
  
  
  /**
   * Returns the number of columns.
   * 
   * @return the number of columns
   */
  public int getColumnCount()
  {
    return 3;
  }
  
  
  /**
   * Returns the name of the column.
   * 
   * @param col the column to get the name for
   * @return the name of the specified column
   */
  public String getColumnName(final int col)
  {
    return columnNames[col];
  }
  
  
  /**
   * Retrieves a value from a row/column.
   * 
   * @param rowIndex the row index
   * @param columnIndex the column index
   * @return the value at the specified row/column
   */
  public Object getValueAt(final int rowIndex,
                           final int columnIndex)
  {
    StoreInfo info = rowData.get(rowIndex);
    switch (columnIndex)
    {
      case 0:
        return info.getStoreFilename();
        
      case 1:
        return Utility.formatDate(info.getLastModified());
      
      case 2:
        return info.getStoreName();
      
      default:
        return "x";
    }
  }
  
  
  /**
   * Set the row data.
   * 
   * @param listData the data to fill in a row
   */
  public void setRowData(final List<StoreInfo> listData)
  {
    // Empty the previous data
    rowData = null;
    
    if (listData == null)
    {
      nRowCount = 0;
    }
    else
    {
      nRowCount = listData.size();
      if (nRowCount > 0)
      {
        rowData = listData;
      }
    }
  }
  
  
  /**
   * Return the requested row object.
   * 
   * @param row the index of the object to return
   * @return the requested object
   */
  public StoreInfo getRow(final int row)
  {
    if (rowData == null)
    {
      return null;
    }
    else if ((row < 0) || (row >= rowData.size()))
    {
      return null;
    }
    
    return rowData.get(row);
  }
  
  
  /**
   * Return the requested row object.
   * 
   * @param volume the volume name to match on
   * @return the requested object
   */
  public StoreInfo getRowByVolumeName(final String volume)
  {
    // Check the data and the method parameter
    if (rowData == null)
    {
      return null;
    }
    else if (volume == null)
    {
      return null;
    }
    
    // Traverse over the rows, looking for a match on volume name
    for (StoreInfo info : rowData)
    {
      if (info.getStoreName().equals(volume))
      {
        return info;
      }
    }
    
    // No match found, so return null
    return null;
  }
}
