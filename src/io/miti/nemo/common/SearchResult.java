/**
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

/**
 * Encapsulate the search results from searches.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class SearchResult implements Comparable<SearchResult>
{
  /**
   * The name of this file or directory.
   */
  private String name = null;
  
  /**
   * The last modified time.  -1 for directories.
   */
  private long lastModified = -1L;
  
  /**
   * The file size.  -1 for directories.
   */
  private long fileSize = -1L;
  
  /**
   * The path for this object.
   */
  private String path = null;
  
  /**
   * The volume for this object.
   */
  private String volume = null;
  
  /**
   * Whether this is a directory.
   */
  private boolean isDirectory = false;
  
  
  /**
   * Default constructor.
   */
  public SearchResult()
  {
    super();
  }
  
  
  /**
   * Return the file size.
   * 
   * @return the size of the file
   */
  public long getFileSize()
  {
    return fileSize;
  }
  
  
  /**
   * Set the file size.
   * 
   * @param lFileSize the file size
   */
  public void setFileSize(final long lFileSize)
  {
    fileSize = lFileSize;
  }
  
  
  /**
   * Return the last-modified date.
   * 
   * @return the last-modified date
   */
  public long getLastModified()
  {
    return lastModified;
  }
  
  
  /**
   * Set the last-modified date.
   * 
   * @param lLastModified the last-modified date
   */
  public void setLastModified(final long lLastModified)
  {
    lastModified = lLastModified;
  }
  
  
  /**
   * Return the file or directory name.
   * 
   * @return the name of the file or directory
   */
  public String getName()
  {
    return name;
  }
  
  
  /**
   * Set the name of the file or directory.
   * 
   * @param sName the name of the file or directory
   */
  public void setName(final String sName)
  {
    name = sName;
  }
  
  
  /**
   * Return the path.
   * 
   * @return the path
   */
  public String getPath()
  {
    return path;
  }
  
  
  /**
   * Set the path for this file or directory.
   * 
   * @param sPath the path name
   */
  public void setPath(final String sPath)
  {
    path = sPath;
  }
  
  
  /**
   * Return the volume name.
   * 
   * @return the volume name
   */
  public String getVolume()
  {
    return volume;
  }
  
  
  /**
   * Set the volume name.
   * 
   * @param sVolume the volume name
   */
  public void setVolume(final String sVolume)
  {
    volume = sVolume;
  }
  
  
  /**
   * Return whether this points to a directory.
   * 
   * @return whether this is a directory
   */
  public boolean isDirectory()
  {
    return isDirectory;
  }
  
  
  /**
   * Set whether this is a directory.
   * 
   * @param bIsDirectory whether this is a directory
   */
  public void setDirectory(final boolean bIsDirectory)
  {
    isDirectory = bIsDirectory;
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
    sb.append("Name: ").append(name)
      .append("    Volume: ").append(volume)
      .append("\nPath: ").append(path)
      .append("  Last-Mod: ").append(Long.toString(lastModified))
      .append("  Size: ").append(Long.toString(fileSize));
    
    // Return the string
    return sb.toString();
  }
  
  
  /**
   * Compare this to another object.
   * 
   * @param obj the object to compare to
   * @return the result of the comparison
   */
  @Override
  public int compareTo(final SearchResult obj)
  {
    // Store which of the two objects are directories
    boolean dir1 = isDirectory();
    boolean dir2 = obj.isDirectory();
    
    // We list directories before files, so check for the cases where
    // one is a directory and the other is not
    if (dir1 && !dir2)
    {
      return -1;
    }
    else if (!dir1 && dir2)
    {
      return 1;
    }
    else
    {
      boolean sortAscending = Utility.sortResultsForward();
      int columnIndex = Utility.getSortColumn();
      
      switch (columnIndex)
      {
        case 0:
          return compareStringString(getName(), obj.getName(), false, sortAscending);
        
        case 1:
          return compareLongLong(getLastModified(), obj.getLastModified(), sortAscending);
        
        case 2:
          return compareLongLong(getFileSize(), obj.getFileSize(), sortAscending);
        
        case 3:
          return compareStringString(getVolume(), obj.getVolume(), true, sortAscending);
        
        case 4:
          return compareStringString(getPath(), obj.getPath(), false, sortAscending);
        
        default:
          break;
      }
    }
    
    return 0;
  }
  
  
  /**
   * Method to compare two Strings, and return -1 if
   * value1 < value2 and we're sorting in ascending
   * order.  Return 0 if equal.
   * 
   * @param value1 the first value to compare
   * @param value2 the second value to compare
   * @param caseSensitive whether the search should be case sensitive
   * @param sortAscending whether the sort order is ascending
   * @return -1 if value1 < value2 and sortAscending is true
   */
  private static int compareStringString(final String value1,
                                         final String value2,
                                         final boolean caseSensitive,
                                         final boolean sortAscending)
  {
    final String s1 = (caseSensitive ? value1 : value1.toLowerCase());
    final String s2 = (caseSensitive ? value2 : value2.toLowerCase());
    
    // See if the values are equal
    int result = s1.compareTo(s2);
    if (result == 0)
    {
      return 0;
    }
    else if (sortAscending)
    {
      return result;
    }
    
    return (-result);
  }
  
  
  /**
   * Method to compare two longs, and return -1 if
   * value1 < value2 and we're sorting in ascending
   * order.  Return 0 if equal.
   * 
   * @param value1 the first value to compare
   * @param value2 the second value to compare
   * @param sortAscending whether the sort order is ascending
   * @return -1 if value1 < value2 and sortAscending is true
   */
  private static int compareLongLong(final long value1,
                                     final long value2,
                                     final boolean sortAscending)
  {
    // See if the values are equal
    if (value1 == value2)
    {
      return 0;
    }
    
    boolean compare = (value1 < value2);
    return ((compare == sortAscending) ? -1 : 1);
  }
  
  
  /**
   * Returns whether this object equals the parameter.
   * 
   * @param obj the object to compare this to
   * @return whether the objects are equal
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj)
  {
    // Verify the argument is not null, and is a SearchResult
    if ((obj == null) || (!(obj instanceof SearchResult)))
    {
      return false;
    }
    
    // Compare the objects
    SearchResult sr = (SearchResult) obj;
    return (this.compareTo(sr) == 0);
  }
  
  
  /**
   * Return whether this object equals the parameter.
   * 
   * @param result the object to compare this to
   * @return whether the two objects are equal
   */
  public boolean isEqualTo(final SearchResult result)
  {
    if (result == null)
    {
      return false;
    }
    
    return ((name.equals(result.name)) &&
            (lastModified == result.lastModified) &&
            (fileSize == result.fileSize) &&
            (path.equals(result.path)) &&
            (volume.equals(result.volume)) &&
            (isDirectory == result.isDirectory));
  }
  
  
  /**
   * Return the hashcode for this object.
   * 
   * @return the hashcode for this object
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    return super.hashCode();
  }
}
