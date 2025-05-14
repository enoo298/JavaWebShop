package hr.algebra.javawebshop.repo;

import hr.algebra.javawebshop.models.Order;
import hr.algebra.javawebshop.models.OrderItem;
import hr.algebra.javawebshop.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
    void deleteByProduct(Product product);
}

