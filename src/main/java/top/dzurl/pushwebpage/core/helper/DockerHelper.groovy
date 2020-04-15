package top.dzurl.pushwebpage.core.helper


import groovy.util.logging.Log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.FileCopyUtils
import org.springframework.util.StreamUtils
import org.springframework.util.StringUtils
import top.dzurl.pushwebpage.core.conf.PushTaskConf
import top.dzurl.pushwebpage.core.model.DockerCreate
import top.dzurl.pushwebpage.core.model.DockerCurlExecute
import top.dzurl.pushwebpage.core.model.DockerProcess
import top.dzurl.pushwebpage.core.service.task.StreamTaskService
import top.dzurl.pushwebpage.core.type.StreamTaskType
import top.dzurl.pushwebpage.core.util.JsonUtil

import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

@Log
@Component
class DockerHelper {


    @Autowired
    private PushTaskConf pushTaskConf


    /**
     * 取出本机的ip
     * @return
     */
    String getMyIp() {
        //取出自己的ip
        Process process = Runtime.getRuntime().exec("hostname -i")
        process.waitFor(10, TimeUnit.SECONDS)
        InputStream inputStream = process.getInputStream()
        def cmd_ret = StreamUtils.copyToString(inputStream, Charset.forName("UTF-8"))
        String[] ips = cmd_ret.split(" ")
        if (ips.length > 0) {
            return ips[0]
        }
        return null
    }


    /**
     * 获取容器ip
     * @param id
     * @return
     */
    String getContainerIp(String id) {
        def ret = executeCmd(new DockerCurlExecute(String.format("http://localhost/containers/%s/json", id)))
        return ret['NetworkSettings']['Networks'][this.pushTaskConf.getDockerNetWorkName()]['IPAddress']
    }


    /**
     * 获取容器的状态
     * @param id
     * @return
     */
    Map<String, Object> getContainerState(String id) {
        def ret = executeCmd(new DockerCurlExecute(String.format("http://localhost/containers/%s/json", id)))
        return (ret != null && ret != [] && ret['Id'] == id) ? ret['State'] : null
    }


    /**
     * 创建docker容器
     * @param dockerCreate
     * @return
     */
    String run(DockerCreate dockerCreate) {
        DockerCurlExecute createContainer = new DockerCurlExecute("http://localhost/containers/create", "POST")
        createContainer.setJson(true)
        createContainer.setData(dockerCreate)

        //执行命令行
        String id = executeCmd(createContainer)['Id']
        if (StringUtils.hasText(id)) {
            return executeCmd(new DockerCurlExecute(String.format("http://localhost/containers/%s/start", id), "POST")) == [:] ? id : null
        }


        return null
        //还需要start这个容器
    }


    /**
     * docker ps
     * @return
     */
    Object ps() {
        def ret = []
        executeCmd(new DockerCurlExecute("http://localhost/containers/json?all=true")).each { it ->
            if (it['Labels'] && it['Labels'][StreamTaskService.DEFAULT_LABELS_Name] == StreamTaskService.DEFAULT_LABELS_Value) {
                DockerProcess dockerProcess = new DockerProcess()
                dockerProcess.setId(it['Id'])
                dockerProcess.setCreateTime(it['Created'])
                dockerProcess.setNames(it['Names'])
                dockerProcess.setState(it['State'])

                //任务类型
                if (it['Labels'][StreamTaskService.DEFAULT_LABELS_StreamTaskType] != null) {
                    dockerProcess.setTaskType(StreamTaskType.valueOf(it['Labels'][StreamTaskService.DEFAULT_LABELS_StreamTaskType]))
                }

                ret.add(dockerProcess)
            }
        }
        return ret
    }


    /**
     * docker kill
     * @param id
     * @return
     */
    Object rm(String id) {
        return executeCmd(DockerCurlExecute.builder().url(String.format("http://localhost/containers/%s?force=true", id)).method("DELETE").build()) == [:]
    }


    /**
     * 执行命令行
     * @param isPost
     * @param returnObject
     * @param cmd
     * @return
     */
    private Object executeCmd(DockerCurlExecute execute) {
        String dockerSock = pushTaskConf.getDockerSock()
        List<String> cmds = new ArrayList<String>() {
            {
                add("curl")
                add("--unix-socket")
                add(dockerSock)
                addAll(execute.toCmd())
            }
        }

        //命令行
        String cmdLine = String.join(" ", cmds.toArray(new String[0]))
        log.info("cmd -> " + cmdLine)

        //生产sh脚本文件
        cmdLine = "#!/bin/bash\n" + cmdLine
        File outFile = new File("/dev/shm/" + UUID.randomUUID().toString().replaceAll("-", "") + ".sh")
        FileCopyUtils.copy(cmdLine.getBytes("UTF-8"), outFile)
        log.info("sh -> " + outFile)


        Process process = Runtime.getRuntime().exec("sh " + outFile.getPath())
        process.waitFor(10, TimeUnit.SECONDS)
        InputStream inputStream = process.getInputStream()
        def cmd_ret = StreamUtils.copyToString(inputStream, Charset.forName("UTF-8"))
        log.info("ret : " + cmd_ret)

        //删除临时的脚本文件
        outFile.delete()

        return StringUtils.hasText(cmd_ret) ? JsonUtil.toObject(cmd_ret, Object.class) : [:]
    }


}
