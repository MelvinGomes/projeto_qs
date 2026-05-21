package trabalho_qs.melvini.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LivroApiService {

    // Essa variável vai puxar nossa URL verdadeira lá no servidor do Google API Books ou API externa (qualquer lugar que retorne livros)
    @Value("${livro.api.url:https://www.googleapis.com/books/v1/volumes}")
    private String apiUrl;

    public String buscarInformacoesDoLivroNaGringa(String isbnBusca) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            // Dá o comando: O Google Web pesquise esse nome!
            String resultadoDaApiExterna = restTemplate.getForObject(apiUrl + "?q=isbn:" + isbnBusca, String.class);
            return resultadoDaApiExterna;
        } catch (Exception e) {
            return "Erro: Sistema externo está fora do ar";
        }
    }
}