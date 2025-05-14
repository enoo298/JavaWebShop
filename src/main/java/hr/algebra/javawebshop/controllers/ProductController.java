package hr.algebra.javawebshop.controllers;

import hr.algebra.javawebshop.models.Product;
import hr.algebra.javawebshop.models.Category;
import hr.algebra.javawebshop.services.ProductService;
import hr.algebra.javawebshop.services.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService,
                             CategoryService categoryService) {
        this.productService  = productService;
        this.categoryService = categoryService;
    }


    @GetMapping("/listProducts")
    public String listProducts(Model model,
                               @RequestParam(required = false) Long categoryId) {
        List<Category> categories = categoryService.getAllCategories();
        List<Product>  products   = (categoryId != null)
                ? productService.getProductsByCategory(categoryId)
                : productService.getAllProducts();

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategoryId", categoryId);
        return "products/product-list";
    }


    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "products/product-create";
    }


    @PostMapping("/create")
    public String createProduct(@ModelAttribute Product product) {
        productService.createProduct(product);
        return "redirect:/products/listProducts";
    }


    @GetMapping("/product/{id}")
    public String showDetail(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProduct(id));
        return "products/product-detail";
    }


    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProduct(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "products/product-edit";
    }


    @PostMapping("/{id}/update")
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute Product product) {
        productService.updateProduct(id, product);
        return "redirect:/products/listProducts";
    }


    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products/listProducts";
    }
}
