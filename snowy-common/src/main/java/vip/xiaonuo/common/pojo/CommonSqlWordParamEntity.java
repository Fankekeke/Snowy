package vip.xiaonuo.common.pojo;

import lombok.Data;

/**
 * SQL转三线图参数的实体
 *
 * @author FanK
 * @date 2025/9/12 09:35
 **/
@Data
public class CommonSqlWordParamEntity {

    /**
     * 字段索引-是否展示
     */
    private boolean indexFlag;

    /**
     * 字段名称-是否展示
     */
    private boolean nameFlag;

    /**
     * 字段类型-是否展示
     */
    private boolean typeFlag;

    /**
     * 字段长度-是否展示
     */
    private boolean lengthFlag;

    /**
     * 主键-是否展示
     */
    private boolean primaryKeyFlag;

    /**
     * 描述-是否展示
     */
    private boolean remarkFlag;

    /**
     * 表格类型（1.三线图 2.普通表格）
     */
    private String tableType;
}
