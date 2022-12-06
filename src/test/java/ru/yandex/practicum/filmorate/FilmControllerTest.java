package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@ContextConfiguration(classes = {FilmController.class, Film.class})
@WebMvcTest
public class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    Film film;
    @MockBean
    private FilmController filmController;
    private static final ObjectMapper mapper = new ObjectMapper();
    String json;

    @Test
    public void testPostCorrectFilm() throws Exception {
        film.setId(1);
        film.setName("nisi eiusmod1");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967, 3, 25));
        film.setDuration(100);

        Mockito.when(filmController.createFilm(ArgumentMatchers.any())).thenReturn(film);
        json = mapper.writeValueAsString(film);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.name", Matchers.equalTo("nisi eiusmod1")))
                .andExpect(jsonPath("$.description", Matchers.equalTo("adipisicing")))
                .andExpect(jsonPath("$.releaseDate", Matchers.equalTo("1967-03-25")))
                .andExpect(jsonPath("$.duration", Matchers.equalTo(100)));
    }

    @Test
    public void testPostEmptyFilmName() throws Exception {
        film.setId(1);
        film.setName("");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967, 3, 25));
        film.setDuration(100);

        Mockito.when(filmController.createFilm(ArgumentMatchers.any())).thenReturn(film);
        json = mapper.writeValueAsString(film);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testPostLongFilmDescription() throws Exception {
        film.setId(1);
        film.setName("nisi eiusmod1");
        film.setDescription("The main film of the year 2009 is Avatar by James Cameron, the producer of such films " +
                "as Terminator, Titanic, The Strangers. This film ranks with above-mentioned world-famous films and " +
                "probably belongs to the science-fiction genre.");
        film.setReleaseDate(LocalDate.of(1967, 3, 25));
        film.setDuration(100);

        Mockito.when(filmController.createFilm(ArgumentMatchers.any())).thenReturn(film);
        json = mapper.writeValueAsString(film);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testPostWrongReleaseDateFilm() throws Exception {
        film.setId(1);
        film.setName("nisi eiusmod1");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1767, 3, 25));
        film.setDuration(100);

        Mockito.when(filmController.createFilm(ArgumentMatchers.any())).thenReturn(film);
        json = mapper.writeValueAsString(film);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testPostNegativeFilmDuration() throws Exception {
        film.setId(1);
        film.setName("nisi eiusmod1");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967, 3, 25));
        film.setDuration(-100);

        Mockito.when(filmController.createFilm(ArgumentMatchers.any())).thenReturn(film);
        json = mapper.writeValueAsString(film);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(status().is4xxClientError());
    }
}