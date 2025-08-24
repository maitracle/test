# Demo Spring Boot Project

Spring Boot와 Kotlin을 사용한 데모 프로젝트입니다.

## 1. 환경 세팅

### 필요 환경
- **Java**: 21 버전
- **Kotlin**: 1.9.25 버전
- **Gradle**: 프로젝트에 포함된 Gradle Wrapper 사용

### 환경 확인 명령어

환경이 올바르게 설정되었는지 확인하려면 다음 명령어를 실행하세요:

```bash
# Java 버전 확인
java -version

# Gradle 버전 확인
./gradlew --version
```

**예상 출력**:
- Java: `openjdk version "21.x.x"` 또는 `java version "21.x.x"`
- Gradle: `Gradle 8.x.x`

## 2. 실행 방법

프로젝트 루트 디렉토리에서 다음 명령어를 실행하세요:

```bash
./gradlew bootRun
```

## 3. 확인 방법

애플리케이션이 실행되면 다음 URL로 접속하여 확인할 수 있습니다:

- **메인 페이지**: http://localhost:8080
- **Hello API**: http://localhost:8080/hello

## 4. 프로젝트 구조

- Spring Boot 3.5.5
- Kotlin 1.9.25
- H2 데이터베이스 (인메모리)
- JPA 지원 