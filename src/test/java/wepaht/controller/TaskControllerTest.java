package wepaht.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import wepaht.Application;
import wepaht.domain.Database;
import wepaht.domain.Task;
import wepaht.repository.DatabaseRepository;
import wepaht.repository.TaskRepository;
import java.util.List;

import static org.junit.Assert.assertTrue;
import org.springframework.test.web.servlet.MvcResult;
import wepaht.service.DatabaseService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(value = SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class TaskControllerTest {

    private final String API_URI = "/tasks";

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private DatabaseRepository databaseRepository;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    private Task randomTask() {
        Task task = new Task();
        task.setName(RandomStringUtils.randomAlphanumeric(10));
        task.setDescription(RandomStringUtils.randomAlphabetic(30));
        task.setStatus("Uncomplete");
        return task;
    }

    @Test
    public void statusIsOkTest() throws Exception {
        mockMvc.perform(get(API_URI))
                .andExpect(status().isOk());
    }

    @Test
    public void createQuery() throws Exception {
        Task task = randomTask();
        task = taskRepository.save(task);

        String query ="Jee";          
        mockMvc.perform(post(API_URI + "/" + task.getId() + "/query").param("query", query).param("id",""+ task.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/tasks/{id}"))
                .andExpect(flash().attributeExists("messages"))
                .andReturn();

    }

    @Test
    public void createTask() throws Exception {
        databaseService.createDatabase("Huee", "CREATE TABLE Foo(id integer); INSERT INTO Foo (id) VALUES (7);");
        Database database = databaseRepository.findByName("Huee").get(0);

        String taskName = "testTask";
        mockMvc.perform(post(API_URI).param("name", taskName)
                                        .param("description", "To test creation of a task with a database")
                                        .param("databaseId", database.getId().toString()))
                        .andExpect(status().is3xxRedirection())
                        .andReturn();

        List<Task> tasks = taskRepository.findAll();

        assertTrue(tasks.stream().filter(task -> task.getName().equals(taskName)).findFirst().isPresent());
    }
}