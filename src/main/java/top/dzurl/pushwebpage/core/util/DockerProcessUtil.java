package top.dzurl.pushwebpage.core.util;

import top.dzurl.pushwebpage.core.model.DockerProcess;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * docker 进程工具
 */
public class DockerProcessUtil {


    /**
     * 通过进程状态计算出hash
     *
     * @param ps
     * @return
     */
    public static String getHashByState(Collection<DockerProcess> ps) {

        List<String> items = ps.stream().map((it) -> {
            return it.getId() + "_" + it.getState();
        }).collect(Collectors.toList());

        //排序
        Collections.sort(items);

        //相加并计算Hash
        String rest = String.join(",", items.toArray(new String[0]));


        return HashUtil.md5(rest);
    }


}
