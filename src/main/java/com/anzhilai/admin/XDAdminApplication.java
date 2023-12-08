package com.anzhilai.admin;

import com.anzhilai.core.database.SqlInfo;
import com.anzhilai.core.framework.BaseApplication;
import com.anzhilai.core.framework.CommonConfig;
import com.anzhilai.core.framework.SpringConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling//开启定时任务功能
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.anzhilai"})
public class XDAdminApplication extends BaseApplication implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Override
    public String[] GetScanPackages() {
        return new String[]{"com.anzhilai"};
    }

    //上传文件路径
    @Override
    public String GetUploadFilePath() {
        CommonConfig config = SpringConfig.getBean(CommonConfig.class);
        return config.getUploadFilePath();
    }

    //临时文件路径
    @Override
    public String GetTempFilePath() {
        CommonConfig config = SpringConfig.getBean(CommonConfig.class);
        return config.getTempFilePath();
    }

    public static void main(String[] args) {
        SpringApplication.run(XDAdminApplication.class, args);
    }
}
