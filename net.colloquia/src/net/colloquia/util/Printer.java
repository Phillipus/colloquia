package net.colloquia.util;

import java.awt.*;
import java.util.*;

import net.colloquia.*;



public class Printer {
    // Holds user preferences between print jobs
    protected static Properties printProps = new Properties();

    protected Toolkit toolkit;
    protected PrintJob printJob;                // The print job
    protected String jobName;                   // The job name
    protected Graphics page;                    // Graphics object for current page
    protected Dimension pageSize;               // Overall Page size
    protected int pageDPI;                      // Pixels per inch
    protected Font currentFont;                 // Current font in use
    protected int rightEdge, bottomEdge;        // Inside margin
    protected int leftMargin, topMargin;        // Margins
    protected int rightMargin, bottomMargin;
    protected int pageWidth, pageHeight;
    protected int currentX, currentY;           // Current position
    protected int tabSpace;
    protected int lineHeight;
    protected FontMetrics metrics;
    protected int pageNumber;

    protected Font footerFont = new Font("SansSerif", Font.PLAIN, 8);

    public Printer(String jobName) throws PrintCancelledException {
        // This call is synchronized on the static printProps object
        // so that only one print dialog can be shown at a time.
        // The exception is thrown if the user clicks Cancel.
        toolkit = Toolkit.getDefaultToolkit();
        synchronized(printProps) {
            printJob = toolkit.getPrintJob(MainFrame.getInstance(), jobName, printProps);
        }

        if(printJob == null) throw new PrintCancelledException("User Cancelled Print Job", null);

        // Get Page Size in Pixels
        pageSize = printJob.getPageDimension();
        // Get pixels per inch (usually 72)
        pageDPI = printJob.getPageResolution();

        // Calculate margins in pixels
        leftMargin = pageDPI/2;      // 1/2"
        topMargin = pageDPI/2;       // 1/2"
        rightMargin = pageDPI/2;     // 1/2"
        bottomMargin = pageDPI/2;    // 1/2"

        currentX = leftMargin;
        currentY = topMargin;

        // Calculate edges
        rightEdge = pageSize.width - rightMargin;
        bottomEdge = pageSize.height - bottomMargin;

        // Page width, print area
        pageWidth = pageSize.width - leftMargin - rightMargin;

        // Page height, print area
        pageHeight = pageSize.height - topMargin - bottomMargin;

        // Tab Size
        tabSpace = pageWidth / 9;

        // Set a default font
        setFont(new Font("SansSerif", Font.PLAIN, 10));

        // Page Number
        pageNumber = 1;

        this.jobName = jobName;
    }


    /**
    * Print a Document
    * PENDING - Needs more work on scaling and multi-page
    */
    public void printDocument(Container c) {
        page = printJob.getGraphics();
        Graphics p = page.create(leftMargin, topMargin, pageWidth, pageHeight);
        c.printAll(p);
        close();
        //Graphics edGraphics = c.getGraphics();
    }


    /**
    * Prints text
    */
    public void printText(String str) {
        if(str == null) return;
        char c;
        int wordWidth;
        String word = "";

        for(int i = 0; i < str.length(); i++) {
            c = str.charAt(i);

            // If a CR then begin new line
            if(c == '\n') {
                printWord(word);
                word = "";
                newLine();
                continue;
            }

            // Ignore these
            else if(c == '\r') continue;

            // If a tab
            else if(c == '\t') {
                printWord(word);
                word = "";
                printTab();
                continue;
            }

            // A space - end of word
            else if(c == ' ') {
                word += " ";
                printWord(word);
                word = "";
            }

            // Else add the character to the word making sure that the word
            // doesn't exceed the page width!
            else {
                wordWidth = metrics.stringWidth(word + c);
                if(wordWidth <= pageWidth) word += c;
                else {
                    printWord(word);
                    word = "" + c;
                }
            }
        }
        // Flush the word Buffer
        printWord(word);
    }

    /**
    * Print a word
    */
    protected void printWord(String word) {
        if(word == null || word.length() == 0) return;

        // Measure the width of the word
        int wordWidth = metrics.stringWidth(word);

        // If word won't fit on end of line, start a new line
        if(currentX + wordWidth >= rightEdge) {
            // Strip off leading spaces
            word = stripLeadingSpaces(word);
            // Force new line
            newLine();
        }

        // Do we need a new page?
        if(page == null) newPage();

        page.setFont(currentFont);
        page.drawString(word, currentX, currentY);
        currentX += wordWidth;
    }


    /**
    * Print a tab
    */
    protected void printTab() {
        currentX += tabSpace - (currentX % tabSpace);
        if(currentX >= rightEdge) newLine();
    }

    /**
    *
    */
    protected void newLine() {
        currentX = leftMargin;
        currentY += lineHeight;
        // Come to a new Page
        if(currentY >= bottomEdge) {
            if(page != null) {
                page.dispose();
                page = null;    // Don't start new page yet
            }
            currentY = topMargin;
        }
    }

    /**
    * Draws a line
    */
    public void drawLine() {
        if(page == null) newPage();
        page.drawLine(leftMargin, currentY, rightEdge, currentY);
    }

    protected void newPage() {
        page = printJob.getGraphics();
        currentX = leftMargin;
        currentY = topMargin;

        // Page Number
        page.setFont(footerFont);
        page.drawString("Page " + pageNumber++, leftMargin, bottomEdge + 20);
    }

    /**
    * Set the font and calculate some stuff
    */
    public void setFont(Font font) {
        if(font != null) {
            currentFont = font;
            // getFontMetrics is deprecated
            metrics = toolkit.getFontMetrics(currentFont);
            lineHeight = metrics.getHeight();
        }
    }

    /**
    * Print the pending page (if any)
    */
    public void close() {
        if(page != null) page.dispose();
        if(printJob != null) printJob.end();
    }

    /**
    * Strip off any leading spaces
    */
    protected String stripLeadingSpaces(String str) {
        if(str == null) return str;
        int pos = 0;
        while(str.charAt(pos) == ' ' && pos < str.length()) pos++;
        return str.substring(pos);
    }
}