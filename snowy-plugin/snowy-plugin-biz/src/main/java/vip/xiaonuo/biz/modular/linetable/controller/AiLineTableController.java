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
package vip.xiaonuo.biz.modular.linetable.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.biz.modular.linetable.entity.AiLineTable;
import vip.xiaonuo.biz.modular.linetable.param.AiLineTableAddParam;
import vip.xiaonuo.biz.modular.linetable.param.AiLineTableEditParam;
import vip.xiaonuo.biz.modular.linetable.param.AiLineTableIdParam;
import vip.xiaonuo.biz.modular.linetable.param.AiLineTablePageParam;
import vip.xiaonuo.biz.modular.linetable.service.AiLineTableService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import vip.xiaonuo.common.pojo.CommonSqlWordEntity;
import vip.xiaonuo.common.util.CommonSqlWordUtil;

import java.io.IOException;
import java.util.List;

/**
 * 三线图记录控制器
 *
 * @author Fank
 * @date  2025/09/12 22:03
 */
@Tag(name = "三线图记录控制器")
@RestController
@Validated
public class AiLineTableController {

    @Resource
    private AiLineTableService aiLineTableService;

    /**
     * 获取三线图记录分页
     *
     * @author Fank
     * @date  2025/09/12 22:03
     */
    @Operation(summary = "获取三线图记录分页")
    @SaCheckPermission("/biz/linetable/page")
    @GetMapping("/biz/linetable/page")
    public CommonResult<Page<AiLineTable>> page(AiLineTablePageParam aiLineTablePageParam) {
        return CommonResult.data(aiLineTableService.page(aiLineTablePageParam));
    }

    /**
     * 添加三线图记录
     *
     * @author Fank
     * @date  2025/09/12 22:03
     */
    @Operation(summary = "添加三线图记录")
    @CommonLog("添加三线图记录")
    @SaCheckPermission("/biz/linetable/add")
    @PostMapping("/biz/linetable/add")
    public CommonResult<String> add(@RequestBody @Valid AiLineTableAddParam aiLineTableAddParam) {
        aiLineTableService.add(aiLineTableAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑三线图记录
     *
     * @author Fank
     * @date  2025/09/12 22:03
     */
    @Operation(summary = "编辑三线图记录")
    @CommonLog("编辑三线图记录")
    @SaCheckPermission("/biz/linetable/edit")
    @PostMapping("/biz/linetable/edit")
    public CommonResult<String> edit(@RequestBody @Valid AiLineTableEditParam aiLineTableEditParam) {
        aiLineTableService.edit(aiLineTableEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除三线图记录
     *
     * @author Fank
     * @date  2025/09/12 22:03
     */
    @Operation(summary = "删除三线图记录")
    @CommonLog("删除三线图记录")
    @SaCheckPermission("/biz/linetable/delete")
    @PostMapping("/biz/linetable/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<AiLineTableIdParam> aiLineTableIdParamList) {
        aiLineTableService.delete(aiLineTableIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取三线图记录详情
     *
     * @author Fank
     * @date  2025/09/12 22:03
     */
    @Operation(summary = "获取三线图记录详情")
    @SaCheckPermission("/biz/linetable/detail")
    @GetMapping("/biz/linetable/detail")
    public CommonResult<AiLineTable> detail(@Valid AiLineTableIdParam aiLineTableIdParam) {
        return CommonResult.data(aiLineTableService.detail(aiLineTableIdParam));
    }

    /**
     * 下载三线图记录导入模板
     *
     * @author Fank
     * @date  2025/09/12 22:03
     */
    @Operation(summary = "下载三线图记录导入模板")
    @GetMapping(value = "/biz/linetable/downloadImportTemplate", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        aiLineTableService.downloadImportTemplate(response);
    }

    /**
     * 导入三线图记录
     *
     * @author Fank
     * @date  2025/09/12 22:03
     */
    @Operation(summary = "导入三线图记录")
    @CommonLog("导入三线图记录")
    @SaCheckPermission("/biz/linetable/importData")
    @PostMapping("/biz/linetable/importData")
    public CommonResult<JSONObject> importData(@RequestPart("file") MultipartFile file) {
        return CommonResult.data(aiLineTableService.importData(file));
    }

    /**
     * 导出三线图记录
     *
     * @author Fank
     * @date  2025/09/12 22:03
     */
    @Operation(summary = "导出三线图记录")
    @SaCheckPermission("/biz/linetable/exportData")
    @PostMapping(value = "/biz/linetable/exportData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportData(@RequestBody List<AiLineTableIdParam> aiLineTableIdParamList, HttpServletResponse response) throws IOException {
        aiLineTableService.exportData(aiLineTableIdParamList, response);
    }

    @Operation(summary = "导出word")
    @PostMapping(value = "/biz/linetable/exportWordTest")
    public void exportWordTest(HttpServletResponse response) throws Exception {
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
        List<CommonSqlWordEntity> fieldList = CommonSqlWordUtil.geFieldListBySql(sql);
        CommonSqlWordUtil.createWord(fieldList, null, response);
    }
}
