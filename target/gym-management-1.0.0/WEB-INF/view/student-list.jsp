<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--日期格式--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2026/1/5
  Time: 14:27
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>学生列表</title>
    <link rel="icon" href="favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
</head>

<body>
<div class="lyear-layout-web">
    <div class="lyear-layout-container">

        <jsp:include page="_aside_header.jsp"></jsp:include>


        <!--页面主要内容-->
        <main class="lyear-layout-content">

            <div class="container-fluid">
                <div class="row">
                    <div class="col-md-12">
                    <div class="card">
                        <div class="card-header"><h4>学生列表</h4></div>
                        <div class="card-body">

                            <form id="form" style="margin-bottom: 20px" class="form-inline" action="${pageContext.request.contextPath}/student" method="get" >
                               <input type="hidden" id="current"  name="current" value="1">
                                <div class="form-group">
                                    <label  >学号</label>
                                    <input class="form-control" type="text"  value="${sno}" name="sno">
                                </div>
                                <div class="form-group">
                                    <label >姓名</label>
                                    <input class="form-control" type="text" value="${name}" name="name" placeholder="">
                                </div>
                                <div class="form-group">
                                    <label >性别</label>
                                    <select class="form-control"  name="gender" size="1">
                                        <option value="">请选择</option>
                                        <option <c:if test="${gender=='m'}" >selected</c:if>value="m">男</option>
                                        <option <c:if test="${gender=='w'}" >selected</c:if>value="w">女</option>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label >班级编号</label>
                                    <input class="form-control" type="text" value="${clazzno}" name="clazzno" placeholder="">
                                </div>
                                <div class="form-group">
                                    <button class="btn btn-brown btn-round" type="submit">查询</button>
                                    <c:if test="${sessionScope.role=='admin'}">
                                    <button class="btn btn-brown btn-success" type="button" onclick="location.href='?r=add'">新增</button>
                                    </c:if>
                                </div>
                            </form>

                        </div>
                    </div>
            </div>

                    <table class="table table-bordered">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>学号</th>
                            <th>密码</th>
                            <th>姓名</th>
                            <th>电话</th>
                            <th>入学时间</th>
                            <th>年龄</th>
                            <th>性别 </th>
                            <th>详细地址</th>
                            <th>班级</th>
                            <c:if test="${sessionScope.role=='admin'}">  <th>操作</th> </c:if>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${pagerVO.list}" var="i" varStatus="s">
                        <tr>
                            <th scope="row">${s.count}</th>
                            <td>${i.sno}</td>
                            <td>${i.password}</td>
                            <td>${i.name}</td>
                            <td>${i.tele}</td>
                            <td><fmt:formatDate value="${i.enterdate}" pattern="yyyy年-MM月-dd日 HH时:mm分:ss秒"/></td>
                            <td>${i. age}</td>
                            <td>${i.gender == 'm'?'男':(i.gender=='w'?'女':'')}</td>
                            <td>${i.address}</td>
                            <td>${i.clazz.clazzno}  /  ${i.clazz.name}  </td>
                            <td>
                                <c:if test="${sessionScope.role=='admin'}">
                                <button class="btn  btn-primary btn-xs" type="button" onclick="location.href='?r=edit&sno=${i.sno}'">编辑</button>
                                <button class="btn  btn-danger btn-xs" type="button" onclick="del('${i.sno}')">删除</button>
                                </c:if>
                            </td>
                        </tr>
                        </c:forEach>

                        </tbody>
                    </table>
                    <jsp:include page="_pager.jsp"></jsp:include>

                </div>

            </div>

        </main>
        <!--End 页面主要内容-->
    </div>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/perfect-scrollbar.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/main.min.js"></script>
<!--消息提示-->
<script src="${pageContext.request.contextPath}/assets/js/bootstrap-notify.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/lightyear.js"></script>

<script type="text/javascript">
    $(document).ready(function(e) {

    });
function gotoPage(page) {
$("#current").val(page)
    $("#form").submit()
}
function del(sno) {
    lightyear.loading('show');
    $.ajax({
        type:'post',
        url:'student?r=del',
        data:{sno},
        success:function (response) {
            if (response.success) {
                lightyear.loading('hide');
                lightyear.url('student');
                lightyear.notify(response.message, 'success', 500);
            } else {
                lightyear.loading('hide');
                lightyear.notify(response.message, 'danger', 3000);
            }
        },
        error:function () {
            lightyear.loading('hide');
            lightyear.notify("请求失败，请检查", 'danger', 3000);
        }
    })
}
</script>
</body>
</html>