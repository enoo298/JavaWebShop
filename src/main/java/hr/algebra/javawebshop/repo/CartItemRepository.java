package hr.algebra.javawebshop.repo;

import hr.algebra.javawebshop.models.CartItem;
import hr.algebra.javawebshop.models.Product;
import hr.algebra.javawebshop.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    Optional<CartItem> findByUserAndProduct(User user, Product product);
}