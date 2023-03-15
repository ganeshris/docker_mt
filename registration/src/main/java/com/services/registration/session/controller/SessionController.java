package com.services.registration.session.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.services.registration.config.TokenProvider;
import com.services.registration.response.OperationResponse;
import com.services.registration.session.entity.AboutWork;
import com.services.registration.session.entity.SessionItem;
import com.services.registration.session.response.SessionResponse;
import com.services.registration.users.entity.CompanyDto;
import com.services.registration.users.entity.Email;
import com.services.registration.users.entity.EmailRequest;
import com.services.registration.users.entity.LoginUser;
import com.services.registration.users.entity.Sys_Accounts;
import com.services.registration.users.entity.User;
import com.services.registration.users.entity.UserDto;
import com.services.registration.users.repository.AboutWorkRepo;
import com.services.registration.users.repository.CompanyRepo;
import com.services.registration.users.repository.UserRepo;
import com.services.registration.users.response.UserItem;
import com.services.registration.users.response.UserResponse;
import com.services.registration.users.service.AboutWorkService;
import com.services.registration.users.service.CompanyService;
import com.services.registration.users.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/*
This is a dummy rest controller, for the purpose of documentation (/session) path is map to a filter
 - This will only be invoked if security is disabled
 - If Security is enabled then SessionFilter.java is invoked
 - Enabling and Disabling Security is done at config/applicaton.properties 'security.ignored=/**'
*/
@Slf4j
@Api(tags = { "Authentication" })
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping(value = "/token", produces = MediaType.APPLICATION_JSON_VALUE)
public class SessionController {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private AboutWorkRepo aboutWorkRepo;

	private final AuthenticationManager authenticationManager;

	private final TokenProvider jwtTokenUtil;

	private final UserService userService;

	private final CompanyService companyService;

//    private final EmailService emailService;
//
//    private final JavaMailSender mailSender;

	private final AboutWorkService aboutworkservice;

	private final CompanyRepo sysrepo;

	private final BCryptPasswordEncoder bcryptEncoder;

