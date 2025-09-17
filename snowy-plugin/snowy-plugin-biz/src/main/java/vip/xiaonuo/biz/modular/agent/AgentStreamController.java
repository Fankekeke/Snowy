package vip.xiaonuo.biz.modular.agent;

import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgent;
import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgentOptions;
import com.alibaba.cloud.ai.dashscope.api.DashScopeAgentApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AI流式输出控制器
 *
 * @author FanK
 * @date 2025/9/17 10:23
 **/
@RestController
@RequestMapping("/ai/stream")
@Slf4j
public class AgentStreamController {

    private DashScopeAgent agent;

    @Value("${spring.ai.dashscope.agent.app-id}")
    private String appId;

    public AgentStreamController(DashScopeAgentApi dashscopeAgentApi) {
        this.agent = new DashScopeAgent(dashscopeAgentApi,
                DashScopeAgentOptions.builder()
                        .withSessionId("current_session_id")
                        .withIncrementalOutput(true)
                        .withHasThoughts(true)
                        .build());
    }

    @GetMapping(value = "/bailian/agent/stream", produces = "text/event-stream")
    public Flux<String> stream(@RequestParam(value = "message",
            defaultValue = "你好，请问你的知识库文档主要是关于什么内容的?") String message) {
        return agent.stream(new Prompt(message, DashScopeAgentOptions.builder().withAppId(appId).build())).mapNotNull(response -> {
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

            return content;
        });
    }
}
