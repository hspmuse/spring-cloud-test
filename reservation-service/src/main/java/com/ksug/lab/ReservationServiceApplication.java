package com.ksug.lab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;

//@EnableBinding(Sink.class)
//@EnableDiscoveryClient
@SpringBootApplication
public class ReservationServiceApplication {

	private static Logger LOGGER = LoggerFactory.getLogger(
		ReservationServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ReservationServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(ReservationRepository rr) {
		return args -> {

			rr.deleteAll();

			Arrays.asList("Dr. Rod,Dr. Syer,Juergen,Spencer,Phillip,ALL THE COMMUNITY,Josh".split(","))
				.forEach(x -> rr.save(new Reservation(x)));

			rr.findAll().forEach(System.out::println);
		};
	}

	@Bean
	HealthIndicator healthIndicator() {
		return () -> Health.status("I <3 Spring!").build();
	}

	@Component
	@RepositoryEventHandler
	public static class ReservationEventHandler {

		@Autowired
		private CounterService counterService;

		@HandleAfterCreate
		public void create(Reservation p) {
			count("reservation.create", p);
		}

		@HandleAfterSave
		public void save(Reservation p) {
			count("reservations.save", p);
			count("reservations." + p.getId() + ".save", p);
		}

		@HandleAfterDelete
		public void delete(Reservation p) {
			count("reservations.delete", p);
		}

		protected void count(String evt, Reservation p) {
			this.counterService.increment(evt);
			this.counterService.increment("meter." + evt);
		}
	}


}

@RefreshScope
@RestController
class MessageRestController {

	@Value("${message}")
	private String message;

	@RequestMapping("/message")
	String getMessage() {
		return this.message;
	}
}


@Component
class ReservationResourceProcessor implements ResourceProcessor<Resource<Reservation>> {

	@Override
	public Resource<Reservation> process(Resource<Reservation> reservationResource) {
		Reservation reservation = reservationResource.getContent();
		Long id = reservation.getId();
		String url = "http://aws.images.com/" + id + ".jpg";
		reservationResource.add(new Link(url, "profile-photo"));
		return reservationResource;
	}
}

@RepositoryRestResource
interface ReservationRepository extends JpaRepository<Reservation, Long> {

	@RestResource(path = "by-name")
	Collection<Reservation> findByReservationName(@Param("rn") String rn);
}


