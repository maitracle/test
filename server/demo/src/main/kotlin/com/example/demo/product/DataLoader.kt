package com.example.demo.product

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class DataLoader(
    private val productRepository: ProductRepository
) : CommandLineRunner {
    
    override fun run(vararg args: String?) {
        // 샘플 제품 데이터 생성
        val sampleProducts = listOf(
            Product(
                name = "MacBook Pro 16인치",
                description = "Apple M3 Pro 칩을 탑재한 최신 MacBook Pro",
                price = BigDecimal("3500000"),
                stockQuantity = 50,
                category = "노트북",
                brand = "Apple",
                imageUrl = "https://example.com/macbook-pro.jpg"
            ),
            Product(
                name = "iPhone 15 Pro",
                description = "A17 Pro 칩과 48MP 카메라를 탑재한 프리미엄 스마트폰",
                price = BigDecimal("1500000"),
                stockQuantity = 100,
                category = "스마트폰",
                brand = "Apple",
                imageUrl = "https://example.com/iphone15-pro.jpg"
            ),
            Product(
                name = "Samsung Galaxy S24 Ultra",
                description = "S Pen과 200MP 카메라를 탑재한 안드로이드 플래그십",
                price = BigDecimal("1800000"),
                stockQuantity = 75,
                category = "스마트폰",
                brand = "Samsung",
                imageUrl = "https://example.com/galaxy-s24-ultra.jpg"
            ),
            Product(
                name = "LG OLED TV 65인치",
                description = "4K 해상도와 완벽한 블랙을 제공하는 OLED TV",
                price = BigDecimal("2800000"),
                stockQuantity = 30,
                category = "TV",
                brand = "LG",
                imageUrl = "https://example.com/lg-oled-tv.jpg"
            ),
            Product(
                name = "Sony WH-1000XM5",
                description = "업계 최고 수준의 노이즈 캔슬링 헤드폰",
                price = BigDecimal("450000"),
                stockQuantity = 200,
                category = "오디오",
                brand = "Sony",
                imageUrl = "https://example.com/sony-wh1000xm5.jpg"
            )
        )
        
        // 기존 데이터가 없을 때만 샘플 데이터 생성
        if (productRepository.count() == 0L) {
            productRepository.saveAll(sampleProducts)
            println("샘플 제품 데이터가 생성되었습니다.")
        }
    }
} 