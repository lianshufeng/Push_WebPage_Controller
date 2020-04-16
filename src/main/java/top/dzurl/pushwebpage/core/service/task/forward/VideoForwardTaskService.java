package top.dzurl.pushwebpage.core.service.task.forward;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.dzurl.pushwebpage.core.helper.DockerHelper;
import top.dzurl.pushwebpage.core.type.StreamTaskType;

@Log
@Service
public class VideoForwardTaskService extends SuperForwardTaskService {


    @Override
    public StreamTaskType taskType() {
        return StreamTaskType.VideoForward;
    }

    @Override
    public String getFfmpeg_cmd_template() {
        return "ffmpeg -re -i ${url}  -vcodec libx264 -preset ultrafast  -r ${frameRate} -c:a aac -b:v ${vedioBitrate}k -b:a ${audioBitrate}k ${outputSize} -f flv ${pushUrl} ";
    }


}
