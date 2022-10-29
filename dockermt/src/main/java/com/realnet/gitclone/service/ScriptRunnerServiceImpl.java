//package com.realnet.gitclone.service;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import com.realnet.gitclone.exception.ResourceNotFoundException;
//import com.realnet.gitclone.model.ScriptRunnerModel;
//import com.realnet.gitclone.repository.ScriptRunnerRepository;
//
//@Service
//public class ScriptRunnerServiceImpl implements ScriptRunnerService{
//	
//	
//	@Autowired(required=true)
//	private ScriptRunnerRepository scriptRunnerRepository;
//
//	/* GET */
//	public List<ScriptRunnerModel> getAll() {
//		return scriptRunnerRepository.findAll();
//	}
//
//	/* GET by ID */
//	public ResponseEntity<ScriptRunnerModel> getById(int id) throws ResourceNotFoundException {
//		ScriptRunnerModel scriptRunnerModel = scriptRunnerRepository.findById(id)
//				.orElseThrow(() -> new ResourceNotFoundException("User not found on :: " + id));
//		return ResponseEntity.ok().body(scriptRunnerModel);
//	}
//
//	public ScriptRunnerModel create(ScriptRunnerModel scriptRunnerModel) {
//		return scriptRunnerRepository.save(scriptRunnerModel);
//	}
//
//	public ResponseEntity<ScriptRunnerModel> update(int id, ScriptRunnerModel scriptRunnerModel) throws ResourceNotFoundException {
//
//		ScriptRunnerModel ScriptRunnerModel1 = scriptRunnerRepository.findById(id)
//				.orElseThrow(() -> new ResourceNotFoundException("ScriptRunnerModel not found for this id :: " + id));
//
//		ScriptRunnerModel1.setCode(scriptRunnerModel.getCode());
//		
//		final ScriptRunnerModel updatedUser = scriptRunnerRepository.save(ScriptRunnerModel1);
//		return ResponseEntity.ok(updatedUser);
//	}
//
//	public Map<String, Boolean> delete(int id) throws ResourceNotFoundException {
//		ScriptRunnerModel rn_users = scriptRunnerRepository.findById(id)
//				.orElseThrow(() -> new ResourceNotFoundException("ScriptRunnerModel not found for this id :: " + id));
//
//		scriptRunnerRepository.delete(rn_users);
//		
//		Map<String, Boolean> response = new HashMap<>();
//		response.put("deleted", Boolean.TRUE);
//		return response;
//	}
//	
//	
//
//}
