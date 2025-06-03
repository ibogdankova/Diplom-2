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

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.*;

/**
 * Тесты получения заказов пользователя
 * Проверяются: доступ авторизованного и неавторизованного пользователя
 */
public class UserOrdersTest {

    private UserClient userClient;
    private OrderClient orderClient;
    private String token;

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
    }

    @After
    public void tearDown() {
        if (token != null && !token.isEmpty()) {
            userClient.delete(token);
        }
    }

    /**
     * Проверяет, что авторизованный пользователь может получить список своих заказов
     */
    @Test
    @Epic("Order's test")
    @DisplayName("Получение заказов авторизованным пользователем")
    @Description("Проверка успешного получения заказов, если передан accessToken")
    public void getOrdersWithAuthorizationTest() {
        // регистрируем и создаём заказ
        token = userClient.register(User.random())
                .then().statusCode(SC_OK)
                .extract().path("accessToken");

        Order order = new Order(Arrays.asList("61c0c5a71d1f82001bdaaa6d"));
        orderClient.create(order, token).then().statusCode(SC_OK);

        // получаем список заказов
        ValidatableResponse response = orderClient.getOrders(token).then();
        response.statusCode(SC_OK)
                .body("success", is(true))
                .body("orders", not(empty()));
    }

    /**
     * Проверяет, что без авторизации получить заказы нельзя
     */
    @Test
    @Epic("Order's test")
    @DisplayName("Попытка получения заказов без авторизации")
    @Description("Если не передан accessToken, сервер возвращает ошибку 401")
    public void getOrdersWithoutAuthorizationTest() {
        ValidatableResponse response = orderClient.getOrdersWithoutAuth().then();
        response.statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }
}
