package com.example.sr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableRetry
@EnableCircuitBreaker
public class SrApplication {

	public static void main(String[] args) {
		SpringApplication.run(SrApplication.class, args);
	}
}

@RestController
class SrController{
	
	@Autowired
	private final SrService srService;
	
	public SrController(SrService srService) {
		this.srService = srService;
	}
	
	@RequestMapping("/hello")
	public String sayHello() {
		return "Hello, how r u? "+ srService.unReliableService();
	}
}


class UnreliableException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7774721594972925576L;

	public UnreliableException() {
		super();
	}

	public UnreliableException(String message) {
		super(message);
	}
	
	
}

@Component
class SrService{
	
	public static int i;
	//@CircuitBreaker(maxAttempts = 5)
    @Retryable(maxAttemptsExpression = "#{${max.attempts}}")
	public String unReliableService()  {
		
		/*if(Math.random() > 0.3) {
			throw new IllegalArgumentException("value > 0.3");
		}
		if(Math.random() > 0.8)
			throw new UnreliableException("i told u.");
		*/
		System.out.printf("'%s',calling unreliable:'%s'",Thread.currentThread().getName(),i++);
		throw new UnreliableException("making immpossible.");
		
		//return "i am unreliable";
	}
	@Recover
	public String reliable() {
		return "I am reliable. therad name:"+Thread.currentThread().getName();
	}
	
}