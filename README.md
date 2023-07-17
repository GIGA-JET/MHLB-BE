+ 23.07.17 Status null 해결
+ 23.07.11 이메일 발송 비동기 처리
+ 23.07.10 TODO 수정, 패키지명 대문자에서 소문자로 수정, 시간 측정 AOP 생성
+ 23.07.04 README.md 백엔드 정보만 있던 것을 팀 전체 정보로 수정, TODO 추가

<br/>

# 📍 PIN ME

### 온라인으로 협업을 하는 사람들이 편리하게 상태 관리를 할 수 있는 서비스

**Pin me**는 핀을 뽑고 꼽듯이 자신의 상태를 핀으로 표시할 수 있는 서비스입니다. 협업을 하면서 팀원들의 상태를 한 눈에 볼 수 있고 본인의 핀을 드래그 앤 드랍으로 쉽고 편리하게 변경할 수 있습니다.

<br/>

## 💡 주요 기능

🔹 **실시간 상태 변경**

- 다른 멤버가 상태를 변경하면 앱 메인 페이지에서 실시간으로 핀이 옮겨지는 것을 볼 수 있습니다.

🔹 **실시간 알림(채팅, 초대)**

- 워크스페이스에서 초대 요청이 오면 헤더의 프로필사진에 실시간으로 알림뱃지가 생깁니다.
- 실시간으로 안읽은 메시지가 있는지 확인하고 알림 뱃지로 보여줍니다.

🔹 **채팅**

- 메인페이지 오른쪽 바를 열어 팀원과 채팅을 할 수 있습니다.
- 채팅방 목록에서 아직 읽지 않은 메시지를 확인할 수 있습니다.
- 멤버 목록에서 원하는 멤버를 클릭하면 채팅을 바로 시작할 수 있습니다.
- 무한 스크롤을 이용하여 이전 대화 내용을 불러옵니다.

🔹 **초대 메일 발송**

- 비회원을 초대하면 회원가입 페이지로 유도하는 초대 메일을 발송합니다.

🔹 **드래그 앤 드랍**

- 총 7개의 상태로 나눠진 박스에 본인의 핀을 드래그 앤 드랍으로 옮겨서 상태를 보여줄 수 있고 오른쪽 바의 사람 목록에서 해당 워크스페이스의 멤버들이 표시한 상태와 함께 표시됩니다.
- 앱 메인페이지 왼쪽 바에서 드래그 앤 드랍으로 워크스페이스의 순서를 변경할 수 있습니다. 변경한 워크스페이스 순서는 워크스페이스 선택 페이지에서도 반영이 됩니다.

<br/>

# 📅 프로젝트 기간

2023.03.10 - 2023.04.21

<br />

# ⚡️ Giga Jet Team

