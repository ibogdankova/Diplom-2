
package clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.User;

public class UserClient extends BaseClient {

    @Step("Регистрация пользователя")
    public Response register(User user) {
        return getSpec()
                .body(user)
                .post("/api/auth/register");
    }

    @Step("Авторизация пользователя")
    public Response login(User user) {
        return getSpec()
                .body(user)
                .post("/api/auth/login");
    }

    @Step("Удаление пользователя")
    public Response delete(String accessToken) {
        return getSpecWithAuth(accessToken)
                .delete("/api/auth/user");
    }

    @Step("Изменение данных пользователя")
    public Response update(User updatedData, String accessToken) {
        return getSpecWithAuth(accessToken)
                .body(updatedData)
                .patch("/api/auth/user");
    }

    @Step("Получение информации о пользователе")
    public Response getUser(String accessToken) {
        return getSpecWithAuth(accessToken)
                .get("/api/auth/user");
    }
}
