<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <!-- 禁止页面缓存 -->
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">
    <title>健身房客户管理系统-教练签到</title>
    <link rel="icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
</head>
<body>
<div class="lyear-layout-web">
    <div class="lyear-layout-container">
        <!-- 左侧导航 -->
        <jsp:include page="/WEB-INF/view/_aside_header.jsp" />
        <!-- 页面主要内容 -->
        <main class="lyear-layout-content">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-lg-12">
                        <div class="card">
                            <div class="card-header">
                                <h4>教练签到系统</h4>
                            </div>
                            <div class="card-body">
                                <div class="text-center">
                                    <!-- 状态信息 -->
                                    <div class="status-info">
                                        <div style="display: flex; justify-content: space-between; align-items: center;">
                                            <div class="status-text">
                                                今日签到状态：
                                                <c:choose>
                                                    <c:when test="${hasCheckedIn and hasCheckedOut}">
                                                        <span class="status-completed">✓ 上班已签到，下班已签退</span>
                                                    </c:when>
                                                    <c:when test="${hasCheckedIn and not hasCheckedOut}">
                                                        <span class="status-partial">● 上班已签到，下班未签退</span>
                                                    </c:when>
                                                    <c:when test="${not hasCheckedIn and not hasCheckedOut}">
                                                        <span class="status-none">● 今日尚未签到</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="status-error">● 状态异常</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div style="font-size: 12px; color: #6c757d;">
                                                <c:if test="not empty msg">
                                                    提示：<strong>${msg}</strong>
                                                </c:if>
                                            </div>
                                        </div>
                                    </div>

                                    <c:if test="not empty msg">
                                        <c:choose>
                                            <c:when test="${msgType == 'success'}">
                                                <div class="alert alert-success alert-dismissible" role="alert">
                                                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                                        <span aria-hidden="true">×</span>
                                                    </button>
                                                    <i class="mdi mdi-check-circle"></i> ${msg}
                                                </div>
                                            </c:when>
                                            <c:when test="${msgType == 'danger'}">
                                                <div class="alert alert-danger alert-dismissible" role="alert">
                                                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                                        <span aria-hidden="true">×</span>
                                                    </button>
                                                    <i class="mdi mdi-alert-circle"></i> ${msg}
                                                </div>
                                            </c:when>
                                            <c:when test="${msgType == 'warning'}">
                                                <div class="alert alert-warning alert-dismissible" role="alert">
                                                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                                        <span aria-hidden="true">×</span>
                                                    </button>
                                                    <i class="mdi mdi-alert"></i> ${msg}
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="alert alert-info alert-dismissible" role="alert">
                                                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                                        <span aria-hidden="true">×</span>
                                                    </button>
                                                    <i class="mdi mdi-information"></i> ${msg}
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>

                                    <!-- 当前时间显示 -->
                                    <div class="current-time mb-3">
                                        <h6>当前时间：<span id="currentTime"></span></h6>
                                    </div>

                                    <!-- 签到统计信息 -->
                                    <div class="checkin-stats mb-4">
                                        <div class="row justify-content-center">
                                            <div class="col-md-3">
                                                <div class="card card-stat">
                                                    <div class="card-body">
                                                        <h6 class="card-title">本月签到</h6>
                                                        <h4 class="card-value">${monthCheckinDays} 天</h4>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-md-3">
                                                <div class="card card-stat">
                                                    <div class="card-body">
                                                        <h6 class="card-title">本月迟到</h6>
                                                        <h4 class="card-value">${lateCount} 次</h4>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-md-3">
                                                <div class="card card-stat">
                                                    <div class="card-body">
                                                        <h6 class="card-title">本月早退</h6>
                                                        <h4 class="card-value">${earlyLeaveCount} 次</h4>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <form method="post" action="${pageContext.request.contextPath}/checkIn" class="checkin-form" onsubmit="return disableButtonOnSubmit()">
                                        <h5>欢迎回来，${sessionScope.user.name} 教练！</h5>
                                        <div class="checkin-buttons">
                                            <!-- 上班签到按钮 -->
                                            <c:choose>
                                                <c:when test="${hasCheckedIn}">
                                                    <button type="submit" name="checkType" value="1" class="btn btn-success btn-lg m-r-20" disabled>
                                                        <i class="mdi mdi-clock-in"></i> 已上班签到
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <button type="submit" name="checkType" value="1" class="btn btn-success btn-lg m-r-20" id="checkInBtn">
                                                        <i class="mdi mdi-clock-in"></i> 上班签到
                                                    </button>
                                                </c:otherwise>
                                            </c:choose>

                                            <!-- 下班签退按钮 -->
                                            <c:choose>
                                                <c:when test="${hasCheckedOut}">
                                                    <button type="submit" name="checkType" value="2" class="btn btn-danger btn-lg" disabled>
                                                        <i class="mdi mdi-clock-out"></i> 已下班签退
                                                    </button>
                                                </c:when>
                                                <c:when test="${!hasCheckedIn}">
                                                    <button type="submit" name="checkType" value="2" class="btn btn-danger btn-lg" disabled>
                                                        <i class="mdi mdi-clock-out"></i> 请先上班签到
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <button type="submit" name="checkType" value="2" class="btn btn-danger btn-lg" id="checkOutBtn">
                                                        <i class="mdi mdi-clock-out"></i> 下班签退
                                                    </button>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </form>
                                </div>
                                <hr>
                                <div class="mt-4">
                                    <h5>今日签到记录</h5>
                                    <div class="table-responsive">
                                        <table class="table table-hover">
                                            <thead>
                                            <tr>
                                                <th>签到类型</th>
                                                <th>签到时间</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:choose>
                                                <c:when test="not empty todayRecords">
                                                    <c:forEach items="${todayRecords}" var="record">
                                                        <tr>
                                                            <td>
                                                                <c:if test="${record.check_type == '上班签到'}">
                                                                    <span class="label label-success">${record.check_type}</span>
                                                                </c:if>
                                                                <c:if test="${record.check_type == '下班签退'}">
                                                                    <span class="label label-danger">${record.check_type}</span>
                                                                </c:if>
                                                            </td>
                                                            <td>
                                                                <fmt:formatDate value="${record.check_time}" pattern="yyyy-MM-dd HH:mm:ss" />
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </c:when>
                                                <c:otherwise>
                                                    <tr>
                                                        <td colspan="2" class="text-center">今日暂无签到记录</td>
                                                    </tr>
                                                </c:otherwise>
                                            </c:choose>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>

