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

import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for storing and retrieving application data.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class AppData
{
  /**
   * The top-left corner of the application window.
   */
  private Point windowRoot = null;
  
  /**
   * The width and height of the application window.
   */
  private Dimension windowSize = null;
  
  /**
   * The directory to store index files in.
   */
  private String indexDir = ".";
  
  /**
   * The list of data stores.
   */
  private List<StoreInfo> stores = null;
  
  /**
   * The list of prior searches.
   */
  private List<String> searches = null;
  
  /**
   * Whether to search files, dirs, or both.
   */
  private int searchFiles = 0;
  
  /**
   * Whether to only search the selected store.
   */
  private boolean searchSelected = false;
  
  /**
   * The index of the search filter.
   */
  private int searchFilter = 0;
  
  /**
   * Whether to be case sensitive.
   */
  private boolean searchCase = false;
  
  /**
   * If we should apply a search limit.
   */
  private boolean searchLimit = false;
  
  /**
   * The max results to return.
   */
  private int searchMax = 200;
  
  /**
   * Default constructor.
   */
  public AppData()
  {
    super();
  }
  
  
  /**
   * Return the window root.
   * 
   * @return the window root
   */
  public Point getWindowRoot()
  {
    return windowRoot;
  }
  
  
  /**
   * Set the window root.
   * 
   * @param pWindowRoot the new window root
   */
  public void setWindowRoot(final Point pWindowRoot)
  {
    windowRoot = pWindowRoot;
  }
  
  
  /**
   * Set the window root's X value.
   * 
   * @param nRootX the new window root's X value
   */
  public void setWindowRootX(final int nRootX)
  {
    if (windowRoot == null)
    {
      windowRoot = new Point();
    }
    
    windowRoot.x = nRootX;
  }
  
  
  /**
   * Set the window root's Y value.
   * 
   * @param nRootY the new window root's Y value
   */
  public void setWindowRootY(final int nRootY)
  {
    if (windowRoot == null)
    {
      windowRoot = new Point();
    }
    
    windowRoot.y = nRootY;
  }
  
  
  /**
   * Return the window size.
   * 
   * @return the window size
   */
  public Dimension getWindowSize()
  {
    return windowSize;
  }
  
  
  /**
   * Set the window size.
   * 
   * @param dWindowSize the new window size
   */
  public void setWindowSize(final Dimension dWindowSize)
  {
    windowSize = dWindowSize;
  }
  
  
  /**
   * Set the window's height.
   * 
   * @param nHeight the new window height
   */
  public void setWindowHeight(final int nHeight)
  {
    if (windowSize == null)
    {
      windowSize = new Dimension();
    }
    
    windowSize.height = nHeight;
  }
  
  
  /**
   * Set the window's width.
   * 
   * @param nWidth the new window width
   */
  public void setWindowWidth(final int nWidth)
  {
    if (windowSize == null)
    {
      windowSize = new Dimension();
    }
    
    windowSize.width = nWidth;
  }
  
  
  /**
   * Return the index directory.
   * 
   * @return the index directory
   */
  public String getIndexDirectory()
  {
    return indexDir;
  }
  
  
  /**
   * Set the index directory.
   * 
   * @param sIndexDir the new index directory
   */
  public void setIndexDirectory(final String sIndexDir)
  {
    indexDir = sIndexDir;
    Utility.setIndexDirectoryName(indexDir);
  }
  
  
  /**
   * Return the list of known data stores.
   * 
   * @return the list of known data stores
   */
  public List<StoreInfo> getStoresInfo()
  {
    return stores;
  }
  
  
  /**
   * Set the list of known data stores.
   * 
   * @param listStores the list of data stores
   */
  public void setStoresInfo(final List<StoreInfo> listStores)
  {
    stores = listStores;
  }
  
  
  /**
   * Return the list of previous searches.
   * 
   * @return the list of previous searches
   */
  public List<String> getSearches()
  {
    return searches;
  }
  
  
  /**
   * Set the list of previous searches.
   * 
   * @param listSearches the list of searches
   */
  public void setSearches(final List<String> listSearches)
  {
    searches = listSearches;
  }
  
  
  /**
   * Set the Search screen defaults.
   * 
   * @param nSearchFiles whether to search files, dirs, both
   * @param bSearchSel whether to only search the selected store
   * @param nSearchFilter the filter to use
   * @param bSearchCase whether to be case-sensitive
   * @param bSearchLimit whether to limit the search results
   * @param nSearchMax the maximum results to return
   */
  public void setSearchProperties(final int nSearchFiles,
                                  final boolean bSearchSel,
                                  final int nSearchFilter,
                                  final boolean bSearchCase,
                                  final boolean bSearchLimit,
                                  final int nSearchMax)
  {
    searchFiles = nSearchFiles;
    searchSelected = bSearchSel;
    searchFilter = nSearchFilter;
    searchCase = bSearchCase;
    searchLimit = bSearchLimit;
    searchMax = nSearchMax;
  }
  
  
  /**
   * Sets whether to search files, directories or both.
   * @param nSearchFiles whether to search files, directories or both
   */
  public void setSearchFiles(final int nSearchFiles)
  {
    if (nSearchFiles < 0)
    {
      searchFiles = 0;
    }
    else if (nSearchFiles > 2)
    {
      searchFiles = 2;
    }
    else
    {
      searchFiles = nSearchFiles;
    }
  }
  
  
  /**
   * Returns whether to search files, directories or both.
   * 
   * @return whether to search files, directories or both
   */
  public int getSearchFiles()
  {
    return searchFiles;
  }
  
  
  /**
   * Sets whether to only search the selected store.
   * 
   * @param bSearchSel whether to only search the selected store
   */
  public void setSearchSelected(final boolean bSearchSel)
  {
    searchSelected = bSearchSel;
  }
  
  
  /**
   * Returns whether to only search the selected store.
   * 
   * @return whether to only search the selected store
   */
  public boolean getSearchSelected()
  {
    return searchSelected;
  }
  
  
  /**
   * Set the selected index of the search filter.
   * 
   * @param nSearchFilter the selected index of the search filter
   */
  public void setSearchFilter(final int nSearchFilter)
  {
    searchFilter = Math.max(0, Math.min(nSearchFilter, 5));
  }
  
  
  /**
   * Return the selected index of the search filter.
   * 
   * @return the selected index of the search filter
   */
  public int getSearchFilter()
  {
    return searchFilter;
  }
  
  
  /**
   * Set whether searches are case-sensitive.
   * 
   * @param bSearchCase whether searches are case-sensitive
   */
  public void setSearchCase(final boolean bSearchCase)
  {
    searchCase = bSearchCase;
  }
  
  
  /**
   * Return whether searches are case-sensitive.
   * 
   * @return whether searches are case-sensitive
   */
  public boolean getSearchCase()
  {
    return searchCase;
  }
  
  
  /**
   * Set whether to limit searches.
   * 
   * @param bSearchLimit whether to limit searches
   */
  public void setSearchLimit(final boolean bSearchLimit)
  {
    searchLimit = bSearchLimit;
  }
  
  
  /**
   * Return whether to limit searches.
   * 
   * @return whether to limit searches
   */
  public boolean getSearchLimit()
  {
    return searchLimit;
  }
  
  
  /**
   * Set the max number of results to search for.
   * 
   * @param nSearchMax the max number of results to search for
   */
  public void setSearchMax(final int nSearchMax)
  {
    searchMax = Math.max(0, Math.min(nSearchMax, 10000));
  }
  
  
  /**
   * Return the max number of results to search for.
   * 
   * @return the max number of results to search for
   */
  public int getSearchMax()
  {
    return searchMax;
  }
  
  
  /**
   * Add the store to the list.
   * 
   * @param storeInfo the store info to add to the list
   */
  public void addStore(final StoreInfo storeInfo)
  {
    // Get the last-modified time of the file
    long lastMod = Utility.getLastModifiedTime(
          Utility.getIndexedDataFile(Utility.getIndexDirectoryName(),
                                     storeInfo));
    
    // Check if it has a time
    if (lastMod > 0L)
    {
      // The time is valid, so save it and then add the storeInfo
      // to the list
      storeInfo.setLastModified(lastMod);
      stores.add(storeInfo);
    }
  }
  
  
  /**
   * Add the search term to the list.
   * 
   * @param searchTerm the search term to add to our list
   */
  public void addSearch(final String searchTerm)
  {
    searches.add(searchTerm);
  }
  
  
  /**
   * Load the data from the specified input file.
   * 
   * @param filename the input file name
   * @return the result of the operation
   */
  public boolean loadData(final String filename)
  {
    // Check the input file name
    if (!filenameIsValid(filename, false))
    {
      return false;
    }
    
    // Instantiate the list of store info objects
    stores = new ArrayList<StoreInfo>(20);
    
    // Initialize the list of searches
    searches = new ArrayList<String>(20);
    
    // Check if the file exists
    if (!fileExists(filename))
    {
      return true;
    }
    
    // Parse the XML file using SAX
    AppDataImporter importer = new AppDataImporter(filename, this);
    boolean result = importer.parseXmlFile();
    return result;
  }
  
  
  /**
   * Save the data to the specified file.
   * 
   * @param filename the output file name
   */
  public void saveData(final String filename)
  {
    // Check the input file name
    if (!filenameIsValid(filename, false))
    {
      return;
    }
    
    // Open the file for writing
    BufferedWriter out = null;
    try
    {
      // Open the writer
      out = new BufferedWriter(new FileWriter(filename));
      
      // Write out the XML header
      out.write("<?xml version=\"1.0\"?>");
      out.write(Utility.getLineSep());
      out.write("<nemo>");
      out.write(Utility.getLineSep());
      
      // Check the root value
      if (windowRoot != null)
      {
        out.write("<rootx>");
        out.write(Integer.toString(windowRoot.x));
        out.write("</rootx>");
        out.write(Utility.getLineSep());
        out.write("<rooty>");
        out.write(Integer.toString(windowRoot.y));
        out.write("</rooty>");
        out.write(Utility.getLineSep());
      }
      
      // Check the size value
      if (windowSize != null)
      {
        out.write("<sizeheight>");
        out.write(Integer.toString(windowSize.height));
        out.write("</sizeheight>");
        out.write(Utility.getLineSep());
        out.write("<sizewidth>");
        out.write(Integer.toString(windowSize.width));
        out.write("</sizewidth>");
        out.write(Utility.getLineSep());
      }
      
      // Check the directory value
      if (indexDir != null)
      {
        out.write("<indexdir>");
        out.write(Utility.insertXmlChars(indexDir));
        out.write("</indexdir>");
        out.write(Utility.getLineSep());
      }
      
      // Write out the search arguments
      writeInt(out, "search.files", searchFiles);
      writeBool(out, "search.selected", searchSelected);
      writeInt(out, "search.filter", searchFilter);
      writeBool(out, "search.case", searchCase);
      writeBool(out, "search.limit", searchLimit);
      writeInt(out, "search.max", searchMax);
      
      // Check the list of stores
      if (stores != null)
      {
        out.write("<stores>");
        out.write(Utility.getLineSep());
        
        // Iterate over the list
        for (StoreInfo info : stores)
        {
          out.write("  <store>");
          out.write(Utility.getLineSep());
          
          out.write("    <file>");
          out.write(Utility.insertXmlChars(info.getStoreFilename()));
          out.write("</file>");
          out.write(Utility.getLineSep());
          
          out.write("    <name>");
          out.write(Utility.insertXmlChars(info.getStoreName()));
          out.write("</name>");
          out.write(Utility.getLineSep());
          
          out.write("  </store>");
          out.write(Utility.getLineSep());
        }
        
        out.write("</stores>");
        out.write(Utility.getLineSep());
      }
      
      // Write out the list of prior searches
      if (searches != null)
      {
        out.write("<searches>");
        out.write(Utility.getLineSep());
        
        for (String search : searches)
        {
          out.write("  <search>");
          out.write(Utility.insertXmlChars(search));
          out.write("</search>");
          out.write(Utility.getLineSep());
        }
        
        out.write("</searches>");
        out.write(Utility.getLineSep());
      }
      
      // Write the footer
      out.write("</nemo>");
      out.write(Utility.getLineSep());
      
      // Close the output file
      out.close();
      out = null;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    finally
    {
      // Verify the file is closed
      if (out != null)
      {
        try
        {
          out.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
        out = null;
      }
    }
  }
  
  
  /**
   * Write a boolean to the XML file.
   * 
   * @param out the writer
   * @param name the parameter name
   * @param value the parameter value
   * @throws IOException on write
   */
  private void writeInt(final BufferedWriter out,
                        final String name,
                        final int value)
    throws IOException
  {
    out.write("<" + name + ">");
    out.write(Integer.toString(value));
    out.write("</" + name + ">");
    out.write(Utility.getLineSep());
  }
  
  
  /**
   * Write a boolean to the XML file.
   * 
   * @param out the writer
   * @param name the parameter name
   * @param value the parameter value
   * @throws IOException on write
   */
  private void writeBool(final BufferedWriter out,
                         final String name,
                         final boolean value)
    throws IOException
  {
    out.write("<" + name + ">");
    out.write(value ? "1" : "0");
    out.write("</" + name + ">");
    out.write(Utility.getLineSep());
  }
  
  
  /**
   * Check if the file name is valid.
   * 
   * @param filename the file name to check
   * @param mustExist whether the file must exist
   * @return whether the file name is valid
   */
  private boolean filenameIsValid(final String filename,
                                  final boolean mustExist)
  {
    // Check the string
    if ((filename == null) || (filename.length() < 1))
    {
      // Invalid file name
      return false;
    }
    
    // Create a File object
    File file = new File(filename);
    if (mustExist)
    {
      return (file.isFile());
    }
    else
    {
      return (!file.isDirectory());
    }
  }
  
  
  /**
   * Determine whether the file exists.
   * 
   * @param filename the input filename
   * @return whether the file exists
   */
  private boolean fileExists(final String filename)
  {
    // Create a File object and return whether it exists
    File file = new File(filename);
    return file.exists();
  }
}
