package com.video

import org.codehaus.groovy.grails.web.json.JSONObject;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import grails.plugins.rest.client.RequestCustomizer;
import grails.plugins.rest.client.RestBuilder;
import grails.plugins.rest.client.RestResponse;

class VideoController {
	private String apiKey = "gpUQEZplwaFl3y2E32UPsiIs60xdwqbx7zZg"
	private String apiSecret = "kB5jiWoR70aWK2tUAu5SlJ1kukxUO12MYcj8"
	private String apiUrl = "https://api.zoom.us/v1"
	private String userId = "Ysc4QA0sQvSckpxu54WYiw"
	
	static final String USER_LIST = "/user/list"
	static final String MEETING_CREATE = "/meeting/create"
	
	static final String MEETING_TYPE_INSTANT = "1"
	static final String MEETING_TYPE_SCHEDULED = "2"
	static final String MEETING_TYPE_RECURRING = "3"
	
	static final String MEETING_START_VIDEO = "video"
	static final String MEETING_START_SCREENSHARE = "screen_share"

	def index() {
		def function = MEETING_CREATE
		def resp = callAPI(function)

		if (resp.json instanceof JSONObject)
			println resp.json
		else
			println resp
		
		render view: 'index', model:[resp: resp, function: function]
	}
	
	private RestResponse callAPI(String function) {
		RestBuilder rest = new RestBuilder()
		MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>()
		form.add("api_key", apiKey)
		form.add("api_secret", apiSecret)
		form.add("data_type", "JSON")
		
		if (function == USER_LIST) {
			form.add("page_size", "30")
			form.add("page_number", "1")

		} else if (function == MEETING_CREATE) {
			form.add("host_id", userId)
			form.add("topic", "testing")
			form.add("type", MEETING_TYPE_INSTANT)
			form.add("option_start_type", MEETING_START_VIDEO)
		}
		
		def resp = rest.post(apiUrl + function) {
			accept("application/json")
			contentType("application/x-www-form-urlencoded")
			body(form)
		}
		
		return resp
	}
}
