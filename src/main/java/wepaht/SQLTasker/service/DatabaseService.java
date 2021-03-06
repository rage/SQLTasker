package wepaht.SQLTasker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wepaht.SQLTasker.domain.Database;
import wepaht.SQLTasker.domain.Table;
import wepaht.SQLTasker.repository.DatabaseRepository;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TmcAccount;
import static wepaht.SQLTasker.constant.ConstantString.ATTRIBUTE_MESSAGES;
import static wepaht.SQLTasker.constant.ConstantString.MESSAGE_UNAUTHORIZED_ACCESS;
import static wepaht.SQLTasker.constant.ConstantString.MESSAGE_UNAUTHORIZED_ACTION;
import static wepaht.SQLTasker.constant.ConstantString.REDIRECT_DEFAULT;
import static wepaht.SQLTasker.constant.ConstantString.ROLE_STUDENT;
import static wepaht.SQLTasker.constant.ConstantString.VIEW_DATABASES;

@Service
public class DatabaseService {

    @Autowired
    private DatabaseRepository databaseRepository;

    @Autowired
    private AccountService accountService;

    private HashSet<String> defaultTables = new HashSet<>();

    @PostConstruct
    private void init() {
        defaultTables.addAll(Arrays.asList(
                "CATALOGS",
                "COLLATIONS",
                "COLUMNS",
                "COLUMN_PRIVILEGES",
                "CONSTANTS",
                "CONSTRAINTS",
                "CROSS_REFERENCES",
                "DOMAINS",
                "FUNCTION_ALIASES",
                "FUNCTION_COLUMNS",
                "HELP",
                "INDEXES",
                "IN_DOUBT",
                "LOCKS",
                "QUERY_STATISTICS",
                "RIGHTS",
                "ROLES",
                "SCHEMATA",
                "SEQUENCES",
                "SESSIONS",
                "SESSION_STATE",
                "SETTINGS",
                "TABLES",
                "TABLE_PRIVILEGES",
                "TABLE_TYPES",
                "TRIGGERS",
                "TYPE_INFO",
                "USERS",
                "VIEWS"));
    }

