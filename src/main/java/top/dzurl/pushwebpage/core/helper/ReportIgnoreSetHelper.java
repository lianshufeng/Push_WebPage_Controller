package top.dzurl.pushwebpage.core.helper;

import lombok.experimental.Delegate;
import org.springframework.stereotype.Component;
import top.dzurl.pushwebpage.core.model.DockerProcess;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * 报告集合
 */
@Component
public class ReportIgnoreSetHelper {


    private interface ReportIgnoreSetMethod {

        /**
         * 添加需要忽略的报告id
         *
         * @param id
         * @return
         */
        boolean add(String id);

        /**
         * 删除忽略的id
         *
         * @param id
         * @return
         */
        boolean remove(String id);

        /**
         * 是否存在需要忽略的id
         *
         * @param id
         * @return
         */
        boolean contains(String id);
    }

    @Delegate(types = ReportIgnoreSetMethod.class)
    Vector<String> ignoreSet = new Vector<String>();


    /**
     * @param dps
     * @return
     */
    public Collection<DockerProcess> filter(DockerProcess[] dps) {
        return Arrays.stream(dps).filter((it) -> {
            return !this.contains(it.getId());
        }).collect(Collectors.toList());
    }


}
