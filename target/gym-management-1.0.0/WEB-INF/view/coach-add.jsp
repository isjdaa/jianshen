<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>健身房客户管理系统-添加教练</title>
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
                                <h4>添加教练</h4>
                            </div>
                            <div class="card-body">
                                <form id="addForm">
                                    <div class="form-group">
                                        <label for="id">教练编号</label>
                                        <input type="text" class="form-control" id="id" name="id" placeholder="教练编号">
                                    </div>
                                    <div class="form-group">
                                        <label for="password">密码</label>
                                        <input type="password" class="form-control" id="password" name="password" placeholder="密码">
                                    </div>
                                    <div class="form-group">
                                        <label for="name">教练姓名</label>
                                        <input type="text" class="form-control" id="name" name="name" placeholder="教练姓名">
                                    </div>
                                    <div class="form-group">
                                        <label for="tele">联系电话</label>
                                        <input type="text" class="form-control" id="tele" name="tele" placeholder="联系电话">
                                    </div>
                                    <div class="form-group">
                                        <label for="specialization">专业特长</label>
                                        <input type="text" class="form-control" id="specialization" name="specialization" placeholder="专业特长">
                                    </div>
                                    <div class="form-group">
                                        <label for="age">年龄</label>
                                        <input type="number" class="form-control" id="age" name="age" placeholder="年龄">
                                    </div>
                                    <div class="form-group">
                                        <label>性别</label>
                                        <div class="radio">
                                            <label>
                                                <input type="radio" name="gender" value="男" checked> 男
                                            </label>
                                            <label style="margin-left: 20px;">
                                                <input type="radio" name="gender" value="女"> 女
                                            </label>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="address">详细地址</label>
                                        <textarea class="form-control" id="address" name="address" rows="3" placeholder="详细地址"></textarea>
                                    </div>
                                    <button type="button" class="btn btn-primary" onclick="save()">保存</button>
                                    <button type="button" class="btn btn-default" onclick="window.location.href='${pageContext.request.contextPath}/coach'">取消</button>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/lightyear.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/main.min.js"></script>
<script>
    function save() {
        $.ajax({
            type: "post",
            url: "${pageContext.request.contextPath}/coach?r=add",
            data: $('#addForm').serialize(),
            dataType: "json",
            success: function (data) {
                if (data.success) {
                    alert('保存成功');
                    window.location.href='${pageContext.request.contextPath}/coach';
                } else {
                    alert(data.message);
                }
            },
            error: function () {
                alert('请求失败，请重试');
            }
        });
    }
</script>
</body>
</html>