package compiler.project.LexicalAnalyzer.exception;


public class LexicalAnalysisException extends RuntimeException {
    private String token;

    public LexicalAnalysisException(String message ,String token) {
        super(message);
        this.token = token;
    }


    @Override
    public String getMessage() {
        return super.getMessage() + " at line " + " -> \"" + token + "\"";
    }
}
