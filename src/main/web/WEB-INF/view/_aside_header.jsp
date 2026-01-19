<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2026/1/5
  Time: 17:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!--左侧导航-->
<aside class="lyear-layout-sidebar">
    <!-- logo -->
    <div id="logo" class="sidebar-header">
        <a href="${pageContext.request.contextPath}/index.jsp"><img src="${pageContext.request.contextPath}/assets/images/logo-sidebar.png" title="LightYear" alt="LightYear" /></a>
    </div>
    <div class="lyear-layout-sidebar-scroll">

        <nav class="sidebar-main">
            <ul class="nav nav-drawer">
                <li class="nav-item active"> <a href="${pageContext.request.contextPath}/"><i class="mdi mdi-home"></i> 后台首页</a> </li>
                <li class="nav-item nav-item-has-subnav open">
                    <a href="javascript:void(0)"><i class="mdi mdi-format-align-justify"></i> 功能</a>
                    <ul class="nav nav-subnav">
                        <%-- 根据用户角色显示不同的功能菜单 --%>
                        <c:choose>
                            <c:when test="${sessionScope.role == 'admin'}">
                                <li> <a href="${pageContext.request.contextPath}/customer">客户信息</a> </li>
                                <li> <a href="${pageContext.request.contextPath}/coach">教练信息</a> </li>
                            </c:when>
                            <c:when test="${sessionScope.role == 'customer'}">
                                <li> <a href="${pageContext.request.contextPath}/appointment/coach">预约教练</a> </li>
                                <li> <a href="${pageContext.request.contextPath}/appointment/course">预约课程</a> </li>
                            </c:when>
                            <c:when test="${sessionScope.role == 'coach'}">
                                <li> <a href="${pageContext.request.contextPath}/appointment/my">我的预约</a> </li>
                            </c:when>
                        </c:choose>
                    </ul>
                </li>
            </ul>
        </nav>

    </div>

</aside>
<!--End 左侧导航-->

<!--头部信息-->
<header class="lyear-layout-header">

    <nav class="navbar navbar-default">
        <div class="topbar">

            <div class="topbar-left">
                <div class="lyear-aside-toggler">
                    <span class="lyear-toggler-bar"></span>
                    <span class="lyear-toggler-bar"></span>
                    <span class="lyear-toggler-bar"></span>
                </div>
                <span class="navbar-page-title"> 后台首页 </span>
            </div>

            <ul class="topbar-right">
                <li class="dropdown dropdown-profile">
                    <a href="javascript:void(0)" data-toggle="dropdown">
                        <img class="img-avatar img-avatar-48 m-r-10" src="${pageContext.request.contextPath}/assets/images/users/avatar.jpg" alt="笔下光年" />
                        <span>笔下光年 <span class="caret"></span></span>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-right">
                        <li> <a href="lyear_pages_profile.html"><i class="mdi mdi-account"></i> 个人信息</a> </li>
                        <li> <a href="lyear_pages_edit_pwd.html"><i class="mdi mdi-lock-outline"></i> 修改密码</a> </li>
                        <li> <a href="javascript:void(0)"><i class="mdi mdi-delete"></i> 清空缓存</a></li>
                        <li class="divider"></li>
                        <li> <a href="${pageContext.request.contextPath}/logout"><i class="mdi mdi-logout-variant"></i> 退出登录</a> </li>
                    </ul>
                </li>

            </ul>

        </div>
    </nav>

</header>
<!--End 头部信息-->