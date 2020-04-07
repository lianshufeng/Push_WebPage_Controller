package top.dzurl.pushwebpage.core.service

import groovy.util.logging.Log
import lombok.SneakyThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import top.dzurl.pushwebpage.core.conf.PushTaskConf
import top.dzurl.pushwebpage.core.helper.DockerHelper
import top.dzurl.pushwebpage.core.model.ReportModel
import top.dzurl.pushwebpage.core.model.report.RequestReport
import top.dzurl.pushwebpage.core.util.OperatingSystemUtil
import top.dzurl.pushwebpage.core.util.apache.HttpClientUtil
import top.dzurl.pushwebpage.core.util.apache.HttpModel
import top.dzurl.pushwebpage.core.util.apache.MethodType
import top.dzurl.pushwebpage.core.util.apache.ResponseModel

import javax.annotation.PreDestroy
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 报告业务
 */
@Log
@Service
class ReportService {


    @Autowired
    private PushTaskConf pushTaskConf;

    @Autowired
    private DockerHelper dockerHelper;

    //线程池
    private ExecutorService executorService;


    @Autowired
    void init() {
        if (pushTaskConf.getReportTime() > 0 && pushTaskConf.getReports() != null) {
            executorService = Executors.newFixedThreadPool(pushTaskConf.getReports().size());
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                void run() {
                    for (Map.Entry<String, ReportModel> entry : pushTaskConf.getReports().entrySet()) {
                        executorService.execute(() -> {
                            report(entry.getValue());
                        });
                    }

                }
            }, pushTaskConf.getReportTime(), pushTaskConf.getReportTime());
        }

    }

    @PreDestroy
    private void shutdown() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
        executorService = null;
    }


    /**
     * 报告
     */
    @SneakyThrows
    private void report(ReportModel model) {
//        def ps = []
//        dockerHelper.ps().each { it ->
//            ps.add([
//                    'id'     : it['id'],
//                    'state'  : it['state'],
//                    'created': it['created']
//            ])
//        }


        RequestReport report = new RequestReport()
        report.setOs(OperatingSystemUtil.getOS())
        report.setPs(dockerHelper.ps())
        report.setOther(model.getOther())
//        def req = [
//                'os'   : OperatingSystemUtil.getOS(),
//                'ps'   : ps,
//                'other': model.getOther()
//        ]

        HttpModel httpModel = new HttpModel()
        httpModel.setUrl(model.getUrl())
        httpModel.setMethod(MethodType.Json)
        httpModel.setBody(report)
        log.info("request : -> " + httpModel)
        ResponseModel response = HttpClientUtil.request(httpModel)
        log.info("response : " + response)

    }


}