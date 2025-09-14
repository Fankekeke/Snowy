/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.biz.modular.linetable.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 三线图记录实体
 *
 * @author Fank
 * @date  2025/09/12 22:03
 **/
@Getter
@Setter
@TableName("ai_line_table")
public class AiLineTable {

    /** 主键 */
    @TableId
    @Schema(description = "主键")
    private String id;

    /** 所属用户 */
    @Schema(description = "所属用户")
    private String userId;

    /** SQL内容 */
    @Schema(description = "SQL内容")
    private String content;

    /** 表格类型（1.普通表格 2.三线表） */
    @Schema(description = "表格类型（1.普通表格 2.三线表）")
    private String type;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private Date createDate;
}
