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
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

/**
 * Class used to parse a web page and find all dependent links contained
 * within it relative to a given root folder.
 *
 * @author Paul Sharples
 * @version $Id: HTMLParser.java,v 1.3 2007/07/15 20:27:53 phillipus Exp $
 */
public class HTMLParser {
	
	/**
	 * An array of the HTML attributes that may contain references to files
	 * HREF is used in hyperlinks
	 * BACKGROUND is used in tables, body, etc
	 * SRC is used in <object> and <embed>, etc
	 * CODE is used for Applets
	 * ACTION is used for submitting forms
	 * 
	 * This may need to be moved into FileType????
	 */
	public static HTML.Attribute[] htmlAttributesToParse = {
	        HTML.Attribute.HREF, HTML.Attribute.BACKGROUND,
			HTML.Attribute.SRC, HTML.Attribute.CODE, HTML.Attribute.ACTION
	};

	/**
	 * The File to Parse
	 */
	private File _htmlFile;
	
	/**
	 * We need to check the first page and see if it refers to local links
	 * that are higher up in the folder structure than itself. _firstpage
	 * will be used to flag up the first time the code is asked to parse a page.
	 */
	private boolean _firstPage = true;
	
	/**
	 * The Root Folder
	 */
	private File _rootFolder;
	
	/**
	 * We want to know if a user has imported a page which has a local link
	 * higher up the directory tree structure than itself
	 */
	private boolean _higherLocalLinkFound;
	
	/**
	 * We want to know if a user has put an absolute local link in a page
	 */
	private boolean _absoluteLocalLinkFound;
	
	/**
	 * All Links
	 */
	private Vector _allLinks;
	
	/**
	 * Constructor
	 * 
	 * @param htmlFile The web page to be parsed 
	 * @param rootFolder Relative folder
	 */
	public HTMLParser(File htmlFile, File rootFolder) {
		_htmlFile = htmlFile;
		_rootFolder = rootFolder;
		
		_allLinks = new Vector();
		parseTheHtml(htmlFile);
	}
	
	/**
	 * @return All the File links found
	 */
	public File[] getAllLinks(){
		File[] files = new File[_allLinks.size()];
		_allLinks.copyInto(files);
		Arrays.sort(files);
		return files;
	}
	
	/**
	 * Accessor method to see if any FULL absolute local links were found in the page(s)
	 * i.e. 'file:///c:/dreamweaversite/acmeweb/index.htm'
	 * @return true/false
	 */
	public boolean isAbsoluteLocalLinkFound(){
		return _absoluteLocalLinkFound;
	}
	
	/**
	 * Accessor method to see if any local links were found in the first page
	 * that are higher in the directory tree than itself.
	 * @return true/false
	 */
	public boolean isHigherLocalLinkFound() {
		return _higherLocalLinkFound;
	}
	
	/**
	 * Used for second pass to find all links
	 * @param htmlFile
	 */
	protected void parseTheHtml(File htmlFile){
		final String sourcePath = htmlFile.getParent() + File.separatorChar;
		
		if(htmlFile.getName().toLowerCase().endsWith(".css")) {
			CSSParser aCssFile = new CSSParser(htmlFile);
			String[] theCssLinks = aCssFile.getLinks();
			for(int i = 0; i < theCssLinks.length; i++) {
				buildTheResource(theCssLinks[i], sourcePath);
			}
			return;
		}
		
		HTMLEditorKit.ParserCallback callback = new HTMLEditorKit.ParserCallback (){
			// Instead of having almost identical code for both handleStartTag and
			// handleSimpleTag methods, put the code into subroutines.
			public void handleStartTag(HTML.Tag tag, MutableAttributeSet attrSet, int pos) {
				buildTheResource(parseAttributes(tag, attrSet, pos, sourcePath), sourcePath);
			}
			public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attrSet, int pos) {
				buildTheResource(parseAttributes(tag, attrSet, pos, sourcePath), sourcePath);
			}
		};
		
