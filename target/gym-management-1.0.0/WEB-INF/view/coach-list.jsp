<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>健身房客户管理系统-教练列表</title>
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
                        <div class="card">
                            <div class="card-header">
                                <h4>教练管理</h4>
                            </div>
                            <div class="card-body">
                                <form class="form-inline" method="get" action="${pageContext.request.contextPath}/coach">
                                    <div class="form-group">
                                        <label class="sr-only" for="id">教练编号</label>
                                        <input type="text" class="form-control" id="id" name="id" placeholder="教练编号" value="${id}">
                                    </div>
                                    <div class="form-group">
                                        <label class="sr-only" for="name">教练姓名</label>
                                        <input type="text" class="form-control" id="name" name="name" placeholder="教练姓名" value="${name}">
                                    </div>
                                    <div class="form-group">
                                        <select class="form-control" name="gender" id="gender">
                                            <option value="">--性别--</option>
                                            <option value="男" <c:if test="${gender == '男'}">selected</c:if>>男</option>
                                            <option value="女" <c:if test="${gender == '女'}">selected</c:if>>女</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <input type="text" class="form-control" id="specialization" name="specialization" placeholder="专业特长" value="${specialization}">
                                    </div>
                                    <button type="submit" class="btn btn-primary">搜索</button>
                                    <button type="button" class="btn btn-success" onclick="window.location.href='${pageContext.request.contextPath}/coach?r=add'">添加教练</button>
                                </form>
                            </div>
                        </div>
                        <div class="card">
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-bordered">
                                        <thead>
                                        <tr>
                                            <th>教练编号</th>
                                            <th>教练姓名</th>
                                            <th>性别</th>
                                            <th>电话</th>
                                            <th>专业特长</th>
                                            <th>年龄</th>
                                            <th>地址</th>
                                            <th>密码</th>
                                            <th>操作</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach items="${pagerVO.list}" var="coach">
                                            <tr>
                                                <td>${coach.id}</td>
                                                <td>${coach.name}</td>
                                                <td>${coach.gender}</td>
                                                <td>${coach.tele}</td>
                                                <td>${coach.specialization}</td>
                                                <td>${coach.age}</td>
                                                <td>${coach.address}</td>
                                                <td>${coach.password}</td>
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/coach?r=edit&id=${coach.id}" class="btn btn-info btn-xs">编辑</a>
                                                    <a href="javascript:del('${coach.id}')" class="btn btn-danger btn-xs">删除</a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                                <jsp:include page="_pager.jsp" />
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
        <!--End 页面主要内容-->
    </div>
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/lightyear.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/main.min.js"></script>
<script>
    function del(id) {
        if (confirm('确定要删除这个教练吗？')) {
            $.ajax({
                type: "post",
                url: "${pageContext.request.contextPath}/coach",
                data: {id: id}, // r=del
                dataType: "json",
                success: function (data) {
                    if (data.success) {
                        alert('删除成功');
                        window.location.reload();
                    } else {
                        alert(data.message);
                    }
                },
                error: function () {
                    alert('请求失败，请重试');
                }
            });
        }
    }
</script>
</body>
</html>