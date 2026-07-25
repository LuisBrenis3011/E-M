# NUEVA BASE DE DATOS — SISTEMA DE ADMINISTRACIÓN E&M ANIMACIONES

> **Stack objetivo:** Java 17+ / Spring Boot 3 / JPA + Hibernate / PostgreSQL
> **Sistema anterior:** Python FastAPI + SQLAlchemy → se migra a Spring Boot enfocado en el proveedor.

---

## 1. DIAGRAMA ENTIDAD-RELACIÓN

```
proveedor (1)──(N) usuario          (solo usuarios con rol PROVEEDOR)
proveedor (1)──(N) cliente          (registrados manualmente por el proveedor)
proveedor (1)──(N) inventario
proveedor (1)──(N) paquete
proveedor (1)──(N) contrato
proveedor (1)──(N) plantilla_contrato

usuario (1)──(0..1) cliente         (usuario_id nullable, para futuro login de cliente)

categoria (1)──(N) tematica
categoria (1)──(N) paquete
categoria (1)──(N) evento

tematica (1)──(N) paquete
tematica (1)──(N) evento

inventario (1)──(N) detalle_paquete
inventario (1)──(N) detalle_contrato

paquete (1)──(N) detalle_paquete

cliente (1)──(N) evento

evento (1)──(1) contrato

contrato (1)──(N) detalle_contrato
contrato (1)──(N) pago
contrato (1)──(N) contrato_documento

plantilla_contrato (1)──(N) contrato_documento
```

---

## 2. ENUM TYPES (PostgreSQL)

| Enum | Valores | Uso |
|---|---|---|
| `rol_usuario` | PROVEEDOR, CLIENTE | `usuario.rol` |
| `estado_basico` | ACTIVO, INACTIVO | Soft-delete lógico en tablas que lo requieran |
| `estado_evento` | PROGRAMADO, CONFIRMADO, COMPLETADO, CANCELADO | `evento.estado` |
| `estado_contrato` | BORRADOR, PENDIENTE, CONFIRMADO, COMPLETADO, CANCELADO | `contrato.estado` |
| `tipo_pago` | ADELANTO, SALDO, PAGO_TOTAL | `pago.tipo_pago` |
| `metodo_pago` | YAPE, PLIN, TRANSFERENCIA, EFECTIVO | `pago.metodo_pago` |
| `estado_pago` | PENDIENTE, VERIFICADO, RECHAZADO | `pago.estado` |
| `tipo_plantilla` | CONTRATO, COTIZACION, PROFORMA | `plantilla_contrato.tipo` |

```sql
CREATE TYPE rol_usuario       AS ENUM ('PROVEEDOR', 'CLIENTE');
CREATE TYPE estado_basico     AS ENUM ('ACTIVO', 'INACTIVO');
CREATE TYPE estado_evento     AS ENUM ('PROGRAMADO', 'CONFIRMADO', 'COMPLETADO', 'CANCELADO');
CREATE TYPE estado_contrato   AS ENUM ('BORRADOR', 'PENDIENTE', 'CONFIRMADO', 'COMPLETADO', 'CANCELADO');
CREATE TYPE tipo_pago         AS ENUM ('ADELANTO', 'SALDO', 'PAGO_TOTAL');
CREATE TYPE metodo_pago       AS ENUM ('YAPE', 'PLIN', 'TRANSFERENCIA', 'EFECTIVO');
CREATE TYPE estado_pago       AS ENUM ('PENDIENTE', 'VERIFICADO', 'RECHAZADO');
CREATE TYPE tipo_plantilla    AS ENUM ('CONTRATO', 'COTIZACION', 'PROFORMA');
```

---

## 3. TABLAS

### 3.1 `proveedor` — Datos de la empresa

> El proveedor es la entidad raíz. Toda la data del sistema pertenece a un proveedor.

| Columna | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | `BIGSERIAL` | PK | |
| `nombre_empresa` | `VARCHAR(150)` | NOT NULL | Ej: "E&M ANIMACIONES" |
| `ruc` | `VARCHAR(20)` | UNIQUE, NOT NULL | Para SUNAT |
| `nombre_gerente` | `VARCHAR(150)` | NOT NULL | Nombre que firma en contratos |
| `direccion` | `VARCHAR(255)` | | Dirección fiscal |
| `telefono` | `VARCHAR(20)` | NOT NULL | |
| `email` | `VARCHAR(150)` | | Correo corporativo |
| `logo_url` | `VARCHAR(255)` | | Logo para contratos |
| `terminos_condiciones` | `TEXT` | | Términos por defecto que aparecen en todo contrato |
| `fecha_registro` | `TIMESTAMP` | NOT NULL, DEFAULT now() | |
| `updated_at` | `TIMESTAMP` | NOT NULL, DEFAULT now() | |
| `estado` | `estado_basico` | NOT NULL, DEFAULT 'ACTIVO' | |

```sql
CREATE TABLE proveedor (
    id                   BIGSERIAL PRIMARY KEY,
    nombre_empresa       VARCHAR(150) NOT NULL,
    ruc                  VARCHAR(20) NOT NULL UNIQUE,
    nombre_gerente       VARCHAR(150) NOT NULL,
    direccion            VARCHAR(255),
    telefono             VARCHAR(20) NOT NULL,
    email                VARCHAR(150),
    logo_url             VARCHAR(255),
    terminos_condiciones TEXT,
    fecha_registro       TIMESTAMP NOT NULL DEFAULT now(),
    updated_at           TIMESTAMP NOT NULL DEFAULT now(),
    estado               estado_basico NOT NULL DEFAULT 'ACTIVO'
);
```

