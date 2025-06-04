package org.pedrozc90.adapters.web;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pedrozc90.adapters.web.dtos.FileStorageDto;
import org.pedrozc90.helpers.ResourceHelper;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class FilesResourceTest {

    @Inject
    protected ResourceHelper helper;

    @Test
    @DisplayName("POST /files successfully uploads image file with valid metadata")
    void uploadFileWithValidMetadata() throws IOException, URISyntaxException {
        final File file = helper.getResourceFile("files/black-labrador-3500x2095.jpg");

        given()
            .when()
            .multiPart("file", file, "image/jpeg")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .post("/files")
            .then()
            .statusCode(Response.Status.CREATED.getStatusCode())
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("uuid", notNullValue())
            .body("inserted_at", notNullValue())
            .body("updated_at", notNullValue())
            .body("version", equalTo(0))
            .body("hash", notNullValue())
            .body("filename", equalTo("black-labrador-3500x2095.jpg"))
            .body("content_type", equalTo("image/jpeg"))
            .body("charset", anything())
            .body("length", notNullValue())
            .body("space", notNullValue())
            .body("width", equalTo(3500))
            .body("height", equalTo(2095))
            .body("$", not(hasKey("content")));
    }

    @Test
    @DisplayName("GET /files/{id} returns file metadata")
    void getFileMetadata() throws IOException, URISyntaxException {
        final File file = helper.getResourceFile("files/sample.txt");

        final FileStorageDto dto = uploadFile(file, "text/plain");
        assertNotNull(dto);

        given()
            .when()
            .pathParam("id", dto.getId())
            .get("/files/{id}")
            .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(ContentType.JSON)
            .body("id", equalTo(dto.getId().intValue()))
            .body("uuid", equalTo(dto.getUuid().toString()))
            .body("inserted_at", notNullValue())
            .body("updated_at", notNullValue())
            .body("version", equalTo(dto.getVersion().intValue()))
            .body("hash", equalTo(dto.getHash()))
            .body("filename", equalTo(dto.getFilename()))
            .body("content_type", equalTo(dto.getContentType()))
            .body("charset", equalTo(dto.getCharset()))
            .body("length", equalTo(dto.getLength().intValue()))
            .body("space", notNullValue())
            .body("$", not(hasKey("content")));
    }

    private static FileStorageDto uploadFile(final File file, final String contentType) {
        return given()
            .multiPart("file", file, contentType)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .post("/files")
            .then()
            .extract()
            .jsonPath()
            .getObject("$", FileStorageDto.class);
    }

}