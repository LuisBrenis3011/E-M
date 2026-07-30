package com.brenis.em.application.service.impl;

import com.brenis.em.application.dto.request.LoginRequest;
import com.brenis.em.application.dto.request.RegisterEmpresaRequest;
import com.brenis.em.application.dto.request.RegisterRequest;
import com.brenis.em.application.dto.response.EmpresaResponse;
import com.brenis.em.application.dto.response.JwtResponse;
import com.brenis.em.application.service.IAuthService;
import com.brenis.em.application.service.IPlantillaService;
import com.brenis.em.application.service.IProveedorService;
import com.brenis.em.application.service.IUsuarioService;
import com.brenis.em.domain.enums.TipoPlantilla;
import com.brenis.em.domain.plantilla.PlantillaContrato;
import com.brenis.em.domain.proveedor.Proveedor;
import com.brenis.em.domain.usuario.Usuario;
import com.brenis.em.infrastructure.exception.BusinessException;
import com.brenis.em.infrastructure.security.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@Transactional
public class AuthServiceImpl implements IAuthService {

    private final IUsuarioService usuarioService;
    private final IProveedorService proveedorService;
    private final IPlantillaService plantillaService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Value("classpath:templates/contrato-default.html")
    private Resource defaultTemplate;

    public AuthServiceImpl(IUsuarioService usuarioService,
                           IProveedorService proveedorService,
                           IPlantillaService plantillaService,
                           AuthenticationManager authenticationManager,
                           JwtProvider jwtProvider,
                           PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.proveedorService = proveedorService;
        this.plantillaService = plantillaService;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        usuarioService.updateLastAccess(request.getEmail());

        Usuario usuario = usuarioService.findByEmail(request.getEmail());
        String token = jwtProvider.generateToken(usuario.getEmail(), usuario.getRol().name());

        return buildJwtResponse(token, usuario);
    }

    @Override
    public EmpresaResponse registerEmpresa(RegisterEmpresaRequest request) {
        if (usuarioService.existsByEmail(request.getAdminEmail())) {
            throw new BusinessException("El email ya esta registrado");
        }

        if (proveedorService.findByRuc(request.getRuc()).isPresent()) {
            throw new BusinessException("Ya existe una empresa registrada con ese RUC");
        }

        Proveedor proveedor = Proveedor.builder()
                .nombreEmpresa(request.getNombreEmpresa())
                .ruc(request.getRuc())
                .nombreGerente(request.getNombreGerente())
                .direccion(request.getDireccion())
                .telefono(request.getTelefono())
                .email(request.getAdminEmail())
                .build();
        proveedor = proveedorService.save(proveedor);

        crearPlantillaDefault(proveedor);

        Usuario usuario = Usuario.builder()
                .nombre(request.getAdminNombre())
                .apellido(request.getAdminApellido())
                .email(request.getAdminEmail())
                .contrasenaHash(passwordEncoder.encode(request.getAdminPassword()))
                .build();
        usuario = usuarioService.saveProveedor(usuario, proveedor.getId());

        String token = jwtProvider.generateToken(usuario.getEmail(), usuario.getRol().name());

        return EmpresaResponse.builder()
                .proveedorId(proveedor.getId())
                .nombreEmpresa(proveedor.getNombreEmpresa())
                .ruc(proveedor.getRuc())
                .token(token)
                .adminEmail(usuario.getEmail())
                .build();
    }

    @Override
    public JwtResponse register(RegisterRequest request) {
        if (usuarioService.existsByEmail(request.getEmail())) {
            throw new BusinessException("El email ya esta registrado");
        }

        Proveedor proveedor = proveedorService.findByRuc(request.getRuc())
                .orElseThrow(() -> new BusinessException(
                        "No existe una empresa con el RUC " + request.getRuc()
                                + ". La empresa debe registrarse primero."));

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .contrasenaHash(passwordEncoder.encode(request.getPassword()))
                .build();
        usuario = usuarioService.saveProveedor(usuario, proveedor.getId());

        String token = jwtProvider.generateToken(usuario.getEmail(), usuario.getRol().name());
        return buildJwtResponse(token, usuario);
    }

    private JwtResponse buildJwtResponse(String token, Usuario usuario) {
        return new JwtResponse(
                token,
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getRol().name(),
                usuario.getProveedor() != null ? usuario.getProveedor().getId() : null
        );
    }

    private void crearPlantillaDefault(Proveedor proveedor) {
        PlantillaContrato plantilla = PlantillaContrato.builder()
                .proveedor(proveedor)
                .nombre("Contrato Estandar")
                .descripcion("Plantilla default generada al registrarse")
                .tipo(TipoPlantilla.CONTRATO)
                .contenidoHtml(loadDefaultTemplate())
                .esDefault(true)
                .build();
        plantillaService.create(proveedor.getId(), plantilla);
    }

    private String loadDefaultTemplate() {
        try {
            return defaultTemplate.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new BusinessException("No se pudo cargar la plantilla default del contrato");
        }
    }
}
