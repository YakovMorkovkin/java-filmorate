package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration(classes = {UserController.class, User.class})
@WebMvcTest
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    User user;
    @MockBean
    private UserController userController;
    private static final ObjectMapper mapper = new ObjectMapper();
    String json;

    @Test
    void testPostCorrectUser() throws Exception {
        user.setId(1);
        user.setEmail("mail@mail.ru");
        user.setLogin("dolores");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(2000, 12, 31));

        Mockito.when(userController.createUser(ArgumentMatchers.any())).thenReturn(user);
        json = mapper.writeValueAsString(user);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.email", Matchers.equalTo("mail@mail.ru")))
                .andExpect(jsonPath("$.login", Matchers.equalTo("dolores")))
                .andExpect(jsonPath("$.name", Matchers.equalTo("Nick Name")))
                .andExpect(jsonPath("$.birthday", Matchers.equalTo("2000-12-31")));
    }

    @ParameterizedTest
    @CsvSource({
            "1,,dolores,Nick Name,2000-12-31",
            "1,'',dolores,Nick Name,2000-12-31",
            "1,mail.ru,dolores,Nick Name,2000-12-31",
            "1,mail@mail.ru,'',Nick Name,2000-12-31",
            "1,mail@mail.ru,dolores santos,Nick Name,2023-12-31",
            "1,mail@mail.ru,,Nick Name,2000-12-31",
    })
    void testPostUserWithWrongParameters(int id, String email, String login, String name, LocalDate birthday) throws Exception {
        user.setId(id);
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(birthday);

        Mockito.when(userController.createUser(ArgumentMatchers.any())).thenReturn(user);
        String json = mapper.writeValueAsString(user);

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(status().is4xxClientError());
    }
}