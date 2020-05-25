# TripGuide Cloud Firestore Reference Guide
본 문서는 TripGuide(가칭)의 데이터 저장을 위한 Cloud Firestore의 구조에 대해 설명한다.
## 1. TripGuide(가칭) DB의 구조

Cloud Firestore는 크게 컬렉션과 문서로 이루어져 있다.
컬렉션 안에 들어있는 문서들의 집합이라고 생각해도 무방함.
### 1. Users 컬렉션
User 컬렉션 내부의 각각의 문서가 각각의 유저를 의미한다. 문서의 이름은 회원가입시 부여된 **UID**로 함.

### 1.1. User 컬렉션의 필드
#### name
	유저의 이름을 저장하는 필드
	type: String
#### user_language
	유저가 구사 가능한 언어를 저장하는 필드
	type: array[String]
#### location
	유저의 거주 지역 또는 여행 지역을 저장하는 필드
	type: String
#### user_keyword
	유저가 자신의 관심 항목으로 지정한 키워드를 저장하는 필드
	type: array[String]
#### user_profile_photo
	유저의 프로필 사진 경로를 저장하는 필드
	프로필 사진은 firebase storage에 업로드 한 후 그 URL을 받아와 이 필드에 저장한다.
	type: String
#### age
	유저의 나이를 저장하는 필드
	유저의 프로필 입력 내용에 따라 존재할 수도, 존재하지 않을 수도 있음
	type:number
#### match_cart
	매칭을 위해 장바구니에 담아놓은 유저들을 저장하는 필드
	배열 안에 장바구니에 추가한 유저의 레퍼런스로 저장
	type:array(reference)

### 1.2 User 컬렉션의 하위 컬렉션

### 1.2.1 duration 컬렉션
duration 컬렉션은 각각의 설정한 기간에 따라서 문서가 추가됨. 문서의 이름은 미정
#### date_start
	기간의 시작시점을 저장하는 필드
	type: timestamp

#### date_end
	기간의 종료시점을 저장하는 필드
	type: timestamp

### 1.2.2 point 컬렉션
	point 컬렉션은 유저가 획득, 충전한 포인트 내역 또는 사용된 포인트 내역이 저장되는 컬렉션
	이 컬렉션에 생성되는 각각의 문서가 내역이 됨
#### timestamp
	포인트 변동이 생긴 시간을 저장하는 필드
	type: timestamp

#### value
	변동된 포인트의 값을 저장하는 필드
	type:number

### 2. Feeds 컬렉션
Feeds 컬렉션은 사용자가 작성한 feed들이 저장되는 컬렉션
### 2.1. Feeds 컬렉션의 필드

#### uid
	피드를 작성한 사용자의 uid
	* uid를 레퍼런스로 할지 스트링으로 할지 확인 필요함 *
	type: String
	
#### author
	피드를 작성한 사용자의 이름
	피드에 표시하기 위해 필요
	type: String

#### content
	피드의 내용을 저장하는 필드
	type: String

#### hits
	피드의 조회수를 저장하는 필드
	type: number

#### photos
	피드의 사진을 저장하는 필드
	배열로 여러 개의 사진을 저장 가능
	사진은 URL의 형태로 저장
	type: array[String]

#### time_first
	피드가 처음 게시된 시간을 저장하는 필드
	type: timestamp

#### time_modified
	피드가 수정된 시간을 저장하는 필드
	* 필요하지 않을 수도 있음*
	type: timestamp
	
### 2.2. Feeds 컬렉션의 하위 컬렉션

### 2.2.1. likes 컬렉션
피드에 좋아요를 누른 사람과 횟수를 저장하기 위한 서브 컬렉션
문서의 이름은 자유롭게 지정
#### uid
	피드에 좋아요를 누른 사람의 uid를 저장하는 필드
	type: String


### 3. Matching 컬렉션
유저간의 매칭 상태를 저장하기 위한 컬렉션
하위 컬렉션으로 채팅 컬렉션이 있어 채팅시 이곳으로 접근
문서의 이름은 자유롭게

#### uid_q
	질문자의 uid를 저장하는 필드
	* uid 말고 레퍼런스로 지정할 수 있지 않을까? *
	type:String

#### uid_a
	답변자의 uid를 저장하는 필드
	* uid 말고 레퍼런스로 지정할 수 있지 않을까? *
	type:String
	
### 3.1. Matching 컬렉션의 하위 컬렉션

### 3.1.1. Chat 컬렉션
유저 간의 채팅을 저장하는 컬렉션
각각의 채팅 내역을 문서로 저장
#### message
	메시지의 내용을 저장하는 필드
	type: String
#### timestamp
	메시지 발송 시간을 저장하는 필드
	type: timestamp
#### uid
	메시지를 보낸 사람의 uid를 저장하는 필드
	type: String
#### username
	채팅방에 표시될 이름(메시지 보낸 사람의 이름)
	type: String

### 4. Payment 컬렉션
추후 결제 시스템 구현을 위해 결제 내역을 저장하는 컬렉션
문서의 이름은 자유롭게 지정

#### date
	결제일을 저장하는 필드
	type: timestamp

#### uid
	결제한 사용자의 uid를 저장하는 필드
	* 레퍼런스를 이용해도 되지 않을까? *
	type: String

#### value
	결제한 액수 혹은 포인트를 저장하는 필드
	type: number


## 2. DB 다이어그램

![enter image description here](https://raw.githubusercontent.com/vsc0705/TripGuide_SW/master/DB/Diagram.svg)
