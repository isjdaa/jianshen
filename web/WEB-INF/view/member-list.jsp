<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <title>会员列表</title>
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container">
    <h3>会员列表</h3>
    <table class="table table-bordered">
        <thead>
        <tr>
            <th>#</th>
            <th>会员编号</th>
            <th>姓名</th>
            <th>电话</th>
            <th>入会时间</th>
            <th>会员类型</th>
            <th>余额</th>
            <c:if test="${sessionScope.role=='admin'}"><th>操作</th></c:if>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${pagerVO.list}" var="m" varStatus="s">
            <tr>
                <td>${s.count}</td>
                <td>${m.id}</td>
                <td>${m.name}</td>
                <td>${m.tele}</td>
                <td><fmt:formatDate value="${m.joindate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                <td>${m.membershipType}</td>
                <td>${m.balance}</td>
                <td>
                    <c:if test="${sessionScope.role=='admin'}">
                        <button class="btn btn-primary btn-xs" onclick="location.href='?r=edit&id=${m.id}'">编辑</button>
                        <button class="btn btn-danger btn-xs" onclick="deleteMember('${m.id}')">删除</button>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<script src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
<script>
function deleteMember(id){
    if(!confirm('确认删除？')) return;
    $.post('${pageContext.request.contextPath}/member', {r:'delete', id:id}, function(res){
        if(res && res.code === 0){
            alert('删除成功');
            location.reload();
        } else {
            alert(res ? res.msg : '删除失败');
        }
    }, 'json');
}
</script>
</body>
</html>
