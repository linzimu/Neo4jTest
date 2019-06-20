package doctorai_v2;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StoreRecords {
    private final static String[] attrName = {
            "disease", "important", "part",
            "inducement", "description", "name",
            "irClass", "ageMax", "ageMin",
            "abnormal", "condMax", "condMin", "unit"};

    //节点类型
    enum nodeType {
        disnode, // 疾病
        cpcnode, // 并发症
        asnode, // 伴随症状
        irnode, // 检查结果
    }

    //关系类型
    enum relationType {
        cpcrelation, // 并发症
        asrelation,// 伴随症状
        irrelation, // 检查结果
    }

    private static void addRecords(StoreTools storeTools, String relation, String type1, String type2, String relationType) throws FileNotFoundException {
        CSVReader cr = new CSVReaderBuilder(new BufferedReader(new InputStreamReader(new FileInputStream("./data/疾病" + relation + ".csv")))).build();
        Iterator<String[]> iter = cr.iterator();
        for (Iterator<String[]> it = iter; it.hasNext(); ) {
            String[] record = it.next();
            storeTools.addNodeRelation(type1, record[0], type2, record[1], relationType);
        }
    }

    /**
     * 添加检查结果
     *
     * @param storeTools
     * @throws IOException
     */
    private static void addIR(StoreTools storeTools) throws IOException {
        // 添加检查结果
        String srcPath = "./data/疾病检查结果.csv";
        CSVReader cr = new CSVReaderBuilder(new BufferedReader(new InputStreamReader(new FileInputStream(srcPath)))).build();
        Iterator<String[]> iter = cr.iterator();
        while (iter.hasNext()) {
            String[] record = iter.next();
            // 添加检查结果的年龄
            String handler = "match (p:nodeType{name:\"nodeName\"}) merge (p)-[:relationType]->(:attrType{attrVal})";
            StringBuilder sb = null;
            List<String> attrList = new ArrayList<>();

            for (int i = 0; i < 13; i++) {
                if (i != 0) {
                    String attrVal = record[i].replaceAll("\\s", "");
                    if (!attrVal.isEmpty()) {
                        attrList.add(attrName[i] + ":\"" + attrVal + "\"");
                    }
                }
            }
            // 添加疾病节点
            String disName = record[0].replaceAll("\\s", "");
            storeTools.addNode(nodeType.disnode.toString(), disName);
            // 添加属性信息
            handler = handler.replace("attrVal", String.join(", ", attrList));
            handler = handler.replace("attrType", nodeType.irnode.toString());
            handler = handler.replace("nodeType", nodeType.disnode.toString());
            handler = handler.replace("nodeName", disName);
            handler = handler.replace("relationType", relationType.irrelation.toString());
            System.out.println(handler);
            storeTools.execClause(handler);
        }
    }

    public static void main(String[] args) throws IOException {
        StoreTools storeTools = new StoreTools("bolt://localhost:7687", "neo4j", "123456");

        storeTools.clearDB(); //清空数据库
        // 添加并发症
        addRecords(storeTools, "并发症", nodeType.disnode.toString(), nodeType.cpcnode.toString(),
                relationType.cpcrelation.toString());

        // 添加伴随症状
        addRecords(storeTools, "伴随症状", nodeType.disnode.toString(), nodeType.asnode.toString(),
                relationType.asrelation.toString());

        // 添加检查结果
        addIR(storeTools);

        storeTools.close();
        System.out.println("done!");
    }
}
