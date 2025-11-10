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
        System.out.println("in service  initKeyWords");
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

            System.out.println(" file saved successfully" + filePath.toAbsolutePath());

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
            if (ch == -1) {
                break;
            }
            if ((char) ch == '\n' || (char) ch == '\r' || (char) ch == '\t' || (char) ch == ' ')
                continue;
            token = this.arithmatichOpToken((char) ch);
            if (token != null) {
                tokens.add(token);
                continue;
            }
            token = this.relationalOpToken((char) ch);
            if (token != null) {
                tokens.add(token);
                continue;
            }
            token = this.assignOpToken((char) ch);
            if (token != null) {
                tokens.add(token);
                continue;
            }
            token = this.openPToken((char) ch);
            if (token != null) {
                tokens.add(token);
                continue;
            }
            token = this.closePToken((char) ch);
            if (token != null) {
                tokens.add(token);
                continue;
            }
            token = this.openBToken((char) ch);
            if (token != null) {
                tokens.add(token);
                continue;
            }
            ;
            token = this.closeBToken((char) ch);
            if (token != null) {
                tokens.add(token);
                continue;
            }
            ;
            token = this.scToken((char) ch);
            if (token != null) {
                tokens.add(token);
                continue;
            }
            ;
            token = this.numberToken((char) ch);
            if (token != null) {
                tokens.add(token);
                continue;
            }
            ;
            token = this.idAndKeywordToken((char) ch);
            if (token != null) {
                tokens.add(token);
            } else
                throw new IllegalArgumentException("bad word");
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
            stringBuilder.append(fileReader.readChar());
            if (stringBuilder.toString().equals("==")) {
                token.setAttribute("==");
            } else
                assignOpToken(character);
        } else if (stringBuilder.toString().equals("!")) {
            stringBuilder.append(fileReader.readChar());
            if (stringBuilder.toString().equals("!="))
                token.setAttribute("!=");
            else
                return null; // throw exception
        } else if (stringBuilder.toString().equals(">")) {
            stringBuilder.append(fileReader.readChar());
            if (stringBuilder.toString().equals(">="))
                token.setAttribute(">=");
            else
                token.setAttribute(">");
        } else if (stringBuilder.toString().equals("<")) {
            stringBuilder.append(fileReader.readChar());
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
        stringBuilder.append(character);
        if (isDigit(character)) {
            String s = isNumber((char) fileReader.readChar());
            stringBuilder.append(s);
            if (fileReader.readChar() == '.') {
                stringBuilder.append(".");
                String s1 = isNumber((char) fileReader.readChar());
                stringBuilder.append(s1);
            }
            if (fileReader.readChar() == 'E') {
                stringBuilder.append("E");
                char ch = (char) fileReader.readChar();
                if (ch == '+' || ch == '-') {
                    stringBuilder.append(ch);
                    String s2 = isNumber((char) fileReader.readChar());
                    stringBuilder.append(s2);
                }
            } else {
                token.setAttribute(stringBuilder.toString());
                return token;
            }

        } else
            return null;
        return token;
    }

    public String isNumber(char character) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        while (isDigit(character)) {
            stringBuilder.append(character);
            character = (char) fileReader.readChar();
        }
        fileReader.unreadChar(stringBuilder.charAt(stringBuilder.length() - 1));
        return stringBuilder.toString();
    }

    public String isWord(char character) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(character);
        while (isLetter(character)) {
            stringBuilder.append(character);
        }
        fileReader.unreadChar(stringBuilder.charAt(stringBuilder.length() - 1));
        return stringBuilder.toString();
    }

    public String isNumberOrWord(char character) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(character);
        while (isLetter(character) ||  isDigit(character)) {
            stringBuilder.append(character);
        }
        fileReader.unreadChar(stringBuilder.charAt(stringBuilder.length() - 1));
        return stringBuilder.toString();
    }

    public Token idAndKeywordToken(char character) throws IOException {
        Token token = new Token();
        StringBuilder stringBuilder = new StringBuilder();
        if (isLetter(character)) {
            stringBuilder.append(character);
            String s = isNumberOrWord(character);
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
