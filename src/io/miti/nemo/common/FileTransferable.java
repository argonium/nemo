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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * Class to copy a file reference to the clipboard.
 * 
 * @author mwallace
 */
public final class FileTransferable implements Transferable
{
  /**
   * Supported data flavor.
   */
  private DataFlavor[] dataFlavors = {DataFlavor.javaFileListFlavor};
  
  /**
   * Instances of the File classes to be transferred.
   */
  private List<File> files = new LinkedList<File>();
  
  /**
   * Return the supported flavors.
   * 
   * @return the supported flavors
   */
  public DataFlavor[] getTransferDataFlavors()
  {
    return dataFlavors;
  }
  
  
  /**
   * Return whether the flavor is supported.
   * 
   * @param flavor the flavor to check
   * @return whether the flavor is supported
   */
  public boolean isDataFlavorSupported(final DataFlavor flavor)
  {
    return dataFlavors[0].equals(flavor);
  }
  
  
  /**
   * Return the data.
   * 
   * @param flavor the flavor
   * @return the data
   * @throws IOException
   * @throws UnsupportedFlavorException
   */
  public Object getTransferData(final DataFlavor flavor)
    // throws UnsupportedFlavorException, IOException
  {
    return files;
  }
  
  
  /**
   * Adds a file for the transfer.
   * 
   * @param f the file
   */
  private void addFile(final File f)
  {
    files.add(f);
  }
  
  
  /**
   * Check if this is a valid file.
   * 
   * @param file the file
   * @return whether it exists and is a file
   */
  private static boolean checkFile(final File file)
  {
    // Check the input
    if (file == null)
    {
      JOptionPane.showMessageDialog(null, "The file is null", "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    }
    else if (!file.exists())
    {
      JOptionPane.showMessageDialog(null, "The file does not exist", "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    }
    
    return true;
  }
  
  
  /**
   * Copies the file to the clipboard.
   * 
   * @param file the file to copy
   */
  public static void copyToClipboard(final File file)
  {
    if (!checkFile(file))
    {
      return;
    }
    
    FileTransferable ft = new FileTransferable();
    ft.addFile(file);
    java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ft, null);
  }
}
