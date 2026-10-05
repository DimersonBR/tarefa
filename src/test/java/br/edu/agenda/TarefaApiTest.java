package br.edu.agenda;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TarefaApiTest {
    @Autowired MockMvc api;
    @Autowired ObjectMapper json;
    private static final String ENTRADA = """
        {"nome":"  Estudar Java  ","descricao":"Revisar JDBC","status":"PENDENTE","observacoes":"Capitulo 3"}
        """;

    @Test
    void cicloCompletoMantemCriacaoEAtualizaCampos() throws Exception {
        var resposta = api.perform(post("/api/tarefas").contentType("application/json").content(ENTRADA))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.nome").value("Estudar Java"))
                .andReturn().getResponse();
        var criada = json.readTree(resposta.getContentAsString());
        String recurso = "/api/tarefas/" + criada.get("id").asLong();
        assertThat(resposta.getHeader("Location")).isEqualTo(recurso);
        api.perform(get(recurso)).andExpect(status().isOk());
        api.perform(get("/api/tarefas").param("status", "PENDENTE"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(criada.get("id").asInt()));
        var atualizada = json.readTree(api.perform(put(recurso).contentType("application/json").content("""
            {"nome":"Revisao final","descricao":"Concluir exercicios","status":"CONCLUIDA","observacoes":null}
            """))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("CONCLUIDA"))
                .andExpect(jsonPath("$.nome").value("Revisao final"))
                .andReturn().getResponse().getContentAsString());
        assertThat(atualizada.get("dataCriacao")).isEqualTo(criada.get("dataCriacao"));
        assertThat(java.time.OffsetDateTime.parse(atualizada.get("dataAtualizacao").asText()))
                .isAfterOrEqualTo(java.time.OffsetDateTime.parse(criada.get("dataAtualizacao").asText()));
        api.perform(get("/api/tarefas").param("status", "PENDENTE"))
                .andExpect(jsonPath("$").isEmpty());
        api.perform(delete(recurso)).andExpect(status().isNoContent());
        api.perform(get(recurso)).andExpect(status().isNotFound());
    }

    @Test
    void rejeitaNomeVazioEStatusInvalido() throws Exception {
        api.perform(post("/api/tarefas").contentType("application/json").content(
                ENTRADA.replace("  Estudar Java  ", "   ")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.campos.nome").exists());
        api.perform(post("/api/tarefas").contentType("application/json").content(
                ENTRADA.replace("PENDENTE", "INVALIDO"))).andExpect(status().isBadRequest());
        api.perform(get("/api/tarefas").param("status", "INVALIDO")).andExpect(status().isBadRequest());
    }

    @Test
    void alterarOuExcluirIdAusenteRetorna404() throws Exception {
        api.perform(put("/api/tarefas/999999").contentType("application/json").content(ENTRADA))
                .andExpect(status().isNotFound());
        api.perform(delete("/api/tarefas/999999")).andExpect(status().isNotFound());
    }
}
