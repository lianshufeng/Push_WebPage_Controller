package top.dzurl.pushwebpage.core.service.task;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.dzurl.pushwebpage.core.conf.PushTaskConf;
import top.dzurl.pushwebpage.core.helper.DockerHelper;
import top.dzurl.pushwebpage.core.model.BaseTaskParm;
import top.dzurl.pushwebpage.core.model.DockerCreate;
import top.dzurl.pushwebpage.core.model.TaskResult;
import top.dzurl.pushwebpage.core.type.StreamTaskState;
import top.dzurl.pushwebpage.core.type.StreamTaskType;

import java.math.BigDecimal;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PushWebPageTaskService extends StreamTaskService {


    @Autowired
    private DockerHelper dockerHelper;

    @Autowired
    private PushTaskConf pushTaskConf;

    @Override
    public StreamTaskType taskType() {
        return StreamTaskType.PushWebPage;
    }

    @Override
    public TaskResult execute(BaseTaskParm baseParm) {

        //创建推流容器
        DockerCreate dockerCreate = buildDockerCreate(baseParm);

        //挂载
        dockerCreate.setBinds(new String[]{"/dev/shm:/dev/shm"});


        //运行环境
        dockerCreate.setEnv(new String[]{
                        //推流地址
                        "STREAM_URL=" + baseParm.getPushUrl(),
                        //屏幕分辨率
                        "SCREEN_WIDTH=" + String.valueOf(baseParm.getScreenWidth()),
                        "SCREEN_HEIGHT=" + String.valueOf(baseParm.getScreenHeight()),
                        //输出分辨率
                        "Output_WIDTH=" + String.valueOf(baseParm.getOutputWidth()),
                        "Output_HEIGHT=" + String.valueOf(baseParm.getOutputHeight()),
                        //码率
                        "Vedio_Bitrate=" + String.valueOf(baseParm.getVedioBitrate() + "k"),
                        "Audio_Bitrate=" + String.valueOf(baseParm.getAudioBitrate() + "k"),
                        //帧率
                        "FRAMERATE=" + String.valueOf(baseParm.getFrameRate()),
                        //设置session超时时间
                        "SE_OPTS=-sessionTimeout " + String.valueOf(baseParm.getSessionTimeout())

                }
        );


        String id = this.dockerHelper.run(dockerCreate);
        if (StringUtils.hasText(id)) {
            //连接并通信
            String remoteHost = this.dockerHelper.getContainerIp(id) + ":" + this.pushTaskConf.getRemoteHostPort();
            log.info("remote ip : " + remoteHost);
            if (openWebPageUrl(remoteHost, baseParm, 20)) {
                return new TaskResult(StreamTaskState.Success, id);
            }
        }


        //失败尝试结束这个进程
        this.dockerHelper.rm(id);
        return new TaskResult(StreamTaskState.Error);
    }


    /**
     * 连接并设置远程网页访问的地址
     */
    @SneakyThrows
    private boolean openWebPageUrl(String remoteHost, BaseTaskParm baseParm, int tryCount) {
        final Boolean[] ret = new Boolean[]{false};
        //阻塞当前的主线程
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(() -> {
            for (int i = 0; i < tryCount; i++) {
                WebDriver webDriver = null;
                try {
                    webDriver = openWebPage(remoteHost, baseParm);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //连接成功则退出，并标记结束
                if (webDriver != null) {
                    ret[0] = true;
                    break;
                }

                //延迟
                try {
                    Thread.sleep(500l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //激活阻塞的主线程
            countDownLatch.countDown();
        }).start();
        countDownLatch.await(60, TimeUnit.SECONDS);
        return ret[0];
    }

    /**
     * 连接chrome并打开网页
     *
     * @param remoteHost
     * @param baseParm
     * @return
     */
    private WebDriver openWebPage(String remoteHost, BaseTaskParm baseParm) throws Exception {
        String remoteUrl = "http://" + remoteHost + "/wd/hub";
        log.info("try :  " + remoteUrl);


        ChromeOptions options = new ChromeOptions();
        //禁用通知
        options.addArguments("--disable-notifications");
        options.addArguments("--app=" + baseParm.getUrl());

        //全屏分辨率
        options.addArguments("--window-size=" + baseParm.getScreenWidth() + "," + baseParm.getScreenHeight());
        options.addArguments("--window-position=0,0");
        //最大化启动
        options.addArguments("--start-maximized");

        // 隐藏滚动条, 应对一些特殊页面
        options.addArguments("--hide-scrollbars");
        //# 隐身模式（无痕模式）
        options.addArguments("--incognito");
        //允许播放声音
        options.addArguments("--autoplay-policy=no-user-gesture-required");
        //禁用完整渲染
        options.addArguments("--disable-features=RendererCodeIntegrity");
        // options.addArguments("--incognito");
        //禁用GPU
        options.addArguments("--no-sandbox");
//        options.addArguments("--disable-gpu");

        //禁用了缓存渲染
        options.addArguments("--disable-dev-shm-usage");


        // 设置允许弹框
        options.addArguments("disable-infobars", "disable-web-security");


        //进行访问
        WebDriver driver = new RemoteWebDriver(new URL(remoteUrl), options);
//        log.info(driver.findElement(By.tagName("body")).getText());
        return driver;
    }


}
