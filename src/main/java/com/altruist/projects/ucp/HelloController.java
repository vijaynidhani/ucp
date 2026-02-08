package com.altruist.projects.ucp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class HelloController {

	@GetMapping("/hello")
	public String hello() {
		log.info("Hello endpoint called");
		log.debug("Processing hello request");
		return "Hello, World!";
	}

}
