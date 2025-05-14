package hr.algebra.javawebshop.controllers;

import hr.algebra.javawebshop.models.CartItem;
import hr.algebra.javawebshop.models.Product;
import hr.algebra.javawebshop.models.User;
import hr.algebra.javawebshop.services.CartService;
import hr.algebra.javawebshop.services.ProductService;
import hr.algebra.javawebshop.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.math.BigDecimal;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final ProductService productService;
    private final UserService userService;

    public CartController(CartService cartService,
                          ProductService productService,
                          UserService userService) {
        this.cartService    = cartService;
        this.productService = productService;
        this.userService    = userService;
    }

    /** Prikaz košarice, session ili DB ovisno o autentikaciji */
    @GetMapping
    public String viewCart(Model model,
                           Principal principal,
                           HttpSession session) {
        List<CartItem> items;

        if (principal != null) {
            // logirani: merge session u DB, pa dohvati iz DB
            User user = userService.getByUsernameOrEmail(principal.getName());  // :contentReference[oaicite:0]{index=0}:contentReference[oaicite:1]{index=1}
            @SuppressWarnings("unchecked")
            List<CartItem> sessionCart = (List<CartItem>) session.getAttribute("cart");
            if (sessionCart != null && !sessionCart.isEmpty()) {
                cartService.mergeSession(user.getId(), sessionCart);       // :contentReference[oaicite:2]{index=2}:contentReference[oaicite:3]{index=3}
                session.removeAttribute("cart");
            }
            items = cartService.getItems(user.getId());                   // :contentReference[oaicite:4]{index=4}:contentReference[oaicite:5]{index=5}
        } else {
            // anonimni: samo session
            @SuppressWarnings("unchecked")
            List<CartItem> sessionCart = (List<CartItem>) session.getAttribute("cart");
            if (sessionCart == null) {
                sessionCart = new ArrayList<>();
            }
            items = sessionCart;
        }

        model.addAttribute("items", items);
        BigDecimal total = items.stream()
                .map(i -> i.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("total", total);

        return "cart/cart-list";
    }

    /** AJAX: dodaj proizvod u košaricu */
    @PostMapping(value = "/add/{productId}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> addToCartAJAX(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            Principal principal,
            HttpSession session) {

        try {
            if (principal != null) {
                // logirani: spremaj u DB
                User user = userService.getByUsernameOrEmail(principal.getName());
                cartService.addItem(user.getId(), productId, quantity);      // :contentReference[oaicite:6]{index=6}:contentReference[oaicite:7]{index=7}
            } else {
                // anonimni: spremaj u session
                @SuppressWarnings("unchecked")
                List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
                if (cart == null) { cart = new ArrayList<>(); }
                Optional<CartItem> existing = cart.stream()
                        .filter(ci -> ci.getProduct().getId().equals(productId))
                        .findFirst();
                if (existing.isPresent()) {
                    existing.get().setQuantity(existing.get().getQuantity() + quantity);
                } else {
                    Product product = productService.getProduct(productId);   // :contentReference[oaicite:8]{index=8}:contentReference[oaicite:9]{index=9}
                    CartItem newItem = new CartItem();
                    newItem.setProduct(product);
                    newItem.setQuantity(quantity);
                    cart.add(newItem);
                }
                session.setAttribute("cart", cart);
            }
            return ResponseEntity.ok("{\"message\":\"Added to cart\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    /** AJAX: ažuriraj količinu */
    @PostMapping(value = "/update/{itemId}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> updateItemAJAX(
            @PathVariable Long itemId,
            @RequestParam int quantity,
            Principal principal,
            HttpSession session) {

        if (principal != null) {
            cartService.updateItem(itemId, quantity);                    // :contentReference[oaicite:10]{index=10}:contentReference[oaicite:11]{index=11}
        } else {
            @SuppressWarnings("unchecked")
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart != null) {
                for (CartItem ci : cart) {
                    if (ci.getProduct().getId().equals(itemId)) {
                        ci.setQuantity(quantity);
                        break;
                    }
                }
                session.setAttribute("cart", cart);
            }
        }
        return ResponseEntity.ok("{\"message\":\"Cart updated\"}");
    }

    /** AJAX: ukloni stavku */
    @PostMapping(value = "/remove/{itemId}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> removeItemAJAX(
            @PathVariable Long itemId,
            Principal principal,
            HttpSession session) {

        if (principal != null) {
            cartService.removeItem(itemId);                             // :contentReference[oaicite:12]{index=12}:contentReference[oaicite:13]{index=13}
        } else {
            @SuppressWarnings("unchecked")
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart != null) {
                cart.removeIf(ci -> ci.getProduct().getId().equals(itemId));
                session.setAttribute("cart", cart);
            }
        }
        return ResponseEntity.ok("{\"message\":\"Removed\"}");
    }

    /** AJAX: isprazni kompletnu košaricu */
    @PostMapping(value = "/clear", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> clearCartAJAX(
            Principal principal,
            HttpSession session) {

        if (principal != null) {
            User user = userService.getByUsernameOrEmail(principal.getName());
            cartService.clearCart(user.getId());                         // :contentReference[oaicite:14]{index=14}:contentReference[oaicite:15]{index=15}
        } else {
            session.removeAttribute("cart");
        }
        return ResponseEntity.ok("{\"message\":\"Cart cleared\"}");
    }
}
