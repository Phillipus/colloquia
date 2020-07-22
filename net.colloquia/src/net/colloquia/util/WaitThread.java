package net.colloquia.util;


public abstract class WaitThread extends Thread {
    static final long timeout = 10000;  

    /**                                                 
    * Wait for this thread to die
    */
    public void waitFor() {
        // Wait for the thread to die
        try {
            if(isAlive()) join(timeout);
        }
        catch(InterruptedException ex) {
            System.err.println("WaitThread WaitFor Exception");
            ex.printStackTrace();
        }
    }

}