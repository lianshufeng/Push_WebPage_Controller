package top.dzurl.pushwebpage.core.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class ResponseReport {

    /**
     * 需要删除的id
     */
    private String[] removeIds;


}
