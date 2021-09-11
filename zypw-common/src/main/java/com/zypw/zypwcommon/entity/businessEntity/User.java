package com.zypw.zypwcommon.entity.businessEntity;

import com.sun.xml.internal.bind.v2.TODO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户实体类
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "t_sys_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(name = "user_account")
    private String userAccount;

    @Column(name = "user_name")
    private String username;

    @Column
    private String password;

    @Column
    private Integer sex;

    @Column(name = "self_introduce")
    private String selfIntroduce;

    @Column(name = "user_level")
    private Integer userLevel;

    // TODO: 2021/9/6 这里的OneToMany如何表示
    @Column
    private String role;

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

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userAccount='" + userAccount + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", sex=" + sex +
                ", selfIntroduce='" + selfIntroduce + '\'' +
                ", userLevel=" + userLevel +
                ", role='" + role + '\'' +
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
