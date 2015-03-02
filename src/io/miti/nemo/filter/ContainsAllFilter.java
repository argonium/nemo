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

package io.miti.nemo.filter;

import java.util.HashMap;
import java.util.List;

/**
 * Provide a filter for search terms that only
 * accepts matches where the parameter to accept()
 * contains all of the phrases in the string used
 * to initialize this class, where a phrase is either
 * a word delimited by spaces or one or more words
 * surrounded by quotes.  If a phrase occurs more
 * than once in the string passed to the constructor,
 * then it must occur at least the same number of times
 * in the string passed to accept().
 * 
 * @author mwallace
 * @version 1.0
 */
public final class ContainsAllFilter implements TermFilter
{
  /**
   * Whether to ignore the case.
   */
  private final boolean ignoreCase;
  
  /**
   * The list of terms.
   */
  private final HashMap<String, Integer> terms;
  
  
  /**
   * Default constructor.
   */
  @SuppressWarnings("unused")
  private ContainsAllFilter()
  {
    super();
    ignoreCase = false;
    terms = new HashMap<String, Integer>(0);
  }
  
  
  /**
   * Initializes the filter with the source term and
   * whether to ignore case on searches.
   * 
   * @param word the source term
   * @param bIgnoreCase whether to ignore the case of string comparisons
   */
  public ContainsAllFilter(final String word,
                           final boolean bIgnoreCase)
  {
    super();
    
    // Save whether to ignore the case
    ignoreCase = bIgnoreCase;
    
    // See if we need to ignore case
    final String target = ((ignoreCase) ? word.toLowerCase() : word);
    
    // Save the String parameter, after processing
    final List<String> strings = FilterUtility.parseIntoPhrases(target);
    final int size = strings.size();
    
    // Build the hashmap
    terms = new HashMap<String, Integer>(size);
    for (int i = 0; i < size; ++i)
    {
      // Save the current string
      final String term = strings.get(i);
      
      // See if the hashmap contains the string
      if (!terms.containsKey(term))
      {
        terms.put(term, Integer.valueOf(1));
      }
      else
      {
        // Increment the count
        Integer count = Integer.valueOf(terms.get(term).intValue() + 1);
        terms.put(term, count);
      }
    }
  }
  
  
  /**
   * Determines if the term matches the source term.
   * 
   * @param word the term to compare to the source term
   * @return whether the terms match
   */
  @Override
  public boolean accept(final String word)
  {
    // Check the input
    if (word == null)
    {
      return false;
    }
    
    // See if we need to ignore case
    final String target = ((ignoreCase) ? word.toLowerCase() : word);
    
    // Iterate over the list of phrases to see if target
    // contains all of them
    boolean found = true;
    for (String key : terms.keySet())
    {
      // Get the number of occurrences
      final int count = terms.get(key);
      
      // See if it exists
      int index = target.indexOf(key);
      if (index < 0)
      {
        // The string was not found
        found = false;
        break;
      }
      
      // It does, so check all the other occurrences (if any)
      for (int i = 1; (i < count) && (found); ++i)
      {
        // See if it exists after the previous occurrence
        index = target.indexOf(key, index + 1);
        if (index < 0)
        {
          // Not found, so update the status and break
          found = false;
          break;
        }
      }
      
      // Check for failure
      if (!found)
      {
        break;
      }
    }
    
    // Return whether we found one of the strings
    return found;
  }
}
