(function(){
    "use strict";

    const form      = document.getElementById("filterForm");
    const monthSel  = document.getElementById("monthSelect");
    const statusSel = document.getElementById("statusSelect");

    // --- 월 옵션 영구 저장용 키(페이지 경로 기준) ---
    const MONTH_KEY = "resv.monthOptions:" + location.pathname;

    // ====== 옵션 저장/복원 ======
    function saveMonthOptionsIfFirstView(){
        // '전체' 상태로 들어왔을 때만 초기 옵션을 저장
        const current = (monthSel?.dataset.current || "ALL");
        if(current !== "ALL") return; // 전체 첫 진입이 아닐 때는 스킵

        const menu = monthSel.querySelector(".pill-menu");
        if(!menu) return;

        const opts = [...menu.querySelectorAll("li[role='option']")].map(li => ({
            value: li.dataset.value,
            label: li.textContent.trim()
        }));

        // 최소한 '전체' + 하나 이상일 때만 저장
        if(opts.length >= 2){
            localStorage.setItem(MONTH_KEY, JSON.stringify(opts));
        }
    }

    function restoreMonthOptionsIfNeeded(){
        const menu = monthSel.querySelector(".pill-menu");
        if(!menu) return;

        const stored = localStorage.getItem(MONTH_KEY);
        if(!stored) return;

        const list = JSON.parse(stored);
        // 서버가 재렌더로 옵션을 줄여 보낸 경우(예: ALL + 선택월만) 복원
        const currentCount = menu.querySelectorAll("li[role='option']").length;
        const storedCount  = Array.isArray(list) ? list.length : 0;
        if(storedCount > currentCount){
            // 현재 선택값 보존
            const current = (monthSel.dataset.current || "ALL");

            // 메뉴 재구성
            menu.innerHTML = "";
            list.forEach(({value,label})=>{
                const li = document.createElement("li");
                li.setAttribute("role","option");
                li.dataset.value = value;
                li.textContent = label;
                if(value === current) li.setAttribute("aria-selected","true");
                menu.appendChild(li);
            });

            // 라벨도 다시 반영
            const lab = monthSel.querySelector(".pill-label");
            lab.dataset.value = current;
            lab.textContent = labelFromValue(current, textFromValue(list, current));
        }
    }

    function textFromValue(list, val){
        const f = list.find(o => o.value === val);
        return f ? f.label : val;
    }

    // ====== 필터 드롭다운 공통 ======
    setupPill(monthSel, (val)=>{
        form.elements["month"].value = (val === "ALL" ? "" : val);
        form.submit();
    });

    setupPill(statusSel, (val)=>{
        form.elements["status"].value = (val === "ALL" ? "" : val);
        form.submit();
    });

    function setupPill(selectEl, onPick){
        if(!selectEl) return;
        const btn  = selectEl.querySelector(".pill-trigger");
        const lab  = selectEl.querySelector(".pill-label");
        const menu = selectEl.querySelector(".pill-menu");
        const current = selectEl.dataset.current || "ALL";

        // 현재 선택 라벨 세팅
        const curLi = [...menu.querySelectorAll("li[role='option']")]
            .find(li => li.dataset.value === current);
        if (curLi) {
            curLi.setAttribute("aria-selected","true");
            lab.dataset.value = current;
            lab.textContent = labelFromValue(current, curLi.textContent.trim());
        }

        btn.addEventListener("click", ()=>{
            const open = btn.getAttribute("aria-expanded") === "true";
            btn.setAttribute("aria-expanded", String(!open));
            menu.hidden = open;
        });

        menu.addEventListener("click", (e)=>{
            const li = e.target.closest("li[role='option']");
            if(!li) return;
            const val = li.dataset.value;
            lab.dataset.value = val;
            lab.textContent = labelFromValue(val, li.textContent.trim());
            btn.setAttribute("aria-expanded","false");
            menu.hidden = true;
            onPick(val);
        });

        document.addEventListener("click", (e)=>{
            if(!selectEl.contains(e.target)){
                btn.setAttribute("aria-expanded","false");
                menu.hidden = true;
            }
        });
    }

    function labelFromValue(val, fallback){
        if (val === "ALL") return "전체";
        if (/^\d{4}-\d{2}$/.test(val)) { // YYYY-MM
            const y = val.slice(0,4), m = parseInt(val.slice(5,7),10);
            return `${y}년 ${m}월`;
        }
        return fallback || val;
    }

    // 초기: 전체 첫 진입이면 옵션 저장, 이후엔 옵션 복원
    saveMonthOptionsIfFirstView();
    restoreMonthOptionsIfNeeded();
})();

document.addEventListener("click", async (e) => {
    const btn = e.target.closest(".js-cancel");
    if (!btn) return;

    const id = btn.dataset.id;
    if (!id) return;

    if (!confirm("정말 이 예약을 취소할까요?")) return;

    const base = document.body.dataset.ctx || '';
    try {
        const res = await fetch(`${base}/v1/reservation/cancel`, {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
            body: `reservationId=${encodeURIComponent(id)}`
        });

        const ct = res.headers.get("content-type") || "";
        if (!res.ok) {
            const text = await res.text().catch(()=> '');
            alert(`취소 실패 (${res.status})\n${text.slice(0,120)}`);
            return;
        }

        const json = ct.includes("application/json")
            ? await res.json()
            : { ok:false, message:"JSON 응답 아님" };

        alert(json.message || (json.ok ? "취소되었습니다." : "취소 실패"));
        if (json.ok) location.reload();
    } catch (err) {
        console.error(err);
        alert("취소 처리 중 오류가 발생했습니다.");
    }
});
