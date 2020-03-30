package top.dzurl.pushwebpage.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.dzurl.pushwebpage.core.helper.DockerHelper;
import top.dzurl.pushwebpage.core.model.TaskParm;

import java.util.HashMap;

@RestController
public class PushStreamController {

    @Autowired
    private DockerHelper dockerHelper;


    /**
     * 创建推流任务
     *
     * @param parm
     * @return
     */
    @RequestMapping("run.json")
    public Object createByJson(@RequestBody TaskParm parm) {
        return _create(parm);
    }

    @RequestMapping("run")
    public Object create(TaskParm parm) {
        return _create(parm);
    }


    @RequestMapping("ps")
    public Object list() {
        return dockerHelper.ps();
    }


    @RequestMapping("rm")
    public Object delete(String... id) {
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
    private Object _create(TaskParm parm) {
        return null;
    }


}