    public boolean createDatabase(String name, String createTable) {
        try {
            Database db = new Database();
            TmcAccount user = null;
            try {
                user = accountService.getAuthenticatedUser();
            } catch (Exception e) {
                System.out.println("No authenticated user. Setting null as database " + name + " owner");
            }

            db.setName(name);
            db.setDatabaseSchema(createTable);
            db.setOwner(user);

            //Testing the connection
            Connection conn = createConnectionToDatabase(name, createTable);
            conn.close();

            System.out.println(name + " table-schema is valid");

            databaseRepository.save(db);

            System.out.println(name + " created");

            return true;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return false;
    }

    /**
     * Lists tables of a single Database-entity. Example of use found in
     * database.html-resource file and DatabaseController.
     *
     * @param databaseId ID of selected database
     * @param updateQuery teehee
     * @return A map in which String-object indicates the name of certain table,
     * and Table contains its' columns and rows in separate lists. In case of
     * broken database, the only returned table name is "ERROR".
     */
    public Map<String, Table> performUpdateQuery(Long databaseId, String updateQuery) {
        HashMap<String, Table> listedDatabase = new HashMap<>();
        Database database = databaseRepository.findOne(databaseId);
        Connection connection = null;
        String query = database.getDatabaseSchema();

        if (updateQuery != null) {
            query += updateQuery;
        }

        try {
            connection = createConnectionToDatabase(database.getName(), query);

            List<String> tables = listDatabaseTables(connection);

            final Connection finalConnection = connection;
            tables.parallelStream().forEach(tableName -> {
                Table table = new Table(tableName);
                try {
                    table.setColumns(listTableColumns(tableName, finalConnection));
                    table.setRows(listTableRows(tableName, table.getColumns(), finalConnection));
                } catch (Exception e) {
                }
                listedDatabase.put(tableName, table);
            });

            finalConnection.close();
        } catch (Exception e) {
            listedDatabase.put("ERROR", null);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }

        return listedDatabase;
    }

    /**
     * Performs a query in the selected database.
     *
     * @param databaseId ID of the selected database
     * @param sqlQuery the query. Syntax must be correct in order this to work!
     * @return a table-object, which contains separately its' columns and rows.
     * In case of sql error, returned table will be named as the exception.
     */
    public Map<String, Table> performQuery(Long databaseId, String sqlQuery) {

        if (isUpdateQuery(sqlQuery)) {
            return performUpdateQuery(databaseId, sqlQuery);
        }

        Map<String, Table> queryResult = new HashMap<>();
        Table table = new Table("Response");
        Database database = databaseRepository.findOne(databaseId);
        Statement statement = null;
        Connection connection = null;

        try {
            connection = createConnectionToDatabase(database.getName(), database.getDatabaseSchema());
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            table.setColumns(listQueryColumns(resultSet));
            table.setRows(listQueryRows(resultSet, table.getColumns()));
            queryResult.put("Response", table);
        } catch (Exception e) {
            table.setColumns(Arrays.asList("Error"));
            table.setRows(Arrays.asList(Arrays.asList(e.toString())));
            queryResult.put("Response", table);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }

        return queryResult;
    }

    public boolean isValidQuery(Database database, String sqlQuery) {
        Statement statement = null;
        Connection connection = null;
        Boolean isValid = true;

        try {
            if (isUpdateQuery(sqlQuery)) {
                connection = createConnectionToDatabase(database.getName(), database.getDatabaseSchema() + sqlQuery);
            } else {
                connection = createConnectionToDatabase(database.getName(), database.getDatabaseSchema());
                statement = connection.createStatement();
                statement.executeQuery(sqlQuery);
            }
        } catch (Exception e) {
            isValid = false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }

        return isValid;
    }

    private boolean isUpdateQuery(String sql) {
        sql = sql.toUpperCase();
        if (sql.contains("INSERT") || sql.contains("CREATE") || sql.contains("DROP") || sql.contains("UPDATE") || sql.contains("DELETE") || sql.contains("ALTER TABLE")) {
            return true;
        }

        return false;
    }

    private List<List<String>> listQueryRows(ResultSet resultSet, List<String> columns) throws Exception {
        List<List<String>> rows = new ArrayList<>();

        while (resultSet.next()) {
            List<String> row = new ArrayList<>();
            for (String column : columns) {
                row.add(resultSet.getString(column));
            }
            rows.add(row);
        }

        return rows;
    }

    private List<String> listQueryColumns(ResultSet resultSet) throws Exception {
        HashSet<String> columns = new HashSet<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int numberOfColumns = metaData.getColumnCount();

        for (int i = 1; i < numberOfColumns + 1; i++) {
            String columnName = metaData.getColumnLabel(i);
            columns.add(columnName);
        }

        return new ArrayList<String>(columns);
    }

    private List<String> listDatabaseTables(Connection connection) throws Exception {
        ArrayList<String> tables = new ArrayList<>();

        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getTables(null, null, "%", null);

        while (resultSet.next()) {
            String tableName = resultSet.getString(3);

            if (!defaultTables.contains(tableName)) {
                tables.add(tableName);
            }
        }

        return tables;
    }

    private List<String> listTableColumns(String tableName, Connection connection) throws Exception {
        ArrayList<String> columns = new ArrayList<>();

        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getColumns(null, null, tableName, null);

        while (resultSet.next()) {
            String columnName = resultSet.getString(4);

            columns.add(columnName);
        }

        return columns;
    }

    private List<List<String>> listTableRows(String tableName, List<String> columns, Connection connection) throws Exception {
        List<List<String>> rows = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + tableName + ";";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(selectQuery);

        while (resultSet.next()) {
            ArrayList<String> row = new ArrayList<>();

            for (String column : columns) {
                row.add(resultSet.getString(column));
            }

            rows.add(row);
        }

        return rows;
    }

    private Connection createConnectionToDatabase(String databaseName, String sql) throws Exception {
        Class.forName("org.h2.Driver");
        String url = "jdbc:h2:mem:" + databaseName;
        String user = "lol";
        String pwds = "hah";

        Connection conn = DriverManager.getConnection(url, user, pwds);

        Statement statement = conn.createStatement();
        try {
            int result = statement.executeUpdate(sql);
        } catch (Exception e) {
            conn.close();
            return null;
        }

        return conn;
    }

    @Transactional
    public Boolean deleteDatabaseById(Long databaseId) {
        try {
            Database database = databaseRepository.findOne(databaseId);
            database.setDeleted(true);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Database getDatabase(Long id) {
        return databaseRepository.findOne(id);
    }

    public Database getDatabaseByName(String name) {
        return databaseRepository.findByNameAndDeletedFalse(name).get(0);
    }

    public String listDatabases(Model model) {
        TmcAccount owner = accountService.getAuthenticatedUser();
        if (owner.getRole().equals(ROLE_STUDENT)) {
            model.addAttribute("databases", databaseRepository.findByOwnerAndDeletedFalse(owner));
        } else {
            model.addAttribute("databases", databaseRepository.findAll());
        }
        return VIEW_DATABASES;
    }

    @Transactional
    public String deleteDatabase(RedirectAttributes redirAttr, Long databaseId) {
        String redirectAddress = "redirect:/databases";
        TmcAccount user = accountService.getAuthenticatedUser();
        Database db = databaseRepository.findOne(databaseId);
        if (user.getRole().equals(ROLE_STUDENT) && (db == null || db.getOwner() == null || !db.getOwner().equals(user))) {
            redirAttr.addFlashAttribute(ATTRIBUTE_MESSAGES, MESSAGE_UNAUTHORIZED_ACTION);
            return redirectAddress;
        } else {
            if (deleteDatabaseById(databaseId)) {
                redirAttr.addFlashAttribute("messages", "Database deleted");
            } else {
                redirAttr.addFlashAttribute("messages", "No such database");
            }
        }

        return redirectAddress;
    }

    public List<Database> getAllDatabases() {
        return databaseRepository.findAll();
    }

    @Transactional
    public List<String> editDatabaseWithFeedback(Database database, Long id) {
        List<String> messages = new ArrayList<>();
        if (editDatabase(database, id, messages)) {
            messages.add("Database successfully edited");
            return messages;
        }
        messages.add("Database edit failed");
        return messages;
    }

    @Transactional
    private boolean editDatabase(Database newDb, Long oldId, List<String> messages) {
        try {
            Connection conn = createConnectionToDatabase(newDb.getName(), newDb.getDatabaseSchema());
            conn.close();

            System.out.println(newDb.getName() + " table-schema is valid");
            
            Database oldDb = databaseRepository.findOne(oldId);
            oldDb.setName(newDb.getName());
            oldDb.setDatabaseSchema(newDb.getDatabaseSchema());
            return true;
        } catch (Exception e) {
            messages.add(e.toString());
        }

        return false;
    }
}
