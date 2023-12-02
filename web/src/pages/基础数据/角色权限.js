import React from "react";
import {XModal, XBasePage, XForm, XGrid, XCard, XTableGrid, XButton, XCheckGroup, XMessage, XTools, XInput, XRadioGroup, XSelectList, XFlex, XInputTextarea} from "xdcoreweb";

//@menu 角色权限
export default class 角色权限 extends XBasePage {

  static propTypes = {
    ...super.propTypes,
  };
  static defaultProps = {
    ...super.defaultProps,
  };

  constructor(props) {
    super(props)
    this.state.initApp = false;
    this.CheckGroup = {};
    this.state.tableColumns = [
      {field: "角色名称", keyword: true,},
      "数据权限",
    ];
    this.state.cdColumns = [
      {title: '菜单名称', field: '菜单名称',},
      {
        title: <XFlex justifyContent={'flex-start'}>
          <XCheckGroup ref={e => this.操作权限 = e} items={[""]} onValueChangeSleep={0} onValueChange={(values) => {
            this.setChecked(this.menutable?.GetData(), values.length > 0);
          }}/>
          <div style={{marginLeft: -10}}>操作权限</div>
        </XFlex>,
        field: '操作权限', align: "left",
        render: (text, record) => {
          return (<div style={{display: 'flex', paddingLeft: 5}}>
            <XCheckGroup ref={(e) => this.CheckGroup[record.id] = e} showLabel={false} value={record.operationsSelected}
                         items={record.operationsList}
                         onValueChange={(v) => this.handleCheckChanged(record, v)}/>
          </div>);
        },
      }
    ];
  }

  setChecked(list, checked) {
    if (list) {
      list.forEach(item => {
        let value = [];
        if (checked && item.operationsList) {
          value = [...item.operationsList];
        }
        this.CheckGroup[item.id] && this.CheckGroup[item.id].SetValue(value);
        this.setChecked(item.children, checked);
      })
    }
  }

  SetMenuData(selectmenus) {
    let keys = this.menutable.GetExpandedRowKeys();
    let menusData = this.AllMenus;
    this.state.selectedMenus = [];
    this.resetMenuData(menusData, selectmenus);
    this.menutable.SetData(menusData);
    this.menutable.SetExpandedRowKeys(keys);
    this.menutable.SetCheckStateRowKeys(this.state.selectedMenus);
  }

