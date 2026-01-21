<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- 声明外部变量，解决IDE警告 --%>
<%--@elvariable id="coursePager" type="com.hello.utils.vo.PagerVO"--%>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>健身房客户管理系统-我的课程安排</title>
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
        <!--页面主要内容-->
        <main class="lyear-layout-content">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-lg-12">
                        <div class="card">
                            <div class="card-header">
                                <h4>我的课程安排</h4>
                            </div>
                            <div class="card-body">
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
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:choose>
                                                <c:when test="${not empty courses}">
                                                    <c:forEach items="${courses}" var="course">
                                                        <tr>
                                                            <td>${course.courseName}</td>
                                                            <td>${course.courseTime}</td>
                                                            <td>${course.duration}</td>
                                                            <td>${course.maxStudents}</td>
                                                            <td>${course.currentStudents}</td>
                                                            <td>
                                                                <c:choose>
                                                                    <c:when test="${course.status == 'active'}">
                                                                        <span class="label label-success">已发布</span>
                                                                    </c:when>
                                                                    <c:when test="${course.status == 'inactive'}">
                                                                        <span class="label label-warning">未发布</span>
                                                                    </c:when>
                                                                    <c:when test="${course.status == 'completed'}">
                                                                        <span class="label label-info">已完成</span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="label label-default">${course.status}</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td>${course.description != null ? course.description : '-'}</td>
                                                        </tr>
                                                    </c:forEach>
                                                </c:when>
                                                <c:otherwise>
                                                    <tr>
                                                        <td colspan="7" class="text-center">暂无课程安排</td>
                                                    </tr>
                                                </c:otherwise>
                                            </c:choose>
                                        </tbody>
                                    </table>
                                </div>
                                <!-- 分页导航 -->
                                <c:if test="${coursePager != null && coursePager.total > 0}">
                                    <div class="text-center">
                                        <nav aria-label="Page navigation">
                                            <ul class="pagination">
                                                <c:if test="${coursePager.current > 1}">
                                                    <li><a href="${pageContext.request.contextPath}/appointment/coach/courses?page=${coursePager.current - 1}" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>
                                                </c:if>

                                                <c:forEach var="pageNum" begin="1" end="${coursePager.totalPages}">
                                                    <c:if test="${pageNum >= coursePager.current - 2 && pageNum <= coursePager.current + 2}">
                                                        <li class="${pageNum == coursePager.current ? 'active' : ''}">
                                                            <a href="${pageContext.request.contextPath}/appointment/coach/courses?page=${pageNum}">${pageNum}</a>
                                                        </li>
                                                    </c:if>
                                                </c:forEach>

                                                <c:if test="${coursePager.current < coursePager.totalPages}">
                                                    <li><a href="${pageContext.request.contextPath}/appointment/coach/courses?page=${coursePager.current + 1}" aria-label="Next"><span aria-hidden="true">&raquo;</span></a></li>
                                                </c:if>
                                            </ul>
                                        </nav>
                                        <p class="text-muted">共 ${coursePager.total} 条记录，第 ${coursePager.current}/${coursePager.totalPages} 页</p>
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
</body>
</html>