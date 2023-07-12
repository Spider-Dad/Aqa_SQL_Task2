package ru.netology.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHelper {

    private static final QueryRunner runner = new QueryRunner();

    private SQLHelper() {

    }

    private static Connection getConn() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/app", "app", "pass"
        );
    }

    @SneakyThrows
    public static DataHelper.VerificationCode getVerificationCode() {
        var codeSQL = "SELECT * FROM auth_codes ORDER BY created DESC LIMIT 1";
        var conn = getConn();
        var result = runner.query(conn, codeSQL, new BeanHandler<>(SQLAutCode.class));
        return new DataHelper.VerificationCode(result.getCode());
    }

    @Data
    @NoArgsConstructor
    public static class SQLAutCode {
        private String id;
        private String user_id;
        private String code;
        private String created;
    }

}

