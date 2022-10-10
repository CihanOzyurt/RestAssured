import POJO.Location;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ZippoTest {


    @Test
    public void test() {

        given()
                    // hazırlık işlemleri yapacağız (token, send body, parametreler
                .when()
                    // link i ve metodu veriyoruz
                .then()
                    // assertion ve verileri ele alma extract
        ;
    }

    @Test
    public void statusCodeTest() {

        given()

                .when()
                .get("https://api.zippopotam.us/us/90210")

                .then()
                .log().body()  // log.all() bütün response u gösterir
                .statusCode(200) // status kontrolü
        ;
    }

    @Test
    public void contentTypeTest() {

        given()

                .when()
                .get("https://api.zippopotam.us/us/90210")

                .then()
                .log().body()  // log.all() bütün response u gösterir
                .statusCode(200) // status kontrolü
                .contentType(ContentType.JSON) // JSON yerine TEXT vererek hatalı durum kontrolü yap.
        ;
    }

    @Test
    public void checkStateInResponseBody() {

        given()

                .when()
                .get("https://api.zippopotam.us/us/90210")

                .then()
                .log().body()  // log.all() bütün response u gösterir
                .body("country", equalTo("United States")) // body.country == United States  ??
                .statusCode(200)
        ;
    }
// body.country  -> body("country",
// body.'post code'  -> body("post code",
//body.'country abbreviation' -> body("country abbreviation"
//body.places[0].'place name' -> body("body.places[0].'place name'"
//body.places[0].state -> body("places[0].state"

    @Test
    public void bodyJsonPathTest2() {

        given()

                .when()
                .get("https://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("places[0].state", equalTo("California")) // body.country == United States  ??
                .statusCode(200)                                         //yani birebir eşit mi?
        ;
    }

    @Test
    public void bodyJsonPathTest3() {

        given()

                .when()
                .get("https://api.zippopotam.us/tr/01000") //url değişti

                .then()
                .log().body()
                //place.state -> places deki tüm elemanların state lerini bir list olarak verir.
                .body("places.'place name'", hasItem("Çaputçu Köyü"))
                // "places.'place name'" bu bilgiler "Çaputçu Köyü" bu item a sahip mi ??
                //hasItem listenin içinde "Çaputçu Köyü" elemanı var mı diye arıyor.
                //equalTo kullanmak isteseydik "places[5].'place name'" olarak sorgulama yapmalıydık.
                .statusCode(200)
        ;
    }

    @Test
    public void bodyArrayHasSizeTest() {

        given()

                .when()
                .get("https://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("places", hasSize(1)) // verilen path deki listin size kontrolü
                .statusCode(200)
        ;
    }

    @Test
    public void combiningTest() {

        given()

                .when()
                .get("https://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("places", hasSize(1)) // verilen path deki listin size kontrolü
                .body("places.state", hasItem("California"))
                .body("places[0].'place name'", equalTo("Beverly Hills"))
                .statusCode(200)
        ;
    }

    @Test
    public void pathParamTest() {

        given()
                .pathParam("Country","us")
                .pathParam("Zipkod",90210)
                .log().uri() //request linki
                .when()                             //us     //90210   değerleri gelecek
                .get("https://api.zippopotam.us/{Country}/{Zipkod}")

                .then()
                .log().body()

                .statusCode(200)
        ;
    }

    @Test
    public void pathParamTest2() {
        // 90210 dan 90250 ye kadar test sonuçlarında places size hepsinde 1 geldiğini test ediniz

            for (int i = 90210; i <= 90213; i++) {

                given()
                        .pathParam("Country", "us")
                        .pathParam("Zipkod", i)
                        .log().uri() //request linki
                        .when()                             //us     //i   değerleri gelecek
                        .get("https://api.zippopotam.us/{Country}/{Zipkod}")

                        .then()
                        .body("places", hasSize(1))
                        .log().body()
                        .statusCode(200)
                ;
            }
    }

    @Test
    public void queryParamTest() {

        //https://gorest.co.in/public/v1/users?page=1
        given()

                .param("page",1)
                .log().uri() //request linki

                .when()                                     //alt satırdaki linkin sonuna ?page=1 gelecek
                .get("https://gorest.co.in/public/v1/users")

                .then()
                .log().body()
                .body("meta.pagination.page",equalTo(1))

                .statusCode(200)
        ;
    }

    @Test
    public void queryParamTestFor10Page() {

        //https://gorest.co.in/public/v1/users?page=1
        for (int pageNo = 1; pageNo <=10 ; pageNo++) {
            given()
                    .param("page", pageNo)
                    .log().uri() //request linki

                    .when()                                     //alt satırdaki linkin sonuna ?page=1 gelecek
                    .get("https://gorest.co.in/public/v1/users")

                    .then()
                    .log().body()
                    .body("meta.pagination.page", equalTo(pageNo))
                    .statusCode(200)
            ;
        }
    }

    RequestSpecification requestSpecs;
    ResponseSpecification responseSpecs;

    @BeforeClass
    void Setup() {

        baseURI ="https://gorest.co.in/public/v1";
        requestSpecs = new RequestSpecBuilder()
                .log(LogDetail.URI)
                .setAccept(ContentType.JSON)
                .build();

        responseSpecs = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.BODY)
                .build();


    }

    @Test
    public void requestResponseSpecifications() {

        //https://gorest.co.in/public/v1/users?page=1
        given()

                .param("page",1)
                .spec(requestSpecs)

                .when()
                .get("/users") //url in başında http yoksa baseUri deki değer otomatik geliyor.

                .then()
                .body("meta.pagination.page",equalTo(1))
                .spec(responseSpecs)
        ;
    }
    // Json extract
    @Test
    public void extractingJsonPath() {

        String placeName=    //dönecek olan veri String mi int mi ne ise ona göre tip yazılmalı.
        given()

                .when()
                .get("https://api.zippopotam.us/us/90210")

                .then()
                //.log().body() kalabalık yapmasın diye // ile kapadık.
                .statusCode(200)
                .extract().path("places[0].'place name'")
                //exract metodu ile given ile başlayan satır, bir değer döndürür hale geldi.
                //extract en sonda satırda olmalı, yoksa hata verecektir.
        ;
        System.out.println("placeName = " + placeName);
    }

    @Test
    public void extractingJsonPathInt() {
        int limit=
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
//                        .log().body()
                        .statusCode(200)
                        .extract().path("meta.pagination.limit")

                ;
        System.out.println("limit = " + limit);
        Assert.assertEquals(limit,10,"test sonucu");
    }

    @Test
    public void extractingJsonPathInt2() {
        int id=
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
//                        .log().body()
                        .statusCode(200)
                        .extract().path("data[2].id")

                ;
        System.out.println("id = " + id);

    }

    @Test
    public void extractingJsonPathList() {
        List<Integer> idler=
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
//                        .log().body()
                        .statusCode(200)
                        .extract().path("data.id") // data daki bütün idleri List şeklinde verir

                ;
        System.out.println("idler = " + idler);
        Assert.assertTrue(idler.contains(5199));

    }

    @Test
    public void extractingJsonPathStringList() {

        List<String> names=
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
//                        .log().body()
                        .statusCode(200)
                        .extract().path("data.name") // data daki bütün nameleri List şeklinde verir
                ;
        System.out.println("names = " + names);
        Assert.assertTrue(names.contains("Aishani Butt"));

    }

    @Test
    public void extractingJsonPathResponseAll() {

        Response body=
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
//                        .log().body()
                        .statusCode(200)
                        .extract().response() // bütün body alındı
                ;


        List<Integer> idler=body.path("data.id");
        List<String> isimler=body.path("data.name");
        int limit=body.path("meta.pagination.limit");

        System.out.println("limit = " + limit);
        System.out.println("isimler = " + isimler);
        System.out.println("idler = " + idler);
    }

    @Test
    public void extractingJsonPOJO() { //POJO : JSON object (Plain Old Java Object)

        Location yer =
                given()

                .when()
                .get("https://api.zippopotam.us/us/90210")

                .then()
                .extract().as(Location.class); // Location şablonu
        ;

        System.out.println("yer = " + yer);

        System.out.println("yer.getCountry() = " + yer.getCountry());
        System.out.println("yer.getPlaces().get(0).getPlacename() = " +
                yer.getPlaces().get(0).getPlacename());
    }



}
