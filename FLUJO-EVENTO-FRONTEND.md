# Prompt para Frontend — Flujo de Creación de Evento

## Resumen del problema

Actualmente el formulario de creación de evento tiene estos errores:
1. **Pide seleccionar "categoría"** pero no hay forma de crearlas/gestionarlas (las categorías viven en backend, se obtienen del endpoint `GET /api/categorias`)
2. **Pide seleccionar "tipo de evento"** como si fuera un dropdown/selector, cuando en realidad es **texto libre** (`String`, máx 100 chars, opcional)
3. Faltan las **temáticas** como concepto intermedio entre categoría y evento

---

## Modelo de datos

```
CATEGORIA (obligatorio)          TEMATICA (opcional)           tipoEvento (texto libre, opcional)
┌──────────────────┐            ┌──────────────────┐           ┌────────────────────┐
│ id               │ ◄───────── │ id               │           │ "Fiesta con magia" │
│ nombre (String)  │   1:N      │ categoria_id FK  │           │ "Cena de gala"     │
│ descripcion      │            │ nombre           │           └────────────────────┘
└──────────────────┘            │ imagenReferencial│
                                └──────────────────┘
```

```
EVENTO
├── clienteId         (FK a Cliente, OBLIGATORIO)
├── categoriaId       (FK a Categoria, OBLIGATORIO)
├── tematicaId        (FK a Tematica, OPCIONAL, depende de la categoría elegida)
├── tipoEvento        (String libre, máx 100, OPCIONAL)  ← NO es un selector
├── nombreCumpleanero (String, máx 150, OPCIONAL)
├── edadCumpleanero   (Integer, OPCIONAL)
├── fechaEvento       (LocalDate, OBLIGATORIO)
├── horaInicio        (LocalTime, OBLIGATORIO)
├── horaFinEstimada   (LocalTime, OPCIONAL)
├── direccion         (String, máx 255, OBLIGATORIO)
├── referencia        (String, máx 255, OPCIONAL)
├── aforoEstimado     (Integer, OPCIONAL)
├── colorCalendario   (String, default "#3B82F6", OPCIONAL)
├── notasInternas     (String, Text, OPCIONAL)
└── estado            (EstadoEvento enum, default "PROGRAMADO")
```

> **Importante:** No existe la entidad "TipoEvento". `tipoEvento` es solo un campo `String` en Evento. No hay tabla, no hay CRUD, no hay relación.

---

## APIs disponibles

### 1. Clientes
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/clientes` | Listar clientes del proveedor autenticado (paginado). Soporta `?q=` (búsqueda por nombre) y `?dni=` (búsqueda por DNI) |
| `POST` | `/api/clientes` | Crear cliente |
| `GET` | `/api/clientes/{id}` | Obtener cliente por ID |
| `PUT` | `/api/clientes/{id}` | Actualizar cliente |
| `DELETE` | `/api/clientes/{id}` | Eliminar cliente |

### 2. Categorías
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/categorias` | Listar todas las categorías. Devuelve `List<Categoria>` |
| `GET` | `/api/categorias/{id}` | Obtener categoría por ID |
| `GET` | `/api/categorias/{id}/tematicas` | Listar temáticas de una categoría específica |

**Response de Categoria (la API devuelve la entidad directamente):**
```json
{
  "id": 1,
  "nombre": "Fiestas Infantiles",
  "descripcion": "Eventos para niños..."
}
```

### 3. Temáticas
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/tematicas?categoriaId={id}` | Listar temáticas (opcionalmente filtradas por categoría) |
| `GET` | `/api/tematicas/{id}` | Obtener temática por ID |

**Response de Tematica:**
```json
{
  "id": 1,
  "categoria": { "id": 1, "nombre": "Fiestas Infantiles", ... },
  "nombre": "Paw Patrol",
  "imagenReferencial": "url_o_path"
}
```

### 4. Eventos (CRUD)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/eventos` | Crear evento |
| `PUT` | `/api/eventos/{id}` | Actualizar evento |
| `GET` | `/api/eventos/{id}` | Obtener evento por ID |
| `GET` | `/api/eventos/calendario?inicio={fecha}&fin={fecha}` | Eventos en rango de fechas (vista calendario) |
| `GET` | `/api/eventos/cliente/{clienteId}` | Eventos de un cliente |
| `PATCH` | `/api/eventos/{id}/estado?estado={PROGRAMADO\|CONFIRMADO\|COMPLETADO\|CANCELADO}` | Cambiar estado |
| `DELETE` | `/api/eventos/{id}` | Eliminar evento |

