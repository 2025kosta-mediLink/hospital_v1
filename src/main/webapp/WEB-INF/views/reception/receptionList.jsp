<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>전체 접수 내역 조회</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/reception/receptionList.css">
</head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="wrap">

    <div class="topbar">
        <div class="title">전체 접수 내역</div>
    </div>

    <div class="tabs">
        <a class="tab" href="${ctx}/v1/reservations/list">예약내역</a>
        <a class="tab active" href="${ctx}/v1/reception/list">접수내역</a>
    </div>

    <!-- 목록 -->
    <c:choose>
        <c:when test="${empty receptions}">
            <div class="empty">조회된 접수 내역이 없습니다.</div>
        </c:when>
        <c:otherwise>
            <c:forEach var="r" items="${receptions}">
                <div class="card">
                    <div class="card-head">
                        <div class="rec-no">접수번호 <c:out value="${r.receptionNo}"/>번</div>

                            <%-- 상태 배지: 다양한 관용 표기를 모두 취급 --%>
                        <c:set var="st" value="${fn:toUpperCase(r.status)}"/>
                        <c:choose>
                            <c:when test="${st=='WAIT' || st=='WAITING'}">
                                <span class="badge b-wait">대기</span>
                            </c:when>
                            <c:when test="${st=='IN_SERVICE' || st=='INSERVICE' || st=='IN_PROGRESS'}">
                                <span class="badge b-in">진료중</span>
                            </c:when>
                            <c:when test="${st=='DONE' || st=='COMPLETE' || st=='COMPLETED'}">
                                <span class="badge b-done">완료</span>
                            </c:when>
                            <c:when test="${st=='CANCEL' || st=='CANCELED' || st=='CANCELLED'}">
                                <span class="badge b-cancel">취소</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge">기타</span>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="grid">
                        <div class="label">진료과</div>
                        <div class="val"><c:out value="${r.departmentName}" /></div>

                        <div class="label">의료진</div>
                        <div class="val"><c:out value="${r.doctorName}" /></div>

                        <div class="label">접수일시</div>
                        <div class="val"><fmt:formatDate value="${r.createdAt}" pattern="yyyy-MM-dd HH:mm"/></div>
                    </div>

                    <div class="card-actions">
                        <c:choose>
                            <%-- 대기/진료중/완료: '상세보기' (+ 완료 시 처방전 보기) --%>
                            <c:when test="${st=='WAIT' || st=='WAITING' || st=='IN_SERVICE' || st=='INSERVICE' || st=='IN_PROGRESS' || st=='DONE' || st=='COMPLETE' || st=='COMPLETED'}">
                                <a class="btn btn-primary" href="${ctx}/v1/reception/detail?id=${r.receptionId}">
                                    상세보기
                                </a>
                                <c:if test="${st=='DONE' || st=='COMPLETE' || st=='COMPLETED'}">
                                    <a class="btn btn-ghost" href="${ctx}/v1/prescriptions/${r.receptionId}">
                                        처방전 보기
                                    </a>
                                </c:if>
                            </c:when>
                            <%-- 취소 등 기타: 상세보기만 --%>
                            <c:otherwise>
                                <a class="btn btn-ghost" href="${ctx}/v1/reception/detail?id=${r.receptionId}">
                                    상세보기
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </div>

                </div>
            </c:forEach>
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
