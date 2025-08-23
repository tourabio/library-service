package testutils;

import io.agroal.api.AgroalDataSource;
import io.quarkus.test.junit.callback.QuarkusTestBeforeEachCallback;
import io.quarkus.test.junit.callback.QuarkusTestMethodContext;
import jakarta.enterprise.inject.spi.CDI;
import java.sql.Connection;
import java.sql.SQLException;


public class TestBeforeEachCallback implements QuarkusTestBeforeEachCallback {


    @Override
    public void beforeEach(final QuarkusTestMethodContext context) {
        cleanupAllTables();
    }

    private static void cleanupAllTables() {
        var entityManager = CDI.current().select(AgroalDataSource.class).get();
        try (Connection connection = entityManager.getConnection();
             var stmt = connection.createStatement()) {
            stmt.execute("""
                    DELETE FROM loan;
                    DELETE FROM member;
                    DELETE FROM book;
                    """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
