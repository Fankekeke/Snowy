package vip.xiaonuo.common.util;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.util.JdbcConstants;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.pojo.CommonSqlWordEntity;
import vip.xiaonuo.common.pojo.CommonSqlWordParamEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SQL转三线图工具类
 *
 * @author FanK
 * @date 2025/9/11 16:28
 **/
@Slf4j
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
                    String tableComment = createTableStmt.getComment() != null ? createTableStmt.getComment().toString().replace("'", "") : "";
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
                        String name = column.getName().toString().replace("`", "");
                        String dataType = column.getDataType().getName();
                        String length = "";
                        if (column.getDataType().getArguments() != null && !column.getDataType().getArguments().isEmpty()) {
                            length = column.getDataType().getArguments().toString().replace("[", "").replace("]", "");
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
                        fieldEntity.setPrimaryKey(isPrimaryKey ? "是" : "");
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
     * @param response       响应
     */
    public static void createWord(List<CommonSqlWordEntity> tableFieldList, CommonSqlWordParamEntity param, HttpServletResponse response) throws Exception {
        if (CollectionUtil.isEmpty(tableFieldList)) {
            throw new CommonException("请联系管理员，无字段可生成！");
        }
        Map<String, List<CommonSqlWordEntity>> tableFieldMap = tableFieldList.stream().collect(Collectors.groupingBy(CommonSqlWordEntity::getTableName));
        List<Map<String, Object>> tableList = new ArrayList<>();
        List<Map<String, Object>> tableCommentList = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, List<CommonSqlWordEntity>> entry : tableFieldMap.entrySet()) {
            index++;
            int finalIndex = index;
            tableList.add(new HashMap<>() {
                {
                    put("tableIndex", finalIndex);
                    put("tableName", entry.getKey());
                    put("tableComment", entry.getValue().get(0).getTableComment());
                    put("tableFieldList", entry.getValue());
                }
            });
            tableCommentList.add(new HashMap<>() {
                {
                    put("tableIndex", finalIndex);
                    put("tableName", entry.getKey());
                    put("tableComment", entry.getValue().get(0).getTableComment());
                }
            });
        }
        //导出word并指定word导出模板
        try (XWPFDocument doc = WordExportUtil.exportWord07("word/3l.docx", new HashMap<>() {
            {
                put("tableList", tableList);
                put("tableCommentList", tableCommentList);
            }
        })) {
            //设置编码格式
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            //设置内容类型
            response.setContentType("application/octet-stream");
            //设置头及文件命名。
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("悲伤橘子-" + DateUtil.formatDateTime(new Date()) + "表.docx", StandardCharsets.UTF_8));
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setContentType("application/octet-stream;charset=UTF-8");
            //写入
            doc.write(response.getOutputStream());
        } catch (Exception e) {
            log.error(">>> 导出用户信息异常：", e);
        }
    }

}
