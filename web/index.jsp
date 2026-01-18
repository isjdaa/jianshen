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
          <div class="col-sm-6 col-lg-3">
            <div class="card bg-primary">
              <div class="card-body clearfix">
                <div class="pull-right">
                  <p class="h6 text-white m-t-0">班级数</p>
                  <p class="h3 text-white m-b-0 fa-1-5x" id="clazzCount">0</p>
                </div>
                <div class="pull-left"> <span class="img-avatar img-avatar-48 bg-translucent"><i class="mdi mdi-currency-cny fa-1-5x"></i></span> </div>
              </div>
            </div>
          </div>
        </div>


          <div class="col-sm-6 col-lg-3">
            <div class="card bg-danger">
              <div class="card-body clearfix">
                <div class="pull-right">
                  <p class="h6 text-white m-t-0">学生总数</p>
                  <p class="h3 text-white m-b-0 fa-1-5x"id="studentCount">0</p>
                </div>
                <div class="pull-left"> <span class="img-avatar img-avatar-48 bg-translucent"><i class="mdi mdi-account fa-1-5x"></i></span> </div>
              </div>
            </div>
          </div>



        <div class="row">

          <div class="col-lg-6">
            <div class="card">
              <div class="card-header">
                <h4>每周用户</h4>
              </div>
              <div class="card-body">
                <canvas class="js-chartjs-bars"></canvas>
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
          let studentCount=data.data.studentCount;
          let clazzCount=data.data.clazzCount;
          let clazzes=data.data.clazzes;
          console.log(studentCount.clazzCount)
          $("#studentCount").text(studentCount);
          $("#clazzCount").text(clazzCount);

          let l1=[],l2=[]
          for (let i = 0; i <clazzes.length ; i++) {
            l1.push(clazzes[i].name)
            l2.push(clazzes[i].stuCount)
          }
          var $dashChartBarsCnt  = jQuery( '.js-chartjs-bars' )[0].getContext( '2d' )

          var $dashChartBarsData = {
            labels: l1,
            datasets: [
              {
                label: '注册用户',
                borderWidth: 1,
                borderColor: 'rgba(0,0,0,0)',
                backgroundColor: 'rgba(51,202,185,0.5)',
                hoverBackgroundColor: "rgba(51,202,185,0.7)",
                hoverBorderColor: "rgba(0,0,0,0)",
                data: l2
              }
            ]
          };


          new Chart($dashChartBarsCnt, {
            type: 'bar',
            data: $dashChartBarsData,
            options:{
              scales: {
                yAxes: [
                  {
                    ticks: {
                      min: 0,  //最小值
                    },
                    display: true
                  }
                ]
              }
            }
          });
        }else{
          alert("失败")
        }
      }
    })


    });

</script>
</body>
</html>