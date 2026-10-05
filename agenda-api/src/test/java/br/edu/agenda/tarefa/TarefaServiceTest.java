package br.edu.agenda.tarefa;

import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {
    @Mock TarefaRepository repository;
    @InjectMocks TarefaService service;

    @Test
    void criarNormalizaTextoEPermiteObservacaoOpcional() {
        var entrada = new TarefaEntrada("  Ler  ", "  Livro  ", Status.PENDENTE, null);
        var agora = OffsetDateTime.now();
        var tarefa = new Tarefa(7L, "Ler", "Livro", Status.PENDENTE, null, agora, agora);
        when(repository.inserir(any(), any())).thenReturn(7L);
        when(repository.buscar(7L)).thenReturn(Optional.of(tarefa));
        assertThat(service.criar(entrada)).isEqualTo(tarefa);
        var normalizada = ArgumentCaptor.forClass(TarefaEntrada.class);
        verify(repository).inserir(normalizada.capture(), any());
        assertThat(normalizada.getValue()).isEqualTo(new TarefaEntrada("Ler", "Livro", Status.PENDENTE, null));
    }

    @Test
    void buscarAusenteRetorna404() {
        when(repository.buscar(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.buscar(1L)).isInstanceOfSatisfying(ResponseStatusException.class,
                erro -> assertThat(erro.getStatusCode().value()).isEqualTo(404));
    }
}
