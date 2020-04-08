package top.dzurl.pushwebpage.core.model.report;

import lombok.*;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class ResponseReport {

    /**
     * 需要删除的id
     */
    private Collection<String> removeIds;


}
