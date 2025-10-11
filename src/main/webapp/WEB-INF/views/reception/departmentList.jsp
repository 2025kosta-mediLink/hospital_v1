<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>진료과 선택</title>
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
        .topbar{display:flex;align-items:center;gap:10px;margin-bottom:12px}
        .title{font-weight:800;font-size:20px}
        .desc{color:var(--muted);font-size:12px}

        /* 검색 바 (형태만) */
        .search{
            display:flex;gap:8px;align-items:center;background:var(--card);
            border:1px solid var(--line);border-radius:12px;padding:8px 10px;margin:8px 0 14px;box-shadow:var(--shadow)
        }
        .search input{flex:1;border:0;outline:0;font-size:14px;background:transparent}
        .search button{border:0;background:var(--primary);color:#fff;font-weight:800;border-radius:10px;padding:8px 12px;cursor:pointer}
        .search button:hover{opacity:.95}

        /* 그리드 카드: 항상 2열 */
        .grid{display:grid;grid-template-columns:repeat(2,1fr);gap:10px}

        .item{position:relative;display:block}
        .radio{position:absolute;inset:0;opacity:0;cursor:pointer}
        .card{
            background:var(--card);border:1px solid #eef1f6;border-radius:14px;
            padding:14px 12px;min-height:90px;display:flex;flex-direction:column;align-items:center;justify-content:center;
            gap:8px;box-shadow:var(--shadow);transition:transform .08s ease,border-color .08s ease
        }
        .icon{
            width:28px;height:28px;border-radius:999px;display:grid;place-items:center;
            color:var(--primary);background:var(--primary-weak);font-size:18px
        }
        .name{font-weight:800;text-align:center}
        /* 선택 상태 */
        .radio:checked + .card{border-color:var(--primary);box-shadow:0 0 0 3px rgba(37,99,235,.12);transform:translateY(-1px)}
        .radio:focus-visible + .card{outline:3px solid rgba(37,99,235,.25)}

        /* 하단 안내 & 버튼 */
        .footer{margin-top:16px;display:flex;gap:10px}
        .btn-primary{
            flex:1;background:var(--primary);color:#fff;border:0;border-radius:12px;padding:12px 14px;
            font-weight:800;cursor:pointer
        }
        .btn-primary:disabled{opacity:.5;cursor:not-allowed}
        .hint{color:var(--muted);font-size:12px;text-align:center;margin-top:8px}

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
        .nav a{color:#4b5563;text-decoration:none;font-weight:700;padding:8px 10px;border-radius:10px}
        .nav a.active{color:var(--primary);background:#eff6ff}

        /* 빈 상태 */
        .empty{background:var(--card);border:1px dashed var(--line);padding:24px;border-radius:12px;color:var(--muted);text-align:center}
    </style>
    <script>
        // 선택해야 '다음' 버튼 활성화 (널 가드 포함)
        document.addEventListener('DOMContentLoaded', function(){
            const form = document.getElementById('deptForm');
            const nextBtn = document.getElementById('nextBtn');
            if (!form || !nextBtn) return;
            form.addEventListener('change', function(e){
                if (e.target && e.target.name === 'departmentId') nextBtn.disabled = false;
            });
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

    <div class="topbar">
        <button class="back" type="button"
                onclick="goBackOr('${pageContext.request.contextPath}/')">
            <span class="chev">←</span>
        </button>
        <div class="title">진료과 선택</div>
    </div>
    <div class="desc">원하시는 진료과를 선택해 주세요.</div>

    <div class="search">
        <input type="text" placeholder="진료과명을 입력하세요" aria-label="진료과 검색(형태만 제공)"/>
        <button type="button">검색</button>
    </div>

    <form id="deptForm" method="get" action="${pageContext.request.contextPath}/v1/reception/doctors">

        <c:choose>
            <c:when test="${empty departments}">
                <div class="empty">표시할 진료과가 없습니다.</div>
                <div class="footer">
                    <button id="nextBtn" class="btn-primary" type="submit" disabled>다음</button>
                </div>
            </c:when>

            <c:otherwise>
                <div class="grid">
                    <c:forEach var="d" items="${departments}">
                        <label class="item">
                            <input
                                    class="radio"
                                    type="radio"
                                    name="departmentId"
                                    value="${d.departmentId}"
                                    aria-label="${d.name}"
                                    required
                            />
                            <div class="card">
                                <div class="icon"><c:out value="${fn:substring(d.name,0,1)}" /></div>
                                <div class="name"><c:out value="${d.name}" /></div>
                            </div>
                        </label>
                    </c:forEach>
                </div>

                <div class="footer">
                    <button id="nextBtn" class="btn-primary" type="submit" disabled>다음</button>
                </div>
                <div class="hint">선택 후 “다음”을 눌러 의료진 선택으로 이동합니다.</div>
            </c:otherwise>
        </c:choose>
    </form>

    <div class="nav">
        <a href="${pageContext.request.contextPath}/">홈</a>
        <a class="active" href="${pageContext.request.contextPath}/v1/reception/departments">접수</a>
        <a href="${pageContext.request.contextPath}/v1/reception/list">접수내역</a>
    </div>

</div>
</body>
</html>
