package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class JDBCTest {
    public static void main(String[] args) throws Exception {
        try (Connection con =
                     DriverManager.getConnection("jdbc:neo4j:bolt://localhost", "neo4j", "123456")) {
            String operate = "merge (c:complication{name:\"脑出血\"})";
            try (PreparedStatement stmt = con.prepareStatement(operate)) {
                ResultSet rs = stmt.executeQuery();
            }
        }
    }
}
