package wepaht.SQLTasker.controller;

import wepaht.SQLTasker.controller.DatabaseController;
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
import wepaht.SQLTasker.Application;
import wepaht.SQLTasker.domain.Database;
import wepaht.SQLTasker.repository.DatabaseRepository;
import wepaht.SQLTasker.repository.LocalAccountRepository;
import wepaht.SQLTasker.service.DatabaseService;
import java.util.List;

import static org.junit.Assert.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import wepaht.SQLTasker.service.AccountService;


@RunWith(value = SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class DatabaseControllerTest {

    private final String API_URI = "/databases";

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private DatabaseRepository dbRepository;

    @Autowired
    private DatabaseService dbService;

    @Autowired
    private LocalAccountRepository userRepository;
    
    @Mock
    AccountService userServiceMock;
    
    @InjectMocks
    DatabaseController databaseController;

    private MockMvc mockMvc;
    private Database testdatabase = null;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).apply(springSecurity()).build();

        dbService.createDatabase("testDatabase4", "CREATE TABLE Persons"
                + "(PersonID int, LastName varchar(255), FirstName varchar(255), Address varchar(255), City varchar(255));"
                + "INSERT INTO PERSONS (PERSONID, LASTNAME, FIRSTNAME, ADDRESS, CITY)"
                + "VALUES (2, 'Raty', 'Matti', 'Rautalammintie', 'Helsinki');"
                + "INSERT INTO PERSONS (PERSONID, LASTNAME, FIRSTNAME, ADDRESS, CITY)"
                + "VALUES (1, 'Jaaskelainen', 'Timo', 'Jossakin', 'Heslinki');"
                + "INSERT INTO PERSONS (PERSONID, LASTNAME, FIRSTNAME, ADDRESS, CITY)"
                + "VALUES (3, 'Entieda', 'Kake?', 'Laiva', 'KJYR');");
        testdatabase = dbRepository.findByNameAndDeletedFalse("testDatabase4").get(0);
    }

    @Test
    public void statusIsOkTest() throws Exception{
        mockMvc.perform(get(API_URI).with(user("user")))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    public void createDatabaseTest() throws Exception {
        String dbName = "suchDB";
        String dbSchema = "CREATE TABLE WOW(id integer);" +
                            "INSERT INTO WOW (id) VALUES (7);";

        mockMvc.perform(post(API_URI).param("name", dbName).param("databaseSchema", dbSchema).with(user("user").roles("ADMIN")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("messages"))
                .andReturn();

        List<Database> databases = dbRepository.findByNameAndDeletedFalse(dbName);

        assertTrue(databases.stream().filter(db -> db.getDatabaseSchema().equals(dbSchema)).findFirst().isPresent());
    }

    @Test
    public void viewDatabaseContainsTables() throws Exception {
        mockMvc.perform(get(API_URI + "/" + testdatabase.getId()).with(user("user")))
                .andExpect(model().attributeExists("tables"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void createdDatabaseNameIsNotEmpty() throws Exception{
        String dbName = "";
        String dbSchema = "CREATE TABLE NOTEMPTYTEST(id integer);" +
                            "INSERT INTO NOTEMPTYTEST (id) VALUES (7);";

        mockMvc.perform(post(API_URI)
                .param("name", dbName)
                .param("databaseSchema", dbSchema)
                .with(user("user").roles("ADMIN")).with(csrf()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(flash().attributeExists("messages"))
                .andReturn();

        List<Database> databases = dbRepository.findAll();

        assertFalse(databases.stream().filter(db -> db.getDatabaseSchema().equals(dbSchema)).findFirst().isPresent());
    }
    
    @Test
    public void createdDatabaseNameIsNotNull() throws Exception{
        String dbSchema = "CREATE TABLE NOTNULLTEST(id integer);" +
                            "INSERT INTO NOTNULLTEST (id) VALUES (7);";

        mockMvc.perform(post(API_URI)
                .param("databaseSchema", dbSchema)
                .with(user("user").roles("ADMIN")).with(csrf()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(flash().attributeExists("messages"))
                .andReturn();

        List<Database> databases = dbRepository.findAll();

        assertFalse(databases.stream().filter(db -> db.getDatabaseSchema().equals(dbSchema)).findFirst().isPresent());
    }
    
    @Test
    public void testAnyAuthenticatedCanCreateDatabase() throws Exception {
        String dbName = "otherDB";
        String dbSchema = "CREATE TABLE Hii(id integer);" +
                            "INSERT INTO Hii (id) VALUES (7);";        

        mockMvc.perform(post(API_URI)
                .param("name", dbName)
                .param("databaseSchema", dbSchema)
                .with(user("student").roles("STUDENT")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("messages"))
                .andReturn();

        List<Database> databases = dbRepository.findByNameAndDeletedFalse(dbName);

        assertTrue(databases.stream().filter(db -> db.getDatabaseSchema().equals(dbSchema)).findFirst().isPresent());
    }
}
