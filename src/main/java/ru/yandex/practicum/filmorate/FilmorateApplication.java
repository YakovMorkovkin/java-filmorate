package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilmorateApplication {
	public static void main(String[] args) {
		SpringApplication.run(FilmorateApplication.class, args);
	}

	/*@Bean
    public Map<Integer, Film> films() {
		return new HashMap<>();
	}
	@Bean
	public Map<Integer, User> users() {
		return new HashMap<>();
	}
	@Bean
	public Set<Genre> genres() {
		return new HashSet<>();
	}*/
}
