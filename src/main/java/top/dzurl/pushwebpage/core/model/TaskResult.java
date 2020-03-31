package top.dzurl.pushwebpage.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.dzurl.pushwebpage.core.type.StreamTaskState;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResult<T> {

    /**
     * 状态
     */
    private StreamTaskState state;

    /**
     * 内容
     */
    private T content;


    public TaskResult(StreamTaskState state) {
        this.state = state;
    }
}
