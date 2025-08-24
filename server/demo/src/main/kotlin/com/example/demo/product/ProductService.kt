package com.example.demo.product

import com.example.demo.product.dto.ProductRequest
import com.example.demo.product.dto.ProductResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
@Transactional
class ProductService(
    private val productRepository: ProductRepository
) {
    
    fun createProduct(request: ProductRequest): ProductResponse {
        val product = Product(
            name = request.name,
            description = request.description,
            price = request.price,
            stockQuantity = request.stockQuantity,
            category = request.category,
            brand = request.brand,
            imageUrl = request.imageUrl,
            isActive = request.isActive
        )
        
        val savedProduct = productRepository.save(product)
        return ProductResponse.from(savedProduct)
    }
    
    @Transactional(readOnly = true)
    fun getProduct(id: Long): ProductResponse {
        val product = productRepository.findById(id)
            .orElseThrow { RuntimeException("제품을 찾을 수 없습니다. ID: $id") }
        
        return ProductResponse.from(product)
    }
    
    @Transactional(readOnly = true)
    fun getAllProducts(): List<ProductResponse> {
        return productRepository.findByIsActiveTrue()
            .map { ProductResponse.from(it) }
    }
    
    @Transactional(readOnly = true)
    fun getProductsByCategory(category: String): List<ProductResponse> {
        return productRepository.findByCategoryAndIsActiveTrue(category)
            .map { ProductResponse.from(it) }
    }
    
    @Transactional(readOnly = true)
    fun getProductsByBrand(brand: String): List<ProductResponse> {
        return productRepository.findByBrandAndIsActiveTrue(brand)
            .map { ProductResponse.from(it) }
    }
    
    @Transactional(readOnly = true)
    fun searchProducts(keyword: String): List<ProductResponse> {
        return productRepository.searchByKeyword(keyword)
            .map { ProductResponse.from(it) }
    }
    
    @Transactional(readOnly = true)
    fun getProductsByPriceRange(minPrice: BigDecimal, maxPrice: BigDecimal): List<ProductResponse> {
        return productRepository.findByPriceBetweenAndIsActiveTrue(minPrice, maxPrice)
            .map { ProductResponse.from(it) }
    }
    
    fun updateProduct(id: Long, request: ProductRequest): ProductResponse {
        val existingProduct = productRepository.findById(id)
            .orElseThrow { RuntimeException("제품을 찾을 수 없습니다. ID: $id") }
        
        val updatedProduct = existingProduct.copy(
            name = request.name,
            description = request.description,
            price = request.price,
            stockQuantity = request.stockQuantity,
            category = request.category,
            brand = request.brand,
            imageUrl = request.imageUrl,
            isActive = request.isActive,
            updatedAt = LocalDateTime.now()
        )
        
        val savedProduct = productRepository.save(updatedProduct)
        return ProductResponse.from(savedProduct)
    }
    
    fun deleteProduct(id: Long) {
        val product = productRepository.findById(id)
            .orElseThrow { RuntimeException("제품을 찾을 수 없습니다. ID: $id") }
        
        productRepository.delete(product)
    }
    
    fun deactivateProduct(id: Long): ProductResponse {
        val product = productRepository.findById(id)
            .orElseThrow { RuntimeException("제품을 찾을 수 없습니다. ID: $id") }
        
        val deactivatedProduct = product.copy(
            isActive = false,
            updatedAt = LocalDateTime.now()
        )
        
        val savedProduct = productRepository.save(deactivatedProduct)
        return ProductResponse.from(savedProduct)
    }
} 