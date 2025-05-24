package ro.ticle.paula.spring.project.documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import ro.ticle.paula.spring.project.integration.BaseControllerIntegrationTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GenerateSwaggerTest extends BaseControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void generateSwaggerJson() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/v3/api-docs")
                        .with(csrf())
                        .with(user("testuser").roles("ADMIN")))
                .andExpect(status().isOk())
                .andReturn();

        String swaggerJson = mvcResult.getResponse().getContentAsString();

        // Pretty print JSON
        Object jsonObject = objectMapper.readValue(swaggerJson, Object.class);
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);

        // Create docs directory if it doesn't exist
        Path docsDir = Paths.get("docs");
        if (!Files.exists(docsDir)) {
            Files.createDirectories(docsDir);
        }

        // Save to file
        Path path = docsDir.resolve("swagger.json");
        Files.write(path, prettyJson.getBytes());
    }
} 