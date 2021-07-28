package cn.linked.link.controller;

import cn.linked.link.entity.*;
import cn.linked.link.service.ChatService;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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

    // 获取ChatGroup信息，MainInfo表示获取的ChatGroup信息的memberList只包含主要成员的信息
    @PostMapping("/getChatGroup")
    public HttpResult<List<ChatGroup>> getChatGroup(HttpServletRequest request) {
        HttpResult<List<ChatGroup>> result = new HttpResult<>();
        result.setCode(HttpResult.CODE_SUCCESS);
        result.setData(chatService.getChatGroup((Long) request.getSession(false).getAttribute(User.STRING_KEY_ID)));
        return result;
    }

    @PostMapping("/getChatGroupById")
    public HttpResult<ChatGroup> getChatGroupById(String groupId) {
        HttpResult<ChatGroup> result = new HttpResult<>();
        result.setCode(HttpResult.CODE_SUCCESS);
        result.setData(chatService.getChatGroupById(groupId));
        return result;
    }

    @PostMapping("/getChatGroupMember")
    public HttpResult<List<ChatGroupMember>> getChatGroupMember(String groupId) {
        HttpResult<List<ChatGroupMember>> httpResult = new HttpResult<>();
        httpResult.setCode(HttpResult.CODE_SUCCESS);
        httpResult.setData(chatService.getChatGroupMember(groupId));
        return httpResult;
    }

    @PostMapping("/getUserChatGroupMember")
    public HttpResult<ChatGroupMember> getUserChatGroupMember(String groupId, Long userId) {
        HttpResult<ChatGroupMember> httpResult = new HttpResult<>();
        httpResult.setCode(HttpResult.CODE_SUCCESS);
        httpResult.setData(chatService.getUserChatGroupMember(groupId, userId));
        return httpResult;
    }

    @PostMapping("/setUserHaveReadMessageMaxSequenceNum")
    public HttpResult<Boolean> setUserHaveReadMessageMaxSequenceNum(String groupId, Long maxSequenceNum, HttpServletRequest request) {
        HttpResult<Boolean> httpResult = new HttpResult<>();
        httpResult.setCode(HttpResult.CODE_SUCCESS);
        Long userId = (Long) request.getSession(false).getAttribute(User.STRING_KEY_ID);
        httpResult.setData(chatService.setUserHaveReadMessageMaxSequenceNum(userId, groupId, maxSequenceNum));
        return httpResult;
    }

    /**
     * 获取用户能收到的最新消息
     *      根据不同ChatMessage类型 可能有不同获取策略
     * */
    @PostMapping("/getUserNewestChatMessage")
    public HttpResult<List<ChatMessage>> getUserNewestChatMessage(HttpServletRequest request) {
        Long userId = (Long) request.getSession(false).getAttribute(User.STRING_KEY_ID);
        HttpResult<List<ChatMessage>> httpResult = new HttpResult<>();
        httpResult.setCode(HttpResult.CODE_SUCCESS);
        httpResult.setData(chatService.getUserNewestChatMessage(userId));
        return httpResult;
    }

}
