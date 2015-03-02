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

import com.l2fprod.common.swing.JDirectoryChooser;

import io.miti.nemo.common.FileTransferable;
import io.miti.nemo.common.SearchResult;
import io.miti.nemo.common.Store;
import io.miti.nemo.common.StoreInfo;
import io.miti.nemo.common.SystemInfo;
import io.miti.nemo.common.Utility;
import io.miti.nemo.filter.ContainsAllFilter;
import io.miti.nemo.filter.ContainsFilter;
import io.miti.nemo.filter.RegexFilter;
import io.miti.nemo.filter.SimilarFilter;
import io.miti.nemo.filter.SoundFilter;
import io.miti.nemo.filter.TermFilter;
import io.miti.nemo.filter.WildcardFilter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * Application to manage stores of filenames.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class App
{
  /**
   * The name of the properties file.
   */
  private static final String PROP_FILE_NAME = "nemo.xml";
  
  /**
   * The application frame.
   */
  private JFrame frame = null;
  
  /**
   * The table that shows the search results.
   */
  private JTable resultsTable = null;
  
  /**
   * The table that shows the folder stores.
   */
  private JTable storeTable = null;
  
  /**
   * The combo box to enter the search term.
   */
  private JComboBox<String> queryField = null;
  
  /**
   * The combo box to select what to search.
   */
  private JComboBox<String> searchCombo = null;
  
  /**
   * The checkbox to only search the selected volumes.
   */
  private JCheckBox checkSelected = null;
  
  /**
   * The combo box for the filter type.
   */
  private JComboBox<String> comboFilters = null;
  
  /**
   * The checkbox to make the search case-sensitive.
   */
  private JCheckBox checkCase = null;
  
  /**
   * The checkbox for the max number of results.
   */
  private JCheckBox checkMaxResults = null;
  
  /**
   * The maximum number of results to return.
   */
  private JTextField maxResults = null;
  
  /**
   * The name of the current store.
   */
  private JTextField volumeDir = null;
  
  /**
   * The name of the current volume.
   */
  private JTextField volumeName = null;
  
  /**
   * The directory that the index files are stored in.
   */
  private String indexDir = null;
  
  /**
   * The results table model.
   */
  private ResultsTableModel resultsModel = null;
  
  /**
   * The results table renderer.
   */
  private ResultsRenderer resultsRenderer = null;
  
  /**
   * The stores table model.
   */
  private StoreTableModel storeModel = null;
  
  /**
   * The list of known data stores.
   */
  private List<StoreInfo> listStores = null;
  
  /**
   * The storage for session data.
   */
  private AppData appData = null;
  
  /**
   * The popup menu for the results table.
   */
  private JPopupMenu searchMenu = null;
  
  /**
   * The popup menu for the stores table.
   */
  private JPopupMenu storesMenu = null;
  
  /**
   * The tabbed pane.
   */
  private JTabbedPane tabbedPane = null;
  
  /**
   * The index of the last selected tab, except for
   * the second tab.
   */
  private int lastSelectedTab = 0;
  
  /**
   * The Browse panel.
   */
  private BrowsePanel browsePanel = null;
  
  /**
   * The popup menu item for viewing a search result as text.
   */
  private JMenuItem searchTextView = null;
  
  /**
   * The popup menu item for viewing a search result as hex.
   */
  private JMenuItem searchHexView = null;
  
  /**
   * The popup menu item for exploring the parent of a search result file.
   */
  private JMenuItem searchExploreParent = null;
  
  /**
   * Default constructor.
   */
  public App()
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
   * Create the application's GUI.
   */
  public void createGUI()
  {
    // Use the default look and feel
    initLookAndFeel();
    
    // Create and set up the window.
    frame = new JFrame("Nemo - Index file names on a drive");
    
    // Generate the GUI and add it to the frame
    buildUI();
    
    // Set the size and center it on the screen
    frame.setSize(new Dimension(800, 600));
    frame.setLocationRelativeTo(null);
    
    // Check if the size/location were read in the xml file
    getAppProps();
    
    // Have the frame call exitApp() whenever it closes
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter()
    {
      @Override
      public void windowClosing(final WindowEvent e)
      {
        exitApp();
      }
    });
    
    // Display the window
    // frame.pack();
    frame.setVisible(true);
    
    // Select the text
    queryField.requestFocusInWindow();
    ((JTextField) queryField.getEditor().getEditorComponent()).selectAll();
  }
  
  
  /**
   * Get the application properties from the INI file.
   */
  private void getAppProps()
  {
    // Instantiate the AppData object and load the data
    appData = new AppData();
    appData.loadData(PROP_FILE_NAME);
    
    // Get the size of the screen
    Dimension screenDim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    
    // Get the root
    Point root = appData.getWindowRoot();
    if (root != null)
    {
      // Make sure the root will fit on this screen
      if ((root.x >= 0) && (root.y >= 0) &&
          (root.x < screenDim.width) && (root.y < screenDim.height))
      {
        frame.setLocation(root);
      }
      
      // Get the window dimension
      Dimension size = appData.getWindowSize();
      if (size != null)
      {
        // Make sure the specified size is positive
        if ((size.height > 0) && (size.width > 0) &&
            (size.height <= screenDim.height) &&
            (size.width <= screenDim.width))
        {
          frame.setSize(size);
        }
      }
    }
    
    // Get the directory for the index files
    String dirName = appData.getIndexDirectory();
    if ((dirName == null) || (dirName.trim().length() < 1))
    {
      // Use the default name
      dirName = ".";
    }
    
    // Check if the directory exists
    File dir = new File(dirName);
    if (!dir.exists())
    {
      // It does not, so make it now
      dir.mkdirs();
    }
    
    // Save the directory name
    indexDir = dirName;
    Utility.setIndexDirectoryName(indexDir);
    
    // Get the list of stores
    listStores = appData.getStoresInfo();
    
    // Populate the table with the list of stores
    if (!listStores.isEmpty())
    {
      storeModel.setRowData(listStores);
    }
    
    // Add the past searches here, select the first one, if there is one
    List<String> searches = appData.getSearches();
    if ((searches != null) && (searches.size() > 0))
    {
      for (String search : searches)
      {
        queryField.addItem(search);
      }
      // Select the first item
      queryField.setSelectedIndex(0);
    }
    
    // Set the search options
    searchCombo.setSelectedIndex(appData.getSearchFiles());
    // The 'Search Selected' checkbox should not be persisted
    // checkSelected.setSelected(appData.getSearchSelected());
    comboFilters.setSelectedIndex(appData.getSearchFilter());
    checkCase.setSelected(appData.getSearchCase());
    checkMaxResults.setSelected(appData.getSearchLimit());
    maxResults.setText(Integer.toString(appData.getSearchMax()));
  }
  
  
  /**
   * Exit the application.
   */
  public void exitApp()
  {
    // Save the application data
    saveApplicationData();
    
    // Close the application
    frame.dispose();
  }
  
  
  /**
   * Save the application data.
   */
  private void saveApplicationData()
  {
    // Store the window state in the properties file
    appData.setWindowRoot(frame.getLocation());
    appData.setWindowSize(frame.getSize());
    appData.setIndexDirectory(indexDir);
    appData.setStoresInfo(listStores);
    
    // Save the search parameters as defaults
    appData.setSearchProperties(searchCombo.getSelectedIndex(),
                                checkSelected.isSelected(),
                                comboFilters.getSelectedIndex(),
                                checkCase.isSelected(),
                                checkMaxResults.isSelected(),
              Utility.getStringAsInteger(maxResults.getText(), 100));
    
    // Get the first few entries in the Search combo box, and save
    final int size = Math.min(10, queryField.getItemCount());
    List<String> searches = new ArrayList<String>(20);
    for (int i = 0; i < size; ++i)
    {
      searches.add((String) queryField.getItemAt(i));
    }
    appData.setSearches(searches);
    
    // Write the data to the properties file
    appData.saveData(PROP_FILE_NAME);
  }
  
  
  /**
   * Construct the user interface.
   */
  private void buildUI()
  {
    // Build the popup menu
    buildPopups();
    
    // Create the panel for the Browse tab
    buildBrowseTab();
    
    // Declare the tabbed pane and add the three children pane
    tabbedPane = new JTabbedPane(SwingConstants.TOP);
    tabbedPane.addTab("Search", buildSearch());
    tabbedPane.addTab("Browse", browsePanel);
    tabbedPane.addTab("Folders", buildFolders());
    
    // Set mnemonics for the tabs
    tabbedPane.setMnemonicAt(0, KeyEvent.VK_S);
    tabbedPane.setMnemonicAt(1, KeyEvent.VK_B);
    tabbedPane.setMnemonicAt(2, KeyEvent.VK_F);
    
    // Disable the middle tab
    tabbedPane.setEnabledAt(1, false);
    
    tabbedPane.addChangeListener(new ChangeListener()
    {
      public void stateChanged(final ChangeEvent e)
      {
        // Record the current tab
        int currTab = tabbedPane.getSelectedIndex();
        
        // If this is the second tab, populate the Browse tab
        if (currTab == 1)
        {
          browseTree();
          return;
        }
        
        boolean enabled = true;
        lastSelectedTab = currTab;
        if (currTab == 0)
        {
          // Record whether the results table has a selected row
          enabled = (resultsTable.getSelectedRow() >= 0);
        }
        else if (currTab == 2)
        {
          // Record whether the stores table has a selected row
          enabled = (storeTable.getSelectedRow() >= 0);
        }
        
        // Enable the second tab based on if the table on the current
        // page has a selected row
        tabbedPane.setEnabledAt(1, enabled);
      }
    });
    
    // Set the preferred pane size
    tabbedPane.setPreferredSize(new Dimension(500, 500));
    
    // Add the main panel to the content pane
    frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
  }
  
  
  /**
   * Populate the tree in the Browse tab.
   */
  private void browseTree()
  {
    // Check the tab that we're going to browse
    if (lastSelectedTab == 0)
    {
      // Get the selected search result
      int row = resultsTable.getSelectedRow();
      if (row >= 0)
      {
        SearchResult result = resultsModel.getRow(row);
        if (result != null)
        {
          // Get the store for this search result and use it to
          // populate the tree
          StoreInfo info = storeModel.getRowByVolumeName(result.getVolume());
          browsePanel.populateBrowsePanel(info);
          
          // Need to open the folder referenced by the search
          // result, and if it's a file, highlight it on the right
          browsePanel.selectResult(result);
        }
      }
    }
    else if (lastSelectedTab == 2)
    {
      // Get the selected store
      int row = storeTable.getSelectedRow();
      if (row >= 0)
      {
        // Get the Store Info reference and use it to populate the tree
        StoreInfo info = storeModel.getRow(row);
        browsePanel.populateBrowsePanel(info);
      }
    }
  }
  
  
  /**
   * Build the Browse tab component.
   */
  private void buildBrowseTab()
  {
    browsePanel = new BrowsePanel(frame);
  }
  
  
  /**
   * Build the two popup menus.
   */
  private void buildPopups()
  {
    // Create the popup menu for the Search page
    searchMenu = new JPopupMenu();
    
    // Create the Open menu item
    JMenuItem searchOpen = new JMenuItem("Open");
    searchOpen.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        int row = resultsTable.getSelectedRow();
        if (row >= 0)
        {
          // Get the selected row and open the file
          SearchResult result = resultsModel.getRow(row);
          Utility.openResult(result, frame);
        }
      }
    });
    searchMenu.add(searchOpen);
    
    // Create the View As Text menu item
    searchTextView = new JMenuItem("View as text");
    searchTextView.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        int row = resultsTable.getSelectedRow();
        if (row >= 0)
        {
          // Get the selected row and open the file in a text viewer
          SearchResult result = resultsModel.getRow(row);
          new TextPopup(new File(result.getPath(), result.getName()), false);
        }
      }
    });
    searchMenu.add(searchTextView);
    
    // Create the View As Text menu item
    searchHexView = new JMenuItem("View as hex");
    searchHexView.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        int row = resultsTable.getSelectedRow();
        if (row >= 0)
        {
          // Get the selected row and open the file in a hex viewer
          SearchResult result = resultsModel.getRow(row);
          new TextPopup(new File(result.getPath(), result.getName()), true);
        }
      }
    });
    searchMenu.add(searchHexView);
    
    // Create the Copy To Clipboard menu item
    searchMenu.add(createCopyMenu());
    
    // Create the Explore Parent menu item
    searchExploreParent = new JMenuItem("Explore parent");
    searchExploreParent.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        // Copy the full path/filename to the clipboard
        int row = resultsTable.getSelectedRow();
        if (row >= 0)
        {
          // Open the parent
          SearchResult result = resultsModel.getRow(row);
          File file = new File(result.getPath());
          Utility.openFile(file, frame);
        }
      }
    });
    searchMenu.add(searchExploreParent);
    
    // Create the popup menu for the Stores table
    storesMenu = new JPopupMenu();
    
    // Create the Refresh menu item
    final JMenuItem storesRefresh = new JMenuItem("Refresh");
    storesRefresh.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        // Refresh the selected store
        int row = storeTable.getSelectedRow();
        if (row >= 0)
        {
          StoreInfo info = storeModel.getRow(row);
          if (info != null)
          {
            // Tell the BrowsePanel that we're refreshing this store, in case it's
            // the cached (displayed) store
            browsePanel.refreshingStore(info);
            
            // Delete the store (without confirmation), and then regenerate
            int foundIndex = deleteStore(info, false);
            generateStore(info.getStoreFilename(), info.getStoreName(), foundIndex);
          }
        }
      }
    });
    storesMenu.add(storesRefresh);
    
    // Create the Delete menu item
    JMenuItem storesDelete = new JMenuItem("Delete");
    storesDelete.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        // Delete the selected store
        int row = storeTable.getSelectedRow();
        if (row >= 0)
        {
          StoreInfo info = storeModel.getRow(row);
          if (info != null)
          {
            deleteStore(info, true);
          }
        }
      }
    });
    storesMenu.add(storesDelete);
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
        int row = resultsTable.getSelectedRow();
        if (row >= 0)
        {
          SearchResult result = resultsModel.getRow(row);
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
        int row = resultsTable.getSelectedRow();
        if (row >= 0)
        {
          SearchResult result = resultsModel.getRow(row);
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
        int row = resultsTable.getSelectedRow();
        if (row >= 0)
        {
          SearchResult result = resultsModel.getRow(row);
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
   * Delete the specified store.
   * 
   * @param info the store to delete
   * @param bConfirm whether to confirm with the user
   * @return the index of the deleted store
   */
  private int deleteStore(final StoreInfo info, final boolean bConfirm)
  {
    // Confirm via a message box
    if (bConfirm)
    {
      final String msg = "Are you sure you wish to delete this?";
      if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(frame, msg,
          "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE))
      {
        return -2;
      }
    }
    
    // Find the store so we can delete it
    int foundIndex = -1;
    for (int i = 0; i < listStores.size(); ++i)
    {
      // Get the current store info object and check for a match
      StoreInfo si = listStores.get(i);
      if (si.getStoreName().equals(info.getStoreName()))
      {
        // Delete the .ser file
        File file = Utility.getIndexedDataFile(indexDir, info);
        Utility.deleteFile(file);
        
        // We have a match, so remove it
        listStores.remove(i);
        storeModel.setRowData(listStores);
        foundIndex = i;
        
        // Break out of the loop
        break;
      }
    }
    
    // Fire a table data change
    storeModel.fireTableDataChanged();
    
    return foundIndex;
  }
  
  
  /**
   * Build the Search tabbed pane.
   * 
   * @return the contents of the Search tab
   */
  private JComponent buildSearch()
  {
    // Create the split pane for the two children panes
    JSplitPane spSearch = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
    
    // Build the top panel (search options)
    JPanel topPanel = new JPanel(new GridBagLayout());
    
    // Set up the field constraints
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(10, 3, 3, 3);
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.NORTH;
    c.fill = GridBagConstraints.NONE;
    
    // The text field to search
    queryField = new JComboBox<String>();
    queryField.setEditable(true);
    topPanel.add(queryField, c);
    
    // The search button
    JButton queryButton = new JButton("  Search  ");
    queryButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final java.awt.event.ActionEvent evt)
      {
        // Only perform this action if the Search tab is selected;
        // tabbed panes can only have one default button, so we
        // don't want to perform this action on "Enter" if the
        // user is on a different tab
        if (tabbedPane.getSelectedIndex() == 0)
        {
          searchForTerm();
        }
      }
    });
    
    // Make the Search button the default button for the app
    frame.getRootPane().setDefaultButton(queryButton);
    
    c.gridx = 1;
    topPanel.add(queryButton, c);
    
    // The dropdown listbox for selecting what to search
    Vector<String> searchOptions = new Vector<String>(3);
    searchOptions.add("Files and Directories");
    searchOptions.add("Files only");
    searchOptions.add("Directories only");
    searchCombo = new JComboBox<String>(searchOptions);
    
    c.gridx = 2;
    c.insets.left = 20;
    topPanel.add(searchCombo, c);
    
    // The checkbox to only search the selected stores
    checkSelected = new JCheckBox("Search Selected Only?");
    
    // Disable it since no store is selected at startup
    checkSelected.setEnabled(false);
    
    c.gridx = 3;
    c.insets.left = 15;
    topPanel.add(checkSelected, c);
    
    // This is the panel for the options below
    JPanel optPanel = new JPanel();
    optPanel.setLayout(new BoxLayout(optPanel, BoxLayout.X_AXIS));
    
    // The combo box for the filters
    Vector<String> vFilters = new Vector<String>(10);
    vFilters.add("Contains");
    vFilters.add("Contains All");
    vFilters.add("Wildcard");
    vFilters.add("Sounds Like");
    vFilters.add("Similar");
    vFilters.add("Regex");
    comboFilters = new JComboBox<String>(vFilters);
    optPanel.add(comboFilters);
    optPanel.add(Box.createRigidArea(new Dimension(40, 0)));
    
    // The checkbox for case-sensitivity
    checkCase = new JCheckBox("Case sensitive?");
    optPanel.add(checkCase);
    optPanel.add(Box.createRigidArea(new Dimension(32, 0)));
    
    // The checkbox for max results
    checkMaxResults = new JCheckBox("Limit results?");
    optPanel.add(checkMaxResults);
    checkMaxResults.addItemListener(new ItemListener()
    {
      public void itemStateChanged(final ItemEvent e)
      {
        boolean bSet = checkMaxResults.isSelected();
        maxResults.setEnabled(bSet);
      }
    });
    
    // The text field for max results
    maxResults = new JTextField("200");
    maxResults.setPreferredSize(new Dimension(40,
                             maxResults.getPreferredSize().height));
    maxResults.setEnabled(false);
    optPanel.add(maxResults);
    optPanel.add(Box.createRigidArea(new Dimension(24, 0)));
    
    // Add an About button
    final JButton btnAbout = new JButton("About");
    btnAbout.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final java.awt.event.ActionEvent evt)
      {
        final String sMem = NumberFormat.getInstance().format(
            Runtime.getRuntime().maxMemory());
        final String sAvail = NumberFormat.getInstance().format(
            Runtime.getRuntime().freeMemory());
        JOptionPane.showMessageDialog(frame,
            "Nemo: An indexer for file directories.\n" + 
            "Written by Mike Wallace, 2010.\n" +
            "Released under the MIT license. Free for any use.\n" +
            "Portions of the source code copyright L2FProd.com.\n" +
            "Maximum memory: " + sMem + " bytes.\n" +
            "Available memory: " + sAvail + " bytes.\n",
            "About Nemo", JOptionPane.INFORMATION_MESSAGE);
      }
    });
    optPanel.add(btnAbout);
    
    // Add the options panel to the top panel
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 4;
    c.weighty = 1.0;
    topPanel.add(optPanel, c);
    
    // Set the preferred size
    topPanel.setPreferredSize(new Dimension(300, 90));
    
    // Create the bottom panel
    JPanel bottomPanel = new JPanel(new BorderLayout());
    
    // Create the JTable (shows results)
    resultsModel = new ResultsTableModel();
    resultsTable = new JTable(resultsModel);
    resultsTable.getTableHeader().setReorderingAllowed(false);
    resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
    resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    fixResultsWidths();
    
    resultsTable.addKeyListener(new KeyAdapter()
    {
      @Override
      public void keyTyped(final KeyEvent e)
      {
        // Let the user hit a character to find the row starting with
        // that character
        super.keyTyped(e);
        char ch = Character.toLowerCase(e.getKeyChar());
        final int row = resultsTable.getSelectedRow();
        final int nextRow = resultsModel.findNextRow(row, ch);
        if (nextRow >= 0)
        {
          resultsTable.getSelectionModel().setSelectionInterval(nextRow, nextRow);
          Utility.scrollToVisible(resultsTable, nextRow, 0);
        }
      }
    });
    
    // Listen for data selection changes
    addTableSelectionListener(resultsTable);
    
    // Add the table listener
    addTableMouseListener(resultsTable, searchMenu);
    
    // Set the renderer for the results table
    resultsRenderer = new ResultsRenderer();
    resultsTable.setDefaultRenderer(Object.class, resultsRenderer);
    
    // Center the column headings
    ((DefaultTableCellRenderer) resultsTable.getTableHeader().
        getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    
    // Listen for clicks on the header
    addTableHeaderMouseListener(resultsTable);
    
    // Put the JTable in a scroll pane and add the scroll pane to the panel
    JScrollPane tableScroll = new JScrollPane(resultsTable);
    bottomPanel.add(tableScroll);
    
    // Add the two child panels to the top panel
    spSearch.add(topPanel);
    spSearch.add(bottomPanel);
    
    // Return the panel
    return spSearch;
  }
  
  
  /**
   * Add a listener to the table header, to allow sorting the table
   * by clicking on a column.
   * 
   * @param table the table to add the listener to
   */
  private void addTableHeaderMouseListener(final JTable table)
  {
    resultsTable.getTableHeader().addMouseListener(new MouseAdapter()
    {
      /**
       * Handle clicking on a column header.
       * 
       * @param e the event
       * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
       */
      @Override
      public void mouseClicked(final MouseEvent e)
      {
        super.mouseClicked(e);
        if (e.getButton() == MouseEvent.BUTTON1)
        {
          final int row = resultsTable.getSelectedRow();
          SearchResult result = (row >= 0) ? resultsModel.getRow(row) : null;
          
          // Sort by the column
          TableColumnModel model = resultsTable.getColumnModel();
          int index = model.getColumnIndexAtX(e.getX());
          Utility.setSortColumn(index);
          resultsModel.sortData();
          
          if (result != null)
          {
            final int rowIndex = resultsModel.findRowByValue(result);
            if (rowIndex >= 0)
            {
              resultsTable.getSelectionModel().setSelectionInterval(rowIndex,
                                                                    rowIndex);
              Utility.scrollToVisible(resultsTable, rowIndex, 0);
            }
          }
        }
      }
    });
  }
  
  
  /**
   * Add a selection listener to the table.  This is so we
   * can enable the second tab (Browse) only when a row in
   * the table is selected.
   * 
   * @param table the table to listen to
   */
  private void addTableSelectionListener(final JTable table)
  {
    table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
    {
      // The selected value changed
      public void valueChanged(final ListSelectionEvent e)
      {
        // If the value is still adjusting, return
        if (e.getValueIsAdjusting())
        {
          return;
        }
        
        // See if a row is selected
        boolean empty = ((DefaultListSelectionModel) e.getSource()).isSelectionEmpty();
        
        // Enable the second tab only if a row is selected
        tabbedPane.setEnabledAt(1, !empty);
        
        // When showing the popup menu on the Search page, enable the
        // Text/Hex views if the selection is not a directory
        if (tabbedPane.getSelectedIndex() == 0)
        {
          int row = resultsTable.getSelectedRow();
          if (row >= 0)
          {
            // Get the selected row and open the file
            SearchResult result = resultsModel.getRow(row);
            searchTextView.setEnabled(!result.isDirectory());
            searchHexView.setEnabled(!result.isDirectory());
            searchExploreParent.setEnabled(!result.isDirectory());
          }
        }
        else if (tabbedPane.getSelectedIndex() == 2)
        {
          // If no row in the Stores table is selected, disable
          // the "Search Selected" checkbox on the Search page
          if (empty)
          {
            // No selection, so first uncheck the checkbox
            checkSelected.setSelected(false);
          }
          checkSelected.setEnabled(!empty);
        }
      }
    });
  }
  
  
  /**
   * Add the listener for right-clicking on a table.
   * 
   * @param table the table
   * @param menu the menu to show
   */
  private static void addTableMouseListener(final JTable table, final JPopupMenu menu)
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
   * Search folders for the selected term.
   */
  private void searchForTerm()
  {
    // Check that there is a search term in the combo box
    String searchTerm = (String) queryField.getSelectedItem();
    if (searchTerm == null)
    {
      JOptionPane.showMessageDialog(frame, "Please enter a search term",
          "No Search Term", JOptionPane.ERROR_MESSAGE);
      return;
    }
    
    // Check if the item is in the list.  If not, add it.
    boolean containsTerm = false;
    for (int i = 0; i < queryField.getItemCount(); ++i)
    {
      if (searchTerm.equals(queryField.getItemAt(i)))
      {
        containsTerm = true;
        break;
      }
    }
    if (!containsTerm)
    {
      queryField.insertItemAt(searchTerm, 0);
    }
    
    // Check whether to only search the selected folder(s)
    final boolean searchSelected = ((checkSelected.isEnabled()) &&
                                    (checkSelected.isSelected()));
    
    // Save the search options
    int selOption = searchCombo.getSelectedIndex();
    final boolean includeFiles = ((selOption == 0) || (selOption == 1));
    final boolean includeDirs = ((selOption == 0) || (selOption == 2));
    final int maxResultSize = getMaxResults();
    
    // Save the list of results
    List<SearchResult> results = new ArrayList<SearchResult>(100);
    
    // Build the filter
    TermFilter filter = getTermFilter(searchTerm);
    
    // Check if only selected stores should be searched
    SearchProgressDialog searchProgDlg = null;
    if (searchSelected)
    {
      // Check the number of selected row
      int[] rows = storeTable.getSelectedRows();
      if (rows.length > 0)
      {
        // Build the list of stores to search
        List<StoreInfo> infos = new ArrayList<StoreInfo>(rows.length);
        for (int i = 0; i < rows.length; ++i)
        {
          // Add the selected row to our temporary array
          infos.add(listStores.get(rows[i]));
        }
        
        // Now search all of the selected stores
        searchProgDlg = new SearchProgressDialog(frame, filter, infos, includeFiles,
                                                 includeDirs, results, maxResultSize);
      }
    }
    else
    {
      // Search all stores
      searchProgDlg = new SearchProgressDialog(frame, filter, listStores, includeFiles,
                                               includeDirs, results, maxResultSize);
    }
    
    // If we created the dialog, start processing
    if (searchProgDlg != null)
    {
      searchProgDlg.start();
      if (!searchProgDlg.completed())
      {
        // The search was interrupted, so empty the results list
        results.clear();
      }
    }
    
    // Sort the results
    Utility.resetSortParameters();
    Collections.sort(results);
    
    // Save the number of directories in the list
    int numDirs = 0;
    for (SearchResult result : results)
    {
      if (result.isDirectory())
      {
        ++numDirs;
      }
    }
    
    // Save the number of directories for the display
    resultsRenderer.setNumberOfDirectories(numDirs);
    
    // Show the results
    resultsModel.setRowData(results);
    resultsModel.fireTableDataChanged();
    
    // Fix results column widths
    fixResultsWidths();
  }
  
  
  /**
   * Return the term filter for the search.
   * 
   * @param searchTerm the term to search for
   * @return the term filter for the search
   */
  private TermFilter getTermFilter(final String searchTerm)
  {
    // Get the selected index and whether to ignore case
    int index = comboFilters.getSelectedIndex();
    boolean ignoreCase = !checkCase.isSelected();
    
    // Create the term based on the selected filter
    switch (index)
    {
      case 0: return new ContainsFilter(searchTerm, ignoreCase);
      case 1: return new ContainsAllFilter(searchTerm, ignoreCase);
      case 2: return new WildcardFilter(searchTerm, ignoreCase);
      case 3: return new SoundFilter(searchTerm, ignoreCase);
      case 4: return new SimilarFilter(searchTerm, ignoreCase, 3);
      case 5: return new RegexFilter(searchTerm, ignoreCase);
      default: return null;
    }
  }
  
  
  /**
   * Fix the widths of the columns in the results table.
   */
  private void fixResultsWidths()
  {
    TableColumnModel model = resultsTable.getColumnModel();
    model.getColumn(0).setPreferredWidth(180);
    model.getColumn(0).setMinWidth(20);
    model.getColumn(0).setMaxWidth(250);
    model.getColumn(1).setPreferredWidth(130);
    model.getColumn(1).setMinWidth(20);
    model.getColumn(1).setMaxWidth(130);
    model.getColumn(2).setPreferredWidth(70);
    model.getColumn(2).setMinWidth(20);
    model.getColumn(2).setMaxWidth(70);
    model.getColumn(3).setPreferredWidth(100);
    model.getColumn(3).setMinWidth(20);
    model.getColumn(3).setMaxWidth(100);
    model.getColumn(4).setPreferredWidth(350);
  }
  
  
  /**
   * Get the maximum number of results to retrieve.
   * 
   * @return the maximum number of results
   */
  private int getMaxResults()
  {
    // Get the maximum number of results
    int maxResultSize = 1000;
    if (checkMaxResults.isSelected())
    {
      // Get the max results specified by the user
      maxResultSize = Utility.getStringAsInteger(maxResults.getText(), 0);
      
      // Normalize the user's value
      maxResultSize = Math.max(0, Math.min(maxResultSize, 1000));
    }
    
    return maxResultSize;
  }
  
  
  /**
   * Build the Folders tabbed pane.
   * 
   * @return the contents of the Folders tab
   */
  private JComponent buildFolders()
  {
    // Create the split pane for the two children panes
    JSplitPane spFolders = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
    
    // Build the top panel (store options)
    JPanel topPanel = new JPanel(new GridBagLayout());
    
    // Set up the field constraints
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(10, 3, 3, 3);
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.EAST;
    
    // The label for the root (store) name
    JLabel labelRoot = new JLabel("Folder: ");
    topPanel.add(labelRoot, c);
    
    // The store name
    volumeDir = new JTextField(20);
    c.gridx = 1;
    c.gridwidth = 2;
    c.anchor = GridBagConstraints.WEST;
    topPanel.add(volumeDir, c);
    
    // Add the [...] button
    JButton browseButton = new JButton(" ... ");
    browseButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final java.awt.event.ActionEvent evt)
      {
        browseDirs(volumeDir.getText().trim());
      }
    });
    
    c.gridx = 2;
    c.gridwidth = 1;
    topPanel.add(browseButton, c);
    
    // The volume name
    JLabel volumeLabel = new JLabel("Label: ");
    
    c.gridx = 0;
    c.gridy = 1;
    c.insets.top = 4;
    // c.insets.bottom = 10;
    c.weighty = 1.0;
    c.anchor = GridBagConstraints.NORTH;
    topPanel.add(volumeLabel, c);
    
    // The field for entering the store name
    volumeName = new JTextField(20);
    
    c.gridx = 1;
    c.insets.top = 2;
    topPanel.add(volumeName, c);
    
    // The "Add" button
    JButton addButton = new JButton("Add");
    addButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final java.awt.event.ActionEvent evt)
      {
        addStore();
      }
    });
    
    c.insets.top = 2;
    c.gridx = 2;
    topPanel.add(addButton, c);
    
    // Set the preferred size
    topPanel.setPreferredSize(new Dimension(300, 50));
    
    // Create the bottom panel
    JPanel bottomPanel = new JPanel(new BorderLayout());
    
    // Create the JTable (shows results)
    storeModel = new StoreTableModel();
    storeTable = new JTable(storeModel);
    storeTable.getTableHeader().setReorderingAllowed(false);
    storeTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    
    // Center the column headings
    ((DefaultTableCellRenderer) storeTable.getTableHeader().
        getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    
    // Add the mouse listener
    addTableMouseListener(storeTable, storesMenu);
    
    // Add a selection listener
    addTableSelectionListener(storeTable);
    
    // Put the JTable in a scroll pane and add the scroll pane to the panel
    JScrollPane tableScroll = new JScrollPane(storeTable);
    bottomPanel.add(tableScroll);
    
    // Add the two child panels to the top panel
    spFolders.add(topPanel);
    spFolders.add(bottomPanel);
    
    // Return the panel
    return spFolders;
  }
  
  
  /**
   * Browse the directories, saving the result to the root field
   * on the Folders page.
   * 
   * @param startDir the initial directory
   */
  private void browseDirs(final String startDir)
  {
    // Set up the directory chooser
    JDirectoryChooser chooser = new JDirectoryChooser("abc");
    
    // Check if a directory was specified
    if ((startDir != null) && (startDir.length() > 0))
    {
      // Verify it exists
      final File file = new File(startDir);
      if ((file.exists()) && (file.isDirectory()))
      {
        chooser.setSelectedFile(file);
      }
    }
    
    // Disable multiple selections
    chooser.setMultiSelectionEnabled(false);
    
    // Show the chooser and check if the user hits OK to close it
    int choice = chooser.showOpenDialog(frame);
    if (choice == JDirectoryChooser.APPROVE_OPTION)
    {
      // Save the selected directory name
      File dir = chooser.getSelectedFile();
      
      // Update the text field with the value
      volumeDir.setText(dir.getAbsolutePath());
      
      // Clear the label entry field and request focus there
      volumeName.setText("");
      volumeName.requestFocusInWindow();
    }
  }
  
  
  /**
   * Add a new store, or replace an existing store.
   */
  private void addStore()
  {
    // Get the directory to index
    final String dir = volumeDir.getText().trim();
    if (dir.length() < 1)
    {
      return;
    }
    
    // Save the store name and directory
    final String name = volumeName.getText().trim();
    
    // Create the data store
    if (generateStore(dir, name, -1))
    {
      // It was successful, so clear the two text fields
      volumeDir.setText("");
      volumeName.setText("");
    }
  }
  
  
  /**
   * Generate the data store by indexing the directory.
   * 
   * @param dir the directory name
   * @param name the volume label
   * @param insertIndex the index to insert the store info into
   * @return whether the generation was successful
   */
  private boolean generateStore(final String dir, final String name,
                                final int insertIndex)
  {
    // Verify the directory exists
    File volume = new File(dir);
    if ((!volume.exists()) || (!volume.isDirectory()))
    {
      JOptionPane.showMessageDialog(frame,
                          "The volume does not exist or is not a directory",
                          "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    
    // Check if the store is already in the list
    if (StoreInfo.listContainsStore(listStores, dir))
    {
      JOptionPane.showMessageDialog(frame, "The store already exists in the list",
                                    "Error", JOptionPane.ERROR_MESSAGE);
      return true;
    }
    
    // Generate the name of the output file
    final String filename = Store.computeNameFromDirectory(name);
    if (filename.length() < 1)
    {
      // Show an error message
      JOptionPane.showMessageDialog(frame, "The volume label is invalid",
                                    "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    
    // See if the file already exists (either for the same directory
    // or a different one) (the name must be unique)
    for (StoreInfo si : listStores)
    {
      if ((si.getStoreName().equalsIgnoreCase(name)) ||
          (Store.computeNameFromDirectory(si.getStoreName()).equalsIgnoreCase(filename)))
      {
        // The volume name is already used, so show an error message and return
        JOptionPane.showMessageDialog(frame, "The volume name is already in use",
                                      "Error", JOptionPane.ERROR_MESSAGE);
        return false;
      }
    }
    
    // Check if the user is adding a store that already exists (as a
    // match on the generated file name)
    final long lastMod =
      Utility.getLastModifiedTime(new File(indexDir, filename + ".ser"));
    if ((lastMod > 0L) && (insertIndex < 0))
    {
      // The user is adding a store that already exists but
      // is not in the store list
      
      // Generate a store reference for the file (don't reindex)
      StoreInfo info = new StoreInfo(name, dir, lastMod);
      
      // Add the store to the store list
      listStores.add(info);
      
      // Update the data model showing the list of stores
      storeModel.setRowData(listStores);
      
      // Force a visual update
      storeModel.fireTableDataChanged();
      
      // Save the app data
      saveApplicationData();
      
      // Exit the method
      return true;
    }
    
    // Index the directory in a different thread
    ProgressDialog progDlg = new ProgressDialog(frame, name, dir,
                                    indexDir, filename);
    
    // Check if it completed
    if (progDlg.completed())
    {
      // Add the store name to the store table
      StoreInfo info = new StoreInfo(name, dir, System.currentTimeMillis());
      if (insertIndex >= 0)
      {
        listStores.add(insertIndex, info);
      }
      else
      {
        listStores.add(info);
      }
    }
    
    // Add the store name to the store table
    storeModel.setRowData(listStores);
    storeModel.fireTableDataChanged();
    
    // Save the app data
    saveApplicationData();
    
    return true;
  }
  
  
  /**
   * Initialize the application's Look And Feel.
   */
  private void initLookAndFeel()
  {
    // Use the default look and feel
    try
    {
      javax.swing.UIManager.setLookAndFeel(
        javax.swing.UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception e)
    {
      writeErr("Exception: " + e.getMessage());
    }
  }
  
  
  /**
   * Create and show the GUI application.
   */
  private static void createAndShowGUI()
  {
    // Run the application
    new App().createGUI();
  }
  
  
  /**
   * Make the application compatible with Apple Macs.
   * 
   * @param appName the name of the application
   */
  public static void makeMacCompatible(final String appName)
  {
    // Set the system properties that a Mac uses
    System.setProperty("apple.awt.brushMetalLook", "true");
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("apple.awt.showGrowBox", "true");
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", appName);
  }
  
  
  /**
   * Entry point to the application.
   * 
   * @param args arguments passed to the application
   */
  public static void main(final String[] args)
  {
    // Set up the Mac-related properties
    makeMacCompatible("Nemo");
    
    // Get the system information
    SystemInfo.initialize();
    
    // Schedule a job for the event-dispatching thread:
    // creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        createAndShowGUI();
      }
    });
  }
}
