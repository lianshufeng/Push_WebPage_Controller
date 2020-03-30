package top.dzurl.pushwebpage.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import top.dzurl.pushwebpage.core.util.JsonUtil;

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



    /**
     * 转换到命令行
     *
     * @return
     */
    public String toCmd() {
        String cmd = String.format(CmdTemplate, url);

        //method
        if (StringUtils.hasText(method)) {
            cmd += " -X " + method;
        }

        //是否json格式
        if (isJson()) {
            cmd += " -H \"Content-Type:application/json\"";
        }

        if (data != null) {
            cmd += " -d '" + (isJson() ? JsonUtil.toJson(data) : String.valueOf(data)) + "'";
        }

        return cmd;
    }

//    public static void main(String[] args) {
//        System.out.println(DockerCurlExecute.builder().url("http://localhost/containers/d21e78070a1c/exec").method("ss").json(true).data(Map.of("Cmd", "")).build().toCmd());
//    }


}
