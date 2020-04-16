package top.dzurl.pushwebpage.core.service

import groovy.util.logging.Log
import org.apache.http.auth.AUTH
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import top.dzurl.pushwebpage.core.conf.PushTaskConf
import top.dzurl.pushwebpage.core.helper.DockerHelper
import top.dzurl.pushwebpage.core.helper.ReportIgnoreSetHelper
import top.dzurl.pushwebpage.core.model.DockerProcess
import top.dzurl.pushwebpage.core.model.ReportModel
import top.dzurl.pushwebpage.core.model.RequestReportExt
import top.dzurl.pushwebpage.core.util.DockerProcessUtil
import top.dzurl.pushwebpage.core.util.OperatingSystemUtil
import top.dzurl.pushwebpage.core.util.apache.HttpClientUtil
import top.dzurl.pushwebpage.core.util.apache.HttpModel
import top.dzurl.pushwebpage.core.util.apache.MethodType
import top.dzurl.pushwebpage.core.util.apache.ResponseModel

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

    @Autowired
    private ReportIgnoreSetHelper reportIgnoreSetHelper;


    //最后一次通次成功的报告
    private RequestReportExt lastReport


    /**
     * 报告
     */
    public synchronized void report() {
        ReportModel model = pushTaskConf.getReport()

        //是否有配置报告
        if (model == null) {
            return
        }


        //请求报表
        RequestReportExt nowReport = getRequestReport()

        //是否需要节流
        if (lastReport != null && lastReport.getHash() == nowReport.getHash()) {
            log.info("重复报告，节流处理:" + nowReport.getHash())
            return
        }


        //进行网络请求
        HttpModel httpModel = new HttpModel()
        httpModel.setUrl(model.getUrl())
        httpModel.setMethod(MethodType.Json)
        httpModel.setBody(nowReport)
        log.info("request : -> " + httpModel)
        ResponseModel response = HttpClientUtil.request(httpModel)
        log.info("response : " + response)


        //状态判断,成功则缓存为最后一次报告数据
        if (response.code == 200) {
            lastReport = nowReport
        }


        //处理需要删除的任务
        if (response.body && response.body['removeIds']) {
            response.body['removeIds'].each { it ->
                {
                    this.dockerHelper.rm(it)
                }
            }
        }
    }


    /**
     * 获取将要报告的数据
     * @return
     */
    public RequestReportExt getRequestReport() {


        //取出docker列表
        def ps = reportIgnoreSetHelper.filter(dockerHelper.ps())


        ReportModel model = pushTaskConf.getReport()
        RequestReportExt report = new RequestReportExt()
        report.setOs(OperatingSystemUtil.getOSAvailableInfo())
        report.setPs(ps)
        report.setOther(model.getOther())

        //设置hash
        report.setHash(DockerProcessUtil.getHashByState(ps))


        return report
    }


}
