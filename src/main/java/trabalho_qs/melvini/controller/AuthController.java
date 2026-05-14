package trabalho_qs.melvini.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import trabalho_qs.melvini.model.Usuario;
import trabalho_qs.melvini.repository.UsuarioRepository;

@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Mostra a tela de login
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Chama o login.html
    }

    // Mostra a tela de cadastro
    @GetMapping("/cadastro")
    public String cadastroPage(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro"; // Chama o cadastro.html
    }

    // Recebe os dados do form de cadastro e salva no banco
    @PostMapping("/cadastro")
    public String salvarUsuario(Usuario usuario) {
        // Se o mano já existir, previne dar B.O. (poderia ter uma mensagem de erro na tela dps)
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            return "redirect:/cadastro?error";
        }
        
        // Criptografa a senha antes de salvar no Mongo
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRole("ROLE_USER");
        usuarioRepository.save(usuario);
        
        // Cadastrou? Manda pro login
        return "redirect:/login?sucesso";
    }
}