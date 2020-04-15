package top.dzurl.pushwebpage.core.task;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.dzurl.pushwebpage.core.conf.PushTaskConf;
import top.dzurl.pushwebpage.core.service.ReportService;


/**
 * 报告调度器
 */
@Log
@Component
@EnableScheduling
public class ReportTask {


    @Autowired
    private PushTaskConf pushTaskConf;

    @Autowired
    private ReportService reportService;

    //docker的客户端
    private DockerClient docker;


    //记录将要报告的次数
    private Long recordReportCount = 0L;


    @Autowired
    private void init() {
        createDockerClient();
        listenReport();
    }

    /**
     * 生产docker的java客户端
     */
    private void createDockerClient() {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("unix://" + pushTaskConf.getDockerSock())
                .build();
        docker = DockerClientBuilder.getInstance(config).build();
    }

    /**
     * 监视docker的事件
     */
    @SneakyThrows
    private void listenReport() {
        docker.eventsCmd()
                .exec(new ResultCallbackTemplate() {
                    @Override
                    public void onNext(Object item) {
                        log.info(String.format("docker event  '%s'.", item));
                        addReportsRecords();
                    }
                });
    }


    @Scheduled(fixedDelay = 60 * 1000)
    public void cycleReport() {
        addReportsRecords();
    }


    /**
     * 添加一次报告
     */
    private synchronized void addReportsRecords() {
        synchronized (recordReportCount) {
            recordReportCount++;
        }
    }

    /**
     * 重置报告
     *
     * @return
     */
    private synchronized boolean resetReportsRecords() {
        boolean ret = false;
        synchronized (recordReportCount) {
            if (recordReportCount > 0) {
                recordReportCount = 0l;
                ret = true;
            }
        }
        return ret;
    }


    @Scheduled(fixedDelay = 1000)
    public void checkAndPushReport() {
        if (resetReportsRecords()) {
            reportService.report();
        }
    }


}
