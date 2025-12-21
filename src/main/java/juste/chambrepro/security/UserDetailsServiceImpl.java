package juste.chambrepro.security;

import juste.chambrepro.entity.Client;
import juste.chambrepro.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ClientRepository clientRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Client non trouvé avec l'email : " + email));

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + client.getRole().name());

        return User.builder()
                .username(client.getEmail())
                .password(client.getMotDePasse())
                .authorities(Collections.singleton(authority))
                .build();
    }
}