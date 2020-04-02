package top.dzurl.pushwebpage.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import top.dzurl.pushwebpage.core.model.BaseTaskParm;
import top.dzurl.pushwebpage.core.model.TaskResult;
import top.dzurl.pushwebpage.core.service.task.StreamTaskService;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StreamService {

    private Map<String, Optional<StreamTaskService>> streamTaskServic = new ConcurrentHashMap<>();

    @Autowired
    private void init(ApplicationContext applicationContext) {
        this.streamTaskServic.clear();
        applicationContext.getBeansOfType(StreamTaskService.class).values().forEach((it) -> {
            this.streamTaskServic.put(String.valueOf(it.taskType()), Optional.of(it));
        });
    }

    /**
     * 创建任务
     *
     * @param taskParm
     * @return
     */
    public TaskResult create(BaseTaskParm taskParm) {
        return this.streamTaskServic.get(String.valueOf(taskParm.getTaskType())).map((it) -> {
            return it.runTask(taskParm);
        }).orElse(null);
    }


}
