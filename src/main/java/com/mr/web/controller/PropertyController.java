package com.mr.web.controller;

import com.mr.util.PropertyUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2017/7/13.
 */
@Controller
@RequestMapping("/prop")
public class PropertyController {
    @RequestMapping(value = "/db", method = RequestMethod.GET)
    @ResponseBody
    public String getProperty(@PathVariable("key") String key){
        return PropertyUtil.getProperty(key, "defaultValue");
    }
}
