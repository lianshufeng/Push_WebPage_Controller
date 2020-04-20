package top.dzurl.pushwebpage.core.service.task.forward;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import top.dzurl.pushwebpage.core.helper.DockerHelper;
import top.dzurl.pushwebpage.core.model.BaseTaskParm;
import top.dzurl.pushwebpage.core.model.DockerCreate;
import top.dzurl.pushwebpage.core.model.TaskResult;
import top.dzurl.pushwebpage.core.service.task.StreamTaskService;
import top.dzurl.pushwebpage.core.type.StreamTaskState;
import top.dzurl.pushwebpage.core.util.BeanUtil;
import top.dzurl.pushwebpage.core.util.GroovyUtil;

import java.util.HashMap;
import java.util.Map;

public abstract class SuperForwardTaskService extends StreamTaskService {


    @Autowired
    private DockerHelper dockerHelper;

    /**
     * 获取推流地址
     *
     * @return
     */
    public abstract String getFfmpeg_cmd_template();


    @Override
    public TaskResult execute(BaseTaskParm baseParm) {


        //创建推流容器
        DockerCreate dockerCreate = buildDockerCreate(baseParm);

        //挂载
        dockerCreate.getHostConfig().put("Binds", new String[]{"/dev/shm:/dev/shm"});


        //运行环境
        dockerCreate.setEnv(new String[]{
                        //推流地址
                        "FFMPEG_CMD=" + makeCmd(baseParm, getFfmpeg_cmd_template())
                }
        );
        //执行docker命令
        String id = this.dockerHelper.run(dockerCreate);
        return new TaskResult(StringUtils.hasText(id) ? StreamTaskState.Success : StreamTaskState.Error, id);
    }


    @Override
    protected void preParm(BaseTaskParm baseParm) {
        //不进行预处理参数
    }

    /**
     * 生产转发的命令行
     *
     * @param taskParm
     * @return
     */
    private static String makeCmd(BaseTaskParm taskParm, String template) {
        Map<String, Object> parm = new HashMap<String, Object>() {{
            putAll(BeanUtil.bean2Map(taskParm));
        }};

        //输出分辨率
        if (taskParm.getOutputWidth() != null && taskParm.getOutputHeight() != null) {
            parm.put("outputSize", String.format("-s %sx%s", taskParm.getOutputWidth(), taskParm.getOutputHeight()));
        } else {
            parm.put("outputSize", "");
        }

        return GroovyUtil.textTemplate(parm, template);
    }


}
