package hr.algebra.javawebshop.services;

import hr.algebra.javawebshop.models.Role;
import hr.algebra.javawebshop.models.User;
import hr.algebra.javawebshop.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository    userRepo;
    private final PasswordEncoder   passwordEncoder;

    public UserService(UserRepository userRepo,
                       PasswordEncoder passwordEncoder) {
        this.userRepo        = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User getUser(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }


    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    public User register(User data) {
        if (userRepo.existsByEmail(data.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        data.setRole(Role.USER);
        data.setPassword(passwordEncoder.encode(data.getPassword()));
        return userRepo.save(data);
    }

    public User createUser(User data) {
        return register(data);
    }

    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    public User updateUser(Long id, User data) {
        User existing = getUser(id);
        existing.setUsername(data.getUsername());
        existing.setEmail(data.getEmail());
        if (data.getPassword() != null && !data.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(data.getPassword()));
        }
        return userRepo.save(existing);
    }

    public void deleteUser(Long id) {
        if (!userRepo.existsById(id)) {
            throw new IllegalArgumentException("User not found");
        }
        userRepo.deleteById(id);
    }
    public User getByUsernameOrEmail(String id) {
        return userRepo.findByUsername(id)
                .or(() -> userRepo.findByEmail(id))
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + id));
    }


    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }
}
