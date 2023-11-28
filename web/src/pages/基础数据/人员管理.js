import React from "react";
import PropTypes from "prop-types";
import {XModal, XRadioGroup, XGrid, XButton, XInput, XCard, XTableGrid, XSelectList, XForm, XBasePage, XSelectTree, XFlex, XMessage, XBaseStyle, XImport, XDateTime, XInputPwd} from "xdcorelib";

//@menu 人员管理
export default class 人员管理 extends XBasePage {

  static operations = {
    编辑: true
  };

  static defaultProps = {
    ...super.defaultProps,
    showFilterView: true,
    onSelectPeopleChange: undefined,
    filterData: undefined,
    views: ["列表显示", "详情显示"],
    view: "详情显示"
  };

  static propTypes = {
    ...super.propTypes,
    onSelectPeopleChange: PropTypes.func,
    filterData: PropTypes.object
  };

  constructor(props) {
    super(props);
    this.state.visibleColumns1 = [{
      field: "组织名称", keyword: true
    }];
    this.state.visibleColumns2 = [{
      field: "姓名", keyword: true
    }, {
      field: "性别",
      type: "string",
      editorName: "XRadioGroup",
      editorOption: {items: ["男", "女"]},
      editorRender: (text, record) => (<XSelectList items={["男", "女"]} ref={(e) => record.editor = e}/>),
      filterName: "XCheckGroup",
      filterOption: {items: ["男", "女"]},
      filterRender: (column, filter) => (<XSelectList items={["男", "女"]} ref={(e) => filter.xeditor = e}
                                                      onValueChange={(v) => filter.triggerFilterChangeEvent()}/>)
    }, "出生日期", "入职时间", "登录账号", "手机号码", "个人邮箱",
      {
        field: "组织名称", foreignKey: "组织部门id",
        editorRender: (text, record) => (<XSelectTree ref={(e) => record.editor = e} dataSourceUrl={"zzbm/querylist"}
                                                      treePathInfoUrl={"zzbm/treeinfo"} displayField={"组织名称"}/>)
      }, {
        field: "角色名称", foreignKey: "角色信息id", title: "角色名称",
        editorRender: (text, record) => (
          <XSelectList dataSourceUrl={"jsxx/querylist"} displayField={"角色名称"} ref={(e) => record.editor = e}/>)
      }];
  }

  resetPassword() {
    let row = this.table.GetSelectRow();
    if (row) {
      XModal.ModalShow("重置密码", async () => {
        const retData = await this.RequestServer("ryxx/resetpassword", {id: row.id});
        if (retData.Success) {
          XMessage.ShowInfo("重置成功");
        } else {
          XMessage.ShowError(retData.Message);
        }
      }, <div>是否重置密码为：123456</div>, "300px");
    }
  }

  showSaveModal组织部门(data, parent) {
    if (!data) {
      data = {};
      if (parent) {
        data.Parentid = parent.id;
      }
    }
    const lableWidth = "100px";
    const Ele = (<XForm infoUrl={"zzbm/queryinfo"} useServerInfo={true} infoData={data} inited={(e) => this.form = e}>
      <XGrid columnGap={"10px"} rowGap={"10px"} rowsTemplate={["auto"]}>
        <XInput field={"id"} visible={false} parent={() => this.form}/>
        <XSelectTree labelWidth={lableWidth} field={"Parentid"} label={"上级组织"}
                     dataSourceUrl={"zzbm/querylist"}
                     treePathInfoUrl={"zzbm/treeinfo"} displayField={"组织名称"} parent={() => this.form}/>
        <XInput isRequired={true} labelWidth={lableWidth} field={"组织名称"} parent={() => this.form}/>
      </XGrid>
    </XForm>);
    XModal.ModalShow("组织信息", () => {
      return this.SaveFormData(this.form, "zzbm/save", this.table组织部门);
    }, Ele, "80vh");
  }

