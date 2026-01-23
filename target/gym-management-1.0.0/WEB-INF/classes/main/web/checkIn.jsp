<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>教练签到系统</title>
    <style>
        /* 保留原有核心样式，统一页面布局 */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: "Microsoft YaHei", Arial, sans-serif;
        }
        body {
            padding: 20px 40px;
            background-color: #f8f9fa;
        }
        h2 {
            color: #333;
            margin-bottom: 25px;
            padding-bottom: 10px;
            border-bottom: 1px solid #eee;
        }
        h3 {
            color: #555;
            margin: 20px 0 10px;
            font-size: 16px;
        }
        /* 保留原有alert样式，微调间距 */
        .alert {
            padding: 15px;
            margin-bottom: 20px;
            border: 1px solid transparent;
            border-radius: 4px;
            line-height: 1.5;
        }
        .alert-success {
            color: #3c763d;
            background-color: #dff0d8;
            border-color: #d6e9c6;
        }
        .alert-warning {
            color: #8a6d3b;
            background-color: #fcf8e3;
            border-color: #faebcc;
        }
        .alert-danger {
            color: #a94442;
            background-color: #f2dede;
            border-color: #ebccd1;
        }
        /* 保留原有btn样式，增加禁用态 */
        .btn {
            padding: 6px 12px;
            margin: 5px;
            border: 1px solid #ccc;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            transition: all 0.2s;
        }
        .btn-primary {
            background-color: #337ab7;
            color: white;
            border-color: #2e6da4;
        }
        .btn-primary:hover {
            background-color: #286090;
            border-color: #204d74;
        }
        .btn-primary:disabled {
            background-color: #7fa8d1;
            border-color: #6b98c5;
            cursor: not-allowed;
            opacity: 0.8;
        }
        /* 统计区域样式优化 */
        .stats-container {
            background-color: #fff;
            padding: 20px;
            border-radius: 4px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            margin-top: 20px;
        }
        .record-list {
            list-style: none;
            margin: 10px 0;
            padding-left: 10px;
        }
        .record-list li {
            padding: 6px 0;
            color: #666;
            border-bottom: 1px dashed #eee;
        }
        .empty-tip {
            color: #999;
            padding: 10px 0;
            font-style: italic;
        }
        .stat-item {
            margin: 8px 0;
            color: #666;
        }
    </style>
    <script>
        // 防重复提交：禁用点击后的按钮
        window.onload = function() {
            const submitBtns = document.querySelectorAll('button[type="submit"]');
            submitBtns.forEach(btn => {
                btn.addEventListener('click', function() {
                    this.disabled = true;
                    this.form.submit();
                });
            });
        }
    </script>
</head>
<body>
<h2>教练签到系统</h2>

<%-- 保留原有提示框逻辑，优化展示 --%>
<c:if test="${not empty msg}">
    <div class="alert alert-${msgType}">
            ${msg}
    </div>
</c:if>

<%-- 签到按钮：保持原有逻辑，增加禁用态样式 --%>
<form action="${pageContext.request.contextPath}/checkIn" method="post">
    <c:if test="${!hasCheckedIn}">
        <button type="submit" name="checkType" value="1" class="btn btn-primary">上班签到</button>
    </c:if>
    <c:if test="${hasCheckedIn && !hasCheckedOut}">
        <button type="submit" name="checkType" value="2" class="btn btn-primary">下班签退</button>
    </c:if>
    <c:if test="${hasCheckedIn && hasCheckedOut}">
        <button type="button" class="btn btn-primary" disabled>今日已完成签到签退</button>
    </c:if>
</form>

<%-- 签到统计信息：优化布局和展示 --%>
<div class="stats-container">
    <h3>今日签到记录</h3>
    <c:if test="${empty todayRecords}">
        <p class="empty-tip">今日暂无签到记录</p>
    </c:if>
    <c:if test="${not empty todayRecords}">
        <ul class="record-list">
            <c:forEach items="${todayRecords}" var="record">
                <li>
                    <strong>${record.check_type}</strong> -
                    <fmt:formatDate value="${record.check_time}" pattern="yyyy-MM-dd HH:mm:ss" />
                </li>
            </c:forEach>
        </ul>
    </c:if>

    <h3>本月统计</h3>
    <div class="stat-item">签到天数：<strong>${monthCheckinDays}</strong> 天</div>
    <div class="stat-item">迟到次数：<strong>${lateCount}</strong> 次</div>
    <div class="stat-item">早退次数：<strong>${earlyLeaveCount}</strong> 次</div>
</div>
</body>
</html>