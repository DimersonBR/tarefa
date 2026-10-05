# Agenda API

Backend para organizar tarefas do dia a dia, sem login e sem usuarios. Implementacao em Java 17 e Spring Boot 3.5.5, com PostgreSQL e acesso ao banco por Spring JDBC. SQL fica explicito no repositorio; o servico concentra transacoes e regras, e o controller cuida do HTTP.

## Executar

Requisitos: JDK 17 ou superior e PostgreSQL (ou Docker Compose para o banco). O Maven Wrapper incluido baixa o Maven automaticamente no primeiro uso; e necessario acesso a internet para as dependencias. Tambem e possivel usar Maven instalado.

1. Inicie o banco com `docker compose up -d`. Se usar PostgreSQL instalado, execute `database/criar-banco.sql` no banco `postgres` como administrador. Depois execute `database/schema.sql` conectado ao banco `agenda` como usuario `agenda`.
2. Execute `.\mvnw.cmd spring-boot:run` no PowerShell, na raiz deste projeto. No Linux/macOS, use `sh mvnw spring-boot:run`.
3. A API fica em `http://localhost:8080/api/tarefas`.

A aplicacao tambem executa o schema ao iniciar, com `CREATE TABLE IF NOT EXISTS`. As credenciais do Compose sao para uso local. Para configurar outra instancia, defina `DB_URL`, `DB_USER` e `DB_PASSWORD`. `PORT` altera a porta HTTP.

Exemplo PowerShell:

```powershell
$env:DB_URL = 'jdbc:postgresql://localhost:5432/agenda'
$env:DB_USER = 'agenda'
$env:DB_PASSWORD = 'agenda_local'
.\mvnw.cmd spring-boot:run
```

## Endpoints

| Metodo | Rota | Resultado |
| --- | --- | --- |
| POST | /api/tarefas | Cria tarefa; HTTP 201 e cabecalho Location |
| GET | /api/tarefas | Lista por ID decrescente |
| GET | /api/tarefas?status=PENDENTE | Filtra por status |
| GET | /api/tarefas/{id} | Consulta uma tarefa |
| PUT | /api/tarefas/{id} | Substitui os campos editaveis |
| DELETE | /api/tarefas/{id} | Exclui; HTTP 204 |

Corpo para POST e PUT:

```json
{
  "nome": "Revisar conteudo",
  "descricao": "Resolver os exercicios de Java",
  "status": "PENDENTE",
  "observacoes": "Separar uma hora"
}
```

Nome e descricao sao obrigatorios, com limites de 120 e 2000 caracteres. Status e obrigatorio e aceita `PENDENTE`, `EM_ANDAMENTO` ou `CONCLUIDA`. Observacoes sao opcionais (ate 2000 caracteres). O backend remove espacos nas extremidades dos textos. ID e datas sao gerados pelo sistema; a criacao e preservada ao editar e a atualizacao recebe o instante da alteracao, em UTC. PUT exige todos os campos obrigatorios novamente.

Dados invalidos retornam 400. IDs ausentes retornam 404. Erros seguem Problem Details; erros de validacao incluem o objeto `campos`.

Exemplo de uso no PowerShell:

```powershell
$corpo = @{ nome = 'Estudar'; descricao = 'Revisar SQL'; status = 'PENDENTE'; observacoes = 'Aula 2' } | ConvertTo-Json
$tarefa = Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/api/tarefas' -ContentType 'application/json' -Body $corpo
Invoke-RestMethod -Uri 'http://localhost:8080/api/tarefas'
$corpo = @{ nome = 'Estudar'; descricao = 'Revisar SQL'; status = 'CONCLUIDA'; observacoes = 'Finalizado' } | ConvertTo-Json
Invoke-RestMethod -Method Put -Uri "http://localhost:8080/api/tarefas/$($tarefa.id)" -ContentType 'application/json' -Body $corpo
Invoke-RestMethod -Method Delete -Uri "http://localhost:8080/api/tarefas/$($tarefa.id)"
```

## Testes e build

```text
.\mvnw.cmd test
.\mvnw.cmd package
java -jar target/agenda-api-1.0.0.jar
```

Testes unitarios verificam normalizacao e tarefa ausente. Testes de integracao sobem o contexto Spring e exercitam HTTP, servico e SQL com H2 em modo PostgreSQL: CRUD, filtro, datas, validacoes e IDs ausentes. O banco de testes e isolado e nao exige PostgreSQL instalado. Isso nao substitui uma verificacao de compatibilidade com PostgreSQL real.

## Arquivos principais

- `src/main/java/br/edu/agenda/tarefa`: modelo, entrada validada, repositorio JDBC, servico e controller.
- `src/main/java/br/edu/agenda/ErroHandler.java`: tratamento de validacao.
- `database/criar-banco.sql`: criacao do usuario e banco.
- `database/schema.sql`: script de tabelas e indice para entrega.
- `src/main/resources/db/schema.sql`: mesmo schema usado na inicializacao.
- `compose.yaml`: PostgreSQL local com volume persistente.

Antes de entregar, leia o codigo e execute os exemplos para entender cada camada. O enunciado pede entrega individual pelo GitHub; este projeto nao cria repositorio nem faz commits.
