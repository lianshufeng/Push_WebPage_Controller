package top.dzurl.pushwebpage.core.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "push.task")
public class PushTaskConf {


    /**
     * docker.sock 路径
     */
    private String DockerSock = "/var/run/docker.sock";

    /**
     * 镜像地址
     */
    private String imageUrl = "registry.cn-chengdu.aliyuncs.com/1s/ffmpeg_chrome";

    /**
     * 镜像里的端口
     */
    private int remoteHostPort = 4444;


    /**
     * 网桥的名称,必须一致，否则可能无法通信
     */
    private String dockerNetWorkName = "stream_bridge";


    /**
     * 保留的内存单位兆字节
     */
    private double reservationsMemory = 1024 * 1.5;


    /**
     * 保留的CPU百分比
     */
    private float reservationsCpu = 0.2f;


}
