package compiler.project.LexicalAnalyzer.service;

import compiler.project.LexicalAnalyzer.ResponseDto;
import compiler.project.LexicalAnalyzer.model.FileReader;
import compiler.project.LexicalAnalyzer.model.SymbolTable;
import compiler.project.LexicalAnalyzer.model.Token;
import compiler.project.LexicalAnalyzer.repository.SymbolTableRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyzeService {
    private SymbolTableRepository symbolTableRepository;
    private final String path = "D:\\w\\LexicalAnalyzer\\data\\input.txt";
    private FileReader fileReader;

    public AnalyzeService(SymbolTableRepository symbolTableRepository) throws IOException {
        this.symbolTableRepository = symbolTableRepository;
        fileReader = new FileReader(path);
    }

    public void initKeyWords(List<String> keyWords) {
        for (String word : keyWords) {
            SymbolTable symbolTable = new SymbolTable();
            symbolTable.setName(word);
            this.symbolTableRepository.save(symbolTable);
        }
    }

    public void fileSaver(String input) {
        String directory = "D:\\w\\LexicalAnalyzer\\data";
        String fileName = "input.txt";

        try {
            Files.createDirectories(Paths.get(directory));

            Path filePath = Paths.get(directory, fileName);

            Files.write(filePath, input.getBytes());


        } catch (IOException e) {
            System.err.println("error" + e.getMessage());
        }
    }

    public List<ResponseDto> analyze(File input) throws IOException {
        List<Token> tokens = new ArrayList<>();
        Token token = null;
        int ch;
        while (true) {
            ch = fileReader.readChar();
            char ch1 = (char) ch;
            if (ch == -1 || ch1 == '\uFFFF') {
                break;
            }
            if (ch1 == '\n' || ch1 == '\r' || ch1 == '\t' || ch1 == ' ')
                continue;
            token = this.arithmatichOpToken(ch1);
            if (token != null) {
                tokens.add(token);
                continue;
            }
            token = this.relationalOpToken(ch1);
            if (token != null) {
                tokens.add(token);
                continue;
            }
            token = this.assignOpToken(ch1);
            if (token != null) {
                tokens.add(token);

                continue;
            }
            token = this.openPToken(ch1);
            if (token != null) {
                tokens.add(token);
                continue;
            }
            token = this.closePToken(ch1);
            if (token != null) {
                tokens.add(token);
                continue;
            }
            token = this.openBToken(ch1);
            if (token != null) {
                tokens.add(token);
                continue;
            }
            token = this.closeBToken(ch1);
            if (token != null) {
                tokens.add(token);
                continue;
            }
            token = this.scToken(ch1);
            if (token != null) {
                tokens.add(token);
                continue;
            }
            token = this.numberToken(ch1);
            if (token != null) {
                tokens.add(token);
                continue;
            }
            token = this.idAndKeywordToken(ch1);
            if (token != null) {
                tokens.add(token);
            } else
                throw new IllegalArgumentException("bad word : " + ch1);
        }
        List<ResponseDto> responseDtos = new ArrayList<>();
        for (Token token2 : tokens) {
            ResponseDto responseDto = new ResponseDto();
            responseDto.setName(token2.getName());
            responseDto.setAttribute(token2.getAttribute());
            responseDtos.add(responseDto);
        }
        return responseDtos;
    }

    public Token arithmatichOpToken(char character) {
        Token token = new Token();
        token.setName("arithOp");
        if (character == '+')
            token.setAttribute("+");
        else if (character == '-')
            token.setAttribute("-");
        else if (character == '*')
            token.setAttribute("*");
        else if (character == '/')
            token.setAttribute("/");
        else if (character == '%')
            token.setAttribute("%");
        else
            return null;
        return token;
    }

    public Token relationalOpToken(char character) throws IOException {
        Token token = new Token();
        token.setName("relationalOp");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(character);
        if (stringBuilder.toString().equals("=")) {
            stringBuilder.append((char) fileReader.readChar());
            if (stringBuilder.toString().equals("==")) {
                token.setAttribute("==");
            } else {
                token.setName(assignOpToken(character).getName());
                token.setAttribute(assignOpToken(character).getAttribute());
            }
        } else if (stringBuilder.toString().equals("!")) {
            stringBuilder.append((char) fileReader.readChar());
            if (stringBuilder.toString().equals("!="))
                token.setAttribute("!=");
            else
                return null; // throw exception
        } else if (stringBuilder.toString().equals(">")) {
            stringBuilder.append((char) fileReader.readChar());
            if (stringBuilder.toString().equals(">="))
                token.setAttribute(">=");
            else
                token.setAttribute(">");
        } else if (stringBuilder.toString().equals("<")) {
            stringBuilder.append((char) fileReader.readChar());
            if (stringBuilder.toString().equals("<="))
                token.setAttribute("<=");
            else
                token.setAttribute("<");
        } else
            return null;
        return token;
    }

    public Token assignOpToken(char character) {
        Token token = new Token();
        token.setName("assignOp");
        if (character == '=')
            token.setAttribute("=");
        else
            return null;
        return token;
    }

    public Token openPToken(char character) {
        Token token = new Token();
        token.setName("openP");
        if (character == '(')
            token.setAttribute("(");
        else
            return null;
        return token;
    }

    public Token closePToken(char character) {
        Token token = new Token();
        token.setName("closeP");
        if (character == ')')
            token.setAttribute(")");
        else
            return null;
        return token;
    }

    public Token openBToken(char character) {
        Token token = new Token();
        token.setName("openB");
        if (character == '{')
            token.setAttribute("{");
        else
            return null;
        return token;
    }

    public Token closeBToken(char character) {
        Token token = new Token();
        token.setName("closeB");
        if (character == '}')
            token.setAttribute("}");
        else
            return null;
        return token;
    }

    public Token scToken(char character) {
        Token token = new Token();
        token.setName("sc");
        if (character == ';')
            token.setAttribute(";");
        else
            return null;
        return token;
    }

    public Token numberToken(char character) throws IOException {
        Token token = new Token();
        token.setName("num");
        StringBuilder stringBuilder = new StringBuilder();
        if (isDigit(character)) {
            stringBuilder.append(character);
            String s = isNumber((char) fileReader.readChar());
            stringBuilder.append(s);
            char c =  (char) fileReader.readChar();
            if (c == '.') {
                stringBuilder.append(".");
                String s1 = isNumber((char) fileReader.readChar());
                stringBuilder.append(s1);
                c =  (char) fileReader.readChar();
            }
            if (c == 'E') {
                stringBuilder.append("E");
                char ch = (char) fileReader.readChar();
                if (ch == '+' || ch == '-') {
                    stringBuilder.append(ch);
                    String s2 = isNumber((char) fileReader.readChar());
                    stringBuilder.append(s2);
                }
            }
        } else
            return null;
        token.setAttribute(stringBuilder.toString());
        return token;
    }

    public String isNumber(char character) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        while (isDigit(character)) {
            stringBuilder.append(character);
            character = (char) fileReader.readChar();
        }
        fileReader.unreadChar(character);
        return stringBuilder.toString();
    }

    public String isWord(char character) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(character);
        while (isLetter(character)) {
            stringBuilder.append(character);
            character = (char) fileReader.readChar();
        }
        fileReader.unreadChar(character);
        return stringBuilder.toString();
    }

    public String isNumberOrWord(char character) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        while (isLetter(character) || isDigit(character)) {
            stringBuilder.append(character);
            character = (char) fileReader.readChar();
        }
        fileReader.unreadChar(character);
        return stringBuilder.toString();
    }

    public Token idAndKeywordToken(char character) throws IOException {
        Token token = new Token();
        StringBuilder stringBuilder = new StringBuilder();
        if (isLetter(character)) {
            stringBuilder.append(character);
            String s = isNumberOrWord((char) fileReader.readChar());
            stringBuilder.append(s);
        } else
            return null;
        SymbolTable symbolTable = symbolTableRepository.findByName(stringBuilder.toString());
        if (symbolTable == null) {
            symbolTable = new SymbolTable();
            symbolTable.setName(stringBuilder.toString());
            SymbolTable symbolTable1 = symbolTableRepository.save(symbolTable);
            token.setName("id");
            token.setAttribute(symbolTable1.getId().toString());
        } else {
            if (symbolTable.getName().equals("if") || symbolTable.getName().equals("for") || symbolTable.getName().equals("while")) {
                token.setName(symbolTable.getName());
                token.setAttribute(symbolTable.getId().toString());
            } else {
                token.setName("id");
                token.setAttribute(symbolTable.getId().toString());
            }
        }
        return token;
    }

    public boolean isDigit(char character) {
        return character == '0' || character == '1' || character == '2' || character == '3' || character == '4' ||
                character == '5' || character == '6' || character == '7' || character == '8' || character == '9';
    }

    public boolean isLetter(char character) {
        return (97 <= (int) character && (int) character <= 122) || (65 <= (int) character && (int) character <= 90);
    }
}
