<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- 声明外部变量，解决IDE警告 --%>
<%--@elvariable id="appointmentPager" type="com.hello.utils.vo.PagerVO"--%>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>健身房客户管理系统-所有预约管理</title>
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
                                <h4>所有预约管理</h4>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-hover">
                                        <thead>
                                            <tr>
                                                <th>客户姓名</th>
                                                <th>教练姓名</th>
                                                <th>预约日期</th>
                                                <th>预约时间</th>
                                                <th>状态</th>
                                                <th>备注</th>
                                                <th>创建时间</th>
                                                <th>更新时间</th>
                                                <th>操作</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:choose>
                                                <c:when test="${not empty appointments}">
                                                    <c:forEach items="${appointments}" var="appointment">
                                                        <tr>
                                                            <td>${appointment.customer != null ? appointment.customer.name : '无'}</td>
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
                                                            <td>${appointment.updateTime}</td>
                                                            <td>
                                                                <div class="btn-group">
                                                                    <c:if test="${appointment.status == 'pending' || appointment.status == '待确认'}">
                                                                        <button class="btn btn-success btn-sm" onclick="updateStatus('${appointment.id}', 'confirmed', 'appointment')">
                                                                            <i class="mdi mdi-check"></i> 确认
                                                                        </button>
                                                                        <button class="btn btn-danger btn-sm" onclick="updateStatus('${appointment.id}', 'cancelled', 'appointment')">
                                                                            <i class="mdi mdi-close"></i> 取消
                                                                        </button>
                                                                    </c:if>
                                                                    <c:if test="${appointment.status == 'confirmed' || appointment.status == '已确认'}">
                                                                        <button class="btn btn-info btn-sm" onclick="updateStatus('${appointment.id}', 'completed', 'appointment')">
                                                                            <i class="mdi mdi-check-circle"></i> 完成
                                                                        </button>
                                                                        <button class="btn btn-danger btn-sm" onclick="updateStatus('${appointment.id}', 'cancelled', 'appointment')">
                                                                            <i class="mdi mdi-close"></i> 取消
                                                                        </button>
                                                                    </c:if>
                                                                    <c:if test="${appointment.status == 'completed' || appointment.status == '已完成' || appointment.status == 'cancelled' || appointment.status == '已取消'}">
                                                                        <button class="btn btn-default btn-sm" disabled>
                                                                            <i class="mdi mdi-check"></i> 已处理
                                                                        </button>
                                                                    </c:if>
                                                                </div>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </c:when>
                                                <c:otherwise>
                                                    <tr>
                                                        <td colspan="9" class="text-center">暂无预约记录</td>
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
                                                    <li><a href="${pageContext.request.contextPath}/appointment/admin/list?page=${appointmentPager.current - 1}" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>
                                                </c:if>

                                                <c:forEach var="pageNum" begin="1" end="${appointmentPager.totalPages}">
                                                    <c:if test="${pageNum >= appointmentPager.current - 2 && pageNum <= appointmentPager.current + 2}">
                                                        <li class="${pageNum == appointmentPager.current ? 'active' : ''}">
                                                            <a href="${pageContext.request.contextPath}/appointment/admin/list?page=${pageNum}">${pageNum}</a>
                                                        </li>
                                                    </c:if>
                                                </c:forEach>

                                                <c:if test="${appointmentPager.current < appointmentPager.totalPages}">
                                                    <li><a href="${pageContext.request.contextPath}/appointment/admin/list?page=${appointmentPager.current + 1}" aria-label="Next"><span aria-hidden="true">&raquo;</span></a></li>
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
    // 更新预约状态的AJAX函数
    function updateStatus(id, status, type) {
        if (confirm('确定要执行此操作吗？')) {
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
                        alert('操作成功！');
                        // 刷新页面以更新状态
                        window.location.reload();
                    } else {
                        alert('操作失败：' + result.msg);
                    }
                },
                error: function(xhr, status, error) {
                    alert('操作失败，请稍后重试！');
                }
            });
        }
    }
</script>
</body>
</html>