package top.dzurl.pushwebpage.core.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;
import top.dzurl.pushwebpage.core.model.DockerProcess;
import top.dzurl.pushwebpage.core.util.apache.HttpClientUtil;
import top.dzurl.pushwebpage.core.util.apache.HttpModel;
import top.dzurl.pushwebpage.core.util.apache.MethodType;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

/**
 * 报告集合
 */

@Log
@Component
public class ReportIgnoreHelper {


    Queue<Item> queue = new LinkedList<Item>();


    /**
     * 添加
     *
     * @return
     */
    public String add() {
        synchronized (queue) {
            Item item = new Item();
            queue.offer(item);
            return item.getId();
        }
    }


    /**
     * 删除
     *
     * @param id
     */
    public void remove(String id) {
        synchronized (queue) {
            //找到需要删除的集合
            Set<Item> items = queue.stream().filter((it) -> {
                return it.getId().equals(id);
            }).collect(Collectors.toSet());
            //删除队列中的元素
            items.forEach((it) -> {
                queue.remove(it);
            });
        }
    }


    /**
     * @param dps
     * @return
     */
    public Collection<DockerProcess> filter(DockerProcess[] dps) {
        synchronized (queue) {
            Item item = queue.peek();
            return Arrays.stream(dps).filter((it) -> {
                //可靠下一秒，保证秒级处理
                return ((it.getCreateTime() + 1) * 1000) < (item == null ? Long.MAX_VALUE : item.getCreateTime());
            }).collect(Collectors.toList());
        }
    }


    @Data
    @AllArgsConstructor
    public static class Item {
        private String id;
        private long createTime;

        public Item() {
            this.id = UUID.randomUUID().toString();
            this.createTime = System.currentTimeMillis();
        }
    }


    public static void main(String[] args) {
        int size = 10;
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(size);

        for (int i = 0; i < size; i++) {
            executorService.execute(() -> {
                task(executorService);
            });
        }

    }

    @SneakyThrows
    private static void task(ScheduledExecutorService executorService) {
        HttpModel httpModel = new HttpModel();
        httpModel.setUrl("http://192.168.0.102:8080/create");
        httpModel.setMethod(MethodType.Post);
        httpModel.setBody("pushUrl=rtmp://push.live.aiyilearning.com/app/test1?auth_key=1587106336-4d214cefd886435882f350075a875e85-0-cfefbd0fca7f5e959aa7fd4a3ca7e937&url=http://web.dev.aiyilearning.com/lpws/#/?jobId=5e9830233c04b6712749a798");
        HttpClientUtil.request(httpModel);


        Thread.sleep(50);

        executorService.execute(() -> {
            task(executorService);
        });
    }

}
