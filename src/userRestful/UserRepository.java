package userRestful;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
	public void update(User u) {
		String sql="update users set user_name=?,email_id=?,role=? where user_id=?";
		try {
			PreparedStatement pStatement = con.prepareStatement(sql);
			pStatement.setString(1, u.getUser_name());
			pStatement.setString(2, u.getUser_email());
			pStatement.setString(3, u.getRole());
			pStatement.setInt(4,  u.getUser_id());
			pStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void delete(Integer id) {
		String sql = "delete from users where user_id=?";
		try {
			PreparedStatement pStatement = con.prepareStatement(sql);
			pStatement.setInt(1,  id);
			pStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

