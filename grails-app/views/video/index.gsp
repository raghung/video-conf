<%@ page import="com.video.VideoController" %>
<g:if test="${function}" value="${VideoController.USER_LIST}">
<g:each var="user" in="${resp.json.users}">
	<div>${user.first_name} ${user.last_name}</div>
	<div>${user.id}</div>
	<div>${user.email}</div>
	<div><hr></div>
</g:each>
</g:if>
<g:if test="${function}" value="${VideoController.MEETING_CREATE}">
	<div><a href="${resp.json.start_url}">Start Meeting</a></div>
	<div>${resp.json.join_url}</div>
	<div>${resp.json.topic}</div>
	<div><hr></div>
</g:if>