# 캐시 (Cache)

---

> - 자주 사용하는 데이터를 미리 보관해둔 임시 장소를 의미
> - 비교적 저장 공간이 적고 전체적인 비용이 비쌈
> - 대신 빠른 IO를 통해 성능적 이점을 가져올 수 있음


## 사용하면 좋은 상황
- 도중에 변경될 일이 없는 데이터베이스 조회 값
- 자주 호출되는 데이터

## 스프링 부트에서의 캐시(Cache)

> - 스프링 부트에서 사용할 수 있는 캐시는 대부분 JSR-107을 따름
> - JSR
>   - Java Specification Requests의 줄임말로, 자바 플랫폼에 대한 규격을 제안하거나 기술한 것을 의미
>   - 그 중 JSR-107은 JCACHE(Java Temporary Caching API)에 관한 내용
> - JSR-107을 따르는 캐시를 사용하면 어떤 구현체 캐시를 사용하는지에 관계없이 추상화를 지원


## 로컬 캐시와 글로벌 캐시
- 로컬 캐시
  - 로컬(해당 서버)에서만 사용하는 캐시
  - 외부 서버와 트랜잭션 비용이 들지 않기 때문에 속도가 빠름
  - 로컬에서만 사용하기 때문에 분산 서버의 구조에서 캐시를 공유하기 어려움
- 글로벌 캐시
  - 여러 서버에서 접근할 수 있는 캐시 서버를 구축하여 사용하는 방식
  - 네트워크를 통해 데이터를 가져오는 트랜잭션 비용이 있기 때문에 로컬 캐시에 비해 상대적으로 느림
  - 별도의 서버로 운영되기 때문에 서버 간 데이터 공유에 용이함

# Redis

> - Remote Dictionary Server의 약자로, '키-값' 구조의 데이터를 저장하고 관리하기 위한 오픈 소스 기반의 비관계형 데이터 관리 시스템
> - ![https://redis.io/](https://redis.io/) 