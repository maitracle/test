package com.example.demo.infrastructure.persistence.product

import com.example.demo.domain.product.model.Product
import com.example.demo.domain.product.model.ProductId
import com.example.demo.domain.product.valueobject.Price
import com.example.demo.domain.common.valueobject.Stock
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.math.BigDecimal

class ProductRepositoryImplTest : FunSpec() {
    
    init {
        context("상품 Repository 테스트") {
            test("ID로 상품을 조회할 수 있어야 한다") {
                // Given
                val productJpaRepository = mockk<ProductJpaRepository>()
                val productRepository = ProductRepositoryImpl(productJpaRepository)
                
                val productEntity = com.example.demo.infrastructure.persistence.product.ProductEntity.createExisting(
                    id = 1L,
                    name = "테스트 상품",
                    description = "테스트 설명",
                    price = BigDecimal("10000"),
                    stock = 100,
                    category = "전자제품",
                    brand = "테스트 브랜드",
                    imageUrl = "http://example.com/image.jpg",
                    isActive = true,
                    createdAt = java.time.LocalDateTime.now(),
                    updatedAt = java.time.LocalDateTime.now()
                )
                val productId = ProductId.of(1L)
                
                every { productJpaRepository.findById(1L) } returns java.util.Optional.of(productEntity)
                
                // When
                val foundProduct = productRepository.findById(productId)
                
                // Then
                foundProduct.shouldNotBeNull()
                foundProduct!!.name shouldBe "테스트 상품"
                foundProduct.description shouldBe "테스트 설명"
                foundProduct.category shouldBe "전자제품"
                foundProduct.brand shouldBe "테스트 브랜드"
                foundProduct.imageUrl shouldBe "http://example.com/image.jpg"
                foundProduct.isActive shouldBe true
                
                verify { productJpaRepository.findById(1L) }
            }
            
            test("존재하지 않는 ID로 조회하면 null을 반환해야 한다") {
                // Given
                val productJpaRepository = mockk<ProductJpaRepository>()
                val productRepository = ProductRepositoryImpl(productJpaRepository)
                
                val productId = ProductId.of(999L)
                
                every { productJpaRepository.findById(999L) } returns java.util.Optional.empty()
                
                // When
                val foundProduct = productRepository.findById(productId)
                
                // Then
                foundProduct shouldBe null
                
                verify { productJpaRepository.findById(999L) }
            }
            
            test("상품을 삭제할 수 있어야 한다") {
                // Given
                val productJpaRepository = mockk<ProductJpaRepository>()
                val productRepository = ProductRepositoryImpl(productJpaRepository)
                
                val productId = ProductId.of(1L)
                
                every { productJpaRepository.deleteById(1L) } returns Unit
                
                // When
                productRepository.delete(productId)
                
                // Then
                verify { productJpaRepository.deleteById(1L) }
            }
        }
    }
}