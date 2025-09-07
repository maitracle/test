package com.example.demo.infrastructure.web.promotion

import com.example.demo.application.promotion.usecase.ApplyPromotionUseCase
import com.example.demo.application.promotion.usecase.CreatePromotionUseCase
import com.example.demo.application.promotion.usecase.ManagePromotionUseCase
import com.example.demo.infrastructure.web.promotion.dto.ApplyPromotionWebRequest
import com.example.demo.infrastructure.web.promotion.dto.CreatePromotionWebRequest
import com.example.demo.infrastructure.web.promotion.dto.PromotionWebResponse
import com.example.demo.infrastructure.web.promotion.dto.UpdatePromotionWebRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 프로모션 관련 REST API 컨트롤러
 * 프로모션 생성, 조회, 수정, 삭제, 적용 등의 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/promotions")
class PromotionController(
    private val createPromotionUseCase: CreatePromotionUseCase,
    private val managePromotionUseCase: ManagePromotionUseCase,
    private val applyPromotionUseCase: ApplyPromotionUseCase,
    private val promotionWebMapper: PromotionWebMapper
) {
    
    /**
     * 프로모션 생성
     * POST /api/promotions
     */
    @PostMapping
    fun createPromotion(@RequestBody request: CreatePromotionWebRequest): ResponseEntity<PromotionWebResponse> {
        val useCaseRequest = promotionWebMapper.toCreatePromotionRequest(request)
        val useCaseResponse = createPromotionUseCase.execute(useCaseRequest)
        val response = promotionWebMapper.toPromotionWebResponse(useCaseResponse)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
    
    /**
     * 프로모션 목록 조회
     * GET /api/promotions
     */
    @GetMapping
    fun getPromotions(@RequestParam(required = false) active: Boolean?): ResponseEntity<List<PromotionWebResponse>> {
        val useCaseResponse = if (active == true) {
            managePromotionUseCase.getActivePromotions()
        } else {
            managePromotionUseCase.getAllPromotions()
        }
        val response = useCaseResponse.map { promotionWebMapper.toPromotionWebResponse(it) }
        return ResponseEntity.ok(response)
    }
    
    /**
     * 프로모션 상세 조회
     * GET /api/promotions/{id}
     */
    @GetMapping("/{id}")
    fun getPromotion(@PathVariable id: Long): ResponseEntity<PromotionWebResponse> {
        val useCaseResponse = managePromotionUseCase.getPromotionById(id)
        val response = promotionWebMapper.toPromotionWebResponse(useCaseResponse)
        return ResponseEntity.ok(response)
    }
    
    /**
     * 프로모션 수정
     * PUT /api/promotions/{id}
     */
    @PutMapping("/{id}")
    fun updatePromotion(
        @PathVariable id: Long,
        @RequestBody request: UpdatePromotionWebRequest
    ): ResponseEntity<PromotionWebResponse> {
        val useCaseResponse = managePromotionUseCase.updatePromotion(
            promotionId = id,
            name = request.name,
            description = request.description,
            priority = request.priority
        )
        val response = promotionWebMapper.toPromotionWebResponse(useCaseResponse)
        return ResponseEntity.ok(response)
    }
    
    /**
     * 프로모션 삭제
     * DELETE /api/promotions/{id}
     */
    @DeleteMapping("/{id}")
    fun deletePromotion(@PathVariable id: Long): ResponseEntity<Void> {
        managePromotionUseCase.deletePromotion(id)
        return ResponseEntity.noContent().build()
    }
    
    /**
     * 프로모션 활성화
     * POST /api/promotions/{id}/activate
     */
    @PostMapping("/{id}/activate")
    fun activatePromotion(@PathVariable id: Long): ResponseEntity<PromotionWebResponse> {
        val useCaseResponse = managePromotionUseCase.activatePromotion(id)
        val response = promotionWebMapper.toPromotionWebResponse(useCaseResponse)
        return ResponseEntity.ok(response)
    }
    
    /**
     * 프로모션 비활성화
     * POST /api/promotions/{id}/deactivate
     */
    @PostMapping("/{id}/deactivate")
    fun deactivatePromotion(@PathVariable id: Long): ResponseEntity<PromotionWebResponse> {
        val useCaseResponse = managePromotionUseCase.deactivatePromotion(id)
        val response = promotionWebMapper.toPromotionWebResponse(useCaseResponse)
        return ResponseEntity.ok(response)
    }
    
    /**
     * 프로모션 적용
     * POST /api/promotions/apply
     */
    @PostMapping("/apply")
    fun applyPromotion(@RequestBody request: ApplyPromotionWebRequest): ResponseEntity<Map<String, Any>> {
        val useCaseRequest = promotionWebMapper.toApplyPromotionRequest(request)
        val useCaseResponse = applyPromotionUseCase.execute(useCaseRequest)
        val response = promotionWebMapper.toApplyPromotionWebResponse(useCaseResponse)
        return ResponseEntity.ok(response)
    }
}
