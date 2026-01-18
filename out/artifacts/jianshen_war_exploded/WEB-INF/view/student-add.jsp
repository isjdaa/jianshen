<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2026/1/5
  Time: 14:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
    <title>新增班级</title>
    <link rel="icon" href="favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
    <!--日期选择插件-->
    <link rel="stylesheet" href="${pageContext.request.contextPath}js/bootstrap-datepicker/bootstrap-datepicker3.min.css">

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
                            <div class="card-body">
                                <form id="myForm" action="${pageContext.request.contextPath}/student?r=add" method="post" >
                                    <div class="form-group">
                                        <label> 学号</label>
                                        <input required class="form-control" type="text" name="sno" >
                                    </div>
                                    <div class="form-group">
                                        <label> 密码</label>
                                        <input required class="form-control" type="password" name="password" >
                                    </div>
                                    <div class="form-group">
                                        <label>姓名</label>
                                        <input required class="form-control" type="text" name="name" >
                                    </div>
                                    <div class="form-group">
                                        <label> 电话</label>
                                        <input class="form-control" type="text" name="tele" >
                                    </div>
                                    <div class="form-group">
                                        <label> 入学日期</label>
                                        <input class="form-control js-datepicker m-b-10" type="text"  name="enterdate" placeholder="yyyy-mm-dd" value="" data-date-format="yyyy-mm-dd" />
                                    </div>
                                    <div class="form-group">
                                        <label> 年龄</label>
                                        <input class="form-control" type="number" name="age" >
                                    </div>

                                    <div class="form-group">
                                        <label>性别</label>
                                        <select class="form-control"  name="gender" size="1">
                                            <option value="">请选择</option>
                                            <option value="m">男</option>
                                            <option value="w">女</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label> 详细地址</label>
                                        <textarea class="form-control"  name="address" ></textarea>
                                    </div>

                                    <div class="form-group">
                                        <label>班级</label>
                                        <select class="form-control"  name="clazzno" size="1">
                                            <option value="">请选择</option>
                                            <c:forEach items="${clazzes}" var="i" varStatus="s">
                                                <option value="${i.clazzno}">${i.clazzno} / ${i.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>


                                    <div class="form-group">
                                        <button class="btn btn-primary" type="submit">提交</button>
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

<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/perfect-scrollbar.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/main.min.js"></script>
<!--消息提示-->
<script src="${pageContext.request.contextPath}/assets/js/bootstrap-notify.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/lightyear.js"></script>
<!--日期选择插件-->
<script src="${pageContext.request.contextPath}/assets/js/bootstrap-datepicker/bootstrap-datepicker.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/bootstrap-datepicker/locales/bootstrap-datepicker.zh-CN.min.js"></script>
<script type="text/javascript">
    $(document).ready(function(e) {
        $('#myForm').on('submit',function(event){
            event.preventDefault();
            lightyear.loading('show');
            var formData=$(this).serialize();
            $.ajax({
                type:'post',
                url:$(this).attr('action'),
                data:formData,
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
        })
    });
</script>
</body>
</html>