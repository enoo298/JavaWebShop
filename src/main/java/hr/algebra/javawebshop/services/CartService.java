package hr.algebra.javawebshop.services;

import hr.algebra.javawebshop.models.CartItem;
import hr.algebra.javawebshop.models.Product;
import hr.algebra.javawebshop.models.User;
import hr.algebra.javawebshop.repo.CartItemRepository;
import hr.algebra.javawebshop.repo.ProductRepository;
import hr.algebra.javawebshop.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CartService {

    private final CartItemRepository cartRepo;
    private final ProductRepository  prodRepo;
    private final UserRepository     userRepo;

    public CartService(CartItemRepository cartRepo,
                       ProductRepository prodRepo,
                       UserRepository userRepo) {
        this.cartRepo = cartRepo;
        this.prodRepo = prodRepo;
        this.userRepo = userRepo;
    }

    public List<CartItem> getItems(Long userId) {
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return cartRepo.findByUser(u);
    }

    public CartItem addItem(Long userId, Long productId, int qty) {
        User u       = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Product prod = prodRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        return cartRepo.findByUserAndProduct(u, prod)
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + qty);
                    return cartRepo.save(existing);
                })
                .orElseGet(() -> {
                    CartItem ci = new CartItem();
                    ci.setUser(u);
                    ci.setProduct(prod);
                    ci.setQuantity(qty);
                    return cartRepo.save(ci);
                });
    }

    public CartItem updateItem(Long itemId, int qty) {
        CartItem ci = cartRepo.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        ci.setQuantity(qty);
        return cartRepo.save(ci);
    }

    public void removeItem(Long itemId) {
        cartRepo.deleteById(itemId);
    }

    public void clearCart(Long userId) {
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        cartRepo.deleteAll(cartRepo.findByUser(u));
    }

    public void mergeSession(Long userId, List<CartItem> sessionItems) {
        sessionItems.forEach(ci ->
                addItem(userId, ci.getProduct().getId(), ci.getQuantity())
        );
    }
}
