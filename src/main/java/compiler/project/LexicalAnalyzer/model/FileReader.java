package compiler.project.LexicalAnalyzer.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

public class FileReader {
    private PushbackInputStream input;

    public FileReader(String filePath) throws IOException {
        input = new PushbackInputStream(new FileInputStream(filePath), 1);
    }

    public int readChar() throws IOException {
        return input.read();
    }
    public void unreadChar(int ch) throws IOException {
        if (ch != -1) { // فقط اگر انتهای فایل نیست
            input.unread(ch);
        }
    }

    public void close() throws IOException {
        if (input != null) {
            input.close();
        }
    }
}