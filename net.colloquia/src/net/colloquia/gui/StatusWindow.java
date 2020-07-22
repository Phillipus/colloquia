package net.colloquia.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.Date;

import javax.swing.*;

import net.colloquia.ColloquiaConstants;
import net.colloquia.io.DataFiler;
import net.colloquia.prefs.UserPrefs;
import net.colloquia.util.LanguageManager;
import net.colloquia.util.Utils;


public class StatusWindow extends JFrame {
    private static boolean macKludge;
    private static StatusWindow instance;
    private JTextArea editor;

    private StatusWindow() {
        super(LanguageManager.getString("2_1"));

        // Get Application Icon
        setIconImage(Utils.getIcon(ColloquiaConstants.iconAppIcon).getImage());

        getContentPane().setLayout(new BorderLayout());

        editor = new JTextArea();
        editor.setEditable(false);
        editor.setCursor(ColloquiaConstants.textCursor);
        getContentPane().add(new JScrollPane(editor), BorderLayout.CENTER);

        JMenuBar menuBar = constructMenuBar();
        setJMenuBar(menuBar);
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        macKludge = (Utils.getOS() == Utils.MACINTOSH);

        // re-direct system output to the Status window
        if(ColloquiaConstants.STATUS_WINDOW) {
            try {
                PrintStream output = new PrintStream(new DumpStream(), true);
                PrintStream err = new PrintStream(new ErrorStream(), true);
                System.setOut(output);
                System.setErr(err);
            }
            catch (Exception e) { }
        }

        String version = LanguageManager.getString("VERSION");

        System.out.println(ColloquiaConstants.APP_NAME + " " + version + ":\t" + ColloquiaConstants.VERSION + " (" + ColloquiaConstants.BUILD_DATE + ")");
        System.out.println("Java " + version + ":\t\t" + System.getProperties().getProperty("java.version"));
        System.out.println(LanguageManager.getString("2_2") + ":\t\t" + Utils.parseDate(Utils.getNow()));
        System.out.println(LanguageManager.getString("2_3") + ":\t\t" + System.getProperties().getProperty("user.home"));
        System.out.println(LanguageManager.getString("2_4") + ":\t\t" + System.getProperties().getProperty("user.name"));
        System.out.println();
    }

    public static StatusWindow newInstance() {
        instance = new StatusWindow();
        return instance;
    }

    public static StatusWindow getInstance() {
        return instance == null ? newInstance() : instance;
    }

    /**
    * An OutputStream that redirects all System output to the Status Window
    */
    private class DumpStream extends OutputStream {
        String s;

        public DumpStream() {
        }

        public void write(int b) {
            s = String.valueOf((char)(b & 255));
            // Kludge time!
            if(macKludge && (s.equals(ColloquiaConstants.CR) || s.equals("\r"))) {
                editor.append("\r\n");
            }
            else editor.append(s);
            editor.setCaretPosition(editor.getText().length());
        }
    }

    /**
    * An OutputStream that redirects all Error output to the Status Window
    * and error log
    */
    private class ErrorStream extends DumpStream {
        File file;
        FileWriter writer;
        boolean dateWritten;
        boolean canWrite = true;

        public ErrorStream() {
            openFile();
        }

        public void write(int b) {
           if(canWrite) {
                try {
                    if(!dateWritten) {
                        dateWritten = true;
                        writeDate();
                    }
                    writer.write(b);
                    writer.flush();
                }
                catch(IOException ex) {
                    canWrite = false;
                    //System.err.println("Could not write to error log: " + ex);
                }
            }

            super.write(b);
        }

        private void openFile() {
            String folder = DataFiler.getColloquiaFolder();
            String logName = "log.log";
            file = new File(folder, logName);
            try {
                // If file size > 128k, rename it
                if(file.length() > (1024*128)) {
                    Date date = new Date();
                    File tmp = new File(folder + "log" + date.getTime() + ".log");
                    file.renameTo(tmp);
                    file = new File(folder + logName);
                }
                writer = new FileWriter(file.getPath(), true);
            }
            catch(IOException ex) {
                canWrite = false;
                //System.err.println("Could not open error log: " + ex);
            }
        }

        private void writeDate() throws IOException {
            writer.write("-----------------------------------------------------------------------------" + ColloquiaConstants.CR);
            writer.write(ColloquiaConstants.APP_NAME + " version:\t" + ColloquiaConstants.VERSION + " (" + ColloquiaConstants.BUILD_DATE + ")" + ColloquiaConstants.CR);
            writer.write("Java version:\t\t" + System.getProperties().getProperty("java.version") + ColloquiaConstants.CR);
            writer.write("Started at:\t\t" + Utils.parseDate(Utils.getNow()) + ColloquiaConstants.CR);
            writer.write("-----------------------------------------------------------------------------" + ColloquiaConstants.CR);
            writer.write(ColloquiaConstants.CR);
            writer.flush();
        }
    }

    public void showWindow() {
        setVisible(true);
    }

    public static void printTrace(String message) {
        boolean trace = UserPrefs.getUserPrefs().getBooleanProperty(UserPrefs.STATUS_MESSAGES);
        if(trace) {
            System.out.println(message);
        }
    }

    private JMenuBar constructMenuBar() {
     	JMenuBar mb = new JMenuBar();
      	JMenuItem menuItem;

      	JMenu fileMenu = mb.add(new JMenu(LanguageManager.getString("FILE")));
        // Clear
        menuItem = fileMenu.add(new JMenuItem(LanguageManager.getString("CLEAR")));
        menuItem.addActionListener(new Action_Clear());
        // Save text to file
        menuItem = fileMenu.add(new JMenuItem(LanguageManager.getString("SAVE_TO_FILE") + "..."));
        menuItem.addActionListener(new Action_Save());

        return mb;
    }

    private class Action_Clear extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            editor.setText("");
        }
    }

    private class Action_Save extends AbstractAction {
        private JFileChooser chooser;
        private BufferedWriter out;
        String fileName;

        public void actionPerformed(ActionEvent e) {
            chooser = new JFileChooser();
            int returnVal = chooser.showSaveDialog(getInstance());
            if(returnVal != JFileChooser.APPROVE_OPTION) return;
            fileName = chooser.getSelectedFile().getPath();
            try {
                out = new BufferedWriter(new FileWriter(fileName));
                out.write(editor.getText());
                out.flush();
                out.close();
            }
            catch (IOException ex) {
                System.out.println("Status log save error " + ex);
            }
        }
    }
}


