package hr.algebra.javawebshop.services;

import hr.algebra.javawebshop.models.Category;
import hr.algebra.javawebshop.repo.CategoryRepository;
import hr.algebra.javawebshop.repo.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepo;
    private final ProductRepository  productRepo;

    public CategoryService(CategoryRepository categoryRepo,
                           ProductRepository productRepo) {
        this.categoryRepo = categoryRepo;
        this.productRepo  = productRepo;
    }

    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    public Category getCategory(Long id) {
        return categoryRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    public Category createCategory(Category data) {
        if (categoryRepo.findByName(data.getName()).isPresent()) {
            throw new IllegalArgumentException("Category '" + data.getName() + "' already exists");
        }
        return categoryRepo.save(data);
    }

    public Category updateCategory(Long id, Category data) {
        Category existing = getCategory(id);
        existing.setName(data.getName());
        existing.setDescription(data.getDescription());
        return categoryRepo.save(existing);
    }

    public void deleteCategory(Long id) {
        Category existing = getCategory(id);
        if (!productRepo.findByCategory(existing).isEmpty()) {
            throw new IllegalStateException("Cannot delete category with existing products");
        }
        categoryRepo.delete(existing);
    }
}
