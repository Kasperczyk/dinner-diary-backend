create table account (
  account_id uuid primary key,
  username varchar(50) not null,
  email_address varchar(50) unique not null,
  password char(60) not null,
  last_login_timestamp timestamp,
  creation_timestamp timestamp,
  update_timestamp timestamp
);

 create table contact (
   contact_id uuid primary key,
   account_id uuid not null,
   email_address varchar(50) not null,
   creation_timestamp timestamp,
   update_timestamp timestamp,
   foreign key (account_id) references account (account_id)
 );

 create table menu (
   menu_id uuid primary key,
   account_id uuid not null,
   cooked_on date,
   creation_timestamp timestamp,
   update_timestamp timestamp,
   foreign key (account_id) references account(account_id)
 );

 create table dish (
   dish_id uuid primary key,
   creation_timestamp timestamp,
   update_timestamp timestamp
 );

 create table course (
   course_id uuid primary key,
   menu_id uuid not null,
   dish_id uuid not null,
   course_number smallint not null check(course_number >= 0),
   creation_timestamp timestamp,
   update_timestamp timestamp,
   unique(menu_id, course_number),
   foreign key (menu_id) references menu(menu_id),
   foreign key (dish_id) references dish(dish_id)
 );

 create table rating (
   rating_id uuid primary key,
   rating smallint not null check(rating >= 0 and rating <= 5),
   creation_timestamp timestamp,
   update_timestamp timestamp
 );

 create table cookbook (
   cookbook_id uuid primary key,
   title varchar(50) not null,
   number_of_pages smallint not null check(number_of_pages >= 0 and number_of_pages <= 1000),
   cover_image_url varchar(255),
   creation_timestamp timestamp,
   update_timestamp timestamp
 );

 create table recipe (
   recipe_id uuid primary key,
   cookbook_id uuid not null,
   page smallint not null check(page >= 0 and page <= 1000),
   ingredient_list varchar(1000) not null,
   instruction_text varchar(1000) not null,
   creation_timestamp timestamp,
   update_timestamp timestamp,
   foreign key (cookbook_id) references cookbook(cookbook_id)
 );

 create table image (
   image_id uuid primary key,
   image_url varchar(255) not null,
   creation_timestamp timestamp,
   update_timestamp timestamp
 );