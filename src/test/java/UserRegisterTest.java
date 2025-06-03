import clients.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.core.Is.is;

/**
 * Тесты регистрации пользователя через API
 * Проверяются: успешное создание, повторная регистрация и ошибки при отсутствии обязательных полей
 */

public class UserRegisterTest {

    private UserClient userClient;
    private User user;
    private String bearerToken;

    @Before
    public void setUp() {
        user = User.random();
        userClient = new UserClient();
    }

    @Test
    @Epic("User's test")
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка создание уникального пользователя")
    public void createUserTest() {
        ValidatableResponse responseRegister = userClient.register(user).then();
        bearerToken = responseRegister.extract().path("accessToken");
        responseRegister.statusCode(SC_OK).body("success", is(true));
    }

    @Test
    @Epic("User's test")
    @DisplayName("Создание не уникального пользователя")
    @Description("Проверка создания  не уникального пользователя")
    public void createAlreadyExistsUserTest() {
        ValidatableResponse first = userClient.register(user).then();
        bearerToken = first.extract().path("accessToken");

        ValidatableResponse second = userClient.register(user).then();
        second.statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("User already exists"));
    }

    @Test
    @Epic("User's test")
    @DisplayName("Создание пользователя без имени")
    @Description("Проверка пользователя без имени")
    public void createUserWithoutNameTest() {
        user.setName("");
        ValidatableResponse response = userClient.register(user).then();
        response.statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @Epic("User's test")
    @DisplayName("Создание пользователя без email")
    @Description("Проверка пользователя без email")
    public void createUserWithoutEmailTest() {
        user.setEmail("");
        ValidatableResponse response = userClient.register(user).then();
        response.statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @Epic("User's test")
    @DisplayName("Создание пользователя без пароля")
    @Description("Проверка пользователя без пароля")
    public void createUserWithoutPasswordTest() {
        user.setPassword("");
        ValidatableResponse response = userClient.register(user).then();
        response.statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @After
    public void tearDown() {
        if (bearerToken != null) {
            userClient.delete(bearerToken);
        }
    }
}
