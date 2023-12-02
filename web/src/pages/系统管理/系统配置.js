import React from "react";
import PropTypes from 'prop-types'
import {XUpload, XBasePage, XButton, XCard, XFlex, XDivider, XForm, XGrid, XInput, XModal, XPopConfirm, XRadioGroup, XTableGrid, XMessage} from "xdcoreweb";

//@menu 系统配置
export default class 系统配置 extends XBasePage {

  static defaultProps = {
    ...super.defaultProps,
  };

  static propTypes = {
    ...super.propTypes,
  };

  constructor(props) {
    super(props);
    this.state.visibleColumns = [
      {field: "配置类型", keyword: true,},
      {field: "配置项", keyword: true,},
      {
        field: "配置值", render: (text, record) => {
          if (record.配置类型 === "模板配置") {
            return <XUpload readOnly={true} value={text} downloadUrl={"download_file"}/>
          }
          return <div>{text}</div>
        }
      },
      {
        title: '操作', field: '操作', width: 200, visible: this.CheckOperation("编辑"),
        render: (text, record) => {
          return <XFlex>
            <XButton isA={true} onClick={() => this.showSaveModal(record)} text={"修改"}/>
            <XDivider/>
            <XPopConfirm width={"auto"} title="是否删除以下所有记录?" onOK={() => {
              this.DeleteTableData('xtpz/delete', record, this.table);
            }}>
              <XButton isA={true} text={"删除"}/>
            </XPopConfirm>
          </XFlex>
        },
      }
    ];
  }

  showSaveModal(data) {
    if (!data) {
      data = {};
    }
    const labelWidth = "100px";
    const Ele = <XForm infoUrl={"xtpz/queryinfo"} useServerInfo={false} infoData={data} inited={(e) => this.form = e}>
      <XGrid columnGap={"10px"} rowGap={"10px"} rowsTemplate={["auto"]}>
        <XInput field={"id"} visible={false} parent={() => this.form}/>
        <XRadioGroup labelWidth={labelWidth} value={"系统配置"}
                     isRequired={true} label={"配置类型"} field={"配置类型"}
                     items={["系统配置", "数据字典"]}
                     parent={() => this.form}/>
        <XInput labelWidth={labelWidth} field={"配置项"} isRequired={true} parent={() => this.form}/>
        <XInput labelWidth={labelWidth} field={"配置值"} parent={() => this.form}/>
        {/*<XUpload labelWidth={lableWidth} visible={false} field={"配置值"}*/}
        {/*         maxFileNum={1} maxFileSize={20} uploadUrl={"xtpz/upload"} downloadUrl={"download_file"}*/}
        {/*         fileType={XUpload.FileType.excel}*/}
        {/*         parent={() => this.form}/>*/}
      </XGrid>
    </XForm>;
    XModal.ModalShow("配置信息", async () => {
      let p = this.form.GetEditorValues()
      if (p && !p.id && p.配置类型 && p.配置项) {
        p.id = p.配置类型 + p.配置项;
        let ret = await this.RequestServer("xtpz/save", {...p});
        if (ret.Success) {
          this.table.Refresh();
          return true;
        } else {
          return false;
        }
      }
      return this.SaveFormData(this.form, "xtpz/save", this.table, {});
    }, Ele, '950px',);
  }


  render() {
    return <XCard paddingTRBL={"10px"}>
      <XTableGrid inited={(e) => this.table = e} dataSourceUrl="xtpz/querylist" extraButtons={(
        <XFlex visible={this.CheckOperation("编辑")}>
          <XButton text={"新增"} onClick={() => this.showSaveModal()}/>
        </XFlex>)} visibleColumns={this.state.visibleColumns}/>
    </XCard>
  }
}
