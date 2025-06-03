import clients.OrderClient;
import clients.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.Order;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.core.Is.is;

/**
 * Тесты создания заказов через API
 * Проверяются все сценарии: с/без авторизации, с/без ингредиентов, с ошибками
 */
public class OrderCreateTest {

    private UserClient userClient;
    private OrderClient orderClient;
    private String token;
    private Order order;

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
        order = new Order(Arrays.asList("61c0c5a71d1f82001bdaaa6d"));
    }

    @After
    public void tearDown() {
        if (token != null && !token.isEmpty()) {
            userClient.delete(token);
        }
    }

    /**
     * Проверка создания заказа с авторизацией и валидными ингредиентами
     */
    @Test
    @Epic("Order's test")
    @DisplayName("Создание заказа с авторизацией")
    @Description("Пользователь с токеном может создать заказ")
    public void createOrderWithAuthTest() {
        token = userClient.register(User.random())
                .then().statusCode(SC_OK)
                .extract().path("accessToken");

        ValidatableResponse response = orderClient.create(order, token).then();
        response.statusCode(SC_OK).body("success", is(true));
    }

    /**
     * Проверка создания заказа без авторизации
     */
    @Test
    @Epic("Order's test")
    @DisplayName("Создание заказа без авторизации")
    @Description("Сервер должен позволять создавать заказы без токена")
    public void createOrderWithoutAuthTest() {
        ValidatableResponse response = orderClient.createWithoutAuth(order).then();
        response.statusCode(SC_OK).body("success", is(true));
    }

    /**
     * Проверка ошибки при создании заказа без ингредиентов
     */
    @Test
    @Epic("Order's test")
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Сервер должен возвращать 400, если список ингредиентов пуст")
    public void createOrderWithoutIngredientsTest() {
        token = userClient.register(User.random())
                .then().statusCode(SC_OK)
                .extract().path("accessToken");

        Order emptyOrder = new Order(Collections.emptyList());
        ValidatableResponse response = orderClient.create(emptyOrder, token).then();
        response.statusCode(SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));
    }

    /**
     * Проверка ошибки при создании заказа с невалидным хешем ингредиента
     */
    @Test
    @Epic("Order's test")
    @DisplayName("Создание заказа с невалидным ингредиентом")
    @Description("Сервер должен вернуть 500 Internal Server Error при некорректном ID ингредиента")
    public void createOrderWithInvalidIngredientTest() {
        token = userClient.register(User.random())
                .then().statusCode(SC_OK)
                .extract().path("accessToken");

        // передаём невалидный ингредиент
        Order badOrder = new Order(Arrays.asList("invalid_ingredient_hash"));

        ValidatableResponse response = orderClient.create(badOrder, token).then();
        response.statusCode(SC_INTERNAL_SERVER_ERROR); // ожидаем 500
    }
}