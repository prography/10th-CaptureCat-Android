# prography-android
프로그라피 10기 Android 과제전형 레포지토리입니다.

<br>

## 📱 앱 화면 구성

| **메인 화면** | **랜덤 포토** | **포토 디테일** |
|--------------|--------------|--------------|
| <img src="https://github.com/user-attachments/assets/2817f3c7-f862-4ad3-b2e7-a6c5a48122ba" width="270" height="510.5" /> | <img src="https://github.com/user-attachments/assets/773dc85d-4acc-4baf-8eac-1ca008814ded" width="270" height="510.5" /> | <img src="https://github.com/user-attachments/assets/5c434adc-8e70-4884-930e-810e56a18b9c" width="270" height="510.5" /> |

<br>

| **메인 화면** | **카드 스와이프** | **북마크 관리** |
|--------------|--------------|--------------|
| ![11-ezgif com-video-to-gif-converter (1)](https://github.com/user-attachments/assets/bfc73004-4827-46f6-a06c-7e45a7cbb195) | ![1-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/d8847c0b-4831-40fe-baf4-f9578674a5e8) | ![1-ezgif com-video-to-gif-converter (1)](https://github.com/user-attachments/assets/0d797b37-4133-4583-a295-079baf6e520b) |


<br>



## 💻 기술 스택

### Development Tools
- Android Studio

### Architecture
- MVVM
- Clean Architecture

### Network & API
- Retrofit : HTTP 클라이언트
- OkHttp : 네트워크 요청 관리

### Dependency Injection
- Hilt : 의존성 주입(DI)

### Jetpack (AndroidX)
- Room : 로컬 DB 활용 (북마크 이미지 저장)
- LiveData : 데이터 변경 감지
- ViewModel : UI 관련 데이터 관리

### UI Components
- RecyclerView : 리스트 UI 구현
- ContainerView : Fragment 연결 뷰
- CardStackView : 랜덤 카드 스와이프 기능 구현
- Shimmer : 스켈레톤 로딩 효과

### 기타 라이브러리
- Glide : 이미지 로딩 라이브러리
