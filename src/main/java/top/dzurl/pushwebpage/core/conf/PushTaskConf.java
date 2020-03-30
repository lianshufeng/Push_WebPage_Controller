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
    private  String imageUrl = "registry.cn-chengdu.aliyuncs.com/1s/ffmpeg_chrome";




}
