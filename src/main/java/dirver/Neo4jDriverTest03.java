package dirver;

import org.neo4j.driver.v1.*;
import java.util.List;

import static org.neo4j.driver.v1.Values.parameters;

public class Neo4jDriverTest03 {
    private static String getBookmark(Driver driver) {
        String bookmark;
        try (Session sess = driver.session(AccessMode.WRITE)) {
            try (Transaction tx = sess.beginTransaction()) {
                tx.run("CREATE(person:Person{name:{name},title:{title}})",
                        parameters("name", "Arthur", "title", "king"));
                tx.success();
            } finally {
                bookmark = sess.lastBookmark();
            }
        }
        return bookmark;
    }

    public static void main(String[] args) {
        Driver driver = GraphDatabase.driver("bolt://localhost:7687",
                AuthTokens.basic("neo4j", "123456"));
        List<Record> records;
        String bookmark = getBookmark(driver);
        try (Session sess = driver.session(AccessMode.READ)) {
            StatementResult res;
            try (Transaction tx = sess.beginTransaction(bookmark)) {
                res = tx.run("MATCH (person) RETURN person.title",
                        parameters("name", "Arthur"));
//                tx.success();
                records = res.list();
//                while (res.hasNext()) {
//                    Record r = res.next();
//                    System.out.println("Arthur's title is " + r.get("person.title"));
//                }
            }
        }
        for(Record r:records){
            System.out.println(r.get(0));
        }
        driver.close();
    }
}
