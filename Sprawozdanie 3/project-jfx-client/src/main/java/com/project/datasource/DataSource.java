package com.project.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSource {

    private static final String DB_DIR = "db";
    private static final String DB_NAME = "projekty";
    private static final String DB_USERNAME = "admin";
    private static final String DB_USER_PASSWORD = "admin";

    private static final String HSQL_ADDITIONAL_PARAMS =
            ";hsqldb.write_delay=false;sql.syntax_pgs=true";

    private static final String DB_URL =
            String.format("jdbc:hsqldb:file:%s/%s%s", DB_DIR, DB_NAME, HSQL_ADDITIONAL_PARAMS);

    private static final HikariDataSource ds;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername(DB_USERNAME);
        config.setPassword(DB_USER_PASSWORD);
        config.setMaximumPoolSize(1);
        ds = new HikariDataSource(config);
    }

    private DataSource() {
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}