package warehouse.viewingclients;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewingClients {
    public static void viewSeller(Connection connection, boolean isClient) throws Exception {
        final String sqlSelect = """
                select cl.id as number, tС.name as type,cl.name as name from clients cl --клиенты
                inner join type_client tС on tС.id = cl.type_client_ref and tС.external=?
                order by cl.id;
                """;
        PreparedStatement statement;
        ResultSet resultSet = null;
        statement = connection.prepareStatement(sqlSelect);
        statement.setBoolean(1, isClient);
        resultSet = statement.executeQuery();
        String var1 = "поставщиков";
        String var2 = "организации=";
        if (isClient == false) {
            var1 = "подразделений";
            var2 = "подразделения=";
        }
        System.out.println("");
        System.out.println("Вывод существующих " + var1);
        while (resultSet.next()) {
            int number = resultSet.getInt("number");
            String type = resultSet.getString("type");
            String name = resultSet.getString("name");
            System.out.println("id=" + number + ",Вид " + var2 + type +
                    ",Наименование='" + name + '\'');
        }
        System.out.println("Конец вывода существующих "+var1);
        System.out.println("");
    }
}
