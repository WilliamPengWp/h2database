package org.h2.test.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.test.TestBase;

/**
 * Test table hints feature
 */
public class TestTableHints extends TestBase {

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
		deleteDb("tableHints");

		Connection conn = getConnection("tableHints;MODE=MSSQLServer");
		Statement stat = conn.createStatement();

		stat.execute("create table parent(id int primary key)");
		stat.execute("create table child(id int primary key, parent_id int, foreign key (parent_id) references public.parent(id) )");

		assertTrue(isSupportedSyntax(stat, "select * from parent with(nolock)"));
		assertTrue(isSupportedSyntax(stat, "select * from parent with(nolock, index = id)"));
		assertTrue(isSupportedSyntax(stat, "select * from parent with(nolock, index(id, name))"));
		assertTrue(isSupportedSyntax(stat, "select * from parent p with(nolock) join child ch with(nolock) on ch.parent_id = p.id"));
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
