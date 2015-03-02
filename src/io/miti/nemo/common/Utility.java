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

package io.miti.nemo.common;

import java.awt.Desktop;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JViewport;

/**
 * Utility class for the application.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class Utility
{
  /**
   * The list of special XML characters that need to be replaced
   * by XML entities.  This list and saXmlEntities must have the
   * same number of items.
   */
  private static final String[] saXmlChars =
    new String[] {"&", "<", ">", "\"", "\'"};
  
  /**
   * The list of XML entities that replace special XML characters.
   * This list and caXmlChars must have the same number of items.
   */
  private static final String[] saXmlEntities =
    new String[] {"&amp;", "&lt;", "&gt;", "&quot;", "&apos;"};
  
  /**
   * The format for date strings.
   */
  private static final String DATE_STR = "dd MMM yyyy  HH:mm:ss";
  
  /**
   * The date format object.
   */
  private static final SimpleDateFormat dateFormat;
  
  /**
   * Whether the current results are sorted in forward order.
   */
  private static boolean sortResultsForward = true;
  
  /**
   * The column to sort the results by.
   */
  private static int resultsSortColumn = 0;
  
  /**
   * The name of the index directory.
   */
  private static String indexDir = null;
  
  /**
   * The line separator for this OS.
   */
  private static final String lineSep;
  
  /**
   * The path separator.
   */
  private static final String pathSep;
  
  /**
   * The characters for the hex conversion.
   */
  private static final char[] hexChar = {'0', '1', '2', '3',
                                         '4', '5', '6', '7',
                                         '8', '9', 'A', 'B',
                                         'C', 'D', 'E', 'F'};
  
  /**
   * Initialize the date format object.
   */
  static
  {
    dateFormat = new SimpleDateFormat(DATE_STR);
    pathSep = System.getProperty("file.separator");
    lineSep = System.getProperty("line.separator");
  }
  
  
  /**
   * Default constructor.
   */
  private Utility()
  {
    super();
  }
  
  
  /**
   * Return the line separator for this OS.
   * 
   * @return the line separator for this OS
   */
  public static String getLineSep()
  {
    return lineSep;
  }
  
  
  /**
   * Return the path separator for this OS.
   * 
   * @return the path separator for this OS
   */
  public static String getPathSep()
  {
    return pathSep;
  }
  
  
  /**
   * Replace XML entities with characters.
   * 
   * @param word the word to update
   * @return the modified string
   */
  public static String removeXmlChars(final String word)
  {
    String outWord = word;
    
    int len = saXmlEntities.length;
    for (int i = 0; i < len; ++i)
    {
      outWord = outWord.replace(saXmlEntities[i], saXmlChars[i]);
    }
    
    // Return the modified string
    return outWord;
  }
  
  
  /**
   * Replace XML characters with entities.
   * 
   * @param word the word to update
   * @return the modified string
   */
  public static String insertXmlChars(final String word)
  {
    String outWord = word;
    
    int len = saXmlEntities.length;
    for (int i = 0; i < len; ++i)
    {
      outWord = outWord.replace(saXmlChars[i], saXmlEntities[i]);
    }
    
    // Return the modified string
    return outWord;
  }
  
  
  /**
   * Returns the last modified time of the file, or -1
   * if the file does not exist or is not a file.
   * 
   * @param file the file to check
   * @return the last modified time of the file
   */
  public static long getLastModifiedTime(final File file)
  {
    // Check if it exists and is a file
    if ((!file.exists()) || (!file.isFile()))
    {
      return -1L;
    }
    
    // Return the last modified time
    return file.lastModified();
  }
  
  
  /**
   * Returns the file size, or 0 if the file does not exist.
   * 
   * @param file the file to check
   * @return the file size
   */
  public static long getFileSize(final File file)
  {
    // Check if it exists and is a file
    if ((!file.exists()) || (!file.isFile()))
    {
      return 0L;
    }
    
    // Return the file size
    return file.length();
  }
  
  
  /**
   * Delete the specified file.
   * 
   * @param file the file to delete
   */
  public static void deleteFile(final File file)
  {
    file.delete();
  }
  
  
  /**
   * Generate the name of the index file, and return it
   * as a File object.
   * 
   * @param dir the data file directory
   * @param info the root of the file name
   * @return the name of the index file
   */
  public static File getIndexedDataFile(final String dir,
                                        final StoreInfo info)
  {
    // Generate the name
    File file = new File(dir,
        Store.computeNameFromDirectory(info.getStoreName()) + ".ser");
    
    // Return the file pointer
    return file;
  }
  
  
  /**
   * Convert a string into an integer.
   * 
   * @param sInput the input string
   * @param defaultValue the default value
   * @return the value as an integer
   */
  public static int getStringAsInteger(final String sInput,
                                       final int defaultValue)
  {
    // This is the variable that gets returned
    int value = defaultValue;
    
    // Check the input
    if (sInput == null)
    {
      return value;
    }
    
    // Trim the string
    final String inStr = sInput.trim();
    if (inStr.length() < 1)
    {
      // The string is empty
      return value;
    }
    
    // Convert the number
    try
    {
      value = Integer.parseInt(inStr);
    }
    catch (NumberFormatException nfe)
    {
      value = defaultValue;
    }
    
    // Return the value
    return value;
  }
  
  
  /**
   * Convert a string into a long.
   * 
   * @param sInput the input string
   * @param defaultValue the default value
   * @return the value as a long
   */
  public static long parseLong(final String sInput, final long defaultValue)
  {
    // This is the variable that gets returned
    long value = defaultValue;
    
    // Check the input
    if (sInput == null)
    {
      return value;
    }
    
    // Trim the string
    final String inStr = sInput.trim();
    if (inStr.length() < 1)
    {
      // The string is empty
      return value;
    }
    
    // Convert the number
    try
    {
      value = Long.parseLong(inStr);
    }
    catch (NumberFormatException nfe)
    {
      value = defaultValue;
    }
    
    // Return the value
    return value;
  }
  
  
  /**
   * Format a time as a date.
   * 
   * @param time the time to format
   * @return the time as a date
   */
  public static synchronized String formatDate(final long time)
  {
    // Check for a negative date
    if (time < 0L)
    {
      return "";
    }
    
    // Return the date/time string
    return dateFormat.format(new Date(time));
  }
  
  
  /**
   * Reset the stored values for sorting search results.
   */
  public static void resetSortParameters()
  {
    sortResultsForward = true;
    resultsSortColumn = 0;
  }
  
  
  /**
   * Return whether to sort results in ascending order.
   * 
   * @return whether to sort results in ascending order
   */
  public static boolean sortResultsForward()
  {
    return sortResultsForward;
  }
  
  
  /**
   * Return the column to sort results by.
   * 
   * @return the column to sort results by
   */
  public static int getSortColumn()
  {
    return resultsSortColumn;
  }
  
  
  /**
   * Set the column to sort results by.
   * 
   * @param nColumn column to sort results by
   */
  public static void setSortColumn(final int nColumn)
  {
    if (nColumn == resultsSortColumn)
    {
      // Same column as before, so just reverse the sort direction
      sortResultsForward = !sortResultsForward;
    }
    else
    {
      // Different sort column, so sort forward and save the sort column
      sortResultsForward = true;
      resultsSortColumn = nColumn;
    }
  }
  
  
  /**
   * Set the index directory.
   * 
   * @param sIndexDir the index directory
   */
  public static void setIndexDirectoryName(final String sIndexDir)
  {
    indexDir = sIndexDir;
  }
  
  
  /**
   * Return the name of the index directory.
   * 
   * @return the index directory name
   */
  public static String getIndexDirectoryName()
  {
    return indexDir;
  }
  
  
  /**
   * Print out the items in a list of SearchResults.
   * 
   * @param results the list of search results
   */
  public static void printSearchResults(final List<SearchResult> results)
  {
    int i = 1;
    System.out.println("\nFiles:");
    for (SearchResult line : results)
    {
      System.out.println("#" + Integer.toString(i++) + ": " + line.getName());
    }
  }
  
  
  /**
   * Return the contents of a file as a string.
   * 
   * @param file the input file
   * @return the contents of the file
   */
  public static String getFileAsText(final File file)
  {
    // Check the input parameter
    if ((file == null) || (!file.exists()) || (file.isDirectory()))
    {
      return "";
    }
    
    // Get the text of the file
    StringBuilder sb = new StringBuilder(1000);
    
    // Read the file
    BufferedReader in = null;
    try
    {
      in = new BufferedReader(new FileReader(file));
      String str;
      while ((str = in.readLine()) != null)
      {
        sb.append(str).append('\n');
      }
      
      in.close();
      in = null;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    finally
    {
      if (in != null)
      {
        try
        {
          in.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
        
        in = null;
      }
    }
    
    // Return the builder
    return sb.toString();
  }
  
  
  /**
   * Return the contents of a file as a hex dump.
   * 
   * @param file the input file
   * @return the contents of the file
   */
  public static String getFileAsHex(final File file)
  {
    // Check the input parameter
    if ((file == null) || (!file.exists()) || (file.isDirectory()))
    {
      return "";
    }
    
    // Save the size
    final int size = (int) file.length();
    
    // Get the text of the file
    StringBuilder sb = new StringBuilder(1000);
    
    // Read the input file
    FileInputStream fis = null;
    try
    {
      // Create a memory-map for the file
      fis = new FileInputStream(file);
      FileChannel fc = fis.getChannel();
      MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, size);
      
      // Declare two string builders, one for the middle section of
      // the output (hex), and one for the end section (chars)
      StringBuilder hexOutput = new StringBuilder(48);
      StringBuilder charOutput = new StringBuilder(16);
      
      // The offset for this row
      int offset = 0;
      
      // Iterate over the file, processing each character
      for (int index = 0; index < size;)
      {
        // Get the character
        byte b = mbb.get(index);
        
        // Build the middle section (each byte in hex)
        hexOutput.append(byteToHex(b)).append(' ');
        
        // Build the end section - the char, or a period
        char ch = (char) b;
        if ((ch < 32) || (ch > 126))
        {
          charOutput.append('.');
        }
        else
        {
          charOutput.append(ch);
        }
        
        // Increment the index and check if we've finished a row
        ++index;
        if (0 == (index % 16))
        {
          // Add the current row to the builder
          addHexRow(sb, offset, hexOutput, charOutput);
          
          // Reset the variables
          offset = index;
          hexOutput.setLength(0);
          charOutput.setLength(0);
        }
      }
      
      // Add the last row, if necessary
      if (hexOutput.length() > 0)
      {
        addHexRow(sb, offset, hexOutput, charOutput);
      }
      
      // Close the map
      fis.close();
      fis = null;
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      System.err.println("IOException: " + e.getMessage());
      e.printStackTrace();
    }
    finally
    {
      if (fis != null)
      {
        try
        {
          fis.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
        fis = null;
      }
    }
    
    // Return the builder
    return sb.toString();
  }
  
  
  /**
   * Convert a byte into a hex string.
   * 
   * @param b the byte
   * @return the byte as a hex string
   */
  private static String byteToHex(final byte b)
  {
    // Turn the byte into a two character hex string
    StringBuilder sb = new StringBuilder(2);
    sb.append(hexChar[(b & 0xF0) >>> 4]);
    sb.append(hexChar[b & 0x0F]);
    return sb.toString();
  }
  
  
  /**
   * Add a row to the output.
   * 
   * @param output the output string builder to append to
   * @param offset the offset for the row
   * @param hexOutput the middle section
   * @param charOutput the end section
   */
  private static void addHexRow(final StringBuilder output,
                                final int offset,
                                final StringBuilder hexOutput,
                                final StringBuilder charOutput)
  {
    // Build the output row
    output.append(getOffsetColumn(offset)).append("  ")
          .append(hexOutput.toString());
    
    // Check the length of hexOutput (the last row is usually
    // too short, if the file length is not a multiple of 16)
    if (hexOutput.length() < 48)
    {
      for (int i = hexOutput.length(); i < 48; ++i)
      {
        output.append(' ');
      }
    }
    
    // Add the final section
    output.append(' ').append(charOutput.toString()).append('\n');
  }
  
  
  /**
   * Ensure that a particular table cell is visible.
   * 
   * @param table the table
   * @param rowIndex the row index
   * @param vColIndex the column index
   */
  public static void scrollToVisible(final JTable table,
                                     final int rowIndex,
                                     final int vColIndex)
  {
    // Check the parent
    if (!(table.getParent() instanceof JViewport))
    {
      return;
    }
    
    // Get the parent viewport
    JViewport viewport = (JViewport) table.getParent();
    
    // This rectangle is relative to the table where the
    // northwest corner of cell (0,0) is always (0,0).
    final Rectangle rect = table.getCellRect(rowIndex, vColIndex, true);
    
    // The location of the viewport relative to the table
    final Point pt = viewport.getViewPosition();
    
    // Translate the cell location so that it is relative
    // to the view, assuming the northwest corner of the
    // view is (0,0)
    rect.setLocation(rect.x - pt.x, rect.y - pt.y);
    
    // Scroll the area into view
    viewport.scrollRectToVisible(rect);
  }
  
  
  /**
   * Build the string for the offset column.
   * 
   * @param offset the file offset
   * @return a string of the file offset
   */
  private static String getOffsetColumn(final int offset)
  {
    // This is the length of the output column
    final int outputLen = 10;
    
    // Check if we're already at the destination length
    String sOffset = Integer.toString(offset);
    if (sOffset.length() >= outputLen)
    {
      // We are, so return
      return sOffset;
    }
    
    // We need to prepend zeroes to make the output string long enough
    StringBuilder sb = new StringBuilder(outputLen);
    final int diff = outputLen - sOffset.length();
    for (int i = 0; i < diff; ++i)
    {
      sb.append('0');
    }
    
    // Now add the offset value to the end
    sb.append(sOffset);
    
    // Return the string
    return sb.toString();
  }
  
  
  /**
   * Open the specified search result.
   * 
   * @param result the search result to open
   * @param frame the parent frame
   */
  public static void openResult(final SearchResult result,
                                final javax.swing.JFrame frame)
  {
    // Check for a null result
    if (result == null)
    {
      return;
    }
    
    // Create a File object and then open it
    File file = new File(result.getPath(), result.getName());
    openFile(file, frame);
  }
  
  
  /**
   * Open the specified file.
   * 
   * @param file the file to open
   * @param frame the parent frame
   */
  public static void openFile(final File file,
                              final javax.swing.JFrame frame)
  {
    // Check the file argument
    if (file == null)
    {
      JOptionPane.showMessageDialog(frame,
          "The file to open is null",
          "Invalid File", JOptionPane.ERROR_MESSAGE);
      return;
    }
    else if (!file.exists())
    {
      JOptionPane.showMessageDialog(frame,
                                    "The file " + file.getPath() + " was not found",
                                    "File Not Found", JOptionPane.ERROR_MESSAGE);
      return;
    }
    
    // Handle it differently if the OS is Windows
    if (SystemInfo.isWindows())
    {
      try
      {
        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " +
                                  file.getAbsolutePath());
      }
      catch (IOException ioe)
      {
        System.err.println("IOException opening file " + file.getAbsolutePath() +
                           ": " + ioe.getMessage());
      }
      
      return;
    }
    
    // Use the Java method to opening the file
    if (Desktop.isDesktopSupported())
    {
      Desktop desktop = Desktop.getDesktop();
      if (desktop.isSupported(Desktop.Action.OPEN))
      {
        try
        {
          // System.out.println("Opening " + file.getPath());
          desktop.open(file);
        }
        catch (IllegalArgumentException iae)
        {
          JOptionPane.showMessageDialog(frame,
              "This file was not found", "Error",
              JOptionPane.ERROR_MESSAGE);
        }
        catch (UnsupportedOperationException uoe)
        {
          JOptionPane.showMessageDialog(frame,
              "The Open action is not supported on this platform",
              "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException ioe)
        {
          JOptionPane.showMessageDialog(frame,
              "This file type has no associated application", "Error",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }
  
  
  /**
   * Return whether the string is '1'.
   * 
   * @param word the string to compare to '1'
   * @return whether the string is '1'
   */
  public static boolean getStringAsBoolean(final String word)
  {
    if ((word == null) || (word.length() < 1))
    {
      return false;
    }
    
    return (word.equals("1"));
  }
}
