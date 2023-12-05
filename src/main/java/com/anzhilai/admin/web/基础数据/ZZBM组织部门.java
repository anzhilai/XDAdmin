package com.anzhilai.admin.web.基础数据;

import com.anzhilai.core.base.*;
import com.anzhilai.core.database.DataTable;
import com.anzhilai.core.database.SqlInfo;
import com.anzhilai.core.toolkit.StrUtil;

//@domain ZZBM组织部门
@XTable
public class ZZBM组织部门 extends BaseModelTree {
    public final static String F_TableName = "ZZBM组织部门";

    @XColumn
    public String 组织名称;
    public final static String F_组织名称 = "组织名称";

    @Override
    public String GetNameField() {
        return F_组织名称;
    }

    @XColumn(foreignTable = RYXX人员信息.F_TableName)
    public String 负责人id;
    public final static String F_负责人id = "负责人id";

    @XColumn(foreignTable = RYXX人员信息.F_TableName, text = true)
    public String 审核人ids;
    public final static String F_审核人ids = "审核人ids";

    public enum E_组织类型 {
        公司, 部门
    }

    @XColumn
    public String 组织类型;
    public final static String F_组织类型 = "组织类型";

    @XColumn
    public String 联系电话;
    public final static String F_联系电话 = "联系电话";

    @XColumn
    public String 传真;
    public final static String F_传真 = "传真";

    @XColumn
    public String 详细地址;
    public final static String F_详细地址 = "详细地址";

    @XColumn
    public String 法定代表人;
    public final static String F_法定代表人 = "法定代表人";

    @XColumn
    public String 开户行;
    public final static String F_开户行 = "开户行";

    @XColumn
    public String 银行账号;
    public final static String F_银行账号 = "银行账号";

    @XColumn
    public String 公司税号;
    public final static String F_公司税号 = "公司税号";

    @Override
    public String SaveValidate() throws Exception {
        String s = "";
        if (StrUtil.isEmpty(this.组织名称)) {
            s += "组织名称不能为空";
        }
        return s;
    }

    @Override
    public String DeleteValidate() throws Exception {
        String str = "";
        if (this.HasOtherTableValue(RYXX人员信息.F_TableName, RYXX人员信息.F_组织部门id)) {
            str = "该组织还存在人员,无法删除";
        }
        return str;
    }

    public class QueryModel extends BaseQuery {
        public QueryModel(BaseModel bm) {
            super(bm);
        }


        @XQuery(type = XQuery.QueryType.equal)
        public String 负责人id;
        @XQuery(type = XQuery.QueryType.equal)
        public String 组织类型;
        @XQuery(type = XQuery.QueryType.like)
        public String 负责人ids;
        @XQuery(column = ZZBM组织部门.F_id, type = XQuery.QueryType.noEqual)
        public String idNoEqual;
        @XQuery(column = ZZBM组织部门.F_组织名称, type = XQuery.QueryType.like)
        public String 组织名称;
        @XQuery(column = ZZBM组织部门.F_TreePath, type = XQuery.QueryType.leftlike)
        public String 组织部门TreePath;

    }

    @Override
    public BaseQuery CreateQueryModel() {
        return new QueryModel(this);
    }

    @Override
    public DataTable GetList(BaseQuery bq) throws Exception {
        SqlInfo su = new SqlInfo().CreateSelect();
        su.AppendColumn(ZZBM组织部门.F_TableName, F_id);
        su.AppendColumn(ZZBM组织部门.F_TableName, F_Parentid);
        su.AppendColumn(ZZBM组织部门.F_TableName, F_TreePath);
        su.AppendColumn(ZZBM组织部门.F_TableName, F_TreeLevel);
        su.AppendColumn(ZZBM组织部门.F_TableName, F_TreeName);
        su.AppendColumn(ZZBM组织部门.F_TableName, F_IsTreeLeaf);
        su.AppendColumn(ZZBM组织部门.F_TableName, F_组织名称);
        su.AppendColumn(ZZBM组织部门.F_TableName, F_负责人id);
        su.AppendColumn(ZZBM组织部门.F_TableName, F_审核人ids);
        su.AppendColumn(ZZBM组织部门.F_TableName, F_联系电话);
        su.AppendColumn(ZZBM组织部门.F_TableName, F_详细地址);
        su.AppendColumn(ZZBM组织部门.F_TableName, F_组织类型);
        su.AppendColumn(ZZBM组织部门.F_TableName, F_法定代表人);
        su.AppendColumn(ZZBM组织部门.F_TableName, F_开户行);
        su.AppendColumn(ZZBM组织部门.F_TableName, F_银行账号);
        su.AppendColumn(ZZBM组织部门.F_TableName, F_公司税号);
        su.AppendColumn(ZZBM组织部门.F_TableName, F_传真);
        su.AppendColumn(RYXX人员信息.F_TableName, RYXX人员信息.F_姓名);
        su.From(ZZBM组织部门.F_TableName);
        su.LeftJoin(RYXX人员信息.F_TableName, RYXX人员信息.F_id, ZZBM组织部门.F_负责人id);
        return bq.GetList(su);
    }

}
