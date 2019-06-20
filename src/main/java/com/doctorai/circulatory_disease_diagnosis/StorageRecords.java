package com.doctorai.circulatory_disease_diagnosis;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StorageRecords {
    //节点类型
    enum nodeType {
        disnode, // 疾病
        cpcnode, // 并发症
        asnode, // 伴随症状
        irnode, // 检查结果
        partnode, // 部位
        indnode, // 诱因
        adjnode, // 形容词描述
        abnnode, // 异常
        agenode, // 年龄
        condnode, // 条件

    }

    //关系类型
    enum relationType {
        cpcrelation, // 并发症
        asrelation,// 伴随症状
        irrelation, // 检查结果
        partrelation, // 部位
        indrelation, // 诱因
        adjrelation, // 形容词描述
        abnrelation, // 异常
        agerelation, // 年龄
        condrelation, // 条件
    }

    private static List<String[]> getRecords(String filepath) {
        List<String[]> list = new ArrayList<>();
        try (FileReader reader = new FileReader(filepath);
             BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("\\s", "");
                list.add(line.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static void addRecords(GraphTools conn, String relation, String type1, String type2, String relationType) throws SQLException, FileNotFoundException {
        CSVReader cr = new CSVReaderBuilder(new BufferedReader(new InputStreamReader(new FileInputStream("./data/疾病" + relation + ".csv")))).build();
        Iterator<String[]> iter = cr.iterator();
        for (Iterator<String[]> it = iter; it.hasNext(); ) {
            String[] record = it.next();
            conn.addRecord(record[0], type1,
                    record[1], type2,
                    relationType);
        }
    }

    /**
     * 添加疾病检查结果
     *
     * @param conn
     * @throws SQLException
     * @throws IOException
     */
    private static void addIR(GraphTools conn) throws SQLException, IOException {
        // 添加检查结果
        String srcPath = "./data/疾病检查结果.csv";
        CSVReader cr = new CSVReaderBuilder(new BufferedReader(new InputStreamReader(new FileInputStream(srcPath)))).build();
        Iterator<String[]> iter = cr.iterator();
        while (iter.hasNext()) {
            String[] record = iter.next();
            // 添加检查结果的年龄
            String agemax = record[7].replaceAll("\\s", "");
            String agemin = record[8].replaceAll("\\s", "");
            if (!agemax.isEmpty() || !agemin.isEmpty()) {
                conn.addRecord(record[5], nodeType.irnode.toString(),
                        record[5] + "年龄", nodeType.agenode.toString(),
                        relationType.agerelation.toString());
                if (!agemin.isEmpty()) {
                    conn.addAttr("年龄", nodeType.agenode.toString(), "agemin", agemin);
                }
                if (!agemax.isEmpty()) {
                    conn.addAttr("年龄", nodeType.agenode.toString(), "agemax", agemax);
                }
            }

            // 添加检查结果的条件
            String condmax = record[10].replaceAll("\\s", "");
            String condmin = record[11].replaceAll("\\s", "");
            if (!condmax.isEmpty() || !condmin.isEmpty()) {
                conn.addRecord(record[5], nodeType.irnode.toString(),
                        record[5] + "条件", nodeType.condnode.toString(),
                        relationType.condrelation.toString());
                if (!condmin.isEmpty()) {
                    conn.addAttr("条件", nodeType.condnode.toString(), "condmin", condmin);
                }
                if (!agemax.isEmpty()) {
                    conn.addAttr("条件", nodeType.condnode.toString(), "condmax", condmax);
                }
            }

            // 添加检查结构的诱因，部位，形容描述，异常
            String part = record[2].replaceAll("\\s", "");
            if (!part.isEmpty()) {
                conn.addRecord(record[5], nodeType.irnode.toString(),
                        part, nodeType.partnode.toString(),
                        relationType.partrelation.toString());
            }
            String inducement = record[3].replaceAll("\\s", "");
            if (!inducement.isEmpty()) {
                conn.addRecord(record[5], nodeType.irnode.toString(),
                        inducement, nodeType.indnode.toString(),
                        relationType.indrelation.toString());
            }
            String description = record[4].replaceAll("\\s", "");
            if (!description.isEmpty()) {
                conn.addRecord(record[5], nodeType.irnode.toString(),
                        description, nodeType.adjnode.toString(),
                        relationType.adjrelation.toString());
            }
            String abnormal = record[9].replaceAll("\\s", "");
            if (!abnormal.isEmpty()) {
                conn.addRecord(record[5], nodeType.irnode.toString(),
                        abnormal, nodeType.abnnode.toString(),
                        relationType.abnrelation.toString());
            }


            /**
             * 下面的代码有问题
             */
            // 添加检查结果和疾病名称之间的关系
            conn.addRecord(record[5], nodeType.irnode.toString(),
                    record[0], nodeType.disnode.toString(),
                    relationType.irrelation.toString());
            // 设置检查结果的属性{"属于**检查"}
            conn.addAttr(record[5], nodeType.irnode.toString(), "irType", record[6]);
        }
    }

    public static void main(String[] args) throws Exception {
        GraphTools conn = new GraphTools(
                "jdbc:neo4j:bolt:localhost", "neo4j", "123456");

        conn.clearDB(); //清空数据库

        // 添加并发症
        addRecords(conn, "并发症", nodeType.disnode.toString(), nodeType.cpcnode.toString(),
                relationType.cpcrelation.toString());

        // 添加伴随症状
        addRecords(conn, "伴随症状", nodeType.disnode.toString(), nodeType.asnode.toString(),
                relationType.asrelation.toString());

        addIR(conn);


        conn.close();
        System.out.println("done!");
    }
}
