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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * A class used to describe each kind of file type
 * (name, icon to use, etc), by its extension.
 *
 * @author Paul Sharples
 * @version $Id: FileType.java,v 1.3 2007/07/15 20:27:53 phillipus Exp $
 */
public final class FileType {
	
	// Constant Lists of various file extensions
	// Done this way, so we can easily come back and add
	// new ones in the future.
    public static final List XML_EXTENSIONS;
    
	public static final List WEB_PAGE_EXTENSIONS;
	public static final List ASP_EXTENSIONS;
	public static final List JSP_EXTENSIONS;
	public static final List COLDFUSION_EXTENSIONS;
	public static final List PHP_EXTENSIONS;
	public static final List SERVERTECHNOLOGY_EXTENSIONS;
	public static final List IMAGEFILE_EXTENSIONS;
	public static final List STYLESHEET_EXTENSIONS;
	public static final List JAVAAPPLET_EXTENSIONS;
	public static final List MSWORD_EXTENSIONS;
	public static final List MSEXCEL_EXTENSIONS;
	public static final List MSACCESS_EXTENSIONS;
	public static final List MSPOWERPOINT_EXTENSIONS;
	public static final List TEXT_EXTENSIONS;
	public static final List JAVASCRIPT_EXTENSIONS;
	public static final List SHOCKWAVE_EXTENSIONS;
	public static final List FLASH_EXTENSIONS;
	public static final List AUDIOVIDEOMEDIA_EXTENSIONS;
	public static final List PDF_EXTENSIONS;
	public static final List ARCHIVE_EXTENSIONS;
	public static final List EXTENSIONS_TO_PARSE;
	public static final List ALL_EXTENSIONS;
	
