package com.example.StockFlow.service;


import com.example.StockFlow.dto.request.ProductRequest;
import com.example.StockFlow.dto.response.ProductResponse;
import com.example.StockFlow.entity.Product;
import com.example.StockFlow.mapper.ProductMapper;
import com.example.StockFlow.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // CREATE
    public ProductResponse createProduct(ProductRequest requestDTO) {
        if (productRepository.existsBySku(requestDTO.getSku())) {
            throw new IllegalArgumentException("Un produit avec ce SKU existe déjà");
        }

        Product product = productMapper.toEntity(requestDTO);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    // READ - Get by ID
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'id: " + id));
        return productMapper.toResponse(product);
    }

    // READ - Get by SKU
    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec le SKU: " + sku));
        return productMapper.toResponse(product);
    }

    // READ - Get All
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return productMapper.toResponseList(products);
    }

    // READ - Get by Category
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(String category) {
        List<Product> products = productRepository.findByCategory(category);
        return productMapper.toResponseList(products);
    }

    // READ - Get by Active Status
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByActiveStatus(Boolean active) {
        List<Product> products = productRepository.findByActive(active);
        return productMapper.toResponseList(products);
    }

    // READ - Search by Name
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProductsByName(String name) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        return productMapper.toResponseList(products);
    }

    // UPDATE
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'id: " + id));

        // Vérifier si le SKU est modifié et s'il existe déjà
        if (!product.getSku().equals(request.getSku()) &&
                productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("Un produit avec ce SKU existe déjà");
        }

        productMapper.updateEntityFromDTO(request, product);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    // DELETE
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Produit non trouvé avec l'id: " + id);
        }
        productRepository.deleteById(id);
    }

    // Soft Delete (désactiver le produit)
    public ProductResponse deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'id: " + id));

        product.setActive(false);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    // Réactiver le produit
    public ProductResponse activateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'id: " + id));

        product.setActive(true);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }
}