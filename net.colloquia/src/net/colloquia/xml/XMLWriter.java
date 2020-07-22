package net.colloquia.xml;

public abstract class XMLWriter {
    protected int indent;

    public abstract void open() throws XMLWriteException;
    public abstract void close() throws XMLWriteException;

    /**
    * Literally writes line
    */
    public abstract void write(String line) throws XMLWriteException;

    /**
    * Writes line formatted (escaped)
    */
    public void writeFormat(String line) throws XMLWriteException {
	    line = XMLUtils.escape(line);
        write(line);
    }

    /**
    * Literally writes line followed by CR
    */
    public abstract void writeln(String line) throws XMLWriteException;

    /**
    * Writes line formatted (escaped) followed by CR
    */
    public void writelnFormat(String line) throws XMLWriteException {
	    line = XMLUtils.escape(line);
        writeln(line);
    }

    /**
    * Writes in format <tag>value</tag>
    * Value will be escaped by XMLUtils
    */
    public abstract void write(XMLTag xmlTag) throws XMLWriteException;

    /**
    * PENDING - Don't indent comments, PIs etc. and CDATA
    * It will only indent lines that have a < in them
    */
    protected String indent(String line) {
        // Ordinary line
        int tagPos = line.indexOf("<");
        if(tagPos == -1) return line;

        String trimmed = line.trim();

        // <?
        if(trimmed.startsWith("<?")) return line;

        // <!
        if(trimmed.startsWith("<!")) return line;

        // Find position of closing tag
        int closeTagPos = line.indexOf("</");
        if(closeTagPos == -1) closeTagPos = line.indexOf("/>");

        // closeTagPos is first position in line - </closetag>
        if(closeTagPos == 0 && indent > 0) indent--;

        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < indent; i++) sb.append(" ");
        sb.append(line);

        // No close tag
        if(closeTagPos == -1) indent++;

        return sb.toString();
    }
}
