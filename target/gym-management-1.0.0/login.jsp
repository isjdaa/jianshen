<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2026/1/5
  Time: 16:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>力健阳光健身房登录页面 </title>
    <link rel="icon" href="favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
    <style>
        .lyear-wrapper {
            position: relative;

        }
        .lyear-login {
            display: flex !important;
            min-height: 100vh;
            align-items: center !important;
            justify-content: center !important;
        }
        .lyear-login:after{
            content: '';
            min-height: inherit;
            font-size: 0;
        }
        .login-center {
            background: #fff;
            min-width: 29.25rem;
            padding: 2.14286em 3.57143em;
            border-radius: 3px;
            margin: 2.85714em;
        }
        .login-header {
            margin-bottom: 1.5rem !important;
        }
        .login-center .has-feedback.feedback-left .form-control {
            padding-left: 38px;
            padding-right: 12px;
        }
        .login-center .has-feedback.feedback-left .form-control-feedback {
            left: 0;
            right: auto;
            width: 38px;
            height: 38px;
            line-height: 38px;
            z-index: 4;
            color: #dcdcdc;
        }
        .login-center .has-feedback.feedback-left.row .form-control-feedback {
            left: 15px;
        }
    </style>
</head>

<body>
<div class="row lyear-wrapper" style="background-image: url(${pageContext.request.contextPath}/assets/images/dlbj.png); background-size: cover;">
    <div class="lyear-login">
        <div class="login-center">
            <div class="login-header text-center">
                <a href="${pageContext.request.contextPath}/"> <img alt="light year admin" src="${pageContext.request.contextPath}/assets/images/logo-sidebar.png"> </a>
            </div>
            <form action="/${pageContext.request.contextPath}login" method="post">
                <div class="form-group has-feedback feedback-left">
                    <input type="text" placeholder="请输入您的用户名" class="form-control" name="username" id="username" />
                    <span class="mdi mdi-account form-control-feedback" aria-hidden="true"></span>
                </div>
                <div class="form-group has-feedback feedback-left">
                    <input type="password" placeholder="请输入密码" class="form-control" id="password" name="password" />
                    <span class="mdi mdi-lock form-control-feedback" aria-hidden="true"></span>
                </div>
                <div class="form-group has-feedback feedback-left row">
                    <div class="col-xs-7">
                        <input type="text" name="captcha" id="captcha" class="form-control" placeholder="验证码">
                        <span class="mdi mdi-check-all form-control-feedback" aria-hidden="true"></span>
                    </div>
                    <div class="col-xs-5">
                        <img src="${pageContext.request.contextPath}/captcha" class="pull-right"  style="cursor: pointer;" onclick="this.src=this.src+'?d='+Math.random();" title="点击刷新" alt="captcha">

                    </div>
                </div>
                <div class="form-group" style="text-align: center">
                    <input type="radio" checked name="usertype" value="admin">管理员
                    <input type="radio" name="usertype" value="student">学生
                </div>
                <div class="form-group">
                    <button class="btn btn-block btn-primary" type="button" onclick="login()">立即登录</button>
                </div>
            </form>
            <hr>
            <div class="text-center">
                <a href="${pageContext.request.contextPath}/student_register.jsp" class="text-primary">还没有账号？点击注册学生账号</a>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
<script type="text/javascript">
    function login() {
    let username=$("#username").val()
    let password=$("#password").val()
    let captcha=$("#captcha").val()
    let usertype =$("input[name=usertype]:checked").val()
        console.log(usertype,username,password)
        $.ajax({
            type:"post",
            url:"${pageContext.request.contextPath}/login",
            dataType:"json",
            data:{usertype,username,password,captcha},
            success:function (data) {
                if(data.success){
                    location.href='${pageContext.request.contextPath}/index.jsp'
            }else{
                    alert(data.message)
                }
            },
            error:function () {
                alert("请求服务器失败");

            }
        });
    }

</script>
</body>
</html>