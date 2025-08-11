# CaptureCat-Android 🐱
### **스크린샷을 태그로 쉽게 분류하고 관리할 수 있는 앱 🔖**

 <img width="1587" height="696" alt="Image" src="https://github.com/user-attachments/assets/5576cffd-bfbc-4684-9e22-f595a6b0fd1f" />

<br>
<br>

> **“오늘은 어떤 정보를 캡처하셨나요?”**
>
> 우리는 하루에도 수많은 정보를 캡처하지만, 갤러리 속에서 금세 잊히곤 합니다.
> 캡처캣은 스크린샷을 단순한 이미지가 아닌, 다시 활용 가능한 정보로 바꿔주는 조력자입니다.


- **운7기𝙘𝙝𝙞𝙡𝙡** Team Blog: https://10th7.tistory.com/
- iOS Download: 앱스토어에서 **캡처캣**을 검색하세요 ([다운](https://apps.apple.com/kr/app/%EC%BA%A1%EC%B2%98%EC%BA%A3-capturecat-%EB%82%98%EB%A7%8C%EC%9D%98-%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7-%EC%A1%B0%EB%A0%A5%EC%9E%90/id6749074137))
- Android Download: 플레이스토어에서 **캡처캣**을 검색하세요 ([다운](https://play.google.com/store/apps/details?id=com.prography.capturecat&hl=ko))
- 개발 기간: 2025.04 ~ *current*

<br>
<br>

## 프로젝트 구조

### Multi-Module Architecture

<img width="2048" height="419" alt="Image" src="https://github.com/user-attachments/assets/f0d21733-1413-44ae-bf65-f6dea8178b1d" />


```
Screenshot-Android/
├── app/                           # Application 모듈
├── presentation/                  # Presentation 계층
├── core/
│   ├── data/                     # Data 계층 (Repository 구현체)
│   ├── database/                 # Room Database
│   ├── domain/                   # Domain 계층 (UseCase, Repository 인터페이스)
│   ├── navigation/               # Navigation 관련
│   ├── network/                  # Network 관련 (Retrofit)
│   ├── ui/                       # 공통 UI 컴포넌트
│   └── util/                     # 유틸리티 (권한, 이벤트 등)
└── feature/
    ├── auth/                     # 인증 관련
    ├── favorite/                 # 즐겨찾기
    ├── home/                     # 홈 (검색, 메인, 임시보관함)
    ├── imageDetail/              # 이미지 상세
    ├── organize/                 # 정리
    └── start/                    # 시작하기
```

## 기술 스택

### Architecture

- **Clean Architecture** + **MVI Pattern**
- **Multi-Module** 구조로 모듈간 의존성 분리
- **Repository Pattern**으로 데이터 계층 추상화

### UI

- **Jetpack Compose** - 선언형 UI
- **Material Design 3**
- **Navigation Compose** - 화면 네비게이션
- **Paging 3** - 무한 스크롤

### DI & Framework

- **Hilt** - 의존성 주입
- **ViewModel** - MVVM + MVI
- **StateFlow & SharedFlow** - 상태 관리 및 이벤트

### Network & Data

- **Retrofit** - HTTP 클라이언트
- **Room** - 로컬 데이터베이스
- **Coil** - 이미지 로딩
- **DataStore** - 로컬 캐시 저장

### Permission & Utils

- **Accompanist Permissions** - 권한 처리
- **Mixpanel** - 이벤트 트래킹
- **Timber** - 로깅

## 아키텍처 패턴

### MVI (Model-View-Intent)

```kotlin
// BaseComposeViewModel 구조
abstract class BaseComposeViewModel<UIState, UIEffect, UIAction> {
    // UIState: 화면 상태 (Model)
    // UIAction: 사용자 액션 (Intent)
    // UIEffect: 일회성 사이드 이펙트

    abstract fun handleAction(action: UIAction)
    protected fun updateState(transform: UIState.() -> UIState)
    protected fun emitEffect(effect: UIEffect)
}
```

### 단방향 데이터 플로우

```
User Input → UIAction → ViewModel → UseCase → Repository → UIState → UI
```

### ViewModel 구조

```kotlin
// Search 기능 예시
class SearchViewModel : BaseComposeViewModel<SearchState, SearchEffect, SearchAction> {
    override fun handleAction(action: SearchAction) {
        when (action) {
            is SearchAction.AddTag -> handleAddTag(action.tag)
            is SearchAction.RefreshSearchResults -> handleRefreshSearchResults()
            // ...
        }
    }
}
```

## 주요 기능

### 1. 스크린샷 관리

- **스크린샷 자동 감지** 및 분류
- **태그 기반 정리** - 커스텀 태그로 분류
- **즐겨찾기** 기능
- **미분류 스크린샷** 관리

### 2. 검색 & 필터링

- **태그 기반 검색** - 여러 태그 조합 검색 가능
- **인기 태그** 바로가기
- **연관 태그** 추천
- **실시간 검색 결과 업데이트**

### 3. 이미지 뷰어

- **태그 추가/삭제/수정**
- **즐겨찾기 토글**
- **이미지 삭제**
- **이전/다음 이미지 넘기기**

### 4. 권한 관리

- **스토리지 권한** 자동 요청
- **권한 거부 시 안내** 다이얼로그
- **설정 화면 연동**

## 데이터 동기화

### 전역 이벤트 관리

```kotlin
// SearchRefreshManager를 통한 화면간 데이터 동기화
@Singleton
class SearchRefreshManager {
    private val _refreshEvent = MutableSharedFlow<Unit>(replay = 1)
    val refreshEvent: SharedFlow<Unit> = _refreshEvent.asSharedFlow()

    fun triggerRefresh() {
        _refreshEvent.tryEmit(Unit)
    }
}
```

**동작 방식:**

1. ImageDetail에서 태그 변경 시 `triggerRefresh()` 호출
2. Search 화면에서 `refreshEvent` 구독하여 자동 새로고침
3. Navigation 구조와 무관하게 전역 이벤트로 동기화