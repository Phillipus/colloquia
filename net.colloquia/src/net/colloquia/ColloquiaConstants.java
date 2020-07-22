/**
 *  Colloquia - An Open Source Groupware tool
 *  Copyright (c) 1998-2004 Oleg Liber, Bill Olivier, Phillip Beauvoir
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
 *  Web:      http://www.colloquia.net
 *
 */

package net.colloquia;

import java.awt.*;

import javax.swing.border.*;

/**
 * Some useful Colloquia Constants
 *
 * @author Phillip Beauvoir
 * @version $Id: ColloquiaConstants.java,v 1.3 2007/07/15 20:24:04 phillipus Exp $
 */
public final class ColloquiaConstants {

    public static boolean ISBUILD = true;

    public static boolean STATUS_WINDOW = ISBUILD;       // Send Output to Status Window
    public static boolean DEBUG = !ISBUILD;              // Debug Trace
    public static boolean PRELOAD_VIEWS = ISBUILD;       // Preload views

    public static String APP_NAME =                "Colloquia";
    public static String VERSION =                 "1.4.3.1";
    public static String BUILD_DATE =              "18th March 2005";
    public static String EMAIL_CONTACT =           "o.liber@bolton.ac.uk";
    public static String WEB_PAGE =                "http://www.colloquia.net";
    public static String VERSION_WEB =             "http://www.colloquia.net/ver.txt";
    public static boolean NETWORKED =              false;

    // The Message Header ID Strings - Don't change these!
    public static final String ColloquiaMessageVersion =   "ToomolMessageVersion";
    public static final String msgVersion =                "1.2";

    // Line Seperator
    public static String CR = System.getProperty("line.separator");

    // ========================================================================
    // ============================== ICONS ===================================
    // ========================================================================
    public static String iconPath = "/net/colloquia/resources/images/";

    public static String iconPhillipus = "000";
    public static String iconAscendingSort = "001";
    public static String iconDescendingSort = "002";
    public static String iconPersonDeclined = "003";
    public static String iconPersonPending = "004";
    public static String iconPersonAwaiting = "004a";
    public static String iconSplash = "005";
    public static String iconAssignment = "006";
    public static String iconPerson = "007";
    public static String iconResource = "008";
    public static String iconActivityCompleted = "009";
    public static String iconActivityCompletedFolder = "010";
    public static String iconActivityFuture = "011";
    public static String iconResourceFree = "012";
    public static String iconAssignmentFree = "013";
    public static String iconActivityLiveFolder = "014";
    public static String iconActivityTemplateFolder = "015";
    public static String iconNew = "016";
    public static String iconEdit = "017";
    public static String iconCopy = "018";
    public static String iconCut = "019";
    public static String iconPaste = "020";
    public static String iconDelete = "021";
    public static String iconSave = "022";
    public static String iconPrint = "023";
    public static String iconAttach = "024";
    public static String iconAttachClear = "025";
    public static String iconHome = "026";
    public static String iconBack = "027";
    public static String iconForward = "028";
    public static String iconReload = "029";
    public static String iconSet = "030";
    public static String iconStop = "031";
    public static String iconBold = "032";
    public static String iconCentre = "033";
    public static String iconUnderLine = "034";
    public static String iconItalic = "035";
    public static String iconJustify = "036";
    public static String iconLeft = "037";
    public static String iconRight = "038";
    public static String iconEditMessage = "040";
    public static String iconNewMessage = "041";
    public static String iconMessageMark = "042";
    public static String iconFolder = "043";
    public static String iconOpenFolder = "043o";
    public static String iconGroupMessage = "044";
    public static String iconSingleMessage = "045";
    public static String iconMessage = "046";
    public static String iconHelp = "047";
    public static String iconAppIcon = "048";
    public static String iconActivityDeclined = "049";
    public static String iconActivityAccepted = "050";
    public static String iconOutbox = "052";
    public static String iconInOutbox = "053";
    public static String iconGetMail = "054";
    //public static String iconNotUsed = "055";
    public static String iconMyDetails = "056";
    //public static String iconNotUsed = "057";
    public static String iconSendActivity = "058";
    public static String iconReply = "059";
    public static String iconSendMessage = "060";
    //public static String iconNotUsed = "061";
    //public static String iconNotUsed = "062";
    //public static String iconNotUsed = "063";
    //public static String iconNotUsed = "064";
    public static String iconMessageAttachment = "065";
    public static String iconViewLocalFile = "066";
    public static String iconCreateResource = "067";
    //public static String iconNotUsed = "068";
    public static String iconHidePeople = "069";
    public static String iconHideResources = "070";
    public static String iconHideAssignments = "071";
    public static String iconHideFuture = "072";
    public static String iconHideCompleted = "073";
    public static String iconGotoNextMessage = "074";
    public static String iconLogonBackground = "075";
    public static String iconAddPeopleResources = "076";
    public static String iconSendMail = "077";
    public static String iconActivityHot = "078";
    public static String iconSaveHot = "079";
    public static String iconHelpLeaf = "help_leaf";
    public static String iconHelpOpen = "help_open";
    public static String iconHelpClosed = "help_closed";


    // ========================================================================
    // ============================== COLOURS =================================
    // ========================================================================
    public static final Color color1 = new Color(102, 153, 153);
    public static final Color color2 = new Color(194, 217, 217);
    public static final Color color3 = new Color(194+30, 217+30, 217+30);
    public static final Color toolBarColor = new Color(255, 255, 255);

    public static Color futureColor = new Color(0, 102, 153);
    public static Color completedColor = new Color(17, 151, 105);
    public static Color liveColor = new Color(153, 0, 0);
    public static Color templateColor = new Color(68, 68, 68);


    // ========================================================================
    // ============================== FONTS ===================================
    // ========================================================================
    public static Font plainFont11 = new Font("SansSerif", Font.PLAIN, 11);
    public static Font boldFont11 = new Font("SansSerif", Font.BOLD, 11);
    public static Font italicFont11 = new Font("SansSerif", Font.ITALIC, 11);
    public static Font bold_italicFont11 = new Font("SansSerif", Font.ITALIC + Font.BOLD, 11);
    public static Font plainFont12 = new Font("SansSerif", Font.PLAIN, 12);
    public static Font boldFont12 = new Font("SansSerif", Font.BOLD, 12);
    public static Font italicFont12 = new Font("SansSerif", Font.ITALIC, 12);
    public static Font bold_italicFont12 = new Font("SansSerif", Font.ITALIC + Font.BOLD, 12);
    public static Font plainFont13 = new Font("SansSerif", Font.PLAIN, 13);
    public static Font boldFont13 = new Font("SansSerif", Font.BOLD, 13);
    public static Font italicFont13 = new Font("SansSerif", Font.ITALIC, 13);
    public static Font bold_italicFont13 = new Font("SansSerif", Font.ITALIC + Font.BOLD, 13);
    public static Font plainFont14 = new Font("SansSerif", Font.PLAIN, 14);
    public static Font boldFont14 = new Font("SansSerif", Font.BOLD, 14);


    // ========================================================================
    // ============================== CURSORS =================================
    // ========================================================================
    public static Cursor textCursor = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
    public static Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    public static Cursor defaultCursor = Cursor.getDefaultCursor();

    // ========================================================================
    // ============================== BORDERS =================================
    // ========================================================================
    public static Border noFocusBorder = new EmptyBorder(1, 2, 1, 2);
    public static Border focusBorder = new LineBorder(new Color(255, 153, 0));
    public static Border hiBorder = new BevelBorder(BevelBorder.RAISED);

}
