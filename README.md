# [네트워크보안 연구실] IoT 기술 활용 스마트 제어 프로젝트

## 🔍 개요
1. 소개
- 네트워크보안 연구실 부원만이 사용할 수 있는 랩실 고유 어플리케이션
- 외부에서 랩실 내부 상태 조회가 가능한 기능 제공
2. 작업기간 : 2019.12 ~ 2020.03
3. 역할분배
- 기획 : 김소현, 이주완
- Front-end 개발 : 김소현, 이민규, 이주완
- Back-end 개발 : 김민규, 최한수 (코드 비공개)
- 보안 : 이주완, 최한수

## 🔗 프로젝트 설계
![image](https://github.com/sinw212/IoT_Control/assets/53486320/c9964abb-b77c-44c4-8170-efec55727b79)

## 📌 기능 요구사항
### Application
- 회원가입
- 로그인(관리자모드/랩실부원모드)
- 자동로그인
- 비밀번호 재발급

- 관리자모드
	- 신규 회원 정보 등록
	- 일정 등록
	- 회의록 등록
	- 인원현황 등록
	- 조직도/구성도/IP,출입키 현황 등록
	- 랩실 규칙 등록

- 랩실부원모드
	- 일정 조회
	- 회의록 조회
	- 인원현황 조회
	- 조직도/구성도/IP,출입키 현황 조회
	- 랩실 규칙 조회
	- 재실/커피/A4 상태 조회

### IoT
- 재실여부 확인 - 초음파센서
- 전등제어(재실여부에 따른 불 켜고 끄기) - 서보모터
- 커피 잔여량 확인 - 무게센서
- A4용지 잔여량 확인 - 무게센서

### 위젯
- 오늘 일정 조회(최대 2개)
- 재실여부/커피잔여량/A4잔여량 조회
- 크기 : 4X2

### 구현 화면
<img src="https://github.com/sinw212/IoT_Control/assets/53486320/8ff5fdcb-d49a-49da-b6f0-e03230172020" width="600" height="800"/>

## 사용물품
| 품목 | 품명 | 규격 |
|:---:|:---:|:---:|
| 와이파이쉴드 | 우노 WIFI ESP8266 보드 | 5핀 |
| 초음파센서 | HC-SR04 | |
| 서보모터 | SG90 | |
| 로드셀 무게센서 | DM865 | 3선식 |
| 로드셀 센서 HX711 모듈 | DM940 | |
