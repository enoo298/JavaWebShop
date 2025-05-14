package hr.algebra.javawebshop.services;

import hr.algebra.javawebshop.models.Category;
import hr.algebra.javawebshop.models.Product;
import hr.algebra.javawebshop.repo.CategoryRepository;
import hr.algebra.javawebshop.repo.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository  productRepo;
    private final CategoryRepository categoryRepo;

    public ProductService(ProductRepository productRepo,
                          CategoryRepository categoryRepo) {
        this.productRepo  = productRepo;
        this.categoryRepo = categoryRepo;
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Product getProduct(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        Category cat = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        return productRepo.findByCategory(cat);
    }

    public Product createProduct(Product data) {
        // validiraj kategoriju
        Long cid = data.getCategory().getId();
        if (!categoryRepo.existsById(cid)) {
            throw new IllegalArgumentException("Category not found for id " + cid);
        }
        return productRepo.save(data);
    }

    public Product updateProduct(Long id, Product data) {
        Product existing = getProduct(id);
        existing.setName(data.getName());
        existing.setPrice(data.getPrice());
        existing.setQuantityInStock(data.getQuantityInStock());
        existing.setDescription(data.getDescription());
        if (data.getImagePath() != null && !data.getImagePath().isBlank()) {
            existing.setImagePath(data.getImagePath());
        }
        // ažuriraj kategoriju ukoliko je promijenjena
        if (!existing.getCategory().getId().equals(data.getCategory().getId())) {
            Category cat = categoryRepo.findById(data.getCategory().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            existing.setCategory(cat);
        }
        return productRepo.save(existing);
    }

    public void deleteProduct(Long id) {
        Product existing = getProduct(id);
        productRepo.delete(existing);
    }
}
