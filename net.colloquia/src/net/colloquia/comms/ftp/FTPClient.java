/**
 *
 *  Java FTP client library.
 *
 *  Copyright (C) 2000  Enterprise Distributed Technologies Ltd
 *
 *  www.enterprisedt.com
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Bug fixes, suggestions and comments should be sent to:
 *
 *  bruceb@cryptsoft.com
 *
 *  or by snail mail to:
 *
 *  Bruce P. Blackshaw
 *  53 Wakehurst Road
 *  London SW11 6DB
 *  United Kingdom
 *
 *  Change Log:
 *
 *	  $Log: FTPClient.java,v $
 *	  Revision 1.1  2006/04/19 14:17:51  phillipus
 *	  First commit
 *	
 */

package net.colloquia.comms.ftp;

import java.io.*;
import java.net.*;
import java.util.*;

import net.colloquia.*;

/**
 *  Supports client-side FTP. Most common
 *  FTP operations are present in this class.
 *  Lots to do, but works ok.
 *
 *  @author		Bruce Blackshaw
 *	@version	$Revision: 1.1 $
 *
 */
public class FTPClient {

	/**
	 *  Socket responsible for controlling
	 *  the connection
	 */
	private FTPControlSocket control = null;

	/**
	 *  Socket responsible for transferring
	 *  the data
	 */
	private Socket data = null;


	/**
	 *  Record of the transfer type
	 */
	 private FTPTransferType transferType = null;


	/**
	 *  Constructor. Creates the control
	 *  socket
	 *
	 *  @param   remoteHost  the remote hostname
	 */
	 public FTPClient(String remoteHost)
		throws IOException, FTPException {

		control = new FTPControlSocket(remoteHost);
	}

	/**
	 *  Constructor. Creates the control
	 *  socket
	 *
	 *  @param   remoteAddr  the address of the
	 *                       remote host
	 */
	public FTPClient(InetAddress remoteAddr)
		throws IOException, FTPException {

		control = new FTPControlSocket(remoteAddr);
	}

	/**
	 *  Login into an account on the FTP server
	 *
	 *  @param   user       user name
	 *  @param   password   user's password
	 */
	public void login(String user, String password)
		throws IOException, FTPException {

		String response = control.sendCommand("USER " + user);
		control.validateReply(response, "331");
		response = control.sendCommand("PASS " + password);
		control.validateReply(response, "230");
	}


	/**
	 *  Put a local file onto the FTP server. It
	 *  is placed in the current directory.
	 *
	 *  @param  localPath   path of the local file
	 *  @param  remoteFile  name of remote file in
	 *                      current directory
	 */
	public void put(String localPath, String remoteFile)
		throws IOException, FTPException {

		// get an output stream
		data = control.createDataSocket();
		DataOutputStream out = new DataOutputStream(data.getOutputStream());

		// send the command to store
		String reply = control.sendCommand("STOR " + remoteFile);

		// Can get a 125 or a 150
		control.validateReply(reply, "1");

		// open input stream to read source file
		FileInputStream input = new FileInputStream(localPath);
		byte[] buf = new byte[512];

		// read a chunk at a time and write to the data socket
		int count = 0;
		while ((count = input.read(buf)) > 0) {
			out.write(buf, 0, count);
            boolean cancel = fireBytesPut(count);  // PB added
            if(cancel) break;                      // PB added
		}
		input.close();

		// flush and clean up
		out.flush();
		out.close();

		// check the control response
		reply = control.readReply();
		control.validateReply(reply, "226");
	}


	/**
	 *  Put data onto the FTP server. It
	 *  is placed in the current directory.
	 *
	 *  @param  data        array of bytes
	 *  @param  remoteFile  name of remote file in
	 *                      current directory
	 */
	public void put(byte[] bytes, String remoteFile)
		throws IOException, FTPException {

		// get an output stream
		data = control.createDataSocket();
		DataOutputStream out = new DataOutputStream(data.getOutputStream());

		// send the command to store
		String reply = control.sendCommand("STOR " + remoteFile);

		// Can get a 125 or a 150
		control.validateReply(reply, "1");

		// write array
		out.write(bytes, 0, bytes.length);

		// flush and clean up
		out.flush();
		out.close();

		// check the control response
		reply = control.readReply();
		control.validateReply(reply, "226");
	}


	/**
	 *  Get data from the FTP server. Uses the currently
	 *  set transfer mode.
	 *
	 *  @param  localPath   local file to put data in
	 *  @param  remoteFile  name of remote file in
	 *                      current directory
	 */
	public void get(String localPath, String remoteFile)
		throws IOException, FTPException {

		// get an input stream to read data from
		data = control.createDataSocket();
		DataInputStream in = new DataInputStream(data.getInputStream());
		BufferedInputStream bIn = new BufferedInputStream(in);

		// send the retrieve command
		String reply = control.sendCommand("RETR " + remoteFile);
		control.validateReply(reply, "150");

		// do the retrieving
		int chunksize = 4096;
		byte [] chunk = new byte[chunksize];
		int count;

		// create the buffered output stream for writing the file
		BufferedOutputStream out = null;
		out = new BufferedOutputStream(new FileOutputStream(localPath, false));

		// read from socket & write to file in chunks
		while ((count = bIn.read(chunk, 0, chunksize)) >= 0) {
			out.write(chunk, 0, count);
            boolean cancel = fireBytesGot(count);  // PB added
            if(cancel) break;                      // PB added
		}
		out.close();

		// check all ok
		reply = control.readReply();
		control.validateReply(reply, "226");
	}


