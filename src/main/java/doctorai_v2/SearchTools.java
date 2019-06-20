package doctorai_v2;

import org.neo4j.driver.v1.*;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchTools {
    private static Driver driver;

    private static HashMap<String, String> map =
            new HashMap<String, String>() {
                {
                    put("并发症", "asrelation");
                    put("伴随症状", "cpcrelation");
                    put("疾病", "disnode");
                }
            };

    public SearchTools(String uri, String user, String pw) {
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
                        "MATCH (a:disnode) RETURN a.name as name");
                while (res.hasNext()) {
                    Record record = res.next();
                    System.out.println(String.format("%s",
                            record.get("name").asString()));
                }
            }
        }
    }

    public ArrayList searchOneRes(String nodeType, String nodeName, String relationType) {
        boolean flag;
        ArrayList list = new ArrayList<>();
        String clause_dis = "match (n:nodeType{name:\"nodeName\"}) return n.name as name";
        clause_dis = clause_dis.replace("nodeType", nodeType);
        clause_dis = clause_dis.replace("nodeName", nodeName);
        try (Session sess = driver.session()) {
            try (Transaction tx = sess.beginTransaction()) {
                StatementResult res = tx.run(clause_dis);
                flag = res.hasNext();
            }
        }
        if (flag) {
            list.add(true);
            String clause_one = "match (:nodeType{name:\"nodeName\"})-[:relationType]->(n) return n.name as name";
            clause_one = clause_one.replace("nodeType", nodeType);
            clause_one = clause_one.replace("nodeName", nodeName);
            clause_one = clause_one.replace("relationType", relationType);
            try (Session sess = driver.session()) {
                try (Transaction tx = sess.beginTransaction()) {
                    StatementResult res = tx.run(clause_one);
                    while (res.hasNext()) {
                        Record record = res.next();
                        list.add(record.get("name").asString());
                    }
                }
            }
        } else {
            list.add(false);
        }
        return list;
    }

//    public ArrayList searchIR(String nodeType, String nodeName, String relationType) {
//        boolean flag;
//        ArrayList list = new ArrayList<>();
//        String clause_dis = "match (n:nodeType{name:\"nodeName\"}) return n.name as name";
//        clause_dis = clause_dis.replace("nodeType", nodeType);
//        clause_dis = clause_dis.replace("nodeName", nodeName);
//
//        try (Session sess = driver.session()) {
//            try (Transaction tx = sess.beginTransaction()) {
//                StatementResult res = tx.run(clause_dis);
//                flag = res.hasNext();
//            }
//        }
//
//        if (flag) {
//            list.add(true);
//            String clause_one = "match (n:nodeType{name:\"nodeName\"}) return" +
//                    " n.name as name" +
//                    " n.important as important" +
//                    " n.part as part" +
//                    " n.inducement as inducement" +
//                    " n.description as description" +
//                    " n.irClass as irClass" +
//                    " n.ageMax as ageMax" +
//                    " n.ageMin as ageMin" +
//                    " n.abnormal as abnormal" +
//                    " n.condMax as condMax" +
//                    " n.condMin as condMin" +
//                    " n.unit as unit";
//            clause_one = clause_one.replace("nodeType", nodeType);
//            clause_one = clause_one.replace("nodeName", nodeName);
//            clause_one = clause_one.replace("relationType", relationType);
//            try (Session sess = driver.session()) {
//                try (Transaction tx = sess.beginTransaction()) {
//                    StatementResult res = tx.run(clause_one);
//                    while (res.hasNext()) {
//                        Record record = res.next();
//                        list.add(record.get("name").asString());
//                    }
//                }
//            }
//        } else {
//            list.add(false);
//        }
//        return list;
//    }

    /**
     * @param st
     * @param nodeType
     * @param nodeName
     * @param relation
     */
    public void printRes(SearchTools st, String nodeType, String nodeName, String relation) {
        ArrayList res = st.searchOneRes(map.get(nodeType), nodeName, map.get(relation));
        if ((boolean) res.remove(0)) {
            if (res.isEmpty()) {
                System.out.println("该疾病没有");
            } else {
                for (Object str : res) {
                    System.out.println(str);
                }
            }
        } else {
            System.out.println("请注意：数据库中没有该疾病的相关信息！");
        }
    }

    public static void main(String[] args) {
        SearchTools st = new SearchTools("bolt://localhost:7687", "neo4j", "123456");
        st.printRes(st, "疾病", "感染性心内膜炎", "伴随症状");
        System.out.println("****************************************");
        st.printRes(st, "疾病", "主动脉夹层", "并发症");
        st.close();
    }
}
