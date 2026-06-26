##  Integrantes
* Felipe Marchant
* Matías Acevedo
* Kevin Angulo

##  Descripción del Proyecto

GPU Store es una plataforma de comercio electrónico orientada a la venta de componentes informáticos, específicamente tarjetas gráficas (GPUs). El backend está construido bajo una arquitectura de microservicios utilizando Spring Boot y Spring Cloud, lo que permite escalabilidad, alta disponibilidad y un acoplamiento débil entre los distintos dominios del negocio

##  Lista de Microservicios

| Microservicio | Puerto | Descripción Principal |
| :--- | :---: | :--- |
| **API Gateway** | `9090` | Enrutador principal, maneja todas las peticiones externas. |
| **Usuario Service** | `9091` | Gestión de perfiles y datos de clientes. |
| **Carrito Service** | `9092` | Gestión de los carritos de compra activos. |
| **Producto Service** | `9093` | Catálogo de GPUs y manejo de categorías. |
| **Auth Service** | `9094` | Autenticación, autorización y emisión de tokens JWT. |
| **Orden Service** | `9095` | Procesamiento y seguimiento del ciclo de vida de los pedidos. |
| **Pago Service** | `9096` | Procesamiento de transacciones financieras. |
| **Reseña Service** | `9097` | Sistema de valoraciones y comentarios de productos. |
| **Boleta Service** | `9098` | Generación de comprobantes de pago. |
| **Envío Service** | `9099` | Logística y estado de despacho de los productos. |
| **Soporte Service** | `9100` | Plataforma de tickets de ayuda y atención al cliente. |

##  Rutas Gateway Documentadas

| Método HTTP | Endpoint | Descripción de la Operación |
| :---: | :--- | :--- |
| `POST` | `/auth/login` | Autenticación de credenciales y generación de token JWT. |
| `POST` | `/auth/register` | Registro público de un nuevo usuario. |
| `POST` | `/auth` | Creación administrativa de un usuario (Retorna modelo HATEOAS). |
| `GET` | `/auth` | Lista todos los usuarios registrados en el sistema. |
| `GET` | `/auth/{id}` | Obtiene los detalles de un usuario específico por su ID. |
| `GET` | `/auth/{id}/exists` | Retorna un valor booleano validando la existencia de un ID. |
| `GET` | `/auth/email/{email}` | Busca y retorna los datos de un usuario mediante su correo. |
| `GET` | `/auth/role/{email}` | Consulta el rol de acceso asignado a un correo electrónico. |
| `PUT` | `/auth/{id}` | Actualiza la información de un usuario existente. |
| `DELETE`| `/auth/{id}` | Elimina un usuario del sistema. |

| Método HTTP | Endpoint | Descripción de la Operación |
| :---: | :--- | :--- |
| `POST` | `/boletas` | Crea e imprime una nueva boleta a partir de los datos de compra. |
| `GET` | `/boletas` | Lista todas las boletas emitidas en la plataforma (Modelo HATEOAS). |
| `GET` | `/boletas/{id}` | Obtiene los detalles completos de una boleta específica mediante su ID. |
| `GET` | `/boletas/{id}/exists` | Valida mediante un valor booleano si un ID de boleta está registrado. |
| `PUT` | `/boletas/{id}` | Actualiza la información de una boleta existente en la base de datos. |
| `DELETE`| `/boletas/{id}` | Elimina físicamente el registro de una boleta del sistema. |
| `GET` | `/boletas/usuario/{usuarioId}` | Recupera el historial completo de boletas emitidas a un cliente. |
| `GET` | `/boletas/orden/{ordenId}` | Busca la boleta asociada directamente a un número de pedido/orden. |
| `GET` | `/boletas/usuario/{usuarioId}/total` | Calcula de forma agregada el monto total de dinero comprado por un usuario. |

