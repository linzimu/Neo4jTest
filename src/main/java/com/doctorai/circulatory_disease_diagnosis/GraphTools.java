package com.doctorai.circulatory_disease_diagnosis;


import java.sql.*;


public class GraphTools {
    // Driver objects are thread-safe and are typically made available application-wide.
    private final Connection conn;


    /**
     * 打开数据库连接
     *
     * @param uri
     * @param user
     * @param password
     */
    public GraphTools(String uri, String user, String password) throws Exception {
        conn = DriverManager.getConnection(uri, user, password);
    }

    /**
     * 关闭数据库连接
     */
    public void close() throws SQLException {
        conn.close();
    }

    /**
     * 清空数据库
     *
     * @throws SQLException
     */
    public void clearDB() throws SQLException {
        // Sessions are lightweight and disposable connection wrappers.
        String operate = "match (n) detach delete n";
        Statement stmt = conn.createStatement();
        stmt.execute(operate);
        System.out.println("请注意:成功清空数据库里面的所有内容!!!");
    }

    /**
     * 添加节点
     *
     * @param nodeName
     * @param nodeType
     */
    private void addNode(String nodeName, String nodeType) throws SQLException {
        // Sessions are lightweight and disposable connection wrappers.
        String operate = "merge (:nodeType{name:\"nodeName\"})";
        operate = operate.replace("nodeType", nodeType);
        operate = operate.replace("nodeName", nodeName);
//        System.out.println(operate);
        Statement stmt = conn.createStatement();
        stmt.execute(operate);
    }

    /**
     * 添加节点属性
     *
     * @param nodeName
     * @param nodeType
     * @param attrName
     * @param attrVal
     * @throws SQLException
     */
    public void addAttr(String nodeName, String nodeType, String attrName, String attrVal) throws SQLException {
        String operate = "match (n:nodeType{name:\"nodeName\"}) set n.attrName=\"attrVal\"";
        operate = operate.replace("nodeName", nodeName);
        operate = operate.replace("nodeType", nodeType);
        operate = operate.replace("attrName", attrName);
        operate = operate.replace("attrVal", attrVal);
        Statement stmt = conn.createStatement();
        stmt.execute(operate);

    }

    /**
     * 添加两个节点，并确定它们之间的关系
     *
     * @param nodeName1
     * @param nodeType1
     * @param nodeName2
     * @param nodeType2
     * @param relationType
     * @throws SQLException
     */
    private void addRelation(String nodeName1, String nodeType1, String nodeName2, String nodeType2, String relationType) throws SQLException {
        String operate = "match (d:nodeType1{name:\"nodeName1\"}), (c:nodeType2{name:\"nodeName2\"})" +
                " merge (d)-[r:relationType]->(c)";

        operate = operate.replace("nodeName1", nodeName1);
        operate = operate.replace("nodeName2", nodeName2);

        operate = operate.replace("nodeType1", nodeType1);
        operate = operate.replace("nodeType2", nodeType2);

        operate = operate.replace("relationType", relationType);

//        System.out.println(operate);
//        System.out.println();

        Statement stmt = conn.createStatement();
        stmt.execute(operate);
    }

    public void addRecord(String nodeName1, String nodeType1, String nodeName2,
                          String nodeType2, String relationType) throws SQLException {
        this.addNode(nodeName1, nodeType1);
        this.addNode(nodeName2, nodeType2);
        this.addRelation(nodeName1, nodeType1, nodeName2,
                nodeType2, relationType);
    }

//    public static void main(String[] args) throws Exception {
//        GraphTools test = new GraphTools("jdbc:neo4j:bolt:localhost", "neo4j", "123456");
//        String relation = "并发症";
//        List<String[]> cInfo = getComplicationInfo("./data/疾病并发症.csv");
//        for (String[] record : cInfo) {
//            test.addRecord(record[0], nodeType.disease.toString(), record[1], nodeType.complication.toString(), relation);
//        }
//        test.close();
//        System.out.println("done!");
//    }
}

