package com.zypw.zypwgateway.token;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

/**
 * the class that will encapsulate the user input info!
 * */
public class ReacativeSystemAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private Object principal;
    private Object credentials;

    public ReacativeSystemAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

//    /**
//     * Creates a token with the supplied array of authorities.
//     *
//     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
//     *                    represented by this authentication object.
//     */
//    public ReacativeSystemAuthenticationToken(String principal,String credentials,Collection<? extends GrantedAuthority> authorities) {
//        super(authorities);
//    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public void setPrincipal(Object principal) {
        this.principal = principal;
    }

    public void setCredentials(Object credentials) {
        this.credentials = credentials;
    }
}
