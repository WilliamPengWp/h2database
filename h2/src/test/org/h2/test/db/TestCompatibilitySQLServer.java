package org.h2.test.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.test.TestBase;

/**
 * SQL Server compatibility test
 */
public class TestCompatibilitySQLServer extends TestBase {

	/**
	 * Run just this test.
	 *
	 * @param a ignored
	 */
	public static void main(String[] a) throws Exception {
		TestBase.createCaller().init().test();
	}

	@Override
	public void test() throws SQLException {
		testTableHints();
		testUsingIdentityAsAutoIncrementAlias();
	}

	private void testTableHints() throws SQLException {
		deleteDb("sqlserver");

		Connection conn = getConnection("sqlserver;MODE=MSSQLServer");
		Statement stat = conn.createStatement();

		stat.execute("create table parent(id int primary key)");
		stat.execute("create table child(id int primary key, parent_id int, foreign key (parent_id) references public.parent(id) )");

		assertTrue(isSupportedSyntax(stat, "select * from parent with(nolock)"));
		assertTrue(isSupportedSyntax(stat, "select * from parent with(nolock, index = id)"));
		assertTrue(isSupportedSyntax(stat, "select * from parent with(nolock, index(id, name))"));
		assertTrue(isSupportedSyntax(stat, "select * from parent p with(nolock) join child ch with(nolock) on ch.parent_id = p.id"));
	}

	private void testUsingIdentityAsAutoIncrementAlias() throws SQLException {
		Connection conn = getConnection("sqlserver;MODE=MSSQLServer");
		Statement stat = conn.createStatement();

		stat.execute("create table test(id int primary key identity, expected_id int)");
		stat.execute("insert into test (expected_id) VALUES (1), (2), (3)");

		ResultSet results = stat.executeQuery("select * from test");
		while (results.next()) {
			assertEquals(results.getInt("expected_id"), results.getInt("id"));
		}
	}

	private static boolean isSupportedSyntax(Statement stat, String sql) {
		try {
			stat.execute(sql);
			return true;
		} catch (SQLException ex) {
			return false;
		}
	}

}
