//package embedded;
//
//import org.neo4j.cypher.internal.ExecutionEngine;
//import org.neo4j.cypher.internal.javacompat.ExecutionResult;
//import org.neo4j.graphdb.GraphDatabaseService;
//import org.neo4j.graphdb.factory.GraphDatabaseFactory;
//
//import java.io.File;
//
//public class test01 {
//    public static void main(String[] args) {
//        GraphDatabaseFactory gdf = new GraphDatabaseFactory();
//        GraphDatabaseService db = gdf.newEmbeddedDatabase(new File("/Users/kerwin/neo4j"));
//        ExecutionEngine ee = new ExecutionEngine(db);
//        ExecutionResult er = ee.execute("match (java:JAVA) return java");
//        String results = er.dumpToString();
//        System.out.println(results);
//
//    }
//}
