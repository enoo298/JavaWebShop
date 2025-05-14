package hr.algebra.javawebshop.sec;

import hr.algebra.javawebshop.models.User;
import hr.algebra.javawebshop.repo.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        User user = userRepo.findByUsername(usernameOrEmail)
                .or(() -> userRepo.findByEmail(usernameOrEmail))
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + usernameOrEmail)
                );

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),                 // principal
                user.getPassword(),                 // hashed password
                List.of(                            // authorities
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                )
        );
    }
}
