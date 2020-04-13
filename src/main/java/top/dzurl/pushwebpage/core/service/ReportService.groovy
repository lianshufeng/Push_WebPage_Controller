package top.dzurl.pushwebpage.core.service

import groovy.util.logging.Log
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
    private PushTaskConf pushTaskConf

    @Autowired
    private DockerHelper dockerHelper

    //线程池
    private ExecutorService executorService


    @Autowired
    void init() {
        if (pushTaskConf.getReportTime() > 0 && pushTaskConf.getReport() != null) {
            executorService = Executors.newFixedThreadPool(1)
            Timer timer = new Timer()
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                void run() {
                    executorService.execute(() -> {
                        try {
                            report(pushTaskConf.getReport())
                        } catch (Exception e) {
                            e.printStackTrace()
                        }
                    })
                }
            }, pushTaskConf.getReportTime(), pushTaskConf.getReportTime())
        }

    }

    @PreDestroy
    private void shutdown() {
        if (executorService != null) {
            executorService.shutdownNow()
        }
        executorService = null
    }


    /**
     * 报告
     */
    private void report(ReportModel model) {

        RequestReport report = new RequestReport()
        report.setOs(OperatingSystemUtil.getOSAvailableInfo())
        report.setPs(dockerHelper.ps())
        report.setOther(model.getOther())


        HttpModel httpModel = new HttpModel()
        httpModel.setUrl(model.getUrl())
        httpModel.setMethod(MethodType.Json)
        httpModel.setBody(report)
        log.info("request : -> " + httpModel)
        ResponseModel response = HttpClientUtil.request(httpModel)
        log.info("response : " + response)

        //处理需要删除的任务
        if (response.body && response.body['removeIds']) {
            response.body['removeIds'].each { it ->
                {
                    this.dockerHelper.rm(it)
                }
            }
        }

    }


}