| Método HTTP | Endpoint | Descripción de la Operación |
| :---: | :--- | :--- |
| `POST` | `/carritos` | Agrega un nuevo producto (GPU) al carrito de un usuario. |
| `GET` | `/carritos` | Lista todos los ítems registrados en los carritos a nivel global (Administrativo). |
| `GET` | `/carritos/{id}` | Obtiene los detalles de un registro específico del carrito. |
| `GET` | `/carritos/{id}/exists` | Valida mediante un valor booleano si un registro específico del carrito existe. |
| `PUT` | `/carritos/{id}` | Actualiza la cantidad o la información de un ítem ya agregado al carrito. |
| `DELETE`| `/carritos/{id}` | Elimina un ítem del carrito. |
| `GET` | `/carritos/usuario/{usuarioId}` | Recupera la lista completa de productos en el carrito de un usuario específico. |
| `GET` | `/carritos/usuario/{usuarioId}/total` | Devuelve la suma total de la cantidad de productos (ítems) en el carrito de un usuario. |

| Método HTTP | Endpoint | Descripción de la Operación |
| :---: | :--- | :--- |
| `POST` | `/envios` | Genera una nueva orden de despacho y rastreo logístico. |
| `GET` | `/envios` | Lista todos los envíos registrados en la plataforma (Modelo HATEOAS). |
| `GET` | `/envios/{id}` | Consulta el estado y detalle de un envío específico por su ID. |
| `GET` | `/envios/{id}/exists` | Retorna un valor booleano validando si existe un ID de envío. |
| `PUT` | `/envios/{id}` | Actualiza la información o el estado de progreso de un envío. |
| `DELETE`| `/envios/{id}` | Elimina el registro de un envío. |
| `GET` | `/envios/orden/{ordenId}` | Busca el despacho logístico asociado directamente a un número de pedido/orden. |
| `GET` | `/envios/estado/{estado}` | Filtra y lista los envíos según su estatus (ej. "En tránsito", "Entregado"). |
| `GET` | `/envios/ciudad/{ciudad}` | Filtra y lista los despachos agrupados por la ciudad de destino. |


| Método HTTP | Endpoint | Descripción de la Operación |
| :---: | :--- | :--- |
| `POST` | `/ordenes` | Registra y procesa una nueva orden de compra en el sistema. |
| `GET` | `/ordenes` | Lista todo el historial de órdenes a nivel global (Modelo HATEOAS). |
| `GET` | `/ordenes/{id}` | Obtiene los detalles de una orden específica mediante su ID. |
| `GET` | `/ordenes/{id}/exists` | Retorna un valor booleano validando si un número de pedido existe en la base de datos. |
| `PUT` | `/ordenes/{id}` | Actualiza la información o los datos vinculados a una orden existente. |
| `DELETE`| `/ordenes/{id}` | Cancela y elimina el registro de una orden del sistema. |
| `GET` | `/ordenes/usuario/{usuarioId}` | Recupera el historial completo de pedidos realizados por un cliente específico. |
| `GET` | `/ordenes/total-ventas` | Calcula de forma agregada el monto monetario total histórico de todas las ventas de la tienda. |


| Método HTTP | Endpoint | Descripción de la Operación |
| :---: | :--- | :--- |
| `POST` | `/pagos` | Registra y procesa una nueva transacción de pago en el sistema. |
| `GET` | `/pagos` | Lista todo el historial de transacciones de pago registradas (Modelo HATEOAS). |
| `GET` | `/pagos/{id}` | Obtiene los detalles de un pago específico mediante su ID. |
| `GET` | `/pagos/{id}/exists` | Retorna un valor booleano validando si existe un ID de pago. |
| `PUT` | `/pagos/{id}` | Actualiza la información o el estado de una transacción existente. |
| `DELETE`| `/pagos/{id}` | Elimina el registro físico de un pago. |
| `GET` | `/pagos/orden/{ordenId}` | Busca y lista los pagos asociados directamente a un número de pedido/orden. |


| Método HTTP | Endpoint | Descripción de la Operación |
| :---: | :--- | :--- |
| `POST` | `/gpus` | Registra una nueva tarjeta gráfica en el catálogo de la tienda. |
| `GET` | `/gpus` | Lista el inventario completo de GPUs disponibles (Modelo HATEOAS). |
| `GET` | `/gpus/{id}` | Obtiene los detalles específicos (precio, stock, marca) de una GPU. |
| `GET` | `/gpus/{id}/exists` | Retorna un valor booleano validando si el producto existe. |
| `PUT` | `/gpus/{id}` | Actualiza la información técnica o comercial de una GPU. |
| `DELETE`| `/gpus/{id}` | Elimina el registro de una GPU del catálogo. |


