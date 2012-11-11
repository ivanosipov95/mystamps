--
-- Generated by maven: mvn -P test clean compile hibernate3:hbm2ddl
--

    create table countries (
        id integer generated by default as identity,
        created_at timestamp not null,
        updated_at timestamp not null,
        name varchar(50) not null unique,
        version bigint not null,
        created_by integer not null,
        updated_by integer not null,
        primary key (id)
    );

    create table gibbons_catalog (
        id integer generated by default as identity,
        code varchar(4) unique,
        primary key (id)
    );

    create table images (
        id integer generated by default as identity,
        data blob not null,
        type varchar(4) not null,
        primary key (id)
    );

    create table michel_catalog (
        id integer generated by default as identity,
        code varchar(4) unique,
        primary key (id)
    );

    create table scott_catalog (
        id integer generated by default as identity,
        code varchar(4) unique,
        primary key (id)
    );

    create table series (
        id integer generated by default as identity,
        comment varchar(255),
        image_url varchar(255),
        created_at timestamp not null,
        updated_at timestamp not null,
        perforated boolean not null,
        quantity integer not null,
        released_at date,
        version bigint not null,
        country_id integer,
        created_by integer not null,
        updated_by integer not null,
        primary key (id)
    );

    create table series_gibbons_catalog (
        series_id integer not null,
        gibbons_id integer not null,
        primary key (series_id, gibbons_id)
    );

    create table series_michel_catalog (
        series_id integer not null,
        michel_id integer not null,
        primary key (series_id, michel_id)
    );

    create table series_scott_catalog (
        series_id integer not null,
        scott_id integer not null,
        primary key (series_id, scott_id)
    );

    create table series_yvert_catalog (
        series_id integer not null,
        yvert_id integer not null,
        primary key (series_id, yvert_id)
    );

    create table suspicious_activities (
        id integer generated by default as identity,
        ip varchar(15) not null,
        occured_at timestamp not null,
        page varchar(100) not null,
        referer_page varchar(255) not null,
        user_agent varchar(255) not null,
        type_id integer not null,
        user_id integer,
        primary key (id)
    );

    create table suspicious_activities_types (
        id integer generated by default as identity,
        name varchar(100) not null unique,
        primary key (id)
    );

    create table users (
        id integer generated by default as identity,
        activated_at timestamp not null,
        email varchar(255) not null,
        hash varchar(40) not null,
        login varchar(15) not null unique,
        name varchar(100) not null,
        registered_at timestamp not null,
        salt varchar(10) not null,
        primary key (id)
    );

    create table users_activation (
        act_key varchar(10) not null,
        created_at timestamp not null,
        email varchar(255) not null,
        primary key (act_key)
    );

    create table yvert_catalog (
        id integer generated by default as identity,
        code varchar(4) unique,
        primary key (id)
    );

    alter table countries 
        add constraint FK509F9AB4E7A879A3 
        foreign key (created_by) 
        references users;

    alter table countries 
        add constraint FK509F9AB4846862F0 
        foreign key (updated_by) 
        references users;

    alter table series 
        add constraint FKCA01FE77E7A879A3 
        foreign key (created_by) 
        references users;

    alter table series 
        add constraint FKCA01FE77DF85B0F0 
        foreign key (country_id) 
        references countries;

    alter table series 
        add constraint FKCA01FE77846862F0 
        foreign key (updated_by) 
        references users;

    alter table series_gibbons_catalog 
        add constraint FK40EBC5A4FC1DFB59 
        foreign key (gibbons_id) 
        references gibbons_catalog;

    alter table series_gibbons_catalog 
        add constraint FK40EBC5A41F6A4A4 
        foreign key (series_id) 
        references series;

    alter table series_michel_catalog 
        add constraint FKD28C650A128BA359 
        foreign key (michel_id) 
        references michel_catalog;

    alter table series_michel_catalog 
        add constraint FKD28C650A1F6A4A4 
        foreign key (series_id) 
        references series;

    alter table series_scott_catalog 
        add constraint FK39AAAAF17555D7F 
        foreign key (scott_id) 
        references scott_catalog;

    alter table series_scott_catalog 
        add constraint FK39AAAAF11F6A4A4 
        foreign key (series_id) 
        references series;

    alter table series_yvert_catalog 
        add constraint FKA96A3A3C1F6A4A4 
        foreign key (series_id) 
        references series;

    alter table series_yvert_catalog 
        add constraint FKA96A3A3CDA4206A9 
        foreign key (yvert_id) 
        references yvert_catalog;

    alter table suspicious_activities 
        add constraint FK35F0CA0F8D3FBEA4 
        foreign key (user_id) 
        references users;

    alter table suspicious_activities 
        add constraint FK35F0CA0FC005E970 
        foreign key (type_id) 
        references suspicious_activities_types;
