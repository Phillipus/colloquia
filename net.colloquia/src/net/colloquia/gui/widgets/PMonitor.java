package net.colloquia.gui.widgets;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.util.*;


/**
 * My Progress Monitor based on Sun's - this one doesn't have all the waiting for
 * time millis nonsense and you can reset it to start over from the beginning.
 */
public class PMonitor {
    private JDialog         dialog;
    private JOptionPane     pane;
    private JProgressBar    myBar;
    private JLabel          messageLabel;
    private JLabel          noteLabel;
    private String          message;
    private String          note;
    private Object[]        cancelOption = null;
    private int             max;
    private int             progress;

    /**
    * Constructor - specify an initial dialog title, message and note
    */
    public PMonitor(Component parentComponent, String title, String message, String note) {
        if(title == null) title = " ";
        if(message == null) message = " ";
        if(note == null) note = " ";

        cancelOption = new Object[1];
        //cancelOption[0] = UIManager.getString("OptionPane.cancelButtonText");
        cancelOption[0] = LanguageManager.getString("CANCEL");
        myBar = new JProgressBar();
        myBar.setPreferredSize(new Dimension(300, myBar.getPreferredSize().height));
        messageLabel = new JLabel(message);
        noteLabel = new JLabel(note);

        pane = new ProgressOptionPane(new Object[] {messageLabel, noteLabel, myBar});
        dialog = pane.createDialog(parentComponent, title);
        dialog.setVisible(true);
    }

    /**
    * Reset the PMonitor to start from zero with same message and note
    */
    public void init(int max) {
        //if(max == 0) close();
        this.max = max;
        myBar.setMaximum(max);
        setProgress(0, false);
    }

    /**
    * Reset the PMonitor to start from zero with new message and note
    */
    public void init(String message, String note, int max) {
        init(max);
        setMessage(message);
        setNote(note);
    }

    /**
    * Set the progress
    */
    private void setProgress(int progress, boolean closeOnMax) {
        if(myBar != null) {
            this.progress = progress;
            myBar.setValue(progress);
            if(closeOnMax && (progress >= max)) close();
        }
    }

    /**
    * Increment the progress by by inc amount
    * If closeOnmax is true the PMonitor will close when max is reached
    */
    public void incProgress(int inc, boolean closeOnMax) {
        setProgress(progress + inc, closeOnMax);
    }

    public void close() {
        if(dialog != null) {
            dialog.setVisible(false);
            dialog.dispose();
            dialog = null;
        }
        pane = null;
        myBar = null;
    }

    public void setMessage(String message) {
        this.message = message;
        messageLabel.setText(message == null ? " " : message);
    }

    public void setNote(String note) {
        this.note = note;
        noteLabel.setText(note == null ? " " : note);
    }

    public boolean isCanceled() {
        if(pane == null) return false;
        Object v = pane.getValue();
        return ((v != null) && (cancelOption.length == 1) && (v.equals(cancelOption[0])));
    }


    private class ProgressOptionPane extends JOptionPane {

        ProgressOptionPane(Object messageList) {
            super(messageList,
                  JOptionPane.INFORMATION_MESSAGE,
                  JOptionPane.DEFAULT_OPTION,
                  Utils.getIcon(ColloquiaConstants.iconAppIcon),
                  PMonitor.this.cancelOption,
                  null);
        }


        public int getMaxCharactersPerLineCount() {
            return 80;
        }


        // Equivalent to JOptionPane.createDialog,
        // but create a modeless dialog.
        // This is necessary because the Solaris implementation doesn't
        // support Dialog.setModal yet.
        public JDialog createDialog(Component parentComponent, String title) {
            Frame frame = JOptionPane.getFrameForComponent(parentComponent);
            final JDialog dialog = new JDialog(frame, title, false);
            Container contentPane = dialog.getContentPane();

            contentPane.setLayout(new BorderLayout());
            contentPane.add(this, BorderLayout.CENTER);
            dialog.pack();
            dialog.setLocationRelativeTo(parentComponent);
            dialog.addWindowListener(new WindowAdapter() {
                boolean gotFocus = false;

                public void windowClosing(WindowEvent we) {
                    setValue(null);
                }

                public void windowActivated(WindowEvent we) {
                    // Once window gets focus, set initial focus
                    if (!gotFocus) {
                        selectInitialValue();
                        gotFocus = true;
                    }
                }
            });

            addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent event) {
                    if(dialog.isVisible() &&
                       event.getSource() == ProgressOptionPane.this &&
                       (event.getPropertyName().equals(VALUE_PROPERTY) ||
                        event.getPropertyName().equals(INPUT_VALUE_PROPERTY))){
                        dialog.setVisible(false);
                        dialog.dispose();
                    }
                }
            });
            return dialog;
        }
    }
}
