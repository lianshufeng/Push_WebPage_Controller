package top.dzurl.pushwebpage.core.type;

/**
 * 留任务的状态
 */
public enum StreamTaskState {

    Success("成功"),

    Error("错误"),

    MemoryLimit("内存限制"),

    CpuLimit("Cpu限制");


    StreamTaskState(String remark) {
        this.remark = remark;
    }

    private String remark;


}
