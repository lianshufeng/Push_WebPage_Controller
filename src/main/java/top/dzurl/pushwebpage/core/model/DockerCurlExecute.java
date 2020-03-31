package top.dzurl.pushwebpage.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import top.dzurl.pushwebpage.core.util.JsonUtil;

import java.util.ArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DockerCurlExecute {


    private final static String CmdTemplate = "%s";

    /**
     * 访问的地址
     */
    private String url;

    /**
     * 协议 : post, delete 等
     */
    private String method;

    /**
     * 是否json格式
     */
    private boolean json;

    /**
     * 数据
     */
    private Object data;


    public DockerCurlExecute(String url) {
        this.url = url;
    }

    public DockerCurlExecute(String url, String method) {
        this.url = url;
        this.method = method;
    }

    /**
     * 转换到命令行
     *
     * @return
     */
    public String[] toCmd() {
        var cmds = new ArrayList<>();
        cmds.add(url);

        //method
        if (StringUtils.hasText(method)) {
            cmds.add("-X");
            cmds.add(method);
        }

        //是否json格式
        if (isJson()) {
            cmds.add("-H");
            cmds.add("\"Content-Type: application/json\"");
        }

        if (data != null) {
            cmds.add("-d");
            cmds.add("'" + (isJson() ? JsonUtil.toJson(data) : String.valueOf(data)) + "'");
        }

        return cmds.toArray(new String[0]);
    }

//    public static void main(String[] args) {
//        System.out.println(DockerCurlExecute.builder().url("http://localhost/containers/d21e78070a1c/exec").method("ss").json(true).data(Map.of("Cmd", "")).build().toCmd());
//    }


}