---

### 3.2 `usuario` — Usuarios del sistema

> Solo dos roles: PROVEEDOR (quien administra) y CLIENTE (futuro, para auto-reservas).
> `proveedor_id` solo se llena cuando el usuario es PROVEEDOR.

| Columna | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | `BIGSERIAL` | PK | |
| `proveedor_id` | `BIGINT` | FK → proveedor.id, nullable | Solo si rol = PROVEEDOR |
| `nombre` | `VARCHAR(100)` | NOT NULL | |
| `apellido` | `VARCHAR(100)` | NOT NULL | |
| `email` | `VARCHAR(150)` | UNIQUE, NOT NULL | Login |
| `telefono` | `VARCHAR(20)` | | |
| `contrasena_hash` | `VARCHAR(255)` | NOT NULL | BCrypt |
| `rol` | `rol_usuario` | NOT NULL | PROVEEDOR, CLIENTE |
| `fecha_registro` | `TIMESTAMP` | NOT NULL, DEFAULT now() | |
| `ultimo_acceso` | `TIMESTAMP` | | |
| `updated_at` | `TIMESTAMP` | NOT NULL, DEFAULT now() | |
| `estado` | `estado_basico` | NOT NULL, DEFAULT 'ACTIVO' | |

```sql
CREATE TABLE usuario (
    id               BIGSERIAL PRIMARY KEY,
    proveedor_id     BIGINT REFERENCES proveedor(id),
    nombre           VARCHAR(100) NOT NULL,
    apellido         VARCHAR(100) NOT NULL,
    email            VARCHAR(150) NOT NULL UNIQUE,
    telefono         VARCHAR(20),
    contrasena_hash  VARCHAR(255) NOT NULL,
    rol              rol_usuario NOT NULL,
    fecha_registro   TIMESTAMP NOT NULL DEFAULT now(),
    ultimo_acceso    TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT now(),
    estado           estado_basico NOT NULL DEFAULT 'ACTIVO'
);
```

---

### 3.3 `cliente` — Solicitantes / Titulares del contrato

> Tabla independiente. Hoy el proveedor registra al cliente manualmente.
> En el futuro, cuando el cliente cree su propia cuenta, se vincula mediante `usuario_id`.

| Columna | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | `BIGSERIAL` | PK | |
| `proveedor_id` | `BIGINT` | FK → proveedor.id, NOT NULL | |
| `usuario_id` | `BIGINT` | FK → usuario.id, UNIQUE, nullable | Se llena cuando el cliente se registre en el sistema |
| `nombre_completo` | `VARCHAR(200)` | NOT NULL | Nombre o Razón Social |
| `dni` | `VARCHAR(20)` | NOT NULL | |
| `telefono` | `VARCHAR(20)` | NOT NULL | |
| `direccion` | `VARCHAR(255)` | | |
| `referencia` | `VARCHAR(255)` | | Referencia adicional de ubicación |
| `email` | `VARCHAR(150)` | | |
| `fecha_registro` | `TIMESTAMP` | NOT NULL, DEFAULT now() | |
| `updated_at` | `TIMESTAMP` | NOT NULL, DEFAULT now() | |

```sql
CREATE TABLE cliente (
    id               BIGSERIAL PRIMARY KEY,
    proveedor_id     BIGINT NOT NULL REFERENCES proveedor(id),
    usuario_id       BIGINT UNIQUE REFERENCES usuario(id),
    nombre_completo  VARCHAR(200) NOT NULL,
    dni              VARCHAR(20) NOT NULL,
    telefono         VARCHAR(20) NOT NULL,
    direccion        VARCHAR(255),
    referencia       VARCHAR(255),
    email            VARCHAR(150),
    fecha_registro   TIMESTAMP NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP NOT NULL DEFAULT now()
);
```

---

### 3.4 `categoria` — Tipos de evento

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | `BIGSERIAL` | PK |
| `nombre` | `VARCHAR(150)` | NOT NULL |
| `descripcion` | `TEXT` | |

```sql
CREATE TABLE categoria (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(150) NOT NULL,
    descripcion TEXT
);
```

Ejemplos de registros: "Show Infantil", "Hora Loca", "Activaciones", "Aniversarios", "Fiestas Temáticas".

---

### 3.5 `tematica` — Temáticas por categoría

| Columna | Tipo | Restricciones |
|---|---|---|
| `id` | `BIGSERIAL` | PK |
| `categoria_id` | `BIGINT` | FK → categoria.id, NOT NULL |
| `nombre` | `VARCHAR(150)` | NOT NULL |
| `imagen_referencial` | `VARCHAR(255)` | |

```sql
CREATE TABLE tematica (
    id                  BIGSERIAL PRIMARY KEY,
    categoria_id        BIGINT NOT NULL REFERENCES categoria(id),
    nombre              VARCHAR(150) NOT NULL,
    imagen_referencial  VARCHAR(255)
);
```

Ejemplos: "Mario Bross", "Frozen", "Paw Patrol", "Princesas", "Superhéroes".

---

### 3.6 `inventario` — Recursos del proveedor

> **NUEVA.** El proveedor registra aquí todos sus recursos (ítems, equipos, accesorios) con la cantidad que posee.
> Al armar un paquete, busca desde aquí con autocomplete. Si el recurso no existe, lo crea al vuelo.

