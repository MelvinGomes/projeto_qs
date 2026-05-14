package trabalho_qs.melvini.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data // O Lombok já cria os Getters e Setters automático pra gente
@Document(collection = "usuarios")
public class Usuario {
    @Id
    private String id;
    private String username; // O login do mano
    private String password; // A senha (que a gente vai criptografar depois)
    private String role;     // Permissão, ex: "ROLE_USER"
}