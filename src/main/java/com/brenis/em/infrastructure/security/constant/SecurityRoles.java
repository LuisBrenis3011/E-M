package com.brenis.em.infrastructure.security.constant;

public final class SecurityRoles {

    private SecurityRoles() {}

    public static final String ROLE_PREFIX = "ROLE_";
    public static final String ROLE_PROVEEDOR = "PROVEEDOR";
    public static final String ROLE_CLIENTE = "CLIENTE";

    public static final String PROVEEDOR_AUTHORITY = ROLE_PREFIX + ROLE_PROVEEDOR;
    public static final String CLIENTE_AUTHORITY = ROLE_PREFIX + ROLE_CLIENTE;

    public static final String HAS_ROLE_PROVEEDOR = "hasRole('" + ROLE_PROVEEDOR + "')";
    public static final String HAS_ROLE_CLIENTE = "hasRole('" + ROLE_CLIENTE + "')";
}
