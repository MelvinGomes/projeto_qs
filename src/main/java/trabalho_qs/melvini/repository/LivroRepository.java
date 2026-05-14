package trabalho_qs.melvini.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import trabalho_qs.melvini.model.Livro;
import java.util.List;

public interface LivroRepository extends MongoRepository<Livro, String> {
    // Quando o usuário logar, a gente puxa só os livros dele com esse método:
    List<Livro> findByUsuarioId(String usuarioId);
}