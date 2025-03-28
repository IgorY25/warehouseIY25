package warehouse.util;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OperDay {
    public static Date opdate;

    public static boolean setOpdate(Connection connection) throws Exception {
        final String sqlSelect = """
                select date from oper_day where id=1;
                """;
        PreparedStatement statement;
        statement = connection.prepareStatement(sqlSelect);
        System.out.println("Определим опердемь");
        ResultSet resultSet = statement.executeQuery();
        boolean boolRes = resultSet.next();
        if (boolRes) {
            opdate = resultSet.getDate("date");
            System.out.println("Документы будут вводиться оперднем: " + opdate);
            return true;
        } else {
            System.out.println("!!! Ошибка определения опердня");
            return false;
        }
    }
}
