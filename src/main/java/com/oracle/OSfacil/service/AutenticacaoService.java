package com.oracle.OSfacil.service;

import com.oracle.OSfacil.repository.ClienteRepository;
import com.oracle.OSfacil.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutenticacaoService implements UserDetailsService {

    private final FuncionarioRepository funcionarioRepository;
    private final ClienteRepository clienteRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return funcionarioRepository.findByEmailIgnoreCase(username)
                .<UserDetails>map(f -> f)
                .or(() -> clienteRepository.findByEmailIgnoreCase(username).map(c -> c))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado: " + username));
    }
}
