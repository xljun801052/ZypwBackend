package com.zypw.zypwcommon.entity.businessEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户实体类
 */

@Entity
@Table(name = "t_sys_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    @Column(name = "user_account")
    private String userAccount;
    @Column(name = "user_name")
    private String userName;
    @Column
    private String password;
    @Column
    private Integer sex;
    @Column(name = "self_introduce")
    private String selfIntroduce;
    @Column(name = "user_level")
    private Integer userLevel;
    @Column(name = "school_name")
    private String schoolName;
    @Column(name = "birth_date")
    private Date birthDate;
    @Column
    private String address;
    @Column
    private String avatar;
    @Column(name = "bg_img")
    private String bgImg;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "modify_time")
    private Date modifyTime;
    @Column(name = "alive_flag")
    private Integer aliveFlag;

    public User() {
    }

    public User(Integer userId, String userAccount, String userName, String password, Integer sex, String selfIntroduce, Integer userLevel, String schoolName, Date birthDate, String address, String avatar, String bgImg, Date createTime, Date modifyTime, Integer aliveFlag) {
        this.userId = userId;
        this.userAccount = userAccount;
        this.userName = userName;
        this.password = password;
        this.sex = sex;
        this.selfIntroduce = selfIntroduce;
        this.userLevel = userLevel;
        this.schoolName = schoolName;
        this.birthDate = birthDate;
        this.address = address;
        this.avatar = avatar;
        this.bgImg = bgImg;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.aliveFlag = aliveFlag;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getSelfIntroduce() {
        return selfIntroduce;
    }

    public void setSelfIntroduce(String selfIntroduce) {
        this.selfIntroduce = selfIntroduce;
    }

    public Integer getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(Integer userLevel) {
        this.userLevel = userLevel;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBgImg() {
        return bgImg;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Integer getAliveFlag() {
        return aliveFlag;
    }

    public void setAliveFlag(Integer aliveFlag) {
        this.aliveFlag = aliveFlag;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userAccount='" + userAccount + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", sex=" + sex +
                ", selfIntroduce='" + selfIntroduce + '\'' +
                ", userLevel=" + userLevel +
                ", schoolName='" + schoolName + '\'' +
                ", birthDate=" + birthDate +
                ", address='" + address + '\'' +
                ", avatar='" + avatar + '\'' +
                ", bgImg='" + bgImg + '\'' +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", aliveFlag=" + aliveFlag +
                '}';
    }
}
