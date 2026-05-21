# 📚 Melvini - Gerenciador de Biblioteca Pessoal

![Java 21](https://img.shields.io/badge/Java-21-blue.svg?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-success.svg?logo=spring-boot)
![MongoDB](https://img.shields.io/badge/MongoDB-6.0-green.svg?logo=mongodb)
![Coverage](https://img.shields.io/badge/Coverage-93%25-brightgreen.svg?logo=jacoco)
![Status](https://img.shields.io/badge/Status-Concluído-success)

Projeto final para a disciplina de **Qualidade de Software**. O **Melvini** é uma aplicação completa focada em robustez, testabilidade e nas melhores práticas do mercado, fugindo de *Mocks* tradicionais de banco para entregar Testes de Integração de altíssimo nível.

---

## Objetivo e Funcionalidades

O sistema foi desenhado na arquitetura **MVC (Model-View-Controller)** para gerenciar a coleção de livros dos usuários de forma segura e privada (cada usuário possui acesso apenas aos seus dados). 

- **Autenticação Segura:** Login, Registro e Controle de Sessão com `Spring Security` e senha encriptada via `BCryptPasswordEncoder`.
- **CRUD Completo de Livros:** Cadastro (C), Listagem (R), Edição de status (U) e Exclusão (D) isolado por sessão autenticada.
- **Camada Visual:** Desenvolvida em `HTML5` utilizando renderização no backend pelo `Thymeleaf` + estilização Bootstrap 5.
- **Acesso a API Externa:** Rota dedicada a consultar metadados. (Em ambiente de testes, o serviço utiliza Mock Web/VCR).

---

## Padrões de Qualidade e Estratégia de Testes

De acordo com o edital do projeto, a estratégia proíbe expressamente o uso de simuladores convencionais (`@Mock`) para validação de dados em persistência, exigindo containers e validações orgânicas na porta web HTTP (Caixa Preta) atingindo mínima de 80% de cobertura *(atingimos +90%)*. 

Nossas garantias são entregues utilizando:

* **Testcontainers + Docker:** No disparo de um build maven ou execução remota de testes, instanciamos na marra a ISO original de um Servidor de Banco MongoDB (`MongoDBContainer`). Salvamos dados vivos para as validações e destruímos a engine no final dos asserts de *Caixa Branca/Integração*.
* **VCR com WireMock:** Nossa infraestrutura substitui a rede mundial simulando respostas (`JSON StubFor / aResponse`) da comunicação do servidor externo por requisições locais limpas usando Web Server nativo fake (`WireMockServer`).
* **CI Automático via Github Actions:** O script YAML assegura na branch main os pipelines provendo o Status do Teste, além da validação automática para artefatos gerados pelo `JaCoCo (Relatório de Cobertura)`.

---

## Tecnologias Adotadas

| Camada             | Tecnologia Principal                   |
| ------------------ | ---------------------------------------|
| **Base/Plataforma**| Java 21 LTS + Maven                     |
| **Core & API Web** | Spring Boot 3.2 (Web, Security, Data)  |
| **Interface / View**| Thymeleaf + Bootstrap + CSS Puro     |
| **Database**       | MongoDB / NoSQL                        |
| **Engenharia QS**  | JUnit 5, JaCoCo, Testcontainers, WireMock|
| **Pipeline/CI**    | GitHub Actions                         |

---

## Como Rodar este Projeto?

### Pré-requisitos
Ter instalado no sistema operacional: 
- `Git` para o versionamento e dowload da aplicação;
- `Docker Desktop` ativo e executando na máquina.

### Passos de Instalação (Uso normal do sistema):

1. **Baixar Repositório:**
   ```bash
   git clone https://github.com/SeuUsuario/melvini.git
   cd melvini
   ```

2. **Ativar MongoDB Original e Subir Motor do Spring (Servidor Online local):**
   Execute num terminal de confiança na porta clássica 27017:
   ```bash
   docker run -d -p 27017:27017 --name banco-melvini mongo:6.0
   ```
   No terminal da raiz do projeto, inicie o backend:
   ```bash
   mvn spring-boot:run
   ```

3. **Acesso:** Abra no Navegador `http://localhost:8080`.

---

## Validando a Qualidade de Código (E2E, Integração e Testcontainers)

Para testar todas as funcionalidades executando 100% de forma automática (o código levantará containers isolados, testará e os destruirá):

```bash
mvn clean test
```

Verificando o **Relatório Exato de Cobertura (JaCoCo)**:
Abra o arquivo gerado localizado em `target/site/jacoco/index.html` em seu navegador para validar as métricas aprovadas no CI.