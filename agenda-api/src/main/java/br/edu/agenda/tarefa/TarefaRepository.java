package br.edu.agenda.tarefa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class TarefaRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<Tarefa> mapper = this::mapear;

    public TarefaRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<Tarefa> listar(Status status) {
        return status == null
                ? jdbc.query("SELECT * FROM tarefas ORDER BY id DESC", mapper)
                : jdbc.query("SELECT * FROM tarefas WHERE status = ? ORDER BY id DESC", mapper, status.name());
    }

    public Optional<Tarefa> buscar(long id) {
        return jdbc.query("SELECT * FROM tarefas WHERE id = ?", mapper, id).stream().findFirst();
    }

    public long inserir(TarefaEntrada entrada, OffsetDateTime agora) {
        var chave = new GeneratedKeyHolder();
        jdbc.update(conexao -> {
            var comando = conexao.prepareStatement("""
                INSERT INTO tarefas (nome, descricao, status, observacoes, data_criacao, data_atualizacao)
                VALUES (?, ?, ?, ?, ?, ?)
                """, new String[]{"id"});
            comando.setString(1, entrada.nome());
            comando.setString(2, entrada.descricao());
            comando.setString(3, entrada.status().name());
            comando.setString(4, entrada.observacoes());
            comando.setObject(5, agora);
            comando.setObject(6, agora);
            return comando;
        }, chave);
        return java.util.Objects.requireNonNull(chave.getKey()).longValue();
    }

    public int atualizar(long id, TarefaEntrada entrada, OffsetDateTime agora) {
        return jdbc.update("""
            UPDATE tarefas SET nome = ?, descricao = ?, status = ?, observacoes = ?, data_atualizacao = ?
            WHERE id = ?
            """, entrada.nome(), entrada.descricao(), entrada.status().name(), entrada.observacoes(), agora, id);
    }

    public int excluir(long id) { return jdbc.update("DELETE FROM tarefas WHERE id = ?", id); }

    private Tarefa mapear(ResultSet linha, int indice) throws SQLException {
        return new Tarefa(linha.getLong("id"), linha.getString("nome"), linha.getString("descricao"),
                Status.valueOf(linha.getString("status")), linha.getString("observacoes"),
                linha.getObject("data_criacao", OffsetDateTime.class),
                linha.getObject("data_atualizacao", OffsetDateTime.class));
    }
}
