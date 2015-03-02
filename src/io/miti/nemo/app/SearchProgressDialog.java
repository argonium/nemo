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

package io.miti.nemo.app;

import io.miti.nemo.common.Indexer;
import io.miti.nemo.common.SearchResult;
import io.miti.nemo.common.Store;
import io.miti.nemo.common.StoreInfo;
import io.miti.nemo.common.Utility;
import io.miti.nemo.filter.TermFilter;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.Border;

/**
 * Class to show a Cancel dialog while searching a store.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class SearchProgressDialog extends JDialog
{
  /**
   * Default serial version ID.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * The search term.
   */
  private TermFilter filter = null;
  
  /**
   * The list of stores to search.
   */
  private List<StoreInfo> listStores = null;
  
  /**
   * Whether to include files.
   */
  private boolean includeFiles = false;
  
  /**
   * Whether to include directories.
   */
  private boolean includeDirs = false;
  
  /**
   * The search results.
   */
  private List<SearchResult> results = null;
  
  /**
   * The maximum number of search results to return.
   */
  private int maxResultSize = 0;
  
  /**
   * Whether the indexing completed.
   */
  private boolean completed = false;
  
  /**
   * The Cancel button.
   */
  private JButton btnCancel = new JButton("Cancel");
  
  /**
   * The indexing task.
   */
  private Task task = null;
  
  
  /**
   * Default constructor.
   */
  public SearchProgressDialog()
  {
    super();
  }
  
  
  /**
   * Constructor.
   * 
   * @param frame the parent frame
   * @param pFilter the term filter
   * @param lListStores the list of stores to search
   * @param bIncludeFiles whether to include files
   * @param bIncludeDirs whether to include directories
   * @param lResults the result set
   * @param nMaxResultSize the maximum number of results to search
   */
  public SearchProgressDialog(final JFrame frame,
                              final TermFilter pFilter,
                              final List<StoreInfo> lListStores,
                              final boolean bIncludeFiles,
                              final boolean bIncludeDirs,
                              final List<SearchResult> lResults,
                              final int nMaxResultSize)
  {
    // Create the dialog.  Make it modal.
    super(frame, "Searching", true);
    
    // Save the arguments
    filter = pFilter;
    listStores = lListStores;
    includeFiles = bIncludeFiles;
    includeDirs = bIncludeDirs;
    results = lResults;
    maxResultSize = nMaxResultSize;
    
    // Build the dialog
    buildDialog(frame);
  }
  
  
  /**
   * Start processing.
   */
  public void start()
  {
    // Start the processing
    startProcessing();
  }
  
  
  /**
   * Build the dialog.
   * 
   * @param frame the parent frame
   */
  private void buildDialog(final JFrame frame)
  {
    // Create the border
    Border padding = BorderFactory.createEmptyBorder(15, 10, 15, 10);
    
    // Create the panel
    JPanel panel = new JPanel(new GridLayout(2, 1, 0, 15));
    panel.setBorder(padding);
    
    // Add the status label
    JLabel lblStatus = new JLabel("Searching...",
                                  SwingConstants.CENTER);
    panel.add(lblStatus);
    
    // Set up the Cancel button
    btnCancel.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        cancelIndexing();
      }
    });
    panel.add(btnCancel);
    
    // Add the panel to the dialog
    getContentPane().add(panel);
    
    // Pack the dialog and set the display properties
    pack();
    setResizable(false);
    setLocationRelativeTo(frame);
  }
  
  
  /**
   * Cancel the indexing thread.
   */
  private void cancelIndexing()
  {
    // Cancel the thread, if it's still running
    if (task != null)
    {
      // Cancel the thread
      task.cancel(true);
    }
    
    // Kill this dialog
    dispose();
    completed = false;
  }
  
  
  /**
   * Return whether the indexing completed.
   * 
   * @return whether the indexing completed
   */
  public boolean completed()
  {
    return completed;
  }
  
  
  /**
   * Start the thread processing.
   */
  public void startProcessing()
  {
    // Start the indexing
    task = new Task();
    task.execute();
    
    // Show this modal dialog
    setVisible(true);
  }
  
  
  /**
   * The indexing task.
   * 
   * @author mwallace
   */
  class Task extends SwingWorker<Void, Void>
  {
    /**
     * Execute this task in the background.
     * 
     * @return the return value
     */
    @Override
    public Void doInBackground()
    {
      // Iterate over the list of stores to search
      for (StoreInfo info : listStores)
      {
        // Handle this thread getting interrupted
        if (Thread.currentThread().isInterrupted())
        {
          break;
        }
        
        // Perform the search.  First build an indexer.
        Indexer indexer = new Indexer();
        
        // Generate the file name for the data store file
        File file = Utility.getIndexedDataFile(Utility.getIndexDirectoryName(), info);
        
        // Get the store
        Store store = indexer.loadFromFile(file);
        
        // Handle this thread getting interrupted
        if (Thread.currentThread().isInterrupted())
        {
          break;
        }
        
        // Perform the search
        indexer.searchStore(store, maxResultSize, includeFiles,
                            includeDirs, filter, results);
      }
      
      return null;
    }
    
    /**
     * Signal that the process completed.
     */
    @Override
    public void done()
    {
      // This is called whether the task was cancelled or not
      completed = !isCancelled();
      dispose();
    }
  }
}
