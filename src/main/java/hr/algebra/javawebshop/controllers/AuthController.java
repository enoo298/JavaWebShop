package hr.algebra.javawebshop.controllers;

import hr.algebra.javawebshop.models.User;
import hr.algebra.javawebshop.services.UserService;
import hr.algebra.javawebshop.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class AuthController {

    private final UserService  userService;
    private final OrderService orderService;

    public AuthController(UserService userService,
                          OrderService orderService) {
        this.userService  = userService;
        this.orderService = orderService;
    }



    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }



    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegistration(
            @ModelAttribute("user") @Valid User user,
            BindingResult br
    ) {
        if (userService.existsByEmail(user.getEmail())) {
            br.rejectValue("email", "error.user", "Email already registered");
        }
        if (br.hasErrors()) {
            return "auth/register";
        }

        userService.createUser(user);
        return "redirect:/login?registered";
    }



    @GetMapping("/profile")
    public String viewProfile(Model model, Principal principal) {
        User me = userService.getUserByEmail(principal.getName());
        model.addAttribute("user", me);
        return "auth/profile";
    }


    @GetMapping("/profile/orders")
    public String viewOrderHistory(Model model, Principal principal) {
        User me = userService.getUserByEmail(principal.getName());
        model.addAttribute("orders", orderService.getOrdersByUser(me.getId()));
        return "auth/order-history";
    }
}
