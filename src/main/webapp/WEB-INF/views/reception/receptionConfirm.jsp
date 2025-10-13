<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>접수하기 - 최종 확인</title>
    <style>
        :root{
            --bg:#f5f7fb; --card:#fff; --text:#111827; --muted:#6b7280;
            --line:#e5e7eb; --primary:#2563eb; --primary-weak:#eff6ff;
            --danger:#ef4444; --shadow:0 6px 18px rgba(0,0,0,.06); --radius:16px;
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

        /* 상단바 */
        .topbar{display:flex;align-items:center;justify-content:space-between;margin-bottom:12px}
        .title{font-weight:800;font-size:18px}
        .back, .close{
            display:inline-flex;align-items:center;gap:6px;border:1px solid var(--line);background:#fff;
            padding:8px 10px;border-radius:10px;cursor:pointer;color:#111;text-decoration:none;font-weight:800;box-shadow:var(--shadow)
        }
        .back:hover,.close:hover{background:#f8fafc}
        .chev{font-weight:900}

        /* 카드 공통 */
        .card{background:var(--card);border:1px solid #eef1f6;border-radius:12px;box-shadow:var(--shadow);padding:14px;margin-bottom:14px}
        .card h3{margin:0 0 12px;font-size:14px;display:flex;align-items:center;gap:8px}
        .muted{color:var(--muted)}
        .kv{display:grid;grid-template-columns:80px 1fr;row-gap:8px;column-gap:8px}
        .k{color:var(--muted)}
        .v{font-weight:800}
        .pill{display:inline-block;background:#f1f5f9;border:1px solid #e5e7eb;border-radius:999px;padding:4px 10px;margin-right:6px;margin-bottom:6px;font-weight:800}
        .note{font-weight:800;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}

        /* 주의사항 */
        .alert{border-color:#ffe1e1;background:#fffafa}
        .alert h3{color:#b91c1c}
        .alert ul{margin:0 0 0 18px;padding:0}
        .alert li{margin:6px 0}

        /* 동의 */
        .consent{display:flex;align-items:center;gap:10px}
        .consent input{width:18px;height:18px}

        /* 버튼 */
        .actions{position:sticky;bottom:84px;background:transparent;margin-top:16px}
        .btn-primary{
            width:100%;background:var(--primary);color:#fff;border:0;border-radius:12px;padding:14px 16px;
            font-weight:800;cursor:pointer;box-shadow:var(--shadow);font-size:15px
        }
        .btn-primary:disabled{opacity:.5;cursor:not-allowed}
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
        // 뒤로가기 (referrer 없으면 폴백)
        function goBackOr(url){
            if (document.referrer) history.back();
            else location.href = url;
        }
        // 동의 체크 시 버튼 활성화
        document.addEventListener('DOMContentLoaded', function(){
            const agree   = document.getElementById('consentNotice');
            const submit  = document.getElementById('submitBtn');
            if(!agree || !submit) return;
            const sync = () => submit.disabled = !agree.checked;
            agree.addEventListener('change', sync);
            sync();
        });
    </script>
</head>
<body>
<div class="wrap">

    <!-- 상단 -->
    <div class="topbar">
        <button class="back" type="button"
                onclick="goBackOr('${pageContext.request.contextPath}/v1/reception/symptom?departmentId=${departmentId}&doctorId=${doctorId}')">
            <span class="chev">←</span> 이전
        </button>
        <div class="title">접수하기</div>
        <a class="close" href="${pageContext.request.contextPath}/v1/reception/departments">닫기</a>
    </div>

    <!-- 폼 시작 -->
    <form method="post" action="${pageContext.request.contextPath}/v1/reception/done">
        <!-- 숨김값 전달 -->
        <input type="hidden" name="departmentId" value="${departmentId}" />
        <input type="hidden" name="doctorId" value="${doctorId}" />
        <c:forEach var="sid" items="${symptomIds}">
            <input type="hidden" name="symptomIds" value="${sid}" />
        </c:forEach>
        <input type="hidden" name="noteToDoctor" value="${noteToDoctor}" />
        <input type="hidden" name="consentNotice" value="${consentNotice}" />

        <!-- 접수 확인 정보 -->
        <div class="card">
            <h3>🗓️ 접수 확인 정보</h3>
            <div class="kv">
                <div class="k">진료과</div>
                <div class="v">
                    <c:choose>
                        <c:when test="${not empty departmentName}"><c:out value="${departmentName}"/></c:when>
                        <c:otherwise>#<c:out value="${departmentId}"/></c:otherwise>
                    </c:choose>
                </div>

                <div class="k">의료진</div>
                <div class="v">
                    <c:choose>
                        <c:when test="${not empty doctorName}"><c:out value="${doctorName}"/></c:when>
                        <c:otherwise>#<c:out value="${doctorId}"/></c:otherwise>
                    </c:choose>
                </div>

                <div class="k">증상</div>
                <div class="v">
                    <c:choose>
                        <c:when test="${empty symptomNames}">
                            <span class="muted">선택 없음</span>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="nm" items="${symptomNames}">
                                <span class="pill"><c:out value="${nm}"/></span>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="k">전달사항</div>
                <div class="v note" title="${fn:escapeXml(noteToDoctor)}">
                    <c:out value="${noteToDoctor}" />
                </div>
            </div>
        </div>

        <!-- 주의사항 -->
        <div class="card alert">
            <h3>⚠️ 주의사항 확인</h3>
            <ul>
                <li>호명 후 <strong>10분</strong> 지나면 접수 취소될 수 있습니다.</li>
                <li>반드시 <strong>알림</strong>을 확인 바랍니다.</li>
                <li><strong>신분증</strong>을 반드시 지참해 주세요.</li>
                <li>대기 시간은 상황에 따라 변동될 수 있습니다.</li>
            </ul>
        </div>

        <!-- 동의 체크 (서버 검증은 이미 진행됨) -->
        <div class="card">
            <label class="consent">
                <input id="consentNotice" type="checkbox" checked />
                주의사항을 숙지하고 동의합니다.
            </label>
<%--            <p class="muted" style="margin:8px 0 0">※ 서버에서도 동의 여부를 다시 검증합니다.</p>--%>
        </div>

        <!-- 제출 버튼 -->
        <div class="actions">
            <button id="submitBtn" class="btn-primary" type="submit">바로 접수하기</button>
        </div>
    </form>

    <!-- 하단 네비 -->
    <div class="nav">
        <a href="${pageContext.request.contextPath}/">홈</a>
        <a class="active" href="${pageContext.request.contextPath}/v1/reception/departments">접수</a>
        <a href="${pageContext.request.contextPath}/v1/reception/list">접수내역</a>
    </div>

</div>
</body>
</html>
