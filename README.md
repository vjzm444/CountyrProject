# 국가별 공휴일 검색과제

이 프로젝트는 개발/구현/테스트 목적이며 실제 프로젝트는 fork 하여 사용해야 한다.

---

## 📌 목차
- [빌드 & 실행 방법](#빌드--실행-방법)
- [H2 설정](#h2-설정)
- [REST API 명세](#rest-api-명세)
- [테스트 실행](#테스트-실행)
- [Swagger 설정](#swagger-설정)

---

## 빌드 & 실행 방법[docker-compose 실행을 추천]
- ./demo 폴더 하단에서 명령어 실행.
- `docker-compose 혹은 gradlew 중 1개만 실행.`

#### ✔️ Local (Gradle)
```
./gradlew bootRun
```

#### ✔️ Docker Compose(도커컴포즈가 설치되어 있어야함.)
```
docker-compose build
docker-compose up -d

도커 API Log 확인: 
    docker logs holiday-api -f
```
#### 접속 도메인

빌드 종류|접속 도메인
---|---|
docker-compose|http://localhost:8080|
Gradlew|http://localhost|

## H2 Console 접속
빌드 종류|H2 Console URL
---|---|
docker-compose|http://localhost:8080/h2-console|
Gradlew|http://localhost/h2-console|
#### H2 Console 접속정보:
| JDBC URL | User | Password |
|-----------|--------|------|
| `jdbc:h2:file:./data/testdb` | sa | _(없음)_ |



---

## REST API 명세


```
📌 공통 모델 (HolidayDTO)
{
  "country": "KR",
  "date": "2025-01-01",
  "name": "신정"
}
```


### API

| 엔드포인트 | 메소드 | 설명 |
|-----------|--------|------|
| `/holidays/2024/KR?month=5&page=1` | GET | 신규 사용자 등록 |
| `/api/auth/login` | POST | 사용자 로그인 |



---
## 🧪 테스트 성공 캡쳐

사진사진사진
<img width="1189" height="463" alt="Holiday API Flow" src="https://github.com/user-attachments/assets/99ab63dc-09fa-4a97-a976-d8894c7e1422" />

---
## Swagger UI 노출  확인방법
## 🔐 JWT 인증(필수)
- `jwt Token을 설정하지 않으면, 인증이 실패합니다.`
#### Swagger 상단 Authorize 클릭 후 Token 입력:
```java
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30
```


주소|설명
---|---|
`/swagger-ui/index.html`|Swagger 접속url |


#### Postman으로 할 시 headers 설정
Key|Value
---|---|
Authorization|Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30 |




