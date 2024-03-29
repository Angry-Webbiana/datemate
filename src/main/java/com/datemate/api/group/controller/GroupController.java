package com.datemate.api.group.controller;

import com.datemate.api.group.model.Group;
import com.datemate.api.group.service.GroupService;
import com.datemate.api.user.model.UserGroup;
import com.datemate.common.ServiceException;
import com.datemate.common.constants.Constants;
import com.datemate.common.controller.CommonController;
import com.datemate.common.json.JsonMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/group")
public class GroupController extends CommonController {

    @Resource
    private GroupService groupService;

    @RequestMapping(method = RequestMethod.GET, params = "groupId")
    @ResponseBody
    public JsonMessage getGroup(@RequestParam(name = "groupId") Integer groupId) {
        JsonMessage jsonMessage = new JsonMessage();

        if (log.isDebugEnabled()) {
            log.debug("Request Param : {}", groupId);
        }

        try {
            Group group = groupService.selectGroup(groupId);

            jsonMessage.addAttribute("group", group);
            jsonMessage.setResponseCode(Constants.SUCCESS);
        } catch (Exception e) {
            log.error("get Group Fail", e);
            jsonMessage.setErrorMsgWithCode(this.getMessage("DEFAULT_EXCEPTION"));
        }

        return jsonMessage;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public JsonMessage getGroupList() {
        JsonMessage jsonMessage = new JsonMessage();

        try {
            List<UserGroup> userGroupList = groupService.selectGroupListByUserSeq(this.getLoginUserSeq());

            jsonMessage.addAttribute("groupList", userGroupList);
            jsonMessage.setResponseCode(Constants.SUCCESS);
        } catch (Exception e) {
            log.error("get Group List Fail", e);
            jsonMessage.setErrorMsgWithCode(this.getMessage("DEFAULT_EXCEPTION"));
        }

        return jsonMessage;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public JsonMessage addGroup(@RequestBody Group group) {
        JsonMessage jsonMessage = new JsonMessage();
        if (log.isDebugEnabled()) {
            log.debug("Request Body : {}", group);
        }

        try {
            group.setGroupOwner(this.getLoginUserSeq());
            groupService.addGroup(group);

            jsonMessage.setResponseCode(Constants.SUCCESS);
        } catch (Exception e) {
            log.error("addGroup Fail", e);
            jsonMessage.setErrorMsgWithCode(this.getMessage("DEFAULT_EXCEPTION"));
        }

        return jsonMessage;
    }

    @RequestMapping(value = "/invite", method = RequestMethod.POST)
    @ResponseBody
    public JsonMessage inviteGroup(@RequestBody UserGroup userGroup) {
        JsonMessage jsonMessage = new JsonMessage();
        if (log.isDebugEnabled()) {
            log.debug("Request Body : {}", userGroup);
        }

        try {
            // TODO: 친구 상태인지 확인하는 기능 추가
            groupService.inviteGroup(userGroup);
            jsonMessage.setResponseCode(Constants.SUCCESS);
        } catch (Exception e) {
            log.error("inviteGroup Fail", e);
            jsonMessage.setErrorMsgWithCode(this.getMessage("DEFAULT_EXCEPTION"));
        }

        return jsonMessage;
    }

    @RequestMapping(value = "/leave", method = RequestMethod.PUT)
    @ResponseBody
    public JsonMessage leaveGroup(@RequestBody UserGroup userGroup) {
        JsonMessage jsonMessage = new JsonMessage();
        if (log.isDebugEnabled()) {
            log.debug("Request Body : {}", userGroup);
        }

        try {
            // TODO: Group Owner도 강퇴 할 수 있도록 구현
            // TODO: Group Owner가 나갔을 경우 로직도 구현
            if (userGroup.getUserSeq() == this.getLoginUserSeq()) {
                groupService.leaveGroup(userGroup);
            } else {
                throw new ServiceException(this.getMessage("NO_PERMISSION_EXCEPTION"));
            }
        } catch (ServiceException se) {
            jsonMessage.setErrorMsgWithCode(se.getMessage());
        } catch (Exception e) {
            jsonMessage.setErrorMsgWithCode(this.getMessage("DEFAULT_EXCEPTION"));
        }

        return jsonMessage;
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseBody
    public JsonMessage delGroup(@RequestBody Map<String, Object> paramMap) {
        JsonMessage jsonMessage = new JsonMessage();
        if (log.isDebugEnabled()) {
            log.debug("Request Body : {}", paramMap);
        }

        try {
            Integer groupId = (Integer) paramMap.get("groupId");
            Group group = groupService.selectGroup(groupId);

            if (this.getLoginUserSeq() == group.getGroupOwner()) {
                groupService.deleteGroup(groupId);
            } else {
                throw new ServiceException(this.getMessage("NO_PERMISSION_EXCEPTION"));
            }

            jsonMessage.setResponseCode(Constants.SUCCESS);
        } catch (ServiceException se) {
            jsonMessage.setErrorMsgWithCode(se.getMessage());
        } catch (Exception e) {
            log.error("delGroup Fail", e);
            jsonMessage.setErrorMsgWithCode(this.getMessage("DEFAULT_EXCEPTION"));
        }

        return jsonMessage;
    }
}
