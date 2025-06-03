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
 * Тесты на обновление данных пользователя
 * Проверяются оба сценария: с авторизацией и без неё
 */
public class UserUpdateTest {

    private UserClient userClient;
    private User user;
    private String bearerToken;

    @Before
    public void setUp() {
        user = User.random();
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        if (bearerToken != null) {
            userClient.delete(bearerToken);
        }
    }

    /**
     * Проверяет, что пользователь может изменить свои данные с авторизацией
     */
    @Test
    @Epic("User's test")
    @DisplayName("Изменение данных пользователя с авторизацией")
    @Description("Проверка успешного изменения данных пользователя при наличии accessToken")
    public void updateUserWithAuthTest() {
        bearerToken = userClient.register(user)
                .then().statusCode(SC_OK)
                .extract().path("accessToken");

        User updated = User.random();
        ValidatableResponse response = userClient.update(updated, bearerToken).then();
        response.statusCode(SC_OK).body("success", is(true));
    }

    /**
     * Проверяет, что попытка изменения данных без accessToken возвращает ошибку
     */
    @Test
    @Epic("User's test")
    @DisplayName("Изменение данных пользователя без авторизации")
    @Description("Запрос PATCH без авторизации должен вернуть 401 Unauthorized")
    public void updateUserWithoutAuthTest() {
        userClient.register(user).then().statusCode(SC_OK);
        User updated = User.random();
        ValidatableResponse response = userClient.update(updated, null).then();
        response.statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }
}
