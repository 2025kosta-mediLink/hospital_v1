// /static/js/reception/receptionDetail.js
document.addEventListener('DOMContentLoaded', function () {
    var form = document.getElementById('cancelForm');
    var openBtn = document.getElementById('openCancelModalBtn');
    var modal = document.getElementById('cancelModal');
    var reasonTextarea = document.getElementById('cancelReason');
    var reasonHidden = document.getElementById('cancelReasonHidden');
    var confirmBtn = document.getElementById('cancelModalConfirm');

    if (!form || !openBtn || !modal) return;

    // 모달 열기
    openBtn.addEventListener('click', function () {
        openModal();
    });

    // 모달 내 확인 -> reason 세팅 후 실제 제출
    if (confirmBtn) {
        confirmBtn.addEventListener('click', function () {
            if (reasonHidden && reasonTextarea) {
                reasonHidden.value = (reasonTextarea.value || '').trim();
            }
            closeModal();
            form.submit();
        });
    }

    // 배경/닫기 버튼으로 닫기
    modal.addEventListener('click', function (e) {
        var t = e.target;
        if (t && t.getAttribute('data-close') === 'true') {
            closeModal();
        }
    });

    // ESC로 닫기
    document.addEventListener('keydown', function (e) {
        if (!isOpen()) return;
        if (e.key === 'Escape' || e.keyCode === 27) {
            e.preventDefault();
            closeModal();
        }
    });

    function openModal() {
        modal.hidden = false;                         // HTML hidden 해제
        modal.setAttribute('aria-hidden', 'false');
        document.body.style.overflow = 'hidden';      // 배경 스크롤 잠금
        setTimeout(function(){ if (reasonTextarea) reasonTextarea.focus(); }, 0);
    }

    function closeModal() {
        modal.hidden = true;                          // HTML hidden 적용
        modal.setAttribute('aria-hidden', 'true');
        document.body.style.overflow = '';
    }

    function isOpen() {
        return modal && modal.hidden === false;
    }
});
