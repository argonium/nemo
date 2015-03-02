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

import io.miti.nemo.common.SearchResult;
import io.miti.nemo.common.Utility;

import javax.swing.table.AbstractTableModel;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;

/**
 * The model for drawing the results table.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class DetailsTableModel extends AbstractTableModel
{
  /**
   * Set up the version number.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * The names of the columns.
   */
  private static final String[] columnNames = {"Name", "Last Modified", "Size"};
  
  /**
   * The data stored in each row.
   */
  private List<SearchResult> rowData = null;
  
  /**
   * The NumberFormat instance.
   */
  private NumberFormat nf = null;
  
  /**
   * The current row count.
   */
  private int nRowCount = 0;
  
  
  /**
   * Default constructor.
   */
  public DetailsTableModel()
  {
    super();
    
    // Instantiate the NumberFormat instance, for formatting
    // the displayed file size for results.
    nf = NumberFormat.getInstance();
  }
  
  
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
    SearchResult result = rowData.get(rowIndex);
    switch (columnIndex)
    {
      case 0:
        return result.getName();
      
      case 1:
        return getDate(result.getLastModified());
        
      case 2:
        return getFileSize(result.getFileSize());
      
      default:
        return "x";
    }
  }
  
  
  /**
   * Return the file size as a string.
   * 
   * @param size the file size
   * @return the size as a string
   */
  private String getFileSize(final long size)
  {
    return (size < 0) ? "" : nf.format(size);
  }
  
  
  /**
   * Return the date as a String.
   * 
   * @param time the time to convert
   * @return the date as a string
   */
  private String getDate(final long time)
  {
    return (time <= 0) ? "" : Utility.formatDate(time);
  }
  
  
  /**
   * Set the row data.
   * 
   * @param listData the data to fill in a row
   */
  public void setRowData(final List<SearchResult> listData)
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
  public SearchResult getRow(final int row)
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
   * Sort the data.
   */
  public void sortData()
  {
    if (rowData != null)
    {
      Collections.sort(rowData);
      fireTableDataChanged();
    }
  }
}
