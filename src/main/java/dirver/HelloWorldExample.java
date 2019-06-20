package dirver;

import org.neo4j.driver.v1.*;

import static org.neo4j.driver.v1.Values.parameters;

public class HelloWorldExample {
    private final Driver driver;

    public HelloWorldExample(String uri, String user, String pw) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, pw));
    }

    public void close() throws Exception {
        driver.close();
    }

    public void print(final String msg) {
        try (Session sess = driver.session()) {
            String greeting = sess.writeTransaction(new TransactionWork<String>() {
                @Override
                public String execute(Transaction tx) {
                    StatementResult res = tx.run("create (a:greeting) set a.msg = $msg return a.msg, id(a)",
                            parameters("msg", msg));

                    return res.single().get(0).asString();
                }
            });
            System.out.println(greeting);
        }
    }

    public static void main(String[] args) throws Exception {
        HelloWorldExample greeter = new HelloWorldExample("bolt://localhost:7687", "neorj", "123456");
        greeter.print("hello, world");
    }
}
