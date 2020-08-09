package com.ftn.plagiator;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.ModelAndView;


@SpringBootApplication
@EnableJpaRepositories
@EnableAsync
public class PlagiatorApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(PlagiatorApplication.class, args);
	}

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder  builder) {
        return builder.sources(PlagiatorApplication.class);
    }
	//All the resources that cannot be found on server are redirected to index.html. Angular will handle the routing for them.
	@Bean
	ErrorViewResolver supportPathBasedLocationStrategyWithoutHashes() {
	    return new ErrorViewResolver() {
			
	        @Override
	        public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
	            return status == HttpStatus.NOT_FOUND
	                    ? new ModelAndView("index.html", Collections.<String, Object>emptyMap(), HttpStatus.OK)
	                    : null;
	        }
	    };
	}

}
