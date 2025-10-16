(function () {
    "use strict";

    // 카카오 SDK 초기화가 완료된 후 실행되는 함수
    window.onload = function () {
        if (typeof Kakao !== 'undefined' && Kakao.init) {
            // 카카오 SDK 초기화
            Kakao.init('${kakaoJsKey}');
            console.log("Kakao SDK initialized with key: ${kakaoJsKey}");
            console.log("Kakao SDK initialized successfully.");
        } else {
            console.error("Kakao SDK is not loaded correctly.");
        }
    };

    // 카카오톡 공유하기 버튼 처리 함수
    function shareReservation(reservationId, departmentName, doctorName, dateLabel, timeLabel) {
        const month = new URLSearchParams(window.location.search).get('month');
        const status = new URLSearchParams(window.location.search).get('status');

        const shareUrl = `${window.location.origin}/v1/reservation/list?month=${month}&status=${status}`;

        // 카카오톡 SDK가 제대로 로드되었을 때 실행
        if (typeof Kakao !== 'undefined' && Kakao.Share) {
            Kakao.Share.createDefaultButton({
                container: '#kakaotalk-sharing-btn-' + reservationId,  // 해당 버튼에 대한 ID를 container로 설정
                objectType: 'feed',
                content: {
                    title: `${dateLabel} 병원 예약 일정 안내`,
                    description: `${departmentName}, ${doctorName}, 예약 일시(${dateLabel} ${timeLabel})`,
                    imageUrl: 'http://k.kakaocdn.net/dn/Q2iNx/btqgeRgV54P/VLdBs9cvyn8BJXB3o7N8UK/kakaolink40_original.png',
                    link: {
                        mobileWebUrl: shareUrl,
                        webUrl: shareUrl
                    },
                },
                social: {
                    likeCount: 286,
                    commentCount: 45,
                    sharedCount: 845,
                },
                buttons: [
                    {
                        title: '웹으로 보기',
                        link: {
                            mobileWebUrl: shareUrl,
                            webUrl: shareUrl,
                        },
                    },
                    {
                        title: '앱으로 보기',
                        link: {
                            mobileWebUrl: shareUrl,
                            webUrl: shareUrl,
                        },
                    },
                ],
            });
        } else {
            console.error("Kakao SDK is not loaded correctly.");
        }
    }

    // DOM을 통해 버튼 클릭 이벤트 처리
    document.addEventListener('click', function (e) {
        const btn = e.target.closest('.js-share');
        if (!btn) return;

        const reservationId = btn.dataset.id;
        const departmentName = btn.dataset.departmentName;
        const doctorName = btn.dataset.doctorName;
        const dateLabel = btn.dataset.dateLabel;
        const timeLabel = btn.dataset.timeLabel;

        if (reservationId) {
            shareReservation(reservationId, departmentName, doctorName, dateLabel, timeLabel);
        }
    });
})();
