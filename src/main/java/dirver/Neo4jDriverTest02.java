package dirver;

import org.neo4j.driver.v1.*;

import static org.neo4j.driver.v1.Values.parameters;

public class Neo4jDriverTest02 {
    public static void main(String[] args) throws Exception {
        // 1.向数据库对象请求一个新的驱动程序
        Driver driver = GraphDatabase.driver("bolt://localhost:7687",
                AuthTokens.basic("neo4j", "123456"));
        // 2.向驱动程序对象请求一个新的会话
        try (Session sess = driver.session()) {
            // 3.请求会话对象创建事务
            try (Transaction tx = sess.beginTransaction()) {
                // 4.使用事务运行语句。它返回一个表示结果的对象
                tx.run("CREATE(a:Person{name:{name}, title:{title}})",
                        parameters("name", "AI医疗质控公司", "title", "颐圣智能")
                );
                tx.success();
            }
            // 3.请求会话对象创建事务
            try (Transaction tx = sess.beginTransaction()) {
                // 4.使用事务运行语句。它返回一个表示结果的对象
                StatementResult res = tx.run("MATCH (a:Person) WHERE a.name={name}"
                                + "RETURN a.name as name, a.title as title",
                        parameters("name", "AI医疗质控公司"));
                while (res.hasNext()) {
                    Record record = res.next();
                    System.out.println(String.format("%s %s", record.get("title").asString(),
                            record.get("name").asString()));
                }
            }
        }
        driver.close();
    }
}
