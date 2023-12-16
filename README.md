# restful-with-java-jersey
У цьому репозиторії розміщено туторіал для створення базового RESTful-сервісу
мовою Java за допомогою фреймворку Eclipse Jersey.

Для створення нашого сервісу необхідно завантажити:
- Eclipse IDE
- PostgreSQL
- Apache Tomcat 10.0 для запуску сервера
- Postman для тестування

Після встановлення наведених вище програм можна повторити дії, наведені
нижче у вказівках. Крім того, у папці "src" цього репозиторію можна знайти вихідний
текст готової програми, а також вказівки, як її запустити.

# 0. Основні поняття
Eclipse Jersey (у минулому - Glassfish Jersey) - це open-source фреймворк
для розробки RESTful веб-сервісів мовою Java. Цей фреймворк підтримує
JAX-RS API та є еталонною реалізацією AX-RS (JSR 311 & JSR 339 & JSR 370).

Jersey Servlet Container - це контейнер сервлетів, який використовується для обробки запитів у веб-додатках,
побудованих з використанням Jersey, який є реалізацією специфікації JAX-RS (Java API for RESTful Web Services).

Коли ви використовуєте Jersey для розробки RESTful сервісів, ви можете використовувати Jersey Servlet Container
для розгортання вашого додатка і обробки HTTP-запитів, пов'язаних з вашими ресурсами JAX-RS. Jersey інтегрується
з контейнером сервлетів, таким як Apache Tomcat чи іншим, і надає механізми для обробки REST-запитів через сервлети
в вашому веб-додатку.

Коли користувач робить HTTP-запит до ресурсу, який ви визначили у своєму Jersey-додатку, Jersey Servlet Container
відповідає на цей запит, і ресурс JAX-RS викликається для обробки запиту та генерації відповіді.



# 1. Створення проєкту
Заходимо в Eclipse та виконуємо File - New - Dynamic Web Project:
![Створення проєкту](/img/img.png)

Вводимо Project name, натискаємо Next -> Next ->

Ставимо галочку біля "Generate web.xml for deployment descriptor" і натискаємо Finish.

Після цих дій бачимо новостворений проєкт.

Далі нам необхідно додати бібліотеки Jersey. Для цього конвертуємо проєкт в Maven Project.
Клікаємо ПКМ по проєкту, натискаємо Configure - Convert to Maven Project.
![Конвертація в Maven Project](img/img5.png)

У вікні створення pom.xml вказуємо GroupId та ArtifactId, можна залишити як є.


Після цього відкриється наш pom.xml. Тепер залишилося додати необхідні dependencies в тегу project:

```xml
<dependencies>
    <dependency>
        <groupId>org.glassfish.jersey.containers</groupId>
        <artifactId>jersey-container-servlet</artifactId>
        <version>3.1.0-M2</version>
    </dependency>
    <dependency>
        <groupId>org.glassfish.jersey.inject</groupId>
        <artifactId>jersey-hk2</artifactId>
        <version>3.1.0-M2</version>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.6.0</version>
    </dependency>
    <dependency>
        <groupId>jakarta.ws.rs</groupId>
        <artifactId>jakarta.ws.rs-api</artifactId>
        <version>3.1.0</version>
    </dependency>
    <dependency>
        <groupId>org.glassfish.jersey.media</groupId>
        <artifactId>jersey-media-json-jackson</artifactId>
        <version>3.1.0-M2</version>
    </dependency>
    <dependency>
        <groupId>jakarta.ws.rs</groupId>
        <artifactId>all</artifactId>
        <version>3.1.0</version>
        <type>pom</type>
    </dependency>
</dependencies>
```
Після збереження pom.xml маємо бачити всі додані бібліотеки:

![Додані бібліотеки.xml](img/img8.png)

# 2. Створення простого RESTful-сервісу
 У рамках цієї доповіді створимо RESTful-сервіс з управління однією таблицею User. Використовувати будемо PostgreSQL.

Заходимо в pgAdmin, і створюємо відповідну таблицю.

Алгоритм дій в pg: 
- створити сервер, якщо його ще немає
- Databases -> Create -> Database

