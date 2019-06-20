package cypter;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.helpers.collection.Iterators;

import static org.neo4j.io.fs.FileUtils.deleteRecursively;

public class CypherTest01 {
    private static final File databaseDirectory = new File("/Users/kerwin/neo4j");
    String resultString;
    String columnsString;
    String nodeResult;
    String rows = "";

    public static void main(String[] args) {
        CypherTest01 javaQuery = new CypherTest01();
        javaQuery.run();
        javaQuery.clearDbPath();
    }

    void run() {
        clearDbPath();

        // tag::addData[]
        // 添加节点
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(databaseDirectory);

        try (Transaction tx = db.beginTx()) {
            Node myNode = db.createNode();
            myNode.setProperty("name", "my node");
            tx.success();
        }
        // end::addData[]

        // tag::execute[]
        try (Transaction ignored = db.beginTx();
             Result result = db.execute("MATCH (n {name: 'my node'}) RETURN n, n.name")) {
            while (result.hasNext()) {
                Map<String, Object> row = result.next();
                for (Entry<String, Object> column : row.entrySet()) {
                    rows += column.getKey() + ": " + column.getValue() + "; ";
                }
                rows += "\n";
            }
        }
        System.out.println("第1次结果：" + rows);
        // end::execute[]
        // the result is now empty, get a new one
        try (Transaction ignored = db.beginTx();
             Result result = db.execute("MATCH (n {name: 'my node'}) RETURN n, n.name")) {
            // tag::items[]
            Iterator<Node> n_column = result.columnAs("n");
            for (Node node : Iterators.asIterable(n_column)) {
                nodeResult = node + ": " + node.getProperty("name");
            }
            // end::items[]

            // tag::columns[]
            List<String> columns = result.columns();
            // end::columns[]
            columnsString = columns.toString();
            System.out.println(columnsString);
            System.out.println("第2次结果：" + nodeResult);


            resultString = db.execute("MATCH (n {name: 'my node'}) RETURN n, n.name").resultAsString();
            System.out.println("第4次结果：\n" + resultString);
        }

        db.shutdown();
    }

    private void clearDbPath() {
        try {
            deleteRecursively(databaseDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
