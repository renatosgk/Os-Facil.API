package com.oracle.OSfacil.model;


import com.oracle.OSfacil.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "tb_funcionario")
public class Funcionario  implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "funcionario_seq_generator")
    @SequenceGenerator(name = "funcionario_seq_generator",sequenceName = "funcionario_generator",allocationSize=1)
    private Long id;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false, length = 50)
    private String cpf;
    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false)
    private BigDecimal salario;
    @Column(nullable = false,unique = true)
    private String login;
    @Column(nullable = false)
    private String senha;
    @Column(length = 100)
    private String cargo;

    @Column(length = 100)
    private String especialidade;

    @Column(length = 50)
    private String telefone;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


}
