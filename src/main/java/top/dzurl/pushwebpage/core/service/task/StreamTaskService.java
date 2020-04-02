package top.dzurl.pushwebpage.core.service.task;

import org.springframework.beans.factory.annotation.Autowired;
import top.dzurl.pushwebpage.core.conf.PushTaskConf;
import top.dzurl.pushwebpage.core.model.BaseTaskParm;
import top.dzurl.pushwebpage.core.model.DockerCreate;
import top.dzurl.pushwebpage.core.model.TaskResult;
import top.dzurl.pushwebpage.core.type.StreamTaskState;
import top.dzurl.pushwebpage.core.type.StreamTaskType;
import top.dzurl.pushwebpage.core.util.OperatingSystemUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public abstract class StreamTaskService {

    /**
     * 默认的标签名称
     */
    public final static String DEFAULT_LABELS_Name = "StreamTaskName";
    public final static String DEFAULT_LABELS_Value = "StreamTaskNode";

    //默认的CPU时间
    private final static long Default_Cpu_Time = 100 * 1000;


    @Autowired
    private PushTaskConf pushTaskConf;


    /**
     * 任务类型
     *
     * @return
     */
    public abstract StreamTaskType taskType();


    /**
     * 执行
     *
     * @param baseParm
     * @return
     */
    public abstract TaskResult execute(BaseTaskParm baseParm);


    /**
     * 执行任务
     *
     * @param baseParm
     * @return
     */
    public TaskResult runTask(BaseTaskParm baseParm) {
        //系统可用资源监测
        StreamTaskState streamTaskState = this.checkOSAvailable();
        if (streamTaskState != null) {
            return new TaskResult(streamTaskState);
        }
        return this.execute(baseParm);
    }


    /**
     * 构建docker创建的对象
     *
     * @return
     */
    public DockerCreate buildDockerCreate() {
        var dockerCreate = new DockerCreate();
        //设置标识标签
        dockerCreate.setLabels(Map.of(DEFAULT_LABELS_Name, DEFAULT_LABELS_Value));

        //设置镜像
        dockerCreate.setImage(pushTaskConf.getImageUrl());

        //主机设置
        dockerCreate.setHostConfig(new HashMap<String, Object>() {{
            //限制CPU的核心数
            put("CpuPeriod", Default_Cpu_Time);
            put("CpuQuota", new BigDecimal(pushTaskConf.getLimitCpuCore() * Default_Cpu_Time).longValue());

            //设置网桥模式
            put("NetworkMode", pushTaskConf.getDockerNetWorkName());

        }});

        return dockerCreate;
    }


    /**
     * 检查当前系统是否可用
     *
     * @return
     */
    protected StreamTaskState checkOSAvailable() {

        //内存限制
        if (this.pushTaskConf.getReservationsMemory() > 0 && OperatingSystemUtil.getAvailableRAM() <= this.pushTaskConf.getReservationsMemory()) {
            return StreamTaskState.MemoryLimit;
        }

        //CPU限制
        if (this.pushTaskConf.getReservationsCpu() > 0 && OperatingSystemUtil.getAvailableCPU() <= this.pushTaskConf.getReservationsCpu()) {
            return StreamTaskState.CpuLimit;
        }

        return null;
    }


}
