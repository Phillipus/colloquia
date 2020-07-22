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

package uk.ac.reload.diva.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;


/**
 * HTMLUtils functionality 
 *
 * @author Phillip Beauvoir
 * @version $Id: HTMLUtils.java,v 1.3 2007/07/15 20:27:52 phillipus Exp $
 */
public final class HTMLUtils {

    /**
	 * @return The text value of an HTML tag in a file or null
	 */
	public static String getTagText(File htmlFile, String tagName) throws IOException {
		// Determine charset
		String encoding = getCharset(htmlFile);
		
		TagFinderParserCallback callback = new TagFinderParserCallback(tagName);
		
		// Use the charset
		Reader reader = new InputStreamReader(new FileInputStream(htmlFile), encoding);
		//Reader reader = new FileReader(_htmlFile);
		
		new ParserDelegator().parse(reader, callback, true);
	
		return callback.text;
	}
	
    /**
	 * @return The charset of an HTML file or "UTF-8" as a default if not found
	 */
	public static String getCharset(File htmlFile) throws IOException {
		String encoding;
		
		CharsetFinderParserCallback charsetCallback = new CharsetFinderParserCallback();
		Reader reader = new FileReader(htmlFile);
		new ParserDelegator().parse(reader, charsetCallback, true);
		encoding = charsetCallback.text;
		if(encoding == null) {
		    encoding = "UTF-8";
		}
		
		return encoding;
	}

	/**
	 * ParserCallback to find the text of a given tag
	 */
	public static class TagFinderParserCallback extends HTMLEditorKit.ParserCallback {
		boolean tag_found, done;
		String text;
		String _tagName;
		
		TagFinderParserCallback(String tagName) {
			_tagName = tagName;
		}
		
		public void handleStartTag(HTML.Tag tag, MutableAttributeSet attrSet, int pos) {
			if(!tag_found && tag.toString().equals(_tagName)) tag_found = true;
		}
		
		public void handleText(char[] data, int pos) {
			if(tag_found && !done) {
				text = new String(data);
				done = true;
			}
		}
	}
	
	/**
	 * ParserCallback to find the charset of a given HTML File
	 */
	public static class CharsetFinderParserCallback extends HTMLEditorKit.ParserCallback {
		boolean done;
		String text;
		
		CharsetFinderParserCallback() {}
		
		public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attrSet, int pos) {
		    if(attrSet != null && !done) {
		        Object o = attrSet.getAttribute(HTML.Attribute.CONTENT);
		        if(o != null) {
		            String s = o.toString();
		            int index = s.indexOf("charset");
		            if(index != -1) {
		                index = s.indexOf("=", index);
		                if(index != -1) {
		                    try {
		                        text = s.substring(index + 1);
		                    }
		                    catch(IndexOutOfBoundsException ex) {
		                        // Ignore
		                    }
			                done = true;
		                }
		            }
		        }
		    }
		}
	}
}//  end
