package top.dzurl.pushwebpage.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OSAvailableInfo {

    /**
     * 内存
     */
    private double mem;

    /**
     * CPU
     */
    private double cpu;
}
