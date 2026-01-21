<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>健身房客户管理系统-预约教练</title>
    <link rel="icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
</head>
<body>
<div class="lyear-layout-web">
    <div class="lyear-layout-container">
        <!--左侧导航和头部信息-->
        <jsp:include page="_aside_header.jsp" />
        <!--页面主要内容-->
        <main class="lyear-layout-content">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-lg-12">
                        <div class="card">
                            <div class="card-header">
                                <h4>预约教练</h4>
                            </div>
                            <div class="card-body">
                                <form id="appointmentForm" method="post" action="${pageContext.request.contextPath}/appointment/coach">
                                    <div class="form-group">
                                        <label for="coachId">选择教练</label>
                                        <select class="form-control" id="coachId" name="coachId" required>
                                            <option value="">请选择教练</option>
                                            <c:forEach items="${coaches}" var="coach">
                                                <option value="${coach.id}">${coach.name} - ${coach.specialty}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="appointmentDate">预约日期</label>
                                        <input type="date" class="form-control" id="appointmentDate" name="appointmentDate" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="appointmentTime">预约时间</label>
                                        <select class="form-control" id="appointmentTime" name="appointmentTime" required>
                                            <option value="">请选择时间</option>
                                            <option value="09:00-10:00">09:00-10:00</option>
                                            <option value="10:00-11:00">10:00-11:00</option>
                                            <option value="14:00-15:00">14:00-15:00</option>
                                            <option value="15:00-16:00">15:00-16:00</option>
                                            <option value="16:00-17:00">16:00-17:00</option>
                                            <option value="19:00-20:00">19:00-20:00</option>
                                            <option value="20:00-21:00">20:00-21:00</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="remarks">备注</label>
                                        <textarea class="form-control" id="remarks" name="remarks" rows="3" placeholder="请输入备注信息"></textarea>
                                    </div>
                                    <button type="submit" class="btn btn-primary">提交预约</button>
                                    <a href="${pageContext.request.contextPath}/appointment/my" class="btn btn-default">返回我的预约</a>
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

<script src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/script.min.js"></script>
<script>
    $(document).ready(function() {
        // 设置最小日期为今天
        var today = new Date().toISOString().split('T')[0];
        document.getElementById('appointmentDate').min = today;
        
        $('#appointmentForm').submit(function(e) {
            e.preventDefault();

            // 表单验证
            var coachId = $('#coachId').val();
            var appointmentDate = $('#appointmentDate').val();
            var appointmentTime = $('#appointmentTime').val();

            if (!coachId) {
                alert('请选择教练！');
                $('#coachId').focus();
                return false;
            }
            if (!appointmentDate) {
                alert('请选择预约日期！');
                $('#appointmentDate').focus();
                return false;
            }
            if (!appointmentTime) {
                alert('请选择预约时间！');
                $('#appointmentTime').focus();
                return false;
            }

            // 显示加载状态
            var submitBtn = $(this).find('button[type="submit"]');
            var originalText = submitBtn.text();
            submitBtn.prop('disabled', true).text('提交中...');

            $.ajax({
                url: $(this).attr('action'),
                type: 'POST',
                data: $(this).serialize(),
                dataType: 'json',
                success: function(result) {
                    submitBtn.prop('disabled', false).text(originalText);
                    if (result.success) {
                        alert('预约成功！教练确认后您将收到通知。');
                        window.location.href = '${pageContext.request.contextPath}/appointment/my';
                    } else {
                        alert('预约失败：' + result.message);
                    }
                },
                error: function() {
                    submitBtn.prop('disabled', false).text(originalText);
                    alert('预约失败，请检查网络连接后重试');
                }
            });
        });
    });
</script>
</body>
</html>