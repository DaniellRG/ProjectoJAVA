-- base de datos para el sistema de mantenimiento de computadores

drop database if exists bd_mantenimiento;
create database bd_mantenimiento character set utf8mb4 collate utf8mb4_unicode_ci;
use bd_mantenimiento;

-- 1. tablas maestras

-- roles de usuario (administrador, tecnico)
create table rol (
    id_rol int auto_increment primary key,
    nombre_rol varchar(50) not null unique
) engine=innodb;

insert into rol (id_rol, nombre_rol) values 
(1, 'Administrador'),
(2, 'Técnico');

-- usuarios del sistema y tecnicos
create table usuario (
    id_usuario int auto_increment primary key,
    usuario varchar(50) not null unique,
    contraseña varchar(100) not null,
    nombre_completo varchar(100) not null,
    id_rol int not null,
    foreign key (id_rol) references rol(id_rol)
        on update cascade on delete restrict
) engine=innodb;

insert into usuario (id_usuario, usuario, contraseña, nombre_completo, id_rol) values
(1, 'ADMIN', '1234', 'Administrador Principal', 1),
(2, 'DANIEL', '7612', 'Daniel Velasquez', 2),
(3, 'DIEGO', '1234', 'Diego Morales', 2),
(4, 'JUAN', '1234', 'Juan Camilo Perez', 2);

-- tipos de equipo
create table tipo_equipo (
    id_tipo_equipo int auto_increment primary key,
    nombre_tipo varchar(50) not null unique
) engine=innodb;

insert into tipo_equipo (id_tipo_equipo, nombre_tipo) values 
(1, 'Computador de Escritorio (PC)'),
(2, 'Portátil'),
(3, 'Servidor');

-- equipos de computo
create table equipo (
    id_equipo int auto_increment primary key,
    codigo_equipo varchar(50) not null unique,
    marca_modelo varchar(100) not null,
    id_tipo_equipo int not null,
    foreign key (id_tipo_equipo) references tipo_equipo(id_tipo_equipo)
        on update cascade on delete restrict
) engine=innodb;

insert into equipo (id_equipo, codigo_equipo, marca_modelo, id_tipo_equipo) values
(1, 'PC-01 Dell', 'Dell OptiPlex 7090', 1),
(2, 'Portátil HP-02', 'HP ProBook 450 G8', 2),
(3, 'Servidor Lenovo', 'Lenovo ThinkSystem ST50', 3),
(4, 'PC-02 ASUS', 'ASUS ExpertCenter D5', 1);

-- tipos de mantenimiento
create table tipo_mantenimiento (
    id_tipo_mantenimiento int auto_increment primary key,
    nombre_tipo varchar(50) not null unique
) engine=innodb;

insert into tipo_mantenimiento (id_tipo_mantenimiento, nombre_tipo) values
(1, 'Preventivo'),
(2, 'Correctivo');

-- estados del mantenimiento
create table estado_mantenimiento (
    id_estado_mantenimiento int auto_increment primary key,
    nombre_estado varchar(50) not null unique
) engine=innodb;

insert into estado_mantenimiento (id_estado_mantenimiento, nombre_estado) values
(1, 'Realizado'),
(2, 'Pendiente'),
(3, 'Próximo');

-- 2. tabla principal de mantenimiento

create table mantenimiento (
    id_mantenimiento int auto_increment primary key,
    id_equipo int not null,
    id_tecnico int not null,
    fecha date not null,
    id_tipo_mantenimiento int not null,
    id_estado_mantenimiento int not null,
    costo decimal(10, 2) not null,
    descripcion text,
    fecha_registro timestamp default current_timestamp,
    foreign key (id_equipo) references equipo(id_equipo)
        on update cascade on delete restrict,
    foreign key (id_tecnico) references usuario(id_usuario)
        on update cascade on delete restrict,
    foreign key (id_tipo_mantenimiento) references tipo_mantenimiento(id_tipo_mantenimiento)
        on update cascade on delete restrict,
    foreign key (id_estado_mantenimiento) references estado_mantenimiento(id_estado_mantenimiento)
        on update cascade on delete restrict
) engine=innodb;

-- datos de prueba iniciales
insert into mantenimiento (id_equipo, id_tecnico, fecha, id_tipo_mantenimiento, id_estado_mantenimiento, costo, descripcion) values
(1, 2, '2026-10-10', 1, 1, 150000.00, 'Limpieza interna y cambio de pasta térmica'),
(2, 3, '2026-10-12', 2, 2, 320000.00, 'Reemplazo de teclado y pantalla'),
(3, 4, '2026-10-15', 1, 3, 450000.00, 'Mantenimiento preventivo de ventilación y discos'),
(1, 2, '2026-10-20', 2, 1, 80000.00, 'Sustitución de memoria RAM'),
(4, 3, '2026-10-22', 1, 2, 120000.00, 'Limpieza de fuente de poder');

