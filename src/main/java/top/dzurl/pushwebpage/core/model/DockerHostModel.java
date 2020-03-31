package top.dzurl.pushwebpage.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DockerHostModel {

    private String HostIp = "0.0.0.0";

    private String HostPort;

    public DockerHostModel(String hostPort) {
        HostPort = hostPort;
    }
}
