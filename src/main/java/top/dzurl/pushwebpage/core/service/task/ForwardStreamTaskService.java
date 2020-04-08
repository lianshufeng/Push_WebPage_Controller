package top.dzurl.pushwebpage.core.service.task;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.dzurl.pushwebpage.core.helper.DockerHelper;
import top.dzurl.pushwebpage.core.model.BaseTaskParm;
import top.dzurl.pushwebpage.core.model.DockerCreate;
import top.dzurl.pushwebpage.core.model.TaskResult;
import top.dzurl.pushwebpage.core.type.StreamTaskState;
import top.dzurl.pushwebpage.core.type.StreamTaskType;
import top.dzurl.pushwebpage.core.util.BeanUtil;
import top.dzurl.pushwebpage.core.util.GroovyUtil;

import java.util.HashMap;
import java.util.Map;

@Log
@Service
public class ForwardStreamTaskService extends StreamTaskService {

    private static final String ffmpeg_cmd_template = "ffmpeg -i ${url}  -vcodec libx264 -preset ultrafast  -r ${frameRate} -b:v ${vedioBitrate}k -b:a ${audioBitrate}k ${outputSize} -f flv ${pushUrl} ";

    @Autowired
    private DockerHelper dockerHelper;

    @Override
    public StreamTaskType taskType() {
        return StreamTaskType.Forward;
    }

    @Override
    public TaskResult execute(BaseTaskParm baseParm) {


        //创建推流容器
        DockerCreate dockerCreate = buildDockerCreate(baseParm);

        //挂载
        dockerCreate.setBinds(new String[]{"/dev/shm:/dev/shm"});


        //运行环境
        dockerCreate.setEnv(new String[]{
                        //推流地址
                        "FFMPEG_CMD=" + makeCmd(baseParm)
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
    private static String makeCmd(BaseTaskParm taskParm) {
        Map<String, Object> parm = new HashMap<String, Object>() {{
            putAll(BeanUtil.bean2Map(taskParm));
        }};

        //输出分辨率
        if (taskParm.getOutputWidth() != null && taskParm.getOutputHeight() != null) {
            parm.put("outputSize", String.format("-s %sx%s", taskParm.getOutputWidth(), taskParm.getOutputHeight()));
        } else {
            parm.put("outputSize", "");
        }

        return GroovyUtil.textTemplate(parm, ffmpeg_cmd_template);
    }


    public static void main(String[] args) {
        BaseTaskParm taskParm = new BaseTaskParm();
        taskParm.setOutputWidth(800l);
        taskParm.setOutputHeight(500l);
        taskParm.setUrl("rtmp://pull.live.aiyilearning.com/app/test1?auth_key=1586353328-7aebeb9c49a5422ea49da4e5ec875e26-0-53eaf1a81b071c23b770ba7acb690a5d");
        taskParm.setPushUrl("rtmp://push.live.aiyilearning.com/app/test2?auth_key=1586427049-ed3c27629b6c4b92923b040b76cc5411-0-6734fbc67ed1b890e92b8d71134ad2c0");

        System.out.println(makeCmd(taskParm));

    }

}
