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

import io.miti.nemo.common.StoreInfo;
import io.miti.nemo.common.Utility;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handler for importing application data.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class AppDataImporter extends DefaultHandler
{
  /**
   * The input filename.
   */
  private String filename = null;
  
  /**
   * The element that we're currently in.
   */
  private transient int mode = 0;
  
  /**
   * The instance of the AppData class.
   */
  private transient AppData appData = null;
  
  /**
   * The current store info.
   */
  private transient StoreInfo storeInfo = null;
  
  /**
   * The buffer for element strings.
   */
  private transient StringBuilder wordBuilder = new StringBuilder(100);
  
  
  /**
   * Default constructor.
   */
  @SuppressWarnings("unused")
  private AppDataImporter()
  {
    super();
  }
  
  
  /**
   * Constructor taking a filename.
   * 
   * @param sFilename the input filename
   * @param pAppData the AppData instance
   */
  public AppDataImporter(final String sFilename,
                         final AppData pAppData)
  {
    super();
    filename = sFilename;
    appData = pAppData;
  }
  
  
  /**
   * Parses an XML file using a SAX parser.
   * 
   * @return the result of the operation
   */
  public boolean parseXmlFile()
  {
    boolean bResult = false;
    try
    {
      // Create a builder factory
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setValidating(false);
      
      // Create the builder and parse the file
      factory.newSAXParser().parse(new File(filename), this);
      
      // Check for an error
      bResult = true;
    }
    catch (SAXException e)
    {
      bResult = false;
      e.printStackTrace();
    }
    catch (ParserConfigurationException e)
    {
      bResult = false;
      e.printStackTrace();
    }
    catch (IOException e)
    {
      bResult = false;
      e.printStackTrace();
    }
    
    // Return the result of the operation
    return bResult;
  }


  /**
   * Handle the characters.
   * 
   * @param ch the character array
   * @param start the start index
   * @param length the array length
   * @throws SAXException parsing exception
   * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
   */
  @Override
  public void characters(final char[] ch,
                         final int start,
                         final int length) throws SAXException
  {
    // Check if we're handling a field of interest
    if (mode == 0)
    {
      // We're not, so return
      return;
    }
    
    // Add the string to our word buffer
    final String word = new String(ch, start, length);
    wordBuilder.append(word);
  }
  
  
  /**
   * Finish the document.
   * 
   * @throws SAXException parsing exception
   * @see org.xml.sax.helpers.DefaultHandler#endDocument()
   */
  @Override
  public void endDocument() throws SAXException
  {
  }
  
  
  /**
   * Finish parsing an element.
   * 
   * @param uri the URI
   * @param localName the local name
   * @param name the element name
   * @throws SAXException parsing exception
   * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
   *      java.lang.String, java.lang.String)
   */
  @Override
  public void endElement(final String uri,
                         final String localName,
                         final String name)
      throws SAXException
  {
    // Check the current mode
    String word = wordBuilder.toString();
    switch (mode)
    {
      case 1:
        appData.setWindowRootX(Utility.getStringAsInteger(word, 0));
        break;
        
      case 2:
        appData.setWindowRootY(Utility.getStringAsInteger(word, 0));
        break;
        
      case 3:
        appData.setWindowHeight(Utility.getStringAsInteger(word, 0));
        break;
        
      case 4:
        appData.setWindowWidth(Utility.getStringAsInteger(word, 0));
        break;
        
      case 5:
        appData.setIndexDirectory(Utility.removeXmlChars(word));
        break;
        
      case 6:
        storeInfo = new StoreInfo();
        storeInfo.setStoreFilename(Utility.removeXmlChars(word));
        break;
        
      case 7:
        storeInfo.setStoreName(Utility.removeXmlChars(word));
        appData.addStore(storeInfo);
        break;
        
      case 8:
        appData.addSearch(Utility.removeXmlChars(word));
        break;
      
      case 9:
        appData.setSearchFiles(Utility.getStringAsInteger(word, 0));
        break;
      
      case 10:
        appData.setSearchSelected(Utility.getStringAsBoolean(word));
        break;
      
      case 11:
        appData.setSearchFilter(Utility.getStringAsInteger(word, 0));
        break;
        
      case 12:
        appData.setSearchCase(Utility.getStringAsBoolean(word));
        break;
        
      case 13:
        appData.setSearchLimit(Utility.getStringAsBoolean(word));
        break;
        
      case 14:
        appData.setSearchMax(Utility.getStringAsInteger(word, 100));
        break;
        
      default:
        break;
    }
    
    // Reset the mode
    mode = 0;
  }
  
  
  /**
   * Start processing the document.
   * 
   * @throws SAXException parsing exception
   * @see org.xml.sax.helpers.DefaultHandler#startDocument()
   */
  @Override
  public void startDocument() throws SAXException
  {
  }
  
  
  /**
   * Start parsing an element.
   * 
   * @param uri the uri
   * @param localName the local name
   * @param name the name
   * @param attributes the element attributes
   * @throws SAXException parsing exception
   * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
   *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  @Override
  public void startElement(final String uri,
                           final String localName,
                           final String name,
                           final Attributes attributes) throws SAXException
  {
    // Set the mode, based on what element we're in
    if (name.equals("rootx"))
    {
      mode = 1;
    }
    else if (name.equals("rooty"))
    {
      mode = 2;
    }
    else if (name.equals("sizeheight"))
    {
      mode = 3;
    }
    else if (name.equals("sizewidth"))
    {
      mode = 4;
    }
    else if (name.equals("indexdir"))
    {
      mode = 5;
    }
    else if (name.equals("file"))
    {
      mode = 6;
    }
    else if (name.equals("name"))
    {
      mode = 7;
    }
    else if (name.equals("search"))
    {
      mode = 8;
    }
    else if (name.equals("search.files"))
    {
      mode = 9;
    }
    else if (name.equals("search.selected"))
    {
      mode = 10;
    }
    else if (name.equals("search.filter"))
    {
      mode = 11;
    }
    else if (name.equals("search.case"))
    {
      mode = 12;
    }
    else if (name.equals("search.limit"))
    {
      mode = 13;
    }
    else if (name.equals("search.max"))
    {
      mode = 14;
    }
    else
    {
      mode = 0;
    }
    
    // Clear the word buffer
    wordBuilder.setLength(0);
  }
}
