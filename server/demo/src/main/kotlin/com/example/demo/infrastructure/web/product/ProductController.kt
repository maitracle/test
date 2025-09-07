package com.example.demo.infrastructure.web.product

import com.example.demo.application.product.usecase.CreateProductUseCase
import com.example.demo.application.product.usecase.GetProductUseCase
import com.example.demo.application.product.usecase.SearchProductsUseCase
import com.example.demo.infrastructure.web.product.dto.CreateProductWebRequest
import com.example.demo.infrastructure.web.product.dto.ProductWebResponse
import com.example.demo.infrastructure.web.product.dto.SearchProductsWebRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 상품 관련 REST API 컨트롤러
 * 상품 생성, 조회, 검색 등의 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/products")
class ProductController(
    private val createProductUseCase: CreateProductUseCase,
    private val getProductUseCase: GetProductUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val productWebMapper: ProductWebMapper
) {
    
    /**
     * 상품 생성
     * POST /api/products
     */
    @PostMapping
    fun createProduct(@RequestBody request: CreateProductWebRequest): ResponseEntity<ProductWebResponse> {
        val useCaseRequest = productWebMapper.toCreateProductRequest(request)
        val useCaseResponse = createProductUseCase.execute(useCaseRequest)
        val response = productWebMapper.toProductWebResponse(useCaseResponse)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
    
    /**
     * 상품 상세 조회
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: Long): ResponseEntity<ProductWebResponse> {
        val useCaseResponse = getProductUseCase.getProductById(id)
        val response = productWebMapper.toProductWebResponse(useCaseResponse)
        return ResponseEntity.ok(response)
    }
    
    /**
     * 상품 목록 조회
     * GET /api/products
     */
    @GetMapping
    fun getProducts(): ResponseEntity<List<ProductWebResponse>> {
        val searchRequest = com.example.demo.application.product.usecase.ProductSearchRequest()
        val useCaseResponse = searchProductsUseCase.execute(searchRequest)
        val response = useCaseResponse.map { productWebMapper.toProductWebResponse(it) }
        return ResponseEntity.ok(response)
    }
    
    /**
     * 상품 검색
     * GET /api/products/search?category={category}&keyword={keyword}
     */
    @GetMapping("/search")
    fun searchProducts(
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) keyword: String?
    ): ResponseEntity<List<ProductWebResponse>> {
        val request = SearchProductsWebRequest(category = category, keyword = keyword)
        val useCaseRequest = productWebMapper.toSearchProductsRequest(request)
        val useCaseResponse = searchProductsUseCase.execute(useCaseRequest)
        val response = useCaseResponse.map { productWebMapper.toProductWebResponse(it) }
        return ResponseEntity.ok(response)
    }
}
