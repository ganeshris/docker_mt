package com.realnet.gitclone.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.realnet.gitclone.exception.ResourceNotFoundException;
import com.realnet.gitclone.model.ScriptRunnerModel;
import com.realnet.gitclone.service.ScriptRunnerService;

@RestController
@CrossOrigin(origins = "http://localhost:4123")
@RequestMapping("/api")
public class ScriptRunnerController {

//	@Autowired
//	ScriptRunnerService scriptRunnerService;

	@Value("${project_path}")
	private String projectpath;

//	@Value("${project_pathcheck}")
//	private String projectpathcheck;

	/**
	 * // * Get All users. //
	 */
//	@RequestMapping(value = "/getAllScripts", method = RequestMethod.GET, headers = { "Accept=application/json" })
//	@ResponseStatus(HttpStatus.OK)
//	public List<ScriptRunnerModel> getAllScripts() {
//		return scriptRunnerService.getAll();
//	}
//
//	/**
//	 * Gets users by id.
//	 */
//	@GetMapping("/getScriptById/{id}")
//	public ResponseEntity<ScriptRunnerModel> getScriptById(@PathVariable(value = "id") int id) throws ResourceNotFoundException {
//		return scriptRunnerService.getById(id);
//	}
//
//	/**
//	 * create new user
//	 */
//	@PostMapping("/addScript")
//	ScriptRunnerModel addScript(@Valid @RequestBody ScriptRunnerModel scriptRunnerModel) {
//		return scriptRunnerService.create(scriptRunnerModel);
//	}
//
//	/**
//	 * update user
//	 */
//	@PutMapping("/updateScriptById/{id}")
//	public ResponseEntity<ScriptRunnerModel> updateScriptById(@PathVariable(value = "id") int id,
//			@Valid @RequestBody ScriptRunnerModel scriptRunnerModel) throws ResourceNotFoundException {
//
//		return scriptRunnerService.update(id, scriptRunnerModel);
//	}
//
//	/**
//	 * delete user
//	 */
//	@DeleteMapping("/deleteScriptById/{id}")
//	public Map<String, Boolean> deleteScriptById(@PathVariable(value = "id") int id) throws ResourceNotFoundException {
//		return scriptRunnerService.delete(id);
//	}

	/**
	 * runScript method by reading file
	 * 
	 * @throws IOException
	 *
	 */
	
	//RUN SCRIPT
	@GetMapping(value = "/runScript")
	public ResponseEntity<?> runScript(@RequestParam String filepath,
			                           @RequestParam String filename) throws IOException {

		System.out.println("runScript method called in ScriptRunnerController");

		String str = null;

		String path = filepath+"/"+filename;

		ProcessBuilder pb = new
//		ProcessBuilder("C://Users//Karam//git//surepipe-runner//src//main//resources//ScriptFiles//multi_output.bat");
//		ProcessBuilder(path1+filename);
		ProcessBuilder(path);

//		System.out.println("path taken ="+  path);
		System.out.println("file taken ="+new File(path).getAbsoluteFile());

		pb.directory(new File(System.getProperty("user.home")));

		Process process = pb.start();
		if (process.isAlive()) {

//			Process process = Runtime.getRuntime().exec("where java");

			BufferedReader br2 = new BufferedReader(new InputStreamReader(process.getInputStream()));

			System.out.println("file is running");

			while ((str = br2.readLine()) != null) {
				System.out.println(str);
			}
			br2.close();
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>("bad request", HttpStatus.BAD_REQUEST);
		}

	}
	
	
	
	
	//NOT IN USE
	@GetMapping(value = "/runScript1")
	public ResponseEntity<?> runScript1(@RequestParam String s1, @RequestParam String s2) throws IOException {

//		String sentence ="PRJ_NAME=gitclone";
//		String sentence1 ="GIT_USER=admin123";
//		String sentence2 ="GIT_PASS=admin123";

		System.out.println("runScript method called in ScriptRunnerController");

		String str = null;

		String path = projectpath + "/ScriptFiles/copy.sh";
		System.out.println(path);
		File pathfile = new File(path);
		String filename = pathfile.getName();

//		String line1 = "";
//		BufferedReader br1 = new BufferedReader(new FileReader(path));
//		List<String> list = new ArrayList<>();
//		while ((line1 = br1.readLine()) != null) {
//			String[] data = line1.split(",");
//			for (String d : data) {
//				list.add(d+"\n");
//			}
//
//		}
//		int i=list.indexOf(sentence);
//		int i1 = list.indexOf(sentence1);
//		int i2 = list.indexOf(sentence2);
//		list.remove(i);
//		
//		list.remove(i1);
//		list.remove(i2);
//		
//		list.add(i, "PRJ_NAME ="+S);
//		list.add(i1, "GIT_USER="+s1);
//		list.add(i2,"GIT_PASS="+s2);
//		
//		System.out.println(list);

		String line = "";
		StringBuilder intialize = new StringBuilder();
		StringBuilder class_name = new StringBuilder();
		StringBuilder middle = new StringBuilder();
		StringBuilder end = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(path));
		intialize.append("\"");
		intialize.append("*****************************************\n" + "Below is the script to copy reporsitry\n"
				+ "*****************************************\n" + "#!/bin/bash\n");