| Columna | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | `BIGSERIAL` | PK | |
| `proveedor_id` | `BIGINT` | FK → proveedor.id, NOT NULL | Dueño del recurso |
| `nombre` | `VARCHAR(200)` | NOT NULL | Ej: "Muñeco Spiderman", "DJ + micrófono", "Animadora" |
| `descripcion` | `TEXT` | | |
| `cantidad_disponible` | `INTEGER` | NOT NULL, DEFAULT 0 | Stock actual |
| `precio_referencial` | `DECIMAL(10,2)` | NOT NULL, DEFAULT 0.00 | Precio sugerido al incluirlo en un paquete |
| `estado` | `estado_basico` | NOT NULL, DEFAULT 'ACTIVO' | |
| `fecha_creacion` | `TIMESTAMP` | NOT NULL, DEFAULT now() | |
| `updated_at` | `TIMESTAMP` | NOT NULL, DEFAULT now() | |

```sql
CREATE TABLE inventario (
    id                   BIGSERIAL PRIMARY KEY,
    proveedor_id         BIGINT NOT NULL REFERENCES proveedor(id),
    nombre               VARCHAR(200) NOT NULL,
    descripcion          TEXT,
    cantidad_disponible  INTEGER NOT NULL DEFAULT 0,
    precio_referencial   DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    estado               estado_basico NOT NULL DEFAULT 'ACTIVO',
    fecha_creacion       TIMESTAMP NOT NULL DEFAULT now(),
    updated_at           TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_inventario_proveedor ON inventario (proveedor_id);
CREATE INDEX idx_inventario_nombre    ON inventario (proveedor_id, nombre);
```

---

### 3.7 `paquete` — Paquetes armados por el proveedor

> El proveedor crea paquetes eligiendo categoría, temática (opcional) y los ítems de su inventario.
> El precio base puede diferir del total calculado de los ítems (el proveedor pone el precio final que quiera).

| Columna | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | `BIGSERIAL` | PK | |
| `proveedor_id` | `BIGINT` | FK → proveedor.id, NOT NULL | |
| `categoria_id` | `BIGINT` | FK → categoria.id, NOT NULL | |
| `tematica_id` | `BIGINT` | FK → tematica.id, nullable | NULL si el paquete aplica a cualquier temática |
| `nombre` | `VARCHAR(150)` | NOT NULL | Ej: "PAQUETE PREMIUM", "PAQUETE BÁSICO" |
| `descripcion` | `TEXT` | | |
| `precio_base` | `DECIMAL(10,2)` | NOT NULL | Precio de venta del paquete |
| `duracion_base_horas` | `DECIMAL(5,2)` | | Duración estimada |
| `estado` | `estado_basico` | NOT NULL, DEFAULT 'ACTIVO' | |
| `fecha_creacion` | `TIMESTAMP` | NOT NULL, DEFAULT now() | |
| `updated_at` | `TIMESTAMP` | NOT NULL, DEFAULT now() | |

```sql
CREATE TABLE paquete (
    id                  BIGSERIAL PRIMARY KEY,
    proveedor_id        BIGINT NOT NULL REFERENCES proveedor(id),
    categoria_id        BIGINT NOT NULL REFERENCES categoria(id),
    tematica_id         BIGINT REFERENCES tematica(id),
    nombre              VARCHAR(150) NOT NULL,
    descripcion         TEXT,
    precio_base         DECIMAL(10,2) NOT NULL,
    duracion_base_horas DECIMAL(5,2),
    estado              estado_basico NOT NULL DEFAULT 'ACTIVO',
    fecha_creacion      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP NOT NULL DEFAULT now()
);
```

---

### 3.8 `detalle_paquete` — Ítems que componen un paquete

> Cada línea referencia un ítem del inventario del mismo proveedor. Se copia el precio del inventario
> pero puede ajustarse para este paquete en particular.

| Columna | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | `BIGSERIAL` | PK | |
| `paquete_id` | `BIGINT` | FK → paquete.id ON DELETE CASCADE, NOT NULL | |
| `inventario_id` | `BIGINT` | FK → inventario.id, NOT NULL | Ítem del inventario del proveedor |
| `cantidad_incluida` | `INTEGER` | NOT NULL, CHECK > 0 | |
| `precio_unitario` | `DECIMAL(10,2)` | NOT NULL | Precio para este paquete (puede diferir del referencial) |
| `es_obsequio` | `BOOLEAN` | NOT NULL, DEFAULT false | Ej: bazooka de burbujas, globos pencil |
| `orden` | `INTEGER` | NOT NULL, DEFAULT 0 | Orden de aparición en el contrato |

```sql
CREATE TABLE detalle_paquete (
    id                 BIGSERIAL PRIMARY KEY,
    paquete_id         BIGINT NOT NULL REFERENCES paquete(id) ON DELETE CASCADE,
    inventario_id      BIGINT NOT NULL REFERENCES inventario(id),
    cantidad_incluida  INTEGER NOT NULL CHECK (cantidad_incluida > 0),
    precio_unitario    DECIMAL(10,2) NOT NULL,
    es_obsequio        BOOLEAN NOT NULL DEFAULT false,
    orden              INTEGER NOT NULL DEFAULT 0
);
```

---

### 3.9 `evento` — Eventos programados

> Tabla central del **calendario / cronograma**. Un evento puede ser de cualquier tipo:
> show infantil, hora loca, activación, aniversario, etc. Campos como `nombre_cumpleanero`
> y `edad_cumpleanero` son opcionales (aplica solo si es un cumpleaños).

