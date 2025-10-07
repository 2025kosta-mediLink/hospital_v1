<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <title>의료진 선택</title>
    <style>
        :root{
            --bg:#f5f7fb; --card:#fff; --text:#111827; --muted:#6b7280;
            --line:#e5e7eb; --primary:#2563eb; --primary-weak:#eff6ff;
            --shadow:0 6px 18px rgba(0,0,0,.06); --radius:16px;
        }
        *{box-sizing:border-box}
        body{
            margin:0;background:var(--bg);color:var(--text);
            font:14px/1.45 system-ui,-apple-system,"Segoe UI",Roboto,AppleSDGothicNeo,"Noto Sans KR","Malgun Gothic",sans-serif;
        }
        .wrap{max-width:640px;margin:20px auto;padding:0 16px 84px;} /* 하단 네비 피해서 여백 포함 */

        /* 이전 버튼 (좌상단) */
        .back {
            display:inline-flex; align-items:center; gap:6px;
            border:1px solid var(--line); background:#fff;
            padding:8px 10px; border-radius:10px; cursor:pointer;
            color:#111; text-decoration:none; font-weight:800;
            box-shadow:var(--shadow);
        }
        .back:hover { background:#f8fafc; }
        .back .chev { font-weight:900; font-size:14px; }

        /* 헤더 */
        .topbar{display:flex;align-items:center;gap:10px;margin-bottom:8px}
        .title{font-weight:800;font-size:20px}
        .sub{color:var(--muted);font-size:12px;margin-bottom:10px}

        /* 선택 요약 바 */
        .info{background:var(--card);border:1px solid var(--line);border-radius:12px;
            padding:10px 12px;margin:8px 0 14px;box-shadow:var(--shadow);color:#0f172a;font-weight:700}

        /* 칩 리스트 */
        .chips{display:flex;gap:8px;overflow:auto;padding:0 2px 6px}
        .chip{position:relative;display:inline-flex;align-items:center;gap:8px;
            background:#f1f5f9;border:1px solid #e5e7eb;border-radius:999px;padding:8px 12px;cursor:pointer;white-space:nowrap}
        .chip>input{position:absolute;inset:0;opacity:0;cursor:pointer}
        .chip.active{background:var(--primary);color:#fff;border-color:var(--primary)}

        /* 카드 리스트 (1열 기본 / 넓으면 2열) */
        .grid{display:grid;grid-template-columns:1fr;gap:10px;margin-top:10px}
        @media (min-width:560px){ .grid{grid-template-columns:repeat(2,1fr)} }

        .item{position:relative;display:block}
        .radio{position:absolute;inset:0;opacity:0;cursor:pointer}
        .card{background:var(--card);border:1px solid #eef1f6;border-radius:14px;box-shadow:var(--shadow);
            padding:16px;display:flex;gap:14px;align-items:center;transition:transform .08s ease,border-color .08s ease}
        .avatar{width:48px;height:48px;border-radius:12px;background:var(--primary-weak);display:grid;place-items:center;color:var(--primary);font-weight:800}
        .meta{flex:1;min-width:0}
        .name{font-weight:900}
        .dept{color:var(--muted);font-size:12px;margin-top:2px}

        /* 선택 효과 */
        .radio:checked + .card{border-color:var(--primary);box-shadow:0 0 0 3px rgba(37,99,235,.12);transform:translateY(-1px)}

        /* 하단 버튼 */
        .footer{margin-top:16px;display:flex;gap:10px}
        .btn-primary{flex:1;background:var(--primary);color:#fff;border:0;border-radius:12px;padding:12px 14px;
            font-weight:800;cursor:pointer}
        .btn-primary:disabled{opacity:.5;cursor:not-allowed}

        /* 공통 하단 네비 */
        .nav{
            position:sticky;bottom:0;left:0;right:0;background:var(--card);
            border-top:1px solid var(--line);box-shadow:0 -6px 18px rgba(0,0,0,.06);
            padding:calc(8px + env(safe-area-inset-bottom)) 6px 8px;
            display:flex;justify-content:space-around;z-index:50;
        }
        .nav a{color:#4b5563;text-decoration:none;font-weight:700;padding:8px 10px;border-radius:10px}
        .nav a.active{color:var(--primary);background:#eff6ff}

        /* 빈 상태 */
        .empty{background:var(--card);border:1px dashed var(--line);padding:24px;border-radius:12px;color:var(--muted);text-align:center}
    </style>

    <script>
        // 라디오 선택 시 칩 하이라이트 + 버튼 활성화, 초기 상태 동기화 포함
        document.addEventListener('DOMContentLoaded', function(){
            const form    = document.getElementById('doctorForm');
            const nextBtn = document.getElementById('nextBtn');
            const chipEls = document.querySelectorAll('.chip');
            if (!form || !nextBtn) return;

            // 변경 시
            form.addEventListener('change', function(e){
                if (e.target && e.target.name === 'doctorId') {
                    nextBtn.disabled = false;
                    syncChips(e.target.value);
                }
            });

            // 초기(뒤로 가기 등 브라우저가 라디오 선택을 기억한 경우) 동기화
            const checked = form.querySelector('input[name="doctorId"]:checked');
            if (checked) {
                nextBtn.disabled = false;
                syncChips(checked.value);
            }

            function syncChips(val){
                chipEls.forEach(ch => ch.classList.remove('active'));
                const act = document.querySelector('.chip[data-doc="'+ val +'"]');
                if (act) act.classList.add('active');
            }
        });

        // 뒤로가기 (referrer 없으면 폴백으로 이동)
        function goBackOr(url){
            if (document.referrer) { history.back(); }
            else { location.href = url; }
        }
    </script>
</head>
<body>
<div class="wrap">

    <!-- 헤더 -->
    <div class="topbar">
        <button class="back" type="button"
                onclick="goBackOr('${pageContext.request.contextPath}/v1/departments')">
            <span class="chev">←</span>
        </button>
        <div class="title">의료진 선택</div>
    </div>
    <div class="sub">원하시는 의료진을 선택해 주세요.</div>

    <!-- 진료과 안내 바 -->
    <c:if test="${not empty doctors}">
        <div class="info">
            진료과: <c:out value="${doctors[0].departmentName}" />
        </div>
    </c:if>

    <!-- 선택 폼 -->
    <form id="doctorForm" method="get" action="${pageContext.request.contextPath}/v1/reception/symptom">

        <!-- departmentId는 한 번만 전송 -->
        <c:if test="${not empty doctors}">
            <input type="hidden" name="departmentId" value="${doctors[0].departmentId}" />
        </c:if>

        <c:choose>
            <c:when test="${empty doctors}">
                <div class="empty">표시할 의료진이 없습니다.</div>
                <div class="footer">
                    <button id="nextBtn" class="btn-primary" type="submit" disabled>다음</button>
                </div>
            </c:when>

            <c:otherwise>
                <!-- 상단 이름 칩 -->
                <div class="chips">
                    <c:forEach var="doc" items="${doctors}">
                        <label class="chip" data-doc="${doc.doctorId}">
                            <input type="radio" name="doctorId" value="${doc.doctorId}" />
                            <span><c:out value="${doc.name}"/></span>
                        </label>
                    </c:forEach>
                </div>

                <!-- 카드 리스트 -->
                <div class="grid">
                    <c:forEach var="doc" items="${doctors}">
                        <label class="item">
                            <!-- 칩과 동일 name이라 어느 쪽을 클릭해도 동일 선택 -->
                            <input class="radio" type="radio" name="doctorId" value="${doc.doctorId}" required aria-label="${doc.name}" />
                            <div class="card">
                                <div class="avatar">
                                    <c:out value="${fn:substring(doc.name,0,1)}" />
                                </div>
                                <div class="meta">
                                    <div class="name"><c:out value="${doc.name}" /></div>
                                    <div class="dept"><c:out value="${doc.departmentName}" /></div>
                                </div>
                            </div>
                        </label>
                    </c:forEach>
                </div>

                <div class="footer">
                    <button id="nextBtn" class="btn-primary" type="submit" disabled>다음</button>
                </div>
            </c:otherwise>
        </c:choose>
    </form>

    <!-- 하단 네비 -->
    <div class="nav">
        <a href="${pageContext.request.contextPath}/">홈</a>
        <a class="active" href="${pageContext.request.contextPath}/v1/departments">접수</a>
        <a href="${pageContext.request.contextPath}/v1/reception/list">접수내역</a>
    </div>

</div>
</body>
</html>
