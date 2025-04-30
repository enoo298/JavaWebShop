package hr.algebra.javawebshop.repo;

import hr.algebra.javawebshop.models.Order;
import hr.algebra.javawebshop.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder(Order order);
    List<Payment> findByStatus(String status);
}
