package compiler.project.LexicalAnalyzer.repository;

import compiler.project.LexicalAnalyzer.model.SymbolTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SymbolTableRepository extends JpaRepository<SymbolTable, Long> {
    SymbolTable findByName(String name);
}
