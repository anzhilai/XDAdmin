import React from "react";
import PropTypes from 'prop-types'
import {XBasePage, XCard, XTableGrid} from "xdcoreweb";

//@menu 日志查看
export default class 系统日志 extends XBasePage {
  static defaultProps = {
    ...super.defaultProps,
  };

  static propTypes = {
    ...super.propTypes,
  };

  constructor(props) {
    super(props);
    this.state.visibleColumns = [
      "服务IP",
      "端口",
      "时间",
      {field: "信息", keyword: true,},
    ];
  }

  render() {
    return <XCard paddingTRBL={"10px"}>
      <XTableGrid ref={(e) => this.table = e} dataSourceUrl="xtrz/querylist" enableEdit={false}
                  visibleColumns={this.state.visibleColumns}/>
    </XCard>
  }
}
