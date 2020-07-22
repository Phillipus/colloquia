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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

/**
 * Class used to parse a web page and find all dependant links contained
 * within it.  Additionally used to figure out how & where to copy files
 * when a user wishes to import them
 *
 * @author Paul Sharples
 * @version $Id: HTMLParserImporter.java,v 1.3 2007/07/15 20:27:52 phillipus Exp $
 */
public class HTMLParserImporter {
	
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
	 * The Target Folder
	 */
	private File _targetFolder;
	
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
	 * Our main data structure to hold source/destination paths
	 */
	private Hashtable _htmlResourcesTable;
	
	/**
	 * Hold the starting point for the webpage(s)
	 */
	private File _startPath;
	
	/**
	 * Constructor
	 * 
	 * @param htmlFile The web page to be parsed 
	 * @param targetFolder The target folder to copy all files to
	 */
	public HTMLParserImporter(File htmlFile, File targetFolder) {
		_htmlFile = htmlFile;
		_targetFolder = targetFolder;
		_startPath = htmlFile;
		
		_htmlResourcesTable = new Hashtable();
		
		// create the first destination file using the rootFolder and appending the source filename
		File destination = new File(_targetFolder, htmlFile.getName());
		_htmlResourcesTable.put(htmlFile, destination);
		
		// pass the info over to the parsing function
		parseTheHtml(htmlFile, _targetFolder.getAbsolutePath());
	}
	
	
	/**
	 * @return - A Hashtable of all source(key) and destination(value) files
	 */
	public Hashtable getLinks(){
		return _htmlResourcesTable;
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
	 * ParsetheHtml() - utilises HTMLEditorKit.ParserCallback which reports on
	 * either a simple tag <img> or starttag <a href="something">.  Once the
	 * parser has reported it has found a tag, it is passed to parseAttributes()
	 * which then looks for attributes that may contain links to other resources
	 * Either a string representation of the link is returned or null. This result
	 * is then passed to buildTheResource() which checks the file exists and adds
	 * it to a local hashmap of pages.  parseTheHtml() is recursively called from
	 * buildTheResource() if it finds a link that may contain other links
	 * i.e. another web page...
	 * @param HtmlFile
	 * @param destPath
	 */
	protected void parseTheHtml(final File HtmlFile, String destPath){
		final String sourcePath = HtmlFile.getParent() + File.separatorChar;
		final String packagePath = destPath + File.separatorChar;
		
		if(HtmlFile.getName().toLowerCase().endsWith(".css")){
			CSSParser aCssFile = new CSSParser(HtmlFile);
			String[] theCssLinks = aCssFile.getLinks();
			for(int i = 0; i < theCssLinks.length; i++) {
				buildTheResource(theCssLinks[i], sourcePath, packagePath, HtmlFile);
			}
			return;
		}
		
		HTMLEditorKit.ParserCallback callback = new HTMLEditorKit.ParserCallback (){
			// Instead of having almost identical code for both handleStartTag and
			// handleSimpleTag methods, put the code into subroutines.
			public void handleStartTag(HTML.Tag tag, MutableAttributeSet attrSet, int pos) {
				buildTheResource(parseAttributes(tag, attrSet, pos, sourcePath, packagePath, HtmlFile),sourcePath, packagePath, HtmlFile);
			}
			public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attrSet, int pos) {
				buildTheResource(parseAttributes(tag, attrSet, pos, sourcePath, packagePath, HtmlFile),sourcePath, packagePath, HtmlFile);
			}
		};
		
		try {
			Reader reader = new FileReader(HtmlFile.getAbsolutePath());
			new ParserDelegator().parse(reader, callback, true);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}// end of parseTheHTML()
	
	
	/**
	 * dealWithJavascriptEvent - is passed a string which may contain a javascript
	 * event (from parseAttributes()).  The string is split by a single quote into
	 * an array.  Each element is then checked against _webFiles (an array of file
	 * types) to see f it references any other files.  If a filename is found, it
	 * is passed to buildtheResource() which tries to establish whether or not the
	 * file exists on disk and if the hashmap does not already contain the file
	 * reference, it adds it
	 * @param aLink
	 * @param sourcePath
	 * @param packagePath
	 */
	protected void dealWithJavascriptEvent(String aLink, String sourcePath, String packagePath, File pageFrom) {
		// split the string at the ' character and examine
		String[] javascriptBits = aLink.split("'");
		// cycle thru all of the strings that were split
		for(int scriptArrayIndex=0;scriptArrayIndex < javascriptBits.length; scriptArrayIndex++) {
			// cycle thru all of the file types defined in _webFiles to see if we can find a link
			Iterator listElement = FileType.ALL_EXTENSIONS.iterator();
			while(listElement.hasNext()) {
				// if we find that link...
				if(javascriptBits[scriptArrayIndex].toLowerCase().endsWith(listElement.next().toString())) {
					buildTheResource(javascriptBits[scriptArrayIndex], sourcePath, packagePath, pageFrom);
				}
			}
		}
	}
	
	/**
	 * parseAttributes() - parses a HTML tag for any attributes that may contain
	 * references to local files.  If it finds any, then it returns the string
	 * reference to that file, otherwise null.
	 * @param tag
	 * @param attrSet
	 * @param pos
	 * @param sourcePath
	 * @param packagePath
	 * @return
	 */
	protected String parseAttributes(HTML.Tag tag, MutableAttributeSet attrSet, int pos, String sourcePath, String packagePath, File pageFrom){
		String returnString = null;
		
		for(Enumeration attributes = attrSet.getAttributeNames(); attributes.hasMoreElements() ;) {
			Object o = attributes.nextElement();
			// deal with any javascript calls first
			String attributename = o.toString();
			Iterator listElement;
			listElement = FileType.JAVASCRIPTEVENTS.iterator();
			while (listElement.hasNext()) {
				if (attributename.toLowerCase().compareTo(listElement.next().toString()) == 0) {
					String attributevalue = (String) attrSet.getAttribute(o);
					dealWithJavascriptEvent(attributevalue, sourcePath, packagePath, pageFrom);
				}
			}
			// next lets look for specific attributes that may contain file references
			for (int attributeArrayIndex=0; attributeArrayIndex < htmlAttributesToParse.length; attributeArrayIndex++){
				if (o == htmlAttributesToParse[attributeArrayIndex]){
					String aLink = (String) attrSet.getAttribute(o);
					String aLinkLowerCase = aLink.toLowerCase();
					if (!(aLinkLowerCase.startsWith("http:") ||aLinkLowerCase.startsWith("https:")
							|| aLinkLowerCase.startsWith("mailto:"))) {
						if (aLinkLowerCase.startsWith("javascript:") ||
								aLinkLowerCase.startsWith("#")) {
							dealWithJavascriptEvent(aLink, sourcePath, packagePath, pageFrom);
							returnString = null;
						}
						else if (aLinkLowerCase.startsWith("file:")) {
							_absoluteLocalLinkFound = true;
							System.out.println("Warning: The link '" + aLink.toString()
									+ "' in the file '" + pageFrom.getAbsolutePath()
									+ "' is an absolute link.");
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
	
	/**
	 * Routine to to check that the source file is indeed a valid file on the
	 * filesystem and then add it to our hashtable if it isnt there already,
	 * along with the destination path to copy it to.
	 *
	 * @param localLink
	 * @param sourcePath
	 * @param packagePath
	 */
	protected void buildTheResource(String localLink, String sourcePath, String packagePath, File pageFrom) {
		File destinationCanonical = null;
		
		if(localLink != null) {
			try {
				File original = new File(URLDecoder.decode(sourcePath + localLink, "UTF-8"));
				File destination = new File(URLDecoder.decode(packagePath + localLink, "UTF-8"));
				if(original.exists()) {
					try {
						// we need to get the canonical path (without ../../) and add that to the table
						File originalCanonical = new File(original.getCanonicalPath());
						destinationCanonical = new File(destination.getCanonicalPath());
						
						// Make sure that the link is not referencing a file or folder higher
						// than the target folder itself. If the new "destination link" starts
						// with the target folder, then we are okay, otherwise there's a problem
						if(destinationCanonical.getAbsolutePath().toLowerCase().startsWith(_targetFolder.getAbsolutePath().toLowerCase())){
							// Do we have it already?
							boolean gotit = _htmlResourcesTable.containsKey(originalCanonical);
							// No, first time creation, store in table
							if(gotit == false) {
								if(!originalCanonical.isDirectory()) {
									_htmlResourcesTable.put(originalCanonical, destinationCanonical);
								}
								
								Iterator listElement = FileType.EXTENSIONS_TO_PARSE.iterator();
								while(listElement.hasNext()) {
									if(localLink.toLowerCase().endsWith(listElement.next().toString())) {
										_firstPage = false;
										parseTheHtml(original, destinationCanonical.getParent());
									}
								}
							}
						}
						else {
							_higherLocalLinkFound = true;
							// TO DO - This is massively slow when this is output to the StatusWindow
							// Better to save these up and present at the end in one go
							//System.out.println("Warning: The link '" + destinationCanonical.getPath()
							//		+ "' in the file '" + pageFrom.getPath()
							//		+ "' is higher than the package folder, so it cannot be copied across.");
						}
					}
					catch(IOException e) {
						System.out.println("Error: A problem occured during parsing '" + e.toString() + "'");
					}
				}
			}
			catch(UnsupportedEncodingException e){
				System.out.println("Error: Encoding exception in HTML document '" + e.toString() + "'");
			}
		}
	}
	
	
}//  end
