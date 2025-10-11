<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt"  uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title>상세 접수 내역</title>
  <style>
    :root{
      --bg:#f5f7fb; --card:#fff; --text:#111827; --muted:#6b7280;
      --line:#e5e7eb; --primary:#2563eb; --primary-weak:#eff6ff;
      --shadow:0 6px 18px rgba(0,0,0,.06); --radius:16px;
      --wrap-max:640px;   /* 컨텐츠 최대 폭 */
      --tabbar-h:72px;    /* 하단바 높이 */
    }
    *{box-sizing:border-box}
    body{
      margin:0; background:var(--bg); color:var(--text);
      font:14px/1.45 system-ui,-apple-system,"Segoe UI",Roboto,AppleSDGothicNeo,"Noto Sans KR","Malgun Gothic",sans-serif;
    }
    .wrap{
      max-width:640px;
      margin:20px auto;
      /* 기존: padding:0 16px 84px; */
      padding:0 16px calc(var(--tabbar-h) + 24px + env(safe-area-inset-bottom));
    }

    /* 헤더 */
    .topbar{display:flex; align-items:center; justify-content:space-between; margin-bottom:12px;}
    .title{font-weight:800; font-size:20px; letter-spacing:-.2px;}
    .back, .home{
      display:inline-flex; align-items:center; justify-content:center;
      width:36px; height:36px; border:1px solid var(--line); border-radius:10px;
      background:#fff; box-shadow:var(--shadow); cursor:pointer; text-decoration:none; color:#111; font-weight:900;
    }

    /* 카드 공통 */
    .card{
      background:var(--card); border-radius:16px; padding:16px;
      box-shadow:var(--shadow); margin-bottom:14px; border:1px solid #eef1f6;
    }
    .card-head{display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;}
    .rec-no{font-weight:900; font-size:16px;}
    .badge{font-size:12px; font-weight:900; padding:4px 10px; border-radius:999px; display:inline-block; letter-spacing:.3px;}
    .b-wait{background:#fff7ed; color:#c2410c; border:1px solid #fed7aa;}
    .b-in{background:#eff6ff; color:#1d4ed8; border:1px solid #bfdbfe;}
    .b-done{background:#ecfdf5; color:#047857; border:1px solid #a7f3d0;}
    .b-cancel{background:#fef2f2; color:#b91c1c; border:1px solid #fecaca;}

    .grid{display:grid; grid-template-columns:auto 1fr; row-gap:6px; column-gap:10px;}
    .label{color:var(--muted); width:92px;}
    .val{font-weight:800; word-break:break-word;}

    /* 섹션 타이틀 */
    .section-title{display:flex; align-items:center; gap:8px; font-weight:900; margin-bottom:8px;}
    .section-title .dot{width:8px;height:8px;border-radius:999px;background:var(--primary); display:inline-block;}

    /* 강조 박스 */
    .note-box{background:#f8fafc; border:1px solid var(--line); border-radius:12px; padding:12px;}
    .info-box{background:var(--primary-weak); border:1px solid #dbeafe; border-radius:12px; padding:12px;}
    .warn-box{background:#fef2f2; border:1px solid #fecaca; border-radius:12px; padding:12px; color:#991b1b;}

    /* 버튼 */
    .actions{display:flex; gap:8px; margin-top:12px;}
    .btn{flex:1; text-align:center; padding:12px; border-radius:12px; text-decoration:none; font-weight:900; display:inline-block;}
    .btn-primary{background:var(--primary); color:#fff;}
    .btn-ghost{background:#f8fafc; color:#111; border:1px solid var(--line);}
    .btn-danger{background:#ef4444; color:#fff;}
    .btn:hover{opacity:.95;}

    /* 빈 상태 */
    .empty{background:var(--card); border:1px dashed var(--line); padding:24px; border-radius:12px; color:var(--muted); text-align:center;}

    /* 하단 네비 */
    /* 공통 하단 네비 */
    .nav{
      /* 기존: position:sticky; bottom:0; left:0; right:0; ... */
      position:fixed;
      left:50%; transform:translateX(-50%);
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
    .nav a{color:#4b5563; text-decoration:none; font-weight:700; padding:8px 10px; border-radius:10px;}
    .nav a.active{color:var(--primary); background:#eff6ff;}
  </style>
</head>
<body>
<div class="wrap">

  <!-- 헤더 -->
  <div class="topbar">
    <a class="back" href="javascript:history.back()">←</a>
    <div class="title">상세 접수 내역</div>
    <a class="home" href="${pageContext.request.contextPath}/">⌂</a>
  </div>

  <!-- 본문 -->
  <c:choose>
    <c:when test="${empty reception}">
      <div class="empty">해당 접수 내역을 찾을 수 없습니다.</div>
    </c:when>

    <c:otherwise>
      <!-- 접수 요약 -->
      <div class="card">
        <div class="card-head">
          <div class="rec-no">접수번호 <c:out value="${reception.receptionNo}"/>번</div>
          <c:choose>
            <c:when test="${reception.status=='WAITING'}"><span class="badge b-wait">대기</span></c:when>
            <c:when test="${reception.status=='IN_SERVICE'}"><span class="badge b-in">진료중</span></c:when>
            <c:when test="${reception.status=='DONE'}"><span class="badge b-done">완료</span></c:when>
            <c:when test="${reception.status=='CANCELLED' || reception.status=='CANCELED'}"><span class="badge b-cancel">취소</span></c:when>
            <c:otherwise><span class="badge">기타</span></c:otherwise>
          </c:choose>
        </div>

        <div class="grid">
          <div class="label">진료과</div>
          <div class="val"><c:out value="${reception.departmentName}"/></div>

          <div class="label">의료진</div>
          <div class="val"><c:out value="${reception.doctorName}"/></div>

          <div class="label">접수유형</div>
          <div class="val"><c:out value="${reception.type}"/></div>

          <div class="label">접수일시</div>
          <div class="val"><c:out value="${reception.createdAt}"/></div>
        </div>
      </div>

      <!-- 증상/전달사항 -->
      <div class="card">
        <div class="section-title"><span class="dot"></span>증상 및 전달사항</div>
        <div class="info-box" style="margin-bottom:10px;">
          <div class="label" style="color:var(--muted); margin-bottom:4px;">주요 증상</div>
          <div class="val"><c:out value="${empty reception.symptomNames ? '—' : reception.symptomNames}"/></div>
        </div>
        <div class="note-box">
          <div class="label" style="color:var(--muted); margin-bottom:4px;">의사에게 전달</div>
          <div class="val" style="white-space:pre-line;"><c:out value="${empty reception.noteToDoctor ? '—' : reception.noteToDoctor}"/></div>
        </div>
      </div>

      <!-- 동의 정보 및 안내 -->
      <div class="card">
        <div class="section-title"><span class="dot"></span>안내 및 동의</div>
        <div class="grid" style="margin-bottom:10px;">
          <div class="label">안내 동의</div>
          <div class="val">
            <c:choose>
              <c:when test="${reception.consentNotice}">동의함</c:when>
              <c:otherwise>미동의</c:otherwise>
            </c:choose>
          </div>

          <div class="label">동의시각</div>
          <div class="val"><c:out value="${empty reception.consentAt ? '—' : reception.consentAt}"/></div>
        </div>

        <div class="warn-box">
          <ul style="margin:0; padding-left:18px;">
            <li>호출 후 10분이 지나면 접수가 자동 취소될 수 있어요.</li>
            <li>신분증을 꼭 지참해 주세요.</li>
            <li>푸시 알림 및 대기현황을 수시로 확인해 주세요.</li>
          </ul>
        </div>
      </div>

      <!-- 액션 -->
      <div class="card">
        <div class="actions">
          <!-- 상태별 액션 가이드 -->
          <c:choose>
            <c:when test="${reception.status=='DONE'}">
              <a class="btn btn-primary" href="${pageContext.request.contextPath}/v1/prescriptions/${reception.receptionId}">
                처방전 보기
              </a>
              <a class="btn btn-ghost" href="${pageContext.request.contextPath}/v1/reception/list">목록으로</a>
            </c:when>

            <c:when test="${reception.status=='WAITING'}">
              <form method="post" action="${pageContext.request.contextPath}/v1/reception/${reception.receptionId}/cancel" style="flex:1;">
                <button type="submit" class="btn btn-danger" style="width:100%;">접수 취소</button>
              </form>
              <a class="btn btn-ghost" href="${pageContext.request.contextPath}/v1/reception/list">목록으로</a>
            </c:when>

            <c:otherwise>
              <a class="btn btn-ghost" href="${pageContext.request.contextPath}/v1/reception/list">목록으로</a>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </c:otherwise>
  </c:choose>

  <!-- 하단 네비 -->
  <div class="nav">
    <a href="${pageContext.request.contextPath}/">홈</a>
    <a href="${pageContext.request.contextPath}/v1/reception/departments">접수</a>
    <a class="active" href="${pageContext.request.contextPath}/v1/reception/list">접수내역</a>
  </div>

</div>
</body>
</html>