		while ((line = br.readLine()) != null) {
			String[] data = line.split(",");
			for (String d : data) {
				if (d.contains("PRJ_NAME=")) {
					intialize.append("PRJ_NAME=gitclone");
					intialize.append("\n");
				} else if (d.contains("GIT_USER=")) {
					intialize.append("GIT_USER=admin123");
					intialize.append("\n");
				} else if (d.contains("GIT_PASS=")) {
					intialize.append("GIT_PASS=admin123");
					intialize.append("\n");
				} else if (d.contains("GIT_URL_FROM=")) {
					intialize.append("GIT_URL_FROM=http://13.126.217.36:31633/admin123/" + s1 + ".git");
					intialize.append("\n");
				} else if (d.contains("GIT_URL_TO=")) {
					intialize.append("GIT_URL_TO=http://13.126.217.36:31633/admin123/" + s2 + ".git");
					intialize.append("\n");
				}
//				
			}
		}
		intialize.append("docker build .\n" + "echo IMAGE_NAME=$GIT_URL_TO");

		System.out.println(intialize);

		String path1 = projectpath + "/testingfor script/" + filename;
		System.out.println(path1);

		FileWriter fw = null;
		BufferedWriter bw = null;

		// FILE NAME SHOULD CHANGE DEPENDS ON TECH_STACK/OBJECT_tYPE/SUB_OBJECT_TYPE
		File masterBuilderFile = new File(path1);
		if (!masterBuilderFile.exists()) {
			masterBuilderFile.createNewFile();
		}
		fw = new FileWriter(masterBuilderFile.getAbsoluteFile());
		bw = new BufferedWriter(fw);
		bw.write(intialize.toString());
		bw.close();

		ProcessBuilder pb = new
//		ProcessBuilder("C://Users//Karam//git//surepipe-runner//src//main//resources//ScriptFiles//multi_output.bat");
//		ProcessBuilder(path1+filename);
		ProcessBuilder(path1);

		System.out.println("path taken ="+path1);
		System.out.println(new File(path1).getAbsoluteFile());


		pb.directory(new File(System.getProperty("user.home")));

		Process process = pb.start();
		if (process.isAlive()) {

//			Process process = Runtime.getRuntime().exec("where java");

			BufferedReader br2 = new BufferedReader(new InputStreamReader(process.getInputStream()));

			System.out.println("file is running");

			while ((str = br2.readLine()) != null) {
				System.out.println(str);
			}
			br2.close();
			return new ResponseEntity<>("file is running", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("bad request", HttpStatus.BAD_REQUEST);
		}

	}
}
