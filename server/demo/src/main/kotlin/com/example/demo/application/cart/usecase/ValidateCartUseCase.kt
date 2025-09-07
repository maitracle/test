package com.example.demo.application.cart.usecase

import com.example.demo.application.common.dto.request.ValidateCartRequest
import com.example.demo.application.common.dto.response.ValidateCartResponse
import com.example.demo.application.common.port.ProductRepository
import com.example.demo.domain.common.valueobject.Quantity
import com.example.demo.domain.product.model.ProductId
import org.springframework.stereotype.Component

/**
 * 장바구니 검증 유스케이스
 * 장바구니 아이템들의 유효성을 검증합니다.
 */
@Component
class ValidateCartUseCase(
    private val productRepository: ProductRepository
) {
    
    /**
     * 장바구니 검증을 실행합니다.
     * @param request 장바구니 검증 요청
     * @return 장바구니 검증 결과
     */
    fun execute(request: ValidateCartRequest): ValidateCartResponse {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        
        // 1. 장바구니 아이템 검증
        request.items.forEach { itemRequest ->
            val product = productRepository.findById(ProductId.of(itemRequest.productId))
            
            if (product == null) {
                errors.add("상품을 찾을 수 없습니다: ${itemRequest.productId}")
                return@forEach
            }
            
            // 상품 활성화 상태 확인
            if (!product.isActive) {
                errors.add("비활성화된 상품입니다: ${product.name}")
            }
            
            // 재고 확인
            val quantity = Quantity(itemRequest.quantity)
            if (!product.isAvailable(quantity)) {
                errors.add("상품 재고가 부족합니다: ${product.name} (요청: ${quantity.value}, 재고: ${product.stock.quantity})")
            }
            
            // 수량 유효성 확인
            if (itemRequest.quantity <= 0) {
                errors.add("수량은 0보다 커야 합니다: ${product.name}")
            }
            
            // 재고 부족 경고 (재고가 10개 미만인 경우)
            if (product.stock.quantity < 10 && product.stock.quantity >= quantity.value) {
                warnings.add("상품 재고가 부족합니다: ${product.name} (재고: ${product.stock.quantity}개)")
            }
        }
        
        // 2. 중복 상품 확인
        val duplicateProducts = request.items
            .groupBy { it.productId }
            .filter { it.value.size > 1 }
        
        if (duplicateProducts.isNotEmpty()) {
            warnings.add("중복된 상품이 있습니다. 수량을 합산하여 처리됩니다.")
        }
        
        // 3. 빈 장바구니 확인
        if (request.items.isEmpty()) {
            warnings.add("장바구니가 비어있습니다.")
        }
        
        return ValidateCartResponse(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }
}
