package xyz.ufactions.prolib.database;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.prolib.database.column.Column;
import xyz.ufactions.prolib.updater.UpdateType;
import xyz.ufactions.prolib.updater.event.UpdateEvent;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;

public abstract class RepositoryBase implements Listener {
    // Queue for failed processes
    private static final Object _queueLock = new Object();
    private final HashMap<DatabaseRunnable, String> _failedQueue = new HashMap<>();

    private final DataSource _dataSource;    // Connection pool
    protected JavaPlugin Plugin;    // Plugin responsible for this repository

    /**
     * Constructor
     *
     * @param plugin     - the {@link JavaPlugin} module responsible for this repository.
     * @param dataSource - the {@link DataSource} responsible for providing the connection pool to this repository.
     */
    public RepositoryBase(JavaPlugin plugin, DataSource dataSource) {
        Plugin = plugin;
        _dataSource = dataSource;

        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            initialize();
            update();
        });

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    protected abstract void initialize();

    protected abstract void update();

    /**
     * @return the {@link DataSource} used by the repository for connection pooling.
     */
    protected DataSource getConnectionPool() {
        return _dataSource;
    }

    /**
     * Requirements: {@link Connection}s must be closed after usage so they may be returned to the pool!
     *
     * @return a newly fetched {@link Connection} from the connection pool, if a connection can be made, null otherwise.
     * @see Connection#close()
     */
    protected Connection getConnection() {
        try {
            return _dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: Log connection failures?
            return null;
        }
    }

    /**
     * Execute a query against the repository.
     *
     * @param query   - the concatenated query to execute in string form.
     * @param columns - the column data values used for insertion into the query.
     * @return the number of rows affected by this query in the repository.
     */
    protected int executeUpdate(String query, Column<?>... columns) {
        return executeInsert(query, null, columns);
    }

    protected int executeInsert(String query, ResultSetCallable callable, Column<?>... columns) {
        int affectedRows = 0;

        // Automatic resource management for handling/closing objects.
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
        ) {
            for (int i = 0; i < columns.length; i++) {
                columns[i].setValue(preparedStatement, i + 1);
            }

            affectedRows = preparedStatement.executeUpdate();

            if (callable != null) {
                callable.processResultSet(preparedStatement.getGeneratedKeys());
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return affectedRows;
    }

    protected void executeQuery(PreparedStatement statement, ResultSetCallable callable, Column<?>... columns) {
        try {
            for (int i = 0; i < columns.length; i++) {
                columns[i].setValue(statement, i + 1);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                callable.processResultSet(resultSet);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected void executeQuery(String query, ResultSetCallable callable, Column<?>... columns) {
        // Automatic resource management for handling/closing objects.
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            executeQuery(preparedStatement, callable, columns);
        } catch (SQLException exception) {
            exception.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected void handleDatabaseCall(final DatabaseRunnable databaseRunnable, final String errorMessage) {
        Thread asyncThread = new Thread(() -> {
            try {
                databaseRunnable.run();
            } catch (Exception exception) {
                processFailedDatabaseCall(databaseRunnable, exception.getMessage(), errorMessage);
            }
        });

        asyncThread.start();
    }

    protected void processFailedDatabaseCall(DatabaseRunnable databaseRunnable, String errorPreMessage, String runnableMessage) {
        if (databaseRunnable.getFailedCounts() < 4) {
            databaseRunnable.incrementFailCount();

            synchronized (_queueLock) {
                _failedQueue.put(databaseRunnable, runnableMessage);
            }
        }
    }

    @EventHandler
    public void processDatabaseQueue(UpdateEvent event) {
        if (event.getType() != UpdateType.MIN_01)
            return;

        processFailedQueue();
    }

    private void processFailedQueue() {
        synchronized (_queueLock) {
            for (DatabaseRunnable databaseRunnable : _failedQueue.keySet()) {
                handleDatabaseCall(databaseRunnable, _failedQueue.get(databaseRunnable));
            }
        }
    }
}