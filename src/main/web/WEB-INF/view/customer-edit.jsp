<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>健身房客户管理系统-编辑客户</title>
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
                                <h4>编辑客户</h4>
                            </div>
                            <div class="card-body">
                                <form id="editForm">
                                    <input type="hidden" name="id" value="${entity.id}">
                                    <div class="form-group">
                                        <label for="id">客户编号</label>
                                        <input type="text" class="form-control" id="id" name="id" value="${entity.id}" readonly>
                                    </div>
                                    <div class="form-group">
                                        <label for="password">密码</label>
                                        <input type="password" class="form-control" id="password" name="password" placeholder="密码">
                                    </div>
                                    <div class="form-group">
                                        <label for="name">客户姓名</label>
                                        <input type="text" class="form-control" id="name" name="name" value="${entity.name}" placeholder="客户姓名">
                                    </div>
                                    <div class="form-group">
                                        <label for="tele">联系电话</label>
                                        <input type="text" class="form-control" id="tele" name="tele" value="${entity.tele}" placeholder="联系电话">
                                    </div>
                                    <div class="form-group">
                                        <label for="joindate">加入日期</label>
                                        <input type="date" class="form-control" id="joindate" name="joindate" value="${entity.joindate}">
                                    </div>
                                    <div class="form-group">
                                        <label for="age">年龄</label>
                                        <input type="number" class="form-control" id="age" name="age" value="${entity.age}" placeholder="年龄">
                                    </div>
                                    <div class="form-group">
                                        <label>性别</label>
                                        <div class="radio">
                                            <label>
                                                <input type="radio" name="gender" value="男" <c:if test="${entity.gender == '男'}">checked</c:if>> 男
                                            </label>
                                            <label style="margin-left: 20px;">
                                                <input type="radio" name="gender" value="女" <c:if test="${entity.gender == '女'}">checked</c:if>> 女
                                            </label>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="address">详细地址</label>
                                        <textarea class="form-control" id="address" name="address" rows="3" placeholder="详细地址">${entity.address}</textarea>
                                    </div>
                                    <div class="form-group">
                                        <label for="membershipType">会员类型</label>
                                        <select class="form-control" id="membershipType" name="membershipType">
                                            <option value="普通会员" <c:if test="${entity.membershipType == '普通会员'}">selected</c:if>>普通会员</option>
                                            <option value="银卡会员" <c:if test="${entity.membershipType == '银卡会员'}">selected</c:if>>银卡会员</option>
                                            <option value="金卡会员" <c:if test="${entity.membershipType == '金卡会员'}">selected</c:if>>金卡会员</option>
                                            <option value="钻石会员" <c:if test="${entity.membershipType == '钻石会员'}">selected</c:if>>钻石会员</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="expiryDate">到期日期</label>
                                        <input type="date" class="form-control" id="expiryDate" name="expiryDate" value="${entity.expiryDate}">
                                    </div>
                                    <div class="form-group">
                                        <label for="balance">余额</label>
                                        <input type="number" step="0.01" class="form-control" id="balance" name="balance" value="${entity.balance}" placeholder="余额">
                                    </div>
                                    <div class="form-group">
                                        <label for="coachId">教练</label>
                                        <select class="form-control" id="coachId" name="coachId">
                                            <option value="">--请选择教练--</option>
                                            <c:forEach items="${coaches}" var="coach">
                                                <option value="${coach.id}" <c:if test="${entity.coachId == coach.id}">selected</c:if>>${coach.name} - ${coach.specialization}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <button type="button" class="btn btn-primary" onclick="save()">保存</button>
                                    <button type="button" class="btn btn-default" onclick="window.location.href='${pageContext.request.contextPath}/customer'">取消</button>
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
            url: "${pageContext.request.contextPath}/customer?r=edit",
            data: $('#editForm').serialize(),
            dataType: "json",
            success: function (data) {
                if (data.success) {
                    alert('保存成功');
                    window.location.href='${pageContext.request.contextPath}/customer';
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