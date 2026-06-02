# Own Life OS 프로젝트 구조 설명

## 프로젝트 한줄 설명

Android Kotlin 라이프 관리 앱입니다. 할 일, 체크인, 결정, 회고, 예측, 회복 계획 데이터를 Room에 저장하고 도메인 분석 로직으로 하루 운영 상태와 추천을 계산합니다.

## 기본 작동 흐름

- Application/AppContainer가 데이터베이스, DAO, Repository, 도메인 서비스를 묶어 앱 전체 의존성을 구성합니다.
- UI 화면이 Repository를 통해 체크인, 할 일, 회고, 피드백 데이터를 읽고 씁니다.
- domain 계층의 분석/예측/자동화 클래스가 사용자의 상태를 계산해 추천 모드, 리밸런싱, 회복 계획을 만듭니다.

## 문서 기준

- 아래 목록은 `git ls-files`로 확인되는 Git 추적 파일을 기준으로 작성했습니다.
- `.git`, `node_modules`, `build`, `.gradle`, 임시 업로드/출력물처럼 Git이 관리하지 않는 폴더는 제외했습니다.
- 폴더 표는 코드와 자산이 어떤 책임으로 나뉘는지, 파일 표는 각 파일이 실제로 무엇을 담당하는지 설명합니다.

## 폴더별 설명 (56개)

