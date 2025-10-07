<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>증상 입력</title>
    <style>
        :root{
            --bg:#f5f7fb; --card:#fff; --text:#111827; --muted:#6b7280;
            --line:#e5e7eb; --primary:#2563eb; --primary-weak:#eff6ff;
            --danger:#ef4444; --shadow:0 6px 18px rgba(0,0,0,.06); --radius:16px;
        }
        *{box-sizing:border-box}
        body{
            margin:0; background:var(--bg); color:var(--text);
            font:14px/1.45 system-ui,-apple-system,"Segoe UI",Roboto,AppleSDGothicNeo,"Noto Sans KR","Malgun Gothic",sans-serif;
        }
        /* ✅ 하단 네비까지 고려한 여백을 한 번에 */
        .wrap{max-width:640px; margin:20px auto; padding:0 16px 84px;}

        /* 이전 버튼 */
        .back{
            display:inline-flex; align-items:center; gap:6px;
            border:1px solid var(--line); background:#fff;
            padding:8px 10px; border-radius:10px; cursor:pointer;
            color:#111; text-decoration:none; font-weight:800;
            box-shadow:var(--shadow);
        }
        .back:hover{ background:#f8fafc; }
        .back .chev{ font-weight:900; font-size:14px; }

        /* 헤더 */
        .topbar{display:flex; align-items:center; gap:10px; margin-bottom:10px}
        .title{font-weight:800; font-size:20px}
        .sub{color:var(--muted); font-size:12px; margin-bottom:10px}

        /* 카드 */
        .card{background:var(--card); border:1px solid #eef1f6; border-radius:12px; box-shadow:var(--shadow);
            padding:14px; margin-bottom:12px}
        .card h3{margin:0 0 10px; font-size:14px; display:flex; align-items:center; gap:8px}
        .hint{color:var(--muted); font-size:12px}
        .row{display:flex; align-items:center; justify-content:space-between; margin-top:8px}

        /* 체크박스 리스트 */
        .sym-list{display:flex; flex-direction:column; gap:8px; max-height:260px; overflow:auto; padding-right:2px}
        .sym-item{display:flex; align-items:center; gap:8px; padding:8px 10px; border:1px solid var(--line); border-radius:10px; background:#fff}
        .sym-item:hover{background:#f8fafc}
        .sym-item input[type="checkbox"]{width:16px; height:16px}

        /* textarea */
        .ta{width:100%; min-height:110px; border:1px solid var(--line); border-radius:12px; padding:10px 12px; resize:vertical}
        .ta:focus{outline:3px solid rgba(37,99,235,.15); border-color:var(--primary)}

        /* 동의 체크 */
        .consent{display:flex; align-items:center; gap:8px; margin-top:6px}
        .consent input{width:16px; height:16px}

        /* 하단 버튼 */
        .footer{margin-top:14px}
        .btn-primary{width:100%; background:var(--primary); color:#fff; border:0; border-radius:12px; padding:12px 14px;
            font-weight:800; cursor:pointer}
        .btn-primary:disabled{opacity:.5; cursor:not-allowed}

        /* 보조 표시 */
        .counter{font-size:12px; color:var(--muted)}
        .counter.error{color:var(--danger); font-weight:700}
        .badge{background:var(--primary-weak); color:#1d4ed8; border-radius:999px; padding:4px 10px; font-weight:800; font-size:12px}

        /* 공통 하단 네비 */
        .nav{
            position:sticky; bottom:0; left:0; right:0; background:var(--card);
            border-top:1px solid var(--line); box-shadow:0 -6px 18px rgba(0,0,0,.06);
            padding:calc(8px + env(safe-area-inset-bottom)) 6px 8px;
            display:flex; justify-content:space-around; z-index:50;
        }
        .nav a{color:#4b5563; text-decoration:none; font-weight:700; padding:8px 10px; border-radius:10px}
        .nav a.active{color:var(--primary); background:#eff6ff}

        /* 빈 상태 */
        .empty{background:var(--card); border:1px dashed var(--line); padding:24px; border-radius:12px; color:var(--muted); text-align:center}
    </style>

    <script>
        document.addEventListener('DOMContentLoaded', function(){
            const form        = document.getElementById('symptomForm');
            const note        = document.getElementById('noteToDoctor');
            const counter     = document.getElementById('noteCounter');
            const consent     = document.getElementById('consentNotice');
            const submitBtn   = document.getElementById('nextBtn');
            const symCheckboxes = document.querySelectorAll('input[name="symptomIds"]');
            const symCountEl  = document.getElementById('symCount');
            const maxLen      = 500;

            if (!form || !note || !counter || !consent || !submitBtn || !symCountEl) return;

            function updateCount(){
                const len = note.value.length;
                counter.textContent = len + " / " + maxLen;
                counter.classList.toggle('error', len > maxLen);
                submitBtn.disabled = !(consent.checked && len <= maxLen);
            }
            function updateSymCount(){
                let c = 0;
                symCheckboxes.forEach(cb => { if (cb.checked) c++; });
                symCountEl.textContent = c;
            }

            note.addEventListener('input', updateCount);
            consent.addEventListener('change', updateCount);
            symCheckboxes.forEach(cb => cb.addEventListener('change', updateSymCount));

            // 초기 상태
            updateCount();
            updateSymCount();

            form.addEventListener('submit', function(e){
                if (note.value.length > maxLen) {
                    e.preventDefault();
                    alert("전달사항은 최대 " + maxLen + "자까지 입력 가능합니다.");
                }
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
                onclick="goBackOr('${pageContext.request.contextPath}/v1/doctors?departmentId=${departmentId}')"
                aria-label="이전으로">
            <span class="chev">←</span>
            이전
        </button>
        <div class="title">증상 입력</div>
    </div>

    <div class="sub">증상을 선택하고, 의료진에게 전달할 내용을 적어주세요.</div>

    <!-- 폼 -->
    <form id="symptomForm" method="post" action="${pageContext.request.contextPath}/v1/reception/confirm">
        <!-- 이전 단계 값 유지 -->
        <input type="hidden" name="departmentId" value="${departmentId}" />
        <input type="hidden" name="doctorId" value="${doctorId}" />

        <!-- 카드 1: 증상 선택 -->
        <div class="card">
            <h3>
                발생 증상 선택
                <span class="badge">선택 <span id="symCount">0</span>개</span>
            </h3>

            <c:choose>
                <c:when test="${empty symptoms}">
                    <div class="empty">표시할 증상이 없습니다.</div>
                </c:when>
                <c:otherwise>
                    <div class="sym-list">
                        <c:forEach var="s" items="${symptoms}">
                            <label class="sym-item">
                                <input type="checkbox" name="symptomIds" value="${s.symptomId}" aria-label="${s.name}" />
                                <c:out value="${s.name}" />
                            </label>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- 카드 2: 전달 메모 -->
        <div class="card">
            <h3>의료진에게 알림</h3>
            <textarea id="noteToDoctor" name="noteToDoctor" class="ta"
                      placeholder="예) 3일째 열이 나고, 기침과 발열이 심해졌습니다. 약은 이부프로펜을 복용 중입니다."></textarea>
            <div class="row">
                <div class="hint">최대 500자까지 입력 가능합니다.</div>
                <div id="noteCounter" class="counter">0 / 500</div>
            </div>
        </div>

        <!-- 카드 3: 동의 -->
        <div class="card">
            <label class="consent">
                <input id="consentNotice" type="checkbox" name="consentNotice" value="true" required />
                주의사항에 동의합니다.
            </label>
            <div class="hint" style="margin-top:6px">
                개인정보 수집·이용 및 안내사항에 동의해야 접수가 가능합니다.
            </div>
        </div>

        <!-- 제출 버튼 -->
        <div class="footer">
            <button id="nextBtn" class="btn-primary" type="submit" disabled>다음</button>
        </div>
    </form>

    <!-- 하단 네비 (✅ 인라인 스타일 제거) -->
    <div class="nav">
        <a href="${pageContext.request.contextPath}/">홈</a>
        <a class="active" href="${pageContext.request.contextPath}/v1/departments">접수</a>
        <a href="${pageContext.request.contextPath}/v1/reception/list">접수내역</a>
    </div>

</div>
</body>
</html>
