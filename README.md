# 국가별 공휴일 검색과제

플랜잇스퀘어 과제(년도,국가별 공휴일 검색)

---

## 📌 목차
- [빌드 & 실행 방법](#빌드--실행-방법)
- [H2 설정](#h2-설정)
- [REST API 명세](#rest-api-명세)
- [테스트 실행](#테스트-실행)
- [Swagger 설정](#swagger-설정)

---

## ✔️  빌드 & 실행 방법[docker-compose 실행을 추천]
- ./demo 폴더 하단에서 명령어 실행.
- `docker-compose 혹은 gradlew 중 1개만 실행.`

#### - Local (Gradle)
```
./gradlew bootRun
```

#### - Docker Compose(도커컴포즈가 설치되어 있어야함.)
```
docker-compose build
docker-compose up -d

도커 API Log 확인: 
    docker logs holiday-api -f
```
#### - 접속 도메인

빌드 종류|접속 도메인
---|---|
docker-compose|http://localhost:8080|
Gradlew|http://localhost|

### - H2 Console 접속 URL
빌드 종류|H2 Console URL
---|---|
docker-compose|http://localhost:8080/h2-console|
Gradlew|http://localhost/h2-console|

#### - H2 Console 접속정보:
| JDBC URL | User | Password |
|-----------|--------|------|
| `jdbc:h2:file:./data/testdb` | sa | _(없음)_ |


---

## ✔️ REST API 명세


### - 1. 등록된 모든 국가 목록을 조회
- http://localhost:8080/countries
    
| 엔드포인트 | Request | Response |
|-----------|--------|------|
| `GET /countries` | X | O |
#### - Response [ List<CountryEntity> ]
```
[
  {
    "countryCode": "KR",
    "name": "Korea"
  },
  {
    "countryCode": "JP",
    "name": "Japan"
  },
  ....
]
```
---
### - 2. 휴일 조회 (연도 + 국가)
- http://localhost:8080/holidays/2025/KR?month=5&page=0

| 엔드포인트 | Request | Response |
|-----------|--------|------|
| `GET /holidays/{year}/{country}` | O | O |
#### - Request
| 이름 | 타입 | 필수 | 설명 | 예시 |
|------|-------|------|--------|---------|
| `year` | String | O | 조회할 연도 | `2025` |
| `country` | String | O | 국가 코드 | `KR` |
| `month` | Integer | X | 조회할 월(옵션) | `1` |
| `page` | Integer | X | 페이지 번호 | `0` |
#### - Response [Page<PublicHolidayEntity>]
```
{
    "content": [
        {
            "id": 4644,
            "date": "2025-05-05",
            "localName": "어린이날",
            "name": "Children's Day",
            "fixed": false,
            "global": true,
            "counties": null,
            "launchYear": null,
            "holidayYear": "2025"
        },
        {
            "id": 4645,
            "date": "2025-05-05",
            "localName": "부처님 오신 날",
            "name": "Buddha's Birthday",
            "fixed": false,
            "global": true,
            "counties": null,
            "launchYear": null,
            "holidayYear": "2025"
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 5,
        "sort": {
            "sorted": true,
            "empty": false,
            "unsorted": false
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "totalPages": 1,
    "totalElements": 2,
    "last": true,
    "size": 5,
    "number": 0,
    "sort": {
        "sorted": true,
        "empty": false,
        "unsorted": false
    },
    "numberOfElements": 2,
    "first": true,
    "empty": false
}
```

---
### - 3. 공휴일 Upsert (덮어쓰기)
- http://localhost:8080/upsert/2025/KR

| 엔드포인트 | Request | Response |
|-----------|--------|------|
| `POST /upsert/{year}/{country}` | O | O |
#### - Request
| 이름 | 타입 | 필수 | 설명 | 예시 |
|------|-------|------|--------|---------|
| `year` | String | O | 년 | `2025` |
| `country` | String | O | 국가 코드 | `KR` |
#### - Response [List<PublicHolidayEntity>] (Paging되지않고, request로 조회된값 전체리턴)
```
[
    {
        "id": 8357,
        "date": "2025-01-01",
        "localName": "새해",
        "name": "New Year's Day",
        "fixed": false,
        "global": true,
        "counties": null,
        "launchYear": null,
        "holidayYear": "2025"
    },
    {
        "id": 8358,
        "date": "2025-01-28",
        "localName": "설날",
        "name": "Lunar New Year",
        "fixed": false,
        "global": true,
        "counties": null,
        "launchYear": null,
        "holidayYear": "2025"
    },
    .....
```

---
### - 4. 휴일 삭제
- http://localhost:8080/holidays/2025/KR

| 엔드포인트 | Request | Response |
|-----------|--------|------|
| `DELETE /holidays/{year}/{country}` | O | O |
#### - Request
| 이름 | 타입 | 필수 | 설명 | 예시 |
|------|-------|------|--------|---------|
| `year` | String | O | 년 | `2025` |
| `country` | String | O | 국가 코드 | `KR` |
#### - Response [Map<String, Integer>] (request로 검색된값의 삭제된수)
```
{
    "count": 15
}
```

---



## ✔️  테스트 성공 캡쳐
<img width="1189" height="463" alt="Holiday API Flow" src="https://github.com/user-attachments/assets/99ab63dc-09fa-4a97-a976-d8894c7e1422" />

---

```
docker-compose로 실행시 JUnit Test Log 확인: 
docker logs holiday-api-test -f

Gradle에서 JUnit Test 명령어:
 ./gradlew clean test
```

---

## ✔️ Swagger UI 노출  확인방법
### - 🔐 JWT 인증(필수)
- `jwt Token을 설정하지 않으면, 인증이 실패합니다.`
#### - Swagger 상단 Authorize 클릭 후 Token 입력:
```java
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30
```

주소|설명
---|---|
`/swagger-ui/index.html`|Swagger 접속url |


#### - Postman으로 할 시 headers 설정
Key|Value
---|---|
Authorization|Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30 |


---


