package doctorai_v2;

import org.neo4j.driver.v1.*;

public class StoreTools {
    private static Driver driver;

    public StoreTools(String uri, String user, String pw) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, pw));
    }

    public void close() {
        driver.close();
    }

    public void printNode() {
        // 2.向驱动程序对象请求一个新的会话
        try (Session sess = driver.session()) {
            // 3.请求会话对象创建事务
            try (Transaction tx = sess.beginTransaction()) {
                // 4.使用事务运行语句。它返回一个表示结果的对象
                StatementResult res = tx.run(
                        "MATCH (a:Person) RETURN a.name as name, a.sex as sex");
                while (res.hasNext()) {
                    Record record = res.next();
                    System.out.println(String.format("%s %s", record.get("sex").asString(),
                            record.get("name").asString()));
                }
            }
        }
    }

    public void execClause(String Clause) {
        try (Session sess = driver.session()) {
            // 3.请求会话对象创建事务
            try (Transaction tx = sess.beginTransaction()) {
                // 4.使用事务运行语句。它返回一个表示结果的对象
                tx.run(Clause);
                tx.success();
            }
        }
    }

    public void clearDB() {
        // 2.向驱动程序对象请求一个新的会话
        try (Session sess = driver.session()) {
            // 3.请求会话对象创建事务
            try (Transaction tx = sess.beginTransaction()) {
                // 4.使用事务运行语句。它返回一个表示结果的对象
                tx.run("match (n) detach delete n;");
                tx.success();
            }
        }
        System.out.println("请注意：已经成功清除图数据库！！！");
    }

    public void addDupNode(String nodeType, String nodeName) {
        String operate = "create (:nodeType{name:\"nodeName\"});";
        operate = operate.replace("nodeType", nodeType);
        operate = operate.replace("nodeName", nodeName);
//        System.out.println(operate);

        // 2.向驱动程序对象请求一个新的会话
        try (Session sess = driver.session()) {
            // 3.请求会话对象创建事务
            try (Transaction tx = sess.beginTransaction()) {
                // 4.使用事务运行语句。它返回一个表示结果的对象
                tx.run(operate);
                tx.success();
            }
        }
    }

    public void addNode(String nodeType, String nodeName) {
        String operate = "merge (:nodeType{name:\"nodeName\"});";
        operate = operate.replace("nodeType", nodeType);
        operate = operate.replace("nodeName", nodeName);
//        System.out.println(operate);

        // 2.向驱动程序对象请求一个新的会话
        try (Session sess = driver.session()) {
            // 3.请求会话对象创建事务
            try (Transaction tx = sess.beginTransaction()) {
                // 4.使用事务运行语句。它返回一个表示结果的对象
                tx.run(operate);
                tx.success();
            }
        }
    }

    public void addAttr(String nodeType, String nodeName, String attrName, String attrVal) {
        String operate = "match (n:nodeType{name:\"nodeName\"}) set n.attrName=\"attrVal\";";
        operate = operate.replace("nodeName", nodeName);
        operate = operate.replace("nodeType", nodeType);
        operate = operate.replace("attrName", attrName);
        operate = operate.replace("attrVal", attrVal);
//        System.out.println(operate);

        try (Session sess = driver.session()) {
            // 3.请求会话对象创建事务
            try (Transaction tx = sess.beginTransaction()) {
                // 4.使用事务运行语句。它返回一个表示结果的对象
                tx.run(operate);
                tx.success();
            }
        }
    }

    private void addRelation(String nodeType1, String nodeName1,
                             String nodeType2, String nodeName2, String relationType) {
        String handler = "match (d:nodeType1{name:\"nodeName1\"}), (c:nodeType2{name:\"nodeName2\"})" +
                " merge (d)-[r:relationType]->(c);";

        handler = handler.replace("nodeName1", nodeName1);
        handler = handler.replace("nodeName2", nodeName2);

        handler = handler.replace("nodeType1", nodeType1);
        handler = handler.replace("nodeType2", nodeType2);

        handler = handler.replace("relationType", relationType);
//        System.out.println(handler);

        try (Session sess = driver.session()) {
            // 3.请求会话对象创建事务
            try (Transaction tx = sess.beginTransaction()) {
                // 4.使用事务运行语句。它返回一个表示结果的对象
                tx.run(handler);
                tx.success();
            }
        }
    }

    public void addNodeRelation(String nodeType1, String nodeName1,
                                String nodeType2, String nodeName2, String relationType) {
        this.addNode(nodeType1, nodeName1);
        this.addNode(nodeType2, nodeName2);
        this.addRelation(nodeType1, nodeName1, nodeType2, nodeName2, relationType);
    }

    public static void main(String[] args) {
        // 测试
        StoreTools storeDis = new StoreTools("bolt://localhost:7687", "neo4j", "123456");
        storeDis.clearDB();
        storeDis.addAttr("Person", "小明", "sex", "男");
        storeDis.addAttr("Person", "小红", "sex", "女");
        storeDis.addAttr("Person", "小刚", "sex", "男");
        storeDis.addAttr("Person", "小兰", "sex", "女");
        storeDis.addNodeRelation("Person", "小明", "Person", "小红", "妻子");
        storeDis.addNodeRelation("Person", "小红", "Person", "小刚", "丈夫");
        storeDis.addNodeRelation("Person", "小明", "Person", "小兰", "妻子");
        storeDis.addNodeRelation("Person", "小明", "Person", "小刚", "基友");
        storeDis.addNodeRelation("Person", "小刚", "Person", "小明", "基友");
        storeDis.addNodeRelation("Person", "小红", "Person", "小兰", "啦啦");
        storeDis.addNodeRelation("Person", "小刚", "Person", "小兰", "妻子");
        storeDis.printNode();
        storeDis.close();
    }
}
