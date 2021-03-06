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
* Table : user
* remarks: 类型：1：手机人员 2：后台用户 3：国外用户
状态：1：活动 2：锁定（锁定的用户不能访问系统）
                         -&amp;#
*
* @author Jdbc Code Generator Date 2018-04-19
*/
public class User implements Persistent<Integer> {

    private static final long serialVersionUID = 1L;
    
    /**
    * Column : user_id
    * remarks: 
    * default: 
    * isNullable: false
    * isAutoInc: true
    * isPrimary: true
    * type: 4
    * size: 10
    */
    private Integer userId;
    
    /**
    * Column : username
    * remarks: 
    * default: 
    * isNullable: true
    * isAutoInc: false
    * isPrimary: false
    * type: 12
    * size: 60
    */
    private String username;
    
    /**
    * Column : head_pic
    * remarks: 
    * default: 
    * isNullable: true
    * isAutoInc: false
    * isPrimary: false
    * type: 12
    * size: 1024
    */
    private String headPic;
    
    /**
    * Column : nickname
    * remarks: 
    * default: 
    * isNullable: true
    * isAutoInc: false
    * isPrimary: false
    * type: 12
    * size: 16
    */
    private String nickname;
    
    /**
    * Column : fullname
    * remarks: 
    * default: 
    * isNullable: true
    * isAutoInc: false
    * isPrimary: false
    * type: 12
    * size: 16
    */
    private String fullname;
    
    /**
    * Column : mobile
    * remarks: 
    * default: 
    * isNullable: true
    * isAutoInc: false
    * isPrimary: false
    * type: 12
    * size: 11
    */
    private String mobile;
    
    /**
    * Column : mobile_area
    * remarks: 
    * default: 
    * isNullable: true
    * isAutoInc: false
    * isPrimary: false
    * type: 12
    * size: 5
    */
    private String mobileArea;
    
    /**
    * Column : mail
    * remarks: 
    * default: 
    * isNullable: true
    * isAutoInc: false
    * isPrimary: false
    * type: 12
    * size: 60
    */
    private String mail;
    
    /**
    * Column : state
    * remarks: 
    * default: 
    * isNullable: true
    * isAutoInc: false
    * isPrimary: false
    * type: 4
    * size: 10
    */
    private Integer state;
    
    /**
    * Column : gender
    * remarks: 
    * default: 
    * isNullable: true
    * isAutoInc: false
    * isPrimary: false
    * type: 4
    * size: 10
    */
    private Integer gender;
    
    /**
    * Column : birthday
    * remarks: 
    * default: 
    * isNullable: true
    * isAutoInc: false
    * isPrimary: false
    * type: 1
    * size: 10
    */
    private String birthday;
    
    /**
    * Column : age
    * remarks: 
    * default: 
    * isNullable: true
    * isAutoInc: false
    * isPrimary: false
    * type: 4
    * size: 10
    */
    private Integer age;
    
    /**
    * Column : last_login_on
    * remarks: 
    * default: 
    * isNullable: true
    * isAutoInc: false
    * isPrimary: false
    * type: 4
    * size: 10
    */
    private Integer lastLoginOn;
    
    /**
    * Column : language
    * remarks: 
    * default: 
    * isNullable: true
    * isAutoInc: false
    * isPrimary: false
    * type: 4
    * size: 10
    */
    private Integer language;
    
    /**
    * Column : time_zone
    * remarks: 
    * default: 
    * isNullable: true
    * isAutoInc: false
    * isPrimary: false
    * type: 4
    * size: 10
    */
    private Integer timeZone;
    
    /**
    * Column : region_code
    * remarks: 
    * default: 
    * isNullable: true
    * isAutoInc: false
    * isPrimary: false
    * type: 4
    * size: 10
    */
    private Integer regionCode;
    
    /**
    * Column : add_on
    * remarks: 
    * default: 
    * isNullable: true
    * isAutoInc: false
    * isPrimary: false
    * type: 4
    * size: 10
    */
    private Integer addOn;
    
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }
    
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    
    public String getMobileArea() {
        return mobileArea;
    }

    public void setMobileArea(String mobileArea) {
        this.mobileArea = mobileArea;
    }
    
    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
    
    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
    
    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }
    
    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
    
    public Integer getLastLoginOn() {
        return lastLoginOn;
    }

    public void setLastLoginOn(Integer lastLoginOn) {
        this.lastLoginOn = lastLoginOn;
    }
    
    public Integer getLanguage() {
        return language;
    }

    public void setLanguage(Integer language) {
        this.language = language;
    }
    
    public Integer getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(Integer timeZone) {
        this.timeZone = timeZone;
    }
    
    public Integer getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(Integer regionCode) {
        this.regionCode = regionCode;
    }
    
    public Integer getAddOn() {
        return addOn;
    }

    public void setAddOn(Integer addOn) {
        this.addOn = addOn;
    }
    
    @Override
    public String toString() {
        return MoreObjects.toStringHelper("User")
            .add("userId",  userId)
            .add("username",  username)
            .add("headPic",  headPic)
            .add("nickname",  nickname)
            .add("fullname",  fullname)
            .add("mobile",  mobile)
            .add("mobileArea",  mobileArea)
            .add("mail",  mail)
            .add("state",  state)
            .add("gender",  gender)
            .add("birthday",  birthday)
            .add("age",  age)
            .add("lastLoginOn",  lastLoginOn)
            .add("language",  language)
            .add("timeZone",  timeZone)
            .add("regionCode",  regionCode)
            .add("addOn",  addOn)
           .toString();
    }

    @Override
    public List<String> fields() {
      return Lists.newArrayList("userId",
						"username",
						"headPic",
						"nickname",
						"fullname",
						"mobile",
						"mobileArea",
						"mail",
						"state",
						"gender",
						"birthday",
						"age",
						"lastLoginOn",
						"language",
						"timeZone",
						"regionCode",
						"addOn");
    }
    
    @Override
    public String primaryField() {
        return "userId";
    }

    @Override
    public Integer id () {
    return userId;
    }

    @Override
    public void setId(Integer id) {
        this.userId = id;
    }

    @Override
    public void setGeneratedKey(Number key) {
    
        this.userId = key.intValue();
    
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("userId",  userId);
        map.put("username",  username);
        map.put("headPic",  headPic);
        map.put("nickname",  nickname);
        map.put("fullname",  fullname);
        map.put("mobile",  mobile);
        map.put("mobileArea",  mobileArea);
        map.put("mail",  mail);
        map.put("state",  state);
        map.put("gender",  gender);
        map.put("birthday",  birthday);
        map.put("age",  age);
        map.put("lastLoginOn",  lastLoginOn);
        map.put("language",  language);
        map.put("timeZone",  timeZone);
        map.put("regionCode",  regionCode);
        map.put("addOn",  addOn);
        return map;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        
    }

   /* START Do not remove/edit this line. CodeGenerator will preserve any code between start and end tags.*/
	/* END Do not remove/edit this line. CodeGenerator will preserve any code between start and end tags.*/


}