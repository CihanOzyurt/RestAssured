import POJO.ToDo;
import com.sun.codemodel.fmt.JTextFile;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class Tasks {

    /* Task 1
     * create a request to https://jsonplaceholder.typicode.com/todos/2
     * expect status 200
     * Converting Into POJO
     */

    @Test
    public void task1(){

        ToDo attirabutes =
                 given()
                        .when()
                        .get("https://jsonplaceholder.typicode.com/todos/2")
        
                        .then()
                        //.log().body()
                        .statusCode(200)
                        .extract().as(ToDo.class)
                ;
        System.out.println("attirabutes = " + attirabutes);
        System.out.println("attirabutes.getUserId() = " + attirabutes.getUserId());
        System.out.println("attirabutes.getId() = " + attirabutes.getId());
        System.out.println("attirabutes.getUnvan() = " + attirabutes.getUnvan());
        System.out.println("attirabutes.isCompleted() = " + attirabutes.isCompleted());

    }

    /*
     * Task 2
     * create a request to https://httpstat.us/203
     * expect status 203
     * expect content type TEXT
     */
    @Test
    public void task2(){
        given()
                .when()
                .get("https://httpstat.us/203")
                .then()
                .log().body()
                .statusCode(203)
                .contentType(ContentType.TEXT)
        ;
    }

    /*
     * Task 3
     * create a request to https://jsonplaceholder.typicode.com/todos/2
     * expect status 200
     * expect content type JSON
     * expect title in response body to be "quis ut nam facilis et officia qui"
     */
    @Test
    public void task3(){
        String title= //2. yöntem için extract ile elimize aldığımız veriyi bir Stringe attık
        given()
                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("title",equalTo("quis ut nam facilis et officia qui")) //1. yöntem doğrulama
                .extract().path("title") //2. yöntem için lazım

        ;
        Assert.assertEquals("quis ut nam facilis et officia qui",title); //2. yöntem doğrulama
    }

    /* Task 4
     * create a request to https://jsonplaceholder.typicode.com/todos
     * expect status 200
     * expect content type JSON
     * expect third item have:
     *      title = "fugiat veniam minus"
     *      userId = 1
     */
    @Test
    public void task4(){

        given()
                .when()
                .get("https://jsonplaceholder.typicode.com/todos")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("[2].title",equalTo("fugiat veniam minus"))
                .body("[2].userId",equalTo(1))
        ;

    }

}
