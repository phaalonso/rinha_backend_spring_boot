package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


	@Bean
	@ConditionalOnProperty(value = "virtualThreads.enabled", havingValue = "true")
	AsyncTaskExecutor applicationTaskExecutor() {
		System.out.println("Ativando virtual threads");
		// enable async servlet support
		ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
		return new TaskExecutorAdapter(executorService);
	}

	@Bean
	@ConditionalOnProperty(value = "virtualThreads.enabled", havingValue = "true")
	TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
		return protocolHandler -> {
			protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
		};
	}
}