| Role | Name | 담당 기능                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             | GitHub                                        |
| :--- |:----:|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------| :-------------------------------------------- |
| FE👑 | 황지상  | Non-Dev : 팀 리더, 팀 관리, 기술 리더, 디자인 총괄, 기획<br/>Dev : Auth(로그인/회원가입, 계정 찾기, 소셜 로그인, 벨리데이션 및 예외처리, 페이지 스타일링), 워크스페이스 선택 화면(워크스페이스 목록 불러오기, 워크스페이스 현재 인원 표기, 워크스페이스 최근 메세지 표기, 워크스페이스 생성 모달 스타일링, 페이지 스타일링), Unauthenticated 페이지 NavBar, Authenticated 페이지 NavBar, 앱 메인스페이스 왼쪽 바 워크스페이스 목록(워크스페이스 순서 드래그앤 드랍으로 변경, 웹소켓으로 새로운 메세지 알림 및 메세지 업데이트), 앱 워크스페이스 메인 페이지(드래그앤 드롭으로 실시간 상태 변경, 실시간 팀원 상태 변경, 반응형 레이아웃), QA 및 버그 핸들링, 웹 퍼블리싱, Amazon AWS(S3, Cloudfront, Route 53으로 프론트 서버 구축 및 배포)               | [GitHub](https://github.com/RyumForCode)      |
| FE   | 주혜인  | Non-Dev : 기획, Readme작성, Amplitude<br/>Dev : 실시간 채팅, 무한 스크롤(이전 대화 불러오기), 메인 페이지(멤버 목록, 채팅방 불러오기/검색), 관리 페이지(멤버 검색/추가/삭제/역할변경, 워크스페이스 삭제), 마이 페이지(프로필 수정,워크스페이스 탈퇴, 초대 거절/수락),  워크스페이스 생성, 이미지 업로드/용량 제한, 실시간 초대 요청 알림 뱃지, 토스트 알림, 랜딩 페이지, 에러 핸들링                                                                                                                                                                                                                                                   | [GitHub](https://github.com/Joohyein)         |
| BE👑 | 전다빈  | 기획, 팀 관리, 백엔드 기술 리더(코드 관리, 코드 리뷰, 코드 리팩토링, 기술 선택), 배포 및 관리, API 명세서(작성, 관리), ERD 설계<br/>CI/CD : Github Actions, Docker, AWS(ECR, VPC, ALB, ECS)<br/>모니터링(로깅, 지표) : CloudWatch<br/>시큐리티, 회원가입(비밀번호 암호화 알고리즘 변경), 로그인, 구글 회원가입/로그인, 이메일 발송(비밀번호 찾기, 워크스페이스 초대), 이메일용 임시코드 캐싱, 이미지 업로드(S3 bucket)/용량 제한/확장자 제한/리사이징(유저 프로필 이미지 변경, 워크스페이스 생성 이미지 업로드, 워크스페이스 이미지 변경), nav바 이미지 조회, swagger<br/>Websocket : STOMP 설정, 이벤트 핸들링(Connect 때 사용자 검사, Unsubscribe 이벤트 추가), 초대 알림 기능, 실시간 상태 변경, 안 읽은 메세지 알림 | [GitHub](https://github.com/samjan29)         |
| BE   | 권재현  | 워크스페이스 생성 및 조회, 워크스페이스 초대 ,유저 상태 저장 및 조회, 워크스페이스 관리 관련 CRUD, 외부브로커용 Redis 환경설정, 이전 메시지 페이지네이션                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  | [GitHub](https://github.com/Galmaeki)         |
| BE   | 홍우람  | 웹소켓 외부브로커 적용, 채팅기능에 몽고db적용<br/>마이페이지 초대 수락, 거절 기능 구현<br/>마이페이지 초대 되거나 속한 워크스페이스 목록 조회                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  | [GitHub](https://github.com/WooramHong1996)   |

<br />
<br />
<br />

# 📎 링크

[Frontend Repository](https://github.com/GIGA-JET/MHLB-FE)  
[Backend Repository](https://github.com/GIGA-JET/MHLB-BE)  
[Giga Jet Notion](https://daydreampioneer.notion.site/GIGA-JET-8dce960c73884b9499afa03b33e97ff4)

<br/>

# 💻 기술 스택

<div>

### Front-End

<img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=white"> 
<img src="https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white"> 
<img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=HTML5&logoColor=white"> 
<img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=JavaScript&logoColor=black"> 
<img src="https://img.shields.io/badge/redux-764abc?style=for-the-badge&logo=redux&logoColor=white"> 
<img src="https://img.shields.io/badge/SockJS-000000?style=for-the-badge&logo=SockJS&logoColor=white"> 
<img src="https://img.shields.io/badge/STOMP-000000?style=for-the-badge&logo=STOMP&logoColor=white"> 
<img src="https://img.shields.io/badge/styledComponents-DB7093?style=for-the-badge&logo=styledComponents&logoColor=white"> 
<img src="https://img.shields.io/badge/axios-5A29E4?style=for-the-badge&logo=axios&logoColor=white"> 
<img src="https://img.shields.io/badge/Framer-0055ff?style=for-the-badge&logo=Framer&logoColor=white"> 
<img src="https://img.shields.io/badge/ReactRouter-CA4245?style=for-the-badge&logo=ReactRouter&logoColor=white">

<br />
<br />

### Back-End

<img src="https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white"> 
<img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=Spring&logoColor=white"> 
<img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=SpringBoot&logoColor=white"> 
<img src="https://img.shields.io/badge/SpringSecurity-6DB33F?style=for-the-badge&logo=SpringSecurity&logoColor=white"> 
<img src="https://img.shields.io/badge/STOMP-000000?style=for-the-badge&logo=STOMP&logoColor=white">
<img src="https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=MongoDB&logoColor=white"> 
<img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white"> 
<img src="https://img.shields.io/badge/Amazon%20EC2-FF9900?style=for-the-badge&logo=AmazonEC2&logoColor=white"> 
<img src="https://img.shields.io/badge/Amazon%20S3-569A31?style=for-the-badge&logo=AmazonS3&logoColor=white"> 
<img src="https://img.shields.io/badge/Amazon%20RDS-527FFF?style=for-the-badge&logo=AmazonRDS&logoColor=white"> 
<img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white"> 
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white">

<br />

### Dev tools

<img src="https://img.shields.io/badge/Intellij%20IDEA-000000?style=for-the-badge&logo=IntellijIDEA&logoColor=white"> 
<img src="https://img.shields.io/badge/Visual%20Studio%20Code-007ACC?style=for-the-badge&logo=Visual%20Studio%20Code&logoColor=white"> 
<img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=Git&logoColor=white"> 
<img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white"> 
<img src="https://img.shields.io/badge/Jira-0052CC?style=for-the-badge&logo=Jira&logoColor=white"> 
<img src="https://img.shields.io/badge/Google%20Login-4285F4?style=for-the-badge&logo=Google&logoColor=white">
<img src="https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=Figma&logoColor=white">
<img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white">
</div>

<br />
<br />
<br />

## 🛠 프로젝트 아키텍쳐

<img width="1440" alt="front" src="https://user-images.githubusercontent.com/108849398/233392654-06216408-181d-4aa7-bdb3-b7ca7bab4655.png">

<img width="1440" alt="back" src="https://user-images.githubusercontent.com/108849398/233392677-6e82148d-010a-4c19-9565-40204ac34723.png">

## 📒 ERD

![eh6nvvmLaA5bRwwFg](https://user-images.githubusercontent.com/108849398/232505696-36cd9698-b741-4f6b-9bfc-c5e514bd8d3c.png)

<br/>

## 🔎 기술적 의사 결정

| 기술          | 사용 이유                                                                                                                                                                 | 구분   |
| ------------- |-----------------------------------------------------------------------------------------------------------------------------------------------------------------------| ------ |
| SockJS        | Sockjs를 사용하여 websocket을 지원하지 않는 브라우저에서 http기반의 다른 기술로 전환해 연결을 시도할 수 있다.                                                                                               | FE, BE |
| Stomp         | 프로그램 특성상 여러 구독을 해야하기 때문에 Pub/Sub 기능을 제공하는 Stomp를 사용하게 됐다.                                                                                                             | FE, BE |
| Axios         | Javascript에서 fetch를 대체하는 라이브러리이다. fetch에 없는 처리 방법(response timeout)이 있고 Promise 기반으로 만들어졌기 때문에 데이터를 다루기 편리하다는 장점이 있다. 또한, 크로스 브라우징 최적화로 브라우저 호환성이 우수하다.               | FE     |
| redux         | 스테이트를 중앙 관리하기 위해 도입했다.                                                                                                                                                | FE     |
| Typescript    | 주고 받는 데이터의 타입을 지정해서 통신이나 값을 관리할 때 오류를 방지하기 위해 도입                                                                                                                      | FE     |
| thumbnailator | 이미지 리사이징을 위한 라이브러리. pin me는 이미지 작업을 많이, 섬세하게 할 필요가 없는 프로그램이기 때문에 다른 라이브러리보다 가볍고 다루기 쉬운 thumbnailator를 선택하게 됐다.                                                        | BE     |
| tika          | multipart file 확장자를 확인하기 위해 의존성을 추가한 라이브러리. multipar file 객체의 content type을 확인하는 메서드로도 검사가 가능하지만, 중간에 content type의 값이 변경되면 원래 확장자를 확인할 수 없기 때문에 tika 라이브러리를 사용하게 됐다. | BE     |
| MongoDB       | 메세지를 영구적이면서 빠르고 쉽게 저장하고 조회할 수 있는 DB를 고르다 Redis보다 안정적이면서 RDBS보다 가벼운 MongoDB를 선택하게 됐다.                                                                                  | BE     |
| Redis         | 유효 기간을 설정해야 하거나, 채팅방에 들어와 있는 사람을 확인하기 위한 임시 데이터를 위한 캐시 메모리로 사용했다.                                                                                                     | BE     |