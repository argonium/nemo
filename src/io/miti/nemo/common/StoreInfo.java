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

import java.util.List;

/**
 * Describes a data store.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class StoreInfo
{
  /**
   * The store name.
   */
  private String storeName = null;
  
  /**
   * The store's filename.
   */
  private String storeFilename = null;
  
  /**
   * The last modified date.
   */
  private long lastModified = 0L;
  
  
  /**
   * Default constructor.
   */
  public StoreInfo()
  {
    super();
  }
  
  
  /**
   * Constructor taking the main arguments.
   * 
   * @param sName the store name
   * @param sFile the store file name
   * @param lLastMod the last modified time
   */
  public StoreInfo(final String sName,
                   final String sFile,
                   final long lLastMod)
  {
    super();
    storeName = sName;
    storeFilename = sFile;
    lastModified = lLastMod;
  }
  
  
  /**
   * Get the store filename.
   * 
   * @return the store filename
   */
  public String getStoreFilename()
  {
    return storeFilename;
  }
  
  
  /**
   * Set the store file name.
   * 
   * @param sFile the new store file name
   */
  public void setStoreFilename(final String sFile)
  {
    storeFilename = sFile;
  }
  
  
  /**
   * Return the store name.
   * 
   * @return the store name
   */
  public String getStoreName()
  {
    return storeName;
  }
  
  
  /**
   * The store name.
   * 
   * @param sName the store name
   */
  public void setStoreName(final String sName)
  {
    storeName = sName;
  }
  
  
  /**
   * Return the last modified date.
   * 
   * @return the last modified date
   */
  public long getLastModified()
  {
    return lastModified;
  }
  
  
  /**
   * Set the last modified date.
   * 
   * @param lLastModified the last modified date
   */
  public void setLastModified(final long lLastModified)
  {
    lastModified = lLastModified;
  }
  
  
  /**
   * Return a string representation of this object.
   * 
   * @return a string representation of this object
   */
  @Override
  public String toString()
  {
    // Set up the string and return it
    StringBuilder sb = new StringBuilder(100);
    sb.append("Store Name: ").append(storeName)
      .append("   File: ").append(storeFilename)
      .append("   Last Mod: ")
      .append(Utility.formatDate(lastModified));
    return sb.toString();
  }
  
  
  /**
   * Return whether the list contains a specific store.
   * 
   * @param listStores the list of stores
   * @param dir the directory
   * @return whether the list contains the directory
   */
  public static boolean listContainsStore(final List<StoreInfo> listStores,
                                          final String dir)
  {
    // Check if the list is null
    if (listStores == null)
    {
      return false;
    }
    
    // Check for a match in the list
    boolean found = false;
    for (StoreInfo info : listStores)
    {
      if (info.storeName.equalsIgnoreCase(dir))
      {
        found = true;
        break;
      }
    }
    
    // Return whether a match was found
    return found;
  }
}
