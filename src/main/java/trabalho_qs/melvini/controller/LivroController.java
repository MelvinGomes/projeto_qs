package trabalho_qs.melvini.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import trabalho_qs.melvini.model.Livro;
import trabalho_qs.melvini.model.Usuario;
import trabalho_qs.melvini.repository.LivroRepository;
import trabalho_qs.melvini.repository.UsuarioRepository;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/livros")
public class LivroController {

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Método utilitário pra pegar o mano que tá logado
    private Usuario getUsuarioLogado(Principal principal) {
        return usuarioRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado no banco!"));
    }

    // Carrega a tela principal com os livros do usuário
    @GetMapping
    public String listarLivros(Model model, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);
        
        // Puxa do Mongo SÓ os livros desse cara
        List<Livro> meusLivros = livroRepository.findByUsuarioId(usuario.getId());

        model.addAttribute("livros", meusLivros);
        model.addAttribute("novoLivro", new Livro()); // Objeto vazio pro formulário
        model.addAttribute("nomeUsuario", usuario.getUsername()); // Pra dar um "Salve" na tela
        
        return "livros"; // Chama livros.html
    }

    // Salva o livro novo
    @PostMapping
    public String salvarLivro(@Valid @ModelAttribute("novoLivro") Livro livro, BindingResult result, Principal principal, Model model) {
        Usuario usuario = getUsuarioLogado(principal);

        // Se o cara tentar mandar vazio, o @Valid pega e o BindingResult barra
        if (result.hasErrors()) {
            // Se deu erro, recarrega a página com as mensagens em vermelho
            model.addAttribute("livros", livroRepository.findByUsuarioId(usuario.getId()));
            model.addAttribute("nomeUsuario", usuario.getUsername());
            return "livros";
        }

        // Se tá tudo certo, vincula o livro ao usuário logado e salva!
        livro.setUsuarioId(usuario.getId());
        livroRepository.save(livro);

        return "redirect:/livros";
    }

    // Rota pra CARREGAR a telinha com as info originais do livro antes de editar
    @GetMapping("/editar/{id}")
    public String carregarTelaEdicao(@PathVariable String id, Principal principal, Model model) {
        Usuario usuario = getUsuarioLogado(principal);
        Livro livro = livroRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Erro. Livro fantasma?"));

        // Regra de segurança: Tá tentando editar o livro de outra pessoa? Bloqueia o mano!
        if (!livro.getUsuarioId().equals(usuario.getId())) {
            return "redirect:/livros?error=acesso-negado";
        }

        model.addAttribute("livro", livro);
        return "editar-livro";
    }

    // Rota que RECEBE o livro arrumadinho do form e SALVA em cima do antigo
    @PostMapping("/editar/{id}")
    public String atualizarLivro(@PathVariable String id, @Valid @ModelAttribute("livro") Livro livroModificado, BindingResult result, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);

        // Retorna a tela com a caixa vermelha de erro se tentou bolar titulo vazio de sacanagem:
        if (result.hasErrors()) {
            return "editar-livro";
        }

        Livro livroAntigo = livroRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Erro"));

        if (livroAntigo.getUsuarioId().equals(usuario.getId())) {
            livroAntigo.setTitulo(livroModificado.getTitulo());
            livroAntigo.setAutor(livroModificado.getAutor());
            livroAntigo.setStatus(livroModificado.getStatus());
            livroRepository.save(livroAntigo);
        }

        return "redirect:/livros";
    }

    // Deleta o livro
    @GetMapping("/deletar/{id}")
    public String deletarLivro(@PathVariable String id) {
        livroRepository.deleteById(id);
        return "redirect:/livros";
    }
}