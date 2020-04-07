package top.dzurl.pushwebpage.core.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class RequestReport {

    /**
     * 系统信息
     */
    private Map<String, Double> os;

    /**
     * 进程列表
     */
    private Collection ps;


    /**
     * 其他参数
     */
    private Map<String, Object> other;


}