	public SessionController(AuthenticationManager authenticationManager, TokenProvider jwtTokenUtil,
			UserService userService, CompanyService companyService, AboutWorkService aboutworkservice,
			CompanyRepo sysrepo, BCryptPasswordEncoder bcryptEncoder) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenUtil = jwtTokenUtil;
		this.userService = userService;
		this.companyService = companyService;
//        this.emailService = emailService;
//        this.mailSender = mailSender;
		this.aboutworkservice = aboutworkservice;
		this.sysrepo = sysrepo;
		this.bcryptEncoder = bcryptEncoder;
	}

	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Will return a security token, which must be passed in every request", response = SessionResponse.class) })
	@RequestMapping(value = "/session", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public SessionResponse newSession(@RequestBody LoginUser loginRequest) {

		try {

			final Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);

			final String token = jwtTokenUtil.generateToken(authentication);

			System.out.println("authentication.getName() =>" + authentication.getName()); // email

			User loggedInUser = userService.getLoggedInUser();
			// System.out.println("/session logged in user -> " + loggedInUser);

//			List<String> loggedInUserRoles = new ArrayList<String>();
			StringBuilder roleString = new StringBuilder();
			loggedInUser.getRoles().forEach(role -> {
//				loggedInUserRoles.add(role.getName());
				roleString.append(role.getName() + ", ");
			});
			String role = roleString.toString().substring(0, roleString.toString().lastIndexOf(","));
			List<String> roleList = Arrays.asList(role.split("\\s*,\\s*"));

			SessionResponse resp = new SessionResponse();
			SessionItem sessionItem = new SessionItem();
			sessionItem.setToken(token);
			sessionItem.setUserId(loggedInUser.getUserId());
			sessionItem.setFullname(loggedInUser.getFullName());
			sessionItem.setFirstName(loggedInUser.getFirstName());
			// sessionItem.setUsername(loggedInUser.getUsername());
			sessionItem.setEmail(loggedInUser.getEmail());
			// sessionItem.setRoles(roleList);
			sessionItem.setRoles(role);
			resp.setOperationStatus(OperationResponse.ResponseStatusEnum.SUCCESS);
			resp.setOperationMessage("Login Success");
			resp.setItem(sessionItem);
			return resp;
		} catch (Exception e) {
			System.out.print(e.getMessage());
			SessionResponse resp = new SessionResponse();
			resp.setOperationStatus(OperationResponse.ResponseStatusEnum.ERROR);
			resp.setOperationMessage("Login Failed");
			return resp;
		}

	}

	// admin
	@ApiOperation(value = "Add new user (admin)", response = OperationResponse.class)
	@RequestMapping(value = "/user-registration", method = RequestMethod.POST, produces = { "application/json" })
	public UserResponse addNewUser(@RequestBody UserDto user, HttpServletRequest req) {
		System.out.println("----------This is my comment---------");
		User userAddSuccess = userService.userResister(user);
		System.out.println("----------This is my comment---------");
		UserResponse resp = new UserResponse();
		UserItem userItem = new UserItem();
		if (userAddSuccess != null) {
			userItem.setUserId(userAddSuccess.getUserId());
			userItem.setFirstName(userAddSuccess.getFirstName());
			userItem.setFullname(userAddSuccess.getFullName());
			userItem.setEmail(userAddSuccess.getEmail());
			resp.setOperationStatus(OperationResponse.ResponseStatusEnum.SUCCESS);
			resp.setOperationMessage("User Added");
			resp.setItem(userItem);
			return resp;
		} else {
			resp.setOperationStatus(OperationResponse.ResponseStatusEnum.ERROR);
			resp.setOperationMessage("Unable to add user");
			return resp;
		}
	}

	@ApiOperation(value = "Add new Company", response = OperationResponse.class)
	@RequestMapping(value = "/company-registration", method = RequestMethod.POST, produces = { "application/json" })
	public ResponseEntity<?> addNewCompany(@RequestBody CompanyDto company) {
		Sys_Accounts companyAddSuccess = companyService.companyResister(company);

		Map<String, String> res = new HashMap<String, String>();
		if (companyAddSuccess != null) {
			String message = "Company Added";
			res.put("message", message);
			return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
		} else {
			String message = "Unable to add Company";
			res.put("message", message);
			// return ResponseEntity.ok(res);
			return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
		}

	}

	@ApiOperation(value = "Add new cluodnsure", response = OperationResponse.class)
	@PostMapping("/aboutwork")
	public User addNewCustomer(@RequestBody AboutWork aboutWork) {

		System.out.println("about work controller started");

		// save acccount info
		AboutWork about = aboutworkservice.adddata(aboutWork);
		Sys_Accounts sys = new Sys_Accounts();
		sys.setId(about.getId());
		sysrepo.save(sys);

		// save user with accout id
		User user = new User();

		user.setPassword(aboutWork.getPassword());
		user.setEmail(aboutWork.getEmail());
		user.setPhone(aboutWork.getMobile());
		User userResister = userService.userResister(user, about.getId());
		return userResister;
	}

	@ApiOperation(value = "Update about ", response = User.class)
	@PutMapping("/aboutwork/{id}")
	public User updateUser(@PathVariable(value = "id") Long id, @Valid @RequestBody AboutWork aboutWork) {
		User updateabout = userService.updateById(id, aboutWork);
		System.out.println("account id:" + updateabout.getSys_account().getId() + "Pancard NO:" + aboutWork.getPancard()
				+ "Passwored::" + aboutWork.getPassword());
		AboutWork aw = aboutworkservice.updateById(updateabout.getSys_account().getId(), aboutWork);
		return updateabout;
	}

	@ApiOperation(value = "Update about ", response = User.class)
	@PutMapping("/aboutwork2/{id}")
	public User updateUser2(@PathVariable(value = "id") Long id, @Valid @RequestBody AboutWork aboutWork) {

		User updateabout = userService.updateById2(id, aboutWork);
		System.out.println("account id:" + updateabout.getSys_account().getId() + "Pancard NO:" + aboutWork.getPancard()
				+ "Passwored::" + aboutWork.getPassword());
		AboutWork aw = aboutworkservice.updateById2(updateabout.getSys_account().getId(), aboutWork);
		return updateabout;
	}

	@ApiOperation(value = "Update about ", response = User.class)
	@PutMapping("/aboutwork_working/{id}")
	public ResponseEntity<?> updateByIdWorkingId(@PathVariable(value = "id") Long id,
			@Valid @RequestBody AboutWork aboutWork) {
		System.out.println("about work controller started");

		User updateabout = userService.updateByIdWorkingId(id, aboutWork);

		System.out.println(updateabout.getSys_account().getId());

		return ResponseEntity.status(HttpStatus.ACCEPTED).body(updateabout);
	}

	@ApiOperation(value = "Update about ", response = User.class)
	@PutMapping("/aboutwork_managing/{id}")
	public ResponseEntity<?> updateByMangingWork(@PathVariable(value = "id") Long id,
			@Valid @RequestBody AboutWork aboutWork) {
		System.out.println("about work controller started");

		User updateabout = userService.updateByMangingWork(id, aboutWork);
		AboutWork aw = aboutworkservice.updateById3(updateabout.getSys_account().getId(), aboutWork);
		return null;
	}

	@ApiOperation(value = "Add new cluodnsure", response = OperationResponse.class)
	@PostMapping("/addemails/{id}")
	public User addNewEmails(@PathVariable(value = "id") Long id, @RequestBody Email email) {

		System.out.println(id + "about work controller started");

		User u = userService.getUserInfoByUserId(id);
//		 System.out.println(u.getSys_account().getId());

		Sys_Accounts acc_id = u.getSys_account();
		long account = acc_id.getId();
		System.out.println(account);

		Sys_Accounts sys = new Sys_Accounts();
		sys.setId(account);

		User user = new User();

		if (email.getEmail1() != null) {

			Random random = new Random();
//		long random1 = (long)user.getChecknumber();
			System.out.println("Random Long: " + random.nextLong());
			long ra = random.nextLong();
//		System.out.println("Ra: " + ra);

			user.setEmail(email.getEmail1());
			user.setPassword("pfp");
			user.setSys_account(sys);
			user.setChecknumber(ra);
			user.setRole("USER");
//			user.setUserId(id);

			User userid = userService.userResisteremail(user);

			userService.sendEmail2(email.getEmail1(), userid.getUserId(), ra);

//		userService.sendEmail(email.getEmail1());

		}

		User user2 = new User();
		if (email.getEmail2() != null) {

			Random random = new Random();
//		long random1 = (long)user.getChecknumber();
			System.out.println("Random Long: " + random.nextLong());
			long ra = random.nextLong();
//		System.out.println("Ra: " + ra);

			user2.setEmail(email.getEmail2());
			user2.setPassword("kkl");
			user2.setSys_account(sys);
			user2.setChecknumber(ra);
			user2.setRole("USER");
//			user2.setUserId(id);

			User userid2 = userService.userResisteremail(user2);
//			userService.sendEmail(email.getEmail2());

			userService.sendEmail2(email.getEmail2(), userid2.getUserId(), ra);
		}

		User user3 = new User();
		if (email.getEmail3() != null) {

			Random random = new Random();
//			long random1 = (long)user.getChecknumber();
			System.out.println("Random Long: " + random.nextLong());
			long ra = random.nextLong();
//			System.out.println("Ra: " + ra);

			user3.setEmail(email.getEmail3());
			user3.setPassword("skl");
			user3.setSys_account(sys);
			user3.setChecknumber(ra);
			user3.setRole("USER");
//			user3.setUserId(id);

			User user4 = userService.userResisteremail(user3);
//			userService.sendEmail(email.getEmail3() );

			userService.sendEmail2(email.getEmail3(), user4.getUserId(), ra);
		}

		User user4 = new User();
		if (email.getEmail4() != null) {

			Random random = new Random();
//			long random1 = (long)user.getChecknumber();
			System.out.println("Random Long: " + random.nextLong());
			long ra = random.nextLong();
//			System.out.println("Ra: " + ra);

			user4.setEmail(email.getEmail4());
			user4.setPassword("ppl");
			user4.setSys_account(sys);
			user4.setChecknumber(ra);
			user4.setRole("USER");
//			user4.setUserId(id);

			User user5 = userService.userResisteremail(user4);
//			userService.sendEmail(email.getEmail4());

			userService.sendEmail2(email.getEmail4(), user5.getUserId(), ra);

		}
		System.out.println("Last methoddd");
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getForEntity("http://localhost:8085/scheduleData", void.class);
		return null;

	}

	@GetMapping("userid/{id}/{checknumber}")
	public User addRole(@PathVariable("id") Long id, @PathVariable("checknumber") Long checknumber) {
		User user = userService.exitbychecknumber(id, checknumber);
		return user;
	}

	// changed methods
	@ApiResponses(value = { @ApiResponse(code = 200, message = "email varification") })
	@RequestMapping(value = "/create-user", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createNewUser(@RequestBody EmailRequest emailReq) {
		boolean exists = userService.existsByEmail(emailReq.getEmail());
		System.out.println(emailReq.getEmail() + " ::: EMAIL exists? " + exists);
		Map<String, String> res = new HashMap<String, String>();
		if (exists) {
			String message = emailReq.getEmail() + " is Already Exists";
			res.put("message", message);
			// return new ResponseEntity<Error>(HttpStatus.BAD_REQUEST);
			return new ResponseEntity<>(res, HttpStatus.CONFLICT);
		} else {
			AboutWork aboutWork = new AboutWork();
			AboutWork about = aboutworkservice.adddata(aboutWork);
			Sys_Accounts sys = new Sys_Accounts();
			sys.setId(about.getId());
			sysrepo.save(sys);

			User user = new User();
			Random random = new Random();
			long no = random.nextLong();
			user.setChecknumber(no);
			user.setEmail(emailReq.getEmail());
			user.setPassword(bcryptEncoder.encode("demopass"));
			user.setRole("ADMIN");
			user.setSys_account(sys);
			User u = userService.save(user);
//            userService.sendEmail(emailReq.getEmail(), u.getUserId(), u.getChecknumber());

			String message = "Congratulations " + emailReq.getEmail();
			res.put("message", message);
			// return new ResponseEntity<SUCCESSFUL>(HttpStatus.OK);
			return ResponseEntity.ok(res);
		}
	}

	@ApiResponses(value = { @ApiResponse(code = 200, message = "email varification") })
	@RequestMapping(value = "/get-info", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getUserIdAndCt(@RequestBody EmailRequest emailReq) {
		User user = userService.getByEmail(emailReq.getEmail());
		HashMap<String, String> res = new HashMap<>();
		res.put("id", user.getUserId().toString());
		res.put("checkNumber", user.getChecknumber().toString());
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@RequestMapping(value = "/email-exists2", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> emailcheck2(@RequestBody EmailRequest emailReq) {
		boolean exists = userService.existsByEmail(emailReq.getEmail());

		System.out.println(emailReq.getEmail() + " ::: EMAIL exists? " + exists);
		Map<String, String> res = new HashMap<String, String>();
		if (exists) {
//            String message = emailReq.getEmail() + " is Already Exists";
//            res.put("message", message);
			// return new ResponseEntity<Error>(HttpStatus.BAD_REQUEST);
			User u = userService.getByEmail(emailReq.getEmail());
			AboutWork aboutWork = new AboutWork();
			AboutWork about = aboutworkservice.adddata(aboutWork);
			Sys_Accounts sys = new Sys_Accounts();
			sys.setId(about.getId());
			sysrepo.save(sys);
			Random random = new Random();
			long no = random.nextLong();
			u.setChecknumber(no);
			u.setEmail(emailReq.getEmail());
			u.setPassword(bcryptEncoder.encode("demopass"));
			u.setRole("ADMIN");
			u.setSys_account(sys);
			User u1 = userService.save(u);

			String message = "Congratulations " + emailReq.getEmail();
			res.put("message", message);
			// return new ResponseEntity<SUCCESSFUL>(HttpStatus.OK);
			return ResponseEntity.ok(res);
		} else {
			AboutWork aboutWork = new AboutWork();
			AboutWork about = aboutworkservice.adddata(aboutWork);
			Sys_Accounts sys = new Sys_Accounts();
			sys.setId(about.getId());
			sysrepo.save(sys);
			User user = new User();
			Random random = new Random();
			long no = random.nextLong();
			user.setChecknumber(no);
			user.setEmail(emailReq.getEmail());
			user.setPassword(bcryptEncoder.encode("demopass"));
			user.setRole("ADMIN");
			user.setSys_account(sys);
			User u = userService.save(user);

			String message = "Congratulations " + emailReq.getEmail();
			res.put("message", message);
			// return new ResponseEntity<SUCCESSFUL>(HttpStatus.OK);
			return ResponseEntity.ok(res);
		}
	}

	@RequestMapping(value = "/check-email", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> emailcheckonly(@RequestBody EmailRequest emailReq) {
		System.out.println(emailReq.getEmail());
		boolean exists = userService.existsByEmail(emailReq.getEmail());

		System.out.println(emailReq.getEmail() + " :: EMAIL exists? " + exists);
		Map<String, String> res = new HashMap<String, String>();
		if (exists) {
			String message = emailReq.getEmail() + " is Already Exists";
			return new ResponseEntity<>(message, HttpStatus.CONFLICT);
		}
		res.put("res", emailReq.getEmail());
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@RequestMapping(value = "/send-email", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> sendMail(@RequestBody EmailRequest emailReq) {
		HashMap<String, String> res = new HashMap<>();
		User u = userService.getByEmail(emailReq.getEmail());
		if (u == null) {
			return ResponseEntity.status(404).body("No user with this email found");
		}
		userService.sendEmail(emailReq.getEmail(), u.getUserId(), u.getChecknumber());

		String message = "Congratulations " + emailReq.getEmail();
		res.put("email", emailReq.getEmail());
		// return new ResponseEntity<SUCCESSFUL>(HttpStatus.OK);
		return ResponseEntity.ok(res);
	}

	@PostMapping("/getUser")
	public ResponseEntity<?> getUser(@RequestBody EmailRequest emailReq) {
		User u = userService.getByEmail(emailReq.getEmail());
		if (u != null) {
			return new ResponseEntity<>(u, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	//GET ALL USER
	@GetMapping("/getall")
	public ResponseEntity<?> getall() {
		List<User> u = userService.getLastUser();
		return new ResponseEntity<>(u, HttpStatus.OK);
	}

	@DeleteMapping("/deleteAllUser")
	public void deleteAll() {
		userService.deleteAll();
	}

//   DELETE REGISTRATION BY ID 
	@DeleteMapping("/deletereg/{id}")
	public void deletereg(@PathVariable Long id) {
		User a = userRepo.findById(id).orElseThrow(null);

		userRepo.delete(a);
	}

//  GET REGISTRATION by topuser
	@GetMapping("/gettopuser")
	public ResponseEntity<?> GET() {
		User a = userRepo.findTopByOrderByUserIdAsc();

		return new ResponseEntity<>(a, HttpStatus.OK);

	}

	// DELETE TOP USER
	@DeleteMapping("/deletetopuser")
	public void deletetopuser() {
		User a = userRepo.findTopByOrderByUserIdAsc();

		userRepo.delete(a);
	}
	
	// DELETE BY ID
		@DeleteMapping("/deletebyid/{userId}")
		public void deletebyid(@PathVariable Long userId ) {
			Optional<User> a = userRepo.findById(userId);

			userRepo.delete(a.get());
			
		}

//    SAVE ACCOUNT
	@PostMapping("/saveaccount")
	public ResponseEntity<?> save(@RequestBody AboutWork aboutWork) {

		AboutWork a = aboutWorkRepo.save(aboutWork);

		return new ResponseEntity<>(a, HttpStatus.OK);
	}

//    GET ALL ACCOUNT
	@GetMapping("/getallaccount")
	public ResponseEntity<?> getaccount() {
		List<AboutWork> u = aboutWorkRepo.findAll();
		return new ResponseEntity<>(u, HttpStatus.OK);
	}

//    GET ACCOUNT BY ID 
	@GetMapping("/getaccountbyid")
	public ResponseEntity<?> get(@PathVariable Long id) {

		AboutWork a = aboutWorkRepo.findById(id).orElseThrow(null);

		return new ResponseEntity<>(a, HttpStatus.OK);
	}

	// GET TOP ORDER ACCOUNT
	@GetMapping("/gettoporder")
	public ResponseEntity<?> findtoporder() {
		AboutWork a = aboutWorkRepo.findTopByOrderByIdAsc();

		return new ResponseEntity<>(a, HttpStatus.OK);
	}

	// DELETE ACCOUNT
	@DeleteMapping("/deleteaccount")
	public void deleteaccount(
//    		@PathVariable  Long id
	) {
//    	 AboutWork a = aboutWorkRepo.findById(id).orElseThrow(null);
		AboutWork a = aboutWorkRepo.findTopByOrderByIdAsc();

		aboutWorkRepo.delete(a);
	}

}