package net.colloquia.comms.ftp;

public interface FTPListener {
    void commandSent(String cmd);
    boolean bytesPut(int bytes);
    boolean bytesGot(int bytes);
}
