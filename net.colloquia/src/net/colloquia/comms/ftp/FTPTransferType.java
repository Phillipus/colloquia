/**
 *  Copyright (C) 2000 Enterprise Distributed Technologies Ltd.
 *
 * 
 *  Change Log:  
 *
 *	  $Log: FTPTransferType.java,v $
 *	  Revision 1.1  2006/04/19 14:17:51  phillipus
 *	  First commit
 *	
 */

package net.colloquia.comms.ftp;

/**
 *  Enumerates the transfer types possible. We
 *  support only the two common types, ASCII and
 *  Image (often called binary). 
 *
 *  @author		Bruce Blackshaw
 *	@version	$Revision: 1.1 $
 *
 */
 public class FTPTransferType {
 
 	/**
 	 *   Represents ASCII transfer type
	 */
 	public static FTPTransferType ASCII = new FTPTransferType();

 	/**
 	 *   Represents Image (or binary) transfer type
	 */	
	public static FTPTransferType BINARY = new FTPTransferType();
	
	/**
 	 *   The char sent to the server to set ASCII
	 */	
	static String ASCII_CHAR = "A";
	
	/**
 	 *   The char sent to the server to set BINARY
	 */		
	static String BINARY_CHAR = "I";
	
	/**
 	 *  Private so no-one else can instantiate this class
	 */		
	private FTPTransferType() {
	}
 }
 