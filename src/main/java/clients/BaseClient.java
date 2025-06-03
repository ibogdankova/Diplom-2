
package clients;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class BaseClient {

    protected RequestSpecification getSpec() {
        return RestAssured
                .given()
                .baseUri("https://stellarburgers.nomoreparties.site")
                .header("Content-Type", "application/json");
    }

    protected RequestSpecification getSpecWithAuth(String token) {
        return getSpec().header("Authorization", token);
    }
}
