 <%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">
    <title>健身房客户管理系统-${isEdit ? '编辑' : '添加'}排课</title>
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
                                <h4>${isEdit ? '编辑' : '添加'}排课</h4>
                                <p class="text-muted">${isEdit ? '修改课程信息' : '创建新的课程安排'}</p>
                            </div>
                            <div class="card-body">
                                <form id="scheduleForm" class="needs-validation" novalidate>
                                    <input type="hidden" name="action" value="${isEdit ? 'update' : 'add'}">
                                    <c:if test="${isEdit}">
                                        <input type="hidden" name="id" value="${schedule.id}">
                                    </c:if>

                                    <div class="row">
                                        <div class="col-md-6">
                                            <div class="form-group">
                                                <label for="coachId">选择教练 *</label>
                                                <select class="form-control" id="coachId" name="coachId" required>
                                                    <option value="">请选择教练</option>
                                                    <c:forEach items="${coaches}" var="coach">
                                                        <option value="${coach.id}" ${schedule.coachId == coach.id ? 'selected' : ''}>${coach.name}</option>
                                                    </c:forEach>
                                                </select>
                                                <div class="invalid-feedback">请选择教练</div>
                                            </div>
                                        </div>

                                        <div class="col-md-6">
                                            <div class="form-group">
                                                <label for="courseName">课程名称 *</label>
                                                <input type="text" class="form-control" id="courseName" name="courseName"
                                                       value="${schedule.courseName}" required maxlength="64">
                                                <div class="invalid-feedback">请输入课程名称</div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="row">
                                        <div class="col-md-6">
                                            <div class="form-group">
                                                <label for="courseTime">课程时间 *</label>
                                                <input type="datetime-local" class="form-control" id="courseTime" name="courseTime"
                                                       value="${isEdit ? schedule.scheduleDate.substring(0,16) : ''}" required>
                                                <div class="invalid-feedback">请选择课程时间</div>
                                            </div>
                                        </div>

                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="duration">课程时长（分钟）*</label>
                                                <select class="form-control" id="duration" name="duration" required>
                                                    <option value="">请选择时长</option>
                                                    <option value="30" ${schedule.duration == 30 ? 'selected' : ''}>30分钟</option>
                                                    <option value="45" ${schedule.duration == 45 ? 'selected' : ''}>45分钟</option>
                                                    <option value="60" ${schedule.duration == 60 ? 'selected' : ''}>60分钟</option>
                                                    <option value="75" ${schedule.duration == 75 ? 'selected' : ''}>75分钟</option>
                                                    <option value="90" ${schedule.duration == 90 ? 'selected' : ''}>90分钟</option>
                                                    <option value="120" ${schedule.duration == 120 ? 'selected' : ''}>120分钟</option>
                                                </select>
                                                <div class="invalid-feedback">请选择课程时长</div>
                                            </div>
                                        </div>

                                        <div class="col-md-3">
                                            <div class="form-group">
                                                <label for="maxStudents">最大报名人数 *</label>
                                                <select class="form-control" id="maxStudents" name="maxStudents" required>
                                                    <option value="">请选择人数</option>
                                                    <option value="5" ${schedule.maxStudents == 5 ? 'selected' : ''}>5人</option>
                                                    <option value="8" ${schedule.maxStudents == 8 ? 'selected' : ''}>8人</option>
                                                    <option value="10" ${schedule.maxStudents == 10 ? 'selected' : ''}>10人</option>
                                                    <option value="12" ${schedule.maxStudents == 12 ? 'selected' : ''}>12人</option>
                                                    <option value="15" ${schedule.maxStudents == 15 ? 'selected' : ''}>15人</option>
                                                    <option value="20" ${schedule.maxStudents == 20 ? 'selected' : ''}>20人</option>
                                                </select>
                                                <div class="invalid-feedback">请选择最大报名人数</div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label for="description">课程描述</label>
                                        <textarea class="form-control" id="description" name="description"
                                                  rows="3" maxlength="255"
                                                  placeholder="请输入课程的详细描述">${schedule.description}</textarea>
                                    </div>

                                    <div class="form-group">
                                        <button type="submit" class="btn btn-primary">
                                            <i class="mdi mdi-content-save"></i> ${isEdit ? '更新' : '创建'}排课
                                        </button>
                                        <a href="${pageContext.request.contextPath}/schedule" class="btn btn-secondary ml-2">
                                            <i class="mdi mdi-arrow-left"></i> 返回列表
                                        </a>
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

<script src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/script.min.js"></script>

<script>
// 表单验证
(function() {
    'use strict';
    window.addEventListener('load', function() {
        var forms = document.getElementsByClassName('needs-validation');
        var validation = Array.prototype.filter.call(forms, function(form) {
            form.addEventListener('submit', function(event) {
                if (form.checkValidity() === false) {
                    event.preventDefault();
                    event.stopPropagation();
                } else {
                    event.preventDefault(); // 阻止默认提交，使用AJAX
                    submitForm();
                }
                form.classList.add('was-validated');
            }, false);
        });
    }, false);
})();

// 提交表单
function submitForm() {
    // 使用普通表单序列化，不使用FormData
    var formData = $('#scheduleForm').serialize();
    console.log('提交表单数据:', formData);

    $.ajax({
        url: '${pageContext.request.contextPath}/schedule',
        type: 'POST',
        data: formData,
        dataType: 'json',
        success: function(response) {
            console.log('服务器响应:', response);
            if (response.success) {
                alert(response.message);
                window.location.href = '${pageContext.request.contextPath}/schedule';
            } else {
                alert('操作失败：' + response.message);
            }
        },
        error: function(xhr, status, error) {
            console.error('AJAX错误:', xhr.status, xhr.responseText, error);
            alert('操作失败，请重试！错误：' + error);
        }
    });
}
</script>

<style>
.custom-control-label {
    cursor: pointer;
}
</style>
</body>
</html>