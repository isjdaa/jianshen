<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>健身房客户管理系统-教练签到</title>
    <link rel="icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
</head>
<body>
<div class="lyear-layout-web">
    <div class="lyear-layout-container">
        <!--左侧导航-->
        <jsp:include page="WEB-INF/view/_aside_header.jsp" />
        <!--End 左侧导航-->
        <!--页面主要内容-->
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
                                    <c:if test="not empty msg">
                                        <div class="alert alert-info alert-dismissible" role="alert">
                                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                                <span aria-hidden="true">×</span>
                                            </button>
                                            ${msg}
                                        </div>
                                    </c:if>
                                    <form method="post" action="${pageContext.request.contextPath}/checkIn" class="checkin-form">
                                        <h5>欢迎回来，${sessionScope.user.name} 教练！</h5>
                                        <div class="checkin-buttons">
                                            <button type="submit" name="checkType" value="1" class="btn btn-success btn-lg m-r-20">
                                                <i class="mdi mdi-clock-in"></i> 上班签到
                                            </button>
                                            <button type="submit" name="checkType" value="2" class="btn btn-danger btn-lg">
                                                <i class="mdi mdi-clock-out"></i> 下班签退
                                            </button>
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
                                                                    <c:if test="${record.checkType == '上班签到'}">
                                                                        <span class="label label-success">${record.checkType}</span>
                                                                    </c:if>
                                                                    <c:if test="${record.checkType == '下班签退'}">
                                                                        <span class="label label-danger">${record.checkType}</span>
                                                                    </c:if>
                                                                </td>
                                                                <td>${record.checkTime}</td>
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
        <!--End 页面主要内容-->
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
    }
</style>
</body>
</html>