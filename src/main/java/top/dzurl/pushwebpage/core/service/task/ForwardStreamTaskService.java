package top.dzurl.pushwebpage.core.service.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.dzurl.pushwebpage.core.helper.DockerHelper;
import top.dzurl.pushwebpage.core.model.BaseTaskParm;
import top.dzurl.pushwebpage.core.model.TaskResult;
import top.dzurl.pushwebpage.core.type.StreamTaskType;

@Service
public class ForwardStreamTaskService extends StreamTaskService {


    @Autowired
    private DockerHelper dockerHelper;

    @Override
    public StreamTaskType taskType() {
        return StreamTaskType.Forward;
    }

    @Override
    public synchronized TaskResult execute(BaseTaskParm baseParm) {
        return null;
    }
}
