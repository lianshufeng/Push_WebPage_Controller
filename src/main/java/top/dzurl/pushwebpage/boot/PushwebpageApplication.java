package top.dzurl.pushwebpage.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("top.dzurl.pushwebpage.core")
@SpringBootApplication
public class PushwebpageApplication {

    public static void main(String[] args) {
        SpringApplication.run(PushwebpageApplication.class, args);
    }

}
