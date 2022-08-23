package org.dulab.adapcompounddb.site.controllers.ajax;

import org.dulab.adapcompounddb.site.controllers.utils.ControllerUtils;
import org.dulab.adapcompounddb.site.services.search.GroupSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
public class GroupSearchAjaxController {

    private final GroupSearchService groupSearchService;

    public GroupSearchAjaxController(GroupSearchService groupSearchService) {
        this.groupSearchService = groupSearchService;
    }

    @RequestMapping(value = "/ajax/group_search/error", method = RequestMethod.GET)
    @ResponseBody
    public String groupSearchError(HttpSession session) {
        return (String) session.getAttribute(ControllerUtils.GROUP_SEARCH_ERROR_ATTRIBUTE_NAME);
    }
}
