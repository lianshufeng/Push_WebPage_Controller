package top.dzurl.pushwebpage.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportModel {

    /**
     * 通知的地址
     */
    private String url;

    /**
     * 其他自定义参数
     */
    private Map<String, Object> other;


}
