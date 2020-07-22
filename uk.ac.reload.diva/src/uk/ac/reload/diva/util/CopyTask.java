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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This class copies resources from a jar file in your distribution
 * to a destination of your choice. It does a one-way sync, ensuring that
 * every file in the archive exists in the target, while leaving other files
 * and folders alone. You can choose whether to overwrite or leave alone matching
 * files. Pre-existing files which match the names of folders in the archive are
 * renamed, adding a ~ suffix.
 *
 *
 * @author Julian Wood
 * @version $Id: CopyTask.java,v 1.3 2007/07/15 20:27:52 phillipus Exp $
 */
public class CopyTask {

    private File target;
    private ZipEntry zipEntry;
    private InputStream in;
    private FileOutputStream out;
    private byte[] data;
    private int len;
    private boolean exists;

    private File destinationFolder;
    private URL resourceUrl;


    /**
     * Constructor
     * @param resource the path to the jar file (local) or the path of a resource within the jar (webstart)
     * @param destinationFolder File representing the destination folder where the resources are extracted;
     *        the dest is created if required
     */
    public CopyTask(String resource, File destinationFolder) throws IOException {
        this.destinationFolder = destinationFolder;

        // resources can be in one of two places:
        // if in the working dir (ie $build/reload.jar)
        // webstart/jar in classpath - specify something inside this jar
        ClassLoader classLoader = this.getClass().getClassLoader();
        resourceUrl = classLoader.getResource(resource);
        if(resourceUrl == null) {
        	throw new IOException("Resource not found: " + resource);
        } 
    }

    /**
     * Backs up the destinationFolder to a folder in the same parent directory,
     * suffixed by as many appended '~'s as required to make it unique
     * @throws IOException
     */
    public void backup() throws IOException {
        if(!destinationFolder.exists()) {
        	return;
        } 

        StringBuffer backupName = new StringBuffer(destinationFolder.getName());
        backupName.append('~');
        File parentFile = destinationFolder.getParentFile();
        File backupFile = new File(parentFile, backupName.toString());
        while (backupFile.exists()) {
            backupName.append('~');
            backupFile = new File(parentFile, backupName.toString());
        }
        FileUtils.copyFolder(destinationFolder, backupFile);
    }


    /**
     * expand the resource archive into the destination folder
     * @param overwrite
     * @throws IOException
     */
    public void execute(boolean overwrite) throws IOException {
        // we have a jar file which needs to be extracted to the home directory
        // creating de novo if necessary
        // overwriting files
        // creating necessary dirs
        // leaving existing files not in the jar alone

        // need ant to build this support jar from source

        String file = resourceUrl.getFile();
        int start = file.indexOf(':') + 1;
        int end = file.lastIndexOf('!');

        File supportJar = new File(file.substring(start, (end == -1) ? file.length() : end).replaceAll("%20", " "));
        ZipFile zipFile = new ZipFile(supportJar, ZipFile.OPEN_READ);
        Enumeration e = zipFile.entries();
        while(e.hasMoreElements()) {
            zipEntry = (ZipEntry) e.nextElement();
            target = new File(destinationFolder, zipEntry.getName());

            exists = target.exists();

            // is a problem if the user made a file with the same name of a dir we're trying to create
            if(zipEntry.getSize() == 0) { // folder doesn't exist
                if (!target.mkdirs()) {
                    // already exists (or some other problem but that will throw)
                    // check to make sure there isn't a file of the same name
                    if (target.isFile()) {
                        StringBuffer newName = new StringBuffer(target.getName());
                        newName.append('~');
                        File parentFile = target.getParentFile();
                        File newTarget = new File(parentFile, newName.toString());
                        while (newTarget.exists()) {
                            newName.append('~');
                            newTarget = new File(parentFile, newName.toString());
                        }
                        target.renameTo(newTarget);
                    }
                }
                continue;
            } else {
                // sometimes we have a file and it's parent folder isn't made
                File parentFile = target.getParentFile();
                parentFile.mkdirs();
            }

            if(overwrite && exists) {
                // any way to check to see if the files are the same instead?
//                System.out.println("zipEntry.getSize() = " + zipEntry.getSize());
//                System.out.println("zipEntry.getTime() = " + zipEntry.getTime());
//                System.out.println("target.lastModified() = " + target.lastModified());
//                System.out.println("target.length() = " + target.length());
                target.delete();
                exists = false;
            }

            if(!exists) {
                in = zipFile.getInputStream(zipEntry);
                out = new FileOutputStream(target);
                data = new byte[10240];
                while(true) {
                    len = in.read(data);
                    if(len < 1) {
                    	break;
                    } 
                    out.write(data, 0, len);
                }
                in.close();
                out.close();
            }
        }
    }
}