-- 3. tabla de auditoria para los triggers

create table auditoria_mantenimiento (
    id_auditoria int auto_increment primary key,
    id_mantenimiento int not null,
    accion varchar(20) not null,
    costo_anterior decimal(10, 2),
    costo_nuevo decimal(10, 2),
    usuario_bd varchar(50),
    fecha_accion timestamp default current_timestamp
) engine=innodb;

-- 4. vistas para los reportes

-- 4.1 informe de mantenimientos registrados
create or replace view vw_mantenimientos_realizados as
select 
    m.id_mantenimiento,
    e.id_equipo,
    e.codigo_equipo as equipo,
    e.marca_modelo,
    u.id_usuario as id_tecnico,
    u.nombre_completo as tecnico,
    u.usuario as usuario_tecnico,
    m.fecha,
    tm.id_tipo_mantenimiento as id_tipo,
    tm.nombre_tipo as tipo_mantenimiento,
    em.id_estado_mantenimiento as id_estado,
    em.nombre_estado as estado,
    m.costo,
    m.descripcion
from mantenimiento m
inner join equipo e on m.id_equipo = e.id_equipo
inner join usuario u on m.id_tecnico = u.id_usuario
inner join tipo_mantenimiento tm on m.id_tipo_mantenimiento = tm.id_tipo_mantenimiento
inner join estado_mantenimiento em on m.id_estado_mantenimiento = em.id_estado_mantenimiento
order by m.fecha desc;

-- 4.2 informe de equipos pendientes de mantenimiento
create or replace view vw_equipos_pendientes as
select 
    m.id_mantenimiento,
    e.codigo_equipo as equipo,
    e.marca_modelo,
    u.nombre_completo as tecnico,
    m.fecha,
    tm.nombre_tipo as tipo_mantenimiento,
    em.nombre_estado as estado,
    m.costo
from mantenimiento m
inner join equipo e on m.id_equipo = e.id_equipo
inner join usuario u on m.id_tecnico = u.id_usuario
inner join tipo_mantenimiento tm on m.id_tipo_mantenimiento = tm.id_tipo_mantenimiento
inner join estado_mantenimiento em on m.id_estado_mantenimiento = em.id_estado_mantenimiento
where em.nombre_estado in ('Pendiente', 'Próximo')
order by m.fecha asc;

-- 4.3 informe resumen de mantenimientos por tecnico
create or replace view vw_resumen_por_tecnico as
select 
    u.id_usuario as id_tecnico,
    u.nombre_completo as tecnico,
    count(m.id_mantenimiento) as cantidad_mantenimientos,
    coalesce(sum(m.costo), 0.00) as total_facturado
from usuario u
left join mantenimiento m on u.id_usuario = m.id_tecnico
where u.id_rol = 2
group by u.id_usuario, u.nombre_completo;

-- 4.4 informe de costos de mantenimiento por equipo
create or replace view vw_costos_por_equipo as
select 
    e.id_equipo,
    e.codigo_equipo as equipo,
    e.marca_modelo,
    count(m.id_mantenimiento) as cantidad_servicios,
    coalesce(sum(m.costo), 0.00) as costo_acumulado
from equipo e
left join mantenimiento m on e.id_equipo = m.id_equipo
group by e.id_equipo, e.codigo_equipo, e.marca_modelo
order by costo_acumulado desc;

-- 5. triggers

delimiter //

-- validacion para que el costo no sea negativo
create trigger trg_mantenimiento_before_insert
before insert on mantenimiento
for each row
begin
    if new.costo < 0 then
        signal sqlstate '45000'
        set message_text = 'Error: El costo del mantenimiento no puede ser negativo.';
    end if;
end//

-- auditoria al registrar un nuevo mantenimiento
create trigger trg_mantenimiento_after_insert
after insert on mantenimiento
for each row
begin
    insert into auditoria_mantenimiento (id_mantenimiento, accion, costo_anterior, costo_nuevo, usuario_bd)
    values (new.id_mantenimiento, 'INSERTAR', null, new.costo, user());
end//

-- auditoria al modificar un mantenimiento
create trigger trg_mantenimiento_after_update
after update on mantenimiento
for each row
begin
    insert into auditoria_mantenimiento (id_mantenimiento, accion, costo_anterior, costo_nuevo, usuario_bd)
    values (new.id_mantenimiento, 'ACTUALIZAR', old.costo, new.costo, user());
end//

-- auditoria al eliminar un mantenimiento
create trigger trg_mantenimiento_after_delete
after delete on mantenimiento
for each row
begin
    insert into auditoria_mantenimiento (id_mantenimiento, accion, costo_anterior, costo_nuevo, usuario_bd)
    values (old.id_mantenimiento, 'ELIMINAR', old.costo, null, user());
