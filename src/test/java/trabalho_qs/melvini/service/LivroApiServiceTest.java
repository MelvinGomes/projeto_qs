package trabalho_qs.melvini.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
public class LivroApiServiceTest {

    // Instancia nosso Servidor VCR Falsificado (A Mágica da parada que subirá um Servidor Web Invisível localzinho na porta 8089)
    private static WireMockServer gravadorVcrServer;

    @BeforeAll
    static void iniciarGravador() {
        gravadorVcrServer = new WireMockServer(8089);
        gravadorVcrServer.start();
    }

    @AfterAll
    static void desligarGravador() {
        gravadorVcrServer.stop();
    }

    // Essa linha troca aquela API da URL Original pelo nosso RoboVcr WireMock
    @DynamicPropertySource
    static void botarAMascaraNaUrl(DynamicPropertyRegistry registry) {
        registry.add("livro.api.url", () -> "http://localhost:8089/fakebooks");
    }

    @Autowired
    private LivroApiService apiService;

    @Test
    @DisplayName("Teste VCR: Deve consultar info no serviço web mas responder pela gravação interna.")
    public void testConsultarDadosDaAPIWebFake() {
        // GRAVA A FITA DO VCR NO ROBOZÃO ("QUANDO EU CHAMAR A URL ACIMA, ME ENVIE ESTE JSON MANUAL COMO MENTIRA DO SITE OFICIAL")
        gravadorVcrServer.stubFor(WireMock.get(WireMock.urlEqualTo("/fakebooks?q=isbn:999"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"titulos_achados\": \"As Crônicas VCR do Senac\" }")));

        // AGORA EXECUTA DO MUNDO REAL DO TEU CONTROLLER, CHAMANDO SEM ELE SABER QUE NÃO EXISTE:
        String informacaoWebResult = apiService.buscarInformacoesDoLivroNaGringa("999");
        
        // RESULTADO FINAL, O TEXTINHO DA MENTIRA VEIO PELA CAIXINHA
        Assertions.assertTrue(informacaoWebResult.contains("As Crônicas VCR do Senac"));
    }
}