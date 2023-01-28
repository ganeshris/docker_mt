package com.realnet.gitclone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//@SpringBootApplication
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class GitCloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(GitCloneApplication.class, args);
		
		String s ="Script runner is running";
		
		
		System.out.println(s+"\n");
		
		
		
	}

}
