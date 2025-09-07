package com.example.demo.infrastructure.web.user

import com.example.demo.application.user.usecase.CreateUserUseCase
import com.example.demo.application.user.usecase.GetUserUseCase
import com.example.demo.infrastructure.web.user.dto.CreateUserWebRequest
import com.example.demo.infrastructure.web.user.dto.UserWebResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 사용자 관련 REST API 컨트롤러
 * 사용자 생성, 조회 등의 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/users")
class UserController(
    private val createUserUseCase: CreateUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val userWebMapper: UserWebMapper
) {
    
    /**
     * 사용자 생성
     * POST /api/users
     */
    @PostMapping
    fun createUser(@RequestBody request: CreateUserWebRequest): ResponseEntity<UserWebResponse> {
        val useCaseRequest = userWebMapper.toCreateUserRequest(request)
        val useCaseResponse = createUserUseCase.execute(useCaseRequest)
        val response = userWebMapper.toUserWebResponse(useCaseResponse)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
    
    /**
     * 사용자 상세 조회
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): ResponseEntity<UserWebResponse> {
        val useCaseResponse = getUserUseCase.getUserById(id)
        val response = userWebMapper.toUserWebResponse(useCaseResponse)
        return ResponseEntity.ok(response)
    }
}
