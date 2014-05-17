package com.video

import java.text.SimpleDateFormat;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.DateTime;
import org.codehaus.groovy.grails.web.json.JSONObject;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import grails.plugin.springsecurity.annotation.Secured;
import grails.plugins.rest.client.RequestCustomizer;
import grails.plugins.rest.client.RestBuilder;
import grails.plugins.rest.client.RestResponse;

@Secured(["ROLE_DOCTOR", "ROLE_STAFF", "ROLE_PATIENT"])
class VideoController {
	
	def springSecurityService
	
	private String apiKey = "gpUQEZplwaFl3y2E32UPsiIs60xdwqbx7zZg"
	private String apiSecret = "kB5jiWoR70aWK2tUAu5SlJ1kukxUO12MYcj8"
	private String apiUrl = "https://api.zoom.us/v1"
	private String userId = "Ysc4QA0sQvSckpxu54WYiw"

	static final String USER_LIST = "/user/list"
	static final String USER_INFO = "/user/get"
	static final String USER_CREATE = "/user/custcreate"
	static final String MEETING_CREATE = "/meeting/create"

	static final String MEETING_TYPE_INSTANT = "1"
	static final String MEETING_TYPE_SCHEDULED = "2"
	static final String MEETING_TYPE_RECURRING = "3"

	static final String MEETING_START_VIDEO = "video"
	static final String MEETING_START_SCREENSHARE = "screen_share"
	
	DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ")

	def index() {
		def currentUser = springSecurityService.currentUser
		def function = USER_LIST
		def resp = callAPI(function, null)
		
		// Update MeetingUser table
		for(user in resp?.json.users) {
			def meetingUser = MeetingUser.findByEmail(user.email)
			if (meetingUser) 
				continue
				
			DateTime time = dateStringFormat.parseDateTime(user?.created_at)
			def u = User.findByUsername(user.email)
				
			meetingUser = new MeetingUser(userId: u.id, hostId: user.id, email: user.email, 
			  			    firstname: user.first_name, lastname: user.last_name,
							token: user.token, disableChat: user.disable_chat, 
							e2eEncryption: user.enable_e2e_encryption,
							createTime: time.toDate()).save(flush:true, failOnError:true)
			println user?.email
		}
		
		render view: 'index', model:[resp: resp, function: function, users: User.findAllByIdNotEqual(currentUser.id)]
	}

	def startMeeting() {
		def currentUser = springSecurityService.currentUser
		def email = currentUser.username
		
		// Stop all the existing meetings hosted by user
		def query = MeetingInfo.where {
			userId == currentUser.id && isActive == true
		}
		def prevMeeting = query.list()
		for (meeting in prevMeeting) {
			meeting.isActive = false
			meeting.save(flush: true, failOnError: true)
			
			def invitees = MeetingInvitees.findAllByMeetingId(meeting.id)
			for (invitee in invitees) {
				invitee.isActive = false
				invitee.save(flush: true, failOnError: true)
			}
		}
		
		
		def function = MEETING_CREATE
		def resp = callAPI(function, email)
		
		// Populate MeetingInfo table
		def jsonObj = resp.json
		if (jsonObj) {
			def createTime = new DateTime().toDate()
			
			def meeting = new MeetingInfo(userId: currentUser.id, hostId: jsonObj.host_id, startUrl: jsonObj.start_url, 
							joinUrl: jsonObj.join_url, topic: jsonObj.topic,
							startTime: createTime, isActive: true, 
							createTime: createTime, createUser: currentUser.id).save(flush: true, failOnError: true)
			
			def invitees = params.jsonArr.split(",")
			for (invitee in invitees) {
				def obj = new MeetingInvitees(userId: invitee, joinUrl: meeting.joinUrl, startTime: meeting.startTime,
								    endTime: meeting.endTime, meetingId: meeting.id, isActive: true,
									createTime: createTime, createUser: currentUser.id).save(flush: true, failOnError: true)
			}
			def inv = MeetingInvitees.findAll()
			def meetingInfo = MeetingInfo.findByJoinUrl(jsonObj.join_url)
			println meetingInfo?.joinUrl
		}
		
		render resp.json.start_url
	}
	
	def joinMeeting() {
		def currentUser = springSecurityService.currentUser

		def query = MeetingInvitees.where {
			userId == currentUser.id && isActive == true 
		}
		def meetingInfo = query.find()
		
		render meetingInfo.joinUrl
	}
	
	def createUser() {
		def currentUser = springSecurityService.currentUser
		def email = params.email
		def function = USER_CREATE
		def resp = callAPI(function)
		
		// Populate MeetingUser table
		
		render view: 'index', model:[resp: resp, function: function]
	}
	
	def getUserInfo() {
		def email = params.username
		def function = USER_INFO
		def resp = callAPI(function, email)
		
		render view: 'index', model:[resp: resp, function: function]
	}
	
	private RestResponse callAPI(String function, String email) {
		RestBuilder rest = new RestBuilder()
		MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>()
		form.add("api_key", apiKey)
		form.add("api_secret", apiSecret)
		form.add("data_type", "JSON")

		def user
		if (email) {
			user = MeetingUser.findByEmail(email)
		}
		
		if (function == USER_LIST) {
			form.add("page_size", "30")
			form.add("page_number", "1")
		} else if (function == MEETING_CREATE) {
			form.add("host_id", user.hostId)
			form.add("topic", "testing")
			form.add("type", MEETING_TYPE_INSTANT)
			form.add("option_start_type", MEETING_START_VIDEO)
		} else if (function == USER_INFO) {
			form.add("id", user.hostId)
		}

		def resp = rest.post(apiUrl + function) {
			accept("application/json")
			contentType("application/x-www-form-urlencoded")
			body(form)
		}

		if (resp.json instanceof JSONObject)
			println resp.json
		else
			println resp

		return resp
	}
}
