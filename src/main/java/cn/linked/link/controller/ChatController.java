package cn.linked.link.controller;

import cn.linked.link.entity.ChatMessage;
import cn.linked.link.entity.HttpResult;
import cn.linked.link.service.ChatService;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/chatController")
public class ChatController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Setter
    @Resource
    private ChatService chatService;

    @PostMapping("/getChatMessage")
    public HttpResult<List<ChatMessage>> getChatMessage(String groupId, Long maxSequenceNumber, Integer num) {
        List<ChatMessage> messageList = chatService.getChatMessage(groupId, maxSequenceNumber, num);
        HttpResult<List<ChatMessage>> httpResult = new HttpResult<>();
        httpResult.setCode(HttpResult.CODE_SUCCESS);
        httpResult.setData(messageList);
        return httpResult;
    }

}