| `POST` | `/categorias` | Crea una nueva familia/categoría de productos. |
| `GET` | `/categorias` | Lista todas las categorías registradas en el sistema. |
| `GET` | `/categorias/{id}` | Obtiene los detalles de una categoría específica por su ID. |
| `GET` | `/categorias/{id}/exists` | Valida mediante un valor booleano si la categoría existe. |
| `PUT` | `/categorias/{id}` | Actualiza el nombre o la descripción de una categoría. |
| `DELETE`| `/categorias/{id}` | Elimina una categoría del sistema. |


| Método HTTP | Endpoint | Descripción de la Operación |
| :---: | :--- | :--- |
| `POST` | `/resenas` | Crea y registra una nueva reseña o valoración para un producto (GPU). |
| `GET` | `/resenas` | Lista todas las reseñas registradas en la base de datos (Modelo HATEOAS). |
| `GET` | `/resenas/{id}` | Obtiene los detalles específicos de una reseña mediante su ID. |
| `GET` | `/resenas/usuario/{usuarioId}` | Recupera el historial de todas las reseñas escritas por un usuario específico. |
| `GET` | `/resenas/gpu/{gpuId}` | Lista todos los comentarios y valoraciones asociados a una tarjeta gráfica particular. |
| `GET` | `/resenas/{id}/exists` | Retorna un valor booleano validando si el ID de la reseña existe. |
| `PUT` | `/resenas/{id}` | Actualiza el contenido (comentario o calificación) de una reseña existente. |
| `DELETE`| `/resenas/{id}` | Elimina el registro de una reseña del sistema. |


| Método HTTP | Endpoint | Descripción de la Operación |
| :---: | :--- | :--- |
| `POST` | `/soporte` | Crea y registra un nuevo ticket de soporte o atención al cliente. |
| `GET` | `/soporte` | Lista todos los tickets de soporte registrados en el sistema (Modelo HATEOAS). |
| `GET` | `/soporte/{id}` | Obtiene los detalles específicos y el estado de un ticket mediante su ID. |
| `GET` | `/soporte/usuario/{usuarioId}` | Recupera el historial de tickets generados por un cliente específico. |
| `GET` | `/soporte/orden/{ordenId}` | Lista los tickets de soporte vinculados a un problema con un pedido particular. |
| `GET` | `/soporte/{id}/exists` | Retorna un valor booleano validando si el ID del ticket existe en la base de datos. |
| `PUT` | `/soporte/{id}` | Actualiza la información, respuesta o estado de resolución de un ticket existente. |
| `DELETE`| `/soporte/{id}` | Elimina el registro de un ticket del sistema. |


| Método HTTP | Endpoint | Descripción de la Operación |
| :---: | :--- | :--- |
| `POST` | `/usuarios` | Registra un nuevo perfil de usuario en la plataforma. |
| `GET` | `/usuarios` | Lista todos los perfiles de usuario registrados (Modelo HATEOAS). |
| `GET` | `/usuarios/{id}` | Obtiene los detalles y datos personales de un usuario específico por su ID. |
| `GET` | `/usuarios/{id}/exists` | Retorna un valor booleano validando si el ID del usuario existe en el sistema. |
| `PUT` | `/usuarios/{id}` | Actualiza la información personal de un usuario existente. |
| `DELETE`| `/usuarios/{id}` | Elimina el perfil de un usuario de la base de datos. |

##  Enlaces Swagger
http://localhost:9090/swagger-ui/index.html (ver todos)
http://localhost:9091/swagger-ui/index.html
http://localhost:9092/swagger-ui/index.html
http://localhost:9093/swagger-ui/index.html
http://localhost:9094/swagger-ui/index.html
http://localhost:9095/swagger-ui/index.html
http://localhost:9096/swagger-ui/index.html
http://localhost:9097/swagger-ui/index.html
http://localhost:9098/swagger-ui/index.html
http://localhost:9099/swagger-ui/index.html
http://localhost:9100/swagger-ui/index.html

## Instrucciones de ejecución
git clone https://github.com/FelMar135/Proyecto-full
cd Proyecto-full
docker compose build
docker compose up -d

## Enlace git hud
https://github.com/FelMar135/Proyecto-full

