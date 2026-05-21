package trabalho_qs.melvini.service;

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

import trabalho_qs.melvini.model.Usuario;
import trabalho_qs.melvini.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@SpringBootTest
@Testcontainers
public class CustomUserDetailsServiceTest {

    @Container
    public static MongoDBContainer mongo = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void configs(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @Autowired
    private CustomUserDetailsService service;

    @Autowired
    private UsuarioRepository repository;

    @Test
    @DisplayName("Bombando Cobertura 100%: Testa Achou Usuario e Testa Quando Toma Erro (Sem usuario)")
    public void testPuxandoDadosCaminhosDeSucessoEError() {
        Usuario u = new Usuario();
        u.setUsername("xambao");
        u.setPassword("senhaPIX");
        u.setRole("ROLE_USER");
        repository.save(u);

        // Caminho feliz: 
        UserDetails usuarioRecuperado = service.loadUserByUsername("xambao");
        Assertions.assertEquals("xambao", usuarioRecuperado.getUsername());

        // Caminho triste (Força o código de NotFound passar no relatório do Jacoco):
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername("melo-escondido");
        });
    }

    @Test
    @DisplayName("Cobre ramo 1 e 2 IF Branch: Usando usuário c permissao Totalmente Vazia Pra cobrir falses")
    public void cobreIfRamosNulosDoJaCocoRoleInexistentesNelasTest() {
        Usuario fulanoX = new Usuario();
        fulanoX.setUsername("nulao-monarkao");
        fulanoX.setPassword("zimbabaouee");
        fulanoX.setRole(null); // MARCA DO PÊNALTI!: Isso OBRIGA bater cara na falha/falsa ( ? ou o outro Ramo de Roles Nulas) caindo User
        repository.save(fulanoX);

        UserDetails usuarioAbertoMagicaNul = service.loadUserByUsername("nulao-monarkao");
        
        // Garante e valida se recupera bonitinho, já preencheu a branch na Mágica dos Testes
        Assertions.assertEquals("nulao-monarkao", usuarioAbertoMagicaNul.getUsername());
    }
}