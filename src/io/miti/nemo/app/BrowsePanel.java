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

import io.miti.nemo.common.FileTransferable;
import io.miti.nemo.common.Indexer;
import io.miti.nemo.common.SearchResult;
import io.miti.nemo.common.Store;
import io.miti.nemo.common.StoreInfo;
import io.miti.nemo.common.Utility;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * This class encapsulates the behavior of the Browse panel of
 * the main application.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class BrowsePanel extends JPanel
{
  /**
   * Default serial version ID.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * The parent frame.
   */
  private JFrame frame = null;
  
  /**
   * The tree.
   */
  private JTree tree = null;
  
  /**
   * The details table.
   */
  private JTable detailsTable = null;
  
  /**
   * The model for the details table.
   */
  private DetailsTableModel detailsModel = null;
  
  /**
   * The popup menu for the details table.
   */
  private JPopupMenu searchMenu = null;
  
  /**
   * The popup menu for the tree.
   */
  private JPopupMenu treeMenu = null;
  
  /**
   * The name of the last viewed store.
   */
  private String lastShownStore = null;
  
  /**
   * The menu for the "Copy to clipboard" menu item.
   */
  // private JMenu menuCopyToCB = null;
  
  /**
   * Default constructor.
   */
  public BrowsePanel()
  {
    super();
  }
  
  
  /**
   * Constructor.
   * 
   * @param fFrame the parent frame
   */
  public BrowsePanel(final JFrame fFrame)
  {
    super();
    frame = fFrame;
  }
  
  
  /**
   * Checks if the store getting refreshed is the displayed store.
   * 
   * @param info the store getting refreshed
   */
  public void refreshingStore(final StoreInfo info)
  {
    // See if the argument or cache name is null
    if ((info == null) || (lastShownStore == null))
    {
      // Do nothing
      return;
    }
    
    // Check if the names match
    if (lastShownStore.equals(info.getStoreFilename()))
    {
      // They match, so clear out the cached name
      lastShownStore = null;
    }
  }
  
  
  /**
   * Populate the Browse tab.
   * 
   * @param info the store info reference
   */
  public void populateBrowsePanel(final StoreInfo info)
  {
    // Check the argument
    if (info == null)
    {
      return;
    }
    else if ((lastShownStore != null) &&
             (lastShownStore.equals(info.getStoreFilename())))
    {
      // We're already viewing this store, so return
      return;
    }
    
    // Update the name of the cached store
    lastShownStore = info.getStoreFilename();
    
    // Remove everything from the browse tab
    removeAll();
    setLayout(new GridLayout(1, 1));
    
    // Create the root data node for the tree
    DefaultMutableTreeNode root =
      new DefaultMutableTreeNode(info.getStoreFilename());
    
    // Load the data in the store.  First build an indexer.
    final Indexer indexer = new Indexer();
    
    // Generate the file name for the data store file
    File file = Utility.getIndexedDataFile(Utility.getIndexDirectoryName(), info);
    
    // Set the busy cursor
    Cursor cursor = frame.getCursor();
    frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    
    // Get the store
    final Store store = indexer.loadFromFile(file);
    
    // Restore the cursor
    frame.setCursor(cursor);
    
    // Populate the tree root node using the store variable
    indexer.buildTree(store.getDataStore(), root);
    
    // Create the tree based on the root node
    tree = new JTree(root);
    
    // Install a listener on the tree, so we can show the files
    // in the selected folder on the right-side panel
    tree.addTreeSelectionListener(new TreeSelectionListener()
    {
      public void valueChanged(final TreeSelectionEvent e)
      {
        // Get the selection path and the number of elements in the path
        TreePath path = e.getNewLeadSelectionPath();
        if (path == null)
        {
          return;
        }
        
        int pathCount = path.getPathCount();
        
        // Check if a root item was selected
        if (pathCount < 2)
        {
          // Get the files at the root
          List<SearchResult> results = indexer.findChildrenFiles(store, null);
          
          // Show the search results (if any) in detailsPanel
          showFiles(results);
        }
        else
        {
          // Build an array of the paths
          List<String> paths = new ArrayList<String>(pathCount - 1);
          for (int index = 1; index < pathCount; ++index)
          {
            // Get the current component in the path
            final DefaultMutableTreeNode node =
              (DefaultMutableTreeNode) path.getPathComponent(index);
            
            // Add the node text to our list (of directories)
            paths.add(node.toString());
          }
          
          // Get the files inside 'paths' and display on right
          List<SearchResult> results = indexer.findChildrenFiles(store, paths);
          
          // Show the search results (if any) in detailsPanel
          showFiles(results);
        }
      }
    });
    
    // Set the tree renderer to always use folders
    tree.setCellRenderer(new DefaultTreeCellRenderer()
    {
      /**
       * The default serial version ID.
       */
      private static final long serialVersionUID = 1L;
      
      /**
       * Override the icon to show for leaf nodes.
       * 
       * @see javax.swing.tree.DefaultTreeCellRenderer#getLeafIcon()
       */
      @Override
      public Icon getLeafIcon()
      {
        // Instead of showing a leaf icon, always show the default
        // folder icon, since we only show folders in the tree
        return super.getDefaultClosedIcon();
      }
    });
    
    // Set up the tree menu
    setupTreeMenu();
    
    // Add a right-click listener on the tree so we can install treeMenu
    tree.addMouseListener(new MouseAdapter()
    {
      /**
       * Handle a user clicking on the tree
       * 
       * @param e the event
       * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
       */
      @Override
      public void mouseClicked(final MouseEvent e)
      {
        // Show the popup menu
        handleTreeMouseClick(e);
      }
    });
    
    // Build the split pane
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    
    // Add the tree first (with an empty border)
    tree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    splitPane.add(new JScrollPane(tree));
    
    // Add the details panel
    JPanel detailsPanel = new JPanel(new BorderLayout());
    initDetailsTable();
    detailsPanel.add(detailsTable.getTableHeader(), BorderLayout.NORTH);
    detailsPanel.add(detailsTable, BorderLayout.CENTER);
    
    // Put the details panel in a scroll pane.
    JScrollPane scroller = new JScrollPane(detailsPanel);
    scroller.getHorizontalScrollBar().setUnitIncrement(40);
    scroller.getVerticalScrollBar().setUnitIncrement(40);
    splitPane.add(scroller);
    
    // Select the root of the tree
    tree.setSelectionRow(0);
    
    // Add the tree to a scroll pane, and add that to the panel
    add(splitPane);
    
    // Set the divider bar
    splitPane.setDividerLocation(200);
  }
  
  
  /**
   * Setup the popup menu for the tree.
   */
  private void setupTreeMenu()
  {
    // Create the popup menu for the Search page
    treeMenu = new JPopupMenu();
    
    // Create the Open menu item
    JMenuItem searchOpen = new JMenuItem("Open");
    searchOpen.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        // Get the selected node
        final TreePath parentPath = tree.getSelectionPath();
        if (parentPath != null)
        {
          // Get the tree path as a file path
          File path = getTreePath(parentPath);
          Utility.openFile(path, frame);
        }
      }
    });
    treeMenu.add(searchOpen);
    
    // Create the Copy Full Path menu item
    JMenuItem searchCopyFullPath = new JMenuItem("Copy full path");
    searchCopyFullPath.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        // Get the selected node
        final TreePath parentPath = tree.getSelectionPath();
        if (parentPath != null)
        {
          // Get the tree path as a file path
          File path = getTreePath(parentPath);
          if (path != null)
          {
            String name = path.getPath();
            if (name != null)
            {
              StringSelection ss = new StringSelection(name);
              java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                  .setContents(ss, null);
            }
          }
        }
      }
    });
    treeMenu.add(searchCopyFullPath);
  }
  
  
  /**
   * Return the tree path as a File object.
   * 
   * @param parentPath the tree path
   * @return the path as a File
   */
  private File getTreePath(final TreePath parentPath)
  {
    // Get the number of components in the path
    final int count = parentPath.getPathCount();
    if (count < 1)
    {
      // Not enough; this should never happen
      return null;
    }
    
    // Create a File object for the root node
    File file = new File(parentPath.getPathComponent(0).toString());
    
    // Iterate over the rest of the path components
    for (int i = 1; i < count; ++i)
    {
      // Get the current node and update file based on it
      DefaultMutableTreeNode node =
        (DefaultMutableTreeNode) parentPath.getPathComponent(i);
      String dir = node.toString();
      file = new File(file.getPath(), dir);
    }
    
    // Return the file handle
    return file;
  }
  
  
  /**
   * The user clicked on the tree, so show the popup menu.
   * 
   * @param e the event
   */
  private void handleTreeMouseClick(final MouseEvent e)
  {
    // Check which button was pressed
    if (e.getButton() != MouseEvent.BUTTON3)
    {
      // It's not button 3, so return
      return;
    }
    
    // Get the point
    final Point loc = e.getPoint();
    
    // See if the user clicked on the tree
    if (e.getSource() == tree)
    {
      // Get the location.  If not over a tree node, return
      TreePath path = tree.getPathForLocation(loc.x, loc.y);
      if (path != null)
      {
        // Select the node the user right-clicked on
        tree.setSelectionPath(path);
        
        // Show the popup menu
        treeMenu.show(tree, loc.x, loc.y);
      }
    }
  }
  
  
  /**
   * Initialize the details table.
   */
  private void initDetailsTable()
  {
    // Initialize the popup menu
    buildPopup();
    
    // Initialize the table and model for showing the children (files)
    detailsModel = new DetailsTableModel();
    detailsTable = new JTable(detailsModel);
    detailsTable.getTableHeader().setReorderingAllowed(false);
    detailsTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
    detailsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    // Set the renderer for the results table
    ResultsRenderer detailsRenderer = new ResultsRenderer();
    detailsRenderer.setNumberOfDirectories(0);
    detailsTable.setDefaultRenderer(Object.class, detailsRenderer);
    
    // Center the column headings
    ((DefaultTableCellRenderer) detailsTable.getTableHeader().
        getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    
    // Add the right-click listener
    addTableMouseListener(detailsTable, searchMenu);
  }
  
  
  /**
   * Add the listener for right-clicking on a table.
   * 
   * @param table the table
   * @param menu the menu to show
   */
  private static void addTableMouseListener(final JTable table,
                                            final JPopupMenu menu)
  {
    // Add the table listener
    table.addMouseListener(new MouseAdapter()
    {
      /**
       * Handle the user right-clicking on the table
       * 
       * @param e the event
       * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
       */
      @Override
      public void mouseClicked(final MouseEvent e)
      {
        // Check for a right-click on a row
        if (e.getButton() == MouseEvent.BUTTON3)
        {
          // Determine the row
          int row = table.rowAtPoint(new Point(e.getX(), e.getY()));
          if (row >= 0)
          {
            // Select the row and show the popup menu
            table.setRowSelectionInterval(row, row);
            menu.show(e.getComponent(), e.getX(), e.getY());
          }
        }
      }
    });
  }
  
  
  /**
   * Show the files for the selected directory.
   * 
   * @param results the search results
   */
  private void showFiles(final List<SearchResult> results)
  {
    // Update the displayed data and fire a data-change event
    detailsModel.setRowData(results);
    detailsModel.fireTableDataChanged();
  }
  
  
  /**
   * Build the popup menu.
   */
  private void buildPopup()
  {
    // Create the popup menu for the Search page
    searchMenu = new JPopupMenu();
    
    // Create the Open menu item
    JMenuItem searchOpen = new JMenuItem("Open");
    searchOpen.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        int row = detailsTable.getSelectedRow();
        if (row >= 0)
        {
          SearchResult result = detailsModel.getRow(row);
          Utility.openResult(result, frame);
        }
      }
    });
    searchMenu.add(searchOpen);
    
    // Create the View As Text menu item
    JMenuItem miTextView = new JMenuItem("View as text");
    miTextView.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        int row = detailsTable.getSelectedRow();
        if (row >= 0)
        {
          // Get the selected row and open the file in a text viewer
          SearchResult result = detailsModel.getRow(row);
          new TextPopup(new File(result.getPath(), result.getName()), false);
        }
      }
    });
    searchMenu.add(miTextView);
    
    // Create the View As Text menu item
    JMenuItem miHexView = new JMenuItem("View as hex");
    miHexView.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        int row = detailsTable.getSelectedRow();
        if (row >= 0)
        {
          // Get the selected row and open the file in a hex viewer
          SearchResult result = detailsModel.getRow(row);
          new TextPopup(new File(result.getPath(), result.getName()), true);
        }
      }
    });
    searchMenu.add(miHexView);
    
    // Create the Copy To Clipboard menu item
    searchMenu.add(createCopyMenu());
    
    // Create the Explore Parent menu item
    JMenuItem searchExploreParent = new JMenuItem("Explore parent");
    searchExploreParent.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        // Copy the full path/filename to the clipboard
        int row = detailsTable.getSelectedRow();
        if (row >= 0)
        {
          // Open the parent
          SearchResult result = detailsModel.getRow(row);
          File file = new File(result.getPath());
          Utility.openFile(file, frame);
        }
      }
    });
    searchMenu.add(searchExploreParent);
  }
  
  
  /**
   * Create the Copy To Clipboard menu.
   * 
   * @return the menu item to copy files to the clipboard
   */
  private JMenu createCopyMenu()
  {
    JMenu menuCopyToCB = new JMenu("Copy to clipboard ");
    
    // Create the Copy Full Path menu item
    JMenuItem copyName = new JMenuItem("File name");
    copyName.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        // Copy the full path/filename to the clipboard
        int row = detailsTable.getSelectedRow();
        if (row >= 0)
        {
          SearchResult result = detailsModel.getRow(row);
          if (result != null)
          {
            File file = new File(result.getPath(), result.getName());
            String name = file.getName();
            StringSelection ss = new StringSelection(name);
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(ss, null);
          }
        }
      }
    });
    menuCopyToCB.add(copyName);
    
    // Create the Copy Full Path menu item
    JMenuItem searchCopyFullPath = new JMenuItem("Full path");
    searchCopyFullPath.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        // Copy the full path/filename to the clipboard
        int row = detailsTable.getSelectedRow();
        if (row >= 0)
        {
          SearchResult result = detailsModel.getRow(row);
          if (result != null)
          {
            // System.out.println(result.toString());
            File file = new File(result.getPath(), result.getName());
            try
            {
              String name = file.getCanonicalPath();
              StringSelection ss = new StringSelection(name);
              java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                  .setContents(ss, null);
            }
            catch (IOException e1)
            {
              e1.printStackTrace();
            }
          }
        }
      }
    });
    menuCopyToCB.add(searchCopyFullPath);
    
    // Create the Copy Full Path menu item
    JMenuItem copyFile = new JMenuItem("File pointer");
    copyFile.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        // Copy the full path/filename to the clipboard
        int row = detailsTable.getSelectedRow();
        if (row >= 0)
        {
          SearchResult result = detailsModel.getRow(row);
          if (result != null)
          {
            File file = new File(result.getPath(), result.getName());
            FileTransferable.copyToClipboard(file);
          }
        }
      }
    });
    menuCopyToCB.add(copyFile);
    
    return menuCopyToCB;
  }
  
  
  /**
   * Select the folder/file of the specified search result.
   * 
   * @param result the search result to highlight
   */
  public void selectResult(final SearchResult result)
  {
    // Check the tree
    if ((tree == null) || (tree.getRowCount() < 1))
    {
      return;
    }
    
    // Get the root node string
    TreePath treePath = tree.getPathForRow(0);
    if ((treePath.getPathCount() < 1))
    {
      return;
    }
    String root = treePath.getPathComponent(0).toString();
    
    // Get the length of the root node string
    final int rootLen = root.length();
    if (result.getPath().length() < rootLen)
    {
      return;
    }
    
    // Get everything after the root node path
    String pathAfterRoot = result.getPath().substring(rootLen);
    
    // This will hold the directory of file paths
    List<String> filePaths = new ArrayList<String>(20);
    
    // Parse the remaining strings
    StringTokenizer st = new StringTokenizer(pathAfterRoot, "\\/");
    while (st.hasMoreTokens())
    {
      filePaths.add(st.nextToken());
    }
    
    // If the search result is a directory, add it to the array
    if (result.isDirectory())
    {
      filePaths.add(result.getName());
    }
    
    // Select the node with the path in filePaths
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getModel().getRoot();
    int currentPath = 0;
    DefaultMutableTreeNode targetNode = null;
    final int childCount = node.getChildCount();
    for (int index = 0; index < childCount; ++index)
    {
      DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(index);
      if (child.toString().equals(filePaths.get(currentPath)))
      {
        targetNode = searchResultChildren(child, filePaths, currentPath);
        break;
      }
    }
    
    // Expand the first row
    tree.expandRow(0);
    
    // Check for a null target (this shouldn't happen)
    if (targetNode == null)
    {
      return;
    }
    
    // Select the node
    TreePath path = new TreePath(targetNode.getPath());
    tree.setSelectionPath(path);
    
    // Make sure the path is visible
    tree.scrollPathToVisible(path);
    
    // Check if this is a file
    if (!result.isDirectory())
    {
      // It's a file, so select the table row
      final int rowCount = detailsModel.getRowCount();
      for (int index = 0; index < rowCount; ++index)
      {
        SearchResult row = detailsModel.getRow(index);
        if (row.getName().equals(result.getName()))
        {
          // Ensure the row is visible. First select the row.
          detailsTable.setRowSelectionInterval(index, index);
          
          // Get a rectangle for the row, and then scroll to the rectangle
          Rectangle rect = detailsTable.getCellRect(index, 0, true);
          detailsTable.scrollRectToVisible(rect);
          
          // Break out of the loop
          break;
        }
      }
    }
  }
  
  
  /**
   * Recursively search the children for a match on the directory.
   * 
   * @param node the node to search
   * @param filePaths the list of file paths
   * @param currentPath the current index into filePaths
   * @return the matching node
   */
  private DefaultMutableTreeNode searchResultChildren(final DefaultMutableTreeNode node,
                                                      final List<String> filePaths,
                                                      final int currentPath)
  {
    final int pathIndex = currentPath + 1;
    if (pathIndex == filePaths.size())
    {
      return node;
    }
    
    final int childCount = node.getChildCount();
    for (int index = 0; index < childCount; ++index)
    {
      DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(index);
      if (child.toString().equals(filePaths.get(pathIndex)))
      {
        return searchResultChildren(child, filePaths, pathIndex);
      }
    }
    
    // Return no match - this should never happen
    return null;
  }
  
  
  /**
   * Expand or collapse all nodes in a tree.
   * 
   * @param tree the tree to expand or collapse
   * @param expand whether to expand or collapse
   */
  public static void expandAll(final JTree tree,
                               final boolean expand)
  {
    // Get the root
    TreeNode root = (TreeNode) tree.getModel().getRoot();
    
    // Traverse tree from root
    expandAll(tree, new TreePath(root), expand);
  }
  
  
  /**
   * Recurse over the tree to expand or collapse.
   * 
   * @param tree the tree
   * @param parent the parent node
   * @param expand whether to expand or collapse
   */
  private static void expandAll(final JTree tree,
                                final TreePath parent,
                                final boolean expand)
  {
    // Traverse children
    TreeNode node = (TreeNode) parent.getLastPathComponent();
    if (node.getChildCount() >= 0)
    {
      for (Enumeration<?> e=node.children(); e.hasMoreElements();)
      {
        TreeNode n = (TreeNode) e.nextElement();
        TreePath path = parent.pathByAddingChild(n);
        expandAll(tree, path, expand);
      }
    }
    
    // Expansion or collapse must be done bottom-up
    if (expand)
    {
      tree.expandPath(parent);
    }
    else
    {
      tree.collapsePath(parent);
    }
  }
}
