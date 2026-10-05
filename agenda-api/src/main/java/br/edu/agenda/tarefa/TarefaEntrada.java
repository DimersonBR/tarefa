package br.edu.agenda.tarefa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TarefaEntrada(
        @NotBlank(message = "Informe o nome da tarefa") @Size(max = 120) String nome,
        @NotBlank(message = "Informe a descricao") @Size(max = 2000) String descricao,
        @NotNull(message = "Informe o status") Status status,
        @Size(max = 2000) String observacoes) { }
