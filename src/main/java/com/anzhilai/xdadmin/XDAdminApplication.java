package com.anzhilai.xdadmin;

import com.anzhilai.core.database.SqlInfo;
import com.anzhilai.core.framework.BaseApplication;
import com.anzhilai.core.framework.CacheValue;
import com.anzhilai.core.framework.CommonConfig;
import com.anzhilai.core.framework.SystemSpringConfig;
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

    @Override
    public void ResetCache(SqlInfo su) throws Exception {
        CacheValue.WatchTableChange(su.CurrentMainTable);
    }

    //上传文件路径
    @Override
    public String GetUploadFilePath() {
        CommonConfig config = SystemSpringConfig.getBean(CommonConfig.class);
        return config.getUploadFilePath();
    }

    //临时文件路径
    @Override
    public String GetTempFilePath() {
        CommonConfig config = SystemSpringConfig.getBean(CommonConfig.class);
        return config.getTempFilePath();
    }

    public static void main(String[] args) {
        SpringApplication.run(XDAdminApplication.class, args);
    }
}
