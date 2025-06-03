import clients.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.core.Is.is;

/**
 * Тесты на авторизацию пользователя через API
 * Проверяются: успешная авторизация, ошибки при неправильном email и пароле
 */
public class UserLoginTest {

    private UserClient userClient;
    private User user;
    private String bearerToken;

    @Before
    public void setUp() {
        user = User.random();
        userClient = new UserClient();
        ValidatableResponse register = userClient.register(user).then();
        bearerToken = register.extract().path("accessToken");
    }

    @After
    public void tearDown() {
        if (bearerToken != null) {
            userClient.delete(bearerToken);
        }
    }

    /**
     * Проверка успешного логина зарегистрированного пользователя
     */
    @Test
    @Epic("User's test")
    @DisplayName("Авторизация существующего пользователя")
    @Description("Успешный логин пользователя после регистрации")
    public void loginRegisteredUserTest() {
        ValidatableResponse response = userClient.login(user).then();
        response.statusCode(SC_OK).body("success", is(true));
    }

    /**
     * Проверка, что логин с неправильным паролем возвращает ошибку
     */
    @Test
    @Epic("User's test")
    @DisplayName("Авторизация с неверным паролем")
    @Description("Логин с неправильным паролем должен возвращать 401")
    public void loginWithWrongPasswordTest() {
        user.setPassword("wrongPassword");
        ValidatableResponse response = userClient.login(user).then();
        response.statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    /**
     * Проверка, что логин с пустым email возвращает ошибку
     */
    @Test
    @Epic("User's test")
    @DisplayName("Авторизация с пустым email")
    @Description("Попытка авторизации с пустым email должна завершиться ошибкой")
    public void loginWithEmptyEmailTest() {
        user.setEmail("");
        ValidatableResponse response = userClient.login(user).then();
        response.statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }
}
