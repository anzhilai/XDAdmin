import React from 'react';
import PropTypes from 'prop-types';
import {XModal, XGrid, XButton, XInput, XCard, XTableGrid, XForm, XBasePage, XSelectTree, XSelectList, XFlex, XBaseStyle} from "xdcorelib";

//@menu 组织部门
export default class 组织架构 extends XBasePage {
  static operations = {
    编辑: true,
  };

  static defaultProps = {
    ...super.defaultProps,
  };

  static propTypes = {
    ...super.propTypes,
  };

  constructor(props) {
    super(props);
    this.state.selectedOption = null;
    this.state.visibleColumns1 = [
      {
        field: "机构名称",
        keyword: true,
      },
      {
        field: "机构类型",
        editorRender: (text, record) => (
          <XSelectList items={["公司", "部门"]} ref={(e) => record.editor = e}/>),
      },
      {
        field: "姓名",
        foreignKey: "负责人id",
        title: "负责人",
        editorRender: (text, record) => (
          <XSelectList dataSourceUrl={"ryxx/querylist"} displayField={"姓名"}
                       ref={(e) => record.editor = e}/>),
      },
      {
        field: "审核人",
        foreignKey: "审核人ids",
        foreignField: "姓名",
        title: "审核人",
        editorRender: (text, record) => (
          <XSelectList dataSourceUrl={"ryxx/querylist"} displayField={"姓名"} isMultiSelect={true}
                       ref={(e) => record.editor = e}/>),
      }
    ];
  }


  showSaveModal组织机构(data, parent) {
    if (!data) {
      data = {机构类型: "部门"};
      if (parent) {
        data.Parentid = parent.id;
      }
    }
    const labelWidth = "150px";
    const Ele = (<XForm infoUrl={"zzbm/queryinfo"} useServerInfo={true} infoData={data} inited={(e) => this.form = e}>
      <XGrid columnGap={"10px"} rowGap={"10px"} rowsTemplate={["auto"]}>
        <XInput field={"id"} visible={false} parent={() => this.form}/>
        <XSelectTree labelWidth={labelWidth} field={"Parentid"} label={"上级单位"}
                     dataSourceUrl={"zzbm/querylist"}
                     treePathInfoUrl={"zzbm/treeinfo"} displayField={"机构名称"} parent={() => this.form}/>
        <XInput isRequired={true} labelWidth={labelWidth} field={"机构名称"} parent={() => this.form}/>
        <XSelectList field={"机构类型"} labelWidth={labelWidth} items={["公司", "部门"]} parent={() => this.form}
                     onValueChange={v => {
                       let visible = v === "公司";
                       this.input联系电话?.SetVisible(visible);
                       this.input详细地址?.SetVisible(visible);
                       this.input法定代表人?.SetVisible(visible);
                       this.input开户行?.SetVisible(visible);
                       this.input银行账号?.SetVisible(visible);
                       this.input公司税号?.SetVisible(visible);
                     }}/>
        <XSelectList field={"负责人id"} label={"负责人"} displayField={"姓名"}
                     labelWidth={labelWidth} dataSourceUrl={"ryxx/querylist"} parent={() => this.form}/>
        <XSelectList field={"审核人ids"} label={"审核人"} displayField={"姓名"} isMultiSelect={true}
                     labelWidth={labelWidth} dataSourceUrl={"ryxx/querylist"} parent={() => this.form}/>
        <XInput ref={e => this.input联系电话 = e} labelWidth={labelWidth} field={"联系电话"} visible={false}
                parent={() => this.form}/>
        <XInput ref={e => this.input详细地址 = e} labelWidth={labelWidth} field={"详细地址"} visible={false}
                parent={() => this.form}/>
        <XInput ref={e => this.input法定代表人 = e} labelWidth={labelWidth} field={"法定代表人"} visible={false}
                parent={() => this.form}/>
        <XInput ref={e => this.input开户行 = e} labelWidth={labelWidth} field={"开户行"} parent={() => this.form}
                visible={false}/>
        <XInput ref={e => this.input银行账号 = e} labelWidth={labelWidth} field={"银行账号"} parent={() => this.form}
                visible={false}/>
        <XInput ref={e => this.input公司税号 = e} labelWidth={labelWidth} field={"公司税号"} parent={() => this.form}
                visible={false}/>
      </XGrid>
    </XForm>);
    XModal.ModalShow("单位信息", () => {
      return this.SaveFormData(this.form, "zzbm/save", this.table组织机构);
    }, Ele, '80vh',);
  }

  render() {
    return <XCard paddingTRBL={"10px"}>
      <XTableGrid inited={(e) => this.table组织机构 = e} dataSourceUrl="zzbm/querylist" showSearch={false}
                  isTree={true} onSelectChange={(row) => this.select组织机构 = row} draggable={true}
                  visibleColumns={this.state.visibleColumns1}
                  extraButtons={(<XFlex contentHAlign={XBaseStyle.Align.start}>
                    <XButton text={"新增"} dropdownItems={["新增子级"]} onClick={(item) => {
                      if (item === "新增子级") {
                        this.showSaveModal组织机构(undefined, this.table组织机构.GetSelectRow());
                      } else {
                        this.showSaveModal组织机构();
                      }
                    }}/>
                    <XButton text={"修改"}
                             onClick={() => this.table组织机构.GetSelectRow() && this.showSaveModal组织机构(this.table组织机构.GetSelectRow())}/>
                    <XButton text={"删除"} onClick={() => this.DeleteTableSelect('zzbm/delete', this.table组织机构)}/>
                  </XFlex>)}/>
    </XCard>;
  }
}

