package com.anzhilai.admin.web.基础数据;

import com.anzhilai.core.base.BaseModelController;
import com.anzhilai.core.base.BaseQuery;
import com.anzhilai.core.base.XController;
import com.anzhilai.core.database.DataTable;
import com.anzhilai.core.toolkit.TypeConvert;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

//@domain ZZBM组织部门Controller
@Controller
@XController(name = ZZBM组织部门.F_TableName)
@Transactional(rollbackFor = {Exception.class})
@RequestMapping("/zzbm")
public class ZZBM组织部门Controller<T extends ZZBM组织部门> extends BaseModelController<ZZBM组织部门> {

    @XController(name = "组织和用户列表")
    @RequestMapping(value = "/querylistanduser", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String 组织和用户列表(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        ZZBM组织部门 model =  TypeConvert.CreateNewInstance(GetClass());
        BaseQuery query = model.CreateQueryModel().InitFromRequest(request);
        DataTable dt = model.GetList(query);
        for(Map m :dt.Data){
            m.put(ZZBM组织部门.F_IsTreeLeaf, false);
        }
        if (query.Parentids != null) {
            RYXX人员信息 ry = new RYXX人员信息();
            RYXX人员信息.QueryModel queryModel = (RYXX人员信息.QueryModel) ry.CreateQueryModel();
            for (String 组织部门id : query.Parentids) {
                queryModel.组织部门id = 组织部门id;
                queryModel.hasDataRight = query.hasDataRight;
                DataTable dt2 = ry.GetList(queryModel);
                for (Map<String, Object> obj : dt2.Data) {
                    obj.put(ZZBM组织部门.F_Parentid, 组织部门id);
                    obj.put("类型", "人员信息");
                    obj.put(ZZBM组织部门.F_IsTreeLeaf, true);
                    dt.Data.add(obj);
                }
            }
        }
        return dt.ToJson();
    }
}
