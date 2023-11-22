package com.anzhilai.xdadmin.web.系统管理;

import com.anzhilai.core.base.*;
import com.anzhilai.core.database.DataTable;
import com.anzhilai.core.database.SqlInfo;
import com.anzhilai.core.framework.GlobalValues;

import java.util.Date;

//@domain XTRZ系统日志
@XTable
public class XTRZ系统日志 extends BaseModel {
    public final static String F_TableName = "XTRZ系统日志";

    @XColumn
    public String 服务IP;
    public final static String F_服务IP = "服务IP";

    @XColumn
    public int 端口;
    public final static String F_端口 = "端口";

    @XColumn
    public String 用户名;
    public final static String F_用户名 = "用户名";

    @XColumn
    public Date 时间;
    public final static String F_时间 = "时间";

    public enum RZLX日志类型 {
        通知, 警告, 错误
    }
    @XColumn
    public String 日志类型 = RZLX日志类型.通知.toString();
    public final static String F_日志类型 = "日志类型";

    @XColumn( columnDefinition = "text")
    public String 信息;
    public final static String F_信息 = "信息";


    @Override
    public void Save() throws Exception {
        this.服务IP=(GlobalValues.CurrentIP);
        this.端口=(GlobalValues.CurrentPort);
        if( GlobalValues.GetSessionUser()!=null) {
            this.用户名 = GlobalValues.GetSessionUser().GetLoginName();
        }
        this.时间=(new Date());
        super.Save();
    }


    public class QueryModel extends BaseQuery {
        public QueryModel(BaseModel bm) {
            super(bm);
        }
        @XQuery
        public String 服务IP;
        @XQuery
        public String 端口;
        @XQuery(table = XTRZ系统日志.F_TableName, column = XTRZ系统日志.F_时间, type = XQuery.QueryType.greatEqual)
        public Date 开始时间;
        @XQuery(table = XTRZ系统日志.F_TableName, column = XTRZ系统日志.F_时间, type = XQuery.QueryType.lessEqual)
        public Date 结束时间;
        @XQuery
        public String 信息;
    }

    @Override
    public BaseQuery CreateQueryModel() {
        return new QueryModel(this);
    }

    @Override
    public DataTable GetList(BaseQuery bq) throws Exception {
        SqlInfo su = new SqlInfo().CreateSelect();
        su.AppendColumn(XTRZ系统日志.F_TableName, F_id);
        su.AppendColumn(XTRZ系统日志.F_TableName, F_服务IP);
        su.AppendColumn(XTRZ系统日志.F_TableName, F_端口);
        su.AppendColumn(XTRZ系统日志.F_TableName, F_时间);
        su.AppendColumn(XTRZ系统日志.F_TableName, F_信息);
        su.AppendColumn(XTRZ系统日志.F_TableName, F_用户名);
        su.From(XTRZ系统日志.F_TableName);
        return bq.GetList(su);
    }

    public static void Save系统日志(RZLX日志类型 lx, String message) {
        try {
            XTRZ系统日志 ycrzgl = new XTRZ系统日志();
            ycrzgl.日志类型=(lx.toString());
            ycrzgl.信息=(message);
            ycrzgl.Save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
