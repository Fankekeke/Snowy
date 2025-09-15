package vip.xiaonuo.common.util;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.style.*;
import com.deepoove.poi.plugin.markdown.MarkdownRenderData;
import com.deepoove.poi.plugin.markdown.MarkdownRenderPolicy;
import com.deepoove.poi.plugin.markdown.MarkdownStyle;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * MD转Word工具类
 *
 * @author FanK
 * @date 2025/9/15 16:13
 **/
@Slf4j
public class CommonMarkdown2WordUtil {

    /**
     * markdown转html
     *
     * @param markdownContent md内容
     * @return 结果
     */
    public static String markdownToHtml(String markdownContent) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownContent);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }


    /**
     * 将markdown格式内容转换为word并保存在本地
     *
     * @param markdownContent md内容
     * @param outputFileName  导出文件名称
     */
    public static void toDoc(String markdownContent, String outputFileName) {
        MarkdownRenderData code = new MarkdownRenderData();
        code.setMarkdown(markdownContent);
        MarkdownStyle style = MarkdownStyle.newStyle();
        setMarkdownStyle(style);
        code.setStyle(style);
        // markdown样式处理与word模板中的标签{{md}}绑定
        Map<String, Object> data = new HashMap<>(16);
        data.put("md", code);

        Configure config = Configure.builder().bind("md", new MarkdownRenderPolicy()).build();
        try {
            // 获取classpath
            String path = Objects.requireNonNull(CommonMarkdown2WordUtil.class.getClassLoader().getResource("")).getPath();
            log.info("classpath:{}", path);
            //由于部署到linux上后，程序是从jar包中去读取resources下的文件的，所以需要使用流的方式读取，所以获取流，而不是直接使用文件路径
            ClassPathResource resource = new ClassPathResource("markdown" + File.separator + "markdown_template.docx");
            InputStream resourceAsStream = resource.getInputStream();
            XWPFTemplate.compile(resourceAsStream, config)
                    .render(data)
                    .writeToFile(path + "out_markdown_" + outputFileName + ".docx");
        } catch (IOException e) {
            log.error(">>> markdown转word异常：", e);
            log.error("保存为word出错");
        }

    }

    /**
     * 将markdown转换为word文档并下载
     *
     * @param markdownContent md内容
     * @param response        响应
     * @param fileName        文件名
     */
    public static void convertAndDownloadWordDocument(String markdownContent, HttpServletResponse response, String fileName) {
        MarkdownRenderData code = new MarkdownRenderData();
        code.setMarkdown(markdownContent);
        MarkdownStyle style = MarkdownStyle.newStyle();
        setMarkdownStyle(style);

        code.setStyle(style);
        // markdown样式处理与word模板中的标签{{md}}绑定
        Map<String, Object> data = new HashMap<>(16);
        data.put("md", code);
        Configure configure = Configure.builder().bind("md", new MarkdownRenderPolicy()).build();

        try {
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            //由于部署到linux上后，程序是从jar包中去读取resources下的文件的，所以需要使用流的方式读取，所以获取流，而不是直接使用文件路径
            ClassPathResource resource = new ClassPathResource("markdown" + File.separator + "markdown_template.docx");
            InputStream resourceAsStream = resource.getInputStream();
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + ".docx");
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document;charset=utf-8");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setContentType("application/octet-stream;charset=UTF-8");
            XWPFTemplate template = XWPFTemplate.compile(resourceAsStream, configure)
                    .render(data);
            template.writeAndClose(response.getOutputStream());
        } catch (IOException e) {
            log.error("下载word文档失败:{}", e.getMessage());
        }
    }


    /**
     * 设置转换为word文档时的基本样式
     *
     * @param style 样式
     * @return 样式
     */
    public static MarkdownStyle setMarkdownStyle(MarkdownStyle style) {
        // 一定设置为false,不然生成的word文档中各元素前边都会加上有层级效果的一串数字,
        // 比如一级标题 前边出现1 二级标题出现1.1 三级标题出现1.1.1这样的数字
        style.setShowHeaderNumber(false);
        // 修改默认的表格样式
        // table header style(表格头部,通常为表格顶部第一行,用于设置列标题)
        RowStyle headerStyle = new RowStyle();
        CellStyle cellStyle = new CellStyle();
        // 设置表格头部的背景色为灰色
        cellStyle.setBackgroundColor("cccccc");
        Style textStyle = new Style();
        // 设置表格头部的文字颜色为黑色
        textStyle.setColor("000000");
        // 头部文字加粗
        textStyle.setBold(false);
        // 设置表格头部文字大小为12
        textStyle.setFontSize(10);
        textStyle.setFontFamily("黑体");
        // 设置表格头部文字垂直居中
        cellStyle.setVertAlign(XWPFTableCell.XWPFVertAlign.CENTER);

        cellStyle.setDefaultParagraphStyle(ParagraphStyle.builder().withDefaultTextStyle(textStyle).build());
        headerStyle.setDefaultCellStyle(cellStyle);
        style.setTableHeaderStyle(headerStyle);

        // table border style(表格边框样式)
        BorderStyle borderStyle = new BorderStyle();
        // 设置表格边框颜色为黑色
        borderStyle.setColor("000000");
        // 设置表格边框宽度为3px
        borderStyle.setSize(3);
        // 设置表格边框样式为实线
        borderStyle.setType(XWPFTable.XWPFBorderType.SINGLE);
        style.setTableBorderStyle(borderStyle);

        // 设置普通的引用文本样式
        ParagraphStyle quoteStyle = new ParagraphStyle();
        // 设置段落样式
        quoteStyle.setSpacingBeforeLines(0.5d);
        quoteStyle.setSpacingAfterLines(0.5d);

        // 设置段落的文本样式
        Style quoteTextStyle = new Style();
        quoteTextStyle.setColor("000000");
        quoteTextStyle.setFontSize(10);
        quoteTextStyle.setItalic(true);
        quoteStyle.setDefaultTextStyle(quoteTextStyle);
        style.setQuoteStyle(quoteStyle);

        return style;
    }

    public static void main(String[] args) {
        String markdownContent = "以下是用户登录功能的测试文档示例：\n" +
                "\n" +
                "| 测试用例ID | 测试描述                   | 前置条件                     | 测试步骤                                                                 | 预期结果                             | 通过/失败状态 |\n" +
                "|------------|----------------------------|------------------------------|--------------------------------------------------------------------------|--------------------------------------|---------------|\n" +
                "| TC001      | 普通用户正确信息登录       | 普通用户已注册并存在         | 1. 进入登录页面<br>2. 输入正确的用户名和密码<br>3. 点击“登录”按钮       | 成功登录并跳转至用户主页             |               |\n" +
                "| TC002      | 普通用户错误信息登录       | 普通用户已注册并存在         | 1. 进入登录页面<br>2. 输入错误的密码<br>3. 点击“登录”按钮               | 提示“用户名或密码错误”               |               |\n" +
                "| TC003      | 普通用户未注册账号登录     | 普通用户未注册               | 1. 进入登录页面<br>2. 输入未注册的用户名和任意密码<br>3. 点击“登录”按钮 | 提示“用户名或密码错误”               |               |\n" +
                "| TC004      | 管理员正确信息登录         | 管理员已配置并存在           | 1. 进入管理员登录页面<br>2. 输入正确的管理员用户名和密码<br>3. 点击“登录”按钮 | 成功登录并跳转至管理员主页           |               |\n" +
                "| TC005      | 管理员错误信息登录         | 管理员已配置并存在           | 1. 进入管理员登录页面<br>2. 输入错误的密码<br>3. 点击“登录”按钮         | 提示“用户名或密码错误”               |               |\n" +
                "| TC006      | 管理员未注册账号登录       | 管理员未配置                 | 1. 进入管理员登录页面<br>2. 输入未注册的用户名和任意密码<br>3. 点击“登录”按钮 | 提示“用户名";
        toDoc(markdownContent.replace("<br>", "  "), "test23");


    }
}
