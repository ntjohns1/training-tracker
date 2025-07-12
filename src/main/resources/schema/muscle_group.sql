use training_tracker;

create table muscle_groups (
    id bigint not null auto_increment,
    name varchar(255) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    primary key (id)
);
    