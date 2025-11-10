package compiler.project.LexicalAnalyzer;

import org.springframework.stereotype.Component;

@Component
public class ResponseDto {
    private String name;
    private String attribute;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
}
