package top.dzurl.pushwebpage.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DockerCreate {


    /**
     * 镜像
     */
    private String Image;

    /**
     * 命令行
     */
    private String Cmd;


    /**
     * 环境变量 :  xx=11
     */
    private String[] Env;


    /**
     * 挂在的目录 : xx:11
     */
    private String[] Binds;


    /**
     * 网桥
     */
    private String NetworkMode;


    /**
     * 暴露的端口
     */
    private Map<String, Map<String, String>> ExposedPorts;


    /**
     * 主机配置
     */
    private Map<String, Map<String, Object>> HostConfig;

}
