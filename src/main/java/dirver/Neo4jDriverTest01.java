package dirver;

import org.neo4j.driver.v1.*;

import static org.neo4j.driver.v1.Values.parameters;

public class Neo4jDriverTest01 {
    private final Driver driver;

    public Neo4jDriverTest01(String uri, String user, String pw) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, pw));
    }

    public void close() throws Exception {
        driver.close();
    }

    public void printGreeting(final String msg) {
        try (Session session = driver.session()) {
            String greeting = session.writeTransaction(new TransactionWork<String>() {
                @Override
                public String execute(Transaction tx) {
                    StatementResult result = tx.run("CREATE (a:Greeting) " +
                            "SET a.message = $message " +
                            "RETURN a.message + ', from node ' + id(a)", parameters("message", msg));
                    return result.single().get(0).asString();
                }
            });
            System.out.println(greeting);
        }
    }

    public static void main(String... args) throws Exception {
        Neo4jDriverTest01 greeter = new Neo4jDriverTest01("bolt://localhost:7687", "neo4j", "123456");
        greeter.printGreeting("我连接Neo4j的第一个程序！");
        greeter.close();
    }
}
