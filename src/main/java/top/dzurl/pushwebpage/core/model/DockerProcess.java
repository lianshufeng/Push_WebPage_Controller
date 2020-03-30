package top.dzurl.pushwebpage.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DockerProcess {

    /**
     * id
     */
    private String id;


    /**
     * 容器名称
     */
    private List<String> names;


    /**
     * 创建时间
     */
    private long createTime;


}
