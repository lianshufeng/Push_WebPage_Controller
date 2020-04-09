package top.dzurl.pushwebpage.core.service.task.forward;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import top.dzurl.pushwebpage.core.type.StreamTaskType;

@Log
@Service
public class StreamForwardTaskService extends SuperForwardTaskService {

    @Override
    public StreamTaskType taskType() {
        return StreamTaskType.StreamForward;
    }

    @Override
    public String getFfmpeg_cmd_template() {
        return "ffmpeg -i ${url}  -vcodec libx264 -preset ultrafast  -r ${frameRate} -b:v ${vedioBitrate}k -b:a ${audioBitrate}k ${outputSize} -f flv ${pushUrl} ";
    }

}
