package vip.xiaonuo.biz.modular.agent;

import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgent;
import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgentOptions;
import com.alibaba.cloud.ai.dashscope.api.DashScopeAgentApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.util.CommonMarkdown2WordUtil;

import java.util.List;

/**
 * AI输出控制器
 *
 * @author FanK
 * @date 2025/9/17 10:23
 **/
@RestController
@RequestMapping("/ai")
@Slf4j
public class AgentController {

    private final DashScopeAgent agent;

    @Value("${spring.ai.dashscope.agent.app-id}")
    private String appId;

    public AgentController(DashScopeAgentApi dashscopeAgentApi) {
        this.agent = new DashScopeAgent(dashscopeAgentApi);
    }

    @GetMapping("/bailian/agent/call")
    public String call(@RequestParam(value = "message",
            defaultValue = "如何使用SDK快速调用阿里云百炼的应用?") String message) {
        ChatResponse response = agent.call(new Prompt(message, DashScopeAgentOptions.builder().withAppId(appId).build()));
        if (response == null || response.getResult() == null) {
            log.error("chat response is null");
            return "chat response is null";
        }

        AssistantMessage appOutput = response.getResult().getOutput();
        String content = appOutput.getText();

        DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput output = (DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput) appOutput.getMetadata().get("output");
        List<DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputDocReference> docReferences = output.docReferences();
        List<DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputThoughts> thoughts = output.thoughts();

        log.info("content:\n{}\n\n", content);

        if (docReferences != null && !docReferences.isEmpty()) {
            for (DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputDocReference docReference : docReferences) {
                log.info("{}\n\n", docReference);
            }
        }

        if (thoughts != null && !thoughts.isEmpty()) {
            for (DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputThoughts thought : thoughts) {
                log.info("{}\n\n", thought);
            }
        }

        return content;
    }

    public static void main(String[] args) {
        DashScopeAgent agent = new DashScopeAgent(new DashScopeAgentApi("sk-fkebb4821588054a66aa1951d7f239f77c"));
//        ChatResponse response = agent.call(new Prompt("“请为我的项目生成用户支付宝在线支付功能的测试文档。这个功能的用户角色有普通用户。\n" +
//                "提供的文档应包含以下信息：测试用例ID、测试描述、前置条件、测试步骤、预期结果和通过/失败状态。请使用表格格式，并用中文。”", DashScopeAgentOptions.builder().withAppId("").build()));

        ChatResponse response = agent.call(new Prompt("““请为我的项目生成用户登录功能的用例说明文档。这个功能的用户角色有普通用户和管理员。提供的文档应包含以下信息：用例名称、角色、用例说明、前置条件、后置条件、基本事件流、扩展流程、异常事件流、其他。请使用表格格式，纵向排列，每个用例分开，并用中文.”", DashScopeAgentOptions.builder().withAppId("fk198231f05237428c8087fa9242edd14d").build()));
        if (response == null || response.getResult() == null) {
            log.error("chat response is null");
        }

        AssistantMessage appOutput = response.getResult().getOutput();
        String content = appOutput.getText();

        DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput output = (DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput) appOutput.getMetadata().get("output");
        List<DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputDocReference> docReferences = output.docReferences();
        List<DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputThoughts> thoughts = output.thoughts();

        log.info("content:\n{}\n\n", content);
        CommonMarkdown2WordUtil.toDoc(content.replace("<br>", "  "), "橘子");

        if (docReferences != null && !docReferences.isEmpty()) {
            for (DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputDocReference docReference : docReferences) {
                log.info("{}\n\n", docReference);
            }
        }

        if (thoughts != null && !thoughts.isEmpty()) {
            for (DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputThoughts thought : thoughts) {
                log.info("{}\n\n", thought);
            }
        }
    }

}
