package api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class ReqresTest {
    private final static String URL = "https://reqres.in/";
    @Test
    public void checkAvatarAndId(){
        Specifications.installSpecification(Specifications.requestSpecification(URL), Specifications.responseSpecificationOK200());
        List<UserData> users = given()
                .when()
                .get("api/users?page=2")
                .then().log().all()
                .extract().body().jsonPath().getList("data",UserData.class);

        users.forEach(x-> Assertions.assertTrue(x.getAvatar().contains(x.getId().toString())));
        Assertions.assertTrue(users.stream().allMatch(x->x.getEmail().endsWith("@reqres.in")));

        List<String> avatars = users.stream().map(UserData::getAvatar).collect(Collectors.toList());
        List<String> ids = users.stream().map(x->x.getId().toString()).collect(Collectors.toList());
        for (int i=0; i<avatars.size(); i++){
           Assertions.assertTrue(avatars.get(i).contains(ids.get(i)));
        }
    }

    @Test
    public void successfulRegistration(){
        Specifications.installSpecification(Specifications.requestSpecification(URL), Specifications.responseSpecificationOK200());
        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";
        Register user = new Register("eve.holt@reqres.in", "pistol");
        SuccessReg successReg = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(SuccessReg.class);
        Assertions.assertNotNull(successReg.getId());
        Assertions.assertNotNull(successReg.getToken());
        Assertions.assertEquals(id, successReg.getId());
        Assertions.assertEquals(token, successReg.getToken());
    }

    @Test
    public void unsuccessfulRegistration(){
        Specifications.installSpecification(Specifications.requestSpecification(URL), Specifications.responseSpecificationError400());
        Register user = new Register("sydney@fife", "");
        UnsuccessReg unsuccessReg = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(UnsuccessReg.class);
        Assertions.assertEquals("Missing password", unsuccessReg.getError());
    }

    @Test
    public void sortedByYearsColorsList(){
        Specifications.installSpecification(Specifications.requestSpecification(URL), Specifications.responseSpecificationOK200());
        List<ColorsData> colors = given()
                .when()
                .get("api/unknown")
                .then().log().all()
                .extract().body().jsonPath().getList("data", ColorsData.class);
        List<Integer> years = colors.stream().map(ColorsData::getYear).collect(Collectors.toList());
        List<Integer> sortedYears = years.stream().sorted().collect(Collectors.toList());
        Assertions.assertEquals(sortedYears, years);
    }

    @Test
    public void deleteUser(){
        Specifications.installSpecification(Specifications.requestSpecification(URL), Specifications.responseSpecificationStatus(204));
        given()
                .when()
                .delete("api/users/2")
                .then().log().all();
    }

    @Test
    public void timeUpdateTest(){
        Specifications.installSpecification(Specifications.requestSpecification(URL), Specifications.responseSpecificationStatus(200));
        UserTime user = new UserTime("morpheus", "zion resident");
        UserTimeResponse response = given()
                .body(user)
                .when()
                .put("api/users/2")
                .then().log().all()
                .extract().as(UserTimeResponse.class);
        String regex = "\\..*$";
        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex, "");
        Assertions.assertEquals(currentTime, response.getUpdatedAt().replaceAll(regex, ""));
    }

}
