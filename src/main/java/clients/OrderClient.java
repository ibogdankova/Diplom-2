
package clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Order;

public class OrderClient extends BaseClient {

    @Step("Создание заказа")
    public Response create(Order order, String token) {
        return getSpecWithAuth(token)
                .body(order)
                .post("/api/orders");
    }

    @Step("Создание заказа без авторизации")
    public Response createWithoutAuth(Order order) {
        return getSpec()
                .body(order)
                .post("/api/orders");
    }

    @Step("Получение заказов пользователя")
    public Response getOrders(String token) {
        return getSpecWithAuth(token)
                .get("/api/orders");
    }

    @Step("Получение всех заказов без авторизации")
    public Response getOrdersWithoutAuth() {
        return getSpec()
                .get("/api/orders");
    }
}
