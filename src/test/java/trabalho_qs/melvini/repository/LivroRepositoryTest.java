package trabalho_qs.melvini.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import trabalho_qs.melvini.model.Livro;

import java.util.List;

@SpringBootTest
@Testcontainers // O selo de aprovação do professor!
public class LivroRepositoryTest {

    // Sobe um MongoDB no Docker EXCLUSIVO pra rodar esse teste
    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    // Conecta o Spring nesse banco temporário do Docker
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private LivroRepository livroRepository;

    @Test
    @DisplayName("Caixa Branca/Integração: Deve salvar um livro e buscar pelo ID do usuário")
    public void testDeveSalvarEBuscarLivro() {
        // 1. Prepara o cenário (Dado que...)
        Livro livro = new Livro();
        livro.setTitulo("O Senhor dos Anéis");
        livro.setAutor("Tolkien");
        livro.setStatus("LENDO");
        livro.setUsuarioId("user123"); // Simulando um dono pro livro

        // 2. Ação (Quando...)
        livroRepository.save(livro);

        // 3. Verificação (Então...)
        List<Livro> livrosDoUsuario = livroRepository.findByUsuarioId("user123");
        
        Assertions.assertFalse(livrosDoUsuario.isEmpty(), "A lista de livros não deveria vir vazia!");
        Assertions.assertEquals("O Senhor dos Anéis", livrosDoUsuario.get(0).getTitulo());
    }
}