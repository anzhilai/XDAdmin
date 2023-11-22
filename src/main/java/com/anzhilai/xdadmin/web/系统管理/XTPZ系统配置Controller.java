package com.anzhilai.xdadmin.web.系统管理;

import com.anzhilai.core.base.BaseModelController;
import com.anzhilai.core.base.XController;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

//@domain XTPZ系统配置Controller
@Controller
@XController(name = "系统配置接口",isLogin = XController.LoginState.No)
@Transactional(rollbackFor = {Exception.class})
@RequestMapping("/xtpz")
public class XTPZ系统配置Controller  extends BaseModelController<XTPZ系统配置> {
}
