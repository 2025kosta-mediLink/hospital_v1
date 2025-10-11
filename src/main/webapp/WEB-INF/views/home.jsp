<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>홈</title>
    <style>
        /* ===== 토큰 ===== */
        :root{
            --bg:#f5f7fb; --card:#fff; --text:#111827; --muted:#6b7280;
            --line:#e5e7eb; --primary:#2563eb; --primary-weak:#eff6ff;
            --shadow:0 6px 18px rgba(0,0,0,.06);
            --wrap-max:640px;            /* 최대 컨테이너 폭 */
            --tabbar-h:72px;             /* 하단 탭바 높이 */
        }
        *{box-sizing:border-box}
        body{
            margin:0; background:var(--bg); color:var(--text);
            font:14px/1.45 system-ui,-apple-system,"Segoe UI",Roboto,AppleSDGothicNeo,"Noto Sans KR","Malgun Gothic",sans-serif;
        }

        /* 전체 래퍼: 하단 고정 탭바가 내용 가리지 않게 padding-bottom 크게 */
        .wrap{
            max-width:var(--wrap-max); margin:0 auto;
            padding-bottom:calc(var(--tabbar-h) + 24px + env(safe-area-inset-bottom));
        }

        /* ===== 히어로(상단 이미지) =====
           - 이미지를 꽉 채우게 cover/center
           - 바로 아래 카드가 '떠 있는' 느낌이 나도록 .container에 음수 margin-top 적용 */
        .hero{
            height:200px;
            background-image:url('https://images.unsplash.com/photo-1584433144859-1fc3ab64a957?q=80&w=1200&auto=format&fit=crop');
            background-size:cover;            /* 이미지 꽉 채우기 */
            background-position:center;       /* 중앙 기준 자르기 */
        }

        /* 히어로 아래 콘텐츠. 음수 마진으로 카드가 살짝 겹치게 */
        .container{padding:0 16px; margin-top:-28px;}

        /* 공통 카드 패널 */
        .panel{
            background:var(--card);
            border:1px solid #eef1f6;
            border-radius:18px;
            box-shadow:var(--shadow);
            padding:16px;
        }
        .panel + .panel{margin-top:12px;}

        .row{display:flex; align-items:center; justify-content:space-between;}
        .h1{font-size:18px; font-weight:900;}
        .muted{color:var(--muted);}
        .chip{
            display:inline-flex; align-items:center; gap:6px;
            padding:6px 10px; border-radius:999px;
            background:#eef2ff; color:#1d4ed8; font-weight:800;
            font-size:12px;
        }
        .big{font-size:24px; font-weight:900;}
        .list{display:grid; row-gap:6px; margin-top:8px;}
        .dot{width:4px; height:4px; border-radius:50%; background:#d1d5db; display:inline-block; margin:0 8px 2px;}

        /* 퀵 액션 */
        .grid3{display:grid; grid-template-columns:repeat(3,1fr); gap:10px; margin-top:12px;}
        .quick{
            background:#f8fafc; border:1px solid var(--line); border-radius:14px;
            padding:14px; text-align:center; text-decoration:none; color:#111; font-weight:800;
        }
        .quick:hover{background:#eef2f7;}

        /* ===== 하단 탭바 (디자인 시안처럼 기기 하단 고정) =====
           - viewport 하단 고정 (fixed)
           - 가운데 정렬: left:50% + translateX(-50%)
           - 컨테이너 폭만 차지: width:min(640px, 100vw)
           - 안전 영역(safe-area) 반영 */
        .nav{
            position:fixed; left:50%; transform:translateX(-50%);
            bottom:0;
            width:min(var(--wrap-max), 100vw);
            background:var(--card);
            border-top:1px solid var(--line);
            box-shadow:0 -6px 18px rgba(0,0,0,.06);
            padding:8px 6px calc(8px + env(safe-area-inset-bottom));
            display:flex; justify-content:space-around; align-items:center;
            height:var(--tabbar-h);
            z-index:999;
        }
        .nav a{
            color:#4b5563; text-decoration:none; font-weight:700;
            padding:10px 12px; border-radius:10px; display:inline-flex; align-items:center; gap:6px;
        }
        .nav a.active{color:var(--primary); background:#eff6ff;}
        /* 모바일에서 글자 조금 작게 */
        @media (max-width:420px){ .nav a{font-size:12px;} }
    </style>
</head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="wrap">
    <!-- 히어로: 가로 꽉차고 이미지가 넘치면 중앙 기준으로 잘림 -->
    <div class="hero" aria-hidden="true"></div>

    <div class="container">
        <!-- 오늘의 예약일정 (히어로 위에 떠보이는 카드 느낌) -->
        <section class="panel" aria-labelledby="today-heading">
            <div class="row">
                <h2 id="today-heading" class="h1">오늘의 예약일정</h2>
                <span class="chip">📅 <fmt:formatDate value="<%= new java.util.Date() %>" pattern="yyyy.MM.dd (E)"/></span>
            </div>

            <div class="list">
                <c:choose>
                    <c:when test="${not empty summary.appointmentAt}">
                        <div class="muted">
                            <c:out value="${summary.departmentName}"/> 진료과
                            <span class="dot"></span>
                            의료진: <c:out value="${summary.doctorName}"/>
                        </div>
                        <div class="big"><fmt:formatDate value="${summary.appointmentAt}" pattern="a hh:mm"/></div>
                    </c:when>
                    <c:otherwise>
                        <div class="muted">오늘 예약된 일정이 없습니다.</div>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="row" style="margin-top:12px;">
                <div class="muted">대기 순번</div>
                <div class="big"><c:out value="${empty summary.myQueueNo ? '-' : summary.myQueueNo}"/></div>
            </div>
        </section>

        <!-- 퀵 액션 -->
        <section class="panel" aria-label="빠른 실행">
            <div class="grid3">
                <a class="quick" href="${ctx}/v1/reservations/list">진료예약</a>
                <a class="quick" href="${ctx}/v1/reservations/today">진료접수</a>
                <a class="quick" href="${ctx}/v1/prescriptions/list">처방전</a>
            </div>
        </section>

        <!-- 진료시간 안내 -->
        <section class="panel" aria-labelledby="hours-heading">
            <h2 id="hours-heading" class="h1">진료시간 안내</h2>
            <div class="list" role="list">
                <div role="listitem">월~금&nbsp;&nbsp;오전 9:00 ~ 오후 6:00</div>
                <div role="listitem">토&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;오전 9:00 ~ 오후 1:00</div>
                <div class="muted" role="listitem">점심시간&nbsp; 오후 12:30 ~ 오후 1:30</div>
            </div>
            <div class="list" style="margin-top:10px;">
                <div class="muted">문의: 1588-1234 (09:00~18:00)</div>
            </div>
        </section>
    </div>

    <!-- 하단 탭바: 기기 하단 고정 + 가운데 정렬 -->
    <nav class="nav" aria-label="하단 내비게이션">
        <a href="${ctx}/v1/reservations/list">예약</a>
        <a href="${ctx}/v1/reception/list">접수내역</a>
        <a class="active" href="${ctx}/">홈</a>
        <a href="${ctx}/v1/prescriptions/list">처방전</a>
        <a href="${ctx}/v1/member/mypage">마이페이지</a>
    </nav>
</div>
</body>
</html>
