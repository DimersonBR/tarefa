package br.edu.agenda.tarefa;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class TarefaService {
    private final TarefaRepository repository;
    public TarefaService(TarefaRepository repository) { this.repository = repository; }
    public List<Tarefa> listar(Status status) { return repository.listar(status); }
    public Tarefa buscar(long id) { return repository.buscar(id).orElseThrow(this::naoEncontrada); }

    @Transactional
    public Tarefa criar(TarefaEntrada entrada) {
        long id = repository.inserir(normalizar(entrada), OffsetDateTime.now(ZoneOffset.UTC));
        return buscar(id);
    }

    @Transactional
    public Tarefa atualizar(long id, TarefaEntrada entrada) {
        if (repository.atualizar(id, normalizar(entrada), OffsetDateTime.now(ZoneOffset.UTC)) == 0) {
            throw naoEncontrada();
        }
        return buscar(id);
    }

    @Transactional
    public void excluir(long id) {
        if (repository.excluir(id) == 0) { throw naoEncontrada(); }
    }

    private TarefaEntrada normalizar(TarefaEntrada entrada) {
        return new TarefaEntrada(entrada.nome().strip(), entrada.descricao().strip(), entrada.status(),
                entrada.observacoes() == null ? null : entrada.observacoes().strip());
    }
    private ResponseStatusException naoEncontrada() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa nao encontrada");
    }
}
