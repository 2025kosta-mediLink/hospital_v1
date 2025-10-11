<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title>전체 접수 내역 조회</title>
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

    /* 헤더/탭 */
    .topbar{display:flex; align-items:center; gap:12px; margin-bottom:12px;}
    .title{font-weight:800; font-size:20px; letter-spacing:-.2px;}
    .tabs{display:flex; background:var(--card); border-radius:12px; padding:6px; gap:6px; box-shadow:var(--shadow); margin-bottom:16px;}
    .tab{flex:1; text-align:center; padding:10px 12px; border-radius:10px; color:var(--muted); text-decoration:none; font-weight:700;}
    .tab.active{background:var(--primary); color:#fff;}
    .tab:not(.active):hover{background:#f3f4f6; color:#111;}

    /* 필터: pill */
    .filter{display:flex; gap:10px; align-items:center; margin:10px 0 16px; flex-wrap:wrap;}
    .pill{
      position:relative; display:inline-flex; align-items:center; background:#f1f5f9;
      border:1px solid #e5e7eb; border-radius:999px; padding:6px 12px; cursor:pointer;
    }
    .pill-input,.pill-select{position:absolute; inset:0; opacity:0; cursor:pointer;}
    .pill-text{font-weight:800; color:#374151; pointer-events:none;}
    .pill-submit{padding:8px 14px; border:0; border-radius:999px; background:var(--primary); color:#fff; font-weight:800; cursor:pointer;}
    .pill-submit:hover{opacity:.95;}

    /* 카드 */
    .card{background:var(--card); border-radius:16px; padding:14px 16px; box-shadow:var(--shadow); margin-bottom:14px; border:1px solid #eef1f6;}
    .card-head{display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;}
    .rec-no{font-weight:900; font-size:16px;}
    .badge{font-size:12px; font-weight:900; padding:4px 10px; border-radius:999px; display:inline-block; letter-spacing:.3px;}
    .b-wait{background:#fff7ed; color:#c2410c; border:1px solid #fed7aa;}
    .b-in{background:#eff6ff; color:#1d4ed8; border:1px solid #bfdbfe;}
    .b-done{background:#ecfdf5; color:#047857; border:1px solid #a7f3d0;}
    .b-cancel{background:#fef2f2; color:#b91c1c; border:1px solid #fecaca;}

    .grid{display:grid; grid-template-columns:auto 1fr; row-gap:6px; column-gap:10px;}
    .label{color:var(--muted); width:70px;}
    .val{font-weight:800;}

    .card-actions{display:flex; gap:8px; margin-top:12px;}
    .btn{flex:1; text-align:center; padding:10px 12px; border-radius:12px; text-decoration:none; font-weight:900; display:inline-block;}
    .btn-primary{background:var(--primary); color:#fff;}
    .btn-ghost{background:#f8fafc; color:#111; border:1px solid var(--line);}
    .btn-primary:hover{opacity:.95;}
    .btn-ghost:hover{background:#eef2f7;}

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

  <div class="topbar">
    <div class="title">전체 접수 내역</div>
  </div>

  <div class="tabs">
    <a class="tab" href="${pageContext.request.contextPath}/v1/reservations/list">예약내역</a>
    <a class="tab active" href="${pageContext.request.contextPath}/v1/reception/list">접수내역</a>
  </div>

  <!-- 필터 (월 + 상태) -->
  <form class="filter" method="get" action="${pageContext.request.contextPath}/v1/reception/list">
    <!-- 월 선택 (YYYY-MM) -->
    <label class="pill" title="조회 월 선택">
      <input type="month" name="month" value="${param.month}" class="pill-input" />
      <span class="pill-text">
        <c:choose>
          <c:when test="${not empty param.month}">${param.month}</c:when>
          <c:otherwise>전체 기간</c:otherwise>
        </c:choose>
      </span>
    </label>

    <!-- 상태 선택 -->
    <label class="pill" title="상태 선택">
      <select name="status" class="pill-select">
        <option value="ALL"        ${status=='ALL'?'selected':''}>전체</option>
        <option value="WAITING"    ${status=='WAITING'?'selected':''}>대기</option>
        <option value="IN_SERVICE" ${status=='IN_SERVICE'?'selected':''}>진료중</option>
        <option value="DONE"       ${status=='DONE'?'selected':''}>완료</option>
        <option value="CANCELED"   ${status=='CANCELED'?'selected':''}>취소</option>
      </select>
      <span class="pill-text">
        <c:choose>
          <c:when test="${status=='WAITING'}">대기</c:when>
          <c:when test="${status=='IN_SERVICE'}">진료중</c:when>
          <c:when test="${status=='DONE'}">완료</c:when>
          <c:when test="${status=='CANCELED'}">취소</c:when>
          <c:otherwise>전체</c:otherwise>
        </c:choose>
      </span>
    </label>

    <button type="submit" class="pill-submit">조회</button>
  </form>

  <!-- 목록 -->
  <c:choose>
    <c:when test="${empty receptions}">
      <div class="empty">조회된 접수 내역이 없습니다.</div>
    </c:when>
    <c:otherwise>
      <c:forEach var="r" items="${receptions}">
        <div class="card">
          <div class="card-head">
            <div class="rec-no">접수번호 <c:out value="${r.receptionNo}"/>번</div>
            <c:choose>
              <c:when test="${r.status=='WAITING'}"><span class="badge b-wait">대기</span></c:when>
              <c:when test="${r.status=='IN_SERVICE'}"><span class="badge b-in">진료중</span></c:when>
              <c:when test="${r.status=='DONE'}"><span class="badge b-done">완료</span></c:when>
              <c:when test="${r.status=='CANCELED'}"><span class="badge b-cancel">취소</span></c:when>
              <c:otherwise><span class="badge">기타</span></c:otherwise>
            </c:choose>
          </div>

          <div class="grid">
            <div class="label">진료과</div>
            <div class="val"><c:out value="${r.departmentName}" /></div>

            <div class="label">의료진</div>
            <div class="val"><c:out value="${r.doctorName}" /></div>

            <div class="label">접수일시</div>
            <div class="val"><fmt:formatDate value="${r.createdAt}" pattern="yyyy-MM-dd HH:mm"/></div>
          </div>

          <div class="card-actions">
            <c:choose>
              <%-- 대기/진료중/완료: 모두 '상세보기' 노출 --%>
              <c:when test="${r.status=='WAITING' || r.status=='IN_SERVICE' || r.status=='DONE'}">
                <!-- 상세보기 링크 수정 -->
                <a class="btn btn-primary"
                   href="${pageContext.request.contextPath}/v1/reception/detail?id=${r.receptionId}">
                  상세보기
                </a>
                <c:if test="${r.status=='DONE'}">
                  <a class="btn btn-ghost" href="${pageContext.request.contextPath}/v1/prescriptions/${r.receptionId}">
                    처방전 보기
                  </a>
                </c:if>
              </c:when>

              <%-- 취소 등 기타: 상세보기만 --%>
              <c:otherwise>
                <!-- 상세보기 링크 수정 -->
                <a class="btn btn-ghost"
                   href="${pageContext.request.contextPath}/v1/reception/detail?id=${r.receptionId}">
                  상세보기
                </a>
              </c:otherwise>
            </c:choose>
          </div>

        </div> <!-- /.card  ✅ 이 닫힘이 누락돼 있었습니다 -->
      </c:forEach>
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