![img9.png](img/img9.png)
- вводимо назву БД, наприклад, Users, натискаємо Save
- У новостворенії БД обираємо Schemas -> Tables, ПКМ по Tables -> Create -> Table
![img10.png](img/img10.png)
- вводимо назву (у прикладі users), у вкладці Columns додаємо три колонки: user_id (int), user_name, role та user_email (varchar(50))
![img11.png](img/img11.png)
На цьому початкове налаштування таблиці закінчили. 
Створимо представлення Entity User з таблиці. Створюємо клас Users:
![img12.png](img%2Fimg12.png)
```java 
package userRestful;


public class User {
    private Integer user_id;
    private String user_name;
    private String user_email;
    private String role;
    private Integer getUser_id() {
        return user_id;
    }
    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
    public String getUser_name() {
        return user_name;
    }
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
    public String getUser_email() {
        return user_email;
    }
    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
} 
```
У цьому коді створюємо поля з такими самими назвами, як у таблиці, та створюємо для них гетери та сетери.

У тій самій папці створимо новий клас UserRepository. Цей клас використаємо, щоб підключитися до таблиці.
```java
package userRestful;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    Connection con = null;
    public UserRepository() {
        String user_name = "postgres";
        String password = "1234";
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://localhost/users", user_name, password);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        String sql = "select * from users;";
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                User u = new User();
                u.setUser_id(rs.getInt(1));
                u.setUser_name(rs.getString(2));
                u.setUser_email(rs.getString(3));
                u.setRole(rs.getString(4));
                users.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }
}

```
У цьому коді: 
- вводимо ім'я адміністратора та пароль від постгрес
- підключаємо драйвер для постгресу, та у посиланні вказуємо нашу таблицю
- вже створений код для першої CRUD-операції "get all users"

Якщо виникають проблеми з підключенням бібліотек, ПКМ по проєкту -> Maven -> Update Project.

Після цього в тій самій папці створимо останній клас - UserResource.

```java
package userRestful;

import java.util.List;

import com.google.gson.Gson;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("users")
public class UserResource {
    UserRepository repo = new UserRepository();
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUsers() {
        return new Gson().toJson(repo.getUsers());
    }
}

```
Цей клас використовуємо для створення URI-шляхів для нашого сервісу.
# 3. Конфігурація Jersey Servlet Container

Знаходимо в src/webapp/WEB-INF файл web.xml.
Цей web.xml файл конфігурує ваш веб-додаток для використання Jersey у якості фреймворку для створення 
RESTful веб-сервісів.

