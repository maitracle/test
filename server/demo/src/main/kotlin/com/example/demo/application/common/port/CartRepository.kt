package com.example.demo.application.common.port

import com.example.demo.domain.cart.model.Cart
import com.example.demo.domain.cart.model.CartId
import com.example.demo.domain.user.model.User
import com.example.demo.domain.user.model.UserId

/**
 * 장바구니 데이터 접근을 위한 포트 인터페이스
 * 클린 아키텍처의 의존성 역전 원칙에 따라 애플리케이션 레이어에서 정의
 */
interface CartRepository {
    
    /**
     * 사용자 ID로 사용자 정보를 조회합니다.
     * @param userId 사용자 ID
     * @return 사용자 정보, 없으면 null
     */
    fun findUserById(userId: UserId): User?
    
    /**
     * 장바구니를 저장합니다.
     * @param cart 저장할 장바구니
     * @return 저장된 장바구니
     */
    fun save(cart: Cart): Cart
    
    /**
     * 장바구니 ID로 장바구니를 조회합니다.
     * @param cartId 장바구니 ID
     * @return 장바구니 정보, 없으면 null
     */
    fun findById(cartId: CartId): Cart?
    
    /**
     * 사용자 ID로 장바구니를 조회합니다.
     * @param userId 사용자 ID
     * @return 장바구니 정보, 없으면 null
     */
    fun findByUserId(userId: UserId): Cart?
    
    /**
     * 장바구니를 삭제합니다.
     * @param cartId 삭제할 장바구니 ID
     */
    fun delete(cartId: CartId)
}
