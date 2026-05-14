package trabalho_qs.melvini.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;

@Data
@Document(collection = "livros")
public class Livro {
    @Id
    private String id;

    // Esse @NotBlank já serve pra Qualidade: não deixa salvar livro sem título!
    @NotBlank(message = "O título não pode ser vazio, truta!") 
    private String titulo;

    @NotBlank(message = "Põe o nome do autor aí!")
    private String autor;

    private String status; // Ex: "LENDO", "LIDO", "QUERO LER"

    private String usuarioId; // O pulo do gato: serve pra saber de qual usuário é esse livro
}