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

import java.io.Serializable;

/**
 * Class to encapsulate a data store.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class Store implements Serializable
{
  /**
   * The serial version number.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * The name of the data store.
   */
  private String name = null;
  
  /**
   * The name of the root directory.
   */
  private String dir = null;
  
  /**
   * The data for this directory.
   */
  private Node data = null;
  
  
  /**
   * Default constructor.
   */
  public Store()
  {
    super();
  }
  
  
  /**
   * Compute the default volume name for a directory.
   * 
   * @param directory the directory name
   * @return the default volume name for the directory
   */
  public static String computeNameFromDirectory(final String directory)
  {
    // The string that gets returned
    StringBuilder sb = new StringBuilder(20);
    
    // Check the directory name
    if ((directory == null) || (directory.length() < 1))
    {
      return sb.toString();
    }
    
    // Build the string
    char ch;
    final int len = directory.length();
    for (int i = 0; i < len; ++i)
    {
      // Get the current character
      ch = directory.charAt(i);
      
      // See if it's a letter or digit
      if (Character.isLetterOrDigit(ch))
      {
        // It is, so save it
        sb.append(ch);
      }
      else
      {
        // Use another character
        sb.append('-');
      }
    }
    
    // Return the string
    return sb.toString();
  }
  
  
  /**
   * Construct an object using the store name and root directory.
   * 
   * @param sName the store name
   * @param sDir the root directory
   */
  public Store(final String sName, final String sDir)
  {
    super();
    name = sName;
    dir = sDir;
  }
  
  
  /**
   * Return the name of the data store.
   * 
   * @return the name of the data store
   */
  public String getName()
  {
    return name;
  }
  
  
  /**
   * Return the directory name.
   * 
   * @return the directory name
   */
  public String getDirectory()
  {
    return dir;
  }
  
  
  /**
   * Set the data store for this object.
   * 
   * @param dataStore the data store to save
   */
  public void setDataStore(final Node dataStore)
  {
    data = dataStore;
  }
  
  
  /**
   * Return the data store for this object.
   * 
   * @return the data store for this object
   */
  public Node getDataStore()
  {
    return data;
  }
  
  
  /**
   * Return this object as a text string, for saving to a file.
   * 
   * @param sb the string builder
   * @return this object as a text string
   */
  public String toTextString(final StringBuilder sb)
  {
    sb.setLength(0);
    sb.append(Integer.toString(name.length()))
      .append(',').append(name).append(',').append(dir);
    
    return sb.toString();
  }
  
  
  /**
   * Return a string representing this object.
   * 
   * @return this object as a string
   */
  @Override
  public String toString()
  {
    // Declare the string builder
    StringBuilder sb = new StringBuilder(100);
    
    // Build the string
    sb.append("Name: '").append(name)
      .append("'   Directory: '").append(dir).append('\'');
    
    // Return the string
    return sb.toString();
  }
  
  
  /**
   * Parse the line from the text file to generate a Store.
   * 
   * @param line the first line read from the input file
   * @return the generated Store
   */
  public static Store parseLine(final String line)
  {
    Store store = new Store();
    if ((line == null) || (line.length() < 1))
    {
      return store;
    }
    
    // Parse the fields
    int commaIndex = line.indexOf(',');
    final int nameLen = Integer.parseInt(line.substring(0, commaIndex));
    commaIndex++;
    StringBuilder sb = new StringBuilder(nameLen);
    for (int index = commaIndex; index < (commaIndex + nameLen); ++index)
    {
      sb.append(line.charAt(index));
    }
    store.name = sb.toString();
    
    commaIndex = commaIndex + nameLen + 1;
    store.dir = line.substring(commaIndex);
    
    store.data = null;
    
    // Return the store
    return store;
  }
}
