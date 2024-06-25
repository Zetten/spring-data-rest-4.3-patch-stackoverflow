package com.example.demo;

import com.example.demo.entities.Basket;
import com.example.demo.entities.TaskConfig;
import com.example.demo.entities.TaskDefinition;
import com.example.demo.entities.TaskOutput;
import com.example.demo.repositories.BasketRepository;
import com.example.demo.repositories.TaskConfigRepository;
import com.example.demo.repositories.TaskDefinitionRepository;
import com.jayway.jsonpath.JsonPath;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class DemoApplicationTests {

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private TaskConfigRepository taskConfigRepository;

    @Autowired
    private TaskDefinitionRepository taskDefinitionRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void patchTaskConfigOutputs() throws Exception {

        /*******************************/
        /* Save pre-requisite entities */
        /*******************************/

        Basket basket = basketRepository.saveAndFlush(new Basket(null, "my-task-outputs", new ArrayList<>()));
        TaskDefinition taskDefinition = taskDefinitionRepository.saveAndFlush(
                new TaskDefinition(null,
                        "my-task-definition",
                        new ArrayList<>(),
                        new ArrayList<>(List.of("first-output", "second-output"))
                )
        );

        TaskConfig taskConfig = new TaskConfig(null,
                taskDefinition,
                new ArrayList<>(List.of("some-input", "some-other-input")),
                new ArrayList<>()
        );
        TaskOutput taskOutput = new TaskOutput(null, "first-output", taskConfig, new HashSet<>());
        taskOutput.addCollector(basket, false);
        taskConfig.addOutput(taskOutput);
        taskConfig = taskConfigRepository.saveAndFlush(taskConfig);

        /********************************************/
        /* Check REST state and get URL for testing */
        /********************************************/

        String basketsJson = mockMvc.perform(get("/baskets"))
                .andReturn().getResponse().getContentAsString();
        String basketUrl = JsonPath.read(basketsJson, "$._embedded.baskets[0]._links.self.href");

        String taskConfigsJson = mockMvc.perform(get("/taskConfigs"))
                .andReturn().getResponse().getContentAsString();
        String taskConfigUrl = JsonPath.read(taskConfigsJson, "$._embedded.taskConfigs[0]._links.self.href");

        mockMvc.perform(get(taskConfigUrl))
                .andExpectAll(
                        jsonPath("$.outputs.length()", is(1)),
                        jsonPath("$.outputs[0].outputIdentifier", is("first-output")),
                        jsonPath("$.outputs[0].collectors[0].replaceContents", is(false))
                );

        /************************************************************/
        /* Patch the TaskConfig.outputs to enable `replaceContents` */
        /************************************************************/

        mockMvc.perform(patch(taskConfigUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "outputs": [{
                                          "outputIdentifier": "first-output",
                                          "collectors": [{
                                              "basket": "%s",
                                              "replaceContents": true
                                          }]
                                      }]
                                    }
                                """.formatted(basketUrl)))
                .andExpect(status().is2xxSuccessful());

        /************************************************************/
        /* Patch the TaskConfig.outputs to enable `replaceContents` */
        /************************************************************/

        mockMvc.perform(get(taskConfigUrl))
                .andExpectAll(
                        jsonPath("$.outputs.length()", is(1)),
                        jsonPath("$.outputs[0].outputIdentifier", is("first-output")),
                        jsonPath("$.outputs[0].collectors[0].replaceContents", is(true))
                );
    }

}