<!-- 失败提示弹窗（模态框） -->
<div class="modal fade" id="failModal" tabindex="-1" role="dialog" aria-labelledby="failModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title" id="failModalLabel">
                    <i class="mdi mdi-alert-circle"></i> 操作失败
                </h5>
                <button type="button" class="close text-white" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <p id="failMsg" style="font-size: 16px; line-height: 1.8;">${msg}</p>
                <div class="mt-3">
                    <small class="text-muted">如需帮助，请联系系统管理员</small>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-danger" onclick="location.reload()">刷新页面</button>
            </div>
        </div>
    </div>
</div>

<!-- 警告提示弹窗（模态框） -->
<div class="modal fade" id="warnModal" tabindex="-1" role="dialog" aria-labelledby="warnModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header bg-warning text-white">
                <h5 class="modal-title" id="warnModalLabel">
                    <i class="mdi mdi-alert"></i> 操作提醒
                </h5>
                <button type="button" class="close text-white" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <p id="warnMsg" style="font-size: 16px; line-height: 1.8;">${msg}</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/script.min.js"></script>
<style>
    .checkin-form {
        margin: 30px 0;
    }
    .checkin-buttons {
        margin: 20px 0;
    }
    .checkin-buttons button {
        padding: 12px 30px;
        font-size: 18px;
        margin: 0 10px;
        transition: all 0.3s ease;
    }
    .checkin-buttons button:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
    }
    .checkin-buttons button:disabled {
        opacity: 0.6;
        cursor: not-allowed;
        transform: none;
        box-shadow: none;
    }
    /* 当前时间样式 */
    .current-time {
        font-size: 1.1em;
        color: #666;
        margin-bottom: 20px;
    }
    /* 统计卡片样式 */
    .checkin-stats {
        margin-bottom: 30px;
    }
    .card-stat {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border-radius: 10px;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
        transition: all 0.3s ease;
    }
    .card-stat:hover {
        transform: translateY(-5px);
        box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
    }
    .card-stat .card-title {
        font-size: 0.9em;
        opacity: 0.9;
        margin-bottom: 5px;
    }
    .card-stat .card-value {
        font-size: 1.8em;
        font-weight: bold;
        margin: 0;
    }
    /* 修复label样式 */
    .label {
        display: inline-block;
        padding: .2em .6em .3em;
        font-size: 75%;
        font-weight: 700;
        line-height: 1;
        color: #fff;
        text-align: center;
        white-space: nowrap;
        vertical-align: baseline;
        border-radius: .25em;
    }
    .label-success {
        background-color: #5cb85c;
    }
    .label-danger {
        background-color: #d9534f;
    }
    /* 状态信息响应式样式 */
    .status-info {
        background: #f8f9fa;
        padding: 12px;
        margin: 15px 0;
        border-radius: 5px;
        border: 1px solid #dee2e6;
    }
    .status-info .status-text {
        font-weight: bold;
    }
    .status-info .status-completed { color: #28a745; }
    .status-info .status-partial { color: #ffc107; }
    .status-info .status-none { color: #dc3545; }
    .status-info .status-error { color: #17a2b8; }

    /* 响应式调整 */
    @media (max-width: 768px) {
        .checkin-buttons button {
            display: block;
            margin: 10px auto;
            width: 80%;
        }
        .card-stat {
            margin-bottom: 15px;
        }
        .status-info {
            padding: 8px;
            font-size: 14px;
        }
        .status-info > div {
            flex-direction: column;
            align-items: flex-start !important;
            gap: 8px;
        }
        .status-info .status-text {
            font-size: 16px;
        }
    }

    /* 弹窗样式优化 */
    .modal-content {
        border-radius: 8px;
        box-shadow: 0 5px 15px rgba(0,0,0,0.3);
    }
    .modal-header {
        border-bottom: none;
        border-radius: 8px 8px 0 0;
    }
    .modal-footer {
        border-top: none;
    }
</style>

<script>
    // 显示当前时间
    function updateCurrentTime() {
        const now = new Date();
        const options = {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        };
        const timeString = now.toLocaleString('zh-CN', options);
        document.getElementById('currentTime').textContent = timeString;
    }

    // 初始化当前时间并每秒更新
    updateCurrentTime();
    setInterval(updateCurrentTime, 1000);

    // 防重复提交：禁用按钮
    function disableButtonOnSubmit() {
        const submitBtn = document.activeElement;
        if (submitBtn && submitBtn.tagName === 'BUTTON') {
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="mdi mdi-loading mdi-spin"></i> 处理中...';
        }
        return true;
    }

    // 页面加载完成后，判断是否需要弹出失败/警告弹窗
    $(document).ready(function() {
        const msgType = '${msgType}';
        const msg = '${msg}';

        // 仅在有失败/警告消息时弹出弹窗
        if (msg && msg.trim() !== '') {
            if (msgType === 'danger') {
                // 失败弹窗
                $('#failModal').modal('show');
            } else if (msgType === 'warning') {
                // 警告弹窗
                $('#warnModal').modal('show');
            }
        }

        // 弹窗关闭后恢复按钮状态
        $('.modal').on('hidden.bs.modal', function () {
            $('#checkInBtn, #checkOutBtn').prop('disabled', false);
        });
    });
</script>
</body>
</html>