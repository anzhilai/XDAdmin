package com.anzhilai.xdadmin.web.系统管理;

import com.anzhilai.core.base.*;
import com.anzhilai.core.database.DataTable;
import com.anzhilai.core.database.SqlInfo;

//@domain XTPZ系统配置
@XTable
public class XTPZ系统配置 extends BaseModel {
    public final static String F_TableName = "XTPZ系统配置";

    @XColumn
    public String 配置类型;
    public final static String F_配置类型 = "配置类型";

    @XColumn
    public String 配置项;
    public final static String F_配置项 = "配置项";

    @XColumn(length = 3000)
    public String 配置值;
    public final static String F_配置值 = "配置值";

    @XColumn(length = 3000)
    public String 备注;
    public final static String F_备注 = "备注";

    public enum  E_是否{
        是,否
    }


    @Override
    public void Save() throws Exception {
        super.Save();
    }

    public class QueryModel extends BaseQuery {
        public QueryModel(BaseModel bm) {
            super(bm);
        }

        @XQuery
        public String 配置项;

    }

    @Override
    public BaseQuery CreateQueryModel() {
        return new QueryModel(this);
    }

    @Override
    public DataTable GetList(BaseQuery bq) throws Exception {
        SqlInfo su = new SqlInfo().CreateSelect();
        su.AppendColumn(XTPZ系统配置.F_TableName, F_id);
        su.AppendColumn(XTPZ系统配置.F_TableName, F_配置项);
        su.AppendColumn(XTPZ系统配置.F_TableName, F_配置值);
        su.AppendColumn(XTPZ系统配置.F_TableName, F_配置类型);
        su.AppendColumn(XTPZ系统配置.F_TableName, F_备注);
        su.From(XTPZ系统配置.F_TableName);
        return bq.GetList(su);
    }

    public static String Get系统配置(String pzx) throws Exception {
        XTPZ系统配置 xtpz = XTPZ系统配置.GetObjectById(XTPZ系统配置.class, "内部配置" + pzx);
        if (xtpz == null) {
            return "";
        }
        return xtpz.配置值;
    }

    public static void Save系统配置(String pzx, String value) throws Exception {
        XTPZ系统配置 xtpz = XTPZ系统配置.GetObjectById(XTPZ系统配置.class, "内部配置" + pzx);
        if (xtpz == null) {
            xtpz = new XTPZ系统配置();
            xtpz.id = "内部配置" + pzx;
            xtpz.配置类型="系统配置";
            xtpz.配置项 = pzx;
        }
        xtpz.配置值 = value;
        xtpz.Save();
    }

    public static String Get系统配置(String pzlx, String pzx, String value) throws Exception {
        XTPZ系统配置 xtpz = XTPZ系统配置.GetObjectById(XTPZ系统配置.class, "内部配置" + pzx);
        if (xtpz == null) {
            xtpz = new XTPZ系统配置();
            xtpz.id = "内部配置" + pzx;
            xtpz.配置项 = pzx;
            xtpz.配置类型 = pzlx;
            xtpz.配置值 = value;
            xtpz.Save();
        }
        return xtpz.配置值;
    }
}
