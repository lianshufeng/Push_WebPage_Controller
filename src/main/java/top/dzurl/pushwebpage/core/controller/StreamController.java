package top.dzurl.pushwebpage.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.dzurl.pushwebpage.core.helper.DockerHelper;
import top.dzurl.pushwebpage.core.model.BaseTaskParm;
import top.dzurl.pushwebpage.core.service.StreamService;

import java.util.HashMap;

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
     * 创建推流任务
     *
     * @param parm
     * @return
     */
    private Object _create(BaseTaskParm parm) {
        return this.streamService.create(parm);
    }


}
