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
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration(classes = {UserController.class, User.class})
@WebMvcTest
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserController userController;
    private static final ObjectMapper mapper = new ObjectMapper();
    String json;

    @Test
    public void testPostCorrectUser() throws Exception {
        User user = new User();
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

    @Test
    public void testPostUserWithEmptyEmail() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmail("");
        user.setLogin("dolores");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(2000, 12, 31));

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

    @Test
    public void testPostUserWithWrongEmail() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmail("mail.ru");
        user.setLogin("dolores");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(2000, 12, 31));

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

    @Test
    public void testPostUserWithoutLogin() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmail("mail@mail.ru");
        user.setLogin("");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(2000, 12, 31));

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

    @Test
    public void testPostUserWithBlankInLogin() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmail("mail@mail.ru");
        user.setLogin("dolores santos");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(2000, 12, 31));

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

    @Test
    public void testPostUserWithNullLogin() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmail("mail@mail.ru");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(2000, 12, 31));

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

    @Test
    public void testPostUserWithInFutureBirth() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmail("mail@mail.ru");
        user.setLogin("dolores");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(2023, 12, 31));

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

    @Test
    public void testPostUserWithNullBirth() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmail("mail@mail.ru");
        user.setLogin("dolores");
        user.setName("Nick Name");

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