let currentNoticeIndex = 0;
let currentDoctorIndex = 0; // 현재 선택된 의사 인덱스

// 공지사항 슬라이드
function showNextNotice() {
    const notices = document.querySelectorAll('.doctor-notice');
    if (!notices.length) return;
    currentNoticeIndex = (currentNoticeIndex + 1) % notices.length;
    updateNoticeDisplay();
}

function updateNoticeDisplay() {
    const notices = document.querySelectorAll('.doctor-notice');
    const noticeDots = document.querySelectorAll('.notice-dot');
    if (!notices.length) return;
    notices.forEach((notice, index) => {
        notice.classList.toggle('active', index === currentNoticeIndex);
    });
    noticeDots.forEach((dot, index) => {
        dot.classList.toggle('active', index === currentNoticeIndex);
    });
}

// 의사 이름 슬라이드 및 선택
function updateDoctorNames() {
    const nameNodes = getNameNodes();
    if (!nameNodes.length) return;
    nameNodes.forEach((node, index) => {
        node.classList.toggle('selected', index === currentDoctorIndex);
        if (index === currentDoctorIndex) {
            node.scrollIntoView({ behavior: 'smooth', inline: 'center', block: 'nearest' });
        }
    });
}

// 의사 카드 슬라이드
function updateDoctorCards() {
    const cards = getCardNodes();
    if (!cards.length) return;
    const n = cards.length;
    cards.forEach((card, idx) => {
        const rel = (idx - currentDoctorIndex + n) % n;
        card.classList.remove('card--current', 'card--next', 'card--hidden', 'selected');
        if (rel === 0) {
            card.classList.add('card--current', 'selected');
        } else if (rel === 1) {
            card.classList.add('card--next');
        } else {
            card.classList.add('card--hidden');
        }
    });
    updateSelectedDoctorId();
}

// 의사 선택 시 공지사항 갱신
function selectDoctor(index) {
    const nameNodes = getNameNodes();
    if (!nameNodes.length) return;
    currentDoctorIndex = ((index % nameNodes.length) + nameNodes.length) % nameNodes.length;
    updateDoctorNames();
    updateDoctorCards();

    // 선택된 의사의 공지사항을 갱신하는 부분
    const doctorId = nameNodes[currentDoctorIndex].getAttribute('data-id');
    updateDoctorNotices(doctorId);
}

// 선택된 의사에 해당하는 공지사항을 업데이트하는 함수
function updateDoctorNotices(doctorId) {
    const doctorNotices = document.querySelectorAll('.doctor-notice');
    doctorNotices.forEach((noticeElement) => {
        const noticeDoctorId = noticeElement.getAttribute('data-doctor-id');
        if (noticeDoctorId === doctorId) {
            noticeElement.classList.remove('hidden');  // 해당 의사의 공지 표시
        } else {
            noticeElement.classList.add('hidden');     // 다른 의사의 공지는 숨기기
        }
    });

    // 첫 번째 공지를 자동으로 활성화
    currentNoticeIndex = 0; // 첫 번째 공지로 초기화
    updateNoticeDisplay();   // 공지사항 업데이트
}

// 의사 카드 및 이름 슬라이드 이동
function slideDoctorCard(next = true) {
    const cards = getCardNodes();
    if (!cards.length) return;
    currentDoctorIndex = next ? (currentDoctorIndex + 1) % cards.length : (currentDoctorIndex - 1 + cards.length) % cards.length;
    updateDoctorNames();
    updateDoctorCards();
}

// 의사 카드 클릭 시 해당 의사 정보로 갱신
function getSelectedDoctorId() {
    const cards = getCardNodes();
    if (!cards.length) return null;
    const card = cards[currentDoctorIndex];
    return card?.getAttribute('data-id') || null;
}

function updateSelectedDoctorId() {
    const hid = document.getElementById('selectedDoctorId');
    if (!hid) return;
    const id = getSelectedDoctorId();
    if (id) hid.value = id;
}

// 이벤트 리스너 (네가 준 블록 + 화살표 바인딩 합침)
document.addEventListener('DOMContentLoaded', () => {
    updateNoticeDisplay();
    updateDoctorNames();
    updateDoctorCards();
    updateSelectedDoctorId();

    // 공지사항 자동 슬라이드 (5초 간격)
    setInterval(showNextNotice, 5000);

    // 칩/이름 클릭 -> 인덱스로 선택
    getNameNodes().forEach((node, index) => {
        node.addEventListener('click', () => {
            const dataIdx = node.getAttribute('data-index');
            selectDoctor(dataIdx !== null ? parseInt(dataIdx, 10) : index);
        });
    });

    // 캐러셀 화살표 바인딩
    const prevBtn = document.querySelector('.cards-prev');
    const nextBtn = document.querySelector('.cards-next');
    if (prevBtn) prevBtn.addEventListener('click', () => slideDoctorCard(false));
    if (nextBtn) nextBtn.addEventListener('click', () => slideDoctorCard(true));

    //(옵션) 키보드 좌우 화살표로 넘기기
    window.addEventListener('keydown', (e) => {
        if (e.key === 'ArrowLeft') slideDoctorCard(false);
        if (e.key === 'ArrowRight') slideDoctorCard(true);
    });
});
