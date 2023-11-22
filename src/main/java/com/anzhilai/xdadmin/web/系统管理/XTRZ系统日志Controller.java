package com.anzhilai.xdadmin.web.系统管理;

import com.anzhilai.core.base.BaseModelController;
import com.anzhilai.core.base.XController;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

//@domain XTRZ系统日志Controller
@Controller
@XController(name = "日志")
@Transactional(rollbackFor = {Exception.class})
@RequestMapping("/xtrz")
public class XTRZ系统日志Controller extends BaseModelController<XTRZ系统日志> {
}