| Columna | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | `BIGSERIAL` | PK | |
| `cliente_id` | `BIGINT` | FK → cliente.id, NOT NULL | |
| `categoria_id` | `BIGINT` | FK → categoria.id, NOT NULL | Tipo de evento |
| `tematica_id` | `BIGINT` | FK → tematica.id, nullable | |
| `tipo_evento` | `VARCHAR(100)` | | Ej: "SHOW INFANTIL", "HORA LOCA", "ACTIVACION", "ANIVERSARIO" |
| `nombre_cumpleanero` | `VARCHAR(150)` | nullable | Solo si aplica |
| `edad_cumpleanero` | `INTEGER` | nullable | Solo si aplica |
| `fecha_evento` | `DATE` | NOT NULL | |
| `hora_inicio` | `TIME` | NOT NULL | Hora de inicio del evento |
| `hora_fin_estimada` | `TIME` | | Hora estimada de finalización |
| `direccion` | `VARCHAR(255)` | NOT NULL | |
| `referencia` | `VARCHAR(255)` | | |
| `aforo_estimado` | `INTEGER` | | |
| `color_calendario` | `VARCHAR(7)` | NOT NULL, DEFAULT '#3B82F6' | Color en el calendario |
| `notas_internas` | `TEXT` | | Notas visibles solo para el proveedor |
| `estado` | `estado_evento` | NOT NULL, DEFAULT 'PROGRAMADO' | |
| `fecha_creacion` | `TIMESTAMP` | NOT NULL, DEFAULT now() | |
| `updated_at` | `TIMESTAMP` | NOT NULL, DEFAULT now() | |

```sql
CREATE TABLE evento (
    id                  BIGSERIAL PRIMARY KEY,
    cliente_id          BIGINT NOT NULL REFERENCES cliente(id),
    categoria_id        BIGINT NOT NULL REFERENCES categoria(id),
    tematica_id         BIGINT REFERENCES tematica(id),
    tipo_evento         VARCHAR(100),
    nombre_cumpleanero  VARCHAR(150),
    edad_cumpleanero    INTEGER,
    fecha_evento        DATE NOT NULL,
    hora_inicio         TIME NOT NULL,
    hora_fin_estimada   TIME,
    direccion           VARCHAR(255) NOT NULL,
    referencia          VARCHAR(255),
    aforo_estimado      INTEGER,
    color_calendario    VARCHAR(7) NOT NULL DEFAULT '#3B82F6',
    notas_internas      TEXT,
    estado              estado_evento NOT NULL DEFAULT 'PROGRAMADO',
    fecha_creacion      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_evento_fecha   ON evento (fecha_evento);
CREATE INDEX idx_evento_estado  ON evento (estado);
CREATE INDEX idx_evento_cliente ON evento (cliente_id);
```

---

### 3.10 `contrato` — Contrato vinculado al evento

> Cada evento tiene exactamente un contrato (1:1). El contrato se crea a partir de un paquete
> o manualmente. El `paquete_id` es opcional porque el proveedor podría armar un contrato
> sin basarse en un paquete predefinido.

| Columna | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | `BIGSERIAL` | PK | |
| `evento_id` | `BIGINT` | FK → evento.id, UNIQUE, NOT NULL | Relación 1:1 |
| `paquete_id` | `BIGINT` | FK → paquete.id, nullable | NULL si es contrato manual |
| `proveedor_id` | `BIGINT` | FK → proveedor.id, NOT NULL | |
| `estado` | `estado_contrato` | NOT NULL, DEFAULT 'BORRADOR' | |
| `monto_total` | `DECIMAL(10,2)` | NOT NULL | |
| `costo_movilidad` | `DECIMAL(10,2)` | NOT NULL, DEFAULT 0.00 | |
| `monto_adelanto` | `DECIMAL(10,2)` | NOT NULL, DEFAULT 0.00 | |
| `monto_pendiente` | `DECIMAL(10,2)` | NOT NULL | = monto_total - monto_adelanto |
| `duracion` | `VARCHAR(100)` | | Descripción textual: "2 horas de show + 30 min extra" |
| `observaciones` | `TEXT` | | |
| `fecha_creacion` | `TIMESTAMP` | NOT NULL, DEFAULT now() | |
| `updated_at` | `TIMESTAMP` | NOT NULL, DEFAULT now() | |

| CHECK | |
|---|---|
| `chk_montos_contrato` | `monto_total = monto_adelanto + monto_pendiente` |

```sql
CREATE TABLE contrato (
    id               BIGSERIAL PRIMARY KEY,
    evento_id        BIGINT NOT NULL UNIQUE REFERENCES evento(id),
    paquete_id       BIGINT REFERENCES paquete(id),
    proveedor_id     BIGINT NOT NULL REFERENCES proveedor(id),
    estado           estado_contrato NOT NULL DEFAULT 'BORRADOR',
    monto_total      DECIMAL(10,2) NOT NULL,
    costo_movilidad  DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    monto_adelanto   DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    monto_pendiente  DECIMAL(10,2) NOT NULL,
    duracion         VARCHAR(100),
    observaciones    TEXT,
    fecha_creacion   TIMESTAMP NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT chk_montos_contrato CHECK (monto_total = monto_adelanto + monto_pendiente)
);

CREATE INDEX idx_contrato_estado    ON contrato (estado);
CREATE INDEX idx_contrato_proveedor ON contrato (proveedor_id);
CREATE INDEX idx_contrato_fecha     ON contrato (fecha_creacion);
```

---

### 3.11 `detalle_contrato` — Líneas del contrato

> Si el contrato se crea desde un paquete, los ítems de `detalle_paquete` se copian automáticamente aquí.
> El proveedor puede luego agregar, quitar o modificar ítems para este contrato en particular.
> Cada línea referencia un ítem del `inventario`.

