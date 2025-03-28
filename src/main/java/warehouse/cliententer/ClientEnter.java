package warehouse.cliententer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ClientEnter {
    public static int clientQuery(Connection connection, boolean isClient, int notWarehouse) throws Exception {
        final String sqlSelect = """
                select cl.id as number, tС.name as type,cl.name as name from clients cl --клиенты
                inner join type_client tС on tС.id = cl.type_client_ref and (tС.external = ?)
                where (cl.id = ? or 0=?) and cl.id!= 6  and (cl.id!=1 or 0=?);
                """;
        PreparedStatement statement;
        Scanner scanner = new Scanner(System.in);
        int result = 0;
        boolean boolRes = false;
        String resultType;
        String resultStr = new String();
        ResultSet resultSet = null;
        System.out.println("");
        if (isClient) {
            System.out.println("Ввод поставщика");
        } else {
            if (notWarehouse == 1) {
                System.out.println("Ввод магазина");
            } else {
                System.out.println("Ввод подразделения");
            }

        }
        statement = connection.prepareStatement(sqlSelect);
        statement.setBoolean(1, isClient);
        statement.setInt(2, 0);
        statement.setInt(3, 0);
        statement.setInt(4, notWarehouse);
        resultSet = statement.executeQuery();
        while (resultSet.next()) {
            int number = resultSet.getInt("number");
            String type = resultSet.getString("type");
            String name = resultSet.getString("name");
            System.out.println("id=" + number + ",Вид: '" + type + '\'' +
                    ",Наименование:'" + name + '\'');
        }
        System.out.println("");
        if (isClient) {
            System.out.println("Введите id поставщика");
        } else {

            if (notWarehouse == 1) {
                System.out.println("Введите id магазина");
            } else {
                System.out.println("Введите id подразделения");
            }
        }

        if (scanner.hasNext()) {
            int intRes = scanner.nextInt();
            System.out.println("Вы ввели id= %d".formatted(intRes));
            if (intRes == 0) {
                System.out.println("Ошибка ввода id");
                return 0;
            }
            statement.setInt(2, intRes);
            statement.setInt(3, intRes);
            try {
                resultSet = statement.executeQuery();
                boolRes = resultSet.next();
                if (boolRes) {
                    result = resultSet.getInt("number");
                    resultType = resultSet.getString("type");
                    resultStr = resultSet.getString("name");
                    System.out.println("id=" + result + " Вид: " + resultType + " Наименование: " + resultStr);
                    return intRes;
                } else {
                    System.out.println("Ошибка ввода id");
                    return 0;
                }
            } catch (SQLException e) {
                System.out.println("Ошибка ввода: " + e.toString());
                return 0;
            }
        }
        return 0;
    }
}
