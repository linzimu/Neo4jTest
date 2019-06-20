package jdbc;

import java.sql.*;

/**
 * 这个一neo4j自带的Movies示例演示
 */
public class JDBCTest2 {
    public static void main(String[] args) throws Exception {
        // connect
        Connection conn = DriverManager.getConnection("jdbc:neo4j:bolt://localhost", "neo4j", "123456");
        //querying
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("match (n:Person) return n.name");
//            System.out.println(rs.toString());
            while (rs.next()) {
                System.out.println(rs.getString("n.name"));
            }
        }
        System.out.println("\n\n\n\n");
        String query = "match (:Movie{title:{1}})<-[:ACTED_IN]-(a:Person) return a.name as actor";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "The Matrix");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println(rs.getArray(1));
                    System.out.println(rs.getString("actor"));
                }
            }
        }
        conn.close();
    }
}
