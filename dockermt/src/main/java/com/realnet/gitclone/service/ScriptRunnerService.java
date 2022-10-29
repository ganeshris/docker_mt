package com.realnet.gitclone.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.realnet.gitclone.exception.ResourceNotFoundException;
import com.realnet.gitclone.model.ScriptRunnerModel;

public interface ScriptRunnerService {
	
	/* GET */
	public List<ScriptRunnerModel> getAll();

	/* GET by ID */
	public ResponseEntity<ScriptRunnerModel> getById(int id) throws ResourceNotFoundException;

	public ScriptRunnerModel create(ScriptRunnerModel scriptRunnerModel);

	public ResponseEntity<ScriptRunnerModel> update(int id, ScriptRunnerModel scriptRunnerModel) throws ResourceNotFoundException;

	public Map<String, Boolean> delete(int id) throws ResourceNotFoundException;


}
