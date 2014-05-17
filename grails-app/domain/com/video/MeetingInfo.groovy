package com.video

class MeetingInfo {

	static mapWith = "mongo"
	
	String id
	
	Long userId
	
	String hostId
	String startUrl
	String joinUrl
	String topic
	Date startTime
	Date endTime
	Boolean isActive
		
	Date createTime
	Long createUser
	
    static constraints = {
		userId blank: false
		hostId blank: false
		startUrl blank: false
		joinUrl blank: false
		topic blank: false
		createTime blank: false
		createUser blank: false
		isActive blank: false
		
		startTime nullable: true
		endTime nullable: true
    }
}
