let currentNoticeIndex = 0;
let currentDoctorIndex = 0;

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

// 이름/카드 공통 유틸
function getNameNodes() {
    // .doctor-chip(추천) 또는 .doctor-name(기존) 모두 지원
    const nodes = document.querySelectorAll('.doctor-chip, .doctor-name');
    return Array.prototype.slice.call(nodes);
}
function getCardNodes() {
    return Array.prototype.slice.call(document.querySelectorAll('.doctor-card'));
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

function selectDoctor(index) {
    const nameNodes = getNameNodes();
    if (!nameNodes.length) return;

    // index 안전보정
    const max = nameNodes.length;
    currentDoctorIndex = ((index % max) + max) % max;

    updateDoctorNames();
    updateDoctorCards();
}

function slideDoctorName(next = true) {
    const nameNodes = getNameNodes();
    if (!nameNodes.length) return;

    currentDoctorIndex = next
        ? (currentDoctorIndex + 1) % nameNodes.length
        : (currentDoctorIndex - 1 + nameNodes.length) % nameNodes.length;

    updateDoctorNames();
    updateDoctorCards();
}

// 의사 카드 슬라이드 (캐러셀: 앞 1장, 뒤 1장 예고)
function updateDoctorCards() {
    const cards = getCardNodes();
    if (!cards.length) return;
    const n = cards.length;

    cards.forEach((card, idx) => {
        // 관계 인덱스(0: 현재, 1: 다음, 나머지: 숨김)
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

function slideDoctorCard(next = true) {
    const cards = getCardNodes();
    if (!cards.length) return;

    currentDoctorIndex = next
        ? (currentDoctorIndex + 1) % cards.length
        : (currentDoctorIndex - 1 + cards.length) % cards.length;

    updateDoctorNames();
    updateDoctorCards();
}

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
            // data-index가 있으면 우선 사용(서버에서 내려보낸 경우)
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
