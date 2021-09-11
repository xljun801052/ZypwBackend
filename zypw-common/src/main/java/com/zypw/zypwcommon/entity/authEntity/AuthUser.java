package com.zypw.zypwcommon.entity.authEntity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Entity
@Table(name = "t_sys_auth_user")
public class AuthUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "user_account")
    private String userAccount;
    @Column
    private String password;
    @Column
    private String authorities;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        Arrays.asList(this.authorities.split(",")).forEach(k->authorities.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return k;
            }
        }));
        return authorities;
    }

    public Integer getId() {
        return id;
    }

    public String getUserAccount() {
        return userAccount;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userAccount;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // TODO: 2021/9/11 这里应该提供更改权限，不能覆盖之前的权限
    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public AuthUser(Integer id, String userAccount, String password, String authorities) {
        this.id = id;
        this.userAccount = userAccount;
        this.password = password;
        this.authorities = authorities;
    }
}
