package com.services.registration.users.service;

import com.services.registration.exceptions.ResourceNotFoundException;
import com.services.registration.session.entity.AboutWork;
import com.services.registration.users.entity.Email;
import com.services.registration.users.entity.User;
import com.services.registration.users.repository.AboutWorkRepo;
import com.services.registration.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;


@Service
public class AboutServiceImple implements AboutWorkService {

	@Autowired
	private AboutWorkRepo aboutworkrepo;
	
	@Autowired
	private BCryptPasswordEncoder bcryptEncoder;
	
	@Override
	public AboutWork adddata(AboutWork data) {
		// TODO Auto-generated method stub
	
		AboutWork a=new AboutWork();
		a.setId(data.getId());
		a.setName(data.getName());

		
		User user =new User();
		user.setPassword(data.getPassword());
		user.setEmail(data.getEmail());
//		user.setPhone(data.getMobile());
		

		
		AboutWork about=aboutworkrepo.save(a);
	
		
		return  about;
	}

	@Override
	public AboutWork updateById(Long id, @Valid AboutWork about) {
		AboutWork oldUser = aboutworkrepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Constant.NOT_FOUND_EXCEPTION + " :" + id));
		oldUser.setName(about.getName());
		oldUser.setMobile(about.getMobile());
		oldUser.setEmail(about.getEmail());
		oldUser.setPassword(bcryptEncoder.encode(about.getPassword()));
		oldUser.setCompanyname(about.getCompanyname());
		oldUser.setPancard(about.getPancard());
		AboutWork updatedUser = aboutworkrepo.save(oldUser);
		return updatedUser;
	}
	
	@Override
	public AboutWork updateById2(Long id, @Valid AboutWork about) {
		AboutWork oldUser = aboutworkrepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Constant.NOT_FOUND_EXCEPTION + " :" + id));
		
		oldUser.setCompanyname(about.getCompanyname());
		oldUser.setPancard(about.getPancard());
		AboutWork updatedUser = aboutworkrepo.save(oldUser);
		return updatedUser;
	}
	
	
	@Override
	public AboutWork updateById3(Long id, @Valid AboutWork about) {
		AboutWork oldUser = aboutworkrepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Constant.NOT_FOUND_EXCEPTION + " :" + id));
		oldUser.setWorking(about.getWorking());		
		AboutWork updatedUser = aboutworkrepo.save(oldUser);
		return updatedUser;
	}
	

	@Override
	public AboutWork adddata(Email email) {
		// TODO Auto-generated method stub
		AboutWork a=new AboutWork();
		a.setId(email.getId());
		
		AboutWork about=aboutworkrepo.save(a);
		return about;
	}

//	@Override
//	public AboutWork adddata(Email email) {
//		
//		AboutWork a=new AboutWork();
//	a.setId(email.getUserId());
//	a.setName(email.getEmail1());
//	a.setName(email.getEmail2());
//	a.setName(email.getEmail3());
//	a.setName(email.getEmail4());
//		
//		
//		
//		User user =new User();
//		user.setPassword(email.getPassword());
//		user.setEmail(email.getEmail1());
//		user.setEmail(email.getEmail2());
//		user.setEmail(email.getEmail3());
//		user.setEmail(email.getEmail4());
//AboutWork about=aboutworkrepo.save(a);
//	
//		
//		return  about;
//	}

//	@Override
//	public AboutWork updateById(Long id, @Valid AboutWork aboutWork) {
//		// TODO Auto-generated method stub
//		
//		AboutWork oldUser = aboutworkrepo.findById(id)
//				.orElseThrow(() -> new ResourceNotFoundException(Constant.NOT_FOUND_EXCEPTION + " :" + id));
//		
//		oldUser.setCompanyname(aboutWork.getCompanyname());
//
//		final  AboutWork updatedUser = aboutworkrepo.save(oldUser);
//		return updatedUser;
//	}
	
	
}
