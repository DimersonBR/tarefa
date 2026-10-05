package br.edu.agenda.tarefa;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {
    private final TarefaService service;
    public TarefaController(TarefaService service) { this.service = service; }

    @GetMapping
    public List<Tarefa> listar(@RequestParam(required = false) Status status) { return service.listar(status); }
    @GetMapping("/{id}")
    public Tarefa buscar(@PathVariable long id) { return service.buscar(id); }
    @PostMapping
    public ResponseEntity<Tarefa> criar(@Valid @RequestBody TarefaEntrada entrada) {
        Tarefa tarefa = service.criar(entrada);
        return ResponseEntity.created(URI.create("/api/tarefas/" + tarefa.id())).body(tarefa);
    }
    @PutMapping("/{id}")
    public Tarefa atualizar(@PathVariable long id, @Valid @RequestBody TarefaEntrada entrada) {
        return service.atualizar(id, entrada);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
