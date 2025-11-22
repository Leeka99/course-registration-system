# 🔎오픈 미션

### 📝 회고 및 관련 내용 문서화
> - [학습내용 및 실습과정 보러가기](https://leeka-blog.tistory.com/category/%EB%8F%99%EC%8B%9C%EC%84%B1%20%EB%AC%B8%EC%A0%9C%20%ED%95%99%EC%8A%B5)
> - [오픈 미션 회고 글 보러가기](https://leeka-blog.tistory.com/category/%EC%9A%B0%ED%85%8C%EC%BD%94%208%EA%B8%B0%20%ED%94%84%EB%A6%AC%EC%BD%94%EC%8A%A4)

---
### 📢 오픈 미션 소개
> - 오픈 미션은 2주 동안 자신이 도전하고 싶은 주제를 정해 직접 계획하고 실행하는 프리코스 자율 챌린지입니다.<br>
> - 이 과정에서 목표를 설정하고 문제를 해결하는 경험을 쌓으며, 스스로 성장하는 것을 목표로 합니다.

---
## 👨‍🏫 미션 소개
> <b>주제</b> : **선착순 이벤트 시스템 제작 및 동시성 문제 학습**<br>

> <b>기간</b> : 2025.11.04 ~ 2025.11.17

### 📌 주제 선정 이유 <br>
> 💡 핵심 동기
>> 로또 만들기 미션의 코드 리뷰에서 `동시성 문제`에 대해 고민해보면 좋겠다는 피드백을 받았습니다. <br>
>> 이를 통해 멀티스레드와 락 개념에 대한 이해가 부족함을 깨달았고, 실제로 `동시성 문제`가 발생할 수 있는 상황을 직접 경험해보고 싶다는 동기가 생겼습니다. <br>
>
> 🤓 학습 목표
>> 따라서 이번 미션에서는 <b>동시성 문제가 발생할 수 있는 프로젝트를 직접 설계하고 구현</b>하며, 스레드, 락 등 `동시성` 관련 내용을 집중적으로 학습합니다. <br>
>> 멀티스레드 환경에서 발생할 수 있는 race condition을 이해하고, 이를 해결하기 위한 다양한 방법을 실습하는 것이 목표입니다.
>
> 😎 기대 성과
>> 이 과정을 통해 `동시성 문제 정의` ➡ `해결` ➡ `검증`까지의 과정을 직접 경험하며, 멀티스레드와 락을 적용하는 실제 사례를 학습할 수 있습니다. <br>
>> 2주 동안 프로젝트를 설계하고 구현하면서 `동시성 문제`를 체계적으로 이해하고, 문제 해결 능력과 적용 경험을 쌓는 것이 목표입니다.<br>

---
### 🧑‍🔧 기술 스택
> - Java 21
> - Spring Boot 3.5
> - Gradle
> - MySQL 8.0
> - JPA / Hibernate

### 🔧 개발/운영 도구
> - JMeter
> - Docker

---
### 👉 작업 흐름
> **수강신청 시스템 구현** ➡ **멀티스레드 + 락 적용** ➡ **동시성 검증** ➡ **부하 테스트**

### ✔️ 작업 목록 체크리스트
> #### 1. 수강신청 시스템 제작<br>
> 목표 : 동시성 실험 환경을 구축한다.
>> - [x] 기본 엔티티 설계
>>   - [x] Student
>>   - [x] Course
>>   - [x] Registration
>> - [x] Repository 구현
>>   - [x] StudentRepository
>>   - [x] CourseRepository
>>   - [x] RegistrationRepository
>> - [x] Service 로직 구현
>>   - [x] 수강신청 기능
>>   - [x] 대기자 처리
>> - [x] Controller 구현
>>   - [x] 수강신청 API
>
> #### 2. 스레드 학습
> 목표 : 스레드가 어떻게 만들어지고, 실행되고, 공유 자원을 어떻게 다루는지 학습한다.
>> - [x] 기본 스레드
>>   - [x] Thread 클래스
>>   - [x] Runnable 인터페이스
>> - [x] 스레드 풀
>>   - [x] Callable 인터페이스
>>   - [x] Future 인터페이스
>>   - [x] ExecutorService 인터페이스
>>   - [x] ThreadPoolExecutor 동작 방식
>> - [x] 스레드 안전성
>>   - [x] 공유 자원 개념
>>   - [x] 스레드 간 데이터 충돌 이해
>
> #### 3. 동시성 문제 재현
> 목표 : Race Condition 발생 상황을 직접 만들어보고 확인한다.
>> - [x] 순차적으로 진행 테스트
>> - [x] 여러 스레드가 동시에 같은 강좌 신청 (ExecutorService 사용)
>> - [x] Race Condition 확인
>
> #### 4. DB 락 및 트랜잭션 학습
> 목표 : DB 락 구조와 트랜잭션 전략을 이해한다.
>> - [x] 트랜잭션
>>   - [x] 개념
>>   - [x] ACID
>> - [x] MySQL 엔진 종류
>>   - [x] MyISAM
>>   - [x] InnoDB
>> - [x] DB 락 구조 (DB 내부 메커니즘)
>>   - [x] MySQL 엔진레벨 Lock
>>     - [x] Global Lock
>>     - [x] Backup Lock
>>     - [x] Table Lock
>>   - [x] Storage 엔진레벨 Lock
>>     - [x] Record Lock
>>     - [x] Gap Lock
>>     - [x] Next-Key Lock
>> - [x] DB 락 전략
>>   - [x] 비관적 락(Pessimistic Lock)
>>   - [x] 낙관적 락(Optimistic Lock)
>
> #### 5. 동시성 제어 실습
> 목표 : 코드 수준과 DB 수준 락을 적용하며 효과를 비교 학습한다.<br>
>> - [x] 자바 레벨 락
>>   - [x] synchronized 키워드
>>   - [x] ReentrantLock 적용
>>     - [x] Condition 활용
>> - [x] DB 레벨 락
>>   - [x] 비관적 락(Pessimistic Lock)
>>   - [x] 낙관적 락(Optimistic Lock)
>
> #### 6. 스트라이핑
> 목표 : 락을 효율적으로 사용하는 방법 중 하나인 스트라이핑을 학습한다.
>> - [x] 스트라이핑 실습환경 제작
>>   - [x] 단일 수강신청 서비스 -> 다중 수강신청 서비스로 확장
>> - [x] 샤드 / 샤딩 이해하기
>> - [x] Lock Striping 직접 구현하기
>>   - [x] ConcurrentHashMap<Long, ReentrantLock> + computeIfAbsent
>> - [ ] 라이브러리 활용하기 (안전하게 구현된 Striping)
>>   - [x] Guava Striped
>>   - [ ] Redisson (multiLock)
>
> #### 7. 다중 인스턴스 환경에서 동시성 제어
> - 목표 : 여러 서버에서 동시에 요청이 들어오는 상황에서 성능을 최적화하는 방법을 학습한다.
>> - [ ] 분산 락
>>   - [ ] Redis
>>   - [ ] Zookeeper
>
> #### 8. 로직 검증 테스트
> - 목표 : 로직이 정상적으로 작동되는지 테스트한다.
>>   - [ ] 과목 1개 1,3학년 동시 테스트
>>     - [x] 자바 레벨 락
>>       - [x] Synchronized
>>       - [x] ReentrantLock
>>     - [x] DB 레벨 락
>>       - [x] Pessimistic Lock
>>       - [x] Optimistic Lock
>>     - [ ] Striping
>>       - [x] Lock Striping
>>       - [x] Guava
>>       - [ ] Redisson (multiLock)
>>     - [ ] 분산 락
>>       - [ ] Redis
>>       - [ ] Zookeeper
>>   - [ ] 과목 1개 모든 학년 동시 테스트
>>     - [x] 자바 레벨 락
>>       - [x] Synchronized
>>       - [x] ReentrantLock
>>     - [x] DB 레벨 락
>>       - [x] Pessimistic Lock
>>       - [x] Optimistic Lock
>>     - [ ] Striping
>>       - [x] Lock Striping
>>       - [x] Guava
>>       - [ ] Redisson (multiLock)
>>     - [ ] 분산 락
>>       - [ ] Redis
>>       - [ ] Zookeeper
>>   - [ ] 과목 3개 1,3 학년 동시 테스트
>>     - [x] 자바 레벨 락
>>       - [x] Synchronized
>>       - [x] ReentrantLock
>>     - [x] DB 레벨 락
>>       - [x] Pessimistic Lock
>>       - [x] Optimistic Lock
>>     - [ ] Striping
>>       - [x] Lock Striping
>>       - [x] Guava
>>       - [ ] Redisson (multiLock)
>>     - [ ] 분산 락
>>       - [ ] Redis
>>       - [ ] Zookeeper
>>   - [ ] 과목 3개 모든 학년 동시 테스트
>>     - [x] 자바 레벨 락
>>       - [x] Synchronized
>>       - [x] ReentrantLock
>>     - [x] DB 레벨 락
>>       - [x] Pessimistic Lock
>>       - [x] Optimistic Lock
>>     - [ ] Striping
>>       - [x] Lock Striping
>>       - [x] Guava
>>       - [ ] Redisson (multiLock)
>>     - [ ] 분산 락
>>       - [ ] Redis
>>       - [ ] Zookeeper
>
> #### 9. 성능 테스트
> 목표 : JMeter를 사용하여 동시 요청 상황에서 시스템의 성능을 비교한다.
>> - [x] 자바 레벨 락
>>   - [x] Synchronized
>>   - [x] ReentrantLock
>> - [x] DB 레벨 락
>>   - [x] Pessimistic Lock
>>   - [x] Optimistic Lock
>> - [ ] Striping
>>   - [x] Lock Striping
>>   - [x] Guava
>>   - [ ] Redisson (multiLock)
>> - [ ] 분산 락
>>   - [ ] Redis
>>   - [ ] Zookeeper
---
### 커밋 메시지 컨벤션 정리
> | 타입    | 설명 |
> |-------|------|
> | feat  | 새로운 기능 추가 |
> | fix   | 버그 수정 |
> | docs  | 문서 수정 |
> | style | 코드 스타일 수정 (로직 변경 없음) |
> | refactor | 리팩터링 (동작 변화 없이 구조 개선) |
> | test  | 테스트 코드 추가/수정 |
> | chore | 빌드, 설정, 유지보수 작업 |


