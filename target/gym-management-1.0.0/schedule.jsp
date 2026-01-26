<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>健身房客户管理系统-排课查询</title>
    <link rel="icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/ico">
    <!-- 修复前端404：所有资源路径添加contextPath -->
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
                                <h4>排课${sessionScope.role == 'admin' ? '管理' : '查询'}系统</h4>
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
                                <!-- 查询表单只在查看排课模式下显示 -->
                                <c:if test="${param.action == 'view'}">
                                    <form method="post" action="${pageContext.request.contextPath}/schedule?action=view" class="form-inline mb-4">
                                        <div class="form-group mr-4">
                                            <label class="mr-2">查询类型：</label>
                                            <c:if test="${sessionScope.role == 'admin'}">
                                                <div class="form-check form-check-inline">
                                                    <input class="form-check-input" type="radio" name="queryType" id="today" value="1" ${param.queryType == '1' ? 'checked' : ''}>
                                                    <label class="form-check-label" for="today">今日排课</label>
                                                </div>
                                                <div class="form-check form-check-inline">
                                                    <input class="form-check-input" type="radio" name="queryType" id="allSchedule" value="4" ${empty param.queryType or param.queryType == '4' ? 'checked' : ''}>
                                                    <label class="form-check-label" for="allSchedule">所有排课</label>
                                                </div>
                                            </c:if>
                                            <c:if test="${sessionScope.role != 'admin'}">
                                                <div class="form-check form-check-inline">
                                                    <input class="form-check-input" type="radio" name="queryType" id="today" value="1" ${empty param.queryType or param.queryType == '1' ? 'checked' : ''}>
                                                    <label class="form-check-label" for="today">今日排课</label>
                                                </div>
                                                <c:if test="${sessionScope.role == 'coach'}">
                                                    <div class="form-check form-check-inline">
                                                        <input class="form-check-input" type="radio" name="queryType" id="mySchedule" value="2" ${param.queryType == '2' ? 'checked' : ''}>
                                                        <label class="form-check-label" for="mySchedule">我的排课</label>
                                                    </div>
                                                </c:if>
                                            </c:if>
                                            <div class="form-check form-check-inline">
                                                <input class="form-check-input" type="radio" name="queryType" id="byDate" value="3" ${param.queryType == '3' ? 'checked' : ''}>
                                                <label class="form-check-label" for="byDate">按日期查询</label>
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label for="queryDate" class="mr-2">查询日期：</label>
                                            <input type="date" class="form-control" id="queryDate" name="queryDate" value="${not empty param.queryDate ? param.queryDate : today}">
                                        </div>
                                        <button type="submit" class="btn btn-primary ml-3">
                                            <i class="mdi mdi-magnify"></i> 查询
                                        </button>
                                    </form>
                                </c:if>

                                <!-- 管理员操作按钮 - 仅在非查看模式下显示 -->
                                <c:if test="${sessionScope.role == 'admin' && param.action != 'view'}">
                                    <div class="mb-3">
                                        <a href="${pageContext.request.contextPath}/schedule?action=add" class="btn btn-primary">
                                            <i class="mdi mdi-plus"></i> 添加排课
                                        </a>
                                    </div>
                                </c:if>

                                <!-- 管理员统计信息 - 仅在查看模式下显示 -->
                                <c:if test="${sessionScope.role == 'admin' && param.action == 'view'}">
                                    <div class="row mb-4">
                                        <div class="col-md-3">
                                            <div class="card card-stat">
                                                <div class="card-body text-center">
                                                    <h6 class="card-title">今日课程</h6>
                                                    <h4 class="card-value">${todayCount != null ? todayCount : 0}</h4>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="card card-stat">
                                                <div class="card-body text-center">
                                                    <h6 class="card-title">本周课程</h6>
                                                    <h4 class="card-value">${weekCount != null ? weekCount : 0}</h4>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="card card-stat">
                                                <div class="card-body text-center">
                                                    <h6 class="card-title">总课程数</h6>
                                                    <h4 class="card-value">${totalCount != null ? totalCount : 0}</h4>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-md-3">
                                            <div class="card card-stat">
                                                <div class="card-body text-center">
                                                    <h6 class="card-title">活跃教练</h6>
                                                    <h4 class="card-value">${coachCount != null ? coachCount : 0}</h4>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>

                                <!-- 排课列表只在查看排课模式下显示 -->
                                <c:if test="${param.action == 'view'}">
                                    <div class="table-responsive">
                                        <table class="table table-hover">
                                            <thead>
                                            <tr>
                                                <th>课程名称</th>
                                                <th>上课时间</th>
                                                <th>时长(分钟)</th>
                                                <th>最大人数</th>
                                                <th>已报名人数</th>
                                                <th>状态</th>
                                                <th>课程描述</th>
                                                <th>教练名称</th>
                                                <th>上课地点</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:choose>
                                                <c:when test="${not empty scheduleList}">
                                                    <c:forEach items="${scheduleList}" var="schedule">
                                                        <tr>
                                                            <td>${schedule.courseName}</td>
                                                            <td>${schedule.scheduleDate} ${schedule.startTime}</td>
                                                            <td>${schedule.duration}</td>
                                                            <td>${schedule.maxStudents}</td>
                                                            <td>${schedule.currentStudents}</td>
                                                            <td>
                                                                <c:choose>
                                                                    <c:when test="${schedule.status == 'active' || schedule.status == '已发布'}">
                                                                        <span class="label label-success">已发布</span>
                                                                    </c:when>
                                                                    <c:when test="${schedule.status == 'inactive' || schedule.status == '未开始'}">
                                                                        <span class="label label-warning">未开始</span>
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
                                                            <td>${schedule.description != null ? schedule.description : '-'}</td>
                                                            <td>${schedule.coachName}</td>
                                                            <td>${schedule.location}</td>
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
                                </c:if>

                                <!-- 排课管理模式下的管理提示 -->
                                <c:if test="${sessionScope.role == 'admin' && param.action != 'view'}">
                                    <div class="alert alert-info mt-4">
                                        <h5><i class="mdi mdi-information-outline"></i> 排课管理</h5>
                                        <p>点击"添加排课"按钮创建新课程，或使用"查看排课"功能查看和搜索现有排课记录。</p>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
        <!--End 页面主要内容-->
    </div>
</div>

<!-- 修复前端404：所有JS路径添加contextPath -->
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

        // 初始状态：非按日期查询则隐藏日期框
        if ($('input[name="queryType"]:checked').val() !== '3') {
            $('#queryDate').parent().hide();
        }
    });

    // 删除排课
    function deleteSchedule(id, courseName) {
        if (confirm('确定要删除课程 "' + courseName + '" 吗？此操作不可恢复！')) {
            $.ajax({
                url: '${pageContext.request.contextPath}/schedule',
                type: 'POST',
                data: {
                    action: 'delete',
                    id: id
                },
                dataType: 'json',
                success: function(response) {
                    if (response.success) {
                        alert('删除成功！');
                        location.reload();
                    } else {
                        alert('删除失败：' + response.message);
                    }
                },
                error: function() {
                    alert('删除失败，请重试！');
                }
            });
        }
    }
</script>

<style>
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
        font-size: 2em;
        font-weight: bold;
        margin: 0;
    }
</style>
</body>
</html>