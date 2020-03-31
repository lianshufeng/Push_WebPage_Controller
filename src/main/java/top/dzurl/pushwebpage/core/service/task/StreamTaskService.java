package top.dzurl.pushwebpage.core.service.task;

import top.dzurl.pushwebpage.core.model.BaseTaskParm;
import top.dzurl.pushwebpage.core.model.TaskResult;
import top.dzurl.pushwebpage.core.type.StreamTaskType;

public abstract class StreamTaskService {


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






}
