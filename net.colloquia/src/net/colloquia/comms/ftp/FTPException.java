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
 *	  $Log: FTPException.java,v $
 *	  Revision 1.1  2006/04/19 14:17:51  phillipus
 *	  First commit
 *	
 */

package net.colloquia.comms.ftp;

/**
 *  FTP specific exceptions
 *
 *  @author		Bruce Blackshaw
 *	@version	$Revision: 1.1 $
 *
 */
 public class FTPException extends Exception {
 
 	/**
 	 *   Constructor. Delegates to super.
 	 *
 	 *   @param   msg   Message that the user will be
	 *                  able to retrieve
	 */ 	
 	public FTPException(String msg) {
		super(msg);
	}
 
 
 }
 
