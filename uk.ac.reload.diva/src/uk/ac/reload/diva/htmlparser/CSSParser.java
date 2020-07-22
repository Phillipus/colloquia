/**
 *  RELOAD TOOLS
 *
 *  Copyright (c) 2003 Oleg Liber, Bill Olivier, Phillip Beauvoir
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *  Project Management Contact:
 *
 *  Oleg Liber
 *  Bolton University
 *  Deane Road
 *  Bolton BL3 5AB
 *  UK
 *
 *  e-mail:   o.liber@bolton.ac.uk
 *
 *
 *  Technical Contact:
 *
 *  Phillip Beauvoir
 *  e-mail:   p.beauvoir@dadabeatnik.com
 *
 *  Web:      http://www.reload.ac.uk
 *
 */

package uk.ac.reload.diva.htmlparser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/**
 * Class to read in a css file and find any local links
 * Tested with CSS files containing links that were created with
 *
 * 1. Macromedia Dreamweaver MX
 * 2. TopStyle Pro 3.10
 *
 * @author Paul Sharples
 * @version $Id: CSSParser.java,v 1.3 2007/07/15 20:27:52 phillipus Exp $
 */
public class CSSParser {
	
	public static char TAB = '\t';
	public static char CRETURN = '\r';
	public static char LINEFEED = '\n';
	public static char END_BRACKET = ')';
	public static char SEMI_COLON = ';';
	public static char DOUBLE_QUOTE = '"';
	public static char SINGLE_QUOTE = '\'';
	
	
	/**
	 *  A vector to hold any links that are found.
	 */
	private Vector _linksFound;
	
	/**
	 * Constructor - starts the parsing.
	 * @param thefile
	 */
	public CSSParser(File thefile) {
		_linksFound = new Vector();
		startit(thefile);
	}
	
	/**
	 * Accessor method to return any links that were found
	 * @return
	 */
	public String[] getLinks() {
		String[] resources = new String[_linksFound.size()];
		_linksFound.copyInto(resources);
		return resources;
	}
	
	
	/**
	 * Method to do the actual parsing.  Actually its just a simple character read
	 * which puts each chararcter into a buffer.  If a closing bracket s found
	 * then we look in the buffer for the existance of the "url{" string
	 * which checks for the existence of a substring "url(" which would denote
	 * a link of some sort.
	 * @param thefile
	 */
	private void startit(File thefile) {
		try {
			StringBuffer buf = new StringBuffer();
			FileReader text = new FileReader(thefile);
			int ainByte;
			do {
				ainByte = text.read();
				char inByte = (char) ainByte;
				if(ainByte != -1) {
					if(inByte != TAB && inByte != CRETURN && inByte != LINEFEED) {
						buf.append(inByte);
						if(inByte == END_BRACKET || inByte == SEMI_COLON) {
							findUrlLink(buf);
						}
					}
				}
			}
			while(ainByte != -1);
			text.close();
		}
		catch(IOException ex) {
			System.out.println("Error: Error parsing css file: " + ex);
		}
	}
	
	/**
	 * Look for instances of "url(" and get any local file references
	 */
	private void findUrlLink(StringBuffer buf){
		String astyle = buf.toString();
		String aLink, firstCharRemoved, lastCharRemoved;
		int endcount;
		int startcount = astyle.indexOf("url(");
		if(startcount != -1) {
			endcount = astyle.indexOf(END_BRACKET);
			aLink = astyle.substring(startcount + 4, endcount);
			
			if(aLink.indexOf(DOUBLE_QUOTE) != -1 || aLink.indexOf(SINGLE_QUOTE) != -1) {
				if(aLink.indexOf(DOUBLE_QUOTE) == 0 || aLink.indexOf(SINGLE_QUOTE) == 0) {
					firstCharRemoved = aLink.substring(1, aLink.length());
					aLink = firstCharRemoved;
				}
				if(aLink.indexOf(DOUBLE_QUOTE) == aLink.length()-1 || aLink.indexOf(SINGLE_QUOTE) == aLink.length()-1) {
					lastCharRemoved = aLink.substring(0, aLink.length()-1);
					aLink = lastCharRemoved;
				}
			}
			
			if(!(aLink.toLowerCase().startsWith("http:") || aLink.toLowerCase().startsWith("https:")
					|| aLink.toLowerCase().startsWith("mailto:"))) {
				_linksFound.add(aLink);
			}
			
			buf.delete(0, buf.length());
		}
	}
	
}
