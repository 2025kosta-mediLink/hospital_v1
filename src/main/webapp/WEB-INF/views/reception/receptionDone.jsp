<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title>접수 완료</title>
  <style>
    :root{
      --bg:#f5f7fb; --card:#fff; --text:#111827; --muted:#6b7280;
      --line:#e5e7eb; --primary:#2563eb; --primary-weak:#eff6ff;
      --shadow:0 6px 18px rgba(0,0,0,.06); --radius:16px;
    }
    *{box-sizing:border-box}
    body{
      margin:0; background:var(--bg); color:var(--text);
      font:14px/1.45 system-ui,-apple-system,"Segoe UI",Roboto,AppleSDGothicNeo,"Noto Sans KR","Malgun Gothic",sans-serif;
    }
    .wrap{max-width:640px; margin:20px auto; padding:0 16px 84px;}

    /* 상단바 */
    .topbar{display:flex; align-items:center; justify-content:space-between; margin-bottom:12px}
    .title{font-weight:800; font-size:18px}
    .btn{
      display:inline-flex; align-items:center; gap:6px;
      border:1px solid var(--line); background:#fff;
      padding:8px 10px; border-radius:10px; cursor:pointer;
      color:#111; text-decoration:none; font-weight:800; box-shadow:var(--shadow)
    }
    .btn:hover{ background:#f8fafc }
    .chev{font-weight:900}

    /* 완료 카드 */
    .card{
      background:var(--card); border:1px solid #eef1f6; border-radius:20px;
      box-shadow:var(--shadow); padding:24px 20px; margin-top:20px;
    }
    .ok{
      width:64px; height:64px; border-radius:999px;
      display:grid; place-items:center; margin:0 auto 14px;
      background:var(--primary-weak); color:var(--primary); font-size:28px; font-weight:900;
    }
    .headline{ text-align:center; margin:4px 0 6px; font-weight:900; font-size:18px }
    .sub{ color:var(--muted); text-align:center; margin:0 0 16px }

    /* 접수번호 박스 */
    .num-wrap{
      background:#f3f6ff; border:1px solid #e1e8ff; border-radius:12px;
      padding:12px 10px; margin:0 auto 8px; width:min(360px,90%); text-align:center;
    }
    .num-label{ color:var(--muted); font-weight:800; letter-spacing:.2px }
    .num{
      display:block; font-weight:900; font-size:40px; line-height:1.1; margin-top:4px; letter-spacing:.3px;
    }

    /* 상세 안내 */
    .bullets{ margin:14px auto 6px; width:min(420px,92%); color:#334155 }
    .bullets li{ margin:8px 0 }

    /* 보조 정보(진료과/의료진/상태) */
    .meta{
      margin:14px auto 0; width:min(420px,92%);
      display:grid; grid-template-columns:80px 1fr; row-gap:6px; column-gap:8px;
      color:#111;
    }
    .k{ color:var(--muted) }
    .v{ font-weight:800 }

    /* 버튼 영역 */
    .actions{ margin-top:16px; display:flex; gap:10px; justify-content:center }
    .btn-primary{
      flex:1; max-width:360px; text-align:center;
      background:var(--primary); color:#fff; border:0; border-radius:12px; padding:14px 16px;
      font-weight:800; cursor:pointer; box-shadow:var(--shadow); font-size:15px;
      text-decoration:none; display:inline-block;
    }
    .btn-ghost{
      flex:1; max-width:360px; text-align:center;
      background:#f8fafc; color:#111; border:1px solid var(--line); border-radius:12px; padding:12px 14px;
      font-weight:800; text-decoration:none; display:inline-block;
    }

    /* 하단 네비 */
    .nav{
      position:sticky; bottom:0; left:0; right:0; background:var(--card);
      border-top:1px solid var(--line); box-shadow:0 -6px 18px rgba(0,0,0,.06);
      padding:calc(8px + env(safe-area-inset-bottom)) 6px 8px; display:flex; justify-content:space-around; z-index:50;
    }
    .nav a{ color:#4b5563; text-decoration:none; font-weight:700; padding:8px 10px; border-radius:10px }
    .nav a.active{ color:var(--primary); background:#eff6ff }

    /* 빈 상태 */
    .empty{ background:var(--card); border:1px dashed var(--line); padding:24px; border-radius:12px; color:var(--muted); text-align:center; margin-top:20px }
  </style>

  <script>
    function goBackOr(url){
      if (document.referrer) history.back();
      else location.href = url;
    }
  </script>
</head>
<body>
<div class="wrap">

  <!-- 상단바 -->
  <div class="topbar">
    <button class="btn" type="button" onclick="goBackOr('${pageContext.request.contextPath}/v1/reception/list')">
      <span class="chev">←</span>
    </button>
    <div class="title">접수 완료</div>
    <a class="btn" href="${pageContext.request.contextPath}/v1/departments">닫기</a>
  </div>

  <c:choose>
    <c:when test="${not empty reception}">
      <div class="card">
        <div class="ok">✓</div>
        <div class="headline">접수가 완료되었습니다.</div>
        <div class="sub">정상적으로 접수처리 되었습니다.</div>

        <div class="num-wrap">
          <div class="num-label">접수번호</div>
          <span class="num">
            <c:out value="${reception.receptionNo}" />
          </span>
        </div>

        <ul class="bullets">
          <li>앱으로 알림을 드립니다.</li>
          <li>환자 호명 후 10분 지나면 접수가 취소됩니다.</li>
          <li>대기 현황은 실시간으로 업데이트 됩니다.</li>
        </ul>

        <div class="meta">
          <div class="k">진료과</div>
          <div class="v"><c:out value="${reception.departmentName}" /></div>
          <div class="k">의료진</div>
          <div class="v"><c:out value="${reception.doctorName}" /></div>
          <div class="k">상태</div>
          <div class="v"><c:out value="${reception.status}" /></div>
        </div>

        <div class="actions">
          <a class="btn-primary" href="${pageContext.request.contextPath}/v1/waiting/${reception.receptionId}">대기현황 보기</a>
          <a class="btn-ghost" href="${pageContext.request.contextPath}/v1/reception/list">내 접수내역 보기</a>
        </div>
      </div>
    </c:when>

    <c:otherwise>
      <div class="empty">
        접수 정보를 불러올 수 없습니다.
        <div style="margin-top:12px">
          <a class="btn-ghost" href="${pageContext.request.contextPath}/v1/reception/list">내 접수내역 보기</a>
        </div>
      </div>
    </c:otherwise>
  </c:choose>

  <!-- 하단 네비 -->
  <div class="nav">
    <a href="${pageContext.request.contextPath}/">홈</a>
    <a class="active" href="${pageContext.request.contextPath}/v1/departments">접수</a>
    <a href="${pageContext.request.contextPath}/v1/reception/list">접수내역</a>
  </div>

</div>
</body>
</html>
