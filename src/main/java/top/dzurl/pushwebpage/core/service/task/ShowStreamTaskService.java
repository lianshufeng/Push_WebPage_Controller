package top.dzurl.pushwebpage.core.service.task;

import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.model.Frame;
import lombok.SneakyThrows;
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
import top.dzurl.pushwebpage.core.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;

@Log
@Service
public class ShowStreamTaskService extends StreamTaskService {

    private final static String ffprobe_cmd_template = "ffprobe -v quiet -print_format json -show_format -show_streams ${url} ";

    private final static String dev_shm_path = "/dev/shm/";

    @Autowired
    private DockerHelper dockerHelper;

    @Override
    public StreamTaskType taskType() {
        return StreamTaskType.ShowStream;
    }

    @Override
    @SneakyThrows
    public TaskResult execute(BaseTaskParm baseParm) {


        //构建命令行
        Map<String, Object> parm = new HashMap<String, Object>() {{
            putAll(BeanUtil.bean2Map(baseParm));
        }};

        String ffmpeg_cmd = GroovyUtil.textTemplate(parm, ffprobe_cmd_template);


        //创建推流容器
        DockerCreate dockerCreate = buildDockerCreate(baseParm);

        //挂载
        dockerCreate.getHostConfig().put("Binds", new String[]{dev_shm_path + ":" + dev_shm_path});


        //运行环境
        dockerCreate.setEnv(new String[]{
                        //推流地址
                        "FFMPEG_CMD=" + ffmpeg_cmd
                }
        );
        //执行docker命令
        String dockerId = this.dockerHelper.run(dockerCreate);

        //取出日志
        String json = getLogs(dockerId).trim();

        log.info("logs : " + json);


        //删除进程
        this.dockerHelper.rm(dockerId);
        return new TaskResult(StringUtils.hasText(json) ? StreamTaskState.Success : StreamTaskState.Error, JsonUtil.toObject(json, Object.class));

    }


    @SneakyThrows
    private String getLogs(String dockerId) {
        //取出日志
        StringBuilder sb = new StringBuilder();

        //记录状态
        var recordState = new HashMap<String, Boolean>();
        String startRecord = "StartRecord";


        dockerHelper.getDockerClient().logContainerCmd(dockerId).withStdOut(true).withFollowStream(true).exec(new ResultCallbackTemplate() {
            @Override
            @SneakyThrows
            public void onNext(Object object) {
                Frame frame = (Frame) object;
                String line = new String(frame.getPayload(), "UTF-8");

                //不记录空行
                if (line.equals("\n")) {
                    return;
                }

                //开始记录json数据
                if (line.equals("{\n")) {
                    recordState.put(startRecord, true);
                }

                if (recordState.containsKey(startRecord)) {
                    sb.append(line);
                }

            }
        }).awaitCompletion();
        return sb.toString();
    }

    @Override
    protected void preParm(BaseTaskParm baseParm) {
        //不进行预处理参数
    }
}
