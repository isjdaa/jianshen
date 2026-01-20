<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>健身房客户管理系统-预约课程</title>
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
                                <h4>预约课程</h4>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-hover">
                                        <thead>
                                            <tr>
                                                <th>课程名称</th>
                                                <th>教练</th>
                                                <th>课程时间</th>
                                                <th>时长(分钟)</th>
                                                <th>人数</th>
                                                <th>状态</th>
                                                <th>描述</th>
                                                <th>操作</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${courses}" var="course">
                                                <tr>
                                                    <td>${course.courseName}</td>
                                                    <td>${course.coach != null ? course.coach.name : '无'}</td>
                                                    <td>${course.courseTime}</td>
                                                    <td>${course.duration}</td>
                                                    <td>${course.currentStudents}/${course.maxStudents}</td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${course.currentStudents >= course.maxStudents}">
                                                                <span class="label label-danger">已满</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="label label-success">可预约</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>${course.description}</td>
                                                    <td>
                                                        <c:if test="${course.currentStudents < course.maxStudents}">
                                                            <button class="btn btn-primary btn-sm" onclick="bookCourse('${course.id}')">
                                                                <i class="mdi mdi-calendar-plus"></i> 立即预约
                                                            </button>
                                                        </c:if>
                                                        <c:if test="${course.currentStudents >= course.maxStudents}">
                                                            <button class="btn btn-default btn-sm" disabled>
                                                                <i class="mdi mdi-calendar-alert"></i> 已满
                                                            </button>
                                                        </c:if>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                                <a href="${pageContext.request.contextPath}/appointment/my" class="btn btn-default">返回我的预约</a>
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
    function bookCourse(courseId) {
        if (confirm('确定要预约该课程吗？预约成功后将占用课程名额。')) {
            $.ajax({
                url: '${pageContext.request.contextPath}/appointment/course',
                type: 'POST',
                data: { courseId: courseId },
                dataType: 'json',
                success: function(result) {
                    if (result.success) {
                        alert('课程预约成功！您可以在"我的预约"中查看预约详情。');
                        window.location.reload();
                    } else {
                        alert('预约失败：' + result.message);
                    }
                },
                error: function() {
                    alert('预约失败，请检查网络连接后重试');
                }
            });
        }
    }
</script>
</body>
</html>