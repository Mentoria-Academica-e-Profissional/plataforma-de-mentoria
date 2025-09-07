package br.edu.ufape.plataforma.mentoria.service;

import br.edu.ufape.plataforma.mentoria.model.User;
import br.edu.ufape.plataforma.mentoria.dto.UserDTO;
import br.edu.ufape.plataforma.mentoria.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByEmail(username);
    }

    public User register(UserDTO userDTO){
        UserDetails userDetails = this.userRepository.findByEmail(userDTO.getEmail());

        if(userDetails != null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já existe no banco de dados!");
        }
        String encriptedPassword = this.passwordEncoder.encode(userDTO.getPassword());

        User user = new User(userDTO.getEmail(), encriptedPassword, userDTO.getRole());
        return this.userRepository.save(user);

    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (email.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
        }
        return this.userRepository.findByEmail(email);
    }

}
