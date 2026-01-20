<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>健身房客户管理系统-修改密码</title>
    <link rel="icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
    <script src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/script.min.js"></script>
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
                                <img class="img-avatar img-avatar-48" 
                                     src="${sessionScope.user.avatar != null && !sessionScope.user.avatar.isEmpty() ? sessionScope.user.avatar : '${pageContext.request.contextPath}/assets/images/users/avatar.jpg'}" 
                                     alt="">
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
                                <h4>修改密码</h4>
                            </div>
                            <div class="card-body">
                                <form id="passwordForm" class="form-horizontal">
                                    <div class="form-group">
                                        <label class="col-xs-12" for="oldPassword">原密码</label>
                                        <div class="col-xs-12 col-sm-6">
                                            <input type="password" id="oldPassword" name="oldPassword" class="form-control" 
                                                   placeholder="请输入原密码">
                                        </div>
                                    </div>
                                    
                                    <div class="form-group">
                                        <label class="col-xs-12" for="newPassword">新密码</label>
                                        <div class="col-xs-12 col-sm-6">
                                            <input type="password" id="newPassword" name="newPassword" class="form-control" 
                                                   placeholder="请输入新密码">
                                        </div>
                                    </div>
                                    
                                    <div class="form-group">
                                        <label class="col-xs-12" for="confirmPassword">确认新密码</label>
                                        <div class="col-xs-12 col-sm-6">
                                            <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" 
                                                   placeholder="请再次输入新密码">
                                        </div>
                                    </div>
                                    
                                    <div class="form-group m-b-0">
                                        <div class="col-xs-12 col-sm-6">
                                            <button type="submit" class="btn btn-primary">保存修改</button>
                                            <button type="button" class="btn btn-default" onclick="window.history.back()">取消</button>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
        <!--End 页面主要内容-->
    </div>
</div>

<script>
    // 表单提交
    $(document).ready(function() {
        $('#passwordForm').submit(function(e) {
            e.preventDefault();
            
            // 前端验证
            var oldPassword = $('#oldPassword').val().trim();
            var newPassword = $('#newPassword').val().trim();
            var confirmPassword = $('#confirmPassword').val().trim();
            
            if (oldPassword === '') {
                alert('请输入原密码');
                return;
            }
            
            if (newPassword === '') {
                alert('请输入新密码');
                return;
            }
            
            if (newPassword.length < 6) {
                alert('新密码长度不能少于6位');
                return;
            }
            
            if (newPassword !== confirmPassword) {
                alert('两次输入的新密码不一致');
                return;
            }
            
            // 提交表单
            $.ajax({
                url: '${pageContext.request.contextPath}/password',
                type: 'POST',
                data: $(this).serialize(),
                dataType: 'json',
                success: function(result) {
                    if (result.success) {
                        alert('密码修改成功');
                        window.location.href = '${pageContext.request.contextPath}/';
                    } else {
                        alert('修改失败：' + result.msg);
                    }
                },
                error: function() {
                    alert('修改失败，请稍后重试');
                }
            });
        });
    });
</script>
</body>
</html>