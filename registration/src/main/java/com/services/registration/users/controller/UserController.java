package com.services.registration.users.controller;

import com.services.registration.config.EmailService;
import com.services.registration.exceptions.ResourceNotFoundException;
import com.services.registration.fnd.service.FileStorageService;
import com.services.registration.users.entity.PasswordResetRequest;
import com.services.registration.users.entity.Sys_Accounts;
import com.services.registration.users.entity.User;
import com.services.registration.users.entity.UserProfileDTO;
import com.services.registration.users.repository.UserRepo;
import com.services.registration.users.service.CompanyService;
import com.services.registration.users.service.UserService;
import com.services.registration.utils.UserConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/api") // , produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {"User Mnagement"})
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private EmailService emailService;

    @Value("${projectPath}")
    private String projectPath;

    // GET profile details (ADMIN, USER BOTH, WHO IS LOGGED IN)
    @ApiOperation(value = "Gets current user information", response = User.class)
    @GetMapping("/user-profile")
    public ResponseEntity<?> getUserProfile() {
        User user = userService.getLoggedInUser();
        return ResponseEntity.ok().body(user);
    }

    // UPDATE (ADMIN, USER BOTH, WHO IS LOGGED IN)
    @ApiOperation(value = "Update current user information", response = User.class)
    @PutMapping("/user-profile")
    public ResponseEntity<?> updateUserProfile(@Valid @RequestBody UserProfileDTO userRequest) {
        String loggedInUserEmail = userService.getLoggedInUserEmail();
        User user = userService.updateByEmail(loggedInUserEmail, userRequest);
        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }

    // UPLOAD IMAGE
    @PostMapping("/upload")
    public ResponseEntity<?> uploadProfilePic(@RequestParam("imageFile") MultipartFile file) throws IOException {
        User user = userService.getLoggedInUser();
        // String userId = Long.toString(user.getUserId());
        String userId = String.valueOf(user.getUserId());
        System.out.println("USER ID = " + userId);
        // String uploadPath =
        // projectPath.concat("/src/main/resources/uploaded-picture/" + userId + "/");
        String uploadPath = projectPath.concat("/src/main/resources/uploaded-picture/");
        System.out.println("UPLOAD PATH = " + uploadPath);

        fileStorageService.uploadProfilePicture(file, uploadPath);

        String fileName = file.getOriginalFilename();
        // String head = fileName.substring(0, fileName.indexOf("."));
        String ext = fileName.substring(fileName.lastIndexOf("."));
        String fileNewName = "profile-pic-" + userId + ext;

        System.out.println(
                "UPLOAD PATH = " + uploadPath + "\nFILE NAME = " + fileNewName + "\nFile Size = " + file.getSize());
        user.setPhotos(fileNewName);
        boolean success = userService.insertOrSaveUser(user);
        Map<String, Boolean> res = new HashMap<String, Boolean>();
        if (success) {
            res.put("success", success);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            res.put("success", success);
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    // GET IMAGE
    @GetMapping("/retrieve-image")
    public ResponseEntity<?> getProfilePic() {
        User user = userService.getLoggedInUser();
        String imageName = user.getPhotos();
        String imagePath = projectPath.concat("/src/main/resources/uploaded-picture/" + imageName);
        File file = new File(imagePath);
        Map<String, String> res = new HashMap<String, String>();

        String encodeBase64 = null;
        try {
            String ext = FilenameUtils.getExtension(file.getName());
            FileInputStream fis = new FileInputStream(file);
            // byte[] media = IOUtils.toByteArray(fis);
            byte[] bytes = new byte[(int) file.length()];
            fis.read(bytes);
            encodeBase64 = Base64.getEncoder().encodeToString(bytes);
            String data = "data:/image/" + ext + ";base64," + encodeBase64;
            fis.close();
            res.put("image", data);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (IOException e) {
            log.debug("File Not Found Exception Handled: {}", e.getMessage());
            res.put("image", "Not Found");
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }
    }

    // ====================USER ACCOUNT DETAILS================
    // ########## NEED MOD ##########
    // GET ADMIN profile details (admin accounts)
    @ApiOperation(value = "Get User Account Details", response = Sys_Accounts.class)
    @GetMapping("/user-account")
    public ResponseEntity<?> getUserAccountDetails() {
        User user = userService.getLoggedInUser();
        Sys_Accounts sys_account = user.getSys_account();
        System.out.println("Company Details : " + sys_account);
        return ResponseEntity.ok().body(sys_account);
    }

    // =========== reset password =============
    @ApiOperation(value = "Reset Password", response = PasswordResetRequest.class)
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetRequest passwordResetReq) {
        boolean reset = userService.changePassword(passwordResetReq.getOldPassword(),
                passwordResetReq.getNewPassword());
        System.out.println("resetPassword() Controller : RESET ? " + reset);
        Map<String, Boolean> res = new HashMap<String, Boolean>();
        if (reset) {
            res.put("reset", reset);
            return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
        } else {
            res.put("reset", reset);
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    // ############=== NEED MODIFICATION ========######

    // GET profile details (user ADDED BY ADMIN)
    @ApiOperation(value = "Get Company User Details", response = User.class)
    @GetMapping("/org-users")
    public ResponseEntity<?> getUsersByAccount(@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
                                               @RequestParam(value = "size", defaultValue = "20", required = false) Integer size) {
        User adminUser = userService.getLoggedInUser();
        // Sys_Accounts Sys_Account = adminUser.getCompany();
        Sys_Accounts sys_account = adminUser.getSys_account();
//		Long account_id = Sys_Account.getId();

//		List<User> users = userService.getUsersByAccountId(account_id);
        // OR
//		Sys_Accounts company = companyService.getById(account_id);
//		List<User> users = company.getUsers();
        System.out.println("Company Details : " + sys_account);
        Long accId = adminUser.getSys_account().getId();
        Pageable paging = PageRequest.of(page, size, Sort.by("created_at").descending());

        // invited user list will show here
        List<User> invitedUsers = userRepo.findByAccountIdAndStatus(accId, UserConstant.STATUS_INVITED, paging);


        return ResponseEntity.ok().body(invitedUsers);
    }

    // GET USER BY ID (ADDED BY ADMIN)
    @ApiOperation(value = "Get Company User By Id", response = User.class)
    @GetMapping("/org-users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable(value = "id") Long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User Not Found ::" + id);
        }
        return ResponseEntity.ok().body(user);
    }

    // SAVE A USER BY ADMIN
    @ApiOperation(value = "Add A New Company User", response = User.class)
    @PostMapping("/org-users")
    public ResponseEntity<?> createUser(@Valid @RequestBody User userReq) throws MessagingException, IOException {

        // admin will invite and create user with default value
        User user = userService.createUserByAdmin(userReq);

        // ====EMAIL CODE====
        User admin = userService.getLoggedInUser();
        String from = admin.getEmail(); // from is not working
        String to = userReq.getEmail();
        String subject = "Invitation To Collaborate";
        String text = "Dear <b>" + userReq.getFirstName() + "</b>,<br> " + "You have an Invitation from <b>"
                + admin.getFullName() + "</b>. Please Follow the steps below. <br>"
                + "<b>1</b>. Log on to https://localhost:4200/login <br>"
                + "<b>2</b>. Log on to the system with the following User Name and Password.<br>"
                + "<b>3</b>. User Name : " + userReq.getEmail() + " Password : " + userReq.getPassword() + "<br>"
                + "<b>4</b>. Change the existing system generated password and coninue.";
//		try {
//			//emailService.sendSimpleMessage(from, to, subject, text);
//		} catch (MailException mailException) {
//			System.out.println(mailException);
//		}
        emailService.sendEmailWithAttachment(to, subject, text);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    // UPDATE USER ADDED BY ADMIN
    @ApiOperation(value = "Update A Company User", response = User.class)
    @PutMapping("/org-users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable(value = "id") Long id, @Valid @RequestBody User user) {
        User updatedUser = userService.updateById(id, user);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedUser);
    }

    // DELETE USER ADDED BY ADMIN
    @DeleteMapping("/org-users/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteUser(@PathVariable(value = "id") Long id) {
        boolean deleted = userService.deleteById(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        // response.put("deleted", deleted);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllUser(){
    	List<User> l = userService.getAll();
    	return new ResponseEntity<>(l,HttpStatus.OK);
    }
    @GetMapping("/all1")
    public ResponseEntity<?> getAllUser1(){
    	List<Sys_Accounts> l = companyService.getAll();
    	return new ResponseEntity<>(l,HttpStatus.OK);
    }
    // ==== access by admin done

//	@ApiOperation(value = "Gets current user information", response = UserResponse.class)
//	@RequestMapping(value = "/user", method = RequestMethod.GET, produces = {"application/json"})
//	public UserResponse getUserInformation(@RequestParam(value = "name", required = false) String userIdParam, HttpServletRequest req) {
//
//		String loggedInUserName = userService.getLoggedInUserName();
//
//		User user;
//		boolean provideUserDetails = false;
//
//		if (Strings.isNullOrEmpty(userIdParam)) {
//			provideUserDetails = true;
//			user = userService.getLoggedInUser();
//		}
//		else if (loggedInUserName.equals(userIdParam)) {
//			provideUserDetails = true;
//			user = userService.getLoggedInUser();
//		}
//		else {
//			//Check if the current user is superuser then provide the details of requested user
//			provideUserDetails = true;
//			user = userService.getUserInfoByUserId(userIdParam);
//		}
//
//		UserResponse resp = new UserResponse();
//		if (provideUserDetails) {
//            resp.setOperationStatus(ResponseStatusEnum.SUCCESS);
//		}
//		else {
//            resp.setOperationStatus(ResponseStatusEnum.NO_ACCESS);
//			resp.setOperationMessage("No Access");
//		}
//		resp.setData(user);
//		return resp;
//	}
//
//
//
//	// @Secured({"ROLE_ADMIN", "ROLE_USER"})
//	@PreAuthorize("hasRole('ADMIN')")
//	@RequestMapping(value = "/users", method = RequestMethod.GET)
//	public List<User> listUser() {
//		return userService.getAll();
//	}
//
//	// @Secured("ROLE_USER")
//	// @PreAuthorize("hasRole('USER')")
//	// @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
//	// @PreAuthorize("hasRole('ADMIN') && hasRole('USER')")
//	@PreAuthorize("hasRole('USER')")
//	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
//	public User getOne(@PathVariable(value = "id") Long id) {
//		return userService.getById(id);
//	}
    /*
     * @RequestMapping(value = "/signup", method = RequestMethod.POST) public User
     * saveUser(@RequestBody UserDto user) { return userService.save(user); }
     */
}
