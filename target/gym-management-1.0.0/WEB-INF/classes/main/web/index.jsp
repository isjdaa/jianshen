<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2026/1/5
  Time: 14:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
  <title>首页 </title>
  <link rel="icon" href="favicon.ico" type="image/ico">
  <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
</head>

<body>
<div class="lyear-layout-web">
  <div class="lyear-layout-container">

    <jsp:include page="WEB-INF/view/_aside_header.jsp"></jsp:include>


    <!--页面主要内容-->
    <main class="lyear-layout-content">

      <div class="container-fluid">

        <div class="row">
          <c:choose>
            <c:when test="${sessionScope.role == 'admin'}">
              <!-- 管理员显示统计信息 -->
              <div class="col-sm-6 col-lg-3">
                <div class="card bg-primary">
                  <div class="card-body clearfix">
                    <div class="pull-right">
                      <p class="h6 text-white m-t-0">客户数</p>
                      <p class="h3 text-white m-b-0 fa-1-5x" id="customerCount">0</p>
                    </div>
                    <div class="pull-left"> <span class="img-avatar img-avatar-48 bg-translucent"><i class="mdi mdi-account-group fa-1-5x"></i></span> </div>
                  </div>
                </div>
              </div>

              <div class="col-sm-6 col-lg-3">
                <div class="card bg-danger">
                  <div class="card-body clearfix">
                    <div class="pull-right">
                      <p class="h6 text-white m-t-0">教练数</p>
                      <p class="h3 text-white m-b-0 fa-1-5x" id="coachCount">0</p>
                    </div>
                    <div class="pull-left"> <span class="img-avatar img-avatar-48 bg-translucent"><i class="mdi mdi-account-tie fa-1-5x"></i></span> </div>
                  </div>
                </div>
              </div>
            </c:when>
            <c:when test="${sessionScope.role == 'customer'}">
              <!-- 客户显示预约功能 -->
              <div class="col-sm-6 col-lg-6">
                <div class="card bg-primary">
                  <div class="card-body text-center">
                    <h4 class="card-title">预约教练</h4>
                    <p class="card-text">选择合适的教练进行预约</p>
                    <a href="${pageContext.request.contextPath}/appointment/coach" class="btn btn-light">立即预约</a>
                  </div>
                </div>
              </div>

              <div class="col-sm-6 col-lg-6">
                <div class="card bg-success">
                  <div class="card-body text-center">
                    <h4 class="card-title">预约课程</h4>
                    <p class="card-text">查看并预约感兴趣的课程</p>
                    <a href="${pageContext.request.contextPath}/appointment/course" class="btn btn-light">立即预约</a>
                  </div>
                </div>
              </div>
            </c:when>
            <c:when test="${sessionScope.role == 'coach'}">
              <!-- 教练显示我的预约 -->
              <div class="col-sm-12 col-lg-6 offset-lg-3">
                <div class="card bg-info">
                  <div class="card-body text-center">
                    <h4 class="card-title">我的预约</h4>
                    <p class="card-text">查看客户对您的预约情况</p>
                    <a href="${pageContext.request.contextPath}/appointment/coach/view" class="btn btn-light">查看预约</a>
                  </div>
                </div>
              </div>
            </c:when>
          </c:choose>
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

<!--图表插件-->
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/Chart.js"></script>
<script type="text/javascript">
  $(document).ready(function(e) {

    $.ajax({
      type:"get",
      url:"${pageContext.request.contextPath}/index",
      dataType:"json",
      success:function (data) {
        if(data.success){
          let customerCount=data.data.customerCount;
          let coachCount=data.data.coachCount;
          
          $("#customerCount").text(customerCount);
          $("#coachCount").text(coachCount);
        }else{
          alert("数据加载失败")
        }
      }
    })

    });

</script>
</body>
</html>