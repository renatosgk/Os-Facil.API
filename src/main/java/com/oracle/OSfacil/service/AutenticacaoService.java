package com.oracle.OSfacil.service;

import com.oracle.OSfacil.repository.ClienteRepository;
import com.oracle.OSfacil.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        var funcionario = funcionarioRepository.findByEmailIgnoreCase(username);
        if (funcionario.isPresent()) return funcionario.get();


        var cliente = clienteRepository.findByEmailIgnoreCase(username);
        if (cliente.isPresent()) return cliente.get();

        throw new UsernameNotFoundException("Usuário não encontrado!");
    }
}

