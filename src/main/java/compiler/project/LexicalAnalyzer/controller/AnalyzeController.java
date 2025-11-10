package compiler.project.LexicalAnalyzer.controller;

import compiler.project.LexicalAnalyzer.FileRequest;
import compiler.project.LexicalAnalyzer.ResponseDto;
import compiler.project.LexicalAnalyzer.model.Token;
import compiler.project.LexicalAnalyzer.service.AnalyzeService;
import jakarta.persistence.Table;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping(value = "/api/analyze")
public class AnalyzeController {
    AnalyzeService analyzeService;

    public AnalyzeController(AnalyzeService analyzeService) {
        this.analyzeService = analyzeService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> fileSave(@RequestBody FileRequest input) {
        this.analyzeService.fileSaver(input.getInput());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> analyze() throws IOException {
        File file = new File("D:\\w\\LexicalAnalyzer\\data\\input.txt");

        if (!file.exists()) {
            return ResponseEntity.badRequest().body("❌ فایل مورد نظر یافت نشد: " + file.getAbsolutePath());
        }

        List<ResponseDto> tokens = analyzeService.analyze(file);

        if (tokens == null || tokens.isEmpty()) {
            return ResponseEntity.badRequest().body("⚠️ هیچ توکنی یافت نشد.");
        }

        return ResponseEntity.ok(tokens);
    }

@PostMapping
public ResponseEntity<?> initKeyWord(@RequestBody List<String> input) {
    analyzeService.initKeyWords(input);
    return ResponseEntity.ok().build();
}
}
