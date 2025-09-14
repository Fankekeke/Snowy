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
package vip.xiaonuo.biz.modular.linetable.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.biz.modular.linetable.entity.AiLineTable;
import vip.xiaonuo.biz.modular.linetable.param.AiLineTableAddParam;
import vip.xiaonuo.biz.modular.linetable.param.AiLineTableEditParam;
import vip.xiaonuo.biz.modular.linetable.param.AiLineTableIdParam;
import vip.xiaonuo.biz.modular.linetable.param.AiLineTablePageParam;
import java.io.IOException;
import java.util.List;

/**
 * 三线图记录Service接口
 *
 * @author Fank
 * @date  2025/09/12 22:03
 **/
public interface AiLineTableService extends IService<AiLineTable> {

    /**
     * 获取三线图记录分页
     *
     * @author Fank
     * @date  2025/09/12 22:03
     */
    Page<AiLineTable> page(AiLineTablePageParam aiLineTablePageParam);

    /**
     * 添加三线图记录
     *
     * @author Fank
     * @date  2025/09/12 22:03
     */
    void add(AiLineTableAddParam aiLineTableAddParam);

    /**
     * 编辑三线图记录
     *
     * @author Fank
     * @date  2025/09/12 22:03
     */
    void edit(AiLineTableEditParam aiLineTableEditParam);

    /**
     * 删除三线图记录
     *
     * @author Fank
     * @date  2025/09/12 22:03
     */
    void delete(List<AiLineTableIdParam> aiLineTableIdParamList);

    /**
     * 获取三线图记录详情
     *
     * @author Fank
     * @date  2025/09/12 22:03
     */
    AiLineTable detail(AiLineTableIdParam aiLineTableIdParam);

    /**
     * 获取三线图记录详情
     *
     * @author Fank
     * @date  2025/09/12 22:03
     **/
    AiLineTable queryEntity(String id);

    /**
     * 下载三线图记录导入模板
     *
     * @author Fank
     * @date  2025/09/12 22:03
     */
    void downloadImportTemplate(HttpServletResponse response) throws IOException;

    /**
     * 导入三线图记录
     *
     * @author Fank
     * @date  2025/09/12 22:03
     **/
    JSONObject importData(MultipartFile file);

    /**
     * 导出三线图记录
     *
     * @author Fank
     * @date  2025/09/12 22:03
     */
    void exportData(List<AiLineTableIdParam> aiLineTableIdParamList, HttpServletResponse response) throws IOException;
}
