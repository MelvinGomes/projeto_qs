package trabalho_qs.melvini.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import trabalho_qs.melvini.model.Livro;
import trabalho_qs.melvini.repository.LivroRepository;
import trabalho_qs.melvini.model.Usuario;
import trabalho_qs.melvini.repository.UsuarioRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class LivroControllerTest {

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LivroRepository livroRepository;

    // A MÁGICA: O teste acha que o zé ruela tá logado graças a essa annotation de cima
    @Test
    @WithMockUser(username = "zepipoca") 
    @DisplayName("Caixa Preta - Deve acessar os Livros de forma Autenticada e Redirecionar Status 200")
    public void testAcessarListaLivrosQuandoLogado() throws Exception {
        
        // Pega o cara no sistema pra amarrar nos Livros no Fake test:
        Usuario u = new Usuario();
        u.setUsername("zepipoca");
        u.setPassword("segredo");
        usuarioRepository.save(u);

        // Batendo na Rota e validando se veio O FrontEnd Certo
        mockMvc.perform(get("/livros"))
               .andExpect(status().isOk())
               .andExpect(view().name("livros"));
    }

    @Test
    @WithMockUser(username = "mariateste")
    @DisplayName("Parametrizado/Frontend - Não pode cadastrar livro vazio pela WEB")
    public void testSalvarLivroVazioNaoDeveSalvar() throws Exception {
        // Mesma treta
        Usuario u = new Usuario();
        u.setUsername("mariateste");
        usuarioRepository.save(u);

        mockMvc.perform(post("/livros")
                .param("titulo", "")     // <--- NOME VAZIO ERRADO INTENCIONAL
                .param("autor", "Kafka")
                .param("status", "LIDO")
                .with(csrf())) 
               .andExpect(status().isOk())   
               .andExpect(view().name("livros")); // Ele retorna p mesma tela na view quando da validação
    }
}