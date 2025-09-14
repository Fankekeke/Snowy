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
package vip.xiaonuo.biz.modular.linetable.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import java.math.BigDecimal;
import java.util.Date;
import vip.xiaonuo.biz.modular.linetable.entity.AiLineTable;
import vip.xiaonuo.biz.modular.linetable.mapper.AiLineTableMapper;
import vip.xiaonuo.biz.modular.linetable.param.AiLineTableAddParam;
import vip.xiaonuo.biz.modular.linetable.param.AiLineTableEditParam;
import vip.xiaonuo.biz.modular.linetable.param.AiLineTableIdParam;
import vip.xiaonuo.biz.modular.linetable.param.AiLineTablePageParam;
import vip.xiaonuo.biz.modular.linetable.service.AiLineTableService;

import vip.xiaonuo.common.util.CommonDownloadUtil;
import vip.xiaonuo.common.util.CommonResponseUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 三线图记录Service接口实现类
 *
 * @author Fank
 * @date  2025/09/12 22:03
 **/
@Service
public class AiLineTableServiceImpl extends ServiceImpl<AiLineTableMapper, AiLineTable> implements AiLineTableService {

    @Override
    public Page<AiLineTable> page(AiLineTablePageParam aiLineTablePageParam) {
        QueryWrapper<AiLineTable> queryWrapper = new QueryWrapper<AiLineTable>().checkSqlInjection();
        if(ObjectUtil.isAllNotEmpty(aiLineTablePageParam.getSortField(), aiLineTablePageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(aiLineTablePageParam.getSortOrder());
            queryWrapper.orderBy(true, aiLineTablePageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(aiLineTablePageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(AiLineTable::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(AiLineTableAddParam aiLineTableAddParam) {
        AiLineTable aiLineTable = BeanUtil.toBean(aiLineTableAddParam, AiLineTable.class);
        this.save(aiLineTable);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(AiLineTableEditParam aiLineTableEditParam) {
        AiLineTable aiLineTable = this.queryEntity(aiLineTableEditParam.getId());
        BeanUtil.copyProperties(aiLineTableEditParam, aiLineTable);
        this.updateById(aiLineTable);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<AiLineTableIdParam> aiLineTableIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(aiLineTableIdParamList, AiLineTableIdParam::getId));
    }

    @Override
    public AiLineTable detail(AiLineTableIdParam aiLineTableIdParam) {
        return this.queryEntity(aiLineTableIdParam.getId());
    }

    @Override
    public AiLineTable queryEntity(String id) {
        AiLineTable aiLineTable = this.getById(id);
        if(ObjectUtil.isEmpty(aiLineTable)) {
            throw new CommonException("三线图记录不存在，id值为：{}", id);
        }
        return aiLineTable;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<AiLineTableEditParam> dataList = CollectionUtil.newArrayList();
         String fileName = "三线图记录导入模板_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), AiLineTableEditParam.class).sheet("三线图记录").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 三线图记录导入模板下载失败：", e);
         CommonResponseUtil.renderError(response, "三线图记录导入模板下载失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public JSONObject importData(MultipartFile file) {
        try {
            int successCount = 0;
            int errorCount = 0;
            JSONArray errorDetail = JSONUtil.createArray();
            // 创建临时文件
            File tempFile = FileUtil.writeBytes(file.getBytes(), FileUtil.file(FileUtil.getTmpDir() +
                    FileUtil.FILE_SEPARATOR + "aiLineTableImportTemplate.xlsx"));
            // 读取excel
            List<AiLineTableEditParam> aiLineTableEditParamList =  EasyExcel.read(tempFile).head(AiLineTableEditParam.class).sheet()
                    .headRowNumber(1).doReadSync();
            List<AiLineTable> allDataList = this.list();
            for (int i = 0; i < aiLineTableEditParamList.size(); i++) {
                JSONObject jsonObject = this.doImport(allDataList, aiLineTableEditParamList.get(i), i);
                if(jsonObject.getBool("success")) {
                    successCount += 1;
                } else {
                    errorCount += 1;
                    errorDetail.add(jsonObject);
                }
            }
            return JSONUtil.createObj()
                    .set("totalCount", aiLineTableEditParamList.size())
                    .set("successCount", successCount)
                    .set("errorCount", errorCount)
                    .set("errorDetail", errorDetail);
        } catch (Exception e) {
            log.error(">>> 三线图记录导入失败：", e);
            throw new CommonException("三线图记录导入失败");
        }
    }

    public JSONObject doImport(List<AiLineTable> allDataList, AiLineTableEditParam aiLineTableEditParam, int i) {
        String id = aiLineTableEditParam.getId();
        if(ObjectUtil.hasEmpty(id)) {
            return JSONUtil.createObj().set("index", i + 1).set("success", false).set("msg", "必填字段存在空值");
        } else {
            try {
                int index = CollStreamUtil.toList(allDataList, AiLineTable::getId).indexOf(aiLineTableEditParam.getId());
                AiLineTable aiLineTable;
                boolean isAdd = false;
                if(index == -1) {
                    isAdd = true;
                    aiLineTable = new AiLineTable();
                } else {
                    aiLineTable = allDataList.get(index);
                }
                BeanUtil.copyProperties(aiLineTableEditParam, aiLineTable);
                if(isAdd) {
                    allDataList.add(aiLineTable);
                } else {
                    allDataList.remove(index);
                    allDataList.add(index, aiLineTable);
                }
                this.saveOrUpdate(aiLineTable);
                return JSONUtil.createObj().set("success", true);
            } catch (Exception e) {
              log.error(">>> 数据导入异常：", e);
              return JSONUtil.createObj().set("success", false).set("index", i + 1).set("msg", "数据导入异常");
            }
        }
    }

    @Override
    public void exportData(List<AiLineTableIdParam> aiLineTableIdParamList, HttpServletResponse response) throws IOException {
       File tempFile = null;
       try {
         List<AiLineTableEditParam> dataList;
         if(ObjectUtil.isNotEmpty(aiLineTableIdParamList)) {
            List<String> idList = CollStreamUtil.toList(aiLineTableIdParamList, AiLineTableIdParam::getId);
            dataList = BeanUtil.copyToList(this.listByIds(idList), AiLineTableEditParam.class);
         } else {
            dataList = BeanUtil.copyToList(this.list(), AiLineTableEditParam.class);
         }
         String fileName = "三线图记录_" + DateUtil.format(DateTime.now(), DatePattern.PURE_DATETIME_PATTERN) + ".xlsx";
         tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
         EasyExcel.write(tempFile.getPath(), AiLineTableEditParam.class).sheet("三线图记录").doWrite(dataList);
         CommonDownloadUtil.download(tempFile, response);
       } catch (Exception e) {
         log.error(">>> 三线图记录导出失败：", e);
         CommonResponseUtil.renderError(response, "三线图记录导出失败");
       } finally {
         FileUtil.del(tempFile);
       }
    }
}
