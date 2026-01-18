<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <title>健身房会员注册</title>
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container">
    <h3>会员注册</h3>
    <div class="form-group">
        <input type="text" id="id" class="form-control" placeholder="请输入会员编号（例如手机号）"/>
    </div>
    <div class="form-group">
        <input type="password" id="password" class="form-control" placeholder="请输入密码"/>
    </div>
    <div class="form-group">
        <input type="text" id="name" class="form-control" placeholder="姓名"/>
    </div>
    <div class="form-group">
        <input type="text" id="tele" class="form-control" placeholder="联系电话"/>
    </div>
    <div class="form-group">
        <input type="date" id="joindate" class="form-control"/>
    </div>
    <div class="form-group">
        <select id="membershipType" class="form-control">
            <option value="monthly">月卡</option>
            <option value="annual">年卡</option>
            <option value="single">次卡</option>
        </select>
    </div>
    <div class="form-group">
        <button class="btn btn-primary" onclick="register()">注册</button>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
<script>
function register(){
    let data = {
        r: 'self_register',
        id: $('#id').val(),
        password: $('#password').val(),
        name: $('#name').val(),
        tele: $('#tele').val(),
        joindate: $('#joindate').val(),
        membershipType: $('#membershipType').val()
    };
    $.post('${pageContext.request.contextPath}/member', data, function(res){
        if(res && res.code === 0){
            alert('注册成功，去登录');
            location.href='${pageContext.request.contextPath}/login.jsp';
        } else {
            alert(res ? res.msg : '注册失败');
        }
    }, 'json');
}
</script>
</body>
</html>
