import com.video.Role;
import com.video.User;
import com.video.UserRole;

class BootStrap {

    def init = { servletContext ->
		def doctorRole = Role.findByAuthority("ROLE_DOCTOR")?: new Role(authority: 'ROLE_DOCTOR').save(flush: true)
		def staffRole = Role.findByAuthority("ROLE_STAFF")?: new Role(authority: 'ROLE_STAFF').save(flush: true)
		def patientRole = Role.findByAuthority("ROLE_PATIENT")?: new Role(authority: 'ROLE_PATIENT').save(flush: true)
  
		def testUser1 = new User(username: 'raghunandan_gr@yahoo.com', password: 'raghu@yahoo').save(flush: true)
		def testUser2 = new User(username: 'yamini@onehaystack.com', password: 'yamini').save(flush: true)
		def testUser3 = new User(username: 'raghu@onehaystack.com', password: 'raghu').save(flush: true)
  
		UserRole.create testUser1, doctorRole, true
		UserRole.create testUser2, staffRole, true
		UserRole.create testUser3, patientRole, true
  
		assert User.count() == 3
		assert Role.count() == 3
		assert UserRole.count() == 3
    }
    def destroy = {
    }
}
