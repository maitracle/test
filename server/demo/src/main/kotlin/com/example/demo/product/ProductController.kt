package com.example.demo.product

import com.example.demo.product.dto.ProductRequest
import com.example.demo.product.dto.ProductResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService
) {
    
    @PostMapping
    fun createProduct(@RequestBody request: ProductRequest): ResponseEntity<ProductResponse> {
        val product = productService.createProduct(request)
        return ResponseEntity.ok(product)
    }
    
    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: Long): ResponseEntity<ProductResponse> {
        val product = productService.getProduct(id)
        return ResponseEntity.ok(product)
    }
    
    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductResponse>> {
        val products = productService.getAllProducts()
        return ResponseEntity.ok(products)
    }
    
    @GetMapping("/category/{category}")
    fun getProductsByCategory(@PathVariable category: String): ResponseEntity<List<ProductResponse>> {
        val products = productService.getProductsByCategory(category)
        return ResponseEntity.ok(products)
    }
    
    @GetMapping("/brand/{brand}")
    fun getProductsByBrand(@PathVariable brand: String): ResponseEntity<List<ProductResponse>> {
        val products = productService.getProductsByBrand(brand)
        return ResponseEntity.ok(products)
    }
    
    @GetMapping("/search")
    fun searchProducts(@RequestParam keyword: String): ResponseEntity<List<ProductResponse>> {
        val products = productService.searchProducts(keyword)
        return ResponseEntity.ok(products)
    }
    
    @GetMapping("/price-range")
    fun getProductsByPriceRange(
        @RequestParam minPrice: BigDecimal,
        @RequestParam maxPrice: BigDecimal
    ): ResponseEntity<List<ProductResponse>> {
        val products = productService.getProductsByPriceRange(minPrice, maxPrice)
        return ResponseEntity.ok(products)
    }
    
    @PutMapping("/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @RequestBody request: ProductRequest
    ): ResponseEntity<ProductResponse> {
        val product = productService.updateProduct(id, request)
        return ResponseEntity.ok(product)
    }
    
    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<Unit> {
        productService.deleteProduct(id)
        return ResponseEntity.noContent().build()
    }
    
    @PatchMapping("/{id}/deactivate")
    fun deactivateProduct(@PathVariable id: Long): ResponseEntity<ProductResponse> {
        val product = productService.deactivateProduct(id)
        return ResponseEntity.ok(product)
    }
} 