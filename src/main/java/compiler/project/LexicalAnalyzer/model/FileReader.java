package compiler.project.LexicalAnalyzer.model;

import java.io.*;

public class FileReader {
    private PushbackReader reader;

    public FileReader(String filePath) throws IOException {
        reader = new PushbackReader(
                new InputStreamReader(new FileInputStream(filePath), "UTF-8"), 1
        );
    }

    public int readChar() throws IOException {
        int ch = reader.read();
        if (ch == -1) {
            return -1;
        }
        return ch;
    }

    public void unreadChar(int ch) throws IOException {
        if (ch != -1) {
            reader.unread(ch);
        }
    }

    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }
}
