package com.video

class MeetingInvitees {
	
	static mapWith = "mongo"
	
	String id

	Long userId
	
	String meetingId
	Date startTime
	Date endTime
	Boolean isActive
	
	String joinUrl
	
	Date createTime
	Long createUser
	
    static constraints = {
		userId blank: false
		startTime blank: false
		joinUrl blank: false
		createTime blank: false
		createUser blank: false
		meetingId blank: false
		isActive blank: false
		
		endTime nullable: true
    }
}
