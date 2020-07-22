package net.colloquia.gui;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

import net.colloquia.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.io.*;
import net.colloquia.menu.*;
import net.colloquia.util.*;

/**
 * Core functionality of an HTML Editor
 */
public abstract class HTMLEditor
extends JPanel
implements HyperlinkListener, DocumentListener
{
    protected JEditorPane editor;
    protected JScrollPane editorScrollPane;

    // Editable
    protected JPanel editMenuPanel;
    protected JMenuBar editMenuBar;
    protected ColloquiaToolBar editToolBar;
   	protected JMenu editMenu;
   	protected JMenu insertMenu;
   	protected JMenu fontMenu;
   	protected JMenu paraMenu;
   	protected JMenu colorMenu;
   	protected JMenu viewMenu;

    protected MenuAction actionCut;
    protected MenuAction actionCopy;
    protected MenuAction actionpaste;
    protected HyperAction hyperAction;
    protected MenuAction actionBold;
    protected MenuAction actionItalic;
    protected MenuAction actionUnderLine;
    protected MenuAction actionView;

    // View
    protected JPanel viewMenuPanel;
    protected JMenuBar viewMenuBar;
    protected ColloquiaToolBar viewToolBar;

    protected MenuAction actionEdit;

    protected SuperHTMLEditorKit kit;

    /**
    * CONSTRUCTOR
    */
    protected HTMLEditor(boolean edit) {
        setLayout(new BorderLayout());

        editor = new JEditorPane();

        // Over-ride the editor kit to hide the <TAG> stuff
        // 2002-10-19 This is still necessary to hide some tags (not <HEAD>, any longer)
        kit = new SuperHTMLEditorKit();

        editor.setEditorKit(kit);

        kit.setDefaultCursor(ColloquiaConstants.textCursor);

        editor.addHyperlinkListener(this);

        // Listen for page load to complete on asynchronous loading
        editor.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if(propertyName.equals("page")) pageLoaded();
                if(propertyName.equals("document")) documentLoaded();
            }
        });

        editMenuPanel = createEditMenuBar();
        viewMenuPanel = createViewMenuBar();

        if(edit) add(editMenuPanel, BorderLayout.NORTH);
        else add(viewMenuPanel, BorderLayout.NORTH);

        editorScrollPane = new JScrollPane(editor);
        add(editorScrollPane, BorderLayout.CENTER);

        setEditable(edit);
        actionEdit.setEnabled(edit);

        // New Document needs listener
        editor.getDocument().addDocumentListener(this);

        // Listen to some key presses
        editor.addKeyListener(new KeyPressListener());
    }

    protected class KeyPressListener extends KeyAdapter {
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            if(key == KeyEvent.VK_ENTER && e.isShiftDown()) {
            	insertBreak();
		    }
        }
    }

    protected void newDocument() {
        // Must remove listener so as to not fire event
        Document oldDoc = editor.getDocument();
        oldDoc.removeDocumentListener(this);
        Document newDoc = kit.createDefaultDocument();
        editor.setDocument(newDoc);
        newDoc.addDocumentListener(this);
    }

    protected void showMenu(boolean edit) {
        setEditable(edit);
      	remove(edit ? viewMenuPanel : editMenuPanel);
        add(edit ? editMenuPanel : viewMenuPanel, BorderLayout.NORTH);
        validate();
        repaint();
    }

    public void clear() {
        newDocument();
    }

    public void setEditable(boolean value) {
    	editor.setEditable(value);
    }

    /**
    * Insert some text
    * PENDING Setting att to anything other than null causes a nasty!
    */
    protected void insertText(int offset, String text, AttributeSet att) {
        try {
            editor.getDocument().insertString(offset, text, att);
        }
        catch(BadLocationException ex) {
            System.out.println("Bad Insert text: " + ex);
        }
    }

    /**
    * Insert some HTMLtext
    */
    protected void insertHTMLText(int offset, String htmlText) {

        try {
            HTMLDocument doc = (HTMLDocument)editor.getDocument();
            kit.insertHTML(doc, offset, htmlText, 0, 0, null);
        }
        catch(Exception ex) {
            System.out.println("Could not Insert text: " + ex);
        }

    }

    public static void saveHTMLFile(String fileName, Document doc) throws ColloquiaFileException {
        try {
            File file = new File(fileName);
            DataFiler.checkFolder(file.getParent());

            FileWriter out = new FileWriter(file);

            /*  because of the font bug - fixed in Merlin
            HTMLWriter w = new HTMLWriter(out, (HTMLDocument)doc);
            w.write();
            out.flush();
            out.close();
            */

            String html = getDocumentText(doc);
            html = fixFontString(html);
            out.write(html);
            out.flush();
            out.close();
        }
        catch(Exception ex) {
            throw new ColloquiaFileException("Could not save html file", ex.getMessage());
        }
    }


    /**
    * Loads a file off disk
    */
    public void loadFile(String fileName, boolean canEdit, boolean asynchronous) {
        actionEdit.setEnabled(canEdit);

        // Ensure that correct view is maintained if switching to a read-only view
        if(canEdit == false) showMenu(false);

        URL url = URLUtils.makeLocalURL(fileName);

        if(!DataFiler.fileExists(fileName) || url == null) {
            newDocument();
            return;
        }

        Document oldDoc = editor.getDocument();
        oldDoc.removeDocumentListener(this);

        try {
            //MainFrame.getInstance().setCursor(Constants.waitCursor);

            if(asynchronous) {
                // this will create a new document
                editor.setPage(url);
                // pageLoaded() will be called by PropertyChangeListener
            }

            else {
                Document newDoc = kit.createDefaultDocument();
                editor.setDocument(newDoc);
                FileReader in = new FileReader(fileName);
                editor.read(in, newDoc);
                in.close();
                pageLoaded();
            }

        }
        catch(Exception e) {
            System.out.println("Could not load page! " + e);
            editor.setDocument(oldDoc);
            oldDoc.addDocumentListener(this);
            MainFrame.getInstance().setCursor(ColloquiaConstants.defaultCursor);
        }
    }

    /**
    * Load in html source
    */
    protected String getSource(String fileName) throws IOException, FileNotFoundException {
        File file = new File(fileName);
        if(!file.exists()) return null;

        int bit;
        StringBuffer sb;

        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        sb = new StringBuffer();

        // Extract it
        while((bit = in.read()) != -1) {
            sb.append((char)bit);
        }

        in.close();

        return sb.toString();
    }

    /**
    * This is invoked when the page has been loaded
    * You can over-ride it to do something useful (call super() first though)
    */
    protected void pageLoaded() {
        editor.getDocument().addDocumentListener(this);
        MainFrame.getInstance().setCursor(ColloquiaConstants.defaultCursor);
    }

    protected void documentLoaded() {
    }

    /**
    * The editor is not garbage-collected unless we do this
    */
    public void dispose() {
        editMenu.removeAll();
        insertMenu.removeAll();
        fontMenu.removeAll();
        paraMenu.removeAll();
        colorMenu.removeAll();
        viewMenu.removeAll();
        editMenuBar.removeAll();
        editMenuBar = null;
        editToolBar = null;

        viewMenuBar.removeAll();
        viewMenuBar = null;
        viewToolBar = null;
    }

    /**
    * Create a menu bar for an editable view
    */
    protected JPanel createViewMenuBar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        viewToolBar = new ColloquiaToolBar();
        viewMenuBar = new JMenuBar();
        panel.add(viewMenuBar, BorderLayout.NORTH);
        panel.add(viewToolBar, BorderLayout.CENTER);

        actionEdit = new EditAction();

        viewToolBar.add(actionEdit);

        return panel;
	}


    /**
    * Create a menu bar for an editable view
    */
    protected JPanel createEditMenuBar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        editToolBar = new ColloquiaToolBar();
        editMenuBar = new JMenuBar();
        panel.add(editMenuBar, BorderLayout.NORTH);
        panel.add(editToolBar, BorderLayout.CENTER);

        // The Key Mask will be 'Meta' for Mac and 'Ctrl' for PC/Unix
        int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

      	editMenu = editMenuBar.add(new JMenu(LanguageManager.getString("EDIT")));
      	insertMenu = editMenuBar.add(new JMenu(LanguageManager.getString("INSERT")));
      	fontMenu = editMenuBar.add(new JMenu(LanguageManager.getString("FONT")));
      	paraMenu = editMenuBar.add(new JMenu(LanguageManager.getString("PARAGRAPH")));
      	colorMenu = editMenuBar.add(new JMenu(LanguageManager.getString("COLOUR")));
      	viewMenu = editMenuBar.add(new JMenu(LanguageManager.getString("VIEW")));

        // EDIT
        actionCut = new ActionWrapper(getAction("cut-to-clipboard"), ColloquiaConstants.iconCut,
                                    LanguageManager.getString("CUT"));
        actionCut.setButtonText(LanguageManager.getString("BUT16"));
        JMenuItem item = editMenu.add(actionCut);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, keyMask));

        actionCopy = new ActionWrapper(getAction("copy-to-clipboard"), ColloquiaConstants.iconCopy,
                                    LanguageManager.getString("COPY"));
        actionCopy.setButtonText(LanguageManager.getString("BUT17"));
        item = editMenu.add(actionCopy);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, keyMask));

        actionpaste = new ActionWrapper(getAction("paste-from-clipboard"), ColloquiaConstants.iconPaste,
                                    LanguageManager.getString("PASTE"));
        actionpaste.setButtonText(LanguageManager.getString("BUT18"));
        item = editMenu.add(actionpaste);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, keyMask));

        // INSERT
        hyperAction = new HyperAction();
        item = insertMenu.add(hyperAction);

        MenuAction actionList = new ActionWrapper(getAction("InsertOrderedListItem"), null,
                                "List");
        insertMenu.add(actionList);

        MenuAction actionBullet = new ActionWrapper(getAction("InsertUnorderedListItem"), null,
                                "Bullet");
        insertMenu.add(actionBullet);

        // FONT
        actionBold = new ActionWrapper(getAction("font-bold"), ColloquiaConstants.iconBold,
                        LanguageManager.getString("BOLD"));
        item = fontMenu.add(actionBold);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, keyMask));
        actionItalic = new ActionWrapper(getAction("font-italic"), ColloquiaConstants.iconItalic,
                        LanguageManager.getString("ITALIC"));
        item = fontMenu.add(actionItalic);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, keyMask));
        actionUnderLine = new ActionWrapper(getAction("font-underline"), ColloquiaConstants.iconUnderLine,
                        LanguageManager.getString("UNDERLINE"));
        item = fontMenu.add(actionUnderLine);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, keyMask));

        fontMenu.addSeparator();

        fontMenu.add(new FontSizeAction("8"));
        fontMenu.add(new FontSizeAction("10"));
        fontMenu.add(new FontSizeAction("12"));
        fontMenu.add(new FontSizeAction("14"));
        fontMenu.add(new FontSizeAction("16"));
        fontMenu.add(new FontSizeAction("18"));
        fontMenu.add(new FontSizeAction("20"));
        fontMenu.add(new FontSizeAction("24"));
        fontMenu.add(new FontSizeAction("36"));
        fontMenu.add(new FontSizeAction("48"));
        fontMenu.addSeparator();
        fontMenu.add(new ActionWrapper(getAction("font-family-SansSerif"), "SansSerif"));
        fontMenu.add(new ActionWrapper(getAction("font-family-Monospaced"), "Monospaced"));
        fontMenu.add(new ActionWrapper(getAction("font-family-Serif"), "Serif"));

        // PARAGRAPH
        MenuAction actionLeft = new ActionWrapper(getAction("left-justify"), ColloquiaConstants.iconLeft,
                                LanguageManager.getString("LEFT"));
        paraMenu.add(actionLeft);

        MenuAction actionCentre = new ActionWrapper(getAction("center-justify"), ColloquiaConstants.iconCentre,
                                LanguageManager.getString("CENTRE"));
        paraMenu.add(actionCentre);

        MenuAction actionRight = new ActionWrapper(getAction("right-justify"), ColloquiaConstants.iconRight,
                                LanguageManager.getString("RIGHT"));
        paraMenu.add(actionRight);

        // COLOR
        colorMenu.add(new ColorAction(Color.black, LanguageManager.getString("BLACK")));
        colorMenu.add(new ColorAction(Color.white, LanguageManager.getString("WHITE")));
        colorMenu.add(new ColorAction(Color.red, LanguageManager.getString("RED")));
        colorMenu.add(new ColorAction(Color.green, LanguageManager.getString("GREEN")));
        colorMenu.add(new ColorAction(Color.blue, LanguageManager.getString("BLUE")));
        colorMenu.add(new ColorAction(Color.yellow, LanguageManager.getString("YELLOW")));
        colorMenu.add(new ColorAction(Color.darkGray, LanguageManager.getString("DARK_GREY")));
        colorMenu.add(new ColorAction(Color.lightGray, LanguageManager.getString("LIGHT_GREY")));
        colorMenu.add(new ColorAction(Color.magenta, LanguageManager.getString("MAGENTA")));
        colorMenu.add(new ColorAction(Color.cyan, LanguageManager.getString("CYAN")));
        colorMenu.add(new ColorAction());

        // VIEW
        actionView = new ViewAction();
        viewMenu.add(actionView);

        editToolBar.add(actionCut);
        editToolBar.add(actionCopy);
        editToolBar.add(actionpaste);
        editToolBar.addSeparator();
        editToolBar.add(actionBold);
        editToolBar.add(actionItalic);
        editToolBar.add(actionUnderLine);
        editToolBar.addSeparator();
        editToolBar.add(actionLeft);
        editToolBar.add(actionCentre);
        editToolBar.add(actionRight);
        editToolBar.addSeparator();
        editToolBar.add(actionView);

        return panel;
    }


    // =========================== ACTIONS =================================

    protected static Hashtable actionHash;

    static {
        getActions();
    }

    // Get all actions and store in a table
    private static void getActions() {
        actionHash = new Hashtable();
        Action[] actions = new HTMLEditorKit().getActions();
        for(int i = 0; i < actions.length; i++) {
            String name = (String)actions[i].getValue(Action.NAME);
            actionHash.put(name, actions[i]);
            //System.out.println(name);
        }
    }

    /**
    * Get an Action from the Hashtable
    */
    protected static Action getAction(String name) {
        return (Action)actionHash.get(name);
    }

    /**
    * Insert Hyperlink
    * Note - we have to reload the file to see it!
    */
    protected class HyperAction extends StyledEditorKit.StyledTextAction {
        public HyperAction() {
            super(LanguageManager.getString("14_13"));
        }

        public void actionPerformed(ActionEvent e) {
            // Get name
            String link = (String)JOptionPane.showInputDialog(HTMLEditor.this,
                LanguageManager.getString("14_13"), LanguageManager.getString("14_12"),
                JOptionPane.PLAIN_MESSAGE, null, null, "http://");
            // Trim spaces
            if(link == null) return;
            link = link.trim();
            if(link.length() == 0) return;

            SimpleAttributeSet hrefSet = new SimpleAttributeSet();
            hrefSet.addAttribute("href", link);
            SimpleAttributeSet attrSet = new SimpleAttributeSet();
            attrSet.addAttribute(HTML.Tag.A, hrefSet);
            setCharacterAttributes(editor, attrSet, false);
            hyperLinkInserted();
        }
    }

    protected void hyperLinkInserted() {
    }


    /**
    * Background Colour
    */
    protected class BackColorAction extends StyledEditorKit.StyledTextAction {
        Color color = Color.black;

        public BackColorAction() {
            super(LanguageManager.getString("14_14"));
        }

        public void actionPerformed(ActionEvent e) {
            Color color = JColorChooser.showDialog(HTMLEditor.this,
                LanguageManager.getString("14_15"), Color.white);
            if(color == null) return;
            String colorText = "#" + Integer.toHexString(color.hashCode());

            SimpleAttributeSet hrefSet = new SimpleAttributeSet();
            hrefSet.addAttribute("bgcolor", colorText);
            SimpleAttributeSet attrSet = new SimpleAttributeSet();
            attrSet.addAttribute(HTML.Tag.BODY, hrefSet);
            setCharacterAttributes(editor, attrSet, false);

            //editor.repaint();
            //editor.requestFocus();

        }
    }

    protected static class InsertBreakAction extends HTMLEditorKit.InsertHTMLTextAction {
        public InsertBreakAction() {
    		super("break", "<br>", HTML.Tag.P, HTML.Tag.BR);
        }

        public void insertBreak(JEditorPane editor) {
        	actionPerformed(new ActionEvent(editor, 0, "break"));
        }
    }

    static InsertBreakAction insertBreakAction = new InsertBreakAction();

    protected void insertBreak() {
    	insertBreakAction.insertBreak(editor);
    }

    /**
    *
    */
    protected class ViewAction extends MenuAction {
        public ViewAction() {
            super(LanguageManager.getString("VIEW"), ColloquiaConstants.iconEdit);
            setButtonText(LanguageManager.getString("BUT15"));
        }

        public void actionPerformed(ActionEvent e) {
            showMenu(false);
        }
    }

    /**
    *
    */
    protected class EditAction extends MenuAction {
        public EditAction() {
            super(LanguageManager.getString("EDIT"), ColloquiaConstants.iconEdit);
            setButtonText(LanguageManager.getString("BUT13"));
        }

        public void actionPerformed(ActionEvent e) {
            /*
            String htmlEditor = UserPrefs.getUserPrefs().getProperty(UserPrefs.HTML_EDITOR).trim();
            if(!htmlEditor.equals("")) {
                if(!DataFiler.fileExists(fileName)) {
                    try {
						saveHTMLFile(fileName, editor.getDocument());
                    }
                    catch(ColloquiaFileException ex) {
                    }
                }
            	AppLauncher.launchApp(htmlEditor, fileName);
            }
            else showMenu(true);
            */
            showMenu(true);
        }
    }


    protected class ColorAction extends MenuAction {
        Action a;
        Color color;
        boolean dialog;

        public ColorAction() {
            super(LanguageManager.getString("14_16"), null);
            dialog = true;
        }

        public ColorAction(Color color, String name) {
            super(name, null);
            this.color = color;
            setMenuIcon(new ColoredSquare(color));
        }

        public void actionPerformed(ActionEvent e) {
            if(dialog == true) {
                color = JColorChooser.showDialog(HTMLEditor.this,
                    LanguageManager.getString("14_15"), Color.black);
                if(color == null) return;
            }
            a = new StyledEditorKit.ForegroundAction("fg", color);
            ActionEvent ex = new ActionEvent(editor, 0, color.toString());
            a.actionPerformed(ex);
            editor.repaint();
            editor.requestFocus();
        }

        // Colored square icons
        class ColoredSquare extends ImageIcon {
            Color color;

            public ColoredSquare(Color c) {
                this.color = c;
            }

            public void paintIcon(Component c, Graphics g, int x, int y) {
                Color oldColor = g.getColor();
                g.setColor(color);
                g.fill3DRect(x, y, getIconWidth(), getIconHeight(), true);
                g.setColor(oldColor);
            }

            public int getIconWidth() { return 12; }
            public int getIconHeight() { return 12; }
        }
    }

    protected class FontSizeAction extends AbstractAction {
        Action a;

        public FontSizeAction(String size) {
            super(size);
            a = new StyledEditorKit.FontSizeAction(size, Integer.parseInt(size));
        }

        public void actionPerformed(ActionEvent e) {
            a.actionPerformed(e);
            editor.repaint();
            editor.requestFocus();
        }
    }

    protected class ActionWrapper extends MenuAction {
        Action a;

        public ActionWrapper(Action a, String iconName, String text) {
            super(text, iconName);
            this.a = a;
        }

        public ActionWrapper(Action a, String text) {
            super(text, null);
            this.a = a;
        }

        public void actionPerformed(ActionEvent e) {
            a.actionPerformed(e);
            editor.repaint();
            editor.requestFocus();
        }
    }

    /**
    * A portion of the document has been removed
    */
    public void removeUpdate(DocumentEvent evt) {
        editHappened();
    }

    /**
    * An attribute or set of attributes changed.
    */
    public void changedUpdate(DocumentEvent evt) {
        editHappened();
    }

    /**
    * There was an insert into the document
    */
    public void insertUpdate(DocumentEvent evt) {
        editHappened();
    }

    /**
    * This has to be implemented in sub-classes
    */
    protected abstract void editHappened();

    public JEditorPane getEditorPane() {
        return editor;
    }


    /**
    * A Hyperlink Listener
    */
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
        		BrowserLauncher.openURL(e.getURL().toString());
            }
            catch(IOException ex) {
            	System.out.println("could not launch browser");
            }
        }
        else if(e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
        }
        else if(e.getEventType() == HyperlinkEvent.EventType.EXITED) {
        }
    }

    protected static String getDocumentText(Document doc) {
        String html = "";
        try {
            StringWriter out = new StringWriter();
            HTMLEditorKit kit = new HTMLEditorKit();
            kit.write(out, doc, 0, doc.getLength());
            out.flush();
            html = out.toString();
            out.close();
        }
        catch(Exception ex) {
            System.err.println("Could not get document text:");
            ex.printStackTrace();
        }

        return html;
    }

    // Java Bug #4260461 - fixed in Merlin
    // A hack to work around differing font sizes
    // Instead of writing directly to a file, call this method
    // and then write the resulting string to the file.
    protected static String fixFontString(String html) {
        /* BTW, I don't have to worry about the string 'size="'
        * appearing in open text because the HTML writer will translate
        * the '"' to an &quote; before I ever see it.
        *
        * I also don't have to worry about 'size=' not being followed
        * by a '"' because the HTML writer always uses '"' as the delimiter
        * even if the original HTML didn't (feature or bug?!?).
        */
        int index = html.indexOf("size=\""); // Find the attribute name and the leading delimiter
        if(index < 0) return html;

        int end, size;
        String more, num, realSize;

        more = html.substring(index + 6); // Add 6 to get past 'size="'
        end = more.indexOf('"'); // Find the trailing delimiter
        num = more.substring(0, end); // Everything inbetween is the size

        if(num.charAt(0) == '+') {
            realSize = num; // Relative '+' sizes are fine as is
        }
        else {
            try {
                size = Integer.parseInt(num);
            }
            catch(NumberFormatException exc) {
                size = 0; // Hmm, I don't know. Better not do any translation
            }

            // Numbers less than 8 are either an index or a '-' relative size.
            // Both are fine the way they are
            if(size < 8) {
                realSize = num;
            }
            else {
                realSize = String.valueOf(StyleSheet.getIndexOfSize(size));
            }
        }
        // Yeah, I'm making a lot of inefficient Strings but who cares, it's a workaround!
        return html.substring(0, index) + "size=\"" + realSize + fixFontString(more.substring(end));
    }
}



