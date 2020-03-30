package top.dzurl.pushwebpage.core.helper

import groovy.util.logging.Log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.StreamUtils
import org.springframework.util.StringUtils
import top.dzurl.pushwebpage.core.conf.PushTaskConf
import top.dzurl.pushwebpage.core.model.DockerCurlExecute
import top.dzurl.pushwebpage.core.model.DockerProcess
import top.dzurl.pushwebpage.core.util.JsonUtil

import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

@Log
@Component
class DockerHelper {

    /**
     * 连接方式
     */
    private static final String request_url = "curl --unix-socket %s"


    @Autowired
    private PushTaskConf pushTaskConf


    private String meIp = "";

    @Autowired
    private void init() {

    }


    /**
     * docker ps
     * @return
     */
    Object ps() {
        def ret = []
        executeCmd(new DockerCurlExecute("http://localhost/containers/json")).each { it ->
            DockerProcess dockerProcess = new DockerProcess()
            dockerProcess.setId(it['Id'])
            dockerProcess.setCreateTime(it['Created'])
            dockerProcess.setNames(it['Names'])
            ret.add(dockerProcess)
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
     * @param id
     * @param cmd
     * @return
     */
    Object exec(String id, Object cmd) {
        return executeCmd(DockerCurlExecute.builder().url(String.format("http://localhost/containers/%s/exec", id)).method("POST").json(true).data(cmd).build())
    }


    /**
     * 执行命令行
     * @param isPost
     * @param returnObject
     * @param cmd
     * @return
     */
    private Object executeCmd(DockerCurlExecute execute) {
        //请求的url
//        String req_url = String.format(request_url, isPost ? "-XPOST" : "", pushTaskConf.getDockerSock())
        //命令行
//        String[] cmds = new String[cmd.length + 1]
//        cmds[0] = req_url
//        System.arraycopy(cmd, 0, cmds, 1, cmds.length - 1)

        //将要执行的命令行
//        String cmdLine = String.join(" ", cmds)
        String cmdLine = String.format(request_url, pushTaskConf.getDockerSock()) + " " + execute.toCmd()
        log.info("cmd : " + cmdLine)
        Process process = Runtime.getRuntime().exec(cmdLine)
        process.waitFor(10, TimeUnit.SECONDS)
        InputStream inputStream = process.getInputStream()
        def cmd_ret = StreamUtils.copyToString(inputStream, Charset.forName("UTF-8"))
        log.info("ret : " + cmd_ret)
        return StringUtils.hasText(cmd_ret) ? JsonUtil.toObject(cmd_ret, Object.class) : [:]
    }

}
