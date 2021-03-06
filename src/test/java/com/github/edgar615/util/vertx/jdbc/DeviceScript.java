package com.github.edgar615.util.vertx.jdbc;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.github.edgar615.util.db.Persistent;

import java.util.List;
import java.util.Map;

/**
* This class is generated by Jdbc code generator.
*
* Table : device_script
* remarks: 消息类型: 1-down 2-up
状态：1-test 2-pre 3-release
*
* @author Jdbc Code Generator Date 2018-04-18
*/
public class DeviceScript implements Persistent<String> {

    private static final long serialVersionUID = 1L;
    
    /**
    * Column : device_script_id
    * remarks: device_script_id
    * default: 
    * isNullable: false
    * isAutoInc: false
    * isPrimary: true
    * type: 12
    * size: 32
    */
    private String deviceScriptId;
    
    /**
    * Column : product_type
    * remarks: 产品类型
    * default: 
    * isNullable: true
    * isAutoInc: false
    * isPrimary: false
    * type: 1
    * size: 7
    */
    private String productType;
    
    /**
    * Column : message_type
    * remarks: 消息类型
    * default: 
    * isNullable: true
    * isAutoInc: false
    * isPrimary: false
    * type: 1
    * size: 16
    */
    private String messageType;
    
    /**
    * Column : command
    * remarks: 消息命令
    * default: 
    * isNullable: true
    * isAutoInc: false
    * isPrimary: false
    * type: 12
    * size: 64
    */
    private String command;
    
    /**
    * Column : script_content
    * remarks: 脚本内容
    * default: 
    * isNullable: true
    * isAutoInc: false
    * isPrimary: false
    * type: -1
    * size: 65535
    */
    private String scriptContent;
    
    /**
    * Column : state
    * remarks: 状态
    * default: 
    * isNullable: true
    * isAutoInc: false
    * isPrimary: false
    * type: 4
    * size: 10
    */
    private Integer state;
    
    /**
    * Column : add_on
    * remarks: 添加时间
    * default: 
    * isNullable: true
    * isAutoInc: false
    * isPrimary: false
    * type: 4
    * size: 10
    */
    private Integer addOn;
    
    /**
    * Column : last_modify_on
    * remarks: 上次修改时间
    * default: 
    * isNullable: true
    * isAutoInc: false
    * isPrimary: false
    * type: 4
    * size: 10
    */
    private Integer lastModifyOn;
    
    public String getDeviceScriptId() {
        return deviceScriptId;
    }

    public void setDeviceScriptId(String deviceScriptId) {
        this.deviceScriptId = deviceScriptId;
    }
    
    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }
    
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
    
    public String getScriptContent() {
        return scriptContent;
    }

    public void setScriptContent(String scriptContent) {
        this.scriptContent = scriptContent;
    }
    
    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
    
    public Integer getAddOn() {
        return addOn;
    }

    public void setAddOn(Integer addOn) {
        this.addOn = addOn;
    }
    
    public Integer getLastModifyOn() {
        return lastModifyOn;
    }

    public void setLastModifyOn(Integer lastModifyOn) {
        this.lastModifyOn = lastModifyOn;
    }
    
    @Override
    public String toString() {
        return MoreObjects.toStringHelper("DeviceScript")
            .add("deviceScriptId",  deviceScriptId)
            .add("productType",  productType)
            .add("messageType",  messageType)
            .add("command",  command)
            .add("scriptContent",  scriptContent)
            .add("state",  state)
            .add("addOn",  addOn)
            .add("lastModifyOn",  lastModifyOn)
           .toString();
    }

    @Override
    public List<String> fields() {
      return Lists.newArrayList("deviceScriptId",
						"productType",
						"messageType",
						"command",
						"scriptContent",
						"state",
						"addOn",
						"lastModifyOn");
    }
    
    @Override
    public String primaryField() {
        return "deviceScriptId";
    }

    @Override
    public String id () {
    return deviceScriptId;
    }

    @Override
    public void setId(String id) {
        this.deviceScriptId = id;
    }

    @Override
    public void setGeneratedKey(Number key) {
    
        throw new UnsupportedOperationException();
    
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("deviceScriptId",  deviceScriptId);
        map.put("productType",  productType);
        map.put("messageType",  messageType);
        map.put("command",  command);
        map.put("scriptContent",  scriptContent);
        map.put("state",  state);
        map.put("addOn",  addOn);
        map.put("lastModifyOn",  lastModifyOn);
        return map;
    }

    @Override
    public void fromMap(Map<String, Object> map) {

    }

   /* START Do not remove/edit this line. CodeGenerator will preserve any code between start and end tags.*/
	/* END Do not remove/edit this line. CodeGenerator will preserve any code between start and end tags.*/
}