  resetMenuData(allmenus, selectmenus, parent) {
    for (const i in allmenus) {
      let cd = allmenus[i];
      if (!cd.key) {
        continue;
      }
      // cd.id = cd.path;
      cd.id = cd.key.replace(/\//g, '_');
      cd.菜单名称 = cd.name;
      cd.操作权限 = cd.operations;
      cd.parent = parent;

      let selectmenu = undefined;
      if (selectmenus) {
        if (selectmenus == "all") {
          this.state.selectedMenus.push(cd.id);
        } else {
          for (const i in selectmenus) {
            let menu = selectmenus[i];
            if (cd.id == menu.id) {
              this.state.selectedMenus.push(menu.id);
              selectmenu = menu;
            }
          }
        }
      }
      if (cd.operations) {
        cd.operationsList = [];
        cd.operationsSelected = [];
        for (const key in cd.operations) {
          cd.operationsList.push(key);
          cd.operations[key] = false;
          if (selectmenus == "all") {
            cd.operations[key] = true;
            cd.operationsSelected.push(key);
          }
        }
        if (selectmenu && selectmenu.operations) {
          for (const key in selectmenu.operations) {
            if (selectmenu.operations[key]) {
              if (cd.operations[key] !== undefined) {
                cd.operationsSelected.push(key);
                cd.operations[key] = true;
              }
            } else {
              if (cd.operations[key] !== undefined) {
                cd.operations[key] = false;
              }
            }
          }
        }
        this.CheckGroup[cd.id]?.SetValue(cd.operationsSelected);
      }
      cd._children = cd.children;
      if (cd.children) {
        this.resetMenuData(cd.children, selectmenus, cd);
      }
    }
  }

  handleCheckChanged(record, value, e) {
    for (const key in record.operations) {
      record.operations[key] = false;
    }
    record.operationsSelected = value;
    this.menutable.SetValue(record.id, "operations", record.operations)
    this.menutable.SetValue(record.id, "operationsSelected", record.operationsSelected)
  }

  handleSelectChanged(row) {
    if (!row) {
      return;
    }
    this.操作权限?.SetValue([], false);
    this.select角色信息 = row;
    if (row.id === "超级管理员") {
      this.SetMenuData("all");
    } else {
      if (row.功能列表) {
        this.SetMenuData(JSON.parse(row.功能列表));
      } else {
        this.SetMenuData();
      }
    }
  }


  format功能列表(lb, keyrows) {
    keyrows.map((item, index) => {
      let 存在Index = -1;
      for (let i = 0; i < lb.length; i++) {
        if (lb[i].id === item.id) {
          存在Index = i;
          break;
        }
      }
      for (let key in item.operations) {
        item.operations[key] = item.operationsSelected.indexOf(key) !== -1;
      }
      let _item = {
        id: item.id,
        key: item.key,
        operations: item.operations,
        fields: item.fields,
      };
      if (存在Index >= 0) {
        lb[存在Index] = _item;
      } else {
        lb.push(_item);
      }
      if (item.children && item.children.length > 0) {
        this.format功能列表(lb, item.children);
      }
    });
  }

  async saveMenus() {
    if (!this.select角色信息) {
      XMessage.ShowInfo("请选择角色!");
      return;
    }
    let keyrows = this.menutable.GetCheckedRows();
    let lb = [];
    this.format功能列表(lb, keyrows)
    let 功能列表 = JSON.stringify(lb);
    const retData = await this.RequestServer("jsxx/save", {
      id: this.select角色信息.id,
      功能列表: 功能列表,
    });
    if (retData.Success) {
      this.table.SetValue(this.select角色信息.id, "功能列表", 功能列表);
      XMessage.ShowInfo("保存成功!");
    } else {
      XMessage.ShowWarn(retData.Message);
    }
  }

  expandRow(rows) {
    let newrows = [];
    for (const i in rows) {
      let row = rows[i];
      if (row?.children) {
        newrows = [...newrows, ...this.expandRow(row.children)];
      }
      newrows.push(row);
    }
    return newrows;
  }

  onCheckChange(id, checked) {
    if (id === undefined) {
      this.menutable.GetData().forEach(item => {
        this.onCheck(item.id, checked, false)
      });
    } else {
      this.onCheck(id, checked)
    }
  }

  onCheck(id, checked, children = true) {
    let record = this.menutable.GetRow(id);
    if (record) {
      if (checked) {
        let list = [];
        if (record.operations) {
          for (const key in record.operations) {
            record.operations[key] = true;
            list.push(key);
          }
        }
        record.operationsSelected = list;
        this.CheckGroup[record.id] && this.CheckGroup[record.id].SetValue(list);
        this.menutable.SetValue(record.id, "operationsSelected", record.operationsSelected)
      } else {
        record.operationsSelected = [];
        this.CheckGroup[record.id] && this.CheckGroup[record.id].SetValue([]);
        this.menutable.SetValue(record.id, "operationsSelected", record.operationsSelected)
      }
      if (children && record.children) {
        record.children.forEach(item => {
          this.onCheck(item.id, checked)
        });
      }
    }
  }


  click复制权限到(record) {
    XModal.ModalShow("复制到其他角色-" + record.角色名称, async () => {
      let rows = this.table复制权限.GetSelectedRows();
      if (rows.length == 0) {
        XMessage.ShowInfo("请选择被复制的角色");
        return false;
      }
      let success = true;
      for (let i = 0; i < rows.length; i++) {
        let row = rows[i];
        let _功能列表 = record.功能列表;
        if (this.权限添加方式.GetValue() === "合并权限") {
          _功能列表 = this.合并权限(record.功能列表, row.功能列表);
        }
        let ret = await this.RequestServer("jsxx/save", {id: row.id, 功能列表: _功能列表,});
        if (ret && ret.Success) {
        } else {
          success = false;
        }
      }
      if (success) {
        this.table.Refresh();
        XMessage.ShowInfo("复制权限成功");
      }
      return success;
    }, <XGrid rowsTemplate={["auto", "1fr"]}>
      <XRadioGroup ref={e => this.权限添加方式 = e} field={"权限添加方式"} items={["合并权限", "覆盖权限"]}
                   value={"合并权限"}/>
      <XTableGrid ref={e => this.table复制权限 = e} isMultiSelect={true} visibleColumns={this.state.tableColumns}
                  dataSourceUrl={"jsxx/querylist"} height={'500px'}/>
    </XGrid>, '60%',);
  }


  合并权限(功能列表1, 功能列表2) {
    try {
      if (功能列表1 && 功能列表2) {
        let list1 = JSON.parse(功能列表1);
        let list2 = JSON.parse(功能列表2);
        let map = {};
        list1.forEach(item => {
          map[item.id] = item;
        });
        list2.forEach(item => {
          if (map[item.id]) {//合并
            map[item.id].operations = {...map[item.id].operations, ...item.operations};
          } else {
            map[item.id] = item;
            list1.push(item);
          }
        });
        功能列表1 = JSON.stringify(list1);
      }
    } catch (e) {
      console.log(e);
    }
    return 功能列表1;
  }

  showSaveModal(data) {
    if (!data) {
      data = {
        数据权限: "所属部门",
      };
    }
    const lableWidth = "120px";
    const Ele = (<XForm infoUrl={"jsxx/queryinfo"} useServerInfo={true} infoData={data} inited={(e) => this.form = e}>
      <XGrid columnGap={"4px"} rowGap={"4px"} rowsTemplate={["auto"]}>
        <XInput field={"id"} visible={false} parent={() => this.form}/>
        <XInput field={"功能列表"} visible={false} parent={() => this.form}/>
        <XInput isRequired={true} labelWidth={lableWidth} field={"角色名称"} parent={() => this.form}/>
        <XInputTextarea labelWidth={lableWidth} field={"角色描述"} parent={() => this.form}/>
        <XRadioGroup labelWidth={lableWidth} isRequired={true} field={"数据权限"} onValueChange={(v) => {
          this.自定义部门?.SetVisible(v === "自定义");
        }} items={["全部数据", "所属部门", "个人数据", "自定义"]} parent={() => this.form}/>
        <XSelectList visible={data.数据权限 == "自定义"} ref={e => this.自定义部门 = e} labelWidth={lableWidth}
                     field={"自定义部门"}
                     label={"自定义部门"} dataSourceUrl={"zzbm/querylist"} isMultiSelect={true} searchField={"组织名称"}
                     displayField={"TreePath"} parent={() => this.form}/>
      </XGrid>
    </XForm>);
    XModal.ModalShow("角色信息", () => {
      return this.SaveFormData(this.form, "jsxx/save", this.table);
    }, Ele, '800px',);
  }

  async componentDidMount() {
    super.componentDidMount();
    await window.mainPage?.loadAllApp();
    console.log(this.GetAllMenuData());
    this.AllMenus = XTools.cloneDeep(this.GetCurrentUserMenus());
    this.setState({initApp: true})
  }

  render() {
    if (!this.state.initApp) {
      return "加载应用中...";
    }
    let op = {
      title: '操作', field: '操作', width: 120,
      render: (text, record) =>
        <XButton visible={this.CheckOperation("编辑")} isA={true} text={"复制到其他角色"}
                 onClick={() => this.click复制权限到(record)}/>,
    };
    return <XCard paddingTRBL={"10px"}>
      <XGrid rowsTemplate={["1fr"]} columnsTemplate={["1fr", "1fr"]} columnGap="10px">
        <XTableGrid ref={(e) => this.table = e} onSelectChange={row => this.handleSelectChanged(row)}
                    enableEdit={false} visibleColumns={this.state.tableColumns} dataSourceUrl={"jsxx/querylist"}
                    rowOperate={op} onServerResult={result => {
          if (result.Success) {
            result.Value.rows.forEach(item => {
              if (!item.功能列表) {
                item.功能列表 = "";
              }
            })
          }
          return result;
        }}
                    extraButtons={<XFlex justifyItems={'left'}>
                      <XButton text={"新增"} onClick={(item) => this.showSaveModal()}/>
                      <XButton text={"修改"}
                               onClick={() => this.table?.GetSelectRow() && this.showSaveModal(this.table?.GetSelectRow())}/>
                      <XButton text={"删除"} onClick={() => this.DeleteTableSelect('jsxx/delete', this.table)}/>
                    </XFlex>}/>
        <XTableGrid ref={(e) => this.menutable = e} isTree={true} isMultiSelect={true} showButtons={false}
                    isCheck={true} enableEdit={false} useDragSelect={false}
                    visibleColumns={this.state.cdColumns} showSearch={false}
                    onCheckChange={(id, checked) => this.onCheckChange(id, checked)}
                    extraButtons={<XFlex justifyItems={'left'}>
                      <XButton visible={this.CheckOperation("编辑")} text={"保存菜单"}
                               onClick={() => this.saveMenus()}/>
                    </XFlex>}/>

      </XGrid>
    </XCard>
  }
}
