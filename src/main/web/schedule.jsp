<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>健身房客户管理系统-排课查询</title>
    <link rel="icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/js/bootstrap-datepicker/css/bootstrap-datepicker3.min.css" rel="stylesheet">
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
                                <h4>排课查询系统</h4>
                            </div>
                            <div class="card-body">
                                <c:if test="not empty msg">
                                    <div class="alert alert-info alert-dismissible" role="alert">
                                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                            <span aria-hidden="true">×</span>
                                        </button>
                                        ${msg}
                                    </div>
                                </c:if>
                                <form method="post" action="${pageContext.request.contextPath}/schedule" class="form-inline mb-4">
                                    <div class="form-group mr-4">
                                        <label class="mr-2">查询类型：</label>
                                        <div class="form-check form-check-inline">
                                            <input class="form-check-input" type="radio" name="queryType" id="today" value="1" checked>
                                            <label class="form-check-label" for="today">今日排课</label>
                                        </div>
                                        <div class="form-check form-check-inline">
                                            <input class="form-check-input" type="radio" name="queryType" id="mySchedule" value="2">
                                            <label class="form-check-label" for="mySchedule">我的排课</label>
                                        </div>
                                        <div class="form-check form-check-inline">
                                            <input class="form-check-input" type="radio" name="queryType" id="byDate" value="3">
                                            <label class="form-check-label" for="byDate">按日期查询</label>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="queryDate" class="mr-2">查询日期：</label>
                                        <input type="date" class="form-control" id="queryDate" name="queryDate" value="${today}">
                                    </div>
                                    <button type="submit" class="btn btn-primary ml-3">
                                        <i class="mdi mdi-magnify"></i> 查询
                                    </button>
                                </form>
                                <div class="table-responsive">
                                    <table class="table table-hover">
                                        <thead>
                                            <tr>
                                                <th>排课日期</th>
                                                <th>开始时间</th>
                                                <th>结束时间</th>
                                                <th>课程名称</th>
                                                <th>教练</th>
                                                <th>上课地点</th>
                                                <th>最大人数</th>
                                                <th>已报名人数</th>
                                                <th>状态</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:choose>
                                                <c:when test="not empty scheduleList">
                                                    <c:forEach items="${scheduleList}" var="schedule">
                                                        <tr>
                                                            <td>${schedule.scheduleDate}</td>
                                                            <td>${schedule.startTime}</td>
                                                            <td>${schedule.endTime}</td>
                                                            <td>${schedule.courseName}</td>
                                                            <td>${schedule.coachName}</td>
                                                            <td>${schedule.location}</td>
                                                            <td>${schedule.maxStudents}</td>
                                                            <td>${schedule.currentStudents}</td>
                                                            <td>
                                                                <c:choose>
                                                                    <c:when test="${schedule.status == 'active' || schedule.status == '已发布'}">
                                                                        <span class="label label-success">已发布</span>
                                                                    </c:when>
                                                                    <c:when test="${schedule.status == 'inactive' || schedule.status == '未发布'}">
                                                                        <span class="label label-warning">未发布</span>
                                                                    </c:when>
                                                                    <c:when test="${schedule.status == 'completed' || schedule.status == '已完成'}">
                                                                        <span class="label label-info">已完成</span>
                                                                    </c:when>
                                                                    <c:when test="${schedule.status == 'cancelled' || schedule.status == '已取消'}">
                                                                        <span class="label label-danger">已取消</span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="label label-default">${schedule.status}</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </c:when>
                                                <c:otherwise>
                                                    <tr>
                                                        <td colspan="9" class="text-center">暂无排课记录</td>
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
        </main>
        <!--End 页面主要内容-->
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/script.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/bootstrap-datepicker/bootstrap-datepicker.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/bootstrap-datepicker/locales/bootstrap-datepicker.zh-CN.min.js"></script>
<script>
    $(document).ready(function() {
        // 初始化日期选择器
        $('#queryDate').datepicker({
            format: 'yyyy-mm-dd',
            language: 'zh-CN',
            autoclose: true,
            todayHighlight: true
        });
        
        // 根据查询类型显示/隐藏日期输入框
        $('input[name="queryType"]').change(function() {
            if ($(this).val() === '3') {
                $('#queryDate').parent().show();
            } else {
                $('#queryDate').parent().hide();
            }
        });
        
        // 初始状态
        if ($('input[name="queryType"]:checked').val() !== '3') {
            $('#queryDate').parent().hide();
        }
    });
</script>
</body>
</html>