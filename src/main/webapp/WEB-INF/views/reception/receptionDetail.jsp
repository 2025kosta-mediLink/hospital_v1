<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt"  uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>상세 접수 내역</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/common/common.css">
    <link rel="stylesheet" href="${ctx}/static/css/reception/receptionDetail.css">
    <script defer src="${ctx}/static/js/reception/receptionDetail.js"></script>
</head>
<body class="screen" data-ctx="${pageContext.request.contextPath}">
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<%--<div class="wrap">--%>
<main class="main">
<%--    <div class="topbar">--%>
<%--        <a class="back" href="javascript:history.back()">←</a>--%>
<%--        <div class="title">상세 접수 내역</div>--%>
<%--        <a class="home" href="${ctx}/">⌂</a>--%>
<%--    </div>--%>

    <c:if test="${not empty error}">
        <div class="card" style="border:1px solid #fecaca; background:#fef2f2; color:#991b1b; margin-bottom:12px;">
            <strong>✖ 오류</strong>
            <div><c:out value="${error}"/></div>
        </div>
    </c:if>

    <c:if test="${param.cancel == '1'}">
        <div class="card" style="border:1px solid #a7f3d0; background:#ecfdf5; color:#065f46; margin-bottom:12px;">
            <strong>✔ 접수가 취소되었습니다.</strong>
            <c:if test="${not empty param.msg}">
                <div><c:out value="${param.msg}"/></div>
            </c:if>
        </div>
    </c:if>
    <c:if test="${param.cancel == '0'}">
        <div class="card" style="border:1px solid #fecaca; background:#fef2f2; color:#991b1b; margin-bottom:12px;">
            <strong>✖ 취소에 실패했습니다.</strong>
            <c:if test="${not empty param.msg}">
                <div><c:out value="${param.msg}"/></div>
            </c:if>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${empty reception}">
            <div class="empty">해당 접수 내역을 찾을 수 없습니다.</div>
        </c:when>

        <c:otherwise>
            <div class="card">
                <div class="card-head">
                    <div class="rec-no">접수번호 <c:out value="${reception.receptionNo}"/>번</div>
                    <c:choose>
                        <c:when test="${reception.status=='WAITING'}"><span class="badge b-wait">대기</span></c:when>
                        <c:when test="${reception.status=='CALLED'}"><span class="badge b-in">호출됨</span></c:when>
                        <c:when test="${reception.status=='IN_SERVICE'}"><span class="badge b-in">진료중</span></c:when>
                        <c:when test="${reception.status=='DONE'}"><span class="badge b-done">완료</span></c:when>
                        <c:when test="${reception.status=='CANCELLED' || reception.status=='CANCELED'}"><span class="badge b-cancel">취소</span></c:when>
                        <c:otherwise><span class="badge">기타</span></c:otherwise>
                    </c:choose>
                </div>

                <div class="grid">
                    <div class="label">접수일시</div>
                    <div class="val"><c:out value="${reception.createdAt}"/></div>

                    <div class="label">진료과</div>
                    <div class="val"><c:out value="${reception.departmentName}"/></div>

                    <div class="label">의료진</div>
                    <div class="val"><c:out value="${reception.doctorName}"/></div>
                </div>
            </div>

            <div class="card">
                <div class="section-title"><span class="dot"></span>증상 및 전달사항</div>
                <div class="info-box" style="margin-bottom:10px;">
                    <div class="label" style="color:var(--muted); margin-bottom:4px;">주요 증상</div>
                    <div class="val"><c:out value="${empty reception.symptomNames ? '—' : reception.symptomNames}"/></div>
                </div>
                <div class="note-box">
                    <div class="label" style="color:var(--muted); margin-bottom:4px;">전달 사항</div>
                    <div class="val" style="white-space:pre-line;"><c:out value="${empty reception.noteToDoctor ? '—' : reception.noteToDoctor}"/></div>
                </div>
            </div>

            <div class="card">
                <div class="warn-box">
                    <h4>⚠️ 주의사항</h4>
                    <ul style="margin:0; padding-left:18px;">
                        <li>호출 후 10분이 지나면 접수가 자동 취소될 수 있어요.</li>
                        <li>신분증을 꼭 지참해 주세요.</li>
                        <li>푸시 알림 및 대기현황을 수시로 확인해 주세요.</li>
                    </ul>
                </div>
            </div>

            <div class="card">
                <div class="actions">
                    <c:choose>
                        <c:when test="${reception.status == 'WAITING'}">
                            <form method="post"
                                  action="${ctx}/v1/reception/${reception.receptionId}/cancel"
                                  style="flex:1;" id="cancelForm">
                                <input type="hidden" name="receptionId" value="${reception.receptionId}"/>
<%--                                <input type="hidden" name="reason" id="cancelReasonHidden"/>--%>
                                <!-- 버튼을 submit가 아닌 button으로 → JS가 모달을 열고, 확인 시에만 submit() -->
                                <button type="button" id="openCancelModalBtn" class="btn btn-primary" style="width:100%;">접수 취소</button>
                            </form>
                            <a class="btn btn-ghost" href="${ctx}/v1/reception/list">목록으로</a>
                        </c:when>

                        <c:when test="${reception.status == 'DONE'}">
                            <a class="btn btn-primary" href="${ctx}/v1/prescriptions/${reception.receptionId}">
                                처방전 보기
                            </a>
                            <a class="btn btn-ghost" href="${ctx}/v1/reception/list">목록으로</a>
                        </c:when>

                        <c:otherwise>
                            <a class="btn btn-ghost" href="${ctx}/v1/reception/list">목록으로</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </c:otherwise>
    </c:choose>

    <!-- 확인 모달 -->
    <div id="cancelModal"
         class="modal"
         hidden
         aria-hidden="true"
         role="dialog"
         aria-modal="true"
         aria-labelledby="cancelModalTitle">
        <div class="modal-backdrop" data-close="true"></div>

        <div class="modal-panel" role="document">
            <h3 id="cancelModalTitle" class="modal-title">접수를 취소하시겠어요?</h3>
            <p class="modal-desc">취소 후에는 대기열에서 제거됩니다. 다시 접수하려면 처음부터 진행해야 할 수 있어요.</p>

<%--            <label for="cancelReason" class="modal-label">취소 사유 (선택)</label>--%>
<%--            <textarea id="cancelReason" class="modal-textarea" placeholder="예) 일정 변경, 대기시간 과다 등"></textarea>--%>

            <div class="modal-actions">
                <button type="button" class="btn btn-ghost" data-close="true">돌아가기</button>
                <button type="button" class="btn btn-primary" id="cancelModalConfirm">네, 취소합니다</button>
            </div>

            <button type="button" class="modal-x" title="닫기" aria-label="닫기" data-close="true">×</button>
        </div>
    </div>

<%--</div>--%>
</main>
</body>
</html>
