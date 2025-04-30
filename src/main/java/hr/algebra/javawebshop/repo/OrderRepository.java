package hr.algebra.javawebshop.repo;

import hr.algebra.javawebshop.models.Order;
import hr.algebra.javawebshop.models.PaymentMethod;
import hr.algebra.javawebshop.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    List<Order> findByPaymentMethod(PaymentMethod method);
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Order> findByUserAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end);
}