		try {
			Reader reader = new FileReader(htmlFile);
			new ParserDelegator().parse(reader, callback, true);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}// end of parseTheHTML()
	
	
	protected String parseAttributes(HTML.Tag tag, MutableAttributeSet attrSet, int pos, String sourcePath){
		String returnString=null;
		for (Enumeration attributes = attrSet.getAttributeNames(); attributes.hasMoreElements() ;) {
			Object o = attributes.nextElement();
			// deal with any javascript calls first
			String attributename = o.toString();
			Iterator listElement;
			listElement = FileType.JAVASCRIPTEVENTS.iterator();
			while (listElement.hasNext()) {
				if (attributename.toLowerCase().compareTo(listElement.next().toString()) == 0) {
					String attributevalue = (String) attrSet.getAttribute(o);
					dealWithJavascriptEvent(attributevalue, sourcePath);
				}
			}
			// next lets look for specific attributes that may contain file references
			for(int attributeArrayIndex=0; attributeArrayIndex < htmlAttributesToParse.length; attributeArrayIndex++){
				if(o == htmlAttributesToParse[attributeArrayIndex]){
					String aLink = (String) attrSet.getAttribute(o);
					String aLinkLowerCase = aLink.toLowerCase();
					if(!(aLinkLowerCase.startsWith("http:") || aLinkLowerCase.startsWith("https:")
							|| aLinkLowerCase.startsWith("mailto:"))) {
						if(aLinkLowerCase.startsWith("javascript:") ||
								aLinkLowerCase.startsWith("#")) {
							dealWithJavascriptEvent(aLink, sourcePath);
							returnString = null;
						}
						else if(aLinkLowerCase.startsWith("file:")) {
							_absoluteLocalLinkFound = true;
							returnString = null;
						}
						else {
							returnString = aLink;
						}
					}
				}
			}
		}
		return returnString;
	}
	
	
	protected void dealWithJavascriptEvent(String aLink, String sourcePath) {
		// split the string at the ' character and examine
		String[] javascriptBits = aLink.split("'");
		// cycle thru all of the strings that were split
		for(int scriptArrayIndex=0; scriptArrayIndex < javascriptBits.length; scriptArrayIndex++) {
			// cycle thru all of the file types defined in _webFiles to see if we can find a link
			Iterator listElement = FileType.ALL_EXTENSIONS.iterator();
			while (listElement.hasNext()) {
				// if we find that link...
				if(javascriptBits[scriptArrayIndex].toLowerCase().endsWith(listElement.next().toString())) {
					buildTheResource(javascriptBits[scriptArrayIndex], sourcePath);
				}
			}
		}
	}
	
	
	protected void buildTheResource(String localLink, String sourcePath){
		if(localLink != null) {
			try {
				File original = new File(URLDecoder.decode(sourcePath + localLink, "UTF-8"));
				if(original.exists()) {
					try {
						// we need to get the canonical path (without ../../) and add that to the table
						File originalCanonical = new File(original.getCanonicalPath());
						
						// Make sure that the link is not referencing a file or folder higher
						// than the root folder itself. If the new "destination link" starts
						// with the root folder, then we are okay, otherwise there's a problem
						if(originalCanonical.getAbsolutePath().toLowerCase().startsWith(_rootFolder.getAbsolutePath().toLowerCase())) {
							// Do we have it already?
							boolean gotit = _allLinks.contains(originalCanonical);
							// No, first time creation, store in table
							if(gotit == false) {
								if(!originalCanonical.isDirectory()) _allLinks.add(originalCanonical);
								Iterator listElement = FileType.EXTENSIONS_TO_PARSE.iterator();
								while(listElement.hasNext()) {
									if(localLink.toLowerCase().endsWith(listElement.next().toString())) {
										_firstPage = false;
										parseTheHtml(original);
									}
								}
							}
						}
						else {
							_higherLocalLinkFound = true;
							// TO DO - This is massively slow when this is output to the StatusWindow
							// Better to save these up and present at the end in one go
							//System.out.println("Warning: The link '" + originalCanonical.getPath()
							//		+ "' in the file '" + _rootFolder.getPath()
							//		+ "' is higher than the project folder, so it cannot be copied across.");
						}
					}
					catch(IOException e) {
						System.out.println("A problem occured when trying to find all links in " + localLink +
						" This is probably due to the page having Absolute links to local files on your computer.  Please amend this page\n");
					}
				}
			}
			catch(UnsupportedEncodingException ex) {
				System.out.println("Error: Encoding exception in HTML document '" + ex.toString() + "'");
			}
		}
	}
	
}//  end
