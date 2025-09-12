package vip.xiaonuo.common.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.util.JdbcConstants;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.pojo.CommonSqlWordEntity;
import vip.xiaonuo.common.pojo.CommonSqlWordParamEntity;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SQL转三线图工具类
 *
 * @author FanK
 * @date 2025/9/11 16:28
 **/
public class CommonSqlWordUtil {

    public static void main(String[] args) {
        String sql = "CREATE TABLE `job_road_info`  (\n" +
                "  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
                "  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '道路编号',\n" +
                "  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路段名称',\n" +
                "  `max_speed` int NULL DEFAULT NULL COMMENT '限速',\n" +
                "  `road_total` decimal(10, 2) NULL DEFAULT NULL COMMENT '路长',\n" +
                "  `road_width` decimal(10, 2) NULL DEFAULT NULL COMMENT '路宽',\n" +
                "  `type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '道路类型',\n" +
                "  `start_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '起点地址',\n" +
                "  `end_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '止点地址',\n" +
                "  `work_level` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作业等级（1.一级 2.二级 3.三级）',\n" +
                "  `total_acreage` decimal(10, 2) NULL DEFAULT NULL COMMENT '作业面积',\n" +
                "  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                "  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',\n" +
                "  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '删除标识（0-正常,1-删除）',\n" +
                "  `dept_id` int NOT NULL COMMENT '部门ID',\n" +
                "  `tenant_id` int NULL DEFAULT NULL COMMENT '用户ID',\n" +
                "  PRIMARY KEY (`id`) USING BTREE\n" +
                ") ENGINE = InnoDB AUTO_INCREMENT = 3049 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '作业-道路信息' ROW_FORMAT = DYNAMIC;" +
                "CREATE TABLE `road_job_group`  (\n" +
                "  `id` int NOT NULL AUTO_INCREMENT COMMENT ' id',\n" +
                "  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编号',\n" +
                "  `road_job_group_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '道路作业名称',\n" +
                "  `priority` tinyint NOT NULL DEFAULT 1 COMMENT '优先级，1表示低优先级，2表示高优先级，默认值为2',\n" +
                "  `pollution_point_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '排污点位名称',\n" +
                "  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',\n" +
                "  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',\n" +
                "  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录最后修改时间',\n" +
                "  `delete_flag` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除标识：0 表示未删除，1 表示已删除',\n" +
                "  `business_type` int NULL DEFAULT NULL COMMENT '业务类型(0：铲冰除雪、1：机扫安排、2：机保安排、3：洗地安排、4：夜冲刷（车行道）、5：湿滑安排)',\n" +
                "  PRIMARY KEY (`id`) USING BTREE\n" +
                ") ENGINE = InnoDB AUTO_INCREMENT = 473 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '道路分组作业表' ROW_FORMAT = Dynamic;";
        System.out.println(JSONUtil.toJsonStr(geFieldListBySql(sql)));
    }

    /**
     * 根据sql获取字段列表
     *
     * @param sql sql内容
     * @return 字段列表
     */
    public static List<CommonSqlWordEntity> geFieldListBySql(String sql) {
        if (StrUtil.isEmpty(sql)) {
            throw new CommonException("上传sql内容不能为空！");
        }
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        // 检查是否为 CREATE TABLE 语句
        if (CollectionUtil.isNotEmpty(stmtList)) {
            // 待返回的字段数据
            List<CommonSqlWordEntity> fieldList = new ArrayList<>();
            for (SQLStatement sqlStatement : stmtList) {
                if (sqlStatement instanceof SQLCreateTableStatement createTableStmt) {
                    // 获取表名
                    String tableName = createTableStmt.getTableName();
                    String tableComment = createTableStmt.getComment() != null ? createTableStmt.getComment().toString() : "";
                    System.out.println("表名: " + tableName + ", 注释: " + tableComment);
                    List<SQLColumnDefinition> columnDefinitions = createTableStmt.getColumnDefinitions();
                    // 获取主键列名列表
                    Set<String> primaryKeyColumns = new HashSet<>();
                    for (SQLTableElement element : createTableStmt.getTableElementList()) {
                        if (element instanceof SQLPrimaryKey pk) {
                            for (SQLSelectOrderByItem item : pk.getColumns()) {
                                primaryKeyColumns.add(item.getExpr().toString());
                            }
                        }
                    }

                    int index = 1;
                    for (SQLColumnDefinition column : columnDefinitions) {
                        index++;
                        String name = column.getName().toString();
                        String dataType = column.getDataType().getName();
                        String length = "";
                        if (column.getDataType().getArguments() != null && !column.getDataType().getArguments().isEmpty()) {
                            length = column.getDataType().getArguments().toString();
                        }
                        boolean isPrimaryKey = primaryKeyColumns.contains(name);
                        String comment = column.getComment() != null ? column.getComment().toString() : "";

                        System.out.println("列名: " + name + ", 类型: " + dataType +
                                ", 长度: " + length + ", 主键: " + isPrimaryKey +
                                ", 注释: " + comment);

                        CommonSqlWordEntity fieldEntity = new CommonSqlWordEntity();
                        fieldEntity.setIndex(index);
                        fieldEntity.setName(name);
                        fieldEntity.setType(dataType);
                        fieldEntity.setLength(length);
                        fieldEntity.setPrimaryKey(isPrimaryKey ? "是" : "否");
                        fieldEntity.setRemark(comment);
                        fieldEntity.setTableName(tableName);
                        fieldEntity.setTableComment(tableComment);
                        fieldList.add(fieldEntity);
                    }
                }
            }
            return fieldList;
        } else {
            throw new CommonException("上传sql内容格式有误，必须为CREATE TABLE 语句！");
        }
    }

    /**
     * 生成数据库表-word
     *
     * @param tableFieldList 字段列表
     * @param param          导出参数
     */
    public static void createWord(List<CommonSqlWordEntity> tableFieldList, CommonSqlWordParamEntity param) {
        if (CollectionUtil.isEmpty(tableFieldList)) {
            throw new CommonException("请联系管理员，无字段可生成！");
        }
        Map<String, List<CommonSqlWordEntity>> dataMap = tableFieldList.stream().collect(Collectors.groupingBy(CommonSqlWordEntity::getTableName));
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
        configuration.setDefaultEncoding("UTF-8");
        // 模板文件所在路径
        configuration.setClassForTemplateLoading(this.getClass(), "/templates");
        Template t = null;
        try {
            // 获取模板文件
            t = configuration.getTemplate("line.xml", "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 导出文件
        File outFile = new File(DateUtil.formatDateTime(new Date()) + "测试模板");
        try (Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), StandardCharsets.UTF_8))) {
            if (t != null) {
                // 将填充数据填入模板文件并输出到目标文件
                t.process(dataMap, out);
            }
        } catch (IOException | TemplateException e1) {
            e1.printStackTrace();
        }
    }

}
