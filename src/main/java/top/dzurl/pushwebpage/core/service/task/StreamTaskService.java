package top.dzurl.pushwebpage.core.service.task;

import org.springframework.beans.factory.annotation.Autowired;
import top.dzurl.pushwebpage.core.conf.PushTaskConf;
import top.dzurl.pushwebpage.core.model.BaseTaskParm;
import top.dzurl.pushwebpage.core.model.DockerCreate;
import top.dzurl.pushwebpage.core.model.TaskResult;
import top.dzurl.pushwebpage.core.type.StreamTaskState;
import top.dzurl.pushwebpage.core.type.StreamTaskType;
import top.dzurl.pushwebpage.core.util.OperatingSystemUtil;

import java.util.Map;

public abstract class StreamTaskService {

    /**
     * 默认的标签名称
     */
    public final static String DEFAULT_LABELS_Name = "StreamTaskName";
    public final static String DEFAULT_LABELS_Value = "StreamTaskNode";


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
     * 构建docker创建的对象
     *
     * @return
     */
    public DockerCreate buildDockerCreate() {
        var dockerCreate = new DockerCreate();
        dockerCreate.setLabels(Map.of(DEFAULT_LABELS_Name, DEFAULT_LABELS_Value));
        return dockerCreate;
    }


    /**
     * 检查当前系统是否可用
     *
     * @return
     */
    public StreamTaskState checkOSAvailable() {

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