У файл записуємо: 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_5_0.xsd"
         version="5.0">

    <display-name>userRestful</display-name>

    <servlet>
        <servlet-name>jersey-serlvet</servlet-name>
        <servlet-class>org.glassfish.jersey.servlet.ServletContainer</servlet-class>
        <init-param>
            <param-name>jersey.config.server.provider.packages</param-name>
            <param-value>userRestful</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>jersey-serlvet</servlet-name>
        <url-pattern>/rest/*</url-pattern>
    </servlet-mapping>

</web-app>
```
Розглянемо кожен елемент цього файлу:

- web-app: Основний елемент, що описує початок конфігураційного файлу веб-додатка.

- display-name: Вказує ім'я вашого веб-додатка, яке може використовуватися в адміністративних інтерфейсах сервера.

- servlet: Описує сервлет, який обробляє запити до вашого веб-додатка.

- servlet-name: Ім'я сервлета.

- servlet-class: Повний шлях до класу, який відповідає за обробку запитів. Використовується org.glassfish.jersey.servlet.ServletContainer, 
- який є частиною Jersey і відповідає за обробку запитів JAX-RS.

- init-param: Параметри ініціалізації для сервлета. 

- servlet-mapping: Встановлює відповідність між сервлетом і URL-шляхом, за яким буде доступний сервлет.

- servlet-name: Ім'я сервлета, що мапиться. В цьому випадку, "Java RESTful service".

- url-pattern: URL-шлях, за яким буде доступний сервлет. Всі запити, що співпадають з цим шаблоном, будуть направлені до 
- вказаного сервлета. У цьому випадку, "/rest/*" означає, що сервлет буде доступний за шляхом "/rest/..." (де "..." - може бути будь-яким шляхом).

# 4. Тестування першої функції
Для тестування поки використаємо лише браузер (тестуємо GET-запит). 
- ПКМ по проєкту -> Run as -> Run on Server
- обираємо існуючий сервер або створюємо новий (Apache Tomcat 10.0) -> Next -> Finish
- маємо бачити повідомлення про те, що сервер запустився
![img13.png](img/img13.png)
- переходимо за адресою http://localhost:8080/userRestful/rest/users (залежить від конфігурації вашого проєкту): userRestful -
з display-name web.xml, /rest - з url-pattern, users - з UserResource.
- маємо отримати пустий масив, бо ми ще не додавали юзерів.
![img14.png](img/img14.png)
- додамо юзера з pgAdmin і подивимося на зміни
![img15.png](img/img15.png)
- 
![img16.png](img/img16.png)
# 5. Розширення функціональності сервісу.
## Отримання User за user_id
Додамо новий метод в класі UserResource: 
```java
class UserResource {
    public User getUser(Integer id) {
        User user = new User();
        String sql = "select * from users where user_id=" + id;
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                user.setUser_id(rs.getInt(1));
                user.setUser_name(rs.getString(2));
                user.setUser_email(rs.getString(3));
                user.setRole(rs.getString(4));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
}
```
Принцип дії той самий, що і в попереднього методу отримання всіх юзерів
з цього класу - виконується SQL-запит і зчитується інстанс.

Відповідні зміни до UserResource:
```java
class UserResource {
    @GET
    @Path("{user_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(@PathParam("user_id") Integer user_id) {
        return repo.getUser(user_id);
    }
}
```
Ми вказали, що в адресі запиту після "/" треба вказати ідентифікатор шуканого
користувача. Новий функціонал готовий до тестування:
![img17.png](img/img17.png)


## Додавання нового User-а
Новий метод в класі UserRepository:
```java
class UserRepository {
    public void create(User u) {
        String sql = "insert into users values(?,?,?,?)";
		try {
			PreparedStatement pStatement = con.prepareStatement(sql);
			pStatement.setInt(1, u.getUser_id());
			pStatement.setString(2, u.getUser_name());
			pStatement.setString(3, u.getUser_email());
			pStatement.setString(4, u.getRole());
			pStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
```
Тут з json-об'єкту будуть зчитуватися values, які вставляються в pStatement, після чого
pStatement надсилається на виконання.

Також новий метод в класі UserResource:
```java
class UserResource {
    @POST
    @Produces(MediaType.APPLICATION_JSON) 
    @Consumes(MediaType.APPLICATION_JSON) 
    public User createUser(User user) {
        repo.create(user);
        return user;
	}
}
```
Отже, зрозуміло, що тепер додавання нових видів CRUD-операцій - це додавання відповідних
методів в UserRepository та UserResource.

Тестуємо наш POST-метод. Для цього використаємо Postman.
![img18.png](img/img18.png)

![img19.png](img/img19.png)

## Оновлення User-а
Новий метод UserRepository:
```java
class UserRepository {
    public void update(User u) {
        String sql = "update users set user_name=?,email_id=?,role=? where user_id=?";
        try {
            PreparedStatement pStatement = con.prepareStatement(sql);
            pStatement.setString(1, u.getUser_name());
            pStatement.setString(2, u.getUser_email());
            pStatement.setString(3, u.getRole());
            pStatement.setInt(4, u.getUser_id());
            pStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```
Новий метод UserResource:
```java
class UserResource {
    @PUT
    @Path("{user_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User update(@PathParam("user_id") Integer user_id, User user) {
        repo.update(user);
        return user;
    }
}
```
Тут знову зчитуємо /id з URI(@Path + @PathParam).

Тестуємо:
![img20.png](img/img20.png)

![img21.png](img/img21.png)
## Видалення User-a
Оновлення UserRepository:
```java
class UserRepository {
    public void delete(Integer id) {
        String sql = "delete from users where user_id=?";
        try {
            PreparedStatement pStatement = con.prepareStatement(sql);
            pStatement.setInt(1, id);
            pStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```
Оновлення UserResource:
```java \
class UserResource {
    @DELETE
    @Path("{user_id}")
    public void deleteUser(@PathParam("user_id") Integer user_id) {
        repo.delete(user_id);
    }
}
```
Тестування:
![img22.png](img/img22.png)

![img23.png](img/img23.png)

# 6. Висновки
Отже, у ході створення цього проєкту ми ознайомилися з можливостями створення
RESTful-сервісів мовою Java за допомогою Jersey Framework. Хоча найпопулярнішим 
(і, напевно, трохи легшим в застосуванні) фреймворком для створення таких сервісів
вважається Spring, Eclipse Jersey теж є потужним і поширеним інструментом.

# 7. Корисні посилання
1. https://youtu.be/MBYJ9gFtqt4?si=upf0hstOPgoI1loB 