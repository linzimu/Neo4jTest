package embedded;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.io.fs.FileUtils;

import java.io.File;
import java.io.IOException;

public class Neo4jDriverTest05 {
    private static final File databaseDirectory = new File("/Users/kerwin/neo4j");

    private String greeting;

    // tag::vars[]
    private GraphDatabaseService graphDb;
    private Node firstNode;
    private Node secondNode;
    private Relationship relationship;
    // end::vars[]

    // tag::createReltype[]
    /* 创建关系类型（枚举型） */
    private enum RelTypes implements RelationshipType {
        KNOWS
    }
    // end::createReltype[]

    public static void main(final String[] args) throws IOException {
        Neo4jDriverTest05 hello = new Neo4jDriverTest05();
        hello.createDb();
        hello.removeData();
        hello.shutDown();
    }

    private void createDb() throws IOException {
        FileUtils.deleteRecursively(databaseDirectory);

        // tag::startDb[]
        /* 1. 开启Neo4j数据库服务 */
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(databaseDirectory);
        registerShutdownHook(graphDb);
        // end::startDb[]

        // tag::transaction[]
        /* 在事务中包装操作 */
        try (Transaction tx = graphDb.beginTx()) {
            // Database operations go here
            // end::transaction[]
            // tag::addData[]
            firstNode = graphDb.createNode();
            firstNode.setProperty("message", "Hello, ");
            secondNode = graphDb.createNode();
            secondNode.setProperty("message", "World!");

            relationship = firstNode.createRelationshipTo(secondNode, RelTypes.KNOWS);
            relationship.setProperty("message", "brave Neo4j ");
            // end::addData[]

            // tag::readData[]
//            System.out.print(firstNode.getProperty("message"));
//            System.out.print(relationship.getProperty("message"));
//            System.out.println(secondNode.getProperty("message"));
            // end::readData[]

            greeting = firstNode.getProperty("message").toString()
                    + relationship.getProperty("message").toString()
                    + secondNode.getProperty("message").toString();
            System.out.println(greeting);
            // tag::transaction[]
            tx.success();
        }
        // end::transaction[]
    }

    private void removeData() {
        try (Transaction tx = graphDb.beginTx()) {
            // tag::removingData[]
            // let's remove the data
            firstNode.getSingleRelationship(RelTypes.KNOWS, Direction.OUTGOING).delete();
            firstNode.delete();
            secondNode.delete();
            // end::removingData[]

            tx.success();
        }
    }

    private void shutDown() {
        System.out.println();
        System.out.println("Shutting down database ...");
        // tag::shutdownServer[]
        /* 2. 关闭数据库 */
        graphDb.shutdown();
        // end::shutdownServer[]
    }


    // tag::shutdownHook[]
    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }
    // end::shutdownHook[]
}
