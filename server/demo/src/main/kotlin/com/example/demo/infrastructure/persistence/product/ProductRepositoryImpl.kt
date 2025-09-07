package com.example.demo.infrastructure.persistence.product

import com.example.demo.application.common.port.ProductRepository
import com.example.demo.domain.common.valueobject.Money
import com.example.demo.domain.product.model.Product
import com.example.demo.domain.product.model.ProductId
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 상품 Repository 구현체
 * 
 * ProductRepository 포트의 구현체로, JPA를 사용하여 상품 데이터를 관리합니다.
 * 도메인 모델과 엔티티 간의 변환을 담당합니다.
 */
@Component
@Transactional(readOnly = true)
class ProductRepositoryImpl(
    private val productJpaRepository: ProductJpaRepository
) : ProductRepository {
    
    override fun findById(productId: ProductId): Product? {
        return productJpaRepository.findById(productId.value)
            .map { it.toDomain() }
            .orElse(null)
    }
    
    override fun findAll(): List<Product> {
        return productJpaRepository.findAll()
            .map { it.toDomain() }
    }
    
    override fun findActiveProducts(): List<Product> {
        return productJpaRepository.findByIsActiveTrue()
            .map { it.toDomain() }
    }
    
    override fun findByCategory(category: String): List<Product> {
        return productJpaRepository.findByCategory(category)
            .map { it.toDomain() }
    }
    
    override fun findByNameContaining(name: String): List<Product> {
        return productJpaRepository.findByNameContainingIgnoreCase(name)
            .map { it.toDomain() }
    }
    
    @Transactional
    override fun save(product: Product): Product {
        val entity = product.toEntity()
        val savedEntity = productJpaRepository.save(entity)
        return savedEntity.toDomain()
    }
    
    @Transactional
    override fun delete(productId: ProductId) {
        productJpaRepository.deleteById(productId.value)
    }
}

/**
 * ProductEntity를 Product 도메인 모델로 변환하는 확장 함수
 */
private fun ProductEntity.toDomain(): Product {
    return Product.createExisting(
        id = ProductId.of(id ?: throw IllegalStateException("Product ID cannot be null")),
        name = name,
        description = description,
        price = com.example.demo.domain.product.valueobject.Price.of(price),
        stock = com.example.demo.domain.common.valueobject.Stock.of(stock),
        category = category,
        brand = brand,
        imageUrl = imageUrl,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Product 도메인 모델을 ProductEntity로 변환하는 확장 함수
 */
private fun Product.toEntity(): ProductEntity {
    return ProductEntity.createExisting(
        id = id.value,
        name = name,
        description = description,
        price = price.amount.amount,
        stock = stock.quantity,
        category = category,
        brand = brand,
        imageUrl = imageUrl,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
