package top.dzurl.pushwebpage.core.controller;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.dzurl.pushwebpage.core.helper.DockerHelper;
import top.dzurl.pushwebpage.core.model.BaseTaskParm;
import top.dzurl.pushwebpage.core.model.DockerProcess;
import top.dzurl.pushwebpage.core.model.report.RequestReport;
import top.dzurl.pushwebpage.core.model.report.ResponseReport;
import top.dzurl.pushwebpage.core.service.StreamService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

@Log
@RestController
public class StreamController {

    @Autowired
    private DockerHelper dockerHelper;

    @Autowired
    private StreamService streamService;


    /**
     * 创建推流任务
     *
     * @param parm
     * @return
     */
    @RequestMapping("create.json")
    public Object createByJson(@RequestBody BaseTaskParm parm) {
        return _create(parm);
    }

    @RequestMapping("create")
    public Object create(BaseTaskParm parm) {
        return _create(parm);
    }


    @RequestMapping("ps")
    public Object list() {
        return dockerHelper.ps();
    }


    @RequestMapping("rm")
    public Object rm(String... id) {
        var ret = new HashMap<>();
        for (String i : id) {
            ret.put(i, this.dockerHelper.rm(i));
        }
        return ret;
    }


    /**
     * 报告
     *
     * @param req
     * @return
     */
    @RequestMapping("report")
    public ResponseReport report(@RequestBody RequestReport req) {
        log.info(String.format("report :  %s", req));
        return ResponseReport.builder().removeIds(
                req.getPs().stream().filter((it) -> {
                    return "exited".equals(it.getState());
                }).map((it) -> {
                    return it.getId();
                }).collect(Collectors.toList()))
                .build();
    }


    /**
     * 创建推流任务
     *
     * @param parm
     * @return
     */
    private Object _create(BaseTaskParm parm) {
        Assert.hasText(parm.getUrl(), "源数据地址不能为空");
        Assert.hasText(parm.getPushUrl(), "推流地址不能为空");
        return this.streamService.create(parm);
    }


}
