# 🔐 시크릿 자산 관리부 (Secret Asset Manager)

Java Swing(GUI)과 MySQL 데이터베이스를 기반으로 구축한 **보안이 강화된 개인 자산 관리 프로그램**입니다.
단순한 가계부를 넘어, 사용자 인증(CAPTCHA, 보안 질문)을 통해 민감한 금융 데이터를 보호하며, 직관적인 그래프 통계와 위시리스트 기능을 체계적인 자산 관리를 돕습니다.

## 💡 주요 기능 (Key Features)

### 🛡️ 1. 보안 및 인증 (Security & Auth)
* **보안 로그인**: ID/PW 검증 및 로그인 실패 시 **CAPTCHA(자동 입력 방지)** 시스템 작동
* **계정 보호**: 회원가입 시 아이디 중복 체크 및 보안 질문(Security Question) 설정
* **비밀번호 찾기**: 이름, 전화번호, 보안 질문의 3단계 검증을 통한 안전한 비밀번호 재설정

### 💰 2. 자산 관리 (Asset Management)
* **수입/지출 내역**: 날짜, 카테고리(식비, 교통비 등), 금액, 메모를 포함한 내역 등록/수정/삭제 (CRUD)
* **실시간 잔액**: 수입과 지출을 합산하여 현재 남은 자산을 메인 대시보드에 즉시 반영
* **검색 및 필터**: 날짜별, 카테고리별 내역 검색 기능

### 📊 3. 시각화 및 목표 (Visualization & Goals)
* **통계 그래프**: `Graphics2D`를 활용하여 카테고리별 지출 비중을 시각적인 차트로 제공
* **위시리스트**: 사고 싶은 물건의 **이미지**와 목표 금액을 등록하여 저축 동기 부여
* **달력 보기**: 월별 수입/지출 흐름을 한눈에 파악할 수 있는 캘린더 뷰 제공

## 🛠 기술 스택 (Tech Stack)
* **Language**: Java (JDK 22)
* **IDE**: Apache NetBeans 22
* **Database**: MySQL 8.0
* **GUI**: Java Swing (NetBeans GUI Builder, Graphics2D)
* **Library**: MySQL Connector/J 9.1.0
* **Version Control**: GitHub & GitHub Desktop

## 📂 프로젝트 구조 (Folder Structure)
```text
Secret_Asset_Manager/
├── src/                        # Java 소스 코드 (LoginFrame, MainFrame, DB_MAN 등)
├── lib/                        # 외부 라이브러리 (mysql-connector-j-9.1.0.jar)
├── database_schema.sql         # DB 초기화 및 테이블 생성 SQL 스크립트
└── README.md                   # 프로젝트 설명서
```
🚀 설치 및 실행 가이드 (Getting Started)
팀원 및 사용자는 아래 순서대로 개발 환경을 세팅해 주세요.

1. 프로젝트 불러오기 (Clone)
GitHub Desktop을 사용하여 리포지토리를 로컬(예: Documents/GitHub/)로 복제합니다.

(GitHub Desktop에서): File -> Clone repository -> 해당 프로젝트 선택

2. NetBeans 프로젝트 열기
NetBeans 실행 -> 상단 메뉴 File -> Open Project 클릭

Clone 받은 폴더 내의 Secret_Asset_Manager 폴더를 선택하여 엽니다.

3. 라이브러리 설정 (중요)
⚠️ Note: 이 과정을 건너뛰거나 개인 경로(내문서 등)의 파일을 추가하면 빌드 오류가 발생합니다. 반드시 프로젝트 내부 파일을 사용하세요.

NetBeans 좌측 프로젝트 트리에서 Libraries 폴더 우클릭 -> Add JAR/Folder 클릭

반드시 프로젝트 폴더 내부에 있는 lib/mysql-connector-j-9.1.0.jar 파일을 선택하여 추가합니다.

4. 데이터베이스 설정
MySQL Workbench 실행 및 로그인 (MySQL_local)

상단 메뉴 File -> Open SQL Script 클릭 후, 프로젝트 폴더 내 database_schema.sql 파일 선택

쿼리창 상단의 **번개 아이콘(Execute ⚡)**을 클릭하여 secret_asset 스키마와 테이블 3개 생성

NetBeans에서 src/DB_MAN.java 파일을 열고, strPWD 변수를 본인의 DB 비밀번호로 수정
