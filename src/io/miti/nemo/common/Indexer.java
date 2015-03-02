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

import io.miti.nemo.filter.TermFilter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Comparator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Program to manage directory indices.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class Indexer
{
  /**
   * Whether the ing has been interrupted.
   */
  private boolean bInterrupted = false;
  
  /**
   * Whether to load/save the data in the text format.
   */
  private static final boolean USE_TEXT = true;
  
  
  /**
   * Default constructor.
   */
  public Indexer()
  {
    super();
  }
  
  
  /**
   * Write a message to standard error.
   * 
   * @param msg the message to write
   */
  private static void writeErr(final String msg)
  {
    System.err.println(msg);
  }
  
  
  /**
   * Visit the directories and files under this node.
   * 
   * @param dirName the name of the parent directory
   * @param dir the parent directory (as a File)
   * @param parent the parent node
   * @return the child node that was created
   */
  private Node visitDirectories(final String dirName,
                                final File dir,
                                final Node parent)
  {
    if (bInterrupted)
    {
      return null;
    }
    
    // Create the node that will get returned
    Node root = new Node();
    
    // Save the directory name (just the current name, not the full path)
    root.setName(dirName);
    
    // Save the parent
    root.setParent(parent);
    
    // Save more info
    root.setFileSize(dir.length());
    root.setLastModified(dir.lastModified());
    root.setDirectory(dir.isDirectory());
    
    // Check if this is a directory
    if (dir.isDirectory())
    {
      // See if we should include this directory
      if (includeFile(dir))
      {
        // Get the children for this directory and check if they're null
        String[] children = dir.list();
        if (children == null)
        {
          // We must not have permission to browse this directory
          return root;
        }
        
        // Iterate over each subdirectory, and add it as a child node
        java.util.Arrays.sort(children, new Comparator<String>()
        {
          /**
           * Compare the two file names as case-insensitive.
           * 
           * @param o1 the first filename
           * @param o2 the second filename
           * @return how o1 compares to o2
           */
          public int compare(final String o1, final String o2)
          {
            return (o1.toLowerCase().compareTo(o2.toLowerCase()));
          }
        });
        for (int i = 0; i < children.length; i++)
        {
          // Create a file object for this child
          File child = new File(dir, children[i]);
          
          // If it's a file, see if it should be included
          if (!includeFile(child))
          {
            continue;
          }
          
          // Check if the thread has been cancelled
          if (Thread.currentThread().isInterrupted())
          {
            bInterrupted = true;
          }
          
          // Add the subdirectory as a child of this node
          root.addChild(visitDirectories(children[i], child, root));
        }
      }
    }
    
    // Return the node
    return root;
  }
  
  
  /**
   * Return whether to include this file or directory in the search
   * results.
   * 
   * @param dir the directory or file to process
   * @return whether to include it in the search results
   */
  private boolean includeFile(final File dir)
  {
    // Save the name
    String name = dir.getName();
    
    // See if it's a directory
    if (dir.isDirectory())
    {
      // Don't include the current or parent directories,
      // or temp directories
      if ((name.equals(".")) || (name.equals("..")) ||
          (name.equalsIgnoreCase("temp")) ||
          (name.equalsIgnoreCase("tmp")) ||
          (name.equalsIgnoreCase(".svn")) ||
          (name.toLowerCase().startsWith("temp")))
      {
        return false;
      }
    }
    else
    {
      // Don't include files with a name ending with .tmp
      if (name.toLowerCase().endsWith(".tmp"))
      {
        return false;
      }
    }
    
    return true;
  }
  
  
  /**
   * Index this directory and return the data store.
   * 
   * @param name the name of the data store
   * @param dir the root directory
   * @return the data store
   */
  public Store indexDirectory(final String name,
                              final String dir)
  {
    // Create the Store
    Store store = new Store(name, dir);
    
    // Traverse the directory
    File file = new File(dir);
    Node root = visitDirectories(dir, file, null);
    
    // Check if it was interrupted
    if (bInterrupted)
    {
      return null;
    }
    
    // Save the node
    store.setDataStore(root);
    
    // Return the store
    return store;
  }
  
  
  /**
   * Search a data store and return the search results.
   * 
   * @param store the data store to search
   * @param maxResults the maximum number of results
   * @param includeFiles whether to include files in the results
   * @param includeDirectories whether to include directories in the results
   * @param termFilter the term filter
   * @param list the search results
   */
  public void searchStore(final Store store,
                          final int maxResults,
                          final boolean includeFiles,
                          final boolean includeDirectories,
                          final TermFilter termFilter,
                          final List<SearchResult> list)
  {
    // Check what to include
    if (!includeFiles && !includeDirectories)
    {
      // Nothing to do - no files or directories wanted
      return;
    }
    else if (store == null)
    {
      // Nothing to do - the data store is null
      System.err.println("The store is null");
      return;
    }
    else if (maxResults < 1)
    {
      // Nothing to do - no results wanted
      return;
    }
    
    // Search the data for a match
    Node root = store.getDataStore();
    if (root == null)
    {
      return;
    }
    
    // See if this node has any children
    if (root.getNumChildren() > 0)
    {
      // Search the children
      for (Node node : root.getChildren())
      {
        searchDataStoreChildren(list, node, maxResults, includeFiles,
                                includeDirectories, store.getName(), termFilter);
      }
    }
  }
  
  
  /**
   * Search a data store and save the results.
   * 
   * @param list the list of search results to add to
   * @param root the root node
   * @param maxResults the maximum number of results
   * @param includeFiles whether to include files
   * @param includeDirectories whether to include directories
   * @param volumeName the name of the volume
   * @param termFilter the filter used to check for matches
   */
  private void searchDataStoreChildren(final List<SearchResult> list,
                                       final Node root,
                                       final int maxResults,
                                       final boolean includeFiles,
                                       final boolean includeDirectories,
                                       final String volumeName,
                                       final TermFilter termFilter)
  {
    // Check if this should be included
    boolean bCheck = true;
    boolean bIsDirectory = root.isDirectory();
    if (bIsDirectory && !includeDirectories)
    {
      // Don't include directories
      bCheck = false;
    }
    else if (!bIsDirectory && !includeFiles)
    {
      // Don't include files
      bCheck = false;
    }
    else if (list.size() >= maxResults)
    {
      return;
    }
    
    // See if we're checking for a match
    if (bCheck)
    {
      // Check if the name is a match
      if (termFilter.accept(root.getName()))
      {
        // We have a match.  Create the search result.
        SearchResult sr = new SearchResult();
        
        // Fill in the values
        sr.setFileSize(root.getFileSize());
        sr.setLastModified(root.getLastModified());
        sr.setName(root.getName());
        sr.setPath(root.getParentPath());
        sr.setVolume(volumeName);
        sr.setDirectory(root.isDirectory());
        
        // Add the search result to our list
        list.add(sr);
      }
    }
    
    // See if this node has any children
    if (root.getNumChildren() > 0)
    {
      // Iterate over the children of this node
      for (Node node : root.getChildren())
      {
        // Check if this thread has been interrupted
        if (Thread.currentThread().isInterrupted())
        {
          break;
        }
        
        // Search the child store
        searchDataStoreChildren(list, node, maxResults, includeFiles,
                                includeDirectories, volumeName, termFilter);
      }
    }
  }
  
  
  /**
   * Save the current contents to a file.
   * 
   * @param outFile the output File object
   * @param store the data store
   */
  public void saveToFile(final File outFile, final Store store)
  {
    // See if to save the data as a text file
    if (USE_TEXT)
    {
      saveToTextFile(outFile, store);
      return;
    }
    
    // Write the file
    ObjectOutputStream oos = null;
    try
    {
      // Create the output stream
      oos = new ObjectOutputStream(new FileOutputStream(outFile));
      
      // Write the data
      oos.writeObject(store);
      
      // Clear the stream
      oos.close();
      oos = null;
    }
    catch (FileNotFoundException fnfe)
    {
      writeErr("File not found: " + fnfe.getMessage());
    }
    catch (IOException ioe)
    {
      writeErr("IOException: " + ioe.getMessage());
    }
    finally
    {
      if (oos != null)
      {
        try
        {
          oos.close();
        }
        catch (IOException ioe)
        {
          writeErr("IOException: " + ioe.getMessage());
        }
        
        oos = null;
      }
    }
  }
  
  
  
  /**
   * Save the current contents to a file.
   * 
   * @param outFile the output File object
   * @param store the data store
   */
  private void saveToTextFile(final File outFile, final Store store)
  {
    // Declare the writer we use to save store to a file
    BufferedWriter out = null;
    try
    {
      // Open the writer
      out = new BufferedWriter(new FileWriter(outFile));
      StringBuilder sb = new StringBuilder(200);
      
      // Write the contents of store
      out.write(store.toTextString(sb));
      out.write(Utility.getLineSep());
      
      // Write the node and its children
      writeChildren(sb, store.getDataStore(), out);
      
      // Close the writer
      out.close();
      out = null;
    }
    catch (IOException e)
    {
      System.err.println("IOException writing the file: " + e.getMessage());
    }
    finally
    {
      // Make sure we close the file
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
   * Write out this node and its children, recursively.
   * 
   * @param sb the string builder
   * @param dataStore the node
   * @param out the writer
   * @throws IOException exception thrown by writing to the writer
   */
  private void writeChildren(final StringBuilder sb,
                             final Node dataStore,
                             final BufferedWriter out) throws IOException
  {
    // Check the data store
    if (dataStore == null)
    {
      return;
    }
    
    // Write out this store
    out.write(dataStore.toTextString(sb));
    out.write(Utility.getLineSep());
    
    // Iterate over the children
    List<Node> children = dataStore.getChildren();
    if (children == null)
    {
      return;
    }
    
    for (int i = 0; i < children.size(); ++i)
    {
      writeChildren(sb, children.get(i), out);
    }
  }
  
  
  /**
   * Read the contents of a file.
   * 
   * @param inFile the input File object
   * @return the loaded data store
   */
  public Store loadFromFile(final File inFile)
  {
    // See if to save the data as a text file
    if (USE_TEXT)
    {
      return loadFromTextFile(inFile);
    }
    
    // Verify that inFile points to an existing file
    if ((inFile == null) || (inFile.isDirectory()) || (!inFile.exists()))
    {
      return null;
    }
    
    // Read the file
    ObjectInputStream os = null;
    Store store = null;
    try
    {
      // Create the output stream
      os = new ObjectInputStream(new FileInputStream(inFile));
      
      // Read the data
      store = (Store) os.readObject();
      
      // Clear the stream
      os.close();
      os = null;
    }
    catch (ClassNotFoundException cnfe)
    {
      writeErr("Class not found: " + cnfe.getMessage());
    }
    catch (FileNotFoundException fnfe)
    {
      writeErr("File not found: " + fnfe.getMessage());
    }
    catch (IOException ioe)
    {
      writeErr("IOException: " + ioe.getMessage());
    }
    finally
    {
      if (os != null)
      {
        try
        {
          os.close();
        }
        catch (IOException ioe)
        {
          writeErr("IOException: " + ioe.getMessage());
        }
        
        os = null;
      }
    }
    
    return store;
  }
  
  
  /**
   * Read the contents of a file.
   * 
   * @param inFile the input File object
   * @return the loaded data store
   */
  private Store loadFromTextFile(final File inFile)
  {
    // Verify that inFile points to an existing file
    if ((inFile == null) || (inFile.isDirectory()) || (!inFile.exists()))
    {
      return null;
    }
    
    // Build the store that gets returned
    Store store = null;
    BufferedReader in = null;
    try
    {
      in = new BufferedReader(new FileReader(inFile));
      String str = in.readLine();
      if (str != null)
      {
        // Get the store info
        store = Store.parseLine(str);
        
        // Read the children
        str = in.readLine();
        if (str != null)
        {
          Node node = Node.parseLine(str);
          parseChildrenFile(in, node);
          store.setDataStore(node);
        }
      }
      
      // Close the input file
      in.close();
      in = null;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    finally
    {
      // Close the reader if there was an error
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
    
    // Return the store
    return store;
  }
  
  
  /**
   * Read the children node from the text input file.
   * 
   * @param in the file reader
   * @param parent the parent node
   * @throws IOException exception thrown by reading the file
   */
  private void parseChildrenFile(final BufferedReader in,
                                 final Node parent) throws IOException
  {
    // Read the children for this node
    final int numKids = parent.getNumChildren();
    for (int index = 0; index < numKids; ++index)
    {
      String line = in.readLine();
      Node node = Node.parseLine(line);
      node.setParent(parent);
      parent.saveChild(node);
      
      // If this node is a directory with children, get them
      if ((node.isDirectory()) && (node.getNumChildren() > 0))
      {
        parseChildrenFile(in, node);
      }
    }
  }
  
  
  /**
   * Build a tree of the data in the specified store.
   * 
   * @param root the current store root node
   * @param tree the root tree node
   */
  public void buildTree(final Node root,
                        final DefaultMutableTreeNode tree)
  {
    // Check for a null root or no children
    if (root == null)
    {
      return;
    }
    else if (root.getNumChildren() < 1)
    {
      return;
    }
    
    // Traverse over the children
    for (Node node : root.getChildren())
    {
      // Only grab directories
      if (node.isDirectory())
      {
        // Build a node for the child, then build the subtree for the
        // child (recursively), and then add the child to the tree
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(node.getName());
        
        // Only build the subtree if this is a directory
        if (node.isDirectory())
        {
          buildTree(node, child);
        }
        
        // Now add the child node to the tree
        tree.add(child);
      }
    }
  }
  
  
  /**
   * Find the children (files) located at paths.
   * 
   * @param store the store to search
   * @param paths the names of the parent nodes, in order
   * @return the search results
   */
  public List<SearchResult> findChildrenFiles(final Store store,
                                              final List<String> paths)
  {
    // Declare the list to hold the search results
    List<SearchResult> results = new java.util.ArrayList<SearchResult>(20);
    
    // Check the input store
    if (store == null)
    {
      // Nothing to do - the data store is null
      System.err.println("The store is null");
      return results;
    }
    
    // Search the data for a match
    Node root = store.getDataStore();
    if (root == null)
    {
      return results;
    }
    
    // Handle null paths
    if (paths == null)
    {
      saveFileChildren(root, results);
      
      // Return the results
      return results;
    }
    
    // Search the children
    // See if this node has any children
    if (root.getNumChildren() > 0)
    {
      int index = 0;
      for (Node node : root.getChildren())
      {
        if (node.isDirectory() && (node.getName().equals(paths.get(index))))
        {
          searchFileChildren(results, node, paths, index);
          break;
        }
      }
    }
    
    // Return the search results
    return results;
  }
  
  
  /**
   * Search the children for a match on the entries in paths.
   * 
   * @param results the search results
   * @param root the parent node
   * @param paths the list of parent nodes
   * @param index the current index into paths
   */
  private void searchFileChildren(final List<SearchResult> results,
                                  final Node root,
                                  final List<String> paths,
                                  final int index)
  {
    // Check if we've hit the index end
    int currIndex = index + 1;
    if (currIndex == paths.size())
    {
      // Get the files in this node
      saveFileChildren(root, results);
      return;
    }
    
    // See if this node has any children
    if (root.getNumChildren() > 0)
    {
      // Find the next child, matching on the current index of paths
      for (Node node : root.getChildren())
      {
        if (node.isDirectory() && (node.getName().equals(paths.get(currIndex))))
        {
          // Call this method recursively, to search children directories
          searchFileChildren(results, node, paths, currIndex);
          break;
        }
      }
    }
  }
  
  
  /**
   * Save the file children to our list of search results.
   * 
   * @param root the root node
   * @param results the list of search results
   */
  private void saveFileChildren(final Node root, final List<SearchResult> results)
  {
    // Check for no children
    if (root.getNumChildren() < 1)
    {
      // No children
      return;
    }
    
    // Iterate over the children
    for (Node node : root.getChildren())
    {
      // Skip directories
      if (!node.isDirectory())
      {
        // Create the result object
        SearchResult sr = new SearchResult();
        
        // Fill in the values
        sr.setFileSize(node.getFileSize());
        sr.setLastModified(node.getLastModified());
        sr.setName(node.getName());
        sr.setPath(node.getParentPath());
        sr.setDirectory(node.isDirectory());
        
        // Add the result to our list
        results.add(sr);
      }
    }
  }
  
  
  /**
   * Entry point for this application.
   * 
   * @param args arguments passed to the application
   */
  public static void main(final String[] args)
  {
    String file = "nemotemp.dat";
    
    System.out.println("Starting...");
    
    // Instantiate the class
    Indexer ind = new Indexer();
    
    long lStart = System.currentTimeMillis();
    
    // Index a sample directory
    Store store = ind.indexDirectory("I Drive", "I:\\");
    
    lStart = System.currentTimeMillis() - lStart;
    
    // System.out.println("Time: " + Long.toString(lStart));
    
    // Save the index to a file
    ind.saveToFile(new File(file), store);
    
    // Load the data from the file
    lStart = System.currentTimeMillis();
    ind.loadFromFile(new File(file));
    lStart = System.currentTimeMillis() - lStart;
    System.out.println("Time: " + Long.toString(lStart));
  }
}