/**
* An Editor kit that hides the HEAD etc tags
* Depracated  2002-10-18
*/
class SuperHTMLEditorKit extends HTMLEditorKit {
    static HTML.Tag[] hiddenTags = {
        HTML.Tag.COMMENT, HTML.Tag.HEAD,
        HTML.Tag.TITLE, HTML.Tag.META, HTML.Tag.LINK,
        HTML.Tag.STYLE, HTML.Tag.SCRIPT, HTML.Tag.AREA,
        HTML.Tag.MAP, HTML.Tag.PARAM, HTML.Tag.APPLET
    };

    public Object clone() {
        return new SuperHTMLEditorKit();
    }

    public ViewFactory getViewFactory() {
        return new SuperViewFactory();
    }

    public static class SuperViewFactory extends HTMLEditorKit.HTMLFactory {

        public View create(Element elem) {
            Object tag = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);

            if(tag instanceof HTML.Tag) {
                for(int i = 0; i < hiddenTags.length; i++) {
                    if(hiddenTags[i] == tag) return new HiddenTagView(elem);
                }
            }

            if(tag instanceof HTML.UnknownTag) return new HiddenTagView(elem);

            return super.create(elem);
        }
    }
}


class HiddenTagView extends View {
    public HiddenTagView(Element elem) {
        super(elem);
    }

    public float getMinimumSpan(int axis) {
        return 0;
    }

    public float getPreferredSpan(int axis) {
        return 0;
    }

    public float getMaximumSpan(int axis) {
        return 0;
    }

    public void paint(Graphics g, Shape a) { }


    public boolean isVisible() {
	    return false;
    }

    public Shape modelToView(int pos, Shape a, Position.Bias b) {
        return a;
    }

    public int viewToModel(float x, float y, Shape a, Position.Bias[] biasReturn) {
        return getStartOffset();
    }
}

