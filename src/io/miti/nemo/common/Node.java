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

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A directory or file node.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class Node implements Serializable
{
  /**
   * The serial version number.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * The name of this file or directory.
   */
  private String name = null;
  
  /**
   * The last modified time.
   */
  private long lastModified = -1L;
  
  /**
   * The file size.
   */
  private long fileSize = -1L;
  
  /**
   * The parent directory.
   */
  private Node parent = null;
  
  /**
   * Whether this node is a directory or a file.
   */
  private boolean isDirectory = false;
  
  /**
   * Number of children for this directory.
   */
  private int numChildren = 0;
  
  /**
   * Directories and files in this directory.
   */
  private List<Node> children = null;
  
  
  /**
   * Default constructor.
   */
  public Node()
  {
    super();
  }
  
  
  /**
   * Return the children for this node.
   * 
   * @return the children
   */
  public List<Node> getChildren()
  {
    return children;
  }
  
  
  /**
   * Add a child to this node.
   * 
   * @param node the child to add
   */
  public void addChild(final Node node)
  {
    // See if we've created a list yet
    if (children == null)
    {
      children = new ArrayList<Node>(10);
    }
    
    // Add the child
    children.add(node);
    
    // Increment the number of children
    ++numChildren;
  }
  
  
  /**
   * Set a child in this node.
   * 
   * @param node the child to add
   */
  public void saveChild(final Node node)
  {
    // Add the child
    children.add(node);
  }
  
  
  /**
   * Return the size for this file.
   * 
   * @return the size of this file
   */
  public long getFileSize()
  {
    return fileSize;
  }
  
  
  /**
   * Set the size of this file.
   * 
   * @param lFileSize the new file size
   */
  public void setFileSize(final long lFileSize)
  {
    fileSize = lFileSize;
  }
  
  
  /**
   * Return the last modified date for this node.
   * 
   * @return the last modified date
   */
  public long getLastModified()
  {
    return lastModified;
  }
  
  
  /**
   * Set the last modified date for this node.
   * 
   * @param lLastModified the last modified date
   */
  public void setLastModified(final long lLastModified)
  {
    lastModified = lLastModified;
  }
  
  
  /**
   * Return the name of this file or directory.
   * 
   * @return the name of this file or directory
   */
  public String getName()
  {
    return name;
  }
  
  
  /**
   * Set the name of this file or directory.
   * 
   * @param sName the name of this directory or file
   */
  public void setName(final String sName)
  {
    name = sName;
  }
  
  
  /**
   * Return the parent for this node.
   * 
   * @return the parent for this node
   */
  public Node getParent()
  {
    return parent;
  }
  
  
  /**
   * Set the parent for this node.
   * 
   * @param pParent the parent for this node
   */
  public void setParent(final Node pParent)
  {
    parent = pParent;
  }
  
  
  /**
   * Return the number of children for this node.
   * 
   * @return the number of children
   */
  public int getNumChildren()
  {
    return numChildren;
  }
  
  
  /**
   * Return whether this node points to a directory.
   * 
   * @return whether this node points to a directory
   */
  public boolean isDirectory()
  {
    return isDirectory;
  }
  
  
  /**
   * Set whether this node is a directory.
   * 
   * @param bIsDirectory whether this is a directory
   */
  public void setDirectory(final boolean bIsDirectory)
  {
    isDirectory = bIsDirectory;
  }
  
  
  /**
   * Return the full path of this file name.
   * 
   * @return the full path of this file name
   */
  public String getFullPath()
  {
    // Start with the name of this object
    StringBuilder sb = new StringBuilder(200);
    sb.append(name);
    
    // Visit all parents
    Node anc = parent;
    while (anc != null)
    {
      // Prepend the path with the name of the parent
      sb.insert(0, anc.getName() + Utility.getPathSep());
      
      // Go to the grandparent
      anc = anc.parent;
    }
    
    // Return the string
    return sb.toString();
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
      .append(',').append(name).append(',')
      .append(Long.toString(lastModified))
      .append(',').append(Long.toString(fileSize))
      .append(',').append(isDirectory ? '1' : '0')
      .append(',').append(Integer.toString(numChildren));
    
    return sb.toString();
  }
  
  
  /**
   * Parse the line from the text file to generate a Store.
   * 
   * @param line the first line read from the input file
   * @return the generated Store
   */
  public static Node parseLine(final String line)
  {
    Node node = new Node();
    if ((line == null) || (line.length() < 1))
    {
      return node;
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
    node.name = sb.toString();
    
    int startIndex = commaIndex + nameLen + 1;
    commaIndex = line.indexOf(',', startIndex);
    node.lastModified = Utility.parseLong(line.substring(startIndex, commaIndex), 0L);
    
    startIndex = commaIndex + 1;
    commaIndex = line.indexOf(',', startIndex);
    node.fileSize = Utility.parseLong(line.substring(startIndex, commaIndex), 0L);
    
    startIndex = commaIndex + 1;
    node.isDirectory = (line.charAt(startIndex) == '1');
    
    node.numChildren = Integer.parseInt(line.substring(startIndex + 2));
    
    node.parent = null;
    node.children = new ArrayList<Node>(node.numChildren);
    
    // Return the node
    return node;
  }
  
  
  /**
   * Return a string representing this object.
   * 
   * @return this object as a string
   */
  @Override
  public String toString()
  {
    // Save whether this is a directory or file
    final String id = (isDirectory()) ? "Directory" : "File";
    
    // Construct a date string for the last modified date
    String dt = (lastModified < 0L) ? "Never" : Utility.formatDate(lastModified);
    
    // Declare the string builder
    StringBuilder sb = new StringBuilder(100);
    
    // Build the string
    sb.append(id).append(": ").append(getFullPath())
      .append("  Size: ").append(Long.toString(fileSize))
      .append("  Children: ").append(Integer.toString(numChildren))
      .append("  LastMod: ").append(dt);
    
    // Return the string
    return sb.toString();
  }
  
  
  /**
   * Return the path of the parent directory.
   * 
   * @return the path of the parent directory
   */
  public String getParentPath()
  {
    // Declare the string that will get returned
    StringBuilder sb = new StringBuilder(200);
    
    // Visit all parents
    Node anc = parent;
    if (anc != null)
    {
      // Add the ancestor name
      sb.append(anc.getName());
      
      // Get the parent, and loop over all parents
      anc = anc.parent;
      while (anc != null)
      {
        // Build a File object, to get the separators right
        File f = new File(anc.getName(), sb.toString());
        
        // Clear out the buffer, and replace it with the generated file path
        sb.setLength(0);
        sb.append(f.getPath());
        
        // Get the next parent
        anc = anc.parent;
      }
    }
    
    // Return the string
    return sb.toString();
  }
  
  
  /**
   * Write the node structure out to standard out.
   */
  public void writeTree()
  {
    System.out.println(toString());
    if (children != null)
    {
      for (Node node : children)
      {
        node.writeTree();
      }
    }
  }
}
