package org.dulab.adapcompounddb.site.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionPool.class);

    private static ConnectionPool pool = null;
    private static DataSource dataSource = null;

    public synchronized static ConnectionPool getInstance() {
        if (pool == null)
            pool = new ConnectionPool();
        return pool;
    }

    private ConnectionPool() {
        try {
            InitialContext ic = new InitialContext();
            dataSource = (DataSource) ic.lookup("java:/comp/env/jdbc/adapcompounddb");
        }
        catch (NamingException e) {
            LOG.error(e.getMessage());
        }
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        }
        catch (SQLException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

    public void freeConnection(Connection c) {
        try {
            c.close();
        }
        catch (SQLException e) {
            LOG.error(e.getMessage());
        }
    }
}
