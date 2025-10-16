let currentNoticeIndex = 0;
let currentDoctorIndex = 0;

// 공지사항 슬라이드
function showNextNotice() {
  const notices = document.querySelectorAll('.doctor-notice');
  if (!notices.length) {
    return;
  }
  currentNoticeIndex = (currentNoticeIndex + 1) % notices.length;
  updateNoticeDisplay();
}

function updateNoticeDisplay() {
  const notices = document.querySelectorAll('.doctor-notice');
  const noticeDots = document.querySelectorAll('.notice-dot');
  if (!notices.length) {
    return;
  }

  notices.forEach((notice, index) => {
    notice.classList.toggle('active', index === currentNoticeIndex);
  });
  noticeDots.forEach((dot, index) => {
    dot.classList.toggle('active', index === currentNoticeIndex);
  });
}

// 이름/카드 공통 유틸
function getNameNodes() {
  // .doctor-chip만 선택 (공지용 .doctor-name 배제)
  return Array.from(document.querySelectorAll('#doctorNames .doctor-chip'));
}

function getCardNodes() {
  return Array.from(document.querySelectorAll('.doctor-card'));
}

// 의사 이름 슬라이드 및 선택
function updateDoctorNames() {
  const nameNodes = getNameNodes();
  if (!nameNodes.length) {
    return;
  }

  nameNodes.forEach((node, index) => {
    const doctorId = node.getAttribute('data-id');
    const isSelected = (index === currentDoctorIndex);

    // 선택된 의사에게만 selected 클래스 추가
    if (isSelected) {
      node.classList.add('selected');
    } else {
      node.classList.remove('selected');
    }

    // 선택된 의사 위치로 스크롤
    if (isSelected) {
      node.scrollIntoView(
          {behavior: 'smooth', inline: 'center', block: 'nearest'});
    }
  });
}

function selectDoctor(index) {
  const nameNodes = getNameNodes();
  if (!nameNodes.length) {
    return;
  }

  const max = nameNodes.length;
  currentDoctorIndex = ((index % max) + max) % max; // 안전하게 index 보정

  updateDoctorNames();
  updateDoctorCards();
}

function slideDoctorName(next = true) {
  const nameNodes = getNameNodes();
  if (!nameNodes.length) {
    return;
  }

  currentDoctorIndex = next
      ? (currentDoctorIndex + 1) % nameNodes.length
      : (currentDoctorIndex - 1 + nameNodes.length) % nameNodes.length;

  updateDoctorNames();
  updateDoctorCards();
}

// 의사 카드 슬라이드 (캐러셀: 앞 1장, 뒤 1장 예고)
function updateDoctorCards() {
  const cards = getCardNodes();
  if (!cards.length) {
    return;
  }
  const n = cards.length;

  cards.forEach((card, idx) => {
    const rel = (idx - currentDoctorIndex + n) % n;

    card.classList.remove('card--current', 'card--next', 'card--hidden',
        'selected');

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
  if (!cards.length) {
    return;
  }

  currentDoctorIndex = next
      ? (currentDoctorIndex + 1) % cards.length
      : (currentDoctorIndex - 1 + cards.length) % cards.length;

  updateDoctorNames();
  updateDoctorCards();
}

function getSelectedDoctorId() {
  const cards = getCardNodes();
  if (!cards.length) {
    return null;
  }
  const card = cards[currentDoctorIndex];
  return card?.getAttribute('data-id') || null;
}

function updateSelectedDoctorId() {
  const hid = document.getElementById('selectedDoctorId');
  if (!hid) {
    return;
  }
  const id = getSelectedDoctorId();
  if (id) {
    hid.value = id;
  }
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

  if (prevBtn) {
    prevBtn.addEventListener('click', () => slideDoctorCard(false));
  }
  if (nextBtn) {
    nextBtn.addEventListener('click', () => slideDoctorCard(true));
  }

  //(옵션) 키보드 좌우 화살표로 넘기기
  window.addEventListener('keydown', (e) => {
    if (e.key === 'ArrowLeft') {
      slideDoctorCard(false);
    }
    if (e.key === 'ArrowRight') {
      slideDoctorCard(true);
    }
  });
});
