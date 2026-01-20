<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- 声明外部变量，解决IDE警告 --%>
<%--@elvariable id="appointmentPager" type="com.hello.utils.vo.PagerVO"--%>
<%--@elvariable id="courseAppointmentPager" type="com.hello.utils.vo.PagerVO"--%>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>健身房客户管理系统-我的预约</title>
    <link rel="icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
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
                                <li><a href="${pageContext.request.contextPath}/userinfo"><i class="mdi mdi-account"></i> 我的资料</a></li>
                                <li><a href="${pageContext.request.contextPath}/password"><i class="mdi mdi-lock-reset"></i> 修改密码</a></li>
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
                        <!-- 教练预约 -->
                        <div class="card">
                            <div class="card-header">
                                <h4>教练预约</h4>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-hover">
                                        <thead>
                                            <tr>
                                                <th>教练</th>
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
                                                            <td>${appointment.coach != null ? appointment.coach.name : '无'}</td>
                                                            <td>${appointment.appointmentDate}</td>
                                                            <td>${appointment.appointmentTime}</td>
                                                            <td>
                                                <c:choose>
                                                    <c:when test="${appointment.status == 'pending' || appointment.status == '待确认'}">
                                                        <span class="label label-warning">待确认</span>
                                                    </c:when>
                                                    <c:when test="${appointment.status == 'confirmed' || appointment.status == '已确认'}">
                                                        <span class="label label-success">已确认</span>
                                                    </c:when>
                                                    <c:when test="${appointment.status == 'completed' || appointment.status == '已完成'}">
                                                        <span class="label label-info">已完成</span>
                                                    </c:when>
                                                    <c:when test="${appointment.status == 'cancelled' || appointment.status == '已取消'}">
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
                                                                <c:if test="${appointment.status == 'pending' || appointment.status == '待确认'}">
                                                                    <button class="btn btn-danger btn-sm" onclick="updateStatus('${appointment.id}', 'cancelled', 'appointment')">
                                                                        <i class="mdi mdi-calendar-remove"></i> 取消预约
                                                                    </button>
                                                                </c:if>
                                                                <c:if test="${appointment.status != 'pending' && appointment.status != '待确认'}">
                                                                    <button class="btn btn-default btn-sm" disabled>
                                                                        <i class="mdi mdi-calendar-check"></i> 已处理
                                                                    </button>
                                                                </c:if>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </c:when>
                                                <c:otherwise>
                                                    <tr>
                                                        <td colspan="7" class="text-center">暂无教练预约记录</td>
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
                                                    <li><a href="${pageContext.request.contextPath}/appointment/my?page=${appointmentPager.current - 1}" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>
                                                </c:if>

                                                <c:forEach var="pageNum" begin="1" end="${appointmentPager.totalPages}">
                                                    <c:if test="${pageNum >= appointmentPager.current - 2 && pageNum <= appointmentPager.current + 2}">
                                                        <li class="${pageNum == appointmentPager.current ? 'active' : ''}">
                                                            <a href="${pageContext.request.contextPath}/appointment/my?page=${pageNum}">${pageNum}</a>
                                                        </li>
                                                    </c:if>
                                                </c:forEach>

                                                <c:if test="${appointmentPager.current < appointmentPager.totalPages}">
                                                    <li><a href="${pageContext.request.contextPath}/appointment/my?page=${appointmentPager.current + 1}" aria-label="Next"><span aria-hidden="true">&raquo;</span></a></li>
                                                </c:if>
                                            </ul>
                                        </nav>
                                        <p class="text-muted">教练预约：共 ${appointmentPager.total} 条记录，第 ${appointmentPager.current}/${appointmentPager.totalPages} 页</p>
                                    </div>
                                </c:if>
                            </div>
                        </div>

                        <!-- 课程预约 -->
                        <div class="card">
                            <div class="card-header">
                                <h4>课程预约</h4>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-hover">
                                        <thead>
                                            <tr>
                                                <th>课程名称</th>
                                                <th>教练</th>
                                                <th>课程时间</th>
                                                <th>状态</th>
                                                <th>创建时间</th>
                                                <th>操作</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:choose>
                                                <c:when test="${not empty courseAppointments}">
                                                    <c:forEach items="${courseAppointments}" var="courseAppointment">
                                                        <tr>
                                                            <td>${courseAppointment.course != null ? courseAppointment.course.courseName : '无'}</td>
                                                            <td>
                                                                <c:if test="${courseAppointment.course != null && courseAppointment.course.coach != null}">
                                                                    ${courseAppointment.course.coach.name}
                                                                </c:if>
                                                                <c:if test="${courseAppointment.course == null || courseAppointment.course.coach == null}">
                                                                    无
                                                                </c:if>
                                                            </td>
                                                            <td>
                                                                <c:if test="${courseAppointment.course != null}">
                                                                    ${courseAppointment.course.courseTime}
                                                                </c:if>
                                                                <c:if test="${courseAppointment.course == null}">
                                                                    无
                                                                </c:if>
                                                            </td>
                                                            <td>
                                                                <c:choose>
                                                                    <c:when test="${courseAppointment.status == 'confirmed'}">
                                                                        <span class="label label-success">已确认</span>
                                                                    </c:when>
                                                                    <c:when test="${courseAppointment.status == 'cancelled'}">
                                                                        <span class="label label-danger">已取消</span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="label label-default">${courseAppointment.status}</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td>${courseAppointment.createTime}</td>
                                                            <td>
                                                                <c:if test="${courseAppointment.status == 'confirmed'}">
                                                                    <button class="btn btn-danger btn-sm" onclick="updateStatus('${courseAppointment.id}', 'cancelled', 'courseAppointment')">
                                                                        <i class="mdi mdi-calendar-remove"></i> 取消预约
                                                                    </button>
                                                                </c:if>
                                                                <c:if test="${courseAppointment.status != 'confirmed'}">
                                                                    <button class="btn btn-default btn-sm" disabled>
                                                                        <i class="mdi mdi-calendar-check"></i> 已处理
                                                                    </button>
                                                                </c:if>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </c:when>
                                                <c:otherwise>
                                                    <tr>
                                                        <td colspan="6" class="text-center">暂无课程预约记录</td>
                                                    </tr>
                                                </c:otherwise>
                                            </c:choose>
                                        </tbody>
                                    </table>
                                </div>
                                <!-- 分页导航 -->
                                <c:if test="${courseAppointmentPager != null && courseAppointmentPager.total > 0}">
                                    <div class="text-center">
                                        <nav aria-label="Page navigation">
                                            <ul class="pagination">
                                                <c:if test="${courseAppointmentPager.current > 1}">
                                                    <li><a href="${pageContext.request.contextPath}/appointment/my?page=${courseAppointmentPager.current - 1}" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>
                                                </c:if>

                                                <c:forEach var="pageNum" begin="1" end="${courseAppointmentPager.totalPages}">
                                                    <c:if test="${pageNum >= courseAppointmentPager.current - 2 && pageNum <= courseAppointmentPager.current + 2}">
                                                        <li class="${pageNum == courseAppointmentPager.current ? 'active' : ''}">
                                                            <a href="${pageContext.request.contextPath}/appointment/my?page=${pageNum}">${pageNum}</a>
                                                        </li>
                                                    </c:if>
                                                </c:forEach>

                                                <c:if test="${courseAppointmentPager.current < courseAppointmentPager.totalPages}">
                                                    <li><a href="${pageContext.request.contextPath}/appointment/my?page=${courseAppointmentPager.current + 1}" aria-label="Next"><span aria-hidden="true">&raquo;</span></a></li>
                                                </c:if>
                                            </ul>
                                        </nav>
                                        <p class="text-muted">课程预约：共 ${courseAppointmentPager.total} 条记录，第 ${courseAppointmentPager.current}/${courseAppointmentPager.totalPages} 页</p>
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
    function updateStatus(id, status, type) {
        var actionText = '';
        var confirmText = '';

        if (type === 'appointment') {
            if (status === 'cancelled') {
                actionText = '取消预约';
                confirmText = '确定要取消此教练预约吗？取消后将无法恢复。';
            }
        } else if (type === 'courseAppointment') {
            if (status === 'cancelled') {
                actionText = '取消预约';
                confirmText = '确定要取消此课程预约吗？取消后将无法恢复，且名额将释放给其他学员。';
            }
        }

        if (confirm(confirmText)) {
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
                    alert(actionText + '失败，请检查网络连接后重试');
                }
            });
        }
    }
</script>
</body>
</html>