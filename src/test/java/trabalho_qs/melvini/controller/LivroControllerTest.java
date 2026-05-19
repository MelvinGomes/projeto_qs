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

    @Test
    @WithMockUser(username = "milagreso10") 
    @DisplayName("Força a Entrar dados Redondinhos e Depois usar do controller o botaozinho DELETE")
    public void salvarComSucessoEDepoisDeletaBagulho() throws Exception {
        Usuario u = new Usuario();
        u.setUsername("milagreso10");
        usuarioRepository.save(u);

        // O POST Bonitinho cheio com o Jacoco indo de Save(): 
        mockMvc.perform(post("/livros")
                .param("titulo", "Harry Potter")
                .param("autor", "Mulher da vassoura")
                .param("status", "LENDO")
                .with(csrf())) 
               .andExpect(status().is3xxRedirection()); // Pula por causa de Redireção normal p Index;
        
        // Catação bruta para simular DELEÇÃO / DeleteByID: 
       Livro testzinhoSohSeForDeleta= livroRepository.findByUsuarioId(u.getId()).get(0); 
       String acharQualIpFoiSalvoAgorinha = testzinhoSohSeForDeleta.getId();
        
        mockMvc.perform(get("/livros/deletar/" + acharQualIpFoiSalvoAgorinha))
               .andExpect(status().is3xxRedirection()); 
    }

    @Test
    @WithMockUser(username = "romarioreidosite") 
    @DisplayName("Teste da Funcionalidade Inédita UPDATE Completa! Tem q Salvar Por Cima!")
    public void testaTodoOCaminhoDoEditarOuroDaMassa() throws Exception {
        Usuario cara = new Usuario();
        cara.setUsername("romarioreidosite");
        // Garantindo as instâncias reais das bases sem bater findAll as Cegas do Jacoco
        Usuario caraSalvo = usuarioRepository.save(cara);

        Livro l = new Livro();
        l.setTitulo("Eu odeio ir de Onibus");
        l.setAutor("Baixinho das passagens");
        l.setUsuarioId(caraSalvo.getId());
        l.setStatus("LIDO");
        Livro romarioCriadoComSegurancaAteNaUrlDb = livroRepository.save(l); 
        // 🔥 A MÁGICA PRA NÃO BUGAR O TEST: ESCOLHENDO SÓ ESSE AKI DA SESSÃO!
        String ipDeMalucoUnicoAqui = romarioCriadoComSegurancaAteNaUrlDb.getId(); 

        // Rumo à Felicidade Status 200..:
        mockMvc.perform(get("/livros/editar/" + ipDeMalucoUnicoAqui))
               .andExpect(status().isOk())
               .andExpect(view().name("editar-livro"));

        // Salvar Rumo Felicidade dnv ! 
        mockMvc.perform(post("/livros/editar/" + ipDeMalucoUnicoAqui)
               .param("titulo", "Comprei uma Ferrari Vermelha")
               .param("autor", "Rei Romario 222")
               .param("status", "QUERO LER")
               .with(csrf())) 
               .andExpect(status().is3xxRedirection()); 

        // Tentativa 157 (Editando O ID do outro Cara Inofensivo) =
        Usuario safado = new Usuario(); 
        safado.setUsername("hackerzinho1337");
        Usuario hackGerouSalvoMizeriS= usuarioRepository.save(safado); 

        Livro l2 = new Livro();
        l2.setTitulo("O Invesivavel Livro!");
        l2.setUsuarioId(hackGerouSalvoMizeriS.getId()); 
        l2.setAutor("Desconhecidoooo");
        Livro l2MisteriosoMlcInfectDoZeD = livroRepository.save(l2);

        // BARRADO NO BAILE !! E ESBARRA CORRETO PRO STATUS IS.3x !
        mockMvc.perform(get("/livros/editar/" + l2MisteriosoMlcInfectDoZeD.getId()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "zeh_malandrovsk_do_editao")
    @DisplayName("Branch Maximo Edição IFs 2-Caminhos : Postando com Fórmularios Vazios ERRO + Hacker Tentando Enviar IDs Que Cês nao Dão Conta do Zé!")
    public void limpaTrilhosNoEditarEValidaTelasVaziasNosCamposProIfBranchesSoberboVingarAContaLadoErradooDaEditacaoPorPOSTZadaBrutaA() throws Exception {
        Usuario reiZ = new Usuario();
        reiZ.setUsername("zeh_malandrovsk_do_editao");
        reiZ = usuarioRepository.save(reiZ);

        Livro livretoLegalDaSilvaDdeleOriginalNoDatabaseeHackerTentarNaoEditareskrrIdTrocavel = new Livro();
        livretoLegalDaSilvaDdeleOriginalNoDatabaseeHackerTentarNaoEditareskrrIdTrocavel.setTitulo("Fé no Pix Karai!");
        livretoLegalDaSilvaDdeleOriginalNoDatabaseeHackerTentarNaoEditareskrrIdTrocavel.setAutor("Agostinho");
        livretoLegalDaSilvaDdeleOriginalNoDatabaseeHackerTentarNaoEditareskrrIdTrocavel.setUsuarioId(reiZ.getId());
        Livro slvddsPeloHhMitoDeCrias2CrrntosHkck = livroRepository.save(livretoLegalDaSilvaDdeleOriginalNoDatabaseeHackerTentarNaoEditareskrrIdTrocavel);

        // TESTE O ERRO PARA BARRAR NO HAS.ERROR O RETORNO SEM SUCESSÃO NO Controller, TELA DE EDITA! (Faltou título krl?? Barrando pelo Validation if!!) :
        mockMvc.perform(post("/livros/editar/" + slvddsPeloHhMitoDeCrias2CrrntosHkck.getId())
               .param("titulo", "") 
               .param("autor", "Pega as Info Vazioo e Me Responda Vazio Kkk Tela de erroooo no post brrre")
               .param("status", "LIDO")
               .with(csrf())) 
               .andExpect(status().isOk())
               .andExpect(view().name("editar-livro"));

        // HACKUDOS TENTANDO QUEBRA TUDOO O VIZINHO EM SECRETS E FALHA REDIR NO Controller Oculto SEM SAVE(Acesso bloquea nos == Equals das ids Diferente de UsuarioSesseio X Alvo. CADE o If c/ false indo Reto? 
        Usuario menóCrenteDaRua = new Usuario();
        menóCrenteDaRua.setUsername("irmaoo-alvo");
        menóCrenteDaRua = usuarioRepository.save(menóCrenteDaRua); // Criando Pote na estante Do Menor kkk Alvão de POST fake e Malicioso da Internet...
        
        Livro coitadoNoforumNaWEB = new Livro();
        coitadoNoforumNaWEB.setTitulo("As lógicas das API - SUCESSOR DO NADA");
        coitadoNoforumNaWEB.setAutor("Silvao Dev Da Tarde - ALVO PRA QUEBRA!");
        coitadoNoforumNaWEB.setUsuarioId(menóCrenteDaRua.getId()); 
        Livro objDovittimsSlvvKklKzInjecooF = livroRepository.save(coitadoNoforumNaWEB);

        // Disparo e Acao pro Back!! Eu 'Zeh_malandrovesks' forçando editar livroID  que tá  vinculando do IP/USER pro Irmão menózim de longe !! Bota o If !LivrosUsuario !.. a ignorar isso Cego! Falsa Condition: Passa na Tela direto cega !  "     
        mockMvc.perform(post("/livros/editar/" + objDovittimsSlvvKklKzInjecooF.getId())
               .param("titulo", "INJETADO: TROCANDO TÍTULO DOS PARCERINHOS PRA MOSTRAR MEUS MOCk MVC HAAKRS VCR VINGADAO!") 
               .param("autor", "INJETOU FALSIDADES MUKINHAS DOS HAKEIRUS POR ISSSO TOME NA FALAA E VAA DIREETOO PARA STATUS! 3 REDIC.. REGER")
               .param("status", "QUERO LER")
               .with(csrf())) 
               .andExpect(status().is3xxRedirection()); // Pelo return redicionário que passa nas costas por Falta c/ de Salvação de Cegueta em falsess sem que ele chegue de save().
    }
}