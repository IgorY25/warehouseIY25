package warehouse.viewingunitsmeasur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewingUnitsMeasur {
    public static void viewUnits(Connection connection) throws Exception {
        final String sqlSelect = """
                select mu.id as number, mu.name as name from measure_unit mu;
                """;
        PreparedStatement statement;
        ResultSet resultSet = null;
        statement = connection.prepareStatement(sqlSelect);
        resultSet = statement.executeQuery();
        System.out.println("");
        System.out.println("Вывод единиц измерения");
        while (resultSet.next()) {
            int number = resultSet.getInt("number");
            String name = resultSet.getString("name");
            System.out.println("id=" + number + ",Наименование='" + name + '\'');
        }
        System.out.println("Конец вывода единиц измерения");
        System.out.println("");
    }
}
