package com.anzhilai.admin;

import com.anzhilai.core.framework.BaseApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.ServletContext;

@SpringBootApplication
@EnableScheduling//开启定时任务功能
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.anzhilai"})
public class XDAdminApplication extends BaseApplication {

    @Override
    public String[] GetScanPackages() {
        return new String[]{"com.anzhilai"};
    }

    @Override
    public void onStartup(ServletContext servletContext) {
        System.out.println("作为类库时不启动");
    }

    public static void main(String[] args) {
        SpringApplication.run(XDAdminApplication.class, args);
    }
}