| 폴더 | 설명 |
| --- | --- |
| `.` | 프로젝트 루트입니다. 실행/빌드 설정, README, 전체 구조 문서, 최상위 진입 파일이 모여 있습니다. |
| `app` | Android 앱 모듈입니다. 앱 전용 빌드 설정, 소스 코드, 리소스, ProGuard 설정이 이 아래에 있습니다. |
| `app/schemas` | Room 데이터베이스 스키마 JSON을 버전별로 저장합니다. 마이그레이션 검증과 DB 구조 추적에 사용됩니다. |
| `app/schemas/com.ownlifeos.data.local.AppDatabase` | Room 데이터베이스 스키마 JSON을 버전별로 저장합니다. 마이그레이션 검증과 DB 구조 추적에 사용됩니다. |
| `app/src` | Android 소스 세트가 들어 있는 상위 폴더입니다. main, test 같은 빌드 대상별 파일을 구분합니다. |
| `app/src/main` | 실제 앱에 포함되는 AndroidManifest, Kotlin/Java 소스, 리소스, 에셋을 담는 기본 소스 세트입니다. |
| `app/src/main/java` | 앱의 Kotlin/Java 패키지 루트입니다. 패키지명에 맞춰 실제 클래스 파일이 하위 폴더에 배치됩니다. |
| `app/src/main/java/com` | Kotlin 패키지 네임스페이스의 `com` 단계입니다. 실제 앱 패키지는 이 아래 `findmine`, `focussound`, `ownlifeos` 같은 이름으로 이어집니다. |
| `app/src/main/java/com/ownlifeos` | Own Life OS 앱의 최상위 Kotlin 패키지입니다. 화면 진입점과 주요 기능 패키지가 이 아래에서 갈라집니다. |
| `app/src/main/java/com/ownlifeos/data` | 앱 데이터 계층입니다. 로컬 DB, DAO, Entity, Repository처럼 저장소와 데이터 변환 코드를 담당합니다. |
| `app/src/main/java/com/ownlifeos/data/local` | 앱 데이터 계층입니다. 로컬 DB, DAO, Entity, Repository처럼 저장소와 데이터 변환 코드를 담당합니다. |
| `app/src/main/java/com/ownlifeos/data/local/converter` | 앱 데이터 계층입니다. 로컬 DB, DAO, Entity, Repository처럼 저장소와 데이터 변환 코드를 담당합니다. |
| `app/src/main/java/com/ownlifeos/data/local/dao` | 앱 데이터 계층입니다. 로컬 DB, DAO, Entity, Repository처럼 저장소와 데이터 변환 코드를 담당합니다. |
| `app/src/main/java/com/ownlifeos/data/local/entity` | 앱 데이터 계층입니다. 로컬 DB, DAO, Entity, Repository처럼 저장소와 데이터 변환 코드를 담당합니다. |
| `app/src/main/java/com/ownlifeos/data/local/migration` | 앱 데이터 계층입니다. 로컬 DB, DAO, Entity, Repository처럼 저장소와 데이터 변환 코드를 담당합니다. |
| `app/src/main/java/com/ownlifeos/data/repository` | 앱 데이터 계층입니다. 로컬 DB, DAO, Entity, Repository처럼 저장소와 데이터 변환 코드를 담당합니다. |
| `app/src/main/java/com/ownlifeos/domain` | 비즈니스 규칙과 분석 로직을 담는 도메인 계층의 상위 폴더입니다. |
| `app/src/main/java/com/ownlifeos/domain/analysis` | 사용자 기록과 상태를 분석해 추천, 점수, 리포트, 우선순위 같은 결과를 계산하는 폴더입니다. |
| `app/src/main/java/com/ownlifeos/domain/automation` | 체크인 추정, 회고 초안, 부담도 계산처럼 사용자의 입력을 보조하는 자동화 로직을 담습니다. |
| `app/src/main/java/com/ownlifeos/domain/model` | 앱 내부에서 주고받는 도메인 모델과 값 객체를 정의하는 폴더입니다. |
| `app/src/main/java/com/ownlifeos/domain/prediction` | 미래 부하, 회복 계획, 시뮬레이션 등 예측성 계산을 담당하는 폴더입니다. |
| `app/src/main/java/com/ownlifeos/domain/usecase` | usecase 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/main/java/com/ownlifeos/ui` | 화면, ViewModel, UI 상태처럼 사용자 인터페이스와 직접 연결되는 Kotlin 파일을 담습니다. |
| `app/src/main/java/com/ownlifeos/ui/components` | components 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/main/java/com/ownlifeos/ui/motion` | motion 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/main/java/com/ownlifeos/ui/navigation` | navigation 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/main/java/com/ownlifeos/ui/screens` | screens 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/decision` | decision 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/evening` | evening 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/forecast` | forecast 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/health` | health 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/home` | home 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/morning` | morning 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/pattern` | pattern 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/report` | report 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/simulation` | simulation 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/tasks` | tasks 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/main/java/com/ownlifeos/ui/theme` | theme 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/main/java/com/ownlifeos/util` | util 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/main/java/com/ownlifeos/widget` | widget 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/main/res` | Android XML 리소스 루트입니다. 문자열, 색상, 스타일, 아이콘, XML 설정처럼 코드가 참조하는 리소스를 보관합니다. |
| `app/src/main/res/drawable` | Android 벡터/드로어블 이미지 리소스 폴더입니다. 아이콘이나 그래픽 XML을 보관합니다. |
| `app/src/main/res/font` | font 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/main/res/layout` | layout 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/main/res/mipmap-anydpi` | Android 런처 아이콘처럼 해상도별 앱 아이콘 리소스를 보관합니다. |
| `app/src/main/res/values` | 문자열, 색상, 테마, 스타일 등 앱 전역 XML 값을 정의하는 리소스 폴더입니다. |
| `app/src/main/res/xml` | 백업 규칙, 파일 공유 경로, 데이터 추출 규칙처럼 Android 시스템에 전달하는 XML 설정을 보관합니다. |
| `app/src/test` | test 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/test/java` | java 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/test/java/com` | com 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/test/java/com/ownlifeos` | ownlifeos 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `app/src/test/java/com/ownlifeos/domain` | 비즈니스 규칙과 분석 로직을 담는 도메인 계층의 상위 폴더입니다. |
| `app/src/test/java/com/ownlifeos/domain/automation` | 체크인 추정, 회고 초안, 부담도 계산처럼 사용자의 입력을 보조하는 자동화 로직을 담습니다. |
| `app/src/test/java/com/ownlifeos/domain/usecase` | usecase 관련 파일을 기능별로 묶어 둔 폴더입니다. 같은 책임의 코드나 자산을 한 위치에서 관리하기 위해 사용합니다. |
| `gradle` | Gradle Wrapper와 데몬 설정처럼 Android/Kotlin 빌드 도구가 사용하는 파일을 보관합니다. |
| `gradle/wrapper` | 개발 PC에 Gradle이 없어도 동일한 버전으로 빌드할 수 있게 하는 Wrapper 실행 파일과 속성 파일을 보관합니다. |

## 파일별 설명 (156개)

| 파일 | 설명 |
| --- | --- |
| `.gitignore` | Git에 올리지 않을 빌드 산출물, 캐시, 개인 환경 파일을 지정하는 설정 파일입니다. 저장소에는 필요한 소스/자산만 남기도록 도와줍니다. |
| `app/build.gradle.kts` | Android 앱 모듈의 Gradle 빌드 설정입니다. SDK 버전, 의존성, Kotlin/Compose/Room 같은 모듈별 빌드 옵션을 지정합니다. |
| `app/schemas/com.ownlifeos.data.local.AppDatabase/1.json` | Room 데이터베이스 스키마 JSON입니다. 해당 버전의 테이블/컬럼 구조를 기록해 마이그레이션 검증에 사용합니다. |
| `app/schemas/com.ownlifeos.data.local.AppDatabase/2.json` | Room 데이터베이스 스키마 JSON입니다. 해당 버전의 테이블/컬럼 구조를 기록해 마이그레이션 검증에 사용합니다. |
| `app/schemas/com.ownlifeos.data.local.AppDatabase/3.json` | Room 데이터베이스 스키마 JSON입니다. 해당 버전의 테이블/컬럼 구조를 기록해 마이그레이션 검증에 사용합니다. |
| `app/schemas/com.ownlifeos.data.local.AppDatabase/4.json` | Room 데이터베이스 스키마 JSON입니다. 해당 버전의 테이블/컬럼 구조를 기록해 마이그레이션 검증에 사용합니다. |
| `app/src/main/AndroidManifest.xml` | Android 앱의 패키지 구성, Activity/Service, 권한, 파일 provider 같은 시스템 등록 정보를 선언합니다. |
| `app/src/main/java/com/ownlifeos/AppContainer.kt` | AppContainer Kotlin 소스입니다. 주 역할은 데이터베이스, Repository, 도메인 서비스를 생성해 앱 전체 의존성으로 묶는 구성 컨테이너 역할 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/AppDatabase.kt` | AppDatabase Kotlin 소스입니다. 주 역할은 Room 데이터베이스 정의와 Entity/DAO 연결 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/converter/TaskStatusConverter.kt` | TaskStatusConverter Kotlin 소스입니다. 주 역할은 DB 저장값과 Kotlin 타입 사이의 변환 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/dao/CheckInDao.kt` | Own Life OS의 Room DAO로, 해당 데이터 테이블의 조회/저장 쿼리를 정의합니다. CheckInDao Kotlin 소스입니다. 주 역할은 데이터베이스 접근 쿼리 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/dao/DailyMetricDao.kt` | Own Life OS의 Room DAO로, 해당 데이터 테이블의 조회/저장 쿼리를 정의합니다. DailyMetricDao Kotlin 소스입니다. 주 역할은 데이터베이스 접근 쿼리 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/dao/DecisionDao.kt` | Own Life OS의 Room DAO로, 해당 데이터 테이블의 조회/저장 쿼리를 정의합니다. DecisionDao Kotlin 소스입니다. 주 역할은 데이터베이스 접근 쿼리 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/dao/ErrorLogDao.kt` | Own Life OS의 Room DAO로, 해당 데이터 테이블의 조회/저장 쿼리를 정의합니다. ErrorLogDao Kotlin 소스입니다. 주 역할은 데이터베이스 접근 쿼리 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/dao/ForecastDao.kt` | Own Life OS의 Room DAO로, 해당 데이터 테이블의 조회/저장 쿼리를 정의합니다. ForecastDao Kotlin 소스입니다. 주 역할은 데이터베이스 접근 쿼리 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/dao/LifeSimulationDao.kt` | Own Life OS의 Room DAO로, 해당 데이터 테이블의 조회/저장 쿼리를 정의합니다. LifeSimulationDao Kotlin 소스입니다. 주 역할은 데이터베이스 접근 쿼리 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/dao/RecommendationFeedbackDao.kt` | Own Life OS의 Room DAO로, 해당 데이터 테이블의 조회/저장 쿼리를 정의합니다. RecommendationFeedbackDao Kotlin 소스입니다. 주 역할은 데이터베이스 접근 쿼리 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/dao/RecoveryPlanDao.kt` | Own Life OS의 Room DAO로, 해당 데이터 테이블의 조회/저장 쿼리를 정의합니다. RecoveryPlanDao Kotlin 소스입니다. 주 역할은 데이터베이스 접근 쿼리 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/dao/ReviewDao.kt` | Own Life OS의 Room DAO로, 해당 데이터 테이블의 조회/저장 쿼리를 정의합니다. ReviewDao Kotlin 소스입니다. 주 역할은 데이터베이스 접근 쿼리 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/dao/SystemHealthReportDao.kt` | Own Life OS의 Room DAO로, 해당 데이터 테이블의 조회/저장 쿼리를 정의합니다. SystemHealthReportDao Kotlin 소스입니다. 주 역할은 데이터베이스 접근 쿼리 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/dao/TaskDao.kt` | Own Life OS의 Room DAO로, 해당 데이터 테이블의 조회/저장 쿼리를 정의합니다. TaskDao Kotlin 소스입니다. 주 역할은 데이터베이스 접근 쿼리 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/dao/TaskEventDao.kt` | Own Life OS의 Room DAO로, 해당 데이터 테이블의 조회/저장 쿼리를 정의합니다. TaskEventDao Kotlin 소스입니다. 주 역할은 데이터베이스 접근 쿼리 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/entity/CheckInEntity.kt` | Own Life OS의 Room Entity 또는 DB-도메인 변환 코드로, 저장되는 데이터 구조를 정의합니다. CheckInEntity Kotlin 소스입니다. 주 역할은 저장 데이터 구조 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/entity/DailyMetricEntity.kt` | Own Life OS의 Room Entity 또는 DB-도메인 변환 코드로, 저장되는 데이터 구조를 정의합니다. DailyMetricEntity Kotlin 소스입니다. 주 역할은 저장 데이터 구조 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/entity/DecisionEntity.kt` | Own Life OS의 Room Entity 또는 DB-도메인 변환 코드로, 저장되는 데이터 구조를 정의합니다. DecisionEntity Kotlin 소스입니다. 주 역할은 저장 데이터 구조 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/entity/DecisionOutcomeEntity.kt` | Own Life OS의 Room Entity 또는 DB-도메인 변환 코드로, 저장되는 데이터 구조를 정의합니다. DecisionOutcomeEntity Kotlin 소스입니다. 주 역할은 저장 데이터 구조 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/entity/ForecastResultEntity.kt` | Own Life OS의 Room Entity 또는 DB-도메인 변환 코드로, 저장되는 데이터 구조를 정의합니다. ForecastResultEntity Kotlin 소스입니다. 주 역할은 저장 데이터 구조 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/entity/GeneratedErrorLogEntity.kt` | Own Life OS의 Room Entity 또는 DB-도메인 변환 코드로, 저장되는 데이터 구조를 정의합니다. GeneratedErrorLogEntity Kotlin 소스입니다. 주 역할은 저장 데이터 구조 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/entity/LifeSimulationEntity.kt` | Own Life OS의 Room Entity 또는 DB-도메인 변환 코드로, 저장되는 데이터 구조를 정의합니다. LifeSimulationEntity Kotlin 소스입니다. 주 역할은 저장 데이터 구조 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/entity/LifeTaskEntity.kt` | Own Life OS의 Room Entity 또는 DB-도메인 변환 코드로, 저장되는 데이터 구조를 정의합니다. LifeTaskEntity Kotlin 소스입니다. 주 역할은 저장 데이터 구조 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/entity/Mappers.kt` | Own Life OS의 Room Entity 또는 DB-도메인 변환 코드로, 저장되는 데이터 구조를 정의합니다. Mappers Kotlin 소스입니다. 주 역할은 Room Entity와 도메인 모델 사이의 변환 함수를 모아 DB 값과 화면/분석 값을 연결 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/entity/RecommendationFeedbackEntity.kt` | Own Life OS의 Room Entity 또는 DB-도메인 변환 코드로, 저장되는 데이터 구조를 정의합니다. RecommendationFeedbackEntity Kotlin 소스입니다. 주 역할은 저장 데이터 구조 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/entity/RecoveryPlanEntity.kt` | Own Life OS의 Room Entity 또는 DB-도메인 변환 코드로, 저장되는 데이터 구조를 정의합니다. RecoveryPlanEntity Kotlin 소스입니다. 주 역할은 저장 데이터 구조 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/entity/ReviewEntity.kt` | Own Life OS의 Room Entity 또는 DB-도메인 변환 코드로, 저장되는 데이터 구조를 정의합니다. ReviewEntity Kotlin 소스입니다. 주 역할은 저장 데이터 구조 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/entity/SystemHealthReportEntity.kt` | Own Life OS의 Room Entity 또는 DB-도메인 변환 코드로, 저장되는 데이터 구조를 정의합니다. SystemHealthReportEntity Kotlin 소스입니다. 주 역할은 저장 데이터 구조 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/entity/TaskEventEntity.kt` | Own Life OS의 Room Entity 또는 DB-도메인 변환 코드로, 저장되는 데이터 구조를 정의합니다. TaskEventEntity Kotlin 소스입니다. 주 역할은 저장 데이터 구조 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/local/migration/Migrations.kt` | Migrations Kotlin 소스입니다. 주 역할은 Room 데이터베이스 버전 변경 시 스키마 이전 규칙 정의 입니다. |
| `app/src/main/java/com/ownlifeos/data/repository/CheckInRepository.kt` | Own Life OS의 Repository로, DAO 호출을 감싸 화면/도메인 계층이 사용할 데이터 API를 제공합니다. CheckInRepository Kotlin 소스입니다. 주 역할은 데이터 계층과 화면/도메인 계층 연결 입니다. |
| `app/src/main/java/com/ownlifeos/data/repository/DailyMetricRepository.kt` | Own Life OS의 Repository로, DAO 호출을 감싸 화면/도메인 계층이 사용할 데이터 API를 제공합니다. DailyMetricRepository Kotlin 소스입니다. 주 역할은 데이터 계층과 화면/도메인 계층 연결 입니다. |
| `app/src/main/java/com/ownlifeos/data/repository/DecisionRepository.kt` | Own Life OS의 Repository로, DAO 호출을 감싸 화면/도메인 계층이 사용할 데이터 API를 제공합니다. DecisionRepository Kotlin 소스입니다. 주 역할은 데이터 계층과 화면/도메인 계층 연결 입니다. |
| `app/src/main/java/com/ownlifeos/data/repository/ForecastRepository.kt` | Own Life OS의 Repository로, DAO 호출을 감싸 화면/도메인 계층이 사용할 데이터 API를 제공합니다. ForecastRepository Kotlin 소스입니다. 주 역할은 데이터 계층과 화면/도메인 계층 연결 입니다. |
| `app/src/main/java/com/ownlifeos/data/repository/LifeSimulationRepository.kt` | Own Life OS의 Repository로, DAO 호출을 감싸 화면/도메인 계층이 사용할 데이터 API를 제공합니다. LifeSimulationRepository Kotlin 소스입니다. 주 역할은 데이터 계층과 화면/도메인 계층 연결 입니다. |
| `app/src/main/java/com/ownlifeos/data/repository/RecommendationFeedbackRepository.kt` | Own Life OS의 Repository로, DAO 호출을 감싸 화면/도메인 계층이 사용할 데이터 API를 제공합니다. RecommendationFeedbackRepository Kotlin 소스입니다. 주 역할은 데이터 계층과 화면/도메인 계층 연결 입니다. |
| `app/src/main/java/com/ownlifeos/data/repository/RecoveryPlanRepository.kt` | Own Life OS의 Repository로, DAO 호출을 감싸 화면/도메인 계층이 사용할 데이터 API를 제공합니다. RecoveryPlanRepository Kotlin 소스입니다. 주 역할은 데이터 계층과 화면/도메인 계층 연결 입니다. |
| `app/src/main/java/com/ownlifeos/data/repository/ReviewRepository.kt` | Own Life OS의 Repository로, DAO 호출을 감싸 화면/도메인 계층이 사용할 데이터 API를 제공합니다. ReviewRepository Kotlin 소스입니다. 주 역할은 데이터 계층과 화면/도메인 계층 연결 입니다. |
| `app/src/main/java/com/ownlifeos/data/repository/SystemHealthRepository.kt` | Own Life OS의 Repository로, DAO 호출을 감싸 화면/도메인 계층이 사용할 데이터 API를 제공합니다. SystemHealthRepository Kotlin 소스입니다. 주 역할은 데이터 계층과 화면/도메인 계층 연결 입니다. |
| `app/src/main/java/com/ownlifeos/data/repository/TaskRepository.kt` | Own Life OS의 Repository로, DAO 호출을 감싸 화면/도메인 계층이 사용할 데이터 API를 제공합니다. TaskRepository Kotlin 소스입니다. 주 역할은 데이터 계층과 화면/도메인 계층 연결 입니다. |
| `app/src/main/java/com/ownlifeos/domain/analysis/AnalysisInputs.kt` | Own Life OS의 분석 로직으로, 사용자의 하루 상태/패턴/리포트/우선순위를 계산합니다. AnalysisInputs Kotlin 소스입니다. 주 역할은 분석 엔진들이 공통으로 받는 체크인, 할 일, 지표, 회고 입력 데이터를 묶는 입력 모델 정의 입니다. |
| `app/src/main/java/com/ownlifeos/domain/analysis/DecisionRegretPredictor.kt` | Own Life OS의 분석 로직으로, 사용자의 하루 상태/패턴/리포트/우선순위를 계산합니다. DecisionRegretPredictor Kotlin 소스입니다. 주 역할은 입력 기록을 바탕으로 후회 가능성이나 미래 결과를 예측 입니다. |
| `app/src/main/java/com/ownlifeos/domain/analysis/ErrorLogGenerator.kt` | Own Life OS의 분석 로직으로, 사용자의 하루 상태/패턴/리포트/우선순위를 계산합니다. ErrorLogGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/ownlifeos/domain/analysis/LifeBatteryAnalyzer.kt` | Own Life OS의 분석 로직으로, 사용자의 하루 상태/패턴/리포트/우선순위를 계산합니다. LifeBatteryAnalyzer Kotlin 소스입니다. 주 역할은 입력 데이터 분석과 점수/상태 계산 입니다. |
| `app/src/main/java/com/ownlifeos/domain/analysis/OperatingPatternAnalyzer.kt` | Own Life OS의 분석 로직으로, 사용자의 하루 상태/패턴/리포트/우선순위를 계산합니다. OperatingPatternAnalyzer Kotlin 소스입니다. 주 역할은 입력 데이터 분석과 점수/상태 계산 입니다. |
| `app/src/main/java/com/ownlifeos/domain/analysis/ReasonBuilder.kt` | Own Life OS의 분석 로직으로, 사용자의 하루 상태/패턴/리포트/우선순위를 계산합니다. ReasonBuilder Kotlin 소스입니다. 주 역할은 분석 결과에 붙일 설명 문장과 추천 이유를 사람이 읽기 좋은 형태로 조립 입니다. |
| `app/src/main/java/com/ownlifeos/domain/analysis/TaskQueueRanker.kt` | Own Life OS의 분석 로직으로, 사용자의 하루 상태/패턴/리포트/우선순위를 계산합니다. TaskQueueRanker Kotlin 소스입니다. 주 역할은 작업 목록을 긴급도, 부담도, 회복 상태 기준으로 정렬해 처리 우선순위를 계산 입니다. |
| `app/src/main/java/com/ownlifeos/domain/analysis/TodayModeRecommender.kt` | Own Life OS의 분석 로직으로, 사용자의 하루 상태/패턴/리포트/우선순위를 계산합니다. TodayModeRecommender Kotlin 소스입니다. 주 역할은 사용자 상태에 맞는 추천 결과 생성 입니다. |
| `app/src/main/java/com/ownlifeos/domain/analysis/TodaySystemAnalyzer.kt` | Own Life OS의 분석 로직으로, 사용자의 하루 상태/패턴/리포트/우선순위를 계산합니다. TodaySystemAnalyzer Kotlin 소스입니다. 주 역할은 입력 데이터 분석과 점수/상태 계산 입니다. |
| `app/src/main/java/com/ownlifeos/domain/analysis/WeeklyReportGenerator.kt` | Own Life OS의 분석 로직으로, 사용자의 하루 상태/패턴/리포트/우선순위를 계산합니다. WeeklyReportGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/ownlifeos/domain/automation/CheckInEstimator.kt` | Own Life OS의 자동화 로직으로, 체크인/회고/작업 부담 계산을 보조합니다. CheckInEstimator Kotlin 소스입니다. 주 역할은 입력 부족 상황에서 필요한 값을 추정 입니다. |
| `app/src/main/java/com/ownlifeos/domain/automation/ReviewDraftGenerator.kt` | Own Life OS의 자동화 로직으로, 체크인/회고/작업 부담 계산을 보조합니다. ReviewDraftGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/ownlifeos/domain/automation/TaskBurdenEstimator.kt` | Own Life OS의 자동화 로직으로, 체크인/회고/작업 부담 계산을 보조합니다. TaskBurdenEstimator Kotlin 소스입니다. 주 역할은 입력 부족 상황에서 필요한 값을 추정 입니다. |
| `app/src/main/java/com/ownlifeos/domain/model/AnalysisModels.kt` | Own Life OS의 도메인 모델 정의로, 분석과 UI 사이에서 주고받는 값을 타입으로 표현합니다. AnalysisModels Kotlin 소스입니다. 주 역할은 도메인 또는 UI에서 쓰는 데이터 모델 정의 입니다. |
| `app/src/main/java/com/ownlifeos/domain/model/DailyModels.kt` | Own Life OS의 도메인 모델 정의로, 분석과 UI 사이에서 주고받는 값을 타입으로 표현합니다. DailyModels Kotlin 소스입니다. 주 역할은 도메인 또는 UI에서 쓰는 데이터 모델 정의 입니다. |
| `app/src/main/java/com/ownlifeos/domain/model/DecisionModels.kt` | Own Life OS의 도메인 모델 정의로, 분석과 UI 사이에서 주고받는 값을 타입으로 표현합니다. DecisionModels Kotlin 소스입니다. 주 역할은 도메인 또는 UI에서 쓰는 데이터 모델 정의 입니다. |
| `app/src/main/java/com/ownlifeos/domain/model/FeedbackModels.kt` | Own Life OS의 도메인 모델 정의로, 분석과 UI 사이에서 주고받는 값을 타입으로 표현합니다. FeedbackModels Kotlin 소스입니다. 주 역할은 도메인 또는 UI에서 쓰는 데이터 모델 정의 입니다. |
| `app/src/main/java/com/ownlifeos/domain/model/ForecastModels.kt` | Own Life OS의 도메인 모델 정의로, 분석과 UI 사이에서 주고받는 값을 타입으로 표현합니다. ForecastModels Kotlin 소스입니다. 주 역할은 도메인 또는 UI에서 쓰는 데이터 모델 정의 입니다. |
| `app/src/main/java/com/ownlifeos/domain/model/OperatingPatternModels.kt` | Own Life OS의 도메인 모델 정의로, 분석과 UI 사이에서 주고받는 값을 타입으로 표현합니다. OperatingPatternModels Kotlin 소스입니다. 주 역할은 도메인 또는 UI에서 쓰는 데이터 모델 정의 입니다. |
| `app/src/main/java/com/ownlifeos/domain/model/RebalanceModels.kt` | Own Life OS의 도메인 모델 정의로, 분석과 UI 사이에서 주고받는 값을 타입으로 표현합니다. RebalanceModels Kotlin 소스입니다. 주 역할은 도메인 또는 UI에서 쓰는 데이터 모델 정의 입니다. |
| `app/src/main/java/com/ownlifeos/domain/model/RecommendedMode.kt` | Own Life OS의 도메인 모델 정의로, 분석과 UI 사이에서 주고받는 값을 타입으로 표현합니다. RecommendedMode Kotlin 소스입니다. 주 역할은 오늘의 운영 모드나 추천 상태를 제한된 값으로 표현하는 도메인 enum/model 정의 입니다. |
| `app/src/main/java/com/ownlifeos/domain/model/RecoveryModels.kt` | Own Life OS의 도메인 모델 정의로, 분석과 UI 사이에서 주고받는 값을 타입으로 표현합니다. RecoveryModels Kotlin 소스입니다. 주 역할은 도메인 또는 UI에서 쓰는 데이터 모델 정의 입니다. |
| `app/src/main/java/com/ownlifeos/domain/model/SimulationModels.kt` | Own Life OS의 도메인 모델 정의로, 분석과 UI 사이에서 주고받는 값을 타입으로 표현합니다. SimulationModels Kotlin 소스입니다. 주 역할은 도메인 또는 UI에서 쓰는 데이터 모델 정의 입니다. |
| `app/src/main/java/com/ownlifeos/domain/model/SystemHealthModels.kt` | Own Life OS의 도메인 모델 정의로, 분석과 UI 사이에서 주고받는 값을 타입으로 표현합니다. SystemHealthModels Kotlin 소스입니다. 주 역할은 도메인 또는 UI에서 쓰는 데이터 모델 정의 입니다. |
| `app/src/main/java/com/ownlifeos/domain/model/TaskStatus.kt` | Own Life OS의 도메인 모델 정의로, 분석과 UI 사이에서 주고받는 값을 타입으로 표현합니다. TaskStatus Kotlin 소스입니다. 주 역할은 할 일의 진행 상태를 저장/표시할 수 있도록 상태 값을 정의 입니다. |
| `app/src/main/java/com/ownlifeos/domain/prediction/DayRebalancer.kt` | Own Life OS의 예측 로직으로, 미래 부하/회복/시뮬레이션 결과를 계산합니다. DayRebalancer Kotlin 소스입니다. 주 역할은 하루 계획을 에너지와 부하에 맞게 다시 배치하는 리밸런싱 계산 입니다. |
| `app/src/main/java/com/ownlifeos/domain/prediction/FutureLoadForecaster.kt` | Own Life OS의 예측 로직으로, 미래 부하/회복/시뮬레이션 결과를 계산합니다. FutureLoadForecaster Kotlin 소스입니다. 주 역할은 미래 상태나 부하 예측 입니다. |
| `app/src/main/java/com/ownlifeos/domain/prediction/LifeSimulator.kt` | Own Life OS의 예측 로직으로, 미래 부하/회복/시뮬레이션 결과를 계산합니다. LifeSimulator Kotlin 소스입니다. 주 역할은 가상 시나리오나 흐름 시뮬레이션 입니다. |
| `app/src/main/java/com/ownlifeos/domain/prediction/RecoveryPlanner.kt` | Own Life OS의 예측 로직으로, 미래 부하/회복/시뮬레이션 결과를 계산합니다. RecoveryPlanner Kotlin 소스입니다. 주 역할은 회복/일정/처리 계획 계산 입니다. |
| `app/src/main/java/com/ownlifeos/domain/prediction/SystemHealthAnalyzer.kt` | Own Life OS의 예측 로직으로, 미래 부하/회복/시뮬레이션 결과를 계산합니다. SystemHealthAnalyzer Kotlin 소스입니다. 주 역할은 입력 데이터 분석과 점수/상태 계산 입니다. |
| `app/src/main/java/com/ownlifeos/domain/usecase/EvaluateDecisionUseCase.kt` | EvaluateDecisionUseCase Kotlin 소스입니다. 주 역할은 Repository와 도메인 계산기를 연결해 화면에서 호출할 수 있는 단일 사용자 작업 흐름 실행 입니다. |
| `app/src/main/java/com/ownlifeos/domain/usecase/GetFutureLoadForecastUseCase.kt` | GetFutureLoadForecastUseCase Kotlin 소스입니다. 주 역할은 Repository와 도메인 계산기를 연결해 화면에서 호출할 수 있는 단일 사용자 작업 흐름 실행 입니다. |
| `app/src/main/java/com/ownlifeos/domain/usecase/GetRankedTaskQueueUseCase.kt` | GetRankedTaskQueueUseCase Kotlin 소스입니다. 주 역할은 Repository와 도메인 계산기를 연결해 화면에서 호출할 수 있는 단일 사용자 작업 흐름 실행 입니다. |
| `app/src/main/java/com/ownlifeos/domain/usecase/GetRebalancedDayPlanUseCase.kt` | GetRebalancedDayPlanUseCase Kotlin 소스입니다. 주 역할은 Repository와 도메인 계산기를 연결해 화면에서 호출할 수 있는 단일 사용자 작업 흐름 실행 입니다. |
| `app/src/main/java/com/ownlifeos/domain/usecase/GetRecoveryPlanUseCase.kt` | GetRecoveryPlanUseCase Kotlin 소스입니다. 주 역할은 Repository와 도메인 계산기를 연결해 화면에서 호출할 수 있는 단일 사용자 작업 흐름 실행 입니다. |
| `app/src/main/java/com/ownlifeos/domain/usecase/GetSystemHealthReportV3UseCase.kt` | GetSystemHealthReportV3UseCase Kotlin 소스입니다. 주 역할은 Repository와 도메인 계산기를 연결해 화면에서 호출할 수 있는 단일 사용자 작업 흐름 실행 입니다. |
| `app/src/main/java/com/ownlifeos/domain/usecase/GetTodaySystemAnalysisUseCase.kt` | GetTodaySystemAnalysisUseCase Kotlin 소스입니다. 주 역할은 Repository와 도메인 계산기를 연결해 화면에서 호출할 수 있는 단일 사용자 작업 흐름 실행 입니다. |
| `app/src/main/java/com/ownlifeos/domain/usecase/GetWeeklySystemReportUseCase.kt` | GetWeeklySystemReportUseCase Kotlin 소스입니다. 주 역할은 Repository와 도메인 계산기를 연결해 화면에서 호출할 수 있는 단일 사용자 작업 흐름 실행 입니다. |
| `app/src/main/java/com/ownlifeos/domain/usecase/LifeMetricsCalculator.kt` | LifeMetricsCalculator Kotlin 소스입니다. 주 역할은 체크인과 활동 데이터를 바탕으로 생활 지표, 점수, 요약 값을 계산 입니다. |
| `app/src/main/java/com/ownlifeos/domain/usecase/RunLifeSimulationUseCase.kt` | RunLifeSimulationUseCase Kotlin 소스입니다. 주 역할은 Repository와 도메인 계산기를 연결해 화면에서 호출할 수 있는 단일 사용자 작업 흐름 실행 입니다. |
| `app/src/main/java/com/ownlifeos/MainActivity.kt` | MainActivity Kotlin 소스입니다. 주 역할은 Android 화면 진입점과 UI 초기화 입니다. |
| `app/src/main/java/com/ownlifeos/OwnLifeApplication.kt` | OwnLifeApplication Kotlin 소스입니다. 주 역할은 앱 프로세스 초기화와 전역 의존성 준비 입니다. |
| `app/src/main/java/com/ownlifeos/OwnLifeOsApp.kt` | OwnLifeOsApp Kotlin 소스입니다. 주 역할은 Compose 앱 루트와 화면 내비게이션 구성 입니다. |
| `app/src/main/java/com/ownlifeos/ui/AppViewModelFactory.kt` | AppViewModelFactory Kotlin 소스입니다. 주 역할은 ViewModel이나 의존 객체를 필요한 인자와 함께 생성하는 팩토리 구성 입니다. |
| `app/src/main/java/com/ownlifeos/ui/components/FluidButton.kt` | FluidButton Kotlin 소스입니다. 주 역할은 여러 화면에서 재사용하는 Compose UI 컴포넌트 구성 입니다. |
| `app/src/main/java/com/ownlifeos/ui/components/LevelSelector.kt` | LevelSelector Kotlin 소스입니다. 주 역할은 여러 화면에서 재사용하는 Compose UI 컴포넌트 구성 입니다. |
| `app/src/main/java/com/ownlifeos/ui/components/MetricCard.kt` | MetricCard Kotlin 소스입니다. 주 역할은 여러 화면에서 재사용하는 Compose UI 컴포넌트 구성 입니다. |
| `app/src/main/java/com/ownlifeos/ui/components/OwnLifeDesign.kt` | OwnLifeDesign Kotlin 소스입니다. 주 역할은 여러 화면에서 재사용하는 Compose UI 컴포넌트 구성 입니다. |
| `app/src/main/java/com/ownlifeos/ui/components/OwnLifeTopBar.kt` | OwnLifeTopBar Kotlin 소스입니다. 주 역할은 여러 화면에서 재사용하는 Compose UI 컴포넌트 구성 입니다. |
| `app/src/main/java/com/ownlifeos/ui/components/ReasonList.kt` | ReasonList Kotlin 소스입니다. 주 역할은 여러 화면에서 재사용하는 Compose UI 컴포넌트 구성 입니다. |
| `app/src/main/java/com/ownlifeos/ui/components/SectionHeader.kt` | SectionHeader Kotlin 소스입니다. 주 역할은 여러 화면에서 재사용하는 Compose UI 컴포넌트 구성 입니다. |
| `app/src/main/java/com/ownlifeos/ui/components/TaskQueueItem.kt` | TaskQueueItem Kotlin 소스입니다. 주 역할은 여러 화면에서 재사용하는 Compose UI 컴포넌트 구성 입니다. |
| `app/src/main/java/com/ownlifeos/ui/motion/OwnLifeMotion.kt` | OwnLifeMotion Kotlin 소스입니다. 주 역할은 화면 전환과 애니메이션 움직임 값을 정의해 UI 반응을 조정 입니다. |
| `app/src/main/java/com/ownlifeos/ui/navigation/OwnLifeNavHost.kt` | OwnLifeNavHost Kotlin 소스입니다. 주 역할은 Compose 화면 이동 경로와 네비게이션 그래프를 정의 입니다. |
| `app/src/main/java/com/ownlifeos/ui/navigation/Routes.kt` | Routes Kotlin 소스입니다. 주 역할은 Compose 화면 이동 경로와 네비게이션 그래프를 정의 입니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/decision/DecisionCheckScreen.kt` | DecisionCheckScreen Kotlin 소스입니다. 주 역할은 해당 기능 화면의 Compose 레이아웃, 상태 표시, 사용자 입력 처리를 담당 입니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/decision/DecisionCheckViewModel.kt` | DecisionCheckViewModel Kotlin 소스입니다. 주 역할은 화면 상태, 이벤트 처리, 비동기 작업 관리 입니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/evening/EveningReviewScreen.kt` | EveningReviewScreen Kotlin 소스입니다. 주 역할은 해당 기능 화면의 Compose 레이아웃, 상태 표시, 사용자 입력 처리를 담당 입니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/evening/EveningReviewViewModel.kt` | EveningReviewViewModel Kotlin 소스입니다. 주 역할은 화면 상태, 이벤트 처리, 비동기 작업 관리 입니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/forecast/ForecastScreen.kt` | ForecastScreen Kotlin 소스입니다. 주 역할은 해당 기능 화면의 Compose 레이아웃, 상태 표시, 사용자 입력 처리를 담당 입니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/forecast/ForecastViewModel.kt` | ForecastViewModel Kotlin 소스입니다. 주 역할은 화면 상태, 이벤트 처리, 비동기 작업 관리 입니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/health/SystemHealthScreen.kt` | SystemHealthScreen Kotlin 소스입니다. 주 역할은 해당 기능 화면의 Compose 레이아웃, 상태 표시, 사용자 입력 처리를 담당 입니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/health/SystemHealthViewModel.kt` | SystemHealthViewModel Kotlin 소스입니다. 주 역할은 화면 상태, 이벤트 처리, 비동기 작업 관리 입니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/home/HomeScreen.kt` | HomeScreen Kotlin 소스입니다. 주 역할은 해당 기능 화면의 Compose 레이아웃, 상태 표시, 사용자 입력 처리를 담당 입니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/home/HomeViewModel.kt` | HomeViewModel Kotlin 소스입니다. 주 역할은 화면 상태, 이벤트 처리, 비동기 작업 관리 입니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/morning/MorningCheckInScreen.kt` | MorningCheckInScreen Kotlin 소스입니다. 주 역할은 해당 기능 화면의 Compose 레이아웃, 상태 표시, 사용자 입력 처리를 담당 입니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/morning/MorningCheckInViewModel.kt` | MorningCheckInViewModel Kotlin 소스입니다. 주 역할은 화면 상태, 이벤트 처리, 비동기 작업 관리 입니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/pattern/OperatingPatternScreen.kt` | OperatingPatternScreen Kotlin 소스입니다. 주 역할은 해당 기능 화면의 Compose 레이아웃, 상태 표시, 사용자 입력 처리를 담당 입니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/pattern/OperatingPatternViewModel.kt` | OperatingPatternViewModel Kotlin 소스입니다. 주 역할은 화면 상태, 이벤트 처리, 비동기 작업 관리 입니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/report/WeeklyReportScreen.kt` | WeeklyReportScreen Kotlin 소스입니다. 주 역할은 해당 기능 화면의 Compose 레이아웃, 상태 표시, 사용자 입력 처리를 담당 입니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/report/WeeklyReportViewModel.kt` | WeeklyReportViewModel Kotlin 소스입니다. 주 역할은 화면 상태, 이벤트 처리, 비동기 작업 관리 입니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/simulation/LifeSimulationScreen.kt` | LifeSimulationScreen Kotlin 소스입니다. 주 역할은 해당 기능 화면의 Compose 레이아웃, 상태 표시, 사용자 입력 처리를 담당 입니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/simulation/LifeSimulationViewModel.kt` | LifeSimulationViewModel Kotlin 소스입니다. 주 역할은 화면 상태, 이벤트 처리, 비동기 작업 관리 입니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/tasks/TaskQueueScreen.kt` | TaskQueueScreen Kotlin 소스입니다. 주 역할은 해당 기능 화면의 Compose 레이아웃, 상태 표시, 사용자 입력 처리를 담당 입니다. |
| `app/src/main/java/com/ownlifeos/ui/screens/tasks/TaskQueueViewModel.kt` | TaskQueueViewModel Kotlin 소스입니다. 주 역할은 화면 상태, 이벤트 처리, 비동기 작업 관리 입니다. |
| `app/src/main/java/com/ownlifeos/ui/theme/Color.kt` | Color Kotlin 소스입니다. 주 역할은 Compose 앱의 색상, 글꼴, 테마 값을 정의해 UI 스타일을 통일 입니다. |
| `app/src/main/java/com/ownlifeos/ui/theme/Theme.kt` | Theme Kotlin 소스입니다. 주 역할은 Compose 앱의 색상, 글꼴, 테마 값을 정의해 UI 스타일을 통일 입니다. |
| `app/src/main/java/com/ownlifeos/ui/theme/Type.kt` | Type Kotlin 소스입니다. 주 역할은 Compose 앱의 색상, 글꼴, 테마 값을 정의해 UI 스타일을 통일 입니다. |
| `app/src/main/java/com/ownlifeos/util/DateUtils.kt` | DateUtils Kotlin 소스입니다. 주 역할은 여러 파일에서 재사용하는 날짜/문자열/계산 보조 함수 제공 입니다. |
| `app/src/main/java/com/ownlifeos/widget/OwnLifeWidgetProvider.kt` | OwnLifeWidgetProvider Kotlin 소스입니다. 주 역할은 Android 홈 화면 위젯을 생성하고 업데이트 요청을 처리 입니다. |
| `app/src/main/java/com/ownlifeos/widget/WidgetSnapshotStore.kt` | WidgetSnapshotStore Kotlin 소스입니다. 주 역할은 위젯이나 화면이 재사용할 현재 상태 스냅샷을 저장하고 읽는 저장소 역할 입니다. |
| `app/src/main/res/drawable/ic_launcher_foreground.xml` | Android 적응형 런처 아이콘의 전경 그래픽을 정의합니다. |
| `app/src/main/res/drawable/ic_launcher_monochrome.xml` | Android 13 이상 단색 테마 아이콘에 사용할 런처 아이콘 드로어블 XML입니다. |
| `app/src/main/res/drawable/widget_background.xml` | Own Life OS 홈 화면 위젯의 배경 색상, 모서리, 형태를 정의하는 드로어블 XML입니다. |
| `app/src/main/res/font/pretendard_variable.ttf` | Pretendard Variable 폰트 파일입니다. 앱 화면의 한글/영문 텍스트를 지정한 글꼴로 렌더링할 때 사용합니다. |
| `app/src/main/res/layout/widget_own_life.xml` | Own Life OS Android 홈 화면 위젯의 레이아웃 XML입니다. 위젯에 표시될 텍스트와 배치를 정의합니다. |
| `app/src/main/res/mipmap-anydpi/ic_launcher.xml` | Android 런처 아이콘 또는 적응형 아이콘 구성을 정의하는 XML 리소스입니다. |
| `app/src/main/res/mipmap-anydpi/ic_launcher_round.xml` | Android 원형 런처 아이콘 리소스입니다. 기기 런처가 roundIcon을 요구할 때 사용합니다. |
| `app/src/main/res/values/colors.xml` | 앱에서 사용하는 색상 리소스를 이름으로 정의합니다. |
| `app/src/main/res/values/strings.xml` | 앱에서 표시하는 문자열 리소스를 한 곳에 모아 다국어 처리와 재사용을 쉽게 합니다. |
| `app/src/main/res/values/styles.xml` | 앱 테마와 공통 스타일을 정의해 화면 전반의 색상/폰트/컴포넌트 모양을 통일합니다. |
| `app/src/main/res/xml/backup_rules.xml` | Android 자동 백업에 포함하거나 제외할 앱 데이터를 지정하는 XML 규칙입니다. |
| `app/src/main/res/xml/data_extraction_rules.xml` | Android 데이터 추출/백업 정책에서 어떤 데이터를 이동 가능한지 지정하는 XML 규칙입니다. |
| `app/src/main/res/xml/own_life_widget_info.xml` | Own Life OS 앱 위젯의 크기, 갱신 주기, 미리보기, 초기 레이아웃을 Android 시스템에 등록하는 AppWidgetProvider XML입니다. |
| `app/src/test/java/com/ownlifeos/domain/automation/AutomationEnginesTest.kt` | AutomationEnginesTest Kotlin 소스입니다. 주 역할은 해당 도메인/유스케이스/자동화 로직이 기대한 계산 결과를 내는지 검증하는 테스트 입니다. |
| `app/src/test/java/com/ownlifeos/domain/usecase/LifeMetricsCalculatorTest.kt` | LifeMetricsCalculatorTest Kotlin 소스입니다. 주 역할은 해당 도메인/유스케이스/자동화 로직이 기대한 계산 결과를 내는지 검증하는 테스트 입니다. |
| `app/src/test/java/com/ownlifeos/domain/usecase/OperatingPatternAnalyzerTest.kt` | OperatingPatternAnalyzerTest Kotlin 소스입니다. 주 역할은 해당 도메인/유스케이스/자동화 로직이 기대한 계산 결과를 내는지 검증하는 테스트 입니다. |
| `app/src/test/java/com/ownlifeos/domain/usecase/V2AnalysisTest.kt` | V2AnalysisTest Kotlin 소스입니다. 주 역할은 해당 도메인/유스케이스/자동화 로직이 기대한 계산 결과를 내는지 검증하는 테스트 입니다. |
| `app/src/test/java/com/ownlifeos/domain/usecase/V3PredictionTest.kt` | V3PredictionTest Kotlin 소스입니다. 주 역할은 해당 도메인/유스케이스/자동화 로직이 기대한 계산 결과를 내는지 검증하는 테스트 입니다. |
| `build.gradle.kts` | 루트 Gradle 빌드 설정입니다. Android/Kotlin 플러그인과 전체 프로젝트 빌드 구성을 정의합니다. |
| `gradle.properties` | Gradle 빌드 성능, AndroidX 사용 여부, Kotlin/빌드 옵션 같은 공통 속성을 지정합니다. |
| `gradle/gradle-daemon-jvm.properties` | Gradle 데몬이 사용할 JVM 관련 속성을 지정해 빌드 환경을 일정하게 유지합니다. |
| `gradle/libs.versions.toml` | Gradle Version Catalog 파일입니다. 플러그인과 라이브러리 버전을 한 곳에서 관리해 모듈 빌드 설정이 같은 버전을 참조하게 합니다. |
| `gradle/wrapper/gradle-wrapper.jar` | Gradle Wrapper가 지정된 Gradle 버전을 내려받고 실행하는 데 사용하는 바이너리 파일입니다. |
| `gradle/wrapper/gradle-wrapper.properties` | Gradle Wrapper가 사용할 Gradle 배포판 버전과 다운로드 URL을 지정합니다. |
| `gradlew` | Unix/macOS/Linux에서 Gradle Wrapper를 실행하는 스크립트입니다. |
| `gradlew.bat` | Windows에서 Gradle Wrapper를 실행하는 배치 스크립트입니다. |
| `PROJECT_STRUCTURE.md` | 프로젝트의 모든 주요 폴더와 Git 추적 파일을 한글로 설명하는 구조 문서입니다. 처음 보는 사람이 경로별 역할을 빠르게 파악하기 위해 추가했습니다. |
| `settings.gradle.kts` | Gradle이 인식할 프로젝트 이름과 포함할 모듈을 지정하는 설정 파일입니다. |

## 읽는 방법

- 먼저 폴더별 설명에서 큰 기능 묶음을 확인한 다음, 파일별 설명에서 실제 구현 파일을 찾으면 됩니다.
- Android 프로젝트는 `app/src/main/java` 아래 Kotlin 파일이 핵심 코드이고, `app/src/main/res`와 `app/src/main/assets`는 화면/모델/오디오 자산입니다.
- 웹 프로젝트는 `index.html`, `styles.css`, `script.js` 또는 `app.js`가 화면 구조, 스타일, 동작을 나눠 담당합니다.
- Python 프로젝트는 루트의 실행 스크립트와 `src`, `backend`, `scripts`, `tests` 폴더를 함께 보면 처리 흐름을 이해할 수 있습니다.
