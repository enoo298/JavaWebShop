package hr.algebra.javawebshop.controllers;
import hr.algebra.javawebshop.models.Category;
import hr.algebra.javawebshop.services.CategoryService;
import hr.algebra.javawebshop.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    public CategoryController(CategoryService categoryService,
                              ProductService productService) {
        this.categoryService = categoryService;
        this.productService  = productService;
    }

    @GetMapping("/listCategories")
    public String list(Model m) {
        m.addAttribute("categories", categoryService.getAllCategories());
        return "categories/category-list";
    }

    @GetMapping("/create")
    public String createForm(Model m) {
        m.addAttribute("category", new Category());
        return "categories/category-create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Category c) {
        categoryService.createCategory(c);
        return "redirect:/categories/listCategories";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model m) {
        Category c = categoryService.getCategory(id);
        m.addAttribute("category", c);
        m.addAttribute("categoryProducts", productService.getProductsByCategory(id));
        return "categories/category-detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model m) {
        m.addAttribute("category", categoryService.getCategory(id));
        return "categories/category-edit";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @ModelAttribute Category c) {
        categoryService.updateCategory(id, c);
        return "redirect:/categories/listCategories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/categories/listCategories";
    }
}
