<%@ page import="com.video.VideoController" %>
<html>
<head>
	<meta name="layout" content="main"/>
</head>
<body>
	<g:form name="frm-video" controller="video">
	
	<g:if test="${function == VideoController.USER_LIST}">
	<div>
	<b>Available Hosts:</b><br>
	<g:each var="user" in="${resp.json.users}">
		<div>${user.first_name} ${user.last_name}(${user.email})</div>
	</g:each>
	</div>
	</g:if>
	<hr>
	<br>
	<sec:ifAnyGranted roles="ROLE_DOCTOR">
		<b>Invite Users:</b><br>
		<g:each var="user" in="${users}">
			<g:checkBox name="invitees" value="${user.id}" checked="false"/> ${user.username}<br>
		</g:each>
		<input type="button" value="Start Meeting" 
		onclick="initiateMeeting()">
    </sec:ifAnyGranted>
    <sec:ifAnyGranted roles="ROLE_STAFF,ROLE_PATIENT">
    	<br>
    	<input type="button" value="Join Meeting" 
		onclick="joinMeeting()">
	</sec:ifAnyGranted>
	</g:form>
	<g:javascript>
		function startMeeting(data) {
			if (data == null || data == "") {
				alert("No meeting is scheduled");
			} else {
				popupWin = window.open(data, 'Meeting');
				if (popupWin == null)
    				alert("Turn off your pop-up blocker!");
    		}

			//location.href = data;
		}
		function joinMeeting() {
			jQuery.ajax({
					type:'POST',
					url: "${createLink(controller: 'video', action: 'joinMeeting') }",
					success:function(data,textStatus){startMeeting(data);},
					error:function(XMLHttpRequest,textStatus,errorThrown){alert("Failure!");}
				});
		}
		function initiateMeeting() {
		
			var arrInvitee = document.getElementsByName("invitees");
			var jsonArr = new Array();
			var j = 0;
			for(i=0; i < arrInvitee.length; i++) {
				if (arrInvitee[i].checked)
					jsonArr[j++] = arrInvitee[i].value;
			}
			if (jsonArr.length == 0) {
				alert("Please select the invitee");
			} else {
			
				jQuery.ajax({
					type:'POST',
					data:'jsonArr=' + jsonArr, 
					url: "${createLink(controller: 'video', action: 'startMeeting') }",
					success:function(data,textStatus){startMeeting(data);},
					error:function(XMLHttpRequest,textStatus,errorThrown){alert("Failure!");}
				});
			}
			
		}
	</g:javascript>
</body>
</html>