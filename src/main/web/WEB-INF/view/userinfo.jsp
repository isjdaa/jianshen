<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>健身房客户管理系统-个人信息</title>
    <link rel="icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/ico">
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/materialdesignicons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.min.css" rel="stylesheet">
    <script src="${pageContext.request.contextPath}/assets/js/jquery.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/script.min.js"></script>
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
                                <h4>个人信息</h4>
                            </div>
                            <div class="card-body">
                                <!-- 修改表单enctype支持文件上传 -->
                                <form id="userInfoForm" class="form-horizontal" enctype="multipart/form-data">
                                    <!-- 只有非管理员用户才显示头像上传功能 -->
                                    <c:if test="${sessionScope.role != 'admin'}">
                                        <div class="form-group">
                                            <label class="col-xs-12" for="avatar">头像</label>
                                            <div class="col-xs-12 col-sm-6">
                                                <div class="avatar-upload">
                                                    <div class="avatar-edit">
                                                        <input type='file' id="avatarInput" name="avatarFile" accept=".png,.jpg,.jpeg" />
                                                        <label for="avatarInput"><i class="mdi mdi-pencil"></i></label>
                                                    </div>
                                                    <div class="avatar-preview">
                                                        <c:set var="avatarPreviewUrl" value="${pageContext.request.contextPath}/assets/images/users/avatar.jpg" />
                                                        <c:if test="${sessionScope.user.avatar != null && !sessionScope.user.avatar.isEmpty()}">
                                                            <c:set var="avatarPreviewUrl" value="${sessionScope.user.avatar}" />
                                                        </c:if>
                                                        <div id="avatarPreview" style="background-image: url('${avatarPreviewUrl}');"></div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-xs-12" for="name">姓名</label>
                                            <div class="col-xs-12 col-sm-6">
                                                <input type="text" id="name" name="name" class="form-control"
                                                       value="${sessionScope.user.name}" placeholder="请输入姓名">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-xs-12" for="tele">电话</label>
                                            <div class="col-xs-12 col-sm-6">
                                                <input type="text" id="tele" name="tele" class="form-control"
                                                       value="${sessionScope.user.tele}" placeholder="请输入电话">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-xs-12" for="gender">性别</label>
                                            <div class="col-xs-12 col-sm-6">
                                                <select id="gender" name="gender" class="form-control">
                                                    <option value="男" ${sessionScope.user.gender == '男' ? 'selected' : ''}>男</option>
                                                    <option value="女" ${sessionScope.user.gender == '女' ? 'selected' : ''}>女</option>
                                                </select>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-xs-12" for="address">详细地址</label>
                                            <div class="col-xs-12 col-sm-6">
                                                <textarea id="address" name="address" class="form-control" rows="3"
                                                          placeholder="请输入详细地址">${sessionScope.user.address}</textarea>
                                            </div>
                                        </div>

                                        <!-- 教练特有字段 -->
                                        <c:if test="${sessionScope.role == 'coach'}">
                                            <div class="form-group">
                                                <label class="col-xs-12" for="specialization">专业特长</label>
                                                <div class="col-xs-12 col-sm-6">
                                                    <input type="text" id="specialization" name="specialization" class="form-control"
                                                           value="${sessionScope.user.specialization}" placeholder="请输入专业特长">
                                                </div>
                                            </div>
                                        </c:if>
                                    </c:if>
                                    
                                    <!-- 管理员用户只显示基本信息 -->
                                    <c:if test="${sessionScope.role == 'admin'}">
                                        <div class="form-group">
                                            <div class="col-xs-12 col-sm-6">
                                                <div class="alert alert-info">
                                                    管理员账号不支持修改个人信息
                                                </div>
                                            </div>
                                        </div>
                                    </c:if>

                                    <!-- 只有非管理员用户才显示保存按钮 -->
                                    <c:if test="${sessionScope.role != 'admin'}">
                                        <div class="form-group m-b-0">
                                            <div class="col-xs-12 col-sm-6">
                                                <button type="submit" class="btn btn-primary">保存修改</button>
                                                <button type="button" class="btn btn-default" onclick="window.history.back()">取消</button>
                                            </div>
                                        </div>
                                    </c:if>
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

<style>
    .avatar-upload {
        position: relative;
        max-width: 205px;
        margin: 20px 0;
    }
    .avatar-upload .avatar-edit {
        position: absolute;
        right: 12px;
        z-index: 1;
        top: 10px;
    }
    .avatar-upload .avatar-edit input {
        display: none;
    }
    .avatar-upload .avatar-edit input + label {
        display: inline-block;
        width: 34px;
        height: 34px;
        margin-bottom: 0;
        border-radius: 100%;
        background: #FFFFFF;
        border: 1px solid transparent;
        box-shadow: 0px 2px 4px 0px rgba(0, 0, 0, 0.12);
        cursor: pointer;
        font-weight: normal;
        transition: all 0.2s ease-in-out;
    }
    .avatar-upload .avatar-edit input + label:hover {
        background: #f1f1f1;
        border-color: #d6d6d6;
    }
    .avatar-upload .avatar-edit input + label i {
        color: #757575;
        position: absolute;
        top: 10px;
        left: 0;
        right: 0;
        text-align: center;
        margin: auto;
    }
    .avatar-upload .avatar-preview {
        width: 192px;
        height: 192px;
        position: relative;
        border-radius: 100%;
        border: 6px solid #F8F8F8;
        box-shadow: 0px 2px 4px 0px rgba(0, 0, 0, 0.1);
    }
    .avatar-upload .avatar-preview > div {
        width: 100%;
        height: 100%;
        border-radius: 100%;
        background-size: cover;
        background-repeat: no-repeat;
        background-position: center;
    }
</style>

<script>
    // 头像预览功能
    $(document).ready(function() {
        // 头像预览
        function readURL(input) {
            if (input.files && input.files[0]) {
                // 检查文件大小，限制为2MB
                var fileSize = input.files[0].size / 1024 / 1024;
                if (fileSize > 2) {
                    alert('头像大小不能超过2MB，请选择更小的图片');
                    input.value = ''; // 清空选择的文件
                    return;
                }

                // 检查图片尺寸，限制为1000x1000像素
                var img = new Image();
                img.onload = function() {
                    if (img.width > 1000 || img.height > 1000) {
                        alert('头像尺寸不能超过1000x1000像素，请选择更小的图片');
                        input.value = ''; // 清空选择的文件
                        return;
                    }

                    // 图片符合要求，读取并显示预览
                    var reader = new FileReader();
                    reader.onload = function(e) {
                        $('#avatarPreview').css('background-image', 'url(' + e.target.result + ')');
                    }
                    reader.readAsDataURL(input.files[0]);
                };
                img.src = URL.createObjectURL(input.files[0]);
            }
        }

        $('#avatarInput').change(function() {
            readURL(this);
        });

        // 表单提交（支持文件上传）
        $('#userInfoForm').submit(function(e) {
            e.preventDefault();

            // 使用FormData提交文件和表单数据
            var formData = new FormData(this);

            $.ajax({
                url: '${pageContext.request.contextPath}/userinfo',
                type: 'POST',
                data: formData,
                processData: false, // 不处理数据
                contentType: false, // 不设置内容类型
                dataType: 'json',
                success: function(result) {
                    if (result.success) {
                        alert('个人信息更新成功');
                        window.location.reload();
                    } else {
                        alert('更新失败：' + result.msg);
                    }
                },
                error: function() {
                    alert('更新失败，请稍后重试');
                }
            });
        });
    });
</script>
</body>
</html>