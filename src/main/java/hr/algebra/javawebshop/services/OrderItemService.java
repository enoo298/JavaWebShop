package hr.algebra.javawebshop.services;

import hr.algebra.javawebshop.models.Order;
import hr.algebra.javawebshop.models.OrderItem;
import hr.algebra.javawebshop.repo.OrderItemRepository;
import hr.algebra.javawebshop.repo.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderItemService {

    @Autowired
    private OrderItemRepository itemRepo;

    @Autowired
    private OrderRepository orderRepo;

    public List<OrderItem> getAllOrderItems() {
        return itemRepo.findAll();
    }

    public List<OrderItem> getOrderItemsByOrder(Long orderId) {
        Order order = orderRepo.findById(orderId).orElse(null);
        if (order == null) return List.of();
        return itemRepo.findByOrder(order);
    }

    public OrderItem getOrderItemById(Long id) {
        return itemRepo.findById(id).orElse(null);
    }

    public OrderItem createOrderItem(OrderItem item) {
        return itemRepo.save(item);
    }

    public OrderItem updateOrderItem(Long id, OrderItem data) {
        OrderItem existing = itemRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));
        existing.setQuantity(data.getQuantity());
        existing.setPriceAtPurchase(data.getPriceAtPurchase());
        return itemRepo.save(existing);
    }

    public void deleteOrderItem(Long id) {
        if (!itemRepo.existsById(id)) {
            throw new RuntimeException("OrderItem not found");
        }
        itemRepo.deleteById(id);
    }
}