end//

delimiter ;

-- 6. funciones

delimiter //

-- total general de costos de todos los mantenimientos
create function fn_costo_total_general()
returns decimal(12, 2)
deterministic
reads sql data
begin
    declare total decimal(12, 2);
    select coalesce(sum(costo), 0.00) into total from mantenimiento;
    return total;
end//

-- cantidad total de mantenimientos de un tecnico
create function fn_total_mantenimientos_tecnico(p_id_tecnico int)
returns int
deterministic
reads sql data
begin
    declare total int;
    select count(*) into total 
    from mantenimiento 
    where id_tecnico = p_id_tecnico;
    return total;
end//

-- costo total acumulado de un equipo
create function fn_costo_acumulado_equipo(p_id_equipo int)
returns decimal(12, 2)
deterministic
reads sql data
begin
    declare total decimal(12, 2);
    select coalesce(sum(costo), 0.00) into total 
    from mantenimiento 
    where id_equipo = p_id_equipo;
    return total;
end//

delimiter ;

-- 7. procedimientos almacenados

delimiter //

-- login de usuarios
create procedure sp_iniciar_sesion(
    in p_usuario varchar(50),
    in p_contraseña varchar(100)
)
begin
    select 
        u.id_usuario,
        u.usuario,
        u.nombre_completo,
        u.id_rol,
        r.nombre_rol
    from usuario u
    inner join rol r on u.id_rol = r.id_rol
    where u.usuario = p_usuario and u.contraseña = p_contraseña;
end//

-- insertar nuevo mantenimiento
create procedure sp_insertar_mantenimiento(
    in p_id_equipo int,
    in p_id_tecnico int,
    in p_fecha date,
    in p_id_tipo int,
    in p_id_estado int,
    in p_costo decimal(10, 2),
    in p_descripcion text
)
begin
    insert into mantenimiento (id_equipo, id_tecnico, fecha, id_tipo_mantenimiento, id_estado_mantenimiento, costo, descripcion)
    values (p_id_equipo, p_id_tecnico, p_fecha, p_id_tipo, p_id_estado, p_costo, p_descripcion);
    
    select last_insert_id() as id_generado;
end//

-- actualizar mantenimiento existente
create procedure sp_actualizar_mantenimiento(
    in p_id_mantenimiento int,
    in p_id_equipo int,
    in p_id_tecnico int,
    in p_fecha date,
    in p_id_tipo int,
    in p_id_estado int,
    in p_costo decimal(10, 2),
    in p_descripcion text
)
begin
    update mantenimiento
    set 
        id_equipo = p_id_equipo,
        id_tecnico = p_id_tecnico,
        fecha = p_fecha,
        id_tipo_mantenimiento = p_id_tipo,
        id_estado_mantenimiento = p_id_estado,
        costo = p_costo,
        descripcion = p_descripcion
    where id_mantenimiento = p_id_mantenimiento;
end//

-- eliminar mantenimiento
create procedure sp_eliminar_mantenimiento(
    in p_id_mantenimiento int
)
begin
    delete from mantenimiento where id_mantenimiento = p_id_mantenimiento;
end//

-- consultar mantenimientos por tecnico
create procedure sp_mantenimientos_por_tecnico(
    in p_id_tecnico int
)
begin
    select 
        m.id_mantenimiento,
        e.codigo_equipo as equipo,
        m.fecha,
        tm.nombre_tipo as tipo_mantenimiento,
        em.nombre_estado as estado,
        m.costo,
        m.descripcion
    from mantenimiento m
    inner join equipo e on m.id_equipo = e.id_equipo
    inner join tipo_mantenimiento tm on m.id_tipo_mantenimiento = tm.id_tipo_mantenimiento
    inner join estado_mantenimiento em on m.id_estado_mantenimiento = em.id_estado_mantenimiento
    where m.id_tecnico = p_id_tecnico
    order by m.fecha desc;
end//

-- procedimientos para llenar combos
create procedure sp_combo_equipos()
begin
    select id_equipo, codigo_equipo, marca_modelo from equipo order by codigo_equipo;
end//

create procedure sp_combo_tecnicos()
begin
    select id_usuario, nombre_completo from usuario where id_rol = 2 order by nombre_completo;
end//

create procedure sp_combo_tipos()
begin
    select id_tipo_mantenimiento, nombre_tipo from tipo_mantenimiento order by id_tipo_mantenimiento;
end//

create procedure sp_combo_estados()
begin
    select id_estado_mantenimiento, nombre_estado from estado_mantenimiento order by id_estado_mantenimiento;
end//

delimiter ;