| Columna | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | `BIGSERIAL` | PK | |
| `contrato_id` | `BIGINT` | FK → contrato.id ON DELETE CASCADE, NOT NULL | |
| `inventario_id` | `BIGINT` | FK → inventario.id, NOT NULL | |
| `cantidad` | `INTEGER` | NOT NULL, CHECK > 0 | |
| `precio_unitario` | `DECIMAL(10,2)` | NOT NULL | |
| `subtotal` | `DECIMAL(10,2)` | NOT NULL | cantidad * precio_unitario |
| `es_obsequio` | `BOOLEAN` | NOT NULL, DEFAULT false | |
| `tipo_detalle` | `VARCHAR(20)` | NOT NULL, DEFAULT 'INCLUYE' | INCLUYE, OBSEQUIO, ADICIONAL |
| `orden` | `INTEGER` | NOT NULL, DEFAULT 0 | |

```sql
CREATE TABLE detalle_contrato (
    id               BIGSERIAL PRIMARY KEY,
    contrato_id      BIGINT NOT NULL REFERENCES contrato(id) ON DELETE CASCADE,
    inventario_id    BIGINT NOT NULL REFERENCES inventario(id),
    cantidad         INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario  DECIMAL(10,2) NOT NULL,
    subtotal         DECIMAL(10,2) NOT NULL,
    es_obsequio      BOOLEAN NOT NULL DEFAULT false,
    tipo_detalle     VARCHAR(20) NOT NULL DEFAULT 'INCLUYE',
    orden            INTEGER NOT NULL DEFAULT 0
);
```

---

### 3.12 `pago` — Registro de pagos

> Soporta subida de fotos de vouchers (Yape, Plin, transferencia). El proveedor sube la imagen
> y luego la verifica manualmente cambiando el estado a VERIFICADO.

| Columna | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | `BIGSERIAL` | PK | |
| `contrato_id` | `BIGINT` | FK → contrato.id, NOT NULL | |
| `tipo_pago` | `tipo_pago` | NOT NULL | ADELANTO, SALDO, PAGO_TOTAL |
| `monto` | `DECIMAL(10,2)` | NOT NULL, CHECK > 0 | |
| `metodo_pago` | `metodo_pago` | NOT NULL | YAPE, PLIN, TRANSFERENCIA, EFECTIVO |
| `estado` | `estado_pago` | NOT NULL, DEFAULT 'PENDIENTE' | |
| `url_comprobante` | `VARCHAR(500)` | | Foto del voucher (JPG, PNG, etc.) |
| `nombre_archivo` | `VARCHAR(255)` | | Nombre original del archivo subido |
| `codigo_operacion` | `VARCHAR(150)` | | Número de operación |
| `notas` | `TEXT` | | |
| `fecha_pago` | `TIMESTAMP` | NOT NULL, DEFAULT now() | |
| `verificado_por` | `BIGINT` | FK → usuario.id, nullable | Usuario que verificó |
| `fecha_verificacion` | `TIMESTAMP` | | |
| `fecha_creacion` | `TIMESTAMP` | NOT NULL, DEFAULT now() | |
| `updated_at` | `TIMESTAMP` | NOT NULL, DEFAULT now() | |

```sql
CREATE TABLE pago (
    id                  BIGSERIAL PRIMARY KEY,
    contrato_id         BIGINT NOT NULL REFERENCES contrato(id),
    tipo_pago           tipo_pago NOT NULL,
    monto               DECIMAL(10,2) NOT NULL CHECK (monto > 0),
    metodo_pago         metodo_pago NOT NULL,
    estado              estado_pago NOT NULL DEFAULT 'PENDIENTE',
    url_comprobante     VARCHAR(500),
    nombre_archivo      VARCHAR(255),
    codigo_operacion    VARCHAR(150),
    notas               TEXT,
    fecha_pago          TIMESTAMP NOT NULL DEFAULT now(),
    verificado_por      BIGINT REFERENCES usuario(id),
    fecha_verificacion  TIMESTAMP,
    fecha_creacion      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_pago_contrato ON pago (contrato_id);
CREATE INDEX idx_pago_estado   ON pago (estado);
```

---

### 3.13 `plantilla_contrato` — Plantillas HTML para generar contratos

> **NUEVA.** Contiene el HTML con placeholders `{{variable}}` que el sistema reemplaza con los datos reales
> de `proveedor`, `cliente`, `evento` y `contrato` al generar el PDF.

| Columna | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | `BIGSERIAL` | PK | |
| `proveedor_id` | `BIGINT` | FK → proveedor.id, NOT NULL | |
| `nombre` | `VARCHAR(150)` | NOT NULL | Ej: "Contrato Show Infantil" |
| `descripcion` | `TEXT` | | |
| `tipo` | `tipo_plantilla` | NOT NULL, DEFAULT 'CONTRATO' | |
| `contenido_html` | `TEXT` | NOT NULL | HTML con placeholders |
| `placeholders` | `JSONB` | | Metadatos de los placeholders disponibles |
| `es_default` | `BOOLEAN` | NOT NULL, DEFAULT false | |
| `estado` | `estado_basico` | NOT NULL, DEFAULT 'ACTIVO' | |
| `fecha_creacion` | `TIMESTAMP` | NOT NULL, DEFAULT now() | |
| `updated_at` | `TIMESTAMP` | NOT NULL, DEFAULT now() | |

