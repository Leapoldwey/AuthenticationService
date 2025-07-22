--liquibase formatted sql

--changeset Mihail:1.0.0
create table if not exists users(
    id uuid primary key,
    login varchar(255) not null unique,
    password varchar(255) not null,
    email varchar(255) not null unique
);

--changeset Mihail:1.0.1
create table if not exists roles(
    id uuid primary key,
    name varchar(64) not null unique
);

--changeset Mihail:1.0.2
create table if not exists users_roles(
    user_id uuid,
    role_id uuid,
    constraint user_id_fk foreign key (user_id) references users(id) on delete cascade,
    constraint role_id_fk foreign key (role_id) references roles(id) on delete cascade
);

--changeset Mihail:1.0.3
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


--changeset Mihail:1.0.4
insert into roles(id, name) values
    (uuid_generate_v4(), 'GUEST'),
    (uuid_generate_v4(), 'ADMIN'),
    (uuid_generate_v4(), 'PREMIUM_USER');

--changeset Mihail:1.0.5
insert into users(id, login, password, email) values
    (uuid_generate_v4(),
     'mihail',
     '$2a$12$NU1fkdZdowa1rNwopie7D.7jj.zrL0T3JLgVpor3S1kB6DkiAQL82',
      'mihail@mail.ru'
    );

--changeset Mihail:1.0.6
insert into users_roles (user_id, role_id) values
    (
        (select id from users where login = 'mihail'),
        (select id from roles where name = 'ADMIN')
    );

--changeset Mihail:1.0.7
create table if not exists refresh_token(
    id uuid primary key,
    user_id uuid,
    token varchar(255) not null unique,
    expiry_date timestamp,
    revoked boolean not null default false,
    constraint user_id_refresh_fk foreign key (user_id) references users(id)
);

--changeset Mihail:1.0.8
insert into users(id, login, password, email) values
    (uuid_generate_v4(),
     'Alex',
     '$2a$12$NU1fkdZdowa1rNwopie7D.7jj.zrL0T3JLgVpor3S1kB6DkiAQL82',
     'alex@mail.ru'
    );

--changeset Mihail:1.0.9
insert into users_roles (user_id, role_id) values
    (
        (select id from users where login = 'Alex'),
        (select id from roles where name = 'GUEST')
    );

--changeset Mihail:1.0.10
insert into users(id, login, password, email) values
    (uuid_generate_v4(),
     'Kate',
     '$2a$12$NU1fkdZdowa1rNwopie7D.7jj.zrL0T3JLgVpor3S1kB6DkiAQL82',
     'kate@mail.ru'
    );

--changeset Mihail:1.0.11
insert into users_roles (user_id, role_id) values
    (
        (select id from users where login = 'Kate'),
        (select id from roles where name = 'PREMIUM_USER')
    );