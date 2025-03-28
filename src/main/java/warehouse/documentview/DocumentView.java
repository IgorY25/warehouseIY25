package warehouse.documentview;

import warehouse.cliententer.ClientEnter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DocumentView {
    public static void dovView(Connection connection, boolean isCredit, boolean isClient) throws Exception {
        String sqlSelect = """
                select d.id,d.date,d.numDoc,t.name vidd, c.name named,t2.name vidk, c2.name namek,p.name prod,d.quantity,m.name unit,d.price
                from documents d
                inner join clients c on c.id = d.client_debet
                inner join type_client t on t.id=c.type_client_ref
                inner join clients c2 on c2.id = d.client_kredit
                inner join type_client t2 on t2.id=c2.type_client_ref
                inner join product p on p.id = d.product_ref
                inner join measure_unit m on m.id=p.unit_ref
                inner join oper_day od on d.date = od.date and od.id=1
                """;
        if (isCredit) {
            sqlSelect = sqlSelect + """
                    where d.client_kredit=?;
                    """;
        } else {
            sqlSelect = sqlSelect + """
                    where d.client_debet=?;
                    """;
        }

        int idClient = ClientEnter.clientQuery(connection, isClient, 1);
        PreparedStatement statement;
        ResultSet resultSet = null;
        statement = connection.prepareStatement(sqlSelect);
        statement.setInt(1, idClient);
        resultSet = statement.executeQuery();
        System.out.println("");
        if (isCredit & isClient) {
            System.out.println("Просмотр документа на приход товара на склад от поставщика за текущее число");
        } else if (!isCredit & !isClient) {
            System.out.println("Просмотр документа на расход товара со склада в магазин за текущее число");
        } else if (isCredit & !isClient) {
            System.out.println("Просмотр документа с проданным товаром в магазине за текущее число");
        }

        while (resultSet.next()) {
            System.out.println("id=" + resultSet.getInt("id") +
                    ".Дата " + resultSet.getDate("date") +
                    ".N " + resultSet.getString("numDoc") +
                    ",Вид:" + resultSet.getString("vidd") +
                    ",Наим.:" + resultSet.getString("named") +
                    ",Вид:" + resultSet.getString("vidk") +
                    ",Наим.:" + resultSet.getString("namek") +
                    ",Товар:" + resultSet.getString("prod") +
                    ",кол-во=" + resultSet.getBigDecimal("quantity") +
                    " " + resultSet.getString("unit") +
                    ",цена=" + resultSet.getBigDecimal("price") +
                    " руб");
        }
    }
}
