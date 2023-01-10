package at.fhtw.httpserver.server;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.Method;
import at.fhtw.mtcgapp.service.packages.PackagesController;
import at.fhtw.mtcgapp.service.packages.PackagesService;
import at.fhtw.mtcgapp.service.sessions.SessionsController;
import at.fhtw.mtcgapp.service.sessions.SessionsService;
import at.fhtw.mtcgapp.service.user.UserController;
import at.fhtw.mtcgapp.service.user.UserService;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    @Test
    void testEchoServer() throws Exception {
        URL url = new URL("http://localhost:10001/echo?id=24");
        URLConnection urlConnection = url.openConnection();
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("Hello Underworld!");
        printWriter.close();
        InputStream inputStream = urlConnection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        assertEquals("Echo-Hello Underworld!", bufferedReader.readLine());

        bufferedReader.close();
    }

    @Test
    void testCreateNewUser() {
        UserService userService = new UserService(new UserController());
        Request request = new Request();

        request.setHeaderMap(new HeaderMap());
        request.setMethod(Method.POST);
        request.setPathname("/users");
        request.setBody("{\"Username\": \"admin\",\"Password\": \"admin\"}");
        request.getHeaderMap().setContentLengthHeader(String.valueOf(request.getBody().length()));
        request.getHeaderMap().setContentTypeHeader(String.valueOf(ContentType.JSON));

        Response response = userService.handleRequest(request);

        assertEquals( "{ message : \"Success\" }", response.getContent());
        assertEquals(201, response.getStatus());
    }

    @Test
    void testLoginUser() {
        SessionsService sessionsService = new SessionsService(new SessionsController());
        Request request = new Request();

        request.setHeaderMap(new HeaderMap());
        request.setMethod(Method.POST);
        request.setPathname("/sessions");
        request.setBody("{\"Username\": \"admin\",\"Password\": \"admin\"}");
        request.getHeaderMap().setContentLengthHeader(String.valueOf(request.getBody().length()));
        request.getHeaderMap().setContentTypeHeader(String.valueOf(ContentType.JSON));

        Response response = sessionsService.handleRequest(request);

        assertEquals( "admin-mtcgToken", response.getContent());
        assertEquals(200, response.getStatus());
    }

    @Test
    void testLoginUserWrongPw() {
        SessionsService sessionsService = new SessionsService(new SessionsController());
        Request request = new Request();

        request.setHeaderMap(new HeaderMap());
        request.setMethod(Method.POST);
        request.setPathname("/sessions");
        request.setBody("{\"Username\": \"admin\",\"Password\": \"wrong\"}");
        request.getHeaderMap().setContentLengthHeader(String.valueOf(request.getBody().length()));
        request.getHeaderMap().setContentTypeHeader(String.valueOf(ContentType.JSON));

        Response response = sessionsService.handleRequest(request);

        assertEquals( "{ \"message\" : \"Invalid username/password\" }", response.getContent());
        assertEquals(401, response.getStatus());
    }

    @Test
    void testGetUser() {
        UserService userService = new UserService(new UserController());
        Request request = new Request();

        request.setHeaderMap(new HeaderMap());
        request.setMethod(Method.GET);
        request.setPathname("/users/admin");
        request.getHeaderMap().setToken("admin-mtcgToken");

        Response response = userService.handleRequest(request);

        assertEquals( "{\"name\":null,\"bio\":null,\"image\":null}", response.getContent());
        assertEquals(200, response.getStatus());
    }

    @Test
    void testGetUserMissingAuth() {
        UserService userService = new UserService(new UserController());
        Request request = new Request();

        request.setHeaderMap(new HeaderMap());
        request.setMethod(Method.GET);
        request.setPathname("/users/admin");

        Response response = userService.handleRequest(request);

        assertEquals( "{\"message\" : \"Authentication information is missing or invalid\" }", response.getContent());
        assertEquals(401, response.getStatus());
    }

    @Test
    void testUpdateUser() {
        UserService userService = new UserService(new UserController());
        Request request = new Request();

        request.setHeaderMap(new HeaderMap());
        request.setMethod(Method.PUT);
        request.setPathname("/users/admin");
        request.getHeaderMap().setToken("admin-mtcgToken");
        request.setBody("{\"Name\": \"Hoax\",\"Bio\": \"me playin...\",\"Image\": \":-)\"}");
        request.getHeaderMap().setContentLengthHeader(String.valueOf(request.getBody().length()));
        request.getHeaderMap().setContentTypeHeader(String.valueOf(ContentType.JSON));

        Response response = userService.handleRequest(request);

        assertEquals( "{\"message\" : \"User updated\" }", response.getContent());
        assertEquals(200, response.getStatus());
    }

    @Test
    void testCreatePackageDoubleCards() {
        PackagesService packagesService = new PackagesService(new PackagesController());
        Request request = new Request();

        request.setHeaderMap(new HeaderMap());
        request.setMethod(Method.POST);
        request.setPathname("/packages");
        request.getHeaderMap().setToken("admin-mtcgToken");
        request.setBody("[\n" +
                "  {\n" +
                "    \"Id\": \"4fa85f64-5717-4562-b3fc-2c963f66afa6\",\n" +
                "    \"Name\": \"WaterGoblin\",\n" +
                "    \"Damage\": 55\n" +
                "  },\n" +
                "  {\n" +
                "    \"Id\": \"5fa85f64-5717-4562-b3fc-2c963f66afa6\",\n" +
                "    \"Name\": \"WaterGoblin\",\n" +
                "    \"Damage\": 55\n" +
                "  },\n" +
                "  {\n" +
                "    \"Id\": \"6fa85f64-5717-4562-b3fc-2c963f66afa6\",\n" +
                "    \"Name\": \"WaterGoblin\",\n" +
                "    \"Damage\": 55\n" +
                "  },\n" +
                "  {\n" +
                "    \"Id\": \"7fa85f64-5717-4562-b3fc-2c963f66afa6\",\n" +
                "    \"Name\": \"WaterGoblin\",\n" +
                "    \"Damage\": 55\n" +
                "  },\n" +
                "  {\n" +
                "    \"Id\": \"8fa85f64-5717-4562-b3fc-2c963f66afa6\",\n" +
                "    \"Name\": \"WaterGoblin\",\n" +
                "    \"Damage\": 55\n" +
                "  }\n" +
                "]");
        request.getHeaderMap().setContentLengthHeader(String.valueOf(request.getBody().length()));
        request.getHeaderMap().setContentTypeHeader(String.valueOf(ContentType.JSON));

        Response response = packagesService.handleRequest(request);

        assertEquals( "{ \"message\" : \"At least one card in the packages already exists\" }", response.getContent());
        assertEquals(409, response.getStatus());
    }
}