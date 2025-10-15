document.addEventListener("DOMContentLoaded", function () {
    // 예약 정보 동적으로 채우기
    document.getElementById('reservationNumber').textContent = "${reservation.reservationNo}";
    document.getElementById('department').textContent = "${doctor.departmentName}";
    document.getElementById('doctor').textContent = "${doctor.name}";
    document.getElementById('appointmentDate').textContent = "${reservation.appointmentAt}";

    // 예약 내역 보기 버튼
    document.getElementById('viewAllReservations').addEventListener('click', function () {
        // 예약 내역 페이지로 이동하는 동작 추가
        window.location.href = "/v1/reservation/history";
    });

    // 추가 예약하기 버튼
    document.getElementById('addAnotherReservation').addEventListener('click', function () {
        // 추가 예약 페이지로 이동
        window.location.href = "/v1/reservation/date-selection";
    });
});
