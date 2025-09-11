package vip.xiaonuo.common.pojo;

import lombok.Data;

/**
 * SQL转三线图的实体
 *
 * @author FanK
 * @date 2025/9/11 16:08
 **/
@Data
public class CommonSqlWordEntity {

    /**
     * 字段索引
     */
    private Integer index;

    /**
     * 字段名称
     */
    private String name;

    /**
     * 字段类型
     */
    private String type;

    /**
     * 字段长度
     */
    private String length;

    /**
     * 主键
     */
    private String primaryKey;

    /**
     * 描述
     */
    private String remark;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 表描述
     */
    private String tableComment;
}
