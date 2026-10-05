package br.edu.agenda.tarefa;

import java.time.OffsetDateTime;

public record Tarefa(Long id, String nome, String descricao, Status status,
                     String observacoes, OffsetDateTime dataCriacao,
                     OffsetDateTime dataAtualizacao) { }
