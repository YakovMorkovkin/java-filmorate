package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SqlGroup({
		@Sql(scripts = {"/schema.sql"},config = @SqlConfig(encoding = "UTF-8")),
		@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/test-data.sql"),
		@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/drop-test-data.sql"),
})
@Slf4j
class FilmorateApplicationTests {
	private final UserStorage userStorage;
	private final UserService userService;
	private final FilmStorage filmStorage;
	private final FilmService filmService;

	private User testUser(int id,String email,String login,String name,LocalDate birthday) {
		User user = new User();
		user.setId(id);
		user.setEmail(email);
		user.setLogin(login);
		user.setName(name);
		user.setBirthday(birthday);
		return user;
	}

	private Film testFilm(int id, String name, String description, LocalDate releaseDate, int duration, Mpa mpa) {
		Film film = new Film();
		film.setId(id);
		film.setName(name);
		film.setDescription(description);
		film.setReleaseDate(releaseDate);
		film.setDuration(duration);
		film.setMpa(mpa);
		return film;
	}
	@Test
	void contextLoads() {
	}

	//UserDbStorage
	@Test
	void testGetAllUsers() {
		log.debug("???????? ?????????????????? ???????? ??????????????????????????");
		List<User> users = userStorage.getAllUsers();
		assertEquals(12, users.size());
		users.forEach(System.out::println);
	}
	@Test
	void testFindUserById() {
		log.debug("???????? ???????????? ???????????????????????? ???? id ");
		Optional<User> userOptional = userStorage.getUserById(1);
		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", 1)
				);
		log.debug("{}",userOptional.orElse(null));
	}
	@Test
	void testCreateUser() {
		log.debug("???????? ???????????????? ????????????????????????");
		User testUser = userStorage.createUser(testUser(0,"adress@mail.ru","login"
				,"name",LocalDate.of(1976, 12, 31)));
		assertThat(testUser)
				.hasFieldOrPropertyWithValue("email", "adress@mail.ru")
				.hasFieldOrPropertyWithValue("login", "login")
				.hasFieldOrPropertyWithValue("name", "name")
				.hasFieldOrPropertyWithValue("birthday", LocalDate.of(1976, 12, 31));
		log.debug("{}",testUser);
	}
	@Test
	void testUpdateUser() {
		log.debug("???????? ???????????????????? ????????????????????????");
		log.debug("{}",userStorage.getUserById(2));
		User testUser = userStorage.updateUser(testUser(2,"adress2@mail.ru","login2"
				,"name2",LocalDate.of(1976, 12, 31)));

		assertThat(testUser)
				.hasFieldOrPropertyWithValue("email", "adress2@mail.ru")
				.hasFieldOrPropertyWithValue("login", "login2")
				.hasFieldOrPropertyWithValue("name", "name2")
				.hasFieldOrPropertyWithValue("birthday", LocalDate.of(1976, 12, 31));
		log.debug("{}",userStorage.getUserById(2));
	}
	//UserServiceDb
	@Test
	void testFriendsFunctional() {
		log.debug("???????? ???????????????????? ????????????");
		//Get all friends
		log.debug("{}",userService.getFriendsOfUser(1));
		//Add friend
		assertEquals(0, userService.getFriendsOfUser(1).size());
		userService.addToFriends(1,2);
		assertEquals(1, userService.getFriendsOfUser(1).size());
		log.debug(userService.getFriendsOfUser(1).toString());
		//Common friends
		assertEquals(0, userService.getCommonFriends(1,3).size());
		userService.addToFriends(3,2);
		assertEquals(1, userService.getCommonFriends(1,3).size());
		log.debug(userService.getCommonFriends(1,3).toString());
		//Remove friend
		userService.removeFromFriends(1,2);
		assertEquals(0, userService.getFriendsOfUser(1).size());
		log.debug(userService.getFriendsOfUser(1).toString());
	}
	//FilmDbStorage
	@Test
	void testGetAllFilms() {
		log.debug("???????? ?????????????????? ???????? ??????????????");
		List<Film> films = filmStorage.getAllFilms();
		assertEquals(3, films.size());
		films.forEach(System.out::println);
	}
	@Test
	void testFindFilmById() {
		log.debug("???????? ???????????? ???????????? ???? id ");
		Optional<Film> filmOptional = filmStorage.getFilmById(1);
		assertThat(filmOptional)
				.isPresent()
				.hasValueSatisfying(film ->
						assertThat(film).hasFieldOrPropertyWithValue("id", 1)
				);
		log.debug("{}",filmOptional.orElse(null));
	}
	@Test
	void testCreateFilm() {
		log.debug("???????? ???????????????? ????????????");
		Mpa mpa = new Mpa();
		mpa.setId(1);
		mpa.setName("");
		Film testFilm = filmStorage.createFilm(testFilm(0,"Film"
				,"Very Interesting",LocalDate.of(2021, 12, 31),120,mpa));
		assertThat(testFilm)
				.hasFieldOrPropertyWithValue("name", "Film")
				.hasFieldOrPropertyWithValue("description", "Very Interesting")
				.hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2021, 12, 31))
				.hasFieldOrPropertyWithValue("duration",120);
		log.debug("{}",testFilm);
	}
	@Test
	void testUpdateFilm() {
		log.debug("???????? ???????????????????? ????????????");
		log.debug("{}",filmStorage.getFilmById(1));
		Mpa mpa = new Mpa();
		mpa.setId(1);
		mpa.setName("");
		Film testFilm = filmStorage.updateFilm(testFilm(1,"Updated film"
				,"Incredible film",LocalDate.of(2019, 12, 31),121,mpa));
		assertThat(testFilm)
				.hasFieldOrPropertyWithValue("id", 1)
				.hasFieldOrPropertyWithValue("name", "Updated film")
				.hasFieldOrPropertyWithValue("description", "Incredible film")
				.hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2019, 12, 31))
				.hasFieldOrPropertyWithValue("duration",121);
		log.debug("{}",testFilm);
	}
	//FilmServiceDb
	@Test
	void testLikeFunctional() {
		log.debug("???????? ???????????????????? like");
		log.debug("???????????????? ???????????????????? Like = " + filmStorage.getFilmById(1).orElse(null).getLikes().size());
		//Add like
		filmService.addLike(4,1);
		assertEquals(2, filmStorage.getFilmById(1).orElse(null).getLikes().size());
		log.debug("???????????????????? Like ?????????? ???????????????????? ???????????? = "
				+ filmStorage.getFilmById(1).orElse(null).getLikes().size());
		//Remove like
		filmService.removeLike(4,1);
		assertEquals(1, filmStorage.getFilmById(1).orElse(null).getLikes().size());
		log.debug("???????????????????? Like ?????????? ???????????????? ???????????? = {}", filmStorage.getFilmById(1).orElse(null).getLikes().size());
	}
	@Test
	void testGetCountOfTheBestFilms() {
		log.debug("???????? ?????????????????? ?????????????????? ???????????????????? ???????????? ??????????????");
		Set<Film> bestFilms = filmService.getCountOfTheBestFilms(2);
		List<Film> filmsList = new ArrayList<>(bestFilms);
		assertEquals(2, bestFilms.size());
		assertEquals(filmStorage.getFilmById(2).orElse(null), filmsList.get(0));
		assertEquals(filmStorage.getFilmById(3).orElse(null), filmsList.get(1));
		log.debug("{}",bestFilms);
	}
	@Test
	void testGenres() {
		log.debug("???????? ?????????????????? ????????????");
		Set<Genre> allGenres = filmService.getAllGenres();
		Genre genre = filmService.getGenreById(2).orElse(null);
		assertEquals(6, allGenres.size());
		assertEquals("??????????", genre.getName());
		log.debug("{}",allGenres);
	}
	@Test
	void testMpa() {
		log.debug("???????? ?????????????????? ???????????????????? ??????????????????");
		Set<Mpa> allMpa = filmService.getAllMpa();
		Mpa mpa = filmService.getMpaById(2).orElse(null);
		assertEquals(5, allMpa.size());
		assertEquals("PG", mpa.getName());
		log.debug("{}",allMpa);
	}
}
