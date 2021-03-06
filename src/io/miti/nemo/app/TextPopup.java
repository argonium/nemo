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

import io.miti.nemo.common.Utility;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

/**
 * This class encapsulates the functionality of a popup
 * window that shows a text file.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class TextPopup
{
  /**
   * The highlight painter.
   */
  private static final HighlightPainter painter =
    new DefaultHighlighter.DefaultHighlightPainter(new java.awt.Color(157, 185, 235));
  
  /**
   * The index of the last found occurrence.
   */
  private int lastStart = -1;
  
  /**
   * The length of the last found occurrence.
   */
  private int lastEnd = 0;
  
  /**
   * Whether to match case during searches.
   */
  private boolean matchCase = false;

  /**
   * Default constructor.
   */
  @SuppressWarnings("all")
  private TextPopup()
  {
    super();
  }
  
  
  /**
   * Constructor taking the input file.
   * 
   * @param file the input file
   * @param inHex whether to show the file contents in hex mode
   */
  public TextPopup(final File file, final boolean inHex)
  {
    super();
    initUI(file, inHex);
  }
  
  
  /**
   * Initialize the UI.
   * 
   * @param file the input file
   * @param inHex whether to show the file contents in hex
   */
  private void initUI(final File file, final boolean inHex)
  {
    // Initialize the frame
    final JFrame frame = new JFrame("View");
    
    // Set the window size
    frame.setSize(590, 700);
    frame.setLocationRelativeTo(null);
    
    // Check the file
    if ((!file.exists()) || (file.isDirectory()))
    {
      JOptionPane.showMessageDialog(frame, "Error: The file was not found",
                                    "Error", JOptionPane.ERROR_MESSAGE);
      frame.dispose();
      return;
    }
    
    // Verify we have enough free memory to show the file
    System.gc();
    System.gc();
    final long fileSize = file.length();
    final long reqdMem = 7024000L + ((inHex) ? (fileSize * 10L) : (fileSize * 2L));
    final long freeMem = Runtime.getRuntime().freeMemory();
    if (reqdMem > freeMem)
    {
      JOptionPane.showMessageDialog(frame, "Error: Not enough memory is available",
                                    "Error", JOptionPane.ERROR_MESSAGE);
      frame.dispose();
      return;
    }
    
    // Instantiate the editor, passing a URL to the help file
    final JTextArea editor = new JTextArea();
    
    // Set the font size
    java.awt.Font f = editor.getFont();
    editor.setFont(f.deriveFont(12.0f));
    
    // Don't let the user edit the help
    editor.setEditable(false);
    
    // Set the text
    if (inHex)
    {
      editor.setText(Utility.getFileAsHex(file));
    }
    else
    {
      editor.setText(Utility.getFileAsText(file));
    }
    
    // Scroll to the top
    editor.setCaretPosition(0);
    
    // Set wrapping properties
    editor.setLineWrap(true);
    editor.setWrapStyleWord(true);
    
    // Add the editor to a scroll pane, and add that to a panel
    JScrollPane edScroll = new JScrollPane(editor);
    
    // Create the panel that goes in the middle of the screen
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    panel.add(edScroll, BorderLayout.CENTER);
    
    // Add the search panel
    panel.add(getSearchPanel(editor), BorderLayout.NORTH);
    
    // Add the panel to the frame
    frame.getContentPane().add(panel, BorderLayout.CENTER);
    
    // Create the Close button
    JButton btnClose = new JButton("Close");
    
    // Add a handler for the button (close the frame)
    btnClose.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final java.awt.event.ActionEvent evt)
      {
        editor.setText("");
        frame.dispose();
      }
    });
    
    // Create the panel that will hold the button
    JPanel panelClose = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    
    // Add the button to the panel, and the panel to the frame
    panelClose.add(btnClose);
    frame.getContentPane().add(panelClose, BorderLayout.SOUTH);
    
    // Set the default close operation
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
    // Make the frame visible
    frame.setVisible(true);
    
    // Request focus for the scroll pane
    edScroll.requestFocus();
  }
  
  
  /**
   * Return the panel for showing the search components.
   * 
   * @param editor the text editor
   * @return the panel for showing the search components
   */
  private JPanel getSearchPanel(final JTextArea editor)
  {
    // Set up the panel for the search controls:
    // Search: ______________   [Next] [Previous]  _ Match Case
    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
    
    // Add the label
    JLabel lblSearch = new JLabel("Search:");
    lblSearch.setDisplayedMnemonic(KeyEvent.VK_S);
    searchPanel.add(lblSearch);
    
    // Add the search textfield
    final JTextField tfSearch = new JTextField(20);
    lblSearch.setLabelFor(tfSearch);
    tfSearch.getDocument().addDocumentListener(new DocumentListener()
    {
      @Override
      public void changedUpdate(final DocumentEvent e)
      {
        handleSearchTermChange(tfSearch, editor, true);
      }
      
      @Override
      public void insertUpdate(final DocumentEvent e)
      {
        handleSearchTermChange(tfSearch, editor, true);
      }
      
      @Override
      public void removeUpdate(final DocumentEvent e)
      {
        handleSearchTermChange(tfSearch, editor, true);
      }
    });
    searchPanel.add(tfSearch);
    
    // Add the Next button
    final JButton btnNext = new JButton("Next");
    btnNext.setMnemonic(KeyEvent.VK_N);
    btnNext.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(final ActionEvent e)
      {
        lastStart = lastEnd;
        handleSearchTermChange(tfSearch, editor, true);
      }
    });
    searchPanel.add(btnNext);
    
    // Add the Previous button
    final JButton btnPrev = new JButton("Previous");
    btnPrev.setMnemonic(KeyEvent.VK_P);
    btnPrev.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(final ActionEvent e)
      {
        handleSearchTermChange(tfSearch, editor, false);
      }
    });
    searchPanel.add(btnPrev);
    
    // Add the Match Case checkbox
    final JCheckBox cbCase = new JCheckBox("Match case?");
    cbCase.setMnemonic(KeyEvent.VK_M);
    cbCase.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(final ActionEvent e)
      {
        // Record the case preference
        matchCase = cbCase.isSelected();
      }
    });
    searchPanel.add(cbCase);
    
    return searchPanel;
  }
  
  
  /**
   * Handle searching for a term.
   * 
   * @param tfSearch the search text field
   * @param editor the editor showing the file text
   * @param searchForward whether to search forward
   */
  private void handleSearchTermChange(final JTextField tfSearch,
                                      final JTextArea editor,
                                      final boolean searchForward)
  {
    // Reset the background color
    tfSearch.setBackground(java.awt.Color.WHITE);
    
    // Remove the highlights from the text
    Highlighter h = editor.getHighlighter();
    h.removeAllHighlights();
    
    // Get the search string
    String search = tfSearch.getText();
    if (search.length() < 1)
    {
      // No search string, so reset the variables
      lastStart = -1;
      lastEnd = 0;
      return;
    }
      
    // Get the text we're going to search
    String text = editor.getText();
    if (text.length() < 1)
    {
      return;
    }
    
    // See if we're ignoring case
    if (!matchCase)
    {
      // We are, so make everything lower case
      text = text.toLowerCase();
      search = search.toLowerCase();
    }
    
    // Search for the term
    if (searchForward)
    {
      int i = text.indexOf(search, lastStart);
      if (i >= 0)
      {
        handleMatchFound(h, i, search.length(), editor, tfSearch);
      }
      else
      {
        // The string was not found, so try again, starting
        // from the start of the file
        i = text.indexOf(search);
        if (i >= 0)
        {
          handleMatchFound(h, i, search.length(), editor, tfSearch);
        }
        else
        {
          // Match not found
          tfSearch.setBackground(java.awt.Color.RED);
        }
      }
    }
    else
    {
      // Search backwards
      --lastStart;
      boolean startedAtEnd = false;
      if (lastStart < 0)
      {
        // We're before the beginning of the file, so jump to the end
        lastStart = text.length();
        startedAtEnd = true;
      }
      
      int i = text.lastIndexOf(search, lastStart);
      if (i >= 0)
      {
        handleMatchFound(h, i, search.length(), editor, tfSearch);
      }
      else if (!startedAtEnd)
      {
        // The string was not found, so try again, starting
        // from the end of the file
        i = text.lastIndexOf(search);
        if (i >= 0)
        {
          handleMatchFound(h, i, search.length(), editor, tfSearch);
        }
        else
        {
          // Match not found
          tfSearch.setBackground(java.awt.Color.RED);
          
          if (startedAtEnd)
          {
            lastStart = -1;
            lastEnd = 0;
          }
        }
      }
      else
      {
        // Match not found
        tfSearch.setBackground(java.awt.Color.RED);
        
        lastStart = -1;
        lastEnd = 0;
      }
    }
  }
  
  
  /**
   * A search match was found.
   * 
   * @param h the highlighter
   * @param i the index of the match
   * @param searchLen the length of the search string
   * @param editor the text editor
   * @param tfSearch the search text field
   */
  private void handleMatchFound(final Highlighter h,
                                final int i,
                                final int searchLen,
                                final JTextArea editor,
                                final JTextField tfSearch)
  {
    lastStart = i;
    lastEnd = i + searchLen;
    try
    {
      h.addHighlight(i, lastEnd, painter);
      
      // When a match is found, make sure the position is visible
      editor.requestFocusInWindow();
      editor.setCaretPosition(lastStart);
      tfSearch.requestFocusInWindow();
    }
    catch (BadLocationException ble)
    {
      ble.printStackTrace();
    }
  }
}
