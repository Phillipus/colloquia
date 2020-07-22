package net.colloquia.util;

import java.io.*;

import net.colloquia.*;
import net.colloquia.gui.*;

public class AppLauncher {
	private static final int os;

	/**
	 * The first parameter that needs to be passed into Runtime.exec() to open the default web
	 * browser on Windows.
	 */
    private static final String FIRST_WINDOWS_PARAMETER = "/c";

    /**
     * The second parameter for Runtime.exec() on Windows.
     */
    private static final String SECOND_WINDOWS_PARAMETER = "start";

    /**
     * The third parameter for Runtime.exec() on Windows.  This is a "title"
     * parameter that the command line expects.  Setting this parameter allows
     * URLs containing spaces to work.
     * Yeah - but it doesn't work on Win98!  PB
     */
    private static final String THIRD_WINDOWS_PARAMETER = "\"\"";

	static {
        os = Utils.getOS();
	}

    public static boolean run(String cmd) {

        switch(os) {
            case Utils.NT:
            case Utils.WINDOWS:
                // Windows
                try {
                    Process p = Runtime.getRuntime().exec("rundll32.exe url.dll,FileProtocolHandler " + cmd);
                    try {
                        // wait for exit code -- if it's 0, command worked,
                        int exitCode = p.waitFor();
                        return (exitCode == 0);
                    }
                    catch(InterruptedException x) {
                        StatusWindow.printTrace("AppLauncher error: " + cmd);
                        return false;
                    }
                }
                catch(java.io.IOException ex) {
                    StatusWindow.printTrace("Cannot run: " + cmd);
                    return false;
                }

            case Utils.MACINTOSH:
                try {
                    // This works for files too
                    BrowserLauncher.openURL(cmd);
                }
                catch(IOException ex) {
                    System.out.println("AppLauncher error: " + cmd);
                    return false;
                }
                return true;

            case Utils.UNIX:
                try {
                    //System.out.println(cmd);
                    Process p = Runtime.getRuntime().exec(cmd);
                    try {
                        // wait for exit code -- if it's 0, command worked,
                        int exitCode = p.waitFor();
                        return (exitCode == 0);
                    }
                    catch(InterruptedException x) {
                        StatusWindow.printTrace("AppLauncher error: " + cmd);
                        return false;
                    }
                }
                catch(java.io.IOException ex) {
                    StatusWindow.printTrace("Cannot run: " + cmd);
                    return false;
                }

            default:
                return false;
        }
    }




    /**
     * A Simple way of launching the user's application
    */
    public static void launchApp(String appName, String fileName) {
        // Add quotes
        fileName = "\"" + fileName + "\"";
        try {
            Runtime rt = Runtime.getRuntime();
            rt.exec(appName + " " + fileName, null);
        }
        catch (Exception ex) {
            MainFrame.getInstance().statusBar.setText(LanguageManager.getString("13_1") + ": " + ex);
        }
    }

    private AppLauncher() {}
}

