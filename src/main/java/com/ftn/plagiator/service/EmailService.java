package com.ftn.plagiator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ftn.plagiator.model.User;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender javaMailSender;

	/*
	 * Koriscenje klase za ocitavanje vrednosti iz application.properties fajla
	 */
	@Autowired
	private Environment env;

	/*
	 * Anotacija za oznacavanje asinhronog zadatka
	 * Vise informacija na: https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#scheduling
	 */
	@Async
	public void sendNotificaitionAsync(User user) throws MailException, InterruptedException {

		//Simulacija duze aktivnosti da bi se uocila razlika
		//Thread.sleep(10000);
		System.out.println("Slanje emaila...");

		String link = env.getProperty("spring.mail.mail-path") + "#/registracija/aktiviranjeNaloga/" + user.getId();
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(user.getEmail());
		mail.setFrom(env.getProperty("spring.mail.username"));
		mail.setSubject("Link za aktivaciju naloga");
		mail.setText("Pozdrav " + user.getName() + ",\n\nKlikom na sledeci link Vas nalog ce biti aktiviran.\n" + link);
		javaMailSender.send(mail);

		System.out.println("Email poslat!");
	}
	
	@Async
	public void sendNotificaitionUploadOfNewDocument(User user, String documentTitle, Long plagiatorId) throws MailException, InterruptedException {

		//Simulacija duze aktivnosti da bi se uocila razlika
		System.out.println("Slanje emaila upload...");
		
		String link = env.getProperty("spring.mail.mail-path") + "#/new-document/" + plagiatorId;

		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(user.getEmail());
		mail.setFrom(env.getProperty("spring.mail.username"));
		mail.setSubject("Obavestenje o uspesnom uploadu novog rada u plagiator");
		mail.setText("Pozdrav " /*+ user.getName()*/ + ",\n\n" + documentTitle + " Rad je uspesno uploadovan mozete videti "
				+ "analizu poklapanja sa drugim dokumentima. Klikom na sledeci link ce se prikazati analiza rezultata: \n" 
				+ link);
		javaMailSender.send(mail);

		System.out.println("Email poslat upload!");
	}
	
}