package top.dzurl.pushwebpage.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 转换发任务
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskParm extends BaseParm {

    /**
     * 推流地址
     */
    private String pullUrl;

    /**
     * 拉流地址
     */
    private String pushUrl;

}
