package com.video

class MeetingUser {

	static mapWith = "mongo"
	
	String id
	Long userId
	
	String hostId
	String email
	String firstname
	String lastname
	String token
	Boolean disableChat = false
	Boolean e2eEncryption = false
	
	Date createTime 
	
    static constraints = {
		userId blank: false
		hostId blank: false
		createTime blank: false
		token nullable: true
    }
}
