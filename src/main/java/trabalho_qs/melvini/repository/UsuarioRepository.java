package trabalho_qs.melvini.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import trabalho_qs.melvini.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    // Esse método a gente vai usar na hora do login pra achar o cara no banco
    Optional<Usuario> findByUsername(String username);
}