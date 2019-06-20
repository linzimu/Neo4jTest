package dirver;

import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.summary.ResultSummary;

public class Neo4jDriverTest04 {
    public static void main(String[] args) {
        Driver driver = GraphDatabase.driver("bolt://localhost:7687",
                AuthTokens.basic("neo4j", "123456"));
        try (Session sess = driver.session()) {
            try (Transaction tx = sess.beginTransaction()) {
                StatementResult res = tx.run("PROFILE MATCH (p:Person{name:{name}}) RETURN id(p)",
                        Values.parameters("name", "Arthur"));
                ResultSummary summary = res.consume();
                System.out.println(summary.statementType());
                System.out.println(summary.profile());
            }
        }
        driver.close();
    }
}