	// Our HTML parser needs to know various constants, so they are
	// here to keep all constants together...
	public static final List JAVASCRIPTEVENTS;
	
	
	/*
	 ***************************************************************************
	 * We need to define which file extensions are associated with various
	 * file file types i.e MSword is .doc, Macromedia Flash is .swf
	 * We use the lists below to maintain our our constants (above) and
	 * also we use these lists to concatenate and make larger lists
	 * i.e. EXTENSIONS_TO_PARSE contains several other lists.
	 ***************************************************************************
	 */
	static{
		List _audio = new ArrayList();
		// Audio and Video File extensions
		_audio.add(".aif");
		_audio.add(".aiff");
		_audio.add(".au");
		_audio.add(".mov");
		_audio.add(".mp2");
		_audio.add(".mp3");
		_audio.add(".mpg");
		_audio.add(".mpeg");
		_audio.add(".ra");
		_audio.add(".ram");
		_audio.add(".wav");
		AUDIOVIDEOMEDIA_EXTENSIONS = Collections.unmodifiableList(_audio);
		
		List _shockwave = new ArrayList();
		// Shockwave/Director File extensions
		_shockwave.add(".dcr");
		SHOCKWAVE_EXTENSIONS = Collections.unmodifiableList(_shockwave);
		
		List _archivers = new ArrayList();
		// Zips and other archive file types
		// probably more than this, wiil have to add to it...
		_archivers.add(".zip");
		_archivers.add(".ace");
		_archivers.add(".rar");
		_archivers.add(".tar");
		ARCHIVE_EXTENSIONS = Collections.unmodifiableList(_archivers);
		
		List _flash = new ArrayList();
		// Flash File extensions
		_flash.add(".swf");
		FLASH_EXTENSIONS = Collections.unmodifiableList(_flash);
		
		List _pdf = new ArrayList();
		// Adobe Acrobat File extensions
		_pdf.add(".pdf");
		PDF_EXTENSIONS = Collections.unmodifiableList(_pdf);
		
		List _javascript = new ArrayList();
		// Javascript File extensions
		_javascript.add(".js");
		JAVASCRIPT_EXTENSIONS = Collections.unmodifiableList(_javascript);
		
		List _text = new ArrayList();
		// Text File extensions
		_text.add(".txt");
		TEXT_EXTENSIONS = Collections.unmodifiableList(_text);
		
		List _mspowerpoint = new ArrayList();
		// Microsoft Powerpoint File extensions
		_mspowerpoint.add(".ppt");
		_mspowerpoint.add(".pps");
		MSPOWERPOINT_EXTENSIONS = Collections.unmodifiableList(_mspowerpoint);
		
		List _msaccess = new ArrayList();
		// Microsoft Access File extensions
		_msaccess.add(".mdb");
		MSACCESS_EXTENSIONS = Collections.unmodifiableList(_msaccess);
		
		List _msexcel = new ArrayList();
		// Microsoft Excel File extensions
		_msexcel.add(".xls");
		_msexcel.add(".csv");
		MSEXCEL_EXTENSIONS = Collections.unmodifiableList(_msexcel);
		
		List _msword = new ArrayList();
		// Microsoft Word File extensions
		_msword.add(".doc");
		_msword.add(".dot");
		_msword.add(".rtf");
		MSWORD_EXTENSIONS = Collections.unmodifiableList(_msword);
		
		List _applets = new ArrayList();
		// Java applet File extensions
		_applets.add(".class");
		_applets.add(".java");
		JAVAAPPLET_EXTENSIONS = Collections.unmodifiableList(_applets);
		
		List _stylesheets = new ArrayList();
		// File extensions that are stylesheets
		_stylesheets.add(".css");
		STYLESHEET_EXTENSIONS = Collections.unmodifiableList(_stylesheets);
		
		List _webimages = new ArrayList();
		// File extensions that are images
		_webimages.add(".gif");
		_webimages.add(".jpg");
		_webimages.add(".jpeg");
		_webimages.add(".png");
		IMAGEFILE_EXTENSIONS = Collections.unmodifiableList(_webimages);
		
		List _webpages = new ArrayList();
		// File extensions that are web pages
		_webpages.add(".htm");
		_webpages.add(".html");
		_webpages.add(".shtml");
		_webpages.add(".shtm");
		WEB_PAGE_EXTENSIONS = Collections.unmodifiableList(_webpages);
		
		List _xmlpages = new ArrayList();
		// File extensions that are xml
		_webpages.add(".xml");
		_webpages.add(".xslt");		
		XML_EXTENSIONS = Collections.unmodifiableList(_xmlpages);
		
		List _asp = new ArrayList();
		// ASP file extensions
		_asp.add(".asp");
		_asp.add(".aspx");
		_asp.add(".asa");
		ASP_EXTENSIONS = Collections.unmodifiableList(_asp);
		
		List _jsp = new ArrayList();
		// JSP file extensions
		_jsp.add(".jsp");
		JSP_EXTENSIONS = Collections.unmodifiableList(_jsp);
		
		List _php = new ArrayList();
		// PHP file extensions
		_php.add(".php");
		_php.add(".php3");
		_php.add(".php4");
		PHP_EXTENSIONS = Collections.unmodifiableList(_php);
		
		List _coldfusion = new ArrayList();
		// Cold fusion file extensions
		_coldfusion.add(".cfm");
		COLDFUSION_EXTENSIONS = Collections.unmodifiableList(_coldfusion);
		
		List _servertechnologies = new ArrayList();
		// Server technology file extensions
		_servertechnologies.addAll(ASP_EXTENSIONS);
		_servertechnologies.addAll(JSP_EXTENSIONS);
		_servertechnologies.addAll(COLDFUSION_EXTENSIONS);
		_servertechnologies.addAll(PHP_EXTENSIONS);
		SERVERTECHNOLOGY_EXTENSIONS = Collections.unmodifiableList(_servertechnologies);
		
		List _parseextensions = new ArrayList();
		// Add various other lists to create a list of
		// extensions that we may want to parse
		_parseextensions.addAll(XML_EXTENSIONS);
		_parseextensions.addAll(WEB_PAGE_EXTENSIONS);
		_parseextensions.addAll(SERVERTECHNOLOGY_EXTENSIONS);
		_parseextensions.addAll(STYLESHEET_EXTENSIONS);
		_parseextensions.addAll(JAVASCRIPT_EXTENSIONS);
		EXTENSIONS_TO_PARSE = Collections.unmodifiableList(_parseextensions);
		
		List _allextensions = new ArrayList();
		// Add various other lists to create a list of
		// all extensions
		_allextensions.addAll(ARCHIVE_EXTENSIONS);
		_allextensions.addAll(AUDIOVIDEOMEDIA_EXTENSIONS);
		_allextensions.addAll(SHOCKWAVE_EXTENSIONS);
		_allextensions.addAll(FLASH_EXTENSIONS);
		_allextensions.addAll(PDF_EXTENSIONS);
		_allextensions.addAll(TEXT_EXTENSIONS);
		_allextensions.addAll(MSWORD_EXTENSIONS);
		_allextensions.addAll(MSEXCEL_EXTENSIONS);
		_allextensions.addAll(MSACCESS_EXTENSIONS);
		_allextensions.addAll(MSPOWERPOINT_EXTENSIONS);
		_allextensions.addAll(JAVAAPPLET_EXTENSIONS);
		_allextensions.addAll(IMAGEFILE_EXTENSIONS);
		_allextensions.addAll(EXTENSIONS_TO_PARSE);
		ALL_EXTENSIONS = Collections.unmodifiableList(_allextensions);
		
		
		/*
		 ***********************************************************************
		 * Here we have some helper lists for our HTML parser
		 ***********************************************************************
		 */
		
		// Support all version 4 javascript calls
		List _javascriptEvents = new ArrayList();
		_javascriptEvents.add("onload");
		_javascriptEvents.add("onclick");
		_javascriptEvents.add("ondblclick");
		_javascriptEvents.add("onkeydown");
		_javascriptEvents.add("onkeypress");
		_javascriptEvents.add("onkeyup");
		_javascriptEvents.add("onmousedown");
		_javascriptEvents.add("onmouseover");
		_javascriptEvents.add("onmouseout");
		JAVASCRIPTEVENTS = Collections.unmodifiableList(_javascriptEvents);
		/*
		 ***********************************************************************
		 */
	}
	
	/**
	 * @return true if aFile is a suitable file to parse
	 */
	public static boolean isParseableFile(File aFile) {
		String fileName = aFile.toString().toLowerCase();
	    Iterator listElement = EXTENSIONS_TO_PARSE.iterator();
		while(listElement.hasNext()) {
			if(fileName.endsWith(listElement.next().toString())) {
				return true;
			}
		}
		return false;
	}
	
}