	/**
	 *  Get data from the FTP server. Uses the currently
	 *  set transfer mode. Retrieve as a byte array. Note
	 *  that we may experience memory limitations as the
	 *  entire file must be held in memory at one time.
	 *
	 *  @param  remoteFile  name of remote file in
	 *                      current directory
	 */
	public byte[] get(String remoteFile)
		throws IOException, FTPException {

		// get an input stream to read data from
		data = control.createDataSocket();
		DataInputStream in = new DataInputStream(data.getInputStream());
		BufferedInputStream bIn = new BufferedInputStream(in);

		// send the retrieve command
		String reply = control.sendCommand("RETR " + remoteFile);
		control.validateReply(reply, "150");

		// do the retrieving
		int chunksize = 4096;
		byte [] chunk = new byte[chunksize];  // read chunks into
		byte [] resultBuf = new byte[chunksize];  // where we place chunks
		byte [] temp = null;  // temp swap buffer
		int count;  // size of chunk read
		int bufsize = 0;  // size of resultBuf

		// read from socket & write to file
		while ((count = bIn.read(chunk, 0, chunksize)) >= 0) {

			// new buffer to hold current buf + new chunk
			temp = new byte[bufsize+count];

			// copy current buf to temp
			System.arraycopy(resultBuf, 0, temp, 0, bufsize);

			// copy new chunk onto end of temp
			System.arraycopy(chunk, 0, temp, bufsize, count);

			// re-assign temp buffer to buf
			resultBuf = temp;

			// update size of buffer
			bufsize += count;
		}

		// check all ok
		reply = control.readReply();
		control.validateReply(reply, "226");

		return resultBuf;
	}



	/**
	 *  Get the current transfer type
	 *
	 *  @param  dir  name of remote file to
	 *               delete
	 */
	 public FTPTransferType getType() {

		return transferType;
	}

	/**
    * Added by PB get FileSize
    */
    public int getFileSize(String remoteFile) {
        int size = 10000;
        try {
			String reply = control.sendCommand("SIZE " + remoteFile);
            reply = reply.trim();
            int index = reply.indexOf(" ");
            if(index != -1) reply = reply.substring(index + 1);
            size = Integer.parseInt(reply);
            if(ColloquiaConstants.DEBUG) System.out.println("" + size);
        }
        catch(Exception ex) {
        	if(ColloquiaConstants.DEBUG) ex.printStackTrace();
            return size;
        }
        return size;
    }

	/**
	 *  Delete the specified remote file
	 *
	 *  @param  dir  name of remote file to
	 *               delete
	 */
	public void setType(FTPTransferType type)
		throws IOException, FTPException {

		// determine the character to send
		String typeStr = FTPTransferType.ASCII_CHAR;
		if (type.equals(FTPTransferType.BINARY))
			typeStr = FTPTransferType.BINARY_CHAR;

		// send the command
		String reply = control.sendCommand("TYPE " + typeStr);
		control.validateReply(reply, "200");

		// record the type
		transferType = type;
	}


	/**
	 *  Delete the specified remote file
	 *
	 *  @param  dir  name of remote file to
	 *               delete
	 */
	public void delete(String remoteFile)
		throws IOException, FTPException {

		String reply = control.sendCommand("DELE " + remoteFile);
		control.validateReply(reply, "250");
	}


	/**
	 *  Delete the specified remote working directory
	 *
	 *  @param  dir  name of remote directory to
	 *               delete
	 */
	public void rmdir(String dir)
		throws IOException, FTPException {

        String reply = control.sendCommand("RMD " + dir);
		control.validateReply(reply, "250");
	}


	/**
	 *  Create the specified remote working directory
	 *
	 *  @param  dir  name of remote directory to
	 *               create
	 */
	public void mkdir(String dir)
		throws IOException, FTPException {

        String reply = control.sendCommand("MKD " + dir);
		control.validateReply(reply, "257");
	}


	/**
	 *  Change the remote working directory to
	 *  that supplied
	 *
	 *  @param  dir  name of remote directory to
	 *               change to
	 */
	public void chdir(String dir)
		throws IOException, FTPException {

        String reply = control.sendCommand("CWD " + dir);
		control.validateReply(reply, "250");
	}



	/**
	 *  Quit the FTP session
	 *
	 */
	 public void quit()
	 	throws IOException, FTPException {

		String reply = control.sendCommand("QUIT" + FTPControlSocket.EOL);
		control.validateReply(reply, "221");

		control.logout();
		control = null;
	}


    // ==================================================================
    // ========================= Listener Stuff =========================
    // ==================================================================

    private Vector listeners = new Vector();

    public synchronized void addFTPListener(FTPListener listener) {
        if(!listeners.contains(listener)) listeners.addElement(listener);
        control.addFTPListener(listener);
    }

    public synchronized void removeFTPListener(FTPListener listener) {
        listeners.removeElement(listener);
        control.removeFTPListener(listener);
    }

    /**
    * Tell our listeners that we PUT some bytes
    * A listener can return true if they want to cancel
    */
    public boolean fireBytesPut(int bytes) {
        boolean cancel = false;
        for(int i = 0; i < listeners.size(); i++) {
            FTPListener listener = (FTPListener)listeners.elementAt(i);
            boolean result = listener.bytesPut(bytes);
            if(result) cancel = true;
        }
        return cancel;
    }

    /**
    * Tell our listeners that we GET some bytes
    * A listener can return true if they want to cancel
    */
    public boolean fireBytesGot(int bytes) {
        boolean cancel = false;
        for(int i = 0; i < listeners.size(); i++) {
            FTPListener listener = (FTPListener)listeners.elementAt(i);
            boolean result = listener.bytesGot(bytes);
            if(result) cancel = true;
        }
        return cancel;
    }
}



