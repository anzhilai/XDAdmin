package com.anzhilai.admin.web.基础数据;

import com.anzhilai.core.base.BaseModelController;
import com.anzhilai.core.base.XController;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

//@domain JSXX角色信息Controller
@Controller
@XController(name = JSXX角色信息.F_TableName)
@Transactional(rollbackFor = {Exception.class})
@RequestMapping("/jsxx")
public class JSXX角色信息Controller extends BaseModelController<JSXX角色信息> {
}
