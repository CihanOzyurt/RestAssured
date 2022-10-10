package GoRest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GoRestUsersTests {

    @BeforeClass
    void Setup(){
        // RestAssured kendi statik değişkeni tanımlı değer atanıyor
        baseURI="https://gorest.co.in/public/v2/";
    }
    public String getRandomName()
    {
        return RandomStringUtils.randomAlphabetic(8);
    }

    public String getRandomEmail()
    {
        return RandomStringUtils.randomAlphabetic(8).toLowerCase()+"@qmail.com";
    }

    int userID=0;
    User newUser;
    @Test
    public void createUserObject()
    {
        newUser=new User();
        newUser.setName(getRandomName());
        newUser.setGender("male");
        newUser.setEmail(getRandomEmail());
        newUser.setStatus("active");

        userID=
                given()
                        // api metoduna gitmeden önceki hazırlıklar : token, gidecek body, parametleri
                        .header("Authorization","Bearer 1c7346ffa807aef7a6983df25387dc5c447546db37ebb2c970f0db94bbd524f6")
                        .contentType(ContentType.JSON)
                        .body(newUser)
                        .log().body()
                        .when()
                        .post("users") //url nin devamı

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
//                        .extract().path("id");
                        .extract().jsonPath().getInt("id")
        ;

        //path : class veya tip dönüşümüne imkan veremeyen direk veriri verir. List<String> gibi
        //jsonPath : class dönüşümüne ve tip dönüşümüne izin vererek , veriyi istediğimiz formatta verir.

        System.out.println("userID = " + userID);
    }

    @Test(dependsOnMethods = "createUserObject", priority = 1)
    public void updateUserObject()
    {
//        Map<String, String> updateUser=new HashMap<>();
//        updateUser.put("name","Cihan Ege");

        newUser.setName("Cihan Ege");

                given()
                        .header("Authorization","Bearer 1c7346ffa807aef7a6983df25387dc5c447546db37ebb2c970f0db94bbd524f6")
                        .contentType(ContentType.JSON)
                        .body(newUser)  // 69-70. satırda oluşturdugumuz updateUser yerine newUser ı kullandık.
                        .log().body()
                        .pathParam("userID",userID)

                        .when()
                        .put("users/{userID}")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .body("name",equalTo("Cihan Ege"))
                ;
    }

    @Test(dependsOnMethods = "createUserObject",priority = 2)
    public void getUserByID()
    {
        given()
                .header("Authorization","Bearer 1c7346ffa807aef7a6983df25387dc5c447546db37ebb2c970f0db94bbd524f6")
                .contentType(ContentType.JSON)
                .log().body()
                .pathParam("userID",userID)

                .when()
                .get("users/{userID}")

                .then()
                .log().body()
                .statusCode(200)
                .body("id",equalTo(userID))
        ;
    }

    @Test(dependsOnMethods = "createUserObject",priority = 3)
    public void deleteUserByID()
    {
        given()
                .header("Authorization","Bearer 1c7346ffa807aef7a6983df25387dc5c447546db37ebb2c970f0db94bbd524f6")
                .contentType(ContentType.JSON)
                .log().body()
                .pathParam("userID",userID)

                .when()
                .delete("users/{userID}")

                .then()
                .log().body()
                .statusCode(204)
        ;
    }

    @Test(dependsOnMethods = "deleteUserByID")
    public void deleteUserByIDNegative()   //çalıştıramadım bir türlü
    {
        given()
                .header("Authorization","Bearer 1c7346ffa807aef7a6983df25387dc5c447546db37ebb2c970f0db94bbd524f6")
                .contentType(ContentType.JSON)
                .log().body()
                .pathParam("userID",userID)

                .when()
                .delete("users/{userID}")

                .then()
                .log().body()
                .statusCode(404)
        ;
    }

    @Test
    public void getUsers()
    {
        Response response=
        given()
                .header("Authorization","Bearer 1c7346ffa807aef7a6983df25387dc5c447546db37ebb2c970f0db94bbd524f6")

                .when()
                .get("users")

                .then()
                .log().body()
                .statusCode(200)
                .extract().response();
        ;
        // TODO : 3. usersın id sini alınız (path ve JsonPath ile ayrı ayrı yapınız)
        //extract.path("id") -> id yi alma
        //extract.path([1].id) -> dizi halinde 1 indexli id
        int idUser3Path= response.path("[2].id");
        int idUser3JsonPath= response.jsonPath().getInt("[2].id");
        System.out.println("idUser3Path = " + idUser3Path);
        System.out.println("idUser3JsonPath = " + idUser3JsonPath);

        // TODO : Tüm gelen veriyi bir nesneye atınız
        User[] usersPath = response.as(User[].class);
        System.out.println("Arrays.toString(usersPath) = " + Arrays.toString(usersPath));

        List<User> usersJsonPath = response.jsonPath().getList("",User.class);
        System.out.println("usersJsonPath = " + usersJsonPath);
    }

    @Test
    public void getUserByIDExtract()
    {
        // TODO : GetUserByID testinde dönen user ı bir nesneye atınız.
        User user=
        given()
                .header("Authorization","Bearer 1c7346ffa807aef7a6983df25387dc5c447546db37ebb2c970f0db94bbd524f6")
                .contentType(ContentType.JSON)
                .log().body()
                .pathParam("userID",4030)

                .when()
                .get("users/{userID}")

                .then()
                .log().body()
                .statusCode(200)
                .extract().as(User.class); //4030 id noya ait tüm bilgileri alıyoruz.
        ;
        System.out.println("user = " + user);
        System.out.println("user.getName() = " + user.getName()); //burda da aldığımız tüm bilgi içinden Name çağırıyoruz.
    }

    @Test
    public void getUserByIDExtractJsonPath()
    {
        // TODO : GetUserByID testinde dönen user ı bir nesneye atınız. (JsonPath ile)
        User user=
                given()
                        .header("Authorization","Bearer 1c7346ffa807aef7a6983df25387dc5c447546db37ebb2c970f0db94bbd524f6")
                        .contentType(ContentType.JSON)
                        .log().body()
                        .pathParam("userID",4030)

                        .when()
                        .get("users/{userID}")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract().jsonPath().getObject("", User.class); //path kısmı olmadığı için boş bırakıyoruz.
        ;
        System.out.println("user = " + user);
        System.out.println("user.getName() = " + user.getName()); //burda da aldığımız tüm bilgi içinden Name çağırıyoruz.
    }

    @Test
    public void getUsersV1() {
        Response response =
                given()
                        .header("Authorization", "Bearer 1c7346ffa807aef7a6983df25387dc5c447546db37ebb2c970f0db94bbd524f6")

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract().response();

        //        response.as(); // tüm gelen response uygun nesnelerin yapılması gerekiyor.

        List<User> dataUsers = response.jsonPath().getList("data",User.class); //JsonPath farkı response içindeki
                                                                                // bir parçayı nesneye dönüştürebiliyoruz.
        System.out.println("dataUsers = " + dataUsers);

        // Daha önceki örneklerde (as) Class dönüşümleri için tüm yapıya karşılık gelen
        // gereken tüm classları yazarak dönüştürüp istediğimiz elemanlara ulaşıyorduk.
        // Burada ise(JsonPath) aradaki bir veriyi clasa dönüştürerek bir list olarak almamıza
        // imkan veren JSONPATH i kullandık.Böylece tek class ise veri alınmış oldu
        // diğer class lara gerek kalmadan

        // path : class veya tip dönüşümüne imkan veremeyen direk veriyi verir. List<String> gibi
        // jsonPath : class dönüşümüne ve tip dönüşümüne izin vererek , veriyi istediğimiz formatta verir.
    }



    @Test(enabled = false)
    public void createUser()
    {
        int userID=
                given()
                        // api metoduna gitmeden önceki hazırlıklar : token, gidecek body, parametleri

                        .header("Authorization","Bearer 1c7346ffa807aef7a6983df25387dc5c447546db37ebb2c970f0db94bbd524f6")
                        .contentType(ContentType.JSON)
                        .body("{\"name\":\""+getRandomName()+"\", \"gender\":\"male\", \"email\":\""+ getRandomEmail()+"\", \"status\":\"active\"}")

                        .when()
                        .post("users") //url nin devamı

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id");
        ;
        System.out.println("userID = " + userID);
    }

    @Test(enabled = false)
    public void createUserMap()
    {
        Map<String,String> newUser=new HashMap<>();
        newUser.put("name",getRandomName());
        newUser.put("gender","male");
        newUser.put("email",getRandomEmail());
        newUser.put("status","active");

        int userID=
                given()
                        // api metoduna gitmeden önceki hazırlıklar : token, gidecek body, parametleri

                        .header("Authorization","Bearer 1c7346ffa807aef7a6983df25387dc5c447546db37ebb2c970f0db94bbd524f6")
                        .contentType(ContentType.JSON)
                        .body(newUser) //58. satırda tanımladık
                        .log().body()
                        .when()
                        .post("users") //url nin devamı

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id");
        ;
        System.out.println("userID = " + userID);
    }

}

class User {
    private int id;
    private String name;
    private String gender;
    private String email;
    private String status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}