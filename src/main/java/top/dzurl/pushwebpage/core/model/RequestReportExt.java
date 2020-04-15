package top.dzurl.pushwebpage.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import top.dzurl.pushwebpage.core.model.report.RequestReport;

/**
 * 请求报表
 */

@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RequestReportExt extends RequestReport {

    /**
     * hash 值
     */
    private String hash;

    /**
     * 成功发布时间
     */
    private long publishTime;


}