**POST /api/eventos — Request Body:**
```json
{
  "clienteId": 1,
  "categoriaId": 2,
  "tematicaId": 5,
  "tipoEvento": "Cumpleaños con show de magia",
  "nombreCumpleanero": "Luis",
  "edadCumpleanero": 8,
  "fechaEvento": "2026-08-15",
  "horaInicio": "15:00:00",
  "horaFinEstimada": "19:00:00",
  "direccion": "Av. Siempre Viva 742",
  "referencia": "Frente al parque",
  "aforoEstimado": 50,
  "colorCalendario": "#3B82F6",
  "notasInternas": "El cliente pidió globos extra"
}
```

---

## Flujo correcto del formulario de creación de evento

### Paso 1: Seleccionar cliente
- Dropdown/autocomplete con búsqueda. Usar `GET /api/clientes?q={texto}` o `GET /api/clientes?dni={dni}`
- Campo OBLIGATORIO

### Paso 2: Seleccionar categoría
- Dropdown/selector que se alimenta de `GET /api/categorias`
- Campo OBLIGATORIO
- Al seleccionar una categoría, se debe cargar el Paso 3

### Paso 3: Seleccionar temática (OPCIONAL)
- Dropdown que depende de la categoría seleccionada en el Paso 2
- Usar `GET /api/tematicas?categoriaId={id}` o `GET /api/categorias/{id}/tematicas`
- Campo OPCIONAL → debe permitir dejarse vacío ("Sin temática")

### Paso 4: Tipo de evento (CAMPO DE TEXTO LIBRE)
- **NO** es un dropdown ni un selector
- Es un `<input type="text">` o `<textarea>` simple
- Máximo 100 caracteres
- Campo OPCIONAL
- Placeholder sugerido: "Ej: Cumpleaños con show de magia, Cena de gala..."

### Paso 5: Datos del cumpleañero (OPCIONALES)
- `nombreCumpleanero` → input text, máx 150 caracteres
- `edadCumpleanero` → input number

### Paso 6: Fecha y hora
- `fechaEvento` → date picker, OBLIGATORIO
- `horaInicio` → time picker, OBLIGATORIO
- `horaFinEstimada` → time picker, OPCIONAL

### Paso 7: Ubicación
- `direccion` → input text, máx 255 caracteres, OBLIGATORIO
- `referencia` → input text, máx 255 caracteres, OPCIONAL

### Paso 8: Detalles adicionales
- `aforoEstimado` → input number, OPCIONAL
- `colorCalendario` → color picker, OPCIONAL (default `#3B82F6`)
- `notasInternas` → textarea, OPCIONAL

---

## Resumen de correcciones necesarias

| Problema actual | Corrección |
|----------------|------------|
| No se pueden crear/gestionar categorías | Implementar un CRUD de categorías en frontend usando los endpoints de `/api/categorias`. Las categorías ya existen en BD pero no hay UI para administrarlas |
| "Tipo de evento" es un selector | Cambiarlo a un **input de texto libre** (String opcional, máx 100 chars) |
| No existe el concepto de "temática" en el formulario | Agregar un selector de temática que dependa de la categoría elegida (campo opcional) |
| No hay página de gestión de temáticas | Implementar UI para temáticas usando `/api/tematicas` |
| El formulario se llama "cronograma" | Es un naming local del front, no hay backend llamado así. Sugiero renombrar a "Nuevo Evento" |

---

## Notas adicionales para el front

- **Autenticación:** Los endpoints de clientes y eventos requieren `@PreAuthorize("hasRole('PROVEEDOR')")`. Categorías y temáticas son públicos.
- **Categorías y Temáticas no tienen DTOs:** El backend devuelve las entidades JPA directamente (`Categoria` y `Tematica`), con su estructura anidada (Tematica incluye el objeto Categoria dentro).
- **El estado del evento** se asigna automáticamente como `PROGRAMADO` al crear. Para cambiarlo se usa `PATCH /api/eventos/{id}/estado?estado=CONFIRMADO`.