  showSaveModal(data) {
    if (!data) {
      data = {级别: "职员", 是否锁定: "否"};
      if (this.select组织部门) {
        data.组织部门id = this.select组织部门.id;
      }
    }
    const labelWidth = "150px";
    const Ele = <XForm infoUrl={"ryxx/queryinfo"} useServerInfo={true} infoData={data} inited={(e) => this.form = e}>
      <XGrid columnGap={"10px"} rowGap={"10px"} rowsTemplate={["auto"]}>
        <XInput field={"id"} labelWidth={labelWidth} visible={false} parent={() => this.form}/>
        <XSelectTree labelWidth={labelWidth} field={"组织部门id"} isRequired={true} label={"所属单位"}
                     dataSourceUrl={"zzbm/querylist"} filterData={this.props.filterData}
                     treePathInfoUrl={"zzbm/treeinfo"} displayField={"组织名称"} parent={() => this.form}/>
        <XInput field={"姓名"} labelWidth={labelWidth} isRequired={true} parent={() => this.form}/>
        <XRadioGroup field={"性别"} labelWidth={labelWidth} value={"男"} items={["男", "女"]}
                     parent={() => this.form}/>
        <XInput field={"登录账号"} labelWidth={labelWidth} isRequired={true} parent={() => this.form}/>
        <XInput field={"个人邮箱"} labelWidth={labelWidth} parent={() => this.form}/>
        <XInput field={"手机号码"} labelWidth={labelWidth} parent={() => this.form}/>
        <XDateTime field={"出生日期"} labelWidth={labelWidth} parent={() => this.form}/>
        <XRadioGroup isRequired={true} field={"是否锁定"} labelWidth={labelWidth} items={["是", "否"]}
                     parent={() => this.form}/>
        <XSelectList isRequired={true} field={"角色信息id"} label={"角色名称"}
                     displayField={"角色名称"} labelWidth={labelWidth} dataSourceUrl={"jsxx/querylist"}
                     parent={() => this.form}/>
      </XGrid>
    </XForm>;
    XModal.ModalShow("人员信息", () => {
      return this.SaveFormData(this.form, "ryxx/save", this.table, data);
    }, Ele, "800px");
  }

  showImportExcel() {
    XModal.ModalShow("导入Excel", () => {
      if (this.import.Import()) {
        XMessage.ShowInfo("导入成功");
      }
    }, <XImport ref={(e) => this.import = e} importUrl={"ryxx/import_excel"} uniqueField={"姓名"}/>, "60vw");
  }


  render() {
    return (<XCard paddingTRBL={"10px"}>
      <XGrid columnsTemplate={["300px", "1fr"]} columnGap={"10px"}>
        <XTableGrid dataSourceUrl="zzbm/querylist" inited={(e) => this.table组织部门 = e}
                    showButtons={false} showSearch={false} isTree={true} filterData={this.props.filterData}
                    onSelectChange={(row) => {
                      this.select组织部门 = row;
                      this.table.Refresh({组织部门TreePath: row.TreePath});
                    }} draggable={true}
                    visibleColumns={this.state.visibleColumns1}
                    extraButtons={(<XFlex contentHAlign={"start"}>
                      <XButton text={"新增"} dropdownItems={["新增子级"]} onClick={(item) => {
                        if (item === "新增子级") {
                          this.showSaveModal组织部门(undefined, this.table组织部门.GetSelectRow());
                        } else {
                          this.showSaveModal组织部门();
                        }
                      }}/>
                      <XButton text={"修改"}
                               onClick={() => this.table组织部门.GetSelectRow() && this.showSaveModal组织部门(this.table组织部门.GetSelectRow())}/>
                      <XButton text={"删除"}
                               onClick={() => this.DeleteTableSelect("zzbm/delete", this.table组织部门)}/>
                    </XFlex>)}/>
        <XTableGrid inited={(e) => this.table = e} dataSourceUrl="ryxx/querylist" mustHasFilter={true}
                    draggable={true} visibleColumns={this.state.visibleColumns2} filterData={this.props.filterData}
                    onSelectChange={(row, checked, talbe) => this.props.onSelectPeopleChange && this.props.onSelectPeopleChange(row)}
                    extraButtons={(<XFlex contentHAlign={XBaseStyle.Align.start}>
                      <XButton text={"新增"} onClick={() => this.showSaveModal()}/>
                      <XButton text={"修改"}
                               onClick={() => this.table.GetSelectRow() && this.showSaveModal(this.table.GetSelectRow())}/>
                      <XButton text={"删除"} onClick={() => this.DeleteTableSelect("ryxx/delete", this.table)}/>
                      <XButton text={"重置密码"} onClick={() => this.resetPassword()}/>
                      <XButton text={"导入Excel"} onClick={() => this.showImportExcel()}/>
                    </XFlex>)}/>
      </XGrid>
    </XCard>);
  }
}

