package hr.algebra.javawebshop.services;

import hr.algebra.javawebshop.models.CartItem;
import hr.algebra.javawebshop.models.Order;
import hr.algebra.javawebshop.models.OrderItem;
import hr.algebra.javawebshop.models.PaymentMethod;
import hr.algebra.javawebshop.models.User;
import hr.algebra.javawebshop.repo.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepo;
    private final CartService     cartService;

    public OrderService(OrderRepository orderRepo,
                        CartService cartService) {
        this.orderRepo   = orderRepo;
        this.cartService = cartService;
    }

    public Order placeOrder(Long userId, PaymentMethod method) {
        List<CartItem> cart = cartService.getItems(userId);
        if (cart.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(cart.get(0).getUser());
        order.setPaymentMethod(method);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> items = cart.stream().map(ci -> {
            OrderItem oi = new OrderItem();
            oi.setProduct(ci.getProduct());
            oi.setQuantity(ci.getQuantity());
            oi.setPriceAtPurchase(ci.getProduct().getPrice());
            oi.setOrder(order);
            return oi;
        }).toList();
        order.setOrderItems(items);

        BigDecimal total = items.stream()
                .map(i -> i.getPriceAtPurchase()
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(total);

        Order saved = orderRepo.save(order);
        cartService.clearCart(userId);
        return saved;
    }

    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    public Order getOrder(Long id) {
        return orderRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    public List<Order> getOrdersByUser(Long userId) {
        User u = savedOrThrow(userId);
        return orderRepo.findByUser(u);
    }

    public List<Order> getOrdersByPeriod(LocalDateTime from, LocalDateTime to) {
        return orderRepo.findByCreatedAtBetween(from, to);
    }

    public List<Order> getOrdersByUserAndPeriod(Long userId,
                                                LocalDateTime from,
                                                LocalDateTime to) {
        User u = savedOrThrow(userId);
        return orderRepo.findByUserAndCreatedAtBetween(u, from, to);
    }

    public List<Order> getOrdersByPaymentMethod(PaymentMethod method) {
        return orderRepo.findByPaymentMethod(method);
    }

    private User savedOrThrow(Long userId) {
        return orderRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getUser();
    }
}
