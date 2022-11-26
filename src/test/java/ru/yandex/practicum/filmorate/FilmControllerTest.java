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
    @MockBean
    private FilmController filmController;
    private static final ObjectMapper mapper = new ObjectMapper();
    String json;

    @Test
    public void testPostCorrectFilm() throws Exception {
        Film film = new Film();
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
        Film film = new Film();
        film.setId(1);
        //film.setName("nisi eiusmod1");
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
    public void testPostLongFilmDescription() throws Exception {
        Film film = new Film();
        film.setId(1);
        film.setName("nisi eiusmod1");
        film.setDescription("He wandered  by the L-wr-nce H-ll \n" +
                "      (An axe upon his shoulders laid)\n" +
                "Quoth He:—'I do not like at all\n" +
                "      An upstart tree that casts a shade;\n" +
                "Besides, it's bigger than the rest, \n" +
                "Which rankles in my Liberal breast.\n" +
                "\n" +
                "It took some twenty years to grow.\n" +
                "      It is a most offensive tree;\n" +
                "And shall I pass without a blow, \n" +
                "      Arboreal aristocracy?\n" +
                "Jamais—nevaire! So down it comes.—\n" +
                "Bed out some neat chrysanthemums.\n" +
                "\n" +
                "The long weeks came; the long weeks passed; \n" +
                "      The neat chrysanthemums were bedded;\n" +
                "But some grew slow, while some grew fast,\n" +
                "      And some were long, and some short headed,\n" +
                "He watched  their nodding ranks with tears,— \n" +
                "And fetched a malli  and the shears.");
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
    public void testPostWrongReleaseDateFilm() throws Exception {
        Film film = new Film();
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
        Film film = new Film();
        film.setId(1);
        film.setName("nisi eiusmod1");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1767, 3, 25));
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