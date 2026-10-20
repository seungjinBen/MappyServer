# MappyEnglish Server

유럽 각 장소의 사진·영상을 보며 그 상황에 맞는 영어 회화를 익히고, **내 발음을 AI가 채점**해 주는 여행 영어 학습 서비스의 백엔드입니다.
(Spring Boot 3.5 · Java 17 · PostgreSQL · OpenAI Whisper / GPT-4o-mini / TTS · Firebase Storage · Kakao OAuth · JWT)

## 핵심: 하이브리드 발화 채점 흐름

```
사용자 음성(webm)
   │
   ▼
Whisper STT ──▶ 인식된 문장
   │
   ▼
레벤슈타인 유사도 계산 (정답 문장 대비)
   │
   ├─ ≥ 85  ──▶ 즉시 100점 반환        (GPT 호출 없음 → 비용·지연 절감)
   │
   └─ < 85  ──▶ GPT-4o-mini 의미 채점 ──▶ 점수(0~100) + 한국어 피드백
```

- 글자가 거의 같은 경우는 문자열 비교만으로 끝내고, 표현이 달라진 경우에만 LLM이 **의미가 같은지**를 판단합니다.
- 구현: `app/service/SttService.java` (`/api/stt/evaluate` 음성, `/api/stt/evaluate-text` 타이핑)

## 그 외 기능

| 영역 | 설명 | 주요 엔드포인트 |
|---|---|---|
| 대화 생성 | 장소·화자 조건으로 GPT-4o-mini가 3~5턴 영어 대화 생성 (관리자) | `POST /api/ai/generate` |
| TTS 자동 생성 | 대화 저장 시 문장별 OpenAI TTS → Firebase Storage 업로드. 텍스트가 바뀐 문장만 재생성 | `POST /api/conversations` |
| 장소 / 대화 | 장소 목록·상세, 섹션별 대화, 장소명·한국어 해석 통합 검색 | `GET /api/places/**`, `GET /api/conversations/**` |
| 인증 | 이메일 회원가입/로그인, 카카오 OAuth, JWT, 회원 탈퇴, 역할 기반 권한 | `POST /api/auth/{register,login,kakao}` |
| 미션 / 북마크 | 미션 완료 기록·진도 조회, 대화 저장 | `/api/missions/**`, `/api/bookmarks/**` |
| 기타 | 문의 전송, 헬스체크 | `POST /api/contact/send`, `GET /api/ping` |

## 실행

```bash
# 1. 비밀 값 설정 (파일은 .gitignore 대상)
cp src/main/resources/application-secret.properties.example src/main/resources/application-secret.properties
#    → DB_PASSWORD, JWT_SECRET_KEY, OPENAI_API_KEY, KAKAO_CLIENT_ID 입력
#    → Firebase 서비스 계정 키를 src/main/resources/firebase-key.json 에 배치

# 2. 실행 (PostgreSQL mappy DB 필요)
./gradlew bootRun
```

운영 환경에서는 같은 이름의 환경변수로 주입하면 됩니다.

## 프론트엔드

Next.js로 별도 저장소에서 관리합니다. (초기 CRA 버전은 이 저장소 이력에서 제거)
