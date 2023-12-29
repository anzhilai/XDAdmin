package com.anzhilai.admin;

import com.anzhilai.core.framework.BaseApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling//开启定时任务功能
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.anzhilai"})
public class XDAdminApplication extends BaseApplication {

    @Override
    public String[] GetScanPackages() {
        return new String[]{"com.anzhilai"};
    }


    public static void main(String[] args) {
        SpringApplication.run(XDAdminApplication.class, args);
    }
}
