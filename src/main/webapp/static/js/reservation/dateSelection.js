(function () {
  const ctx = window.__ctx;
  const doctorId = window.__doctorId;

  // 기본 상태
  let current = new Date(); // 오늘
  let pickedDate = null;    // YYYY-MM-DD
  let pickedTime = null;    // "HH:MM"

  // 엘리먼트
  const yearEl = document.getElementById('calYear');
  const monthEl = document.getElementById('calMonth');
  const gridEl = document.getElementById('calGrid');
  const amEl = document.getElementById('amSlots');
  const pmEl = document.getElementById('pmSlots');
  const btnOpen = document.getElementById('btnOpenConfirm');

  function ymd(d) {
    return d.toISOString().slice(0, 10);
  }

  // 달력 렌더링
  // 달력 렌더링
  function renderCalendar(baseDate) {
    const y = baseDate.getFullYear();
    const m = baseDate.getMonth(); // 0~11
    yearEl.textContent = y;
    monthEl.textContent = (m + 1).toString().padStart(2, '0');

    gridEl.innerHTML = '';
    // 요일 헤더
    const weekdays = ['일', '월', '화', '수', '목', '금', '토'];
    weekdays.forEach(w => {
      const h = document.createElement('div');
      h.className = 'dow';
      h.textContent = w;
      gridEl.appendChild(h);
    });

    const first = new Date(y, m, 1);
    const startIdx = first.getDay();
    const lastDay = new Date(y, m + 1, 0).getDate();

    for (let i = 0; i < startIdx; i++) {
      const empty = document.createElement('button');
      empty.className = 'day empty';
      empty.disabled = true;
      gridEl.appendChild(empty);
    }

    for (let d = 1; d <= lastDay; d++) {
      const btn = document.createElement('button');
      btn.className = 'day';
      btn.textContent = d;
      const dateStr = `${y}-${(m + 1 + '').padStart(2, '0')}-${(d
          + '').padStart(2, '0')}`;
      btn.dataset.date = dateStr;

      // 오늘 날짜에 'today' 클래스 추가
      const today = new Date();
      const todayStr = `${today.getFullYear()}-${(today.getMonth() + 1
          + '').padStart(2, '0')}-${(today.getDate() + '').padStart(2, '0')}`;
      if (dateStr === todayStr) {
        btn.classList.add('today'); // 오늘 날짜에 'today' 클래스 추가
        if (!pickedDate) { // 오늘 날짜가 선택되지 않으면 선택된 상태로 표시
          btn.classList.add('selected');
        }
      }

      btn.addEventListener('click', () => onPickDate(dateStr, btn));
      gridEl.appendChild(btn);
    }
  }

  async function onPickDate(dateStr, btn) {
    pickedDate = dateStr;
    pickedTime = null;
    // 선택 표시
    [...gridEl.querySelectorAll('.day')].forEach(
        b => b.classList.remove('selected')); // 모든 날짜에서 selected 제거
    btn.classList.add('selected'); // 클릭된 날짜에만 selected 추가

    // 슬롯 로드
    amEl.innerHTML = '';
    pmEl.innerHTML = '';
    btnOpen.disabled = true;

    try {
      const res = await fetch(
          `${ctx}/v1/reservation/slots?doctorId=${doctorId}&date=${dateStr}`);
      const data = await res.json();
      drawSlots(amEl, data.am || []);
      drawSlots(pmEl, data.pm || []);
    } catch (e) {
      console.error(e);
    }
  }

  function drawSlots(container, times) {
    if (times.length === 0) {
      container.innerHTML = '<div class="slot-empty">가능한 시간이 없습니다</div>';
      return;
    }
    times.forEach(t => {
      const b = document.createElement('button');
      b.type = 'button';
      b.className = 'slot';
      b.textContent = t;
      b.addEventListener('click', () => {
        document.querySelectorAll('.slot').forEach(
            x => x.classList.remove('selected'));
        b.classList.add('selected');
        pickedTime = t;
        btnOpen.disabled = false;
      });
      container.appendChild(b);
    });
  }

  // 모달
  const modal = document.getElementById('confirmModal');
  const modalWhen = document.getElementById('modalWhen');
  const appointmentAt = document.getElementById('appointmentAt');

  document.getElementById('btnCancel').onclick = () => {
    modal.hidden = true;
    document.body.classList.remove('modal-open');   // ← 추가
  };

  document.getElementById('btnOpenConfirm').onclick = () => {
    if (!pickedDate || !pickedTime) return;
    modalWhen.textContent = formatKoreanDateTime(pickedDate, pickedTime);
    appointmentAt.value = `${pickedDate} ${pickedTime}:00`;
    modal.hidden = false;
    document.body.classList.add('modal-open');      // ← 추가
  };

  function formatKoreanDateTime(ymd, hm) {
    const [y, m, d] = ymd.split('-').map(Number);
    const dt = new Date(y, m - 1, d);
    const dow = ['일', '월', '화', '수', '목', '금', '토'][dt.getDay()];
    return `${y}.${String(m).padStart(2, '0')}.${String(d).padStart(2,
        '0')}(${dow}) ${hm}`;
  }

  // 네비
  document.getElementById('prevMonth').onclick = () => {
    current.setMonth(current.getMonth() - 1);
    renderCalendar(current);
  };
  document.getElementById('nextMonth').onclick = () => {
    current.setMonth(current.getMonth() + 1);
    renderCalendar(current);
  };

  // 초기 렌더(오늘 자동선택 + 슬롯 로드)
  renderCalendar(current);
  // 오늘 날짜 버튼 찾아 클릭
  const todaySel = () => {
    const y = current.getFullYear(), m = current.getMonth() + 1,
        d = new Date().getDate();
    const key = `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2,
        '0')}`;
    const btn = [...document.querySelectorAll('.day')].find(
        b => b.dataset.date === key);
    if (btn) {
      btn.click();
    }
  };
  setTimeout(todaySel, 0);
})();