**Placeholders disponibles:**
```
{{PROVEEDOR_NOMBRE}}        {{PROVEEDOR_RUC}}         {{PROVEEDOR_GERENTE}}
{{CLIENTE_NOMBRE}}           {{CLIENTE_DNI}}           {{CLIENTE_TELEFONO}}
{{CLIENTE_DIRECCION}}        {{CLIENTE_REFERENCIA}}
{{EVENTO_TIPO}}              {{EVENTO_TEMATICA}}       {{EVENTO_FECHA}}
{{EVENTO_HORA_INICIO}}       {{EVENTO_HORA_FIN}}
{{EVENTO_NOMBRE_CUMPLEANERO}} {{EVENTO_EDAD_CUMPLEANERO}}
{{CONTRATO_MONTO_TOTAL}}     {{CONTRATO_MONTO_ADELANTO}} {{CONTRATO_MONTO_PENDIENTE}}
{{CONTRATO_MOVILIDAD}}       {{CONTRATO_DURACION}}
{{CONTRATO_DETALLE_ITEMS}}   {{CONTRATO_OBSEQUIOS}}
{{CONTRATO_TERMINOS}}        {{FECHA_EMISION}}
```

```sql
CREATE TABLE plantilla_contrato (
    id              BIGSERIAL PRIMARY KEY,
    proveedor_id    BIGINT NOT NULL REFERENCES proveedor(id),
    nombre          VARCHAR(150) NOT NULL,
    descripcion     TEXT,
    tipo            tipo_plantilla NOT NULL DEFAULT 'CONTRATO',
    contenido_html  TEXT NOT NULL,
    placeholders    JSONB,
    es_default      BOOLEAN NOT NULL DEFAULT false,
    estado          estado_basico NOT NULL DEFAULT 'ACTIVO',
    fecha_creacion  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);
```

---

### 3.14 `contrato_documento` — PDFs generados

> **NUEVA.** Cada vez que se genera un contrato en PDF se guarda aquí. Si se regenera,
> se crea una nueva versión. Esto permite historial y reimpresión.

| Columna | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | `BIGSERIAL` | PK | |
| `contrato_id` | `BIGINT` | FK → contrato.id, NOT NULL | |
| `plantilla_id` | `BIGINT` | FK → plantilla_contrato.id, NOT NULL | Plantilla usada |
| `contenido_html` | `TEXT` | | HTML con los datos ya reemplazados |
| `url_pdf` | `VARCHAR(500)` | | Ruta del archivo PDF |
| `version` | `INTEGER` | NOT NULL, DEFAULT 1 | 1, 2, 3... por cada regeneración |
| `generado_por` | `BIGINT` | FK → usuario.id, nullable | |
| `fecha_generacion` | `TIMESTAMP` | NOT NULL, DEFAULT now() | |

```sql
CREATE TABLE contrato_documento (
    id                BIGSERIAL PRIMARY KEY,
    contrato_id       BIGINT NOT NULL REFERENCES contrato(id),
    plantilla_id      BIGINT NOT NULL REFERENCES plantilla_contrato(id),
    contenido_html    TEXT,
    url_pdf           VARCHAR(500),
    version           INTEGER NOT NULL DEFAULT 1,
    generado_por      BIGINT REFERENCES usuario(id),
    fecha_generacion  TIMESTAMP NOT NULL DEFAULT now()
);
```

---

## 4. FLUJO COMPLETO: DEL INVENTARIO AL PDF

Ejemplo con los datos del contrato real de E&M ANIMACIONES:

### Paso 1 — Registrar inventario

El proveedor registra sus recursos en `inventario`:

| nombre | cantidad_disponible | precio_referencial |
|---|---|---|
| Animadora | 3 | 50.00 |
| Bailarina | 2 | 40.00 |
| Muñeco Mario | 1 | 80.00 |
| Muñeco Luigi | 1 | 80.00 |
| Equipo de sonido | 1 | 30.00 |
| DJ + micrófono | 1 | 40.00 |
| Bazooka de burbujas | 2 | 0.00 |
| Bolita de luz | 1 | 0.00 |
| Globos pencil | 50 | 0.00 |
| Tarjeta invitación virtual | 999 | 0.00 |

### Paso 2 — Armar un paquete

Crea el paquete "PAQUETE PREMIUM" (categoría: Show Infantil, temática: Mario Bross) y
usa autocomplete sobre `inventario` para agregar los ítems:

| inventario | cantidad | precio | es_obsequio |
|---|---|---|---|
| Animadora | 1 | 50.00 | false |
| Bailarina | 1 | 40.00 | false |
| Muñeco Mario | 1 | 80.00 | false |
| Muñeco Luigi | 1 | 80.00 | false |
| Equipo de sonido | 1 | 30.00 | false |
| DJ + micrófono | 1 | 40.00 | false |
| Bazooka de burbujas | 1 | 0.00 | true |
| Bolita de luz | 1 | 0.00 | true |
| Globos pencil | 1 | 0.00 | true |
| Tarjeta invitación virtual | 1 | 0.00 | true |

Precio base del paquete: S/525.00 (el proveedor decide, no necesariamente la suma).

### Paso 3 — Agendar evento

Registra el evento en `evento`:

| Campo | Valor |
|---|---|
| cliente | Iris Giovanna Reyes Herrada (ID del registro en `cliente`) |
| categoria | Show Infantil |
| tematica | Mario Bross |
| tipo_evento | SHOW INFANTIL |
| nombre_cumpleanero | Lucca Matías |
| edad_cumpleanero | 4 |
| fecha_evento | 2026-02-21 |
| hora_inicio | 17:00 |
| direccion | Pasaje 1 Mz D lote 16 - Urb. Los Girasoles |

### Paso 4 — Crear contrato

Selecciona el paquete PREMIUM. El sistema copia automáticamente los ítems
de `detalle_paquete` → `detalle_contrato`. El proveedor ajusta montos:

| Campo | Valor |
|---|---|
| paquete_id | ID del paquete PREMIUM |
| monto_total | 525.00 |
| costo_movilidad | 50.00 |
| monto_adelanto | 30.00 |
| monto_pendiente | 495.00 |
| duracion | "2 horas de show + 30 min caritas pintadas" |

### Paso 5 — Registrar pago del adelanto

El cliente paga S/30 por Yape. El proveedor registra en `pago`:

| Campo | Valor |
|---|---|
| tipo_pago | ADELANTO |
| monto | 30.00 |
| metodo_pago | YAPE |
| url_comprobante | /uploads/comprobantes/yape_20260126_001.jpg |
| codigo_operacion | 123456789 |

### Paso 6 — Generar PDF del contrato

1. El sistema lee `plantilla_contrato.contenido_html`
2. Reemplaza cada placeholder:
   - `{{PROVEEDOR_NOMBRE}}` → "E&M ANIMACIONES"
   - `{{PROVEEDOR_RUC}}` → (del proveedor)
   - `{{PROVEEDOR_GERENTE}}` → "Naysha Morales Brenis"
   - `{{CLIENTE_NOMBRE}}` → "Iris Giovanna Reyes Herrada"
   - `{{CLIENTE_DNI}}` → "07257839"
   - `{{CLIENTE_TELEFONO}}` → "993405103"
   - `{{CLIENTE_DIRECCION}}` → "Pasaje 1 Mz D lote 16 - Urb. Los Girasoles"
   - `{{EVENTO_TIPO}}` → "SHOW INFANTIL"
   - `{{EVENTO_TEMATICA}}` → "MARIO BROSS"
   - `{{EVENTO_FECHA}}` → "21 DE FEBRERO DEL 2026"
   - `{{EVENTO_HORA_INICIO}}` → "5:00 pm"
   - `{{EVENTO_NOMBRE_CUMPLEANERO}}` → "Lucca Matías"
   - `{{EVENTO_EDAD_CUMPLEANERO}}` → "04 años"
   - `{{CONTRATO_MONTO_TOTAL}}` → "S/.525.00 SOLES"
   - `{{CONTRATO_MOVILIDAD}}` → "S/.50.00"
   - `{{CONTRATO_MONTO_ADELANTO}}` → "S/30.00 SOLES"
   - `{{CONTRATO_MONTO_PENDIENTE}}` → "S/495.00 SOLES + MOVILIDAD"
   - `{{CONTRATO_DURACION}}` → "2 horas de show + 30 min caritas pintadas"
   - `{{CONTRATO_DETALLE_ITEMS}}` → tabla con los ítems NO obsequio
   - `{{CONTRATO_OBSEQUIOS}}` → tabla con los ítems marcados como obsequio
   - `{{CONTRATO_TERMINOS}}` → `proveedor.terminos_condiciones`
   - `{{FECHA_EMISION}}` → "26 de enero del 2026"
3. Convierte el HTML a PDF
4. Guarda en `contrato_documento` (HTML rellenado + ruta del PDF)
5. El PDF resultante es exactamente el documento que firmará el cliente

---

## 5. QUERIES CLAVE

### 5.1 Calendario / Cronograma mensual

```sql
SELECT
    e.id,
    e.fecha_evento,
    e.hora_inicio,
    e.hora_fin_estimada,
    e.tipo_evento,
    e.estado,
    e.color_calendario,
    c.nombre_completo AS cliente,
    t.nombre AS tematica,
    ct.monto_total,
    ct.estado AS estado_contrato
FROM evento e
JOIN cliente c ON c.id = e.cliente_id
LEFT JOIN tematica t ON t.id = e.tematica_id
LEFT JOIN contrato ct ON ct.evento_id = e.id
WHERE e.fecha_evento BETWEEN :inicio AND :fin
ORDER BY e.fecha_evento, e.hora_inicio;
```

### 5.2 Suma de ventas por período

```sql
SELECT
    DATE_TRUNC('month', ct.fecha_creacion) AS mes,
    COUNT(*) AS total_contratos,
    SUM(ct.monto_total) AS ingresos_totales,
    SUM(ct.monto_adelanto) AS total_adelantos,
    SUM(ct.monto_pendiente) AS total_pendiente,
    SUM(ct.costo_movilidad) AS total_movilidad
FROM contrato ct
WHERE ct.estado IN ('CONFIRMADO', 'COMPLETADO')
GROUP BY DATE_TRUNC('month', ct.fecha_creacion)
ORDER BY mes DESC;
```

### 5.3 Pagos pendientes de verificar

```sql
SELECT
    p.id,
    ct.id AS contrato_id,
    cl.nombre_completo AS cliente,
    e.fecha_evento,
    p.tipo_pago,
    p.monto,
    p.metodo_pago,
    p.url_comprobante,
    p.estado
FROM pago p
JOIN contrato ct ON ct.id = p.contrato_id
JOIN evento e ON e.id = ct.evento_id
JOIN cliente cl ON cl.id = e.cliente_id
WHERE p.estado = 'PENDIENTE'
ORDER BY p.fecha_pago;
```

### 5.4 Autocomplete de inventario al armar paquete

```sql
SELECT id, nombre, cantidad_disponible, precio_referencial
FROM inventario
WHERE proveedor_id = :proveedor_id
  AND estado = 'ACTIVO'
  AND nombre ILIKE '%' || :busqueda || '%'
ORDER BY nombre
LIMIT 10;
```

---

## 6. COMPARATIVA: SISTEMA ANTERIOR vs NUEVO SISTEMA

