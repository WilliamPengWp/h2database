package org.h2.samples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * <a href="https://msdn.microsoft.com/en-us/library/ms187373.aspx">Table hints</a> support for SQL Server
 */
public class TableHints {

	public static void main(String... args) throws Exception {
		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection("jdbc:h2:mem:;MODE=MSSQLServer");
		Statement stat = conn.createStatement();

		stat.execute("create table parent(id int primary key, name varchar(255))");
		stat.execute("create table child(id int primary key, name varchar(255), parent_id int, foreign key (parent_id) references public.parent(id) )");

		stat.execute("insert into parent values(1, 'Thomas')");
		stat.execute("insert into child values(1, 'John', 1)");

		ResultSet rs;
		rs = stat.executeQuery("select * from parent with(nolock)");
		while (rs.next()) {
			System.out.println(rs.getString("name"));
		}

		rs = stat.executeQuery("select * from parent with(nolock, index = id)");
		while (rs.next()) {
			System.out.println(rs.getString("name"));
		}

		rs = stat.executeQuery("select * from parent with(nolock, index(id, name))");
		while (rs.next()) {
			System.out.println(rs.getString("name"));
		}

		rs = stat.executeQuery("select * from parent p with(nolock) join child ch with(nolock) on ch.parent_id = p.id");
		while (rs.next()) {
			System.out.println(rs.getString("parent.name") + " -> " + rs.getString("child.name"));
		}

		stat.close();
		conn.close();
	}

}
