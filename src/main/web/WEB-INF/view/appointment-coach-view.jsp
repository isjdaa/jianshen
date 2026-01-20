<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>健身房客户管理系统-我的预约</title>
    <!-- APPOINTMENT_COACH_VIEW_VERSION:2026-01-19-1 -->
    <link rel="icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
    <script>
        // 防止用户在页面脚本加载完成前点击按钮导致 updateStatus 未定义
        window.updateStatus = window.updateStatus || function () {
            alert('页面正在加载，请稍后再试...');
        };
    </script>
</head>
<body>
<div class="lyear-layout-web">
    <div class="lyear-layout-container">
        <!--左侧导航-->
        <jsp:include page="_aside_header.jsp" />
        <!--End 左侧导航-->
        <!--头部信息-->
        <header class="lyear-layout-header">
            <nav class="navbar navbar-default">
                <div class="topbar">
                    <div class="topbar-left">
                        <div class="lyear-aside-toggler">
                            <span class="lyear-toggler-bar"></span>
                            <span class="lyear-toggler-bar"></span>
                            <span class="lyear-toggler-bar"></span>
                        </div>
                    </div>
                    <ul class="topbar-right">
                        <li class="dropdown dropdown-profile">
                            <a href="javascript:void(0);" data-toggle="dropdown">
                                <img class="img-avatar img-avatar-48" src="${pageContext.request.contextPath}/assets/images/users/avatar.jpg" alt="">
                            </a>
                            <ul class="dropdown-menu dropdown-menu-right">
                                <li><a href="javascript:void(0);"><i class="mdi mdi-account"></i> 我的资料</a></li>
                                <li><a href="javascript:void(0);"><i class="mdi mdi-lock-reset"></i> 修改密码</a></li>
                                <li class="divider"></li>
                                <li><a href="${pageContext.request.contextPath}/logout"><i class="mdi mdi-logout-variant"></i> 退出登录</a></li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </nav>
        </header>
        <!--End 头部信息-->
        <!--页面主要内容-->
        <main class="lyear-layout-content">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-lg-12">
                        <div class="card">
                            <div class="card-header">
                                <h4>我的预约请求</h4>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-hover">
                                        <thead>
                                            <tr>
                                                <th>客户姓名</th>
                                                <th>预约日期</th>
                                                <th>预约时间</th>
                                                <th>状态</th>
                                                <th>备注</th>
                                                <th>创建时间</th>
                                                <th>操作</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:choose>
                                                <c:when test="${not empty appointments}">
                                                    <c:forEach items="${appointments}" var="appointment">
                                                        <tr>
                                                            <td>${appointment.customer != null ? appointment.customer.name : '无'}</td>
                                                            <td>${appointment.appointmentDate}</td>
                                                            <td>${appointment.appointmentTime}</td>
                                                            <td>
                                                                <c:choose>
                                                                    <c:when test="${fn:trim(appointment.status) == 'pending' || fn:trim(appointment.status) == '待确认'}">
                                                                        <span class="label label-warning">待确认</span>
                                                                    </c:when>
                                                                    <c:when test="${fn:trim(appointment.status) == 'confirmed' || fn:trim(appointment.status) == '已确认'}">
                                                                        <span class="label label-success">已确认</span>
                                                                    </c:when>
                                                                    <c:when test="${fn:trim(appointment.status) == 'completed' || fn:trim(appointment.status) == '已完成'}">
                                                                        <span class="label label-info">已完成</span>
                                                                    </c:when>
                                                                    <c:when test="${fn:trim(appointment.status) == 'cancelled' || fn:trim(appointment.status) == '已取消'}">
                                                                        <span class="label label-danger">已取消</span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="label label-default">${appointment.status}</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td>${appointment.remarks != null ? appointment.remarks : '-'}</td>
                                                            <td>${appointment.createTime}</td>
                                                            <td>
                                                                <c:choose>
                                    <c:when test="${appointment.status == 'pending' || appointment.status == '待确认'}">
                                        <div class="btn-group">
                                            <button class="btn btn-success btn-sm" onclick="updateStatus('${appointment.id}', 'confirmed', 'appointment')">
                                                <i class="mdi mdi-check"></i> 确认
                                            </button>
                                            <button class="btn btn-danger btn-sm" onclick="updateStatus('${appointment.id}', 'cancelled', 'appointment')">
                                                <i class="mdi mdi-close"></i> 拒绝
                                            </button>
                                        </div>
                                    </c:when>
                                    <c:when test="${appointment.status == 'confirmed' || appointment.status == '已确认'}">
                                        <button class="btn btn-info btn-sm" onclick="updateStatus('${appointment.id}', 'completed', 'appointment')">
                                            <i class="mdi mdi-calendar-check"></i> 标记完成
                                        </button>
                                    </c:when>
                                    <c:when test="${appointment.status == 'completed' || appointment.status == '已完成' || appointment.status == 'cancelled' || appointment.status == '已取消'}">
                                        <button class="btn btn-default btn-sm" disabled>
                                            <i class="mdi mdi-calendar-check"></i> 已处理
                                        </button>
                                    </c:when>
                                    <c:otherwise>
                                        <button class="btn btn-default btn-sm" disabled title="未知状态：${appointment.status}">
                                            <i class="mdi mdi-alert-circle-outline"></i> 无法操作
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </c:when>
                                                <c:otherwise>
                                                    <tr>
                                                        <td colspan="7" class="text-center">暂无预约请求</td>
                                                    </tr>
                                                </c:otherwise>
                                            </c:choose>
                                        </tbody>
                                    </table>
                                </div>
                                <!-- 分页导航 -->
                                <c:if test="${appointmentPager != null && appointmentPager.total > 0}">
                                    <div class="text-center">
                                        <nav aria-label="Page navigation">
                                            <ul class="pagination">
                                                <c:if test="${appointmentPager.current > 1}">
                                                    <li><a href="${pageContext.request.contextPath}/appointment/coach/view?page=${appointmentPager.current - 1}" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>
                                                </c:if>

                                                <c:forEach var="pageNum" begin="1" end="${appointmentPager.totalPages}">
                                                    <c:if test="${pageNum >= appointmentPager.current - 2 && pageNum <= appointmentPager.current + 2}">
                                                        <li class="${pageNum == appointmentPager.current ? 'active' : ''}">
                                                            <a href="${pageContext.request.contextPath}/appointment/coach/view?page=${pageNum}">${pageNum}</a>
                                                        </li>
                                                    </c:if>
                                                </c:forEach>

                                                <c:if test="${appointmentPager.current < appointmentPager.totalPages}">
                                                    <li><a href="${pageContext.request.contextPath}/appointment/coach/view?page=${appointmentPager.current + 1}" aria-label="Next"><span aria-hidden="true">&raquo;</span></a></li>
                                                </c:if>
                                            </ul>
                                        </nav>
                                        <p class="text-muted">共 ${appointmentPager.total} 条记录，第 ${appointmentPager.current}/${appointmentPager.totalPages} 页</p>
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

<script src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/script.min.js"></script>
<script>
    // 显式挂到全局，避免出现 onclick 找不到函数（某些情况下脚本作用域/缓存会导致未定义）
    window.updateStatus = function(id, status, type) {
        if (window.console && console.log) {
            console.log("updateStatus click:", { id: id, status: status, type: type });
        }
        var actionText = '';
        if (status == 'confirmed') {
            actionText = '确认';
        } else if (status == 'cancelled') {
            actionText = '拒绝';
        } else if (status == 'completed') {
            actionText = '标记完成';
        }
        
        if (confirm('确定要' + actionText + '此预约吗？')) {
            $.ajax({
                url: '${pageContext.request.contextPath}/appointment/updateStatus',
                type: 'POST',
                data: {
                    id: id,
                    status: status,
                    type: type
                },
                dataType: 'json',
                success: function(result) {
                    if (result.success) {
                        alert(actionText + '成功！');
                        window.location.reload();
                    } else {
                        alert(actionText + '失败：' + result.message);
                    }
                },
                error: function() {
                    alert(actionText + '失败，请稍后重试');
                }
            });
        }
    }
</script>
</body>
</html>