| Tabla Anterior | Tabla Nueva | Cambios |
|---|---|---|
| `usuario` | `usuario` | Simplificado. Solo PROVEEDOR y CLIENTE. `proveedor_id` ahora es nullable. |
| `proveedor` | `proveedor` | Ampliado: `nombre_gerente`, `logo_url`, `terminos_condiciones`. |
| `cliente` (dependía de usuario) | `cliente` | Independiente + `usuario_id` nullable para futuro login. |
| `categoria` | `categoria` | Sin cambios. |
| `tematica` | `tematica` | Sin cambios. |
| — | `inventario` | **NUEVA.** Recursos del proveedor con cantidad y precio referencial. Base del autocomplete. |
| `servicio_producto` | — | **ELIMINADA.** Sustituida por `inventario`. |
| `paquete` | `paquete` | Eliminados `incluye_caritas`, `duracion_caritas_min`. Queda `duracion_base_horas`. |
| `detalle_paquete` | `detalle_paquete` | FK `servicio_id` → `inventario_id`. |
| `personal` | — | **ELIMINADA.** No se gestiona personal en esta fase. |
| `personal_rol` | — | **ELIMINADA.** |
| `evento` | `evento` | `hora_inicio_caritas` + `hora_inicio_show` → `hora_inicio` + `hora_fin_estimada`. Agregado `color_calendario`, `notas_internas`. |
| `reserva` | `contrato` | Renombrado. Relación 1:1 con evento. `paquete_id` ahora es nullable. |
| `detalle_reserva` | `detalle_contrato` | FK `servicio_id` → `inventario_id`. Agregado `es_obsequio`, `tipo_detalle`, `orden`. |
| `detalle_reserva_personal` | — | **ELIMINADA.** |
| `pago_transaccion` + `comprobante` | `pago` | **Unificados.** Agregado `url_comprobante` (foto voucher), `codigo_operacion`, `verificado_por`. |
| — | `plantilla_contrato` | **NUEVA.** HTML con placeholders. |
| — | `contrato_documento` | **NUEVA.** Historial de PDFs generados. |

### Tablas del sistema anterior NO incluidas
- `resena` — fase futura
- `notificacion` — fase futura
- `ocupacion_global_proveedor` — se deriva de `evento`
- `ocupacion_servicio_producto` — se deriva de `evento`

---

## 7. FUNCIONALIDADES QUE SOPORTA ESTA NUEVA BASE DE DATOS

| Necesidad | Cómo se cubre |
|---|---|
| Calendario / cronograma de eventos | `evento` con `fecha_evento`, `hora_inicio`, `color_calendario`, `estado`. Query mensual. |
| Control de inventario de recursos | `inventario` con `cantidad_disponible`, `precio_referencial`. |
| Armar paquetes con autocomplete | `detalle_paquete` → `inventario`. Búsqueda `ILIKE` sobre `inventario.nombre`. |
| Generación automática de contratos en PDF | `plantilla_contrato` (HTML + placeholders) → reemplazo con datos reales → `contrato_documento` (PDF). |
| Subida de fotos de vouchers (Yape/transferencia) | `pago.url_comprobante` + `pago.nombre_archivo`. |
| Suma de ventas para SUNAT | Queries de agregación sobre `contrato`. `proveedor.ruc` listo para facturación electrónica. |
| Cliente con futuro acceso al sistema | `cliente.usuario_id` nullable. Hoy NULL, mañana se vincula. |

---

## 8. ÍNDICES

```sql
-- Eventos / Calendario
CREATE INDEX idx_evento_fecha   ON evento (fecha_evento);
CREATE INDEX idx_evento_estado  ON evento (estado);
CREATE INDEX idx_evento_cliente ON evento (cliente_id);

-- Contratos
CREATE INDEX idx_contrato_estado    ON contrato (estado);
CREATE INDEX idx_contrato_proveedor ON contrato (proveedor_id);
CREATE INDEX idx_contrato_fecha     ON contrato (fecha_creacion);

-- Pagos
CREATE INDEX idx_pago_contrato ON pago (contrato_id);
CREATE INDEX idx_pago_estado   ON pago (estado);

-- Clientes
CREATE INDEX idx_cliente_dni      ON cliente (dni);
CREATE INDEX idx_cliente_proveedor ON cliente (proveedor_id);

-- Inventario (clave para autocomplete)
CREATE INDEX idx_inventario_proveedor ON inventario (proveedor_id);
CREATE INDEX idx_inventario_nombre    ON inventario (proveedor_id, nombre);

-- Paquetes
CREATE INDEX idx_paquete_categoria ON paquete (categoria_id);
CREATE INDEX idx_paquete_proveedor ON paquete (proveedor_id);
```

---

## 9. NOTAS PARA SPRING BOOT

1. **Entidades JPA**: `@Entity` por tabla. `@Enumerated(EnumType.STRING)` para los enums.
2. **Auditoría**: `@CreatedDate` / `@LastModifiedDate` con `@EntityListeners(AuditingEntityListener.class)`.
3. **Generación de PDF**: Flying Saucer (HTML → PDF) o iText. La plantilla HTML se rellena con Thymeleaf o simple `String.replace`.
4. **Upload de imágenes**: `MultipartFile` → almacenar en disco local o S3/MinIO. Validar extensiones: jpg, jpeg, png, pdf.
5. **Calendario frontend**: FullCalendar.js consumiendo endpoint REST sobre `evento`.
6. **Autocomplete**: Endpoint `GET /api/inventario/search?q=muñe` → `SELECT ... WHERE nombre ILIKE '%' || :q || '%'`.
7. **Soft delete**: Si se requiere, agregar `deleted_at TIMESTAMP NULL` y usar `@SQLDelete` / `@Where` de Hibernate.
