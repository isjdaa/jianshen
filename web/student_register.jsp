<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>力健阳光健身房-学生注册</title>
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
        .radio-inline{margin-right:15px;}
    </style>
</head>
<body>
<div class="row lyear-wrapper" style="background-image: url(${pageContext.request.contextPath}/assets/images/dlbj.png); background-size: cover;">
    <div class="lyear-login">
        <div class="login-center">
            <div class="login-header text-center">
                <a href="${pageContext.request.contextPath}/"> <img alt="light year admin" src="${pageContext.request.contextPath}/assets/images/logo-sidebar.png"> </a>
                <h4 class="text-primary" style="margin-top:10px;">学生账号注册</h4>
            </div>
            <form>
                <div class="form-group has-feedback feedback-left">
                    <input type="text" placeholder="请输入学号（登录账号）" class="form-control" id="sno" />
                    <span class="mdi mdi-account-card-details form-control-feedback" aria-hidden="true"></span>
                </div>
                <div class="form-group has-feedback feedback-left">
                    <input type="password" placeholder="请输入密码" class="form-control" id="password" />
                    <span class="mdi mdi-lock form-control-feedback" aria-hidden="true"></span>
                </div>
                <div class="form-group has-feedback feedback-left">
                    <input type="text" placeholder="请输入姓名" class="form-control" id="name" />
                    <span class="mdi mdi-account form-control-feedback" aria-hidden="true"></span>
                </div>
                <div class="form-group has-feedback feedback-left">
                    <input type="text" placeholder="请输入联系电话" class="form-control" id="tele" />
                    <span class="mdi mdi-phone form-control-feedback" aria-hidden="true"></span>
                </div>
                <div class="form-group has-feedback feedback-left">
                    <input type="date" class="form-control" id="enterdate" style="padding-left: 10px;">
                    <span class="mdi mdi-calendar form-control-feedback" aria-hidden="true"></span>
                </div>
                <div class="form-group has-feedback feedback-left">
                    <input type="number" placeholder="请输入年龄" class="form-control" id="age" />
                    <span class="mdi mdi-human form-control-feedback" aria-hidden="true"></span>
                </div>
                <div class="form-group">
                    <label class="radio-inline">
                        <input type="radio" name="gender" value="男" checked> 男
                    </label>
                    <label class="radio-inline">
                        <input type="radio" name="gender" value="女"> 女
                    </label>
                </div>
                <div class="form-group has-feedback feedback-left">
                    <input type="text" placeholder="请输入班级编号" class="form-control" id="clazzno" />
                    <span class="mdi mdi-school form-control-feedback" aria-hidden="true"></span>
                </div>
                <div class="form-group has-feedback feedback-left">
                    <input type="text" placeholder="请输入详细地址" class="form-control" id="address" />
                    <span class="mdi mdi-home-map-marker form-control-feedback" aria-hidden="true"></span>
                </div>
                <div class="form-group">
                    <button class="btn btn-block btn-primary" type="button" onclick="register()">完成注册</button>
                </div>
            </form>
            <hr>
            <div class="text-center">
                <a href="${pageContext.request.contextPath}/index.jsp" class="text-primary">已有账号？返回登录</a>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
<script type="text/javascript">
    function register() {
        let sno = $("#sno").val();
        let password = $("#password").val();
        let name = $("#name").val();
        let tele = $("#tele").val();
        let enterdate = $("#enterdate").val();
        let age = $("#age").val();
        let gender = $("input[name=gender]:checked").val();
        let clazzno = $("#clazzno").val();
        let address = $("#address").val();

        // 前端简单校验
        if(sno==""||password==""||name==""||clazzno==""){
            alert("学号、密码、姓名、班级编号为必填项！");
            return;
        }

        $.ajax({
            type:"post",
            url:"${pageContext.request.contextPath}/register",
            dataType:"json",
            data:{sno,password,name,tele,enterdate,age,gender,clazzno,address},
            success:function (data) {
                if(data.success){
                    alert("注册成功！即将返回登录页");
                    location.href="${pageContext.request.contextPath}/index.jsp";
                }else{
                    alert(data.message);
                }
            },
            error:function () {
                alert("请求服务器失败，请重试！");
            }
        });
    }
</script>
</body>
</html>