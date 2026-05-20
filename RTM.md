# Matriz de Rastreabilidade de Requisitos (RTM)

Abaixo estão os mapeamentos funcionais com cobertura > 80% comprovada no relatório do JaCoCo (arquivos gerados pelo plugin e garantidos via integração contínua). Todos utilizando Banco real c/ Testcontainers.

| ID   | Funcionalidade OBRIGATÓRIA | Descrição do Teste Executado | Status | Pacote / Classe de Teste |
| ---- | -------------------------- | ----------------------------- | ------ | ------------------------ |
| RF01 | Cadastro de Usuários       | Verifica se os form vazios falham (Erro) e duplicatas batem erro/Redirecionamento, criando certinho sem furos. | ✅ OK | `AuthControllerTest` |
| RF02 | Autenticação Seguro / Login | Testa a criptografia (ByCrypt) de usuário, carregamento viaDetailsService. Regra sem mock para User. | ✅ OK | `CustomUserDetailsServiceTest` |
| RF03 | Sessão Usuário             | Garante bloqueios/Forbidden e `WithMockUser`. Validado Acessos e Nulos, 200 pro front MVC| ✅ OK | `CustomUserDetailsServiceTest` / `AuthControllerTest` |
| RF04 | Persistir MongoDB CRUD   | Sem `mocks`, roda o *Testcontainers*. Faz Cadastrar Livro, Salva (POST), Acessa, Verifica edição maliciosa em Outros IPs | ✅ OK | `LivroControllerTest`, `LivroRepositoryTest` |
| RF05 | Acesso a API Externa VCR   | Grava Request via `Wiremock/VCR`. Consulta via Template API Retornando Certo e Mente com Stub Resposta Json | ✅ OK | `LivroApiServiceTest` |

<br>

### Diagrama de Sequência (UML) - Processo Cadastro & Busca (MVC Realidade)

```mermaid
sequenceDiagram
    actor Usuario
    participant View HTML (Thymeleaf)
    participant LivroController
    participant MongoDB
    
    Usuario->>View HTML (Thymeleaf): Insere dados novo livro 
    View HTML (Thymeleaf)->>LivroController: Dispara (POST /livros)
    LivroController->>LivroController: Checa Usuário da Sessão atual (@WithMock/Principal)
    LivroController->>MongoDB: Instancia Salvar no MongoDB
    MongoDB-->>LivroController: Document_ID: UUID Concluído (Criação do ObjectId)
    LivroController-->>View HTML (Thymeleaf): "302 REDIRECT /livros" 
    View HTML (Thymeleaf)-->>Usuario: Painel 200 OK + Visualiza