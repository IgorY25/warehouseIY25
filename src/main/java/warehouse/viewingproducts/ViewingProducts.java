package warehouse.viewingproducts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewingProducts {
    public static void viewProd(Connection connection) throws Exception {
        final String sqlSelect = """
                select p.id as number, p.name as name, u.name as unit
                from product p
                         inner join measure_unit u on p.unit_ref = u.id
                order by p.name, u.name;
                """;
        PreparedStatement statement;
        ResultSet resultSet = null;
        statement = connection.prepareStatement(sqlSelect);
        resultSet = statement.executeQuery();
        System.out.println("");
        System.out.println("Вывод существующих наименований продукции");
        while (resultSet.next()) {
            int number = resultSet.getInt("number");
            String name = resultSet.getString("name");
            String unit = resultSet.getString("unit");
            System.out.println("id=" + number + ",Товар='" + name + '\'' +
                    ",Ед.измерения='" + unit + '\'');
        }
        System.out.println("Конец вывода наименований продукции");
        System.out.println("");
    }
}
