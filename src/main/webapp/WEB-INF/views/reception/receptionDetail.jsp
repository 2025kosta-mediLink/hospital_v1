<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt"  uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>상세 접수 내역</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/reception/receptionDetail.css">
    <script defer src="${pageContext.request.contextPath}/static/js/reception/receptionDetail.js"></script>
</head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div class="wrap">

    <!-- 헤더 -->
    <div class="topbar">
        <a class="back" href="javascript:history.back()">←</a>
        <div class="title">상세 접수 내역</div>
        <a class="home" href="${pageContext.request.contextPath}/">⌂</a>
    </div>

    <!-- 본문 -->
    <c:choose>
        <c:when test="${empty reception}">
            <div class="empty">해당 접수 내역을 찾을 수 없습니다.</div>
        </c:when>

        <c:otherwise>
            <!-- 접수 요약 -->
            <div class="card">
                <div class="card-head">
                    <div class="rec-no">접수번호 <c:out value="${reception.receptionNo}"/>번</div>
                    <c:choose>
                        <c:when test="${reception.status=='WAITING'}"><span class="badge b-wait">대기</span></c:when>
                        <c:when test="${reception.status=='IN_SERVICE'}"><span class="badge b-in">진료중</span></c:when>
                        <c:when test="${reception.status=='DONE'}"><span class="badge b-done">완료</span></c:when>
                        <c:when test="${reception.status=='CANCELLED' || reception.status=='CANCELED'}"><span class="badge b-cancel">취소</span></c:when>
                        <c:otherwise><span class="badge">기타</span></c:otherwise>
                    </c:choose>
                </div>

                <div class="grid">
                    <div class="label">진료과</div>
                    <div class="val"><c:out value="${reception.departmentName}"/></div>

                    <div class="label">의료진</div>
                    <div class="val"><c:out value="${reception.doctorName}"/></div>

                    <div class="label">접수유형</div>
                    <div class="val"><c:out value="${reception.type}"/></div>

                    <div class="label">접수일시</div>
                    <div class="val"><c:out value="${reception.createdAt}"/></div>
                </div>
            </div>

            <!-- 증상/전달사항 -->
            <div class="card">
                <div class="section-title"><span class="dot"></span>증상 및 전달사항</div>
                <div class="info-box" style="margin-bottom:10px;">
                    <div class="label" style="color:var(--muted); margin-bottom:4px;">주요 증상</div>
                    <div class="val"><c:out value="${empty reception.symptomNames ? '—' : reception.symptomNames}"/></div>
                </div>
                <div class="note-box">
                    <div class="label" style="color:var(--muted); margin-bottom:4px;">의사에게 전달</div>
                    <div class="val" style="white-space:pre-line;"><c:out value="${empty reception.noteToDoctor ? '—' : reception.noteToDoctor}"/></div>
                </div>
            </div>

            <!-- 동의 정보 및 안내 -->
            <div class="card">
                <div class="section-title"><span class="dot"></span>안내 및 동의</div>
                <div class="grid" style="margin-bottom:10px;">
                    <div class="label">안내 동의</div>
                    <div class="val">
                        <c:choose>
                            <c:when test="${reception.consentNotice}">동의함</c:when>
                            <c:otherwise>미동의</c:otherwise>
                        </c:choose>
                    </div>

                    <div class="label">동의시각</div>
                    <div class="val"><c:out value="${empty reception.consentAt ? '—' : reception.consentAt}"/></div>
                </div>

                <div class="warn-box">
                    <ul style="margin:0; padding-left:18px;">
                        <li>호출 후 10분이 지나면 접수가 자동 취소될 수 있어요.</li>
                        <li>신분증을 꼭 지참해 주세요.</li>
                        <li>푸시 알림 및 대기현황을 수시로 확인해 주세요.</li>
                    </ul>
                </div>
            </div>

            <!-- 액션 -->
            <div class="card">
                <div class="actions">
                    <c:choose>
                        <c:when test="${reception.status=='DONE'}">
                            <a class="btn btn-primary" href="${pageContext.request.contextPath}/v1/prescriptions/${reception.receptionId}">
                                처방전 보기
                            </a>
                            <a class="btn btn-ghost" href="${pageContext.request.contextPath}/v1/reception/list">목록으로</a>
                        </c:when>

                        <c:when test="${reception.status=='WAITING'}">
                            <form method="post" action="${pageContext.request.contextPath}/v1/reception/${reception.receptionId}/cancel" style="flex:1;">
                                <button type="submit" class="btn btn-danger" style="width:100%;">접수 취소</button>
                            </form>
                            <a class="btn btn-ghost" href="${pageContext.request.contextPath}/v1/reception/list">목록으로</a>
                        </c:when>

                        <c:otherwise>
                            <a class="btn btn-ghost" href="${pageContext.request.contextPath}/v1/reception/list">목록으로</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </c:otherwise>
    </c:choose>

    <!-- 하단 탭바 -->
    <nav class="nav" aria-label="하단 내비게이션">
        <a href="${ctx}/v1/reservation/departments">예약</a>
        <a class="active" href="${ctx}/v1/reception/departments">접수</a>
        <a href="${ctx}/v1/home">홈</a>
        <a href="${ctx}/v1/prescription">처방전</a>
        <a href="${ctx}/v1/reception/list">마이페이지</a>
    </nav>
</div>
</body>
</html